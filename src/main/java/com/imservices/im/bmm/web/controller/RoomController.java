package com.imservices.im.bmm.web.controller;

import com.google.common.collect.Lists;
import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.*;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.dao.BaseDAO;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.entity.Room.GameType;
import com.imservices.im.bmm.service.*;
import com.imservices.im.bmm.utils.FTPUtil;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.utils.oss.OSSModel;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.imservices.im.bmm.websocket.cmd.AccessRecordCmd;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller("RoomJsonController")
@RequestMapping(value = "/room/json")
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class RoomController {


    private RoomService roomService;

    private WebConfigService configService;

    private MemberService memberService;

    private StoreComponent storeComponent;
    private IRUPService irupService;
    private AccessRecordService accessRecordService;
    private UserChatCmd userChatCmd;
    private SendUtils sendUtils;
    private AccessRecordCmd accessRecordCmd;
    private ChatStoreComponent chatStoreComponent;

    private RoomMemberService roomMemberService;
	private FunctionConfigService functionConfigService;
	private EmployeeService employeeService;
	private OSSModel ossModel;


    @AuthPassport
    @RequestMapping(value = "/getRoomMgrList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void getRoomMgrList(HttpServletRequest request,HttpServletResponse response) {
		try {
			String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
			String roomid = request.getParameter("roomid");
			RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
			if(null == roomBean ) {
				ResponseUtils.json(response, 200,"此群已不存在" , null);
				return;
			}

			if(!roomBean.getOwner_UUID().equals(MEMBERID)) {
				ResponseUtils.json(response, 500,"权限不足" , null);
				return;
			}
			/**获取所有群管理**/
			String mgrMemberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id","is_manager"}, new Object[]{roomid,1});

			List<MemberBean> list = new ArrayList<MemberBean>();
			if(!StringUtils.isEmpty(mgrMemberIds)) {
				String[] arrs = mgrMemberIds.split("#");
				for(String memberId : arrs) {
					if(StringUtils.isEmpty(memberId)) continue;
					MemberBean mb = storeComponent.getMemberBeanFromMapDB(memberId);
					if(null==mb) {
						roomBean.setMemberMgr_ids(mgrMemberIds.replaceAll(memberId+"#", ""));
						roomMemberService.deleteByHql(new RoomMemberEntity(roomid,memberId));
						continue;
					}
					MemberBean bean = new MemberBean();
					bean.setHeadpic(mb.getHeadpic());
					bean.setId(mb.getId());
					bean.setNickName(mb.getNickName());
					list.add(bean);
				}
			}
			roomBean.setMember_ids(roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid}));
			chatStoreComponent.putRoomBeanMap(roomid,roomBean);
			ResponseUtils.json(response, 200,list , null);
		}catch (Exception e) {
			log.error("",e);
		}

    }


    @AuthPassport
    @RequestMapping(value = "/removeRoomMgr",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void removeRoomMgr(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String roomid = request.getParameter("roomid");
        String mid = request.getParameter("mid");
        Room room = roomService.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == roomBean ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(!room.getOwner_UUID().equals(MEMBERID)) {
            ResponseUtils.json(response, 500,"权限不足" , null);
            return;
        }
		/**获取所有群管理**/
		String mgrMemberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id","is_manager"}, new Object[]{roomid,1});
        roomBean.setMemberMgr_ids(mgrMemberIds.replaceAll(mid+"#", ""));
        /**修改群管理员状态*/
		roomMemberService.updateMgrByHql(new RoomMemberEntity(roomid,mid,0) );
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);

		ChatTxtBean bean = new ChatTxtBean();
		bean.setFromUid("-1");
		bean.setToUid(mid);
		bean.setTxt("您被移除出群组【"+roomBean.getName()+"】的管理员队伍");
		userChatCmd.sendTXT(bean);   
		ResponseUtils.json(response, 200,roomBean , null);
	}
	
	
	@AuthPassport
	@RequestMapping(value = "/addRoomMgr",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void addRoomMgr(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String roomid = request.getParameter("roomid");
		String[] mids = request.getParameter("mids").split(",");
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		MemberBean member = storeComponent.getMemberBeanFromMapDB(MEMBERID);
		Room room = roomService.get(roomid);
		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == roomBean ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(!room.getOwner_UUID().equals(member.getId())) {
			ResponseUtils.json(response, 500,"权限不足" , null);
			return;
		}
		/**获取所有群管理**/
		String mgrMemberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id","is_manager"}, new Object[]{roomid,1});
		roomBean.setMemberMgr_ids(mgrMemberIds);
		for(String mid : mids) {
			if(StringUtils.isEmpty(mid)) continue;
			roomBean.setMemberMgr_ids(roomBean.getMemberMgr_ids()+mid+"#");
			
			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(mid);
			bean.setTxt("您被【"+member.getNickName()+"】设置为群组【"+roomBean.getName()+"】的管理员");
			userChatCmd.sendTXT(bean);
			roomMemberService.updateMgrByHql(new RoomMemberEntity(roomid,mid,1));
		}
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200,roomBean , null);
	}



	@AuthPassport
	@RequestMapping(value = "/uCnfSet",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void uCnfSet(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String roomid = request.getParameter("roomid");
//		RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == roomBean ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(StringUtils.isEmpty(roomBean.getOwner_UUID())||(!roomBean.getOwner_UUID().equals(MEMBERID)
				&& !roomBean.getMemberMgr_ids().contains(MEMBERID))) {
			ResponseUtils.json(response, 500,"出错" , null);
			return;
		}


		String yaoqingAble = request.getParameter("yaoqingAble");
		String yaoqingAuditAble = request.getParameter("yaoqingAuditAble");
		String mgrUid = request.getParameter("mgrUid");
		String mgrUid_opt = request.getParameter("mgrUid_opt");//0减 1加

		String[] ps = new String[] {};
		Object[] vs = new Object[] {};

		if(!StringUtils.isEmpty(yaoqingAble)&&NumberUtils.isNumber(yaoqingAble)) {
			roomBean.setYaoqingAble(Integer.valueOf(yaoqingAble));
			ps = ArrayUtils.add(ps, "yaoqingAble");
			vs = ArrayUtils.add(vs,Integer.valueOf(yaoqingAble));
		}

		if(!StringUtils.isEmpty(yaoqingAuditAble)&&NumberUtils.isNumber(yaoqingAuditAble)) {
			roomBean.setYaoqingAuditAble(Integer.valueOf(yaoqingAuditAble));
			ps = ArrayUtils.add(ps, "yaoqingAuditAble");
			vs = ArrayUtils.add(vs,Integer.valueOf(yaoqingAuditAble));
		}

		if(!StringUtils.isEmpty(mgrUid)&&!StringUtils.isEmpty(mgrUid_opt)) {
			RoomMemberEntity rmEntity=new RoomMemberEntity(roomid,mgrUid);
			if("0".equals(mgrUid_opt)) {
				roomBean.setMemberMgr_ids(roomBean.getMemberMgr_ids().replaceAll(mgrUid+"#", ""));
				rmEntity.setIs_manager(0);
			} else if("1".equals(mgrUid_opt)) {
				roomBean.setMemberMgr_ids(roomBean.getMemberMgr_ids()+mgrUid+"#");
				rmEntity.setIs_manager(1);
			}
			roomMemberService.updateMgrByHql(rmEntity);
		}

		roomService.update(BeanUtils.roomBeanTransferToRoomSimple(roomBean));
		ResponseUtils.json(response, 200,"" , null);
	}


	@AuthPassport
	@RequestMapping(value = "/yaoqiJoinRoom",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void yaoqiJoinRoom(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
		String fromUid = request.getParameter("fromUid");
		String temp = request.getParameter("uids");
		WebConfig config = configService.get();
//		RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean rb =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == rb ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		/**查询邀请人是否是管理员*/
		Long is_manager=roomMemberService.count(new String[]{"room_id","member_id","is_manager"}, new Object[]{roomid,fromUid,1});
		MemberBean mb = storeComponent.getMemberBeanFromMapDB(fromUid);
		if(rb.getYaoqingAble() ==0&&!fromUid.equals(rb.getOwner_UUID())&&is_manager==0) {
			//throw new Exception("没有相关权限");
			ResponseUtils.json(response, 500,"没有相关权限", null);
			return;
		}
		String[] uids = temp.split(",");
		if(!ArrayUtils.isEmpty(uids)) {
			/**根据群ID获取成员数量*/
			Long count=roomMemberService.count(new String[]{"room_id"}, new Object[]{roomid});
			if(count>=config.getRoom_members_limit().intValue()) {
				ResponseUtils.json(response, 500,"超过群人数上限"+ config.getRoom_members_limit(), null);
				return;
			}
			/**获取群成员ID字符串*/
			String memberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid});
			String names = "";
			int i=-1;
			for(Object uid : uids) {
				i++;
				if(null==uid||StringUtils.isEmpty(uid.toString())) continue;
				if(memberIds.contains(uid.toString())) continue;

				Long l = roomService.roomAddCount(new String[]{"mid","roomid","status"},new Object[]{uid.toString(),roomid,RoomAdd.Status.wait});

				if(null!=l&&l>0) continue;

				if(rb.getYaoqingAuditAble() ==1) {
					RoomAdd room_add = new RoomAdd();
					room_add.setContent("来自【"+mb.getNickName()+"】的邀请");
					room_add.setCreateDate(new Date());
					room_add.setRoomid(rb.getId());
					room_add.setMid(uid.toString());
					room_add.setRoom_owner_id(rb.getOwner_UUID());
					room_add.setStatus(RoomAdd.Status.wait);
					room_add.setYaoqing_id(fromUid);
					roomService.saveRoomAdd(room_add);

					List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(rb.getOwner_UUID());
					for(WebSocketSession ws : ws_list) {
						Message message = new Message();
						message.setBody(null);
						message.setCMD(Message.CMD_ENUM.ROOMADD.name());
						sendUtils.send(ws,message);
					}

				} else {
					//不需要审核
					rb.setMember_ids(memberIds+uid+"#");
					MemberBean m = storeComponent.getMemberBeanFromMapDB(uid.toString());
					names+=(m.getNickName());
					if(i<uids.length-1) {
						names+=("、");
					}

				}

				ChatTxtBean bean = new ChatTxtBean();
				bean.setFromUid("-1");
				bean.setToUid(uid.toString());
				bean.setTxt("您被【"+mb.getNickName()+"】邀请申请加入【"+rb.getName()+"】群组");
				userChatCmd.sendTXT(bean);

				roomMemberService.save(new RoomMemberEntity(rb.getId(),uid.toString()));
			}
			//不需要审核
			if(rb.getYaoqingAuditAble() ==0) {
				Room room =roomService.get(rb.getRoomUUID());
				room.setMember_ids(rb.getMember_ids());
//				roomService.update(room);
//				updateRoomHeadpic(rb,request);

				// 更新缓存
				for(Object uid : uids) {
					Member member = memberService.get(uid.toString());
					Map<String, MemberBean> map = Collections.singletonMap(uid.toString(),BeanUtils.memberToBeanSimple(member));
					chatStoreComponent.putGroupMemberMap(rb.getRoomUUID(),map);
				}
				chatStoreComponent.putRoomBeanMap(rb.getRoomUUID(),BeanUtils.roomToBeanSimple(room));

				StringBuffer html = new StringBuffer("<div style='font-size:13px'><span style='color: #FF3F33;margin: 0 2px;'>"+mb.getNickName()+"</span>邀请<span style='color: #FF3F33;margin: 0 2px;'>"+names+"</span>加入群组</div>");
                /**获取所有群管理**/
                String mgrMemberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id","is_manager"}, new Object[]{roomid,1});
                RoomBean mgrRoom=new RoomBean();
                //只发送给群管理员
				mgrRoom.setId(rb.getId());
                mgrRoom.setMember_ids(mgrMemberIds);
				ChatTxtBean txtBean = new ChatTxtBean();
				txtBean.setToGroupid(rb.getRoomUUID());
				txtBean.setTxt(html.toString());

				MessageBean msgBean = new MessageBean();
				msgBean.setChatid(txtBean.getToGroupid());
				msgBean.setChatType("1");
				msgBean.setType(MessageBean.MessageType.SYS_TXT.name());
				msgBean.setBean(txtBean);

				Message msg = new Message();
				msg.setBody(Lists.newArrayList(msgBean));
				msg.setCMD(Message.CMD_ENUM.CHAT_SYS_TXT.name());
				sendUtils.send(mgrRoom, msg);

			}
			ResponseUtils.json(response, 200,rb , null);
		}
	}

//	private void updateRoomHeadpic(RoomBean rb , HttpServletRequest request) {
//		CompletableFuture.runAsync(()->{
//			try {
//				List<String> imgs = new ArrayList<String>();
//				List<String> hlist = new ArrayList<String>();
//				/**获取群成员ID字符串*/
//				String memberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{rb.getId()});
//				if(!StringUtils.isEmpty(memberIds)) {
//					String[] arrs = memberIds.split("#");
//					for(String o : arrs) {
//						if(StringUtils.isEmpty(o)) continue;
//
//						MemberBean mb = storeComponent.getMemberBeanFromMapDB(o);
//						if(null==mb) continue;
//						if(imgs.size()<5) {
//							imgs.add(mb.getHeadpic());
//						}
//
//						if(hlist.size()<9) {
//							if(StringUtils.isEmpty(mb.getHeadpic()) || "/img_sys/defaultHeadPic.jpg".equals(mb.getHeadpic())) {
//								hlist.add(request.getRealPath("/img_sys/defaultHeadPic.jpg"));
//							} else {
//								if(new File(request.getRealPath(mb.getHeadpic())).exists()) {
//									hlist.add(request.getRealPath(mb.getHeadpic()));
//								}
//							}
//						}
//						if(hlist.size()>=9)  break;
//					}
//
//					System.out.println("hlist:"+hlist.size());
//
//					rb.setTop5Hp(imgs);
//
//					if(rb.getUseCustomHeadpic() !=1) {
//						if(!StringUtils.isEmpty(rb.getImg())) {
//							//FileProcess.del(request.getRealPath(rb.getImg()));
//							FTPUtil.deleteFile("chat_img", rb.getImg().substring(rb.getImg().lastIndexOf("/")+1,rb.getImg().length()));
//
//						}
//						String fn = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
//						MakeGropHeadPicUtil.getCombinationOfhead(hlist, request.getRealPath("/images/upload/chat/group_hp"), fn);
//						rb.setImg("/images/upload/chat/group_hp/"+fn);
//
//						roomService.update(BeanUtils.roomBeanTransferToRoomSimple(rb));
//						chatStoreComponent.putRoomBeanMap(rb.getId(),rb);
//						accessRecordService.updateByEid(rb.getId(),new String[]{"img"},new String[]{rb.getImg()});
//					}
//
//				}
//			}catch (Exception e) {
//				// TODO @shenghong
//				log.error("updateRoomHeadpic--- 需要改为 oss",e);
//			}
//		});
//
//
//	}



	@AuthPassport
	@RequestMapping(value = "/transferGroup",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void transferGroup(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String roomid = request.getParameter("roomid");
		String toUid = request.getParameter("toUid");
//		RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == roomBean ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(!MEMBERID.equals(roomBean.getOwner_UUID())) {
			ResponseUtils.json(response, 500,"不是群主无法转让" , null);
			return;
		}
		Member member = memberService.get("memberId", toUid);
		MemberBean fromMember = storeComponent.getMemberBeanFromMapDB(MEMBERID);
		if(null==member) {
			ResponseUtils.json(response, 500,"用户不存在" , null);
			return;
		}
		roomBean.setOwner(member.getMemberId());
		roomBean.setOwner_UUID(member.getId());
		roomBean.setOwnerName(member.getNickName());
		String str = roomBean.getMember_ids();
		if(str.contains(member.getId() + "#")) {
			str = str.replaceAll(member.getId()+"#", "");
		}
		roomBean.setMember_ids(member.getId()+"#"+str);
		Room room = roomService.get(roomid);
		room.setOwner(member.getMemberId());
		room.setOwner_UUID(member.getId());
		roomService.update(room);

		ChatTxtBean bean = new ChatTxtBean();
		bean.setFromUid("-1");
		bean.setToUid(member.getId());
		bean.setTxt(fromMember.getNickName()+"把群组【"+roomBean.getName()+"】转让给你");
		userChatCmd.sendTXT(bean);

		bean = new ChatTxtBean();
		bean.setFromUid("-1");
		bean.setToUid(fromMember.getId());
		bean.setTxt("你成功把群组【"+roomBean.getName()+"】转让给【"+member.getNickName()+"】");
		userChatCmd.sendTXT(bean);
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200,roomBean , null);
	}
	
	
	@AuthPassport
	@RequestMapping(value = "/clearHeadpic",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void clearHeadpic(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String roomid = request.getParameter("roomid");
//		RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == roomBean ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(!roomBean.getOwner_UUID().equals(MEMBERID)
				&&roomBean.getMemberMgr_ids().indexOf(MEMBERID)<0 ) {
			ResponseUtils.json(response, 500,"权限不足" , null);
			return;
		}
		roomBean.setUseCustomHeadpic(0);
//		updateRoomHeadpic(roomBean, request);

		Room room = roomService.get(roomid);
		room.setUseCustomHeadpic(0);
		room.setHeadImg("/img_sys/roomDefaulHeadPic.jpg");
		roomService.update(room);
		accessRecordService.updateByEid(roomBean.getId(),new String[]{"img"},new String[]{"/img_sys/roomDefaulHeadPic.jpg"});
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200,room , null);
	}

	@AuthPassport
	@RequestMapping(value = "/isRoomMember",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void isRoomMember(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String roomid = request.getParameter("roomid");
//		RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean room =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == room ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(StringUtils.isEmpty(room.getMember_ids())|| !room.getMember_ids().contains(MEMBERID)) {
			ResponseUtils.json(response, 200,"0" , null); 
		} else {
			ResponseUtils.json(response, 200,"1" , null); 
		}
	}
	
	
	
	@AuthPassport
	@RequestMapping(value = "/verifyDo",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void verifyDo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String raid = request.getParameter("raid");
		RoomAdd ra = roomService.getRoomAdd(raid);
		
		String roomid = ra.getRoomid();
		String mid = ra.getMid();
		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null == roomBean ) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		String t = request.getParameter("t");//1通过 2拒绝
		if(StringUtils.isEmpty(t)||(!"1".equals(t)&&!"2".equals(t))) {
			ResponseUtils.json(response,500,"错误",null);  
			return;
		}
		roomService.deleteRoomAdd(ra);
		if("2".equals(t)) {
			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(mid);
			bean.setTxt("您申请加入群组【"+roomBean.getName()+"】已被拒绝");
			userChatCmd.sendTXT(bean);
			
			ResponseUtils.json(response, 200,"" , null);
			return;
		}
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);

        if(!roomBean.getOwner_UUID().equals(MEMBERID)
                &&roomBean.getMemberMgr_ids().indexOf(MEMBERID)<0) {
            ResponseUtils.json(response,500,"没权限",null);
            return;
        }
        /**通过申请，把数据加入中间表**/
        roomMemberService.save(new RoomMemberEntity(roomid,mid));

//        roomBean.setMember_ids(roomBean.getMember_ids()+mid+"#");
		/**获取群成员ID字符串*/
		String memberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid});
        roomBean.setMember_ids(memberIds+mid+"#");
        ChatTxtBean bean = new ChatTxtBean();
        bean.setFromUid("-1");
        bean.setToUid(mid);
        bean.setTxt("您申请加入群组【"+roomBean.getName()+"】已通过验证");
        userChatCmd.sendTXT(bean);

        MemberBean joiner = storeComponent.getMemberBeanFromMapDB(mid);
        StringBuffer html = new StringBuffer();
        if(!StringUtils.isEmpty(ra.getYaoqing_id())) {
            MemberBean yq_er = storeComponent.getMemberBeanFromMapDB(ra.getYaoqing_id());
            html = new StringBuffer("<div style='font-size:13px'><span style='color: #FF3F33;margin: 0 2px;'>"+yq_er.getNickName()+"</span>邀请<span style='color: #FF3F33;margin: 0 2px;'>"+joiner.getNickName()+"</span>加入群组</div>");
        } else {
            html = new StringBuffer("<div style='font-size:13px'><span style='color: #FF3F33;margin: 0 2px;'>"+joiner.getNickName()+"</span>加入群组</div>");
        }

		/**获取所有群管理**/
		String mgrMemberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id","is_manager"}, new Object[]{roomid,1});
		RoomBean mgrRoom=new RoomBean();
		//只发送给群管理员
		mgrRoom.setId(roomBean.getId());
		mgrRoom.setMember_ids(mgrMemberIds);
        ChatTxtBean txtBean = new ChatTxtBean();
        txtBean.setToGroupid(roomBean.getRoomUUID());
        txtBean.setTxt(html.toString());

        MessageBean msgBean = new MessageBean();
        msgBean.setChatid(txtBean.getToGroupid());
        msgBean.setChatType("1");
        msgBean.setType(MessageBean.MessageType.SYS_TXT.name());
        msgBean.setBean(txtBean);

        Message msg = new Message();
        msg.setBody(Lists.newArrayList(msgBean));
        msg.setCMD(Message.CMD_ENUM.CHAT_SYS_TXT.name());
        sendUtils.send(mgrRoom, msg);


		if(!StringUtils.isEmpty(roomBean.getMember_ids())) {
//			updateRoomHeadpic(roomBean, request);
		} 
		
		AccessRecord nar = new AccessRecord();
		nar.setCreateDate(new Date());
		nar.setImg(roomBean.getImg());
		nar.setUid(mid);
		nar.setEntityid(roomBean.getId());
		nar.setTypeid("1");
		nar.setTitle(roomBean.getName());
		accessRecordService.save(nar);
		accessRecordCmd.insertOrUpdate( BeanUtils.accessRecordToBean(nar), mid, CMD_ENUM.AR_INSERT);
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200,roomBean , null);
	}
	
	
	
	@AuthPassport
	@RequestMapping(value = "/verify_list",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void verify_list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		 String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		 List<RoomAdd> ra_list = roomService.roomAddList(new String[]{"room_owner_id","status"},new Object[]{MEMBERID,RoomAdd.Status.wait});
		 List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
		 SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		 for(RoomAdd ra : ra_list) {
//			 RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(ra.getRoomid());
			 RoomBean rb =chatStoreComponent.getRoomBeanMap(ra.getRoomid());
			 if(null == rb) {
				 ResponseUtils.json(response, 200,"此群已不存在" , null);
				 return;
			 }
			 MemberBean mb = storeComponent.getMemberBeanFromMapDB(ra.getMid());
			 
			 Map<String, Object> map = new HashMap<String, Object>();
			 map.put("user_name", mb.getNickName());
			 map.put("id", ra.getId());
			 map.put("user_img", mb.getHeadpic());
			 map.put("room_name", rb.getName());
			 map.put("room_img",rb.getImg());
			 map.put("txt", ra.getContent());
			 map.put("date",sdf.format(ra.getCreateDate()));
			 list1.add(map);
		 }
		 ResponseUtils.json(response, 200, list1, null);
	}
	
	
	@AuthPassport    
	@RequestMapping(value = "/sendVerify",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void sendVerify(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String txt = request.getParameter("txt");
		String roomid = request.getParameter("roomid");
		if(StringUtils.isEmpty(txt)||StringUtils.isEmpty(roomid)) return;
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
//		RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(roomid);
		RoomBean rb =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==rb) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(rb.getMember_ids().indexOf(MEMBERID)>=0) {
			ResponseUtils.json(response,500,"您已加入此群",null);  
			return;
		}
		WebConfig config = configService.get();
		if((rb.getMember_ids().split("#").length + 1)>=config.getRoom_members_limit().intValue()) {
			ResponseUtils.json(response, 500,"超过群人数上限"+config.getRoom_members_limit().intValue() , null);
			return;
		}
		
		Long l = roomService.roomAddCount(new String[]{"mid","roomid","status"},new Object[]{MEMBERID,roomid,RoomAdd.Status.wait});
		if(null!=l&&l>0) {
			ResponseUtils.json(response,500,"请匆重复提交,等待对方验证",null);  
			return;
		}
		
		MemberBean from_member = storeComponent.getMemberBeanFromMapDB(MEMBERID);
		RoomAdd room_add = new RoomAdd();
		room_add.setContent(txt);
		room_add.setCreateDate(new Date());
		room_add.setRoomid(rb.getId());
		room_add.setMid(from_member.getId());
		room_add.setRoom_owner_id(rb.getOwner_UUID());
		room_add.setStatus(RoomAdd.Status.wait);
		roomService.saveRoomAdd(room_add);
		
		
		List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(rb.getOwner_UUID());
		for(WebSocketSession ws : ws_list) {
			Message message = new Message();
			message.setBody(null);
			message.setCMD(Message.CMD_ENUM.ROOMADD.name());
			sendUtils.send(ws,message);
		}
		
		
		ResponseUtils.json(response,200,"",null);  
	}
	
	
	@AuthPassport
	@RequestMapping(value = "/search_list",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void search_list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		 String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		 String kw = request.getParameter("kw");
		 if(StringUtils.isEmpty(kw)) {
			 ResponseUtils.json(response, 500, "关键词不能为空", null);
		 }
		 List<Room> list = roomService.getList(new String[] {"name"+BaseDAO.OR,"roomId"}, new Object[] {"%"+kw+"%",kw});
		 List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
		 for(Room r : list) {
			 RoomBean rb = BeanUtils.roomToBeanSimple(r);
			 MemberBean mb = storeComponent.getMemberBeanFromMapDB(rb.getOwner_UUID());
			 Map<String, Object> map = new HashMap<String, Object>();
			 map.put("name", r.getName());
			 map.put("id", r.getId());
			 map.put("img", rb.getImg());
			 map.put("owner", mb.getNickName());
			 map.put("owner_img", mb.getHeadpic());
			 
			 
			 if(rb.getMember_ids().indexOf(MEMBERID)>=0) {
				 map.put("status", 1);
			 } else { 
				 Long l = roomService.roomAddCount(new String[]{"mid","roomid","status"},new Object[]{MEMBERID,r.getId(),RoomAdd.Status.wait});
				 if(null!=l&&l>0) {
					map.put("status", 2);
				 } else {
					map.put("status", 0);
				 }
			 }
			 
			 
			 
			
			 
			 list1.add(map);
		 }
		 ResponseUtils.json(response, 200, list1, null);
	}
	
	//解散
	@AuthPassport
	@RequestMapping(value = "/dissolve",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void dissolve(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String roomid = request.getParameter("roomid");
//		RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

//			//
//			//
        if(!roomBean.getOwner_UUID().equals(MEMBERID)) {
            ResponseUtils.json(response, 500,"不是群主无法解散此群" , null);
        }

        if(!StringUtils.isEmpty(roomBean.getMember_ids())) {
            String[] arrs = roomBean.getMember_ids().split("#");
            for(String uid : arrs) {
                if(StringUtils.isEmpty(uid)) continue;

//                List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
//                for(WebSocketSession ws : ws_list) {
//                    Message message = new Message();
//                    message.setBody(roomBean.getId());
//                    message.setCMD(Message.CMD_ENUM.GROUP_MEMBER_REMOVE.name());
//                    sendUtils.send(ws, message);
//                }
				roomService.sendRemoveMsg(roomBean.getId(),uid);


                ChatTxtBean bean = new ChatTxtBean();
                bean.setFromUid("-1");
                bean.setToUid(uid);
                bean.setTxt("您的群组【"+roomBean.getName()+"】已被解散");
                userChatCmd.sendTXT(bean);
                roomMemberService.deleteByHql(new RoomMemberEntity(roomBean.getId(),uid));
            }
        }



        if(!StringUtils.isEmpty(roomBean.getImg())) {
            //FileProcess.del(request.getRealPath(roomBean.getImg()));
            FTPUtil.deleteFile("chat_img", roomBean.getImg().substring(roomBean.getImg().lastIndexOf("/")+1,roomBean.getImg().length()));
        }
        accessRecordService.deleteByHql("delete from AccessRecord where entityid='"+roomBean.getId()+"'");
		roomService.delete(roomBean.getId());
//		ChatStore.ROOMB_BEAN_MAP.remove(roomBean.getId());
		chatStoreComponent.delRoomBeanMap(roomBean.getId());
		
		ResponseUtils.json(response, 200,roomBean , null);
		
	}
	
	//退组
	@AuthPassport
	@RequestMapping(value = "/out",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void out(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String roomid = request.getParameter("roomid");
		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(roomBean.getOwner_UUID().equals(MEMBERID)) {
            ResponseUtils.json(response, 500,"群组无法退群" , null);
        }
//        roomBean.setMember_ids(roomBean.getMember_ids().replaceAll(MEMBERID+"#", ""));
		/**获取群成员ID字符串*/
		String memberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid});
		roomBean.setMember_ids(memberIds.replaceAll(MEMBERID+"#", ""));
        if(!StringUtils.isEmpty(roomBean.getMember_ids())) {

//			updateRoomHeadpic(roomBean, request);

			roomMemberService.deleteByHql(new RoomMemberEntity(roomid,MEMBERID));
			chatStoreComponent.putRoomBeanMap(roomid,roomBean);

			accessRecordService.updateByEid(roomBean.getId(),new String[]{"title"},new String[]{roomBean.getName()});
			
			
			accessRecordService.deleteByHql("delete from AccessRecord where typeid=1 and entityid='"+roomBean.getId()+"' and uid='"+MEMBERID+"'");
			
			
			
			List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(MEMBERID);
			for(WebSocketSession ws : ws_list) {
				Message message = new Message();
				message.setBody(roomBean.getId()); 
				message.setCMD(Message.CMD_ENUM.GROUP_MEMBER_REMOVE.name());
				sendUtils.send(ws, message);
			}
			
			
		}
		ChatTxtBean bean = new ChatTxtBean();
		bean.setFromUid("-1");
		bean.setToUid(MEMBERID);
		bean.setTxt("您已退出群组【"+roomBean.getName()+"】");
		userChatCmd.sendTXT(bean);   
		ResponseUtils.json(response, 200,roomBean , null);
	}
	
	
	
	@RequestMapping(value = "/isRoomDomain/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void isRoomDomain(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String domain = request.getServerName();
		//
		Room room = roomService.get("domain", domain);
		if(null!=room&&room.getIndependence()==1) {
			request.getSession().setAttribute("IndependenceRoomUUID", room.getId());//用于作为已进入独立房间的标记
			ResponseUtils.json(response, 200,BeanUtils.roomToBeanSimple(room) , null);
		} else {
			ResponseUtils.json(response, 500,"" , null); 
		}
	}


    @AuthPassport
    @RequestMapping(value = "/accessUserList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void accessUserList(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String p = request.getParameter("p");
        String room_uuid = request.getParameter("room_uuid");

//        RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(room_uuid);

		RoomBean rb =chatStoreComponent.getRoomBeanMap(room_uuid);
		if(null==rb) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(rb.getIndependence()==0||!rb.getOwner_UUID().equals(MEMBERID)) {
            ResponseUtils.json(response, 500, "操作无权限", null);
            return;
        }

        if(StringUtils.isEmpty(p)||!NumberUtils.isNumber(p)) p = "1";

        String[] ps = new String[]{"typeid","entityid"};
        Object[] vs = new Object[]{"1",room_uuid};

        String uid = request.getParameter("uid");
        if(!StringUtils.isEmpty(uid)) {
            Member m = memberService.get("memberId", uid);
            if(null!=m) {
                ps = ArrayUtils.add(ps, "uid");
                vs = ArrayUtils.add(vs, m.getId());
            }

        }

        List<AccessRecord> list = accessRecordService.findByPager(ps, vs, new Pager(Integer.valueOf(p), 20, null, null)).getList();
        List<AccessRecordBean> blist = new ArrayList<AccessRecordBean>();

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
        Date now = new Date();
        Date yestoday = new Date();
        yestoday.setDate(yestoday.getDate()-1);
        DecimalFormat df = new DecimalFormat("0.###");
        for(AccessRecord e : list) {
            AccessRecordBean o = new AccessRecordBean();
            Member m = memberService.get(e.getUid());
            if(null==m) continue;
            MemberBean mb = BeanUtils.memberToBeanSimple(m);
            mb.setSendRedPriceSum(m.getSendRedPriceSum());
            mb.setOpenRedPriceSum(m.getOpenRedPriceSum());
            o.setMb(mb);
            List<IndependentRoomUserPrice> irups = irupService.getList(new String[] {"room_uuid","user_uuid"}
                    , new Object[] {e.getEntityid(),mb.getId()});
            if(!irups.isEmpty()) {
                o.setRoomMoney(df.format(irups.get(0).getPrice()));
            } else {
                o.setRoomMoney("0.0");
            }

            o.setContent("");
            o.setId(e.getEntityid());
            o.setTypeid(e.getTypeid());
            o.setImg(e.getImg());
            o.setTitle(e.getTitle());
            Date d = (Date) e.getCreateDate().clone();
            if(d.getYear()==now.getYear()&&d.getMonth()==now.getMonth()&&d.getDate()==now.getDate()) {
                o.setCreateDate(sdf1.format(d));
            } else if(d.getDate() == yestoday.getDate()) {
                o.setCreateDate("昨天");
            } else {
                o.setCreateDate(sdf2.format(d));
            }
            o.setContent("");
            o.setSubname(e.getSubname());
            blist.add(o);

        }
        ResponseUtils.json(response,200, blist, null);

    }


    @AuthPassport
    @RequestMapping(value = "/isIndependent/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void isIndependent_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String roomid = request.getParameter("room_uuid");
		RoomBean room =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==room) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}
        int idpe = room.getIndependence();
        ResponseUtils.json(response, 200,idpe+"" , null);
    }



    @AuthPassport
    @RequestMapping(value = "/isStopSpeak4User",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void isStopSpeak4User(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String roomid = request.getParameter("roomid");
//        RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean room =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==room) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}
//		if(StringUtils.isEmpty(room.getOwner_UUID())||!room.getOwner_UUID().equals(MEMBERID)) {
//			ResponseUtils.json(response, 500,"出错" , null);
//			return;
//		}
        String uid = request.getParameter("uid");
        if(room.getStopspeak_member_ids().indexOf(uid+"#")<0) {
            ResponseUtils.json(response, 200,"0" , null);
        } else {
            ResponseUtils.json(response, 200,"1" , null);
        }


    }


    @AuthPassport
    @RequestMapping(value = "/stopSpeakSingleList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void stopSpeakSingleList(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String roomid = request.getParameter("roomid");
//        RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean room =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==room) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(StringUtils.isEmpty(room.getOwner_UUID())||(!room.getOwner_UUID().equals(MEMBERID)
                &&room.getMemberMgr_ids().indexOf(MEMBERID)<0)) {
            ResponseUtils.json(response, 500,"出错" , null);
            return;
        }
        List<MemberBean> list = new ArrayList<MemberBean>();
        if(!StringUtils.isEmpty(room.getStopspeak_member_ids())) {
            String[] ids = room.getStopspeak_member_ids().split("#");
            for(String id : ids) {
                if(StringUtils.isEmpty(id)) continue;
                MemberBean mb = storeComponent.getMemberBeanFromMapDB(id);
                if(null!=mb) list.add(mb);

            }
        }
        ResponseUtils.json(response, 200,list , null);
    }


    @AuthPassport
    @RequestMapping(value = "/uStopSpeakSingle",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void uStopSpeakSingle(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String roomid = request.getParameter("roomid");

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(StringUtils.isEmpty(roomBean.getOwner_UUID())||(!roomBean.getOwner_UUID().equals(MEMBERID)
                &&roomBean.getMemberMgr_ids().indexOf(MEMBERID)<0)) {
            ResponseUtils.json(response, 500,"出错" , null);
            return;
        }
        String uid = request.getParameter("uid");
		RoomMemberEntity rm=new RoomMemberEntity(roomid,uid);
		/**禁言*/
        if(roomBean.getStopspeak_member_ids().indexOf(uid)<0) {
            roomBean.setStopspeak_member_ids(roomBean.getStopspeak_member_ids()+uid+"#");
			rm.setIs_stop_speaker(1);
        } else {
            roomBean.setStopspeak_member_ids(roomBean.getStopspeak_member_ids().replaceAll(uid+"#", ""));
			rm.setIs_stop_speaker(0);
        }
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		roomMemberService.updateSpeakerByHql(rm);
        ResponseUtils.json(response, 200,"" , null);
    }

    @AuthPassport
    @RequestMapping(value = "/uStopSpeak/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void uStopSpeak_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String roomid = request.getParameter("roomid");
//        RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);
        RoomBean roomBean = chatStoreComponent.getRoomBeanMap(roomid);
        if(StringUtils.isEmpty(roomBean.getOwner_UUID())||!roomBean.getOwner_UUID().equals(MEMBERID)
                &&roomBean.getMemberMgr_ids().indexOf(MEMBERID)<0) {
            ResponseUtils.json(response, 500,"出错" , null);
            return;
        }
        String stopSpeak = request.getParameter("stopSpeak");
        Map prosMap = new HashMap();
        if(!StringUtils.isEmpty(roomBean.getProperties())) {
            prosMap = JsonUtil.getMapFromJson(roomBean.getProperties());
        }
        prosMap.put("STOPSPEAK", Integer.valueOf(stopSpeak));
        roomBean.setProperties(JsonUtil.getJSONString(prosMap));
        roomBean.setStopSpeak(Integer.valueOf(stopSpeak));
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		roomService.update(BeanUtils.roomBeanTransferToRoomSimple(roomBean));
        ResponseUtils.json(response, 200,"" , null);
    }

    @AuthPassport
    @RequestMapping(value = "/getOwner/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void getOwner_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String roomid = request.getParameter("roomid");
//        RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(roomid);
		RoomBean room =chatStoreComponent.getRoomBeanMap(roomid);
		if(null==room) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(StringUtils.isEmpty(room.getOwner_UUID())) {
            ResponseUtils.json(response, 500,"" , null);
            return;
        } else {
            Member member = memberService.get(room.getOwner_UUID());
            ResponseUtils.json(response, 200,BeanUtils.memberToBeanSimple(member) , null);
        }

    }

    @AuthPassport
    @RequestMapping(value = "/onlineMemberList/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void onlineMemberList_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        //MemberBean bean = GameStore.USER_BEAN_MAP.get(MEMBERID);
        MemberBean bean = storeComponent.getMemberBeanFromMapDB(MEMBERID);
        String roomid = request.getParameter("roomid");
        String len = request.getParameter("len");
//		Map<String,MemberBean> map = ChatStore.GROUP_MEMBER_MAP.get(roomid);
        Map<String,MemberBean> map = chatStoreComponent.getGroupMemberMap(roomid);
        LinkedList<MemberBean> list = new LinkedList<MemberBean>();


		if(null!=map&&!map.isEmpty()) { 
			list.addAll(map.values());
		}
		if(!map.containsKey(MEMBERID)) {
			list.add(bean);
		} 
		    
		
		if(list.size()<=Integer.valueOf(len)) {
			ResponseUtils.json(response, 200,list , null);
		} else {
			//
			ResponseUtils.json(response, 200,list.subList(0, Integer.valueOf(len)) , null);
		}
		
	}
	
	
	@RequestMapping(value = "/updateName",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void updateName(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
		String name = request.getParameter("name");
		if(StringUtils.isEmpty(roomid)||StringUtils.isEmpty(name)) {
			ResponseUtils.json(response, 500, "信息要填写完整", null);
			return;
		}
		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}
		roomBean.setName(name);

		Room room =roomService.get(roomid);
		room.setName(name);
		roomService.update(room);
		accessRecordService.updateByEid(roomBean.getId(),new String[]{"title"},new String[]{roomBean.getName()});
		roomBean.setMember_ids(roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid}));
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);

		ResponseUtils.json(response, 200, roomBean.getName(), null);
	}


	@RequestMapping(value = "/updateDescri",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void updateDescri(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
		String descri = request.getParameter("descri");
		if(StringUtils.isEmpty(roomid)||StringUtils.isEmpty(descri)) {
			ResponseUtils.json(response, 500, "信息要填写完整", null);
			return;
		}
		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		roomBean.setDescri(descri);

		Room room =roomService.get(roomid);
		room.setDescri(descri);
		roomService.update(room);
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200, roomBean.getDescri(), null);
	}

	@AuthPassport
	@RequestMapping(value = "/addMember",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void addMember(HttpServletRequest request,HttpServletResponse response) throws Exception {

		String roomid = request.getParameter("roomid");
		String[] mids = request.getParameter("mids").split(",");
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		MemberBean member = storeComponent.getMemberBeanFromMapDB(MEMBERID);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}
		/**根据群ID获取成员数量*/
		Long members=roomMemberService.count(new String[]{"room_id"}, new Object[]{roomid});
		WebConfig config = configService.get();
		if(members>=config.getRoom_members_limit().intValue()) {
			ResponseUtils.json(response, 500,"超过群人数上限"+config.getRoom_members_limit().intValue() , null);
			return;
		}
		/**获取群成员ID字符串*/
		String memberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid});
		/**不从redis获取，有可能是后台添加了人**/
		roomBean.setMember_ids(memberIds);
		String names = "";
		int i = -1;
		for(String mid : mids) {
			i++;
			if(StringUtils.isEmpty(mid)) continue;
			/**如果存在，则不添加*/
			if(memberIds.indexOf(mid)>=0)continue;
			MemberBean m = storeComponent.getMemberBeanFromMapDB(mid);

			roomBean.setMember_ids(roomBean.getMember_ids()+mid+"#");

			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(mid);
			bean.setTxt("您被【"+member.getNickName()+"】邀请加入【"+roomBean.getName()+"】群组");
			userChatCmd.sendTXT(bean);

			names+=(m.getNickName());
			if(i<mids.length-1) {
				names+=("、");
			}
			roomMemberService.save(new RoomMemberEntity(roomid,mid));
		}
		/**如果没有添加用户，则不发送消息*/
        if(!StringUtils.isEmpty(names)) {
			if (!StringUtils.isEmpty(roomBean.getMember_ids())) {

				StringBuffer html = new StringBuffer("<div style='font-size:13px'><span style='color: #FF3F33;margin: 0 2px;'>" + member.getNickName() + "</span>邀请<span style='color: #FF3F33;margin: 0 2px;'>" + names + "</span>加入群组</div>");
				/**获取所有群管理**/
				String mgrMemberIds = roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id", "is_manager"}, new Object[]{roomid, 1});
				RoomBean mgrRoom = new RoomBean();
				//只发送给群管理员
				mgrRoom.setId(roomBean.getId());
				mgrRoom.setMember_ids(mgrMemberIds);
				ChatTxtBean txtBean = new ChatTxtBean();
				txtBean.setToGroupid(roomBean.getRoomUUID());
				txtBean.setTxt(html.toString());

				MessageBean msgBean = new MessageBean();
				msgBean.setChatid(txtBean.getToGroupid());
				msgBean.setChatType("1");
				msgBean.setType(MessageBean.MessageType.SYS_TXT.name());
				msgBean.setBean(txtBean);

				Message msg = new Message();
				msg.setBody(Lists.newArrayList(msgBean));
				msg.setCMD(Message.CMD_ENUM.CHAT_SYS_TXT.name());
				sendUtils.send(mgrRoom, msg);
//				updateRoomHeadpic(roomBean, request);

				for (String mid : mids) {
					Member memberEntity = memberService.get(mid);
					Map<String, MemberBean> map = Collections.singletonMap(mid, BeanUtils.memberToBeanSimple(memberEntity));
					chatStoreComponent.putGroupMemberMap(roomid, map);
				}
				chatStoreComponent.putRoomBeanMap(roomid, roomBean);
				accessRecordService.updateByEid(roomBean.getId(), new String[]{"title"}, new String[]{roomBean.getName()});
			}
		}
		ResponseUtils.json(response, 200,roomBean , null);
	}
	
	
	@AuthPassport
	@RequestMapping(value = "/removeMember",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void removeMember(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
		String mid = request.getParameter("mid");
		Room room = roomService.get(roomid);
//		RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		if(roomBean.getOwner_UUID().equals(mid)) { 
			ResponseUtils.json(response, 500,"群主无法移除" , null);
			return;
		}
		/**获取群成员ID字符串*/
		String memberIds=roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid});
		/**不从redis获取，有可能是后台添加了人**/
		roomBean.setMember_ids(memberIds.replaceAll(mid+"#", ""));
//		roomBean.setMember_ids(roomBean.getMember_ids().replaceAll(mid+"#", ""));
		//更新到redis
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);

		if(!StringUtils.isEmpty(roomBean.getMember_ids())) {
//			updateRoomHeadpic(roomBean, request);
			accessRecordService.updateByEid(roomBean.getId(),new String[]{"title"},new String[]{roomBean.getName()});

			accessRecordService.deleteByHql("delete from AccessRecord where typeid=1 and entityid='"+room.getId()+"' and uid='"+mid+"'");


			List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(mid);
			for(WebSocketSession ws : ws_list) {
				Message message = new Message();
				message.setBody(room.getId());
				message.setCMD(Message.CMD_ENUM.GROUP_MEMBER_REMOVE.name());
				sendUtils.send(ws, message);
			}
			roomService.sendRemoveMsg(room.getId(),mid);
			roomMemberService.deleteByHql(new RoomMemberEntity(room.getId(),mid));
		}
		FunctionConfigEntity fcEntity=functionConfigService.get();
		/**群主/群管理踢人是否删除被踢人信息0否1是*/
		if (fcEntity.getOut_room_del_message()==0) {
			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(mid);
			bean.setTxt("您被移除群组【"+roomBean.getName()+"】");
			userChatCmd.sendTXT(bean);
		}

		ResponseUtils.json(response, 200,roomBean , null);
	}
	@AuthPassport
	@RequestMapping(value = "/getNotShimingMemberList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getNotShimingMemberList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
//		RoomBean roomBean = ChatStore.ROOMB_BEAN_MAP.get(roomid);

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

		List<MemberBean> list = new ArrayList<MemberBean>();
		if(!StringUtils.isEmpty(roomBean.getMember_ids())) {
			String[] arrs = roomBean.getMember_ids().split("#");
			for(String o : arrs) {
				if(StringUtils.isEmpty(o)) continue;
				MemberBean mb = storeComponent.getMemberBeanFromMapDB(o);
				if(mb.getShimingStatus().intValue()==1) continue;
				MemberBean bean = new MemberBean();
				bean.setHeadpic(mb.getHeadpic());
				bean.setId(mb.getId());
				bean.setNickName(mb.getNickName());
				list.add(bean);
			}
		}

		ResponseUtils.json(response, 200,list , null);
	}

	@AuthPassport
	@RequestMapping(value = "/getMemberList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getMemberList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
		if (StringUtils.isEmpty(roomid)){
			ResponseUtils.json(response, 500, "参数错误", null);
			return;
		}
		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}
		List<MemberBean> list = new ArrayList<MemberBean>();
		List<Object> memberList=roomMemberService.getMemberByRoomId(roomid);
		memberList.forEach(member->{
			list.add(BeanUtils.memberToBeanSimple((Member)member));
		});
		roomBean.setMember_ids(roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid}));
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200,list , null);
	}

	@AuthPassport
	@RequestMapping(value = "/getMember",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getMember(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");

		if (StringUtils.isEmpty(roomid)){
			ResponseUtils.json(response, 500, "参数错误", null);
			return;
		}
		RoomBean roomBean = chatStoreComponent.getRoomBeanMap(roomid);

		if (null == roomBean) {
			ResponseUtils.json(response, 500, "此群已不存在", null);
			return;
		}
		String nickname = request.getParameter("nickname");
		if (StringUtils.isEmpty(nickname)){
			ResponseUtils.json(response, 500, "参数错误", null);
			return;
		}
		List<Member> members = memberService.getLike("nickName", nickname);
		if (members.isEmpty()){
			ResponseUtils.json(response, 500, "用户不存在", null);
			return;
		}
//		log.info("members:{}", JSONObject.toJSONString(members));
		List<Member> memberList = new ArrayList<>();
		for (Member member : members) {
			Long count = roomMemberService.count(new String[]{"member_id","room_id"}, new Object[]{member.getId(),roomid});
//			log.info("count:{},member:{}", count,JSONObject.toJSONString(member));
			if (count != 0){
				memberList.add(member);
			}
		}
		ResponseUtils.json(response, 200, memberList, null);
	}

	@AuthPassport
	@RequestMapping(value = "/getMemberListPage",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getMemberListPage(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");

		if (StringUtils.isEmpty(roomid)){
			ResponseUtils.json(response, 500, "参数错误", null);
			return;
		}

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

		if(null==roomBean) {
			ResponseUtils.json(response, 500,"此群已不存在" , null);
			return;
		}

		int maxLen = 30;
		String pageSizeString = request.getParameter("pageSize");
		if (StringUtils.isNotEmpty(pageSizeString)){
			maxLen = Integer.parseInt(pageSizeString);
		}
		int pageNumber = 1;
		String pageNumberString = request.getParameter("pageNumber");
//		log.info("============/listPage,getMemberListPage:{}",pageNumberString);
		if (StringUtils.isNotEmpty(pageNumberString)){
			pageNumber = Integer.parseInt(pageNumberString);
		}
		int start = (pageNumber -1) * maxLen;
		int end = pageNumber  * maxLen;
		int count = 0;
		int pageCount = 0;

		List<MemberBean> list = new ArrayList<MemberBean>();
		if(!StringUtils.isEmpty(roomBean.getMember_ids())) {
			String[] arrs = roomBean.getMember_ids().split("#");
			count = arrs.length;
			if (count % maxLen == 0) {
				pageCount = count/ maxLen;
			}else {
				pageCount = count/ maxLen + 1;
			}
			String[] arrsRange = Arrays.copyOfRange(arrs,start,end);
			for(String o : arrsRange) {
				if(StringUtils.isEmpty(o)) continue;
				MemberBean mb = storeComponent.getMemberBeanFromMapDB(o);
				if(null==mb) {
					roomBean.setMember_ids(roomBean.getMember_ids().replaceAll(o+"#", ""));
					roomMemberService.deleteByHql(new RoomMemberEntity(roomid,o));
					continue;
				}
				MemberBean bean = new MemberBean();
				bean.setHeadpic(mb.getHeadpic());
				bean.setId(mb.getId());
				bean.setNickName(mb.getNickName());
				list.add(bean);
			}
		}

		Pager<MemberBean> listPageBean = new Pager<>(pageNumber,maxLen,"createDate", OrderType.asc);
		listPageBean.setPageCount(pageCount);
		listPageBean.setTotalCount(count);
		listPageBean.setList(list);
		ResponseUtils.json(response, 200, listPageBean , null);
	}
	
	@AuthPassport
	@RequestMapping(value = "/load/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void load_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");

		RoomBean roomBean =chatStoreComponent.getRoomBeanMap(roomid);

        if(null==roomBean) {
			ResponseUtils.json(response, 200,"此群已不存在" , null);
			return;
		}

        if(roomBean.getStatus() ==1) {
            ResponseUtils.json(response, 503,"此群已被冻结" , null);
            return;
        }
		List<String> imgs = new ArrayList<String>();

		if(!StringUtils.isEmpty(roomBean.getMember_ids())) {
			String[] arrs = roomBean.getMember_ids().split("#");
			int i=0;
			for(String o : arrs) {
				if(StringUtils.isEmpty(o)) continue;
				if(i<5) {
					MemberBean mb = storeComponent.getMemberBeanFromMapDB(o);
					if(null==mb) {
						roomBean.setMember_ids(roomBean.getMember_ids().replaceAll(o+"#", ""));
						roomMemberService.deleteByHql(new RoomMemberEntity(roomid,o));
						continue;
					}
					imgs.add(mb.getHeadpic());
				} else {
					break;
				}
				 
				i++;
			} 
		}
		roomBean.setTop5Hp(imgs);
		if(StringUtils.isEmpty(roomBean.getOwnerName())) {
			MemberBean mb = storeComponent.getMemberBeanFromMapDB(roomBean.getOwner_UUID());
			roomBean.setOwnerName(mb.getNickName());
		}
		roomBean.setMember_ids(roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomid}));
		chatStoreComponent.putRoomBeanMap(roomid,roomBean);
		ResponseUtils.json(response, 200,roomBean , null);
	}

	@AuthPassport
	@RequestMapping(value = "/leave/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void leave_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String roomid = request.getParameter("roomid");
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		Map<String, MemberBean> map = chatStoreComponent.getGroupMemberMap(roomid);
		if(null!=map&&map.containsKey(MEMBERID)) {
			map.remove(MEMBERID);
			chatStoreComponent.putGroupMemberMap(roomid,map);
		}
//		ChatStore.USER_BEAN_MAP.get(MEMBERID).setInRoomid("");
		MemberBean memberBean=chatStoreComponent.getMemberBean(MEMBERID);
		chatStoreComponent.putMemberBean(MEMBERID,memberBean);
		ResponseUtils.json(response, 200,"" , null);
	}

	
	@AuthPassport
	@RequestMapping(value = "/createRoom",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void createRoom(HttpServletRequest request,HttpServletResponse response) throws Exception {

		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		Employee employee = employeeService.get("member_uuid",MEMBERID);
		FunctionConfigEntity fcEntity=functionConfigService.get();
		/**如果不是特权用户，且配置：禁止普通用户建群0否1是*/
		if (employee == null&&fcEntity.getCreate_room()==1) {
			ResponseUtils.json(response, 500,"禁止建立群组" , null);
			return;
		}

		Member member = memberService.get(MEMBERID);
		List<RoomMemberEntity> rmList=new ArrayList<>();

		String[] mids = request.getParameter("mids").split(",");
		if(ArrayUtils.isEmpty(mids)) {
			ResponseUtils.json(response, 500,"出错" , null);
			return;
		}
		
		WebConfig config = configService.get();
		if(mids.length>=config.getRoom_members_limit().intValue()) {
			ResponseUtils.json(response, 500,"超过群人数上限"+config.getRoom_members_limit().intValue() +"人", null);
			return;
		}
		
		Room room = new Room();
		
		String roomid = com.imservices.im.bmm.utils.NumberUtils.generateRandomNumber(config.getRoom_id_len());
		Long l = roomService.count(new String[] {"roomId"}, new Object[] {roomid});
		while(null!=l&&l>0) {
			roomid = com.imservices.im.bmm.utils.NumberUtils.generateRandomNumber(config.getRoom_id_len());
			l = roomService.count(new String[] {"roomId"}, new Object[] {roomid});
		}
		room.setRoomId(roomid);
		 
		room.setMember_ids(member.getId()+"#");
		boolean flag = false;
		for(String id : mids) {
			if(StringUtils.isEmpty(id)) continue;
			
			l = memberService.friendsCount(new String[]{"friendid","mid"}, new Object[]{MEMBERID,id});
			if(null==l||l<=0) {
				flag = true;
				continue;
			}
			
			Member m = memberService.get(id);
			room.setMember_ids(room.getMember_ids()+m.getId()+"#");
			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(m.getId());
			bean.setTxt("您被【"+member.getNickName()+"】邀请加入群组");
			userChatCmd.sendTXT(bean);

			RoomMemberEntity roomMemberEntity=new RoomMemberEntity();
			roomMemberEntity.setMember_id(m.getId());

			rmList.add(roomMemberEntity);
		}  
		if(flag) {
			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(MEMBERID);
			//bean.setTxt("您被【"+member.getNickName()+"】邀请加入【"+room.getName()+"】群组");
			bean.setTxt("创建群组成功，部分邀请成员已不再是你的好友，已被过滤掉。");
			userChatCmd.sendTXT(bean);
		} else {
			ChatTxtBean bean = new ChatTxtBean();
			bean.setFromUid("-1");
			bean.setToUid(MEMBERID);
			bean.setTxt("创建群组成功。");
			userChatCmd.sendTXT(bean);
		}


		room.setName("群聊");
		room.setCreateDate(new Date());
		room.setDescri("");
		room.setStatus(0);  

		room.setHeadImg("/img_sys/roomDefaulHeadPic.jpg");
        room.setOwner(member.getMemberId());
        room.setOwner_UUID(member.getId());
        room.setGameStatus(0);
        roomService.save(room);
        //管理员
		roomMemberService.save(new RoomMemberEntity(room.getId(),member.getId(),1));
		rmList.forEach(rm->{
			try {
				rm.setRoom_id(room.getId());
				rm.setIs_manager(0);
				roomMemberService.save(rm);
			}catch (Exception e){
				log.error("roomMemberService.save{}",e);
			}
		});
        chatStoreComponent.putRoomBeanMap(room.getId(), BeanUtils.roomToBeanSimple(room));


        AccessRecord ar = new AccessRecord();
        ar.setCreateDate(new Date());
        ar.setImg(room.getHeadImg());
        ar.setUid(MEMBERID);
        ar.setEntityid(room.getId());
        ar.setTypeid("1");
        ar.setTitle(room.getName());
        accessRecordService.save(ar);
        AccessRecordBean bean = BeanUtils.accessRecordToBean(ar);
        accessRecordCmd.insertOrUpdate(bean, MEMBERID, CMD_ENUM.AR_INSERT);
        for(String id : mids) {
            if(StringUtils.isEmpty(id)) continue;
            AccessRecord nar = new AccessRecord();
            nar.setCreateDate(ar.getCreateDate());
            nar.setImg(ar.getImg());
            nar.setUid(id);
            nar.setEntityid(ar.getEntityid());
            nar.setTypeid("1");
            nar.setTitle(ar.getTitle());
            accessRecordService.save(nar);
            bean = BeanUtils.accessRecordToBean(nar);
            accessRecordCmd.insertOrUpdate(bean, id, CMD_ENUM.AR_INSERT);
        }

        ResponseUtils.json(response, 200,bean , null);
    }

    @AuthPassport
    @RequestMapping(value = "/existByRoomid",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void existByRoomid(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String roomid = request.getParameter("roomid");
        Room room = roomService.get("roomId", roomid);
        if(null!=room) {
            ResponseUtils.json(response, 200,BeanUtils.roomToBeanSimple(room) , null);
        } else {
            ResponseUtils.json(response, 500,"" , null);
        }

    }


    @AuthPassport
    @RequestMapping(value = "/list/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void list_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String type = request.getParameter("type");
        List<Room> list = null;
        if(!StringUtils.isEmpty(type)) {
            list = roomService.findByPager(new String[]{"status","gameType","owner_UUID","independence"}, new Object[]{0,GameType.valueOf(type),null,0}, new Pager(1, 20, "gameType", OrderType.asc)).getList();
        } else {
            list = roomService.findByPager(new String[]{"status","owner_UUID","independence"}, new Object[]{0,null,0}, new Pager(1, 100, "gameType", OrderType.asc)).getList();
        }
//		if(!StringUtils.isEmpty(type)) {
//			list = roomService.findByPager(new String[]{"status","gameType","independence"}, new Object[]{0,GameType.valueOf(type),0}, new Pager(1, 20, "gameType", OrderType.asc)).getList();
//		} else {
//			list = roomService.findByPager(new String[]{"status","independence"}, new Object[]{0,0}, new Pager(1, 100, "gameType", OrderType.asc)).getList();
//		}

        List<RoomBean> blist = new ArrayList<RoomBean>();
        if(null!=list&&!list.isEmpty()) {
            for(Room room : list) {
                RoomBean b = new RoomBean();
                if(StringUtils.isEmpty(room.getHeadImg())) {
                    b.setImg("/img_sys/group.png");
                } else {
                    b.setImg(room.getHeadImg());
                }

                b.setName(room.getName());
                b.setRoomid(room.getRoomId());
                b.setRoomUUID(room.getId());
                b.setSubname(room.getSubname());
                b.setDescri(room.getDescri());
                blist.add(b);
            }
        }
        ResponseUtils.json(response, 200,blist , null);
    }


    @AuthPassport
    @RequestMapping(value = "/myRoom/list/v1",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void myRoomlist_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        List<Room> list = roomService.getList(new String[]{"owner_UUID","independence"}, new Object[]{MEMBERID,0});
        List<RoomBean> blist = new ArrayList<RoomBean>();
        if(null!=list&&!list.isEmpty()) {
            for(Room room : list) {
                RoomBean b = new RoomBean();
                if(StringUtils.isEmpty(room.getHeadImg())) {
                    b.setImg("/img_sys/group.png");
                } else {
                    b.setImg(room.getHeadImg());
                }

                b.setName(room.getName());
                b.setRoomid(room.getRoomId());
                b.setRoomUUID(room.getId());
                b.setSubname(room.getSubname());
                b.setDescri(room.getDescri());
                blist.add(b);
            }
        }
        ResponseUtils.json(response, 200,blist , null);
    }






    @ResponseBody
    @RequestMapping(value = "/list",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public String list(HttpServletRequest request) throws Exception {
        String gameType = request.getParameter("gameType");
        List<Room> list = roomService.getList(new String[]{"gameType","status"}, new Object[]{GameType.valueOf(gameType),0});
        List<RoomListRenderer> rl_list = new ArrayList<RoomListRenderer>();

        RoomListRenderer rl = new RoomListRenderer();
        rl.setGameType(gameType);
        int i=0;
        for(Room r : list) {
            if(i%3==0&&i!=0) {
                rl_list.add(rl);
                rl = new RoomListRenderer();
                rl.setGameType(gameType);
            }
            if(StringUtils.isEmpty(rl.getRoomIcons())) {
                rl.setRoomIcons(r.getHeadImg());
            } else {
                rl.setRoomIcons(rl.getRoomIcons()+","+r.getHeadImg());
            }
            if(StringUtils.isEmpty(rl.getRoomNames())) {
                rl.setRoomNames(r.getName());
            } else {
                rl.setRoomNames(rl.getRoomNames()+","+r.getName());
            }

            if(StringUtils.isEmpty(rl.getRoomIds())) {
                rl.setRoomIds(r.getId());
            } else {
                rl.setRoomIds(rl.getRoomIds()+","+r.getId());
            }

            i++;
        }
        if(!rl_list.contains(rl)) rl_list.add(rl);
        return JsonUtil.getJSONString(rl_list);

    }

}
