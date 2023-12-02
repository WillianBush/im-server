package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.*;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.dao.BaseDAO;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.entity.FriendsAdd.Status;
import com.imservices.im.bmm.service.*;
import com.imservices.im.bmm.utils.PinyinUtils;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.imservices.im.bmm.websocket.cmd.AccessRecordCmd;
import com.imservices.im.bmm.websocket.cmd.FriendsCmd;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller("FriendsController")
@RequestMapping(value = "/user/friend")
@CrossOrigin
public class FriendsController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private WebConfigService configService;
	@Autowired
	private AccessRecordService accessRecordService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private StoreComponent storeComponent;
	@Autowired
	private UserChatCmd userChatCmd;
	@Autowired
	private SendUtils sendUtils;
	@Autowired
	private AccessRecordCmd accessRecordCmd;
	@Autowired
	private FriendsCmd friendsCmd;
	@Autowired
	private FunctionConfigService functionConfigService;
	@Autowired
	private EmployeeService employeeService;
	
	@AuthPassport    
	@RequestMapping(value = "/removeFriends",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void removeFriends(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String uid = request.getParameter("uid");
		if(StringUtils.isEmpty(uid)) return;
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
//		List<Friends> list = memberService.getFriendsList(new String[]{"mid","friendid"}, new Object[]{MEMBERID,uid});
		
		memberService.deleteByHql("delete from Friends where mid='"+MEMBERID+"' and friendid='"+uid+"'");
		accessRecordService.deleteByHql("delete from AccessRecord where uid='"+MEMBERID+"' and entityid='"+uid+"'");
		chatService.deleteWSMbyHql("delete from WaitSendMessage where toUid='"+MEMBERID+"' and fromUid='"+uid+"'");
		
		ResponseUtils.json(response,200,"",null); 
	}
	
	
	
	@AuthPassport    
	@RequestMapping(value = "/isMyFri/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void isMyFri_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String uid = request.getParameter("uid");
		if(StringUtils.isEmpty(uid)) return;
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		Long l = memberService.friendsCount(new String[]{"mid","friendid"}, new Object[]{MEMBERID,uid});
		if(null==l||l<=0) {
			ResponseUtils.json(response,200,"0",null); 
		} else {
			ResponseUtils.json(response,200,"1",null); 
		}
	}
	
	
	@AuthPassport    
	@RequestMapping(value = "/verify/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void verify_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String t = request.getParameter("t");
		
		if(StringUtils.isEmpty(id)||StringUtils.isEmpty(t)) return;
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		FriendsAdd fa = memberService.getFriendsAdd(id);
		if(fa.getStatus()!=FriendsAdd.Status.wait) {
			ResponseUtils.json(response,500,"此信息已处理过",null);  
			return;
		} 
		
		if(!fa.getFriendid().equals(MEMBERID)) return;
		Member member = memberService.get(MEMBERID);
		if("2".equals(t)) {
			fa.setStatus(FriendsAdd.Status.faile);
		} else {
			fa.setStatus(FriendsAdd.Status.success);
		}
		
		memberService.updateFriendsAdd(fa);
		
		if(fa.getStatus()==FriendsAdd.Status.faile) {
			ResponseUtils.json(response,200,"",null);  
			return;
		}
		//我验证的。如果对方没有在我的好友列表中则添加【这个是针对对方先删我好友，再添加的情况 】
		Long l = memberService.friendsCount(new String[] {"mid","friendid"}, new Object[] {MEMBERID,fa.getMid()});
		if(null==l||l<=0) {
			Friends firends1 = new Friends();
			firends1.setCreateDate(new Date());
			firends1.setFriendid(fa.getMid());
			firends1.setMid(MEMBERID);
			memberService.saveFriends(firends1);
		}
		
		List<AccessRecord> arlist = accessRecordService.getList(new String[]{"uid","typeid","entityid"}, new Object[]{MEMBERID,"2",fa.getMid()});
		AccessRecord ar = new AccessRecord();
		if(null==arlist||arlist.isEmpty()) {
			ar.setCreateDate(new Date());
			ar.setUid(MEMBERID);
			ar.setEntityid(fa.getMid());
			ar.setTypeid("2");
			Member e = memberService.get(fa.getMid());
			if(StringUtils.isEmpty(e.getHeadpic())) { 
				ar.setImg("/img_sys/defaultHeadPic.jpg");
			} else {
				ar.setImg(e.getHeadpic());
			}
			ar.setTitle(e.getNickName());
			accessRecordService.save(ar);
		} else {
			ar = arlist.get(0);
			ar.setCreateDate(new Date());
			ar.setModifyDate(new Date());
			accessRecordService.update(ar);
		}
		
		AccessRecordBean arbean = BeanUtils.accessRecordToBean(ar);
		accessRecordCmd.insertOrUpdate(arbean, MEMBERID, CMD_ENUM.AR_INSERT);
		
		
		
		Friends firends2 = new Friends();
		firends2.setCreateDate(new Date());
		firends2.setFriendid(MEMBERID);
		firends2.setMid(fa.getMid());
		memberService.saveFriends(firends2);
		
		arlist = accessRecordService.getList(new String[]{"uid","typeid","entityid"}, new Object[]{fa.getMid(),"2",MEMBERID});
		ar = new AccessRecord();
		if(null==arlist||arlist.isEmpty()) {
			ar.setCreateDate(new Date());
			ar.setUid(fa.getMid());
			ar.setEntityid(MEMBERID);
			ar.setTypeid("2");
			if(StringUtils.isEmpty(member.getHeadpic())) { 
				ar.setImg("/img_sys/defaultHeadPic.jpg");
			} else {
				ar.setImg(member.getHeadpic());
			}
			ar.setTitle(member.getNickName());
			accessRecordService.save(ar);
		} else {
			ar = arlist.get(0);
			ar.setCreateDate(new Date());
			ar.setModifyDate(new Date());
			accessRecordService.update(ar);
		}
		arbean = BeanUtils.accessRecordToBean(ar);
		accessRecordCmd.insertOrUpdate(arbean, fa.getMid(), CMD_ENUM.AR_INSERT);
		   
		 
		
		List<FriendsBean> blist = new ArrayList<FriendsBean>();
		FriendsBean b = new FriendsBean();
		Member friend = memberService.get(fa.getMid());
		
		if(StringUtils.isEmpty(friend.getHeadpic())) {
			b.setHeadpic("/img_sys/defaultHeadPic.jpg");
		} else {
			b.setHeadpic(friend.getHeadpic());
		}
		b.setMember_uuid(friend.getId());
		b.setMemberid(friend.getMemberId());
		b.setName(friend.getNickName());
		blist.add(b); 
		 
//		FriendsBean b1 = new FriendsBean();
//		if(StringUtils.isEmpty(member.getHeadpic())) {
//			b1.setHeadpic("/img_sys/defaultHeadPic.jpg");
//		} else {
//			b1.setHeadpic(member.getHeadpic());
//		}
//		b1.setMember_uuid(member.getId());
//		b1.setMemberid(member.getMemberId());
//		b1.setName(member.getNickName());
//		FriendsUtils.sendFriends(b1, friend.getId());
		
//		if(SessionStore.isMemberOnline(b.getMember_uuid())) {
		if(storeComponent.isMemberOnline(b.getMember_uuid())) {
			Message msg = new Message();
			msg.setBody(""); 
			msg.setCMD(Message.CMD_ENUM.UPDATE_ADDRESS_BOOK.name());
			
			List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(b.getMember_uuid());
			for(WebSocketSession ws : ws_list) {
				sendUtils.send(ws,msg );
			}
			
			
		}
		
		
		
		
		
		ChatTxtBean bean = new ChatTxtBean();
		bean.setFromUid("-1");
		bean.setToUid(fa.getMid());
		bean.setTxt("恭喜！您成功通过新朋友【"+member.getNickName()+"】的验证。");
		userChatCmd.sendTXT(bean);
		
		
		
		ResponseUtils.json(response,200,blist,null);  
	}
	
	
	@AuthPassport    
	@RequestMapping(value = "/sendVerify/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void sendVerify_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String txt = request.getParameter("txt");
		String mid = request.getParameter("mid");
		Employee employee = employeeService.get("member_uuid",MEMBERID);
		FunctionConfigEntity fcEntity=functionConfigService.get();
		/**如果不是特权用户，且配置：禁止普通用户添加好友0否1是*/
		if (employee == null&&fcEntity.getAdd_friend()==1) {
			ResponseUtils.json(response, 500,"禁止添加好友" , null);
			return;
		}
		if(StringUtils.isEmpty(txt)||StringUtils.isEmpty(mid)) return;

		if(mid.equals(MEMBERID)) {
			ResponseUtils.json(response,500,"不能添加自己",null);  
			return;
		}
		Long l = memberService.friendsAddCount(new String[]{"mid","friendid","status"},new Object[]{MEMBERID,mid,FriendsAdd.Status.wait});
		if(null!=l&&l>0) {
			ResponseUtils.json(response,500,"请匆重复提交,等待对方验证",null);  
			return;
		}
		l = memberService.friendsCount(new String[]{"mid","friendid"},new Object[]{MEMBERID,mid});
		if(null!=l&&l>0) {
			ResponseUtils.json(response,500,"已是你的好友",null);  
			return;
		}  
		
		Member from_member = memberService.get(MEMBERID);
		Member to_member = memberService.get(mid);
		FriendsAdd friend_add = new FriendsAdd();
		friend_add.setContent(txt);
		friend_add.setCreateDate(new Date());
		friend_add.setFriendid(to_member.getId());
		friend_add.setMid(from_member.getId());
		friend_add.setStatus(FriendsAdd.Status.wait);
		memberService.saveFriendAdd(friend_add);
		List<FriendsAddBean> blist = new ArrayList<FriendsAddBean>();
		FriendsAddBean b = new FriendsAddBean();
		if(StringUtils.isEmpty(to_member.getHeadpic())) {
			b.setTo_headpic("/img_sys/defaultHeadPic.jpg");
		} else {
			b.setTo_headpic(to_member.getHeadpic());
		}
		b.setTo_member_uuid(to_member.getId());
		b.setTo_memberid(to_member.getMemberId());
		b.setTo_name(to_member.getNickName());
		if(StringUtils.isEmpty(from_member.getHeadpic())) {
			b.setFrom_headpic("/img_sys/defaultHeadPic.jpg");
		} else {
			b.setFrom_headpic(from_member.getHeadpic());
		}
		b.setFrom_member_uuid(from_member.getId());
		b.setFrom_memberid(from_member.getMemberId());
		b.setFrom_name(from_member.getNickName());
		b.setStatus(friend_add.getStatus().name());
		b.setContent(friend_add.getContent());
		blist.add(b);
		
		friendsCmd.sendFriendsAdd(b, b.getTo_member_uuid());
		
		ResponseUtils.json(response,200,blist,null);  
	}
	
	 
	@AuthPassport    
	@RequestMapping(value = "/searchByTelOrName/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void searchByTelOrName_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		MemberBean mb = storeComponent.getMemberBeanFromMapDB(MEMBERID);
		if(wc.getAddNewFriendAble().intValue()==0) {
			if(wc.getAddNewFriendAble_others().indexOf(mb.getMemberId())<0) {
				ResponseUtils.json(response, 200, new ArrayList<FriendsBean>() , null);
				return;
			} 
		}
//		addNewFriendAble
		
		String txt = request.getParameter("txt");
		if(StringUtils.isEmpty(txt)) return;
//		String[] ps = new String[]{"memberId"+BaseDAO.OR,"telphone"+BaseDAO.OR,"nickName"};
//		Object[] vs = new Object[]{txt,txt,txt};
//		List<FriendsBean> blist = new ArrayList<FriendsBean>();
//		List<Member> list = memberService.getList(ps,vs);
//
		String[] ps = new String[]{"memberId"};
		Object[] vs = new Object[]{txt};
		List<FriendsBean> blist = new ArrayList<FriendsBean>();
		Set<Member> listSearchAll = new HashSet<>();
		List<Member> list = memberService.getList(ps,vs);

//		ps = new String[]{"telphone"};
//		vs = new Object[]{txt};
//		List<Member> listT = memberService.getList(ps,vs);

//		ps = new String[]{"nickName"};
//		vs = new Object[]{txt};
//		List<Member> listN = memberService.getList(ps,vs);

		listSearchAll.addAll(list);
//		listSearchAll.addAll(listT);
//		listSearchAll.addAll(listN);

		if(!listSearchAll.isEmpty()) {
			for(Member friend : listSearchAll) {
				FriendsBean b = new FriendsBean(); 
				if(friend.getId().equals(MEMBERID)) {
					b.setStatus("自己");
				} else {
					Long l = memberService.friendsCount(new String[]{"mid","friendid"},new Object[]{MEMBERID,friend.getId()});
					if(null!=l&&l>0) {
						b.setStatus("已添加");
					} else {
						l = memberService.friendsAddCount(new String[]{"mid","friendid","status"},new Object[]{MEMBERID,friend.getId(),FriendsAdd.Status.wait});
						if(null!=l&&l>0) {
							b.setStatus("等待验证");
						} else {
							b.setStatus(null); 
						}
					}
				}
				
				if(StringUtils.isEmpty(friend.getHeadpic())) {
					b.setHeadpic("/img_sys/defaultHeadPic.jpg");
				} else {
					b.setHeadpic(friend.getHeadpic());
				}
				b.setMember_uuid(friend.getId());
				b.setMemberid(friend.getMemberId());
				b.setName(friend.getNickName());
				blist.add(b);
			}
		}
		ResponseUtils.json(response, 200,blist , null);
	}
	
	
	
	@AuthPassport    
	@RequestMapping(value = "/list/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void list_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		Member member = memberService.get(MEMBERID);
		String[] ps = new String[]{"mid"};
		Object[] vs = new Object[]{MEMBERID};
//		List<FriendsBean> blist = new ArrayList<FriendsBean>();
		List<Friends> list = memberService.getFriendsList(ps,vs);
		List<FriendList> nlist = new ArrayList<FriendList>();
		String blacklist = storeComponent.getBlackList(MEMBERID);
		if(null!=list&&!list.isEmpty()) {
			for(Friends m : list) {
				
				if(!StringUtils.isEmpty(blacklist)&&blacklist.indexOf(m.getFriendid())>=0) continue;
				
				
				FriendsBean b = new FriendsBean();
				if (m.getFriendid() == null) {
					memberService.deleteFriends(m);
					continue;
				}
				Member friend = memberService.get(m.getFriendid());
				if(null==friend) {
					memberService.deleteFriends(m);
					continue;
				}
				if(StringUtils.isEmpty(friend.getHeadpic())) {
					b.setHeadpic( "/img_sys/defaultHeadPic.jpg");
				} else {
					b.setHeadpic(friend.getHeadpic());
				}
				b.setMember_uuid(friend.getId());
				b.setMemberid(friend.getMemberId());
				b.setName(friend.getNickName());
//				blist.add(b);
				boolean flag = true;
				String h = PinyinUtils.getPinYin(b.getName().trim()).split("")[0];
				for(FriendList fl : nlist) {
					if(fl.getH().toUpperCase().equals(h.toUpperCase())) {
						fl.getList().add(b);
						flag = false;
					}
				}
				if(flag) {
					FriendList fl = new FriendList();
					fl.setH(h.toUpperCase());
					fl.getList().add(b);
					nlist.add(fl);
				}
			}
		}
		
		
		Collections.sort(nlist, new Comparator<FriendList>() {
				@Override
				public int compare(FriendList u1, FriendList u2) {
					return u1.getH().hashCode() - u2.getH().hashCode();
				}
		}); 
		 
		
		ResponseUtils.json(response, 200,nlist , null);
	}
	   
	

	
	@AuthPassport    
	@RequestMapping(value = "/add/list/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void add_list_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		Member member = memberService.get(MEMBERID);
		String[] ps = new String[]{"mid"+BaseDAO.OR,"friendid"};
		Object[] vs = new Object[]{MEMBERID,MEMBERID};
		List<FriendsAddBean> blist = new ArrayList<FriendsAddBean>();
		List<FriendsAdd> list = memberService.getFriendsAddList(ps,vs);
		WebConfig wc = configService.get();
		if(null!=list&&!list.isEmpty()) {
			for(FriendsAdd o : list) {
				if(o.getStatus()==Status.wait) {
					Long l = new Date().getTime() - o.getCreateDate().getTime();
					int h = (int)(l/1000/60/60);
//					int h = (int)(l/1000/60);
					if(h>=wc.getNewFriendAuditOverdue()) {
						o.setStatus(Status.pass);
						memberService.updateFriendsAdd(o);
					}
				}else if(o.getStatus()==Status.faile || o.getStatus()==Status.success|| o.getStatus()==Status.pass){
					continue;
				}
				
				
				FriendsAddBean b = new FriendsAddBean();
				b.setId(o.getId());  
				Member friend = memberService.get(o.getFriendid());
				if(null==friend) {
					memberService.deleteFriendsAdd(o);
					continue;
				}
				if(StringUtils.isEmpty(friend.getHeadpic())) {
					b.setTo_headpic("/img_sys/defaultHeadPic.jpg");
				} else {
					b.setTo_headpic(friend.getHeadpic());
				}
				b.setTo_member_uuid(friend.getId());
				b.setTo_memberid(friend.getMemberId());
				b.setTo_name(friend.getNickName());
				if(friend.getId().equals(member.getId())) { 
					Member fromMember = memberService.get(o.getMid());
					if(null==fromMember) {
						memberService.deleteFriendsAdd(o);
						continue;
					}
					if(StringUtils.isEmpty(fromMember.getHeadpic())) {
						b.setFrom_headpic("/img_sys/defaultHeadPic.jpg");
					} else {
						b.setFrom_headpic(fromMember.getHeadpic());
					}  
					b.setFrom_member_uuid(fromMember.getId());
					b.setFrom_memberid(fromMember.getMemberId());
					b.setFrom_name(fromMember.getNickName());
				} else { 
					if(StringUtils.isEmpty(member.getHeadpic())) {
						b.setFrom_headpic("/img_sys/defaultHeadPic.jpg");
					} else {
						b.setFrom_headpic(member.getHeadpic());
					}
					b.setFrom_member_uuid(member.getId());
					b.setFrom_memberid(member.getMemberId());
					b.setFrom_name(member.getNickName()); 
				}
				b.setStatus(o.getStatus().name());
				b.setContent(o.getContent());
				blist.add(b);
			}
		}
		ResponseUtils.json(response, 200,blist , null);
	}
	
}
				
				
				
				
				
				
				
				