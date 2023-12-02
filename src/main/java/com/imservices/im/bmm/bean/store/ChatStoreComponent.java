package com.imservices.im.bmm.bean.store;

import com.alibaba.fastjson.JSON;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.entity.Room;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.RoomMemberService;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ChatStoreComponent {

	private final RedisService redisService;

	private final RoomService roomService;

	private final MemberService memberService;

	private final RoomMemberService roomMemberService;

	@Autowired
	public ChatStoreComponent(RedisService redisService, RoomService roomService,MemberService memberService, RoomMemberService roomMemberService) {
		this.redisService = redisService;
		this.roomService = roomService;
		this.memberService = memberService;
		this.roomMemberService=roomMemberService;
	}

	/**
	 * 群用户【《群ID，《用户ID，用户bean》》】这样设计的目的在于快速查找具体用户清除等操作也便于群发信息
	 */
	private static final String REDIS_KEY_GROUP_MEMBER_MAP="GROUP_MEMBER_MAP";
	/**
	 * 根据房间ID保存 roomBean  Map<String, RoomBean>
	 */
	private static final String REDIS_KEY_ROOMB_BEAN_MAP="ROOMB_BEAN_MAP";
	/**
	 * 根据用户ID保存  memberBean【平台登陆的玩家】
	 */
	private static final String REDIS_KEY_USER_BEAN_MAP="USER_BEAN_MAP";
	/**
	 * 根根据用户ID保存  memberBean【已分配的机器人】Map<String, MemberBean>
	 */
	private static final String REDIS_KEY_ROBOT_BEAN_MAP="ROBOT_BEAN_MAP";
	/**
	 * 根据用户ID保存  memberBean【未分配的机器人】Map<String, MemberBean>
	 */
	private static final String REDIS_KEY_FREE_ROBOT_BEAN_MAP="FREE_ROBOT_BEAN_MAP";

	public Map getGroupMemberMap(String roomId){
		Object obj= redisService.hGet(REDIS_KEY_GROUP_MEMBER_MAP,roomId);
		if(obj!=null){
			return JSON.parseObject(obj.toString());
		}
		return null;
	}

	public void putGroupMemberMap(String roomId,Map<String, MemberBean> map){
		redisService.hPut(REDIS_KEY_GROUP_MEMBER_MAP,roomId,JSON.toJSONString(map));

	}

	public RoomBean getRoomBeanMap(String roomId){
		Object obj= redisService.hGet(REDIS_KEY_ROOMB_BEAN_MAP,roomId);
//		log.error("getRoomBeanMap:{}",obj);
		if(!ObjectUtils.isEmpty(obj)){
			return JSON.parseObject(obj.toString(),RoomBean.class);
		}else {
			Room room = null;
			try {
				room = roomService.get(roomId);
			} catch (Exception e) {
				log.error("getRoomBeanMap",e);
				return  null;
			}
			RoomBean roomBean = BeanUtils.roomToBeanSimple(room);
			if (roomBean != null) {
				this.putRoomBeanMap(roomId,roomBean);
			}
			return roomBean;
		}
	}

	public void putRoomBeanMap(String roomId,RoomBean map){
		try {
			/**设置群成员ID字符串*/
			map.setMember_ids(roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{roomId}));
			/**设置所有群管理**/
			map.setMemberMgr_ids(roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id","is_manager"}, new Object[]{roomId,1}));
			redisService.hPut(REDIS_KEY_ROOMB_BEAN_MAP,roomId,JSON.toJSONString(map));
		}catch (Exception e){
			log.error("putRoomBeanMap:{}",e);
		}

	}

	/**
	 * 删除
	 * @param roomId
	 */
	public void delRoomBeanMap(String roomId){
		redisService.hDelete(REDIS_KEY_ROOMB_BEAN_MAP,roomId);
	}

	public MemberBean getMemberBean(String memberId) {
		Object obj = redisService.hGet(REDIS_KEY_USER_BEAN_MAP, memberId);//因为是自己更新的头像。所以缓存 对象肯定存在。这里不必要判断缓存对像存在与否
		if (!ObjectUtils.isEmpty(obj)) {
			return JSON.parseObject(obj.toString(), MemberBean.class);
		} else {
			Member member = null;
			try {
				member = memberService.get(memberId);
			} catch (Exception e) {
				log.error("getMemberBean", e);
				return null;
			}
			MemberBean mb = BeanUtils.memberToBeanSimple(member);
			redisService.hPut(REDIS_KEY_USER_BEAN_MAP, memberId, JSON.toJSONString(mb));
			return mb;
		}
	}

	public void putMemberBean(String memberId,MemberBean memberBean){
		redisService.hPut(REDIS_KEY_USER_BEAN_MAP,memberId,JSON.toJSONString(memberBean));
	}

	public void delMemberBean(String memberId){
		redisService.hDelete(REDIS_KEY_USER_BEAN_MAP,memberId);
	}

	public HashMap getMemberAll(){
		HashMap rs=new HashMap();
		Map userMap=redisService.hGetAll(REDIS_KEY_USER_BEAN_MAP);
		userMap.forEach((k,v)->rs.put(k,JSON.parseObject(v.toString(),MemberBean.class)));
		return rs;
	}

	public MemberBean getRobotBeanMap(String id){
		Object obj= redisService.hGet(REDIS_KEY_ROBOT_BEAN_MAP,id);
		if(obj!=null){
			return JSON.parseObject(obj.toString(),MemberBean.class);
		}
		return null;
	}

	public void putRobotBeanMap(String roomId,MemberBean map){
		redisService.hPut(REDIS_KEY_ROBOT_BEAN_MAP,roomId,JSON.toJSONString(map));

	}

	public MemberBean getFreeRobotBeanMap(String id){
		Object obj= redisService.hGet(REDIS_KEY_FREE_ROBOT_BEAN_MAP,id);
		if(obj!=null){
			return JSON.parseObject(obj.toString(),MemberBean.class);
		}
		return null;
	}

	public void putFreeRobotBeanMap(String roomId,MemberBean map){
		redisService.hPut(REDIS_KEY_FREE_ROBOT_BEAN_MAP,roomId,JSON.toJSONString(map));

	}

}
 