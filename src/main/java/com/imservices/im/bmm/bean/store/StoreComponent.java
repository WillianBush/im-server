package com.imservices.im.bmm.bean.store;

import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.entity.Blacklist;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class StoreComponent {


    protected MemberService memberService;
	private RedisService redisService;
	private ChatStoreComponent chatStoreComponent;
	
	
	public  String getBlackList(String uid) throws Exception {
		if(ChatStore.BLACK_LIST.containsKey(uid)) {
			return ChatStore.BLACK_LIST.get(uid);
		} 
		List<Blacklist> list = memberService.getBlackList(new String[]{"mid"}, new Object[]{uid});
		if(!list.isEmpty()) {
			Blacklist o = list.get(0);
			ChatStore.BLACK_LIST.put(uid, o.getBlacklist_ids());
			return o.getBlacklist_ids();
		} else {
			ChatStore.BLACK_LIST.put(uid, "");
			return "";  
		}
		
	}
	
	public MemberBean getMemberBeanFromMapDB(String uuid) throws Exception {
		if(StringUtils.isEmpty(uuid)){
			return null;
		}
		MemberBean memberBean=chatStoreComponent.getMemberBean(uuid);
		MemberBean robotBean=chatStoreComponent.getRobotBeanMap(uuid);
		MemberBean freeRobotBean=chatStoreComponent.getFreeRobotBeanMap(uuid);
		if(!ObjectUtils.isEmpty(memberBean)) {
			return memberBean;
		} else if(!ObjectUtils.isEmpty(robotBean)) {
			return robotBean;
		} else if(!ObjectUtils.isEmpty(freeRobotBean)) {
			return freeRobotBean;
		}  
		//
		MemberBean mb = null;
		try {
			mb = BeanUtils.memberToBeanSimple(memberService.get(uuid));
		} catch (Exception e) {
			log.error("",e);
			return null;
		}
		if(null==mb) return null;
		chatStoreComponent.putMemberBean(mb.getId(), mb);
		return mb;
	}
	
	
	public MemberBean getMemberBeanFromMap(String uuid){
		MemberBean memberBean = chatStoreComponent.getMemberBean(uuid);
		MemberBean robotBean = chatStoreComponent.getRobotBeanMap(uuid);
		MemberBean freeRobotBean = chatStoreComponent.getFreeRobotBeanMap(uuid);
		if (!ObjectUtils.isEmpty(memberBean)) {
			return memberBean;
		} else if (!ObjectUtils.isEmpty(robotBean)) {
			return robotBean;
		} else if (!ObjectUtils.isEmpty(freeRobotBean)) {
			return freeRobotBean;
		}
		return null;
	}


	public void updateMemberBean(MemberBean mb) {
		MemberBean memberBean = chatStoreComponent.getMemberBean(mb.getId());
		MemberBean robotBean = chatStoreComponent.getRobotBeanMap(mb.getId());
		MemberBean freeRobotBean = chatStoreComponent.getFreeRobotBeanMap(mb.getId());
		if (!ObjectUtils.isEmpty(memberBean)) {
			chatStoreComponent.putMemberBean(mb.getId(),mb);
		}
		if (!ObjectUtils.isEmpty(robotBean)) {
			chatStoreComponent.putRobotBeanMap(mb.getId(),mb);
		}
		if (!ObjectUtils.isEmpty(freeRobotBean)) {
			chatStoreComponent.putFreeRobotBeanMap(mb.getId(),mb);
		} 
	}

	/**
	 * 判断集群中用户是否在线
	 * @param userId
	 * @return
	 */
	public boolean isMemberOnline(String userId){
		//从redis缓存中取出来，如果存在，则判断所在服务器
		Object ipObj= redisService.hGet(SessionStore.REDIS_WSS_KEY,userId);
		if(ipObj!=null){
			return true;
		}
		return false;
	}
	 
}
