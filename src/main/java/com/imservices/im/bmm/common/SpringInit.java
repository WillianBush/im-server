package com.imservices.im.bmm.common;

import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.chat.ChatConfig;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.entity.Member.MEMBER_TYPE;
import com.imservices.im.bmm.entity.Room;
import com.imservices.im.bmm.entity.WebConfig;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
@Slf4j
@AllArgsConstructor
public class SpringInit {
	

	private MemberService memberService;

	private RoomService roomService;

	private WebConfigService configService;

	private ChatStoreComponent chatStoreComponent;

//	@Autowired
//	private RedisService redisService;

	@PostConstruct
	public void initMethod2() throws Exception {
//		String memberId=redisService.get(MemberConstant.REDIS_AUTO_CREATE_MEMBERID);
//		/**清理缓存*/
//		redisService.deleteAll();
//		if(StringUtils.isEmpty(memberId)) {
//			redisService.set(MemberConstant.REDIS_AUTO_CREATE_MEMBERID, (System.nanoTime()+"").substring(0,7));
//		}else{
//			redisService.set(MemberConstant.REDIS_AUTO_CREATE_MEMBERID, memberId);
//		}
		log.info("开始初始化数据加载......");
		//初始化一些重要配置，其它地方调用时也不会频繁调用数据库，引起的资源浪费
		WebConfig wc = configService.get();
		if (wc != null) {
			ChatConfig.cloudGroupChatDataKeepDays = wc.getCloudGroupChatDataKeepDays();
			ChatConfig.cloudUserChatDataKeepDays = wc.getCloudUserChatDataKeepDays();
		}

		//把空闲机器人存入缓存中
		List<Member> mlist = memberService.getList(new String[]{"memberType"}, new Object[]{MEMBER_TYPE.ROBOT});
		if(null!=mlist&&!mlist.isEmpty()) {
			for(Member m : mlist) {
				chatStoreComponent.putFreeRobotBeanMap(m.getId(), BeanUtils.memberToBeanSimple(m));
			}
		}

		Member m1 = memberService.get("0");//系统
		Member m2 = memberService.get("-1");//官方团队
		chatStoreComponent.putMemberBean(m1.getId(), BeanUtils.memberToBeanSimple(m1));
		chatStoreComponent.putMemberBean(m2.getId(), BeanUtils.memberToBeanSimple(m2));


		//把群房间列表存入缓存 中
		List<Room> room_list = roomService.getAll();
		for(Room room : room_list) {
			RoomBean roombean = BeanUtils.roomToBeanSimple(room);
			chatStoreComponent.putRoomBeanMap(room.getId(), roombean);
		}
		log.info("初始化数据加载完成......");
	}
}
