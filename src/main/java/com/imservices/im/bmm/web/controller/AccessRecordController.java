package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.AccessRecordBean;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.AccessRecord;
import com.imservices.im.bmm.entity.IndependentRoomUserPrice;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.AccessRecordService;
import com.imservices.im.bmm.service.IRUPService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.cmd.AccessRecordCmd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/user/accessRecord/json")
@CrossOrigin
@Slf4j
public class AccessRecordController {
	
	@Autowired
	private AccessRecordService accessRecordService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private IRUPService iRUPService;
	@Autowired
	private StoreComponent storeComponent;
	@Autowired
	private AccessRecordCmd accessRecordCmd;
	@Autowired
	private ChatStoreComponent chatStoreComponent;

	
	
	
	@AuthPassport
	@RequestMapping(value = "/remove",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void remove(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		AccessRecord ar = accessRecordService.get(id);
		if(!ar.getUid().equals(MEMBERID)) {
			return;
		}
		accessRecordService.delete(ar);;
		ResponseUtils.json(response,200, null, null);
		
	}
	
	
	  
	@AuthPassport
	@RequestMapping(value = "/saveOrUpdate",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void saveOrUpdate(HttpServletRequest request,HttpServletResponse response)  {
		try {
			String type = request.getParameter("type");//1房间 2用户
			String eid = request.getParameter("eid");
			String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
			Member member = memberService.get(MEMBERID);
			if(null==member){
				ResponseUtils.json(response, 500, "用户不存在", null);
				return;
			}
			
			if(!ChatStore.USER_BEAN_MAP.containsKey(MEMBERID)) {
				ChatStore.USER_BEAN_MAP.put(MEMBERID, BeanUtils.memberToBeanSimple(member));
			}
			//
			//如果是群房间，则判断此用户是否游戏中。
//		if("1".equals(type)) {
//			if(GameStore.inTheGame(MEMBERID)) {
//				ResponseUtils.json(response, 500, "请先退出正在游戏的群，再尝试进入！", null);
//				return;
//			}
//		}
			
			
			List<AccessRecord> list = accessRecordService.getList(new String[]{"uid","entityid"}, new Object[]{MEMBERID,eid});
			AccessRecordBean bean = null;
			AccessRecord ar = null;
			if(null!=list&&!list.isEmpty()) {
				//
				if("1".equals(type)) {
					//
					//群
//					RoomBean e = ChatStore.ROOMB_BEAN_MAP.get(eid);
					RoomBean e = chatStoreComponent.getRoomBeanMap(eid);
					if(null==e) {
						ResponseUtils.json(response, 500, "房间不存在", null);//房间不存在
						return;
					} 
					if(null!=e.getEndDate()) {
						if(e.getEndDate().getTime()<=new Date().getTime()) {
							if(!e.getOwner_UUID().equals(MEMBERID)) {
								ResponseUtils.json(response, 500, "房间已到期", null);
								return; 
							}
							
						}
					}
					if(e.getIndependence()==1) {
						//
						//如果是独立房间
						List<IndependentRoomUserPrice> irupList = iRUPService.getList(new String[]{"room_uuid","user_uuid" }, new Object[] {e.getRoomUUID(),member.getId()});
						IndependentRoomUserPrice irup = null;
						if(irupList.isEmpty()) {
							irup = new IndependentRoomUserPrice();
							irup.setPrice(0.0);
							irup.setRoom_id(e.getRoomid());
							irup.setRoom_uuid(e.getRoomUUID());
							irup.setUser_id(member.getMemberId());
							irup.setUser_uuid(member.getId());
							iRUPService.save(irup);
						} else {
							irup = irupList.get(0);
						}
						MemberBean mb = ChatStore.USER_BEAN_MAP.get(member.getId());
						mb.getIrupMap().put(e.getRoomUUID(), irup.getPrice());
						request.getSession().setAttribute("IndependenceRoomUUID", e.getRoomUUID());//用于作为已进入独立房间
						
					}
//					MemberBean memberBean = ChatStore.USER_BEAN_MAP.get(MEMBERID);
					ChatStore.USER_BEAN_MAP.get(MEMBERID).setInRoomid(e.getRoomid());
				}
				ar = list.get(0);
				ar.setCreateDate(new Date());
				accessRecordService.update(ar);
				bean = BeanUtils.accessRecordToBean(ar);
				bean.setImg(bean.getImg());
				//
				accessRecordCmd.insertOrUpdate(bean, MEMBERID, CMD_ENUM.AR_UPDATE);
			} else {
				//
				ar = new AccessRecord();
				ar.setCreateDate(new Date());
				ar.setUid(MEMBERID);
				ar.setEntityid(eid);
				ar.setTypeid(type);
				if("1".equals(type)) {
					//
					//群
					//Room e = roomService.get(eid);
//					RoomBean e = ChatStore.ROOMB_BEAN_MAP.get(eid);
					RoomBean e = chatStoreComponent.getRoomBeanMap(eid);
					if(null==e) {
						ResponseUtils.json(response, 500, "房间不存在", null);//房间不存在
						return;
					}
					if(null!=e.getEndDate()) {
						if(e.getEndDate().getTime()<=new Date().getTime()) {
							if(!e.getOwner_UUID().equals(MEMBERID)) {
								ResponseUtils.json(response, 500, "房间已到期", null);
								return;
							} 
							
						}
					} 
					if(e.getIndependence()==1) {
						//
						//如果是独立房间
						List<IndependentRoomUserPrice> irupList = iRUPService.getList(new String[]{"room_uuid","user_uuid" }, new Object[] {e.getRoomUUID(),member.getId()});
						IndependentRoomUserPrice irup = null;
						if(irupList.isEmpty()) {
							irup = new IndependentRoomUserPrice();
							irup.setPrice(0.0);
							irup.setRoom_id(e.getRoomid());
							irup.setRoom_uuid(e.getRoomUUID());
							irup.setUser_id(member.getMemberId());
							irup.setUser_uuid(member.getId());
							iRUPService.save(irup);
						} else {
							irup = irupList.get(0);
						}
						MemberBean mb = ChatStore.USER_BEAN_MAP.get(member.getId());
						mb.getIrupMap().put(e.getRoomUUID(), irup.getPrice());
						request.getSession().setAttribute("IndependenceRoomUUID", e.getRoomUUID());//用于作为已进入独立房间
					}
					
					if(StringUtils.isEmpty(e.getImg())) {
						ar.setImg("/img_sys/group.png");
					} else {
						ar.setImg(e.getImg());
					}
					ar.setTitle(e.getName());
					ar.setSubname(e.getSubname());
					ChatStore.USER_BEAN_MAP.get(MEMBERID).setInRoomid(e.getRoomid());
				} else if("2".equals(type)) { 
					//
					//用户
					Member e = memberService.get(eid);
					if(null==e) {
						ResponseUtils.json(response, 500, "用户不存在", null);//房间不存在
						return;
					}
					if(StringUtils.isEmpty(e.getHeadpic())) {
						ar.setImg("/img_sys/defaultHeadPic.jpg");
					} else {
						ar.setImg(e.getHeadpic());
					}
					ar.setTitle(e.getNickName());
					ar.setSubname("");
				}
				
				//
				accessRecordService.save(ar);
				bean = BeanUtils.accessRecordToBean(ar);
				accessRecordCmd.insertOrUpdate(bean, MEMBERID, CMD_ENUM.AR_INSERT);
			}
			if("1".equals(type)) {
				//
				//Room room = roomService.get(ar.getEntityid());
//				RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(ar.getEntityid());
				RoomBean room = chatStoreComponent.getRoomBeanMap(eid);
				//Map prosMap = JsonUtil.getMapFromJson(room.getProps());
//				Map<String, MemberBean> map = ChatStore.GROUP_MEMBER_MAP.get(ar.getEntityid());
				Map<String, MemberBean> map = chatStoreComponent.getGroupMemberMap(ar.getEntityid());

				if(null==map) {
					 map = new HashMap<String, MemberBean>();
					 //
//					 ChatStore.GROUP_MEMBER_MAP.put(ar.getEntityid(), map);
					//存入redis
					chatStoreComponent.putGroupMemberMap(ar.getEntityid(), map);
				}
				if(!map.containsKey(MEMBERID)) {
					if(null!=room.getLimitNum()&&room.getLimitNum()<=map.size()) {
						ResponseUtils.json(response, 500, "房间人数已满", null);
						return;
					}
					map.put(MEMBERID, BeanUtils.memberToBeanSimple(member));//将此用户列入此群用户中

				} 
//				Map<String,MemberBean> map1 = ChatStore.GROUP_MEMBER_MAP.get(ar.getEntityid());
			}
//		//
			//
			if(null!=bean) ResponseUtils.json(response, 200, bean, null);
		} catch (Exception e) {
			e.printStackTrace();  
		}
	}
	
	@AuthPassport
	@RequestMapping(value = "/list",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String type = request.getParameter("type");
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		List<AccessRecord> list = null;
		if(StringUtils.isEmpty(type)) {
			list = accessRecordService.getList(new String[]{"uid"}, new Object[]{MEMBERID});
		} else {
			list = accessRecordService.getList(new String[]{"uid","typeid"}, new Object[]{MEMBERID,type});
		}
		List<AccessRecordBean> blist = new ArrayList<AccessRecordBean>();
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yy/M/dd");
		Date now = new Date();
		Date yestoday = new Date();
		yestoday.setDate(yestoday.getDate()-1);

		int maxLen = 30;
		int i = 0;



		for(AccessRecord e : list) {
			i++;
			if(i>maxLen) {
				//accessRecordService.delete(e.getId()); 这里最好不要删除 。用于独立房间，房主查询在他的房间玩过游戏的玩家记录
				continue;
			}

			AccessRecordBean accessRecordBean =	accessRecordService.getAccessRecordBean(e);
			if (accessRecordBean !=null){
				blist.add(accessRecordBean);
			}

		}
		ResponseUtils.json(response,200, blist, null);

	}

	@AuthPassport
	@RequestMapping(value = "/get",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void get(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String nickname = request.getParameter("nickname");
		List<AccessRecord> accessRecords = accessRecordService.getList(new String[]{"uid","title"}, new Object[]{MEMBERID,nickname});
		List<AccessRecordBean> blist = new ArrayList<>();
		for (AccessRecord accessRecord: accessRecords) {
			AccessRecordBean accessRecordBean =	accessRecordService.getAccessRecordBean(accessRecord);
			if (accessRecordBean !=null){
				blist.add(accessRecordBean);
			}
		}
		ResponseUtils.json(response,200, blist, null);
	}


	@AuthPassport
	@RequestMapping(value = "/listPage",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void listPage(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String type = request.getParameter("type");
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		int maxLen = 30;
		String pageSizeString = request.getParameter("pageSize");
		if (StringUtils.isNotEmpty(pageSizeString)){
			maxLen = Integer.parseInt(pageSizeString);
		}
		int pageNumber = 1;
		String pageNumberString = request.getParameter("pageNumber");
//		log.info("============/listPage,pageNumberString:{}",pageNumberString);
		if (StringUtils.isNotEmpty(pageNumberString)){
			pageNumber = Integer.parseInt(pageNumberString);
		}

		Pager<AccessRecord> pager = new Pager<>(pageNumber,maxLen,"createDate", Pager.OrderType.desc);

		Pager<AccessRecord> listPage = null;
		if(StringUtils.isEmpty(type)) {
			listPage = accessRecordService.findByPager(new String[]{"uid"}, new Object[]{MEMBERID},pager);
		} else {
			listPage = accessRecordService.findByPager(new String[]{"uid","typeid"}, new Object[]{MEMBERID,type},pager);
		}

		Pager<AccessRecordBean> listPageBean = new Pager<>(pageNumber,maxLen,"createDate", Pager.OrderType.desc);
		pager.setPageCount(listPage.getPageCount());
		pager.setTotalCount(listPage.getTotalCount());


		List<AccessRecordBean> blist = new ArrayList<>();

		int i = 0;

		for(AccessRecord e : listPage.getList()) {
			i++;
			if(i>maxLen) {
				continue;
			}
			AccessRecordBean accessRecordBean =	accessRecordService.getAccessRecordBean(e);
			if (accessRecordBean !=null){
				blist.add(accessRecordBean);
			}
		}
		listPageBean.setList(blist);
		ResponseUtils.json(response,200, listPageBean, null);

	}
}
