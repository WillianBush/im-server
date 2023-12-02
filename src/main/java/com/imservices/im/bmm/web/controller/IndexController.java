package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.entity.Room;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller("IndexController")
@RequestMapping(value = "")
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class IndexController {



	private RoomService roomService;

	private RedisService redisService;
	
//	@RequestMapping(value={"/tt"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//	public void tt(HttpServletRequest request, HttpServletResponse response) {
//		try {
////			configService.get();
////			redisService.set("t", "你好啊。测试一下了");
////			String t = redisService.get("t");
////
//			//uniPushService.test();
//			redisService.lLeftPush("abas", "1");
//			redisService.lLeftPush("abas", "2");
//			redisService.lLeftPush("abas", "3");
//			redisService.lLeftPush("abas", "4");
//
//			List<String> list = redisService.lRange("abas", 0, 2);
////			for(String o : list) {
////
////			}
//			redisService.delete("abas");
//			list = redisService.lRange("abas", 0, 2);
//
////			for(String o : list) {
////
////			}
//
//
//		} catch (Exception e) {
//			log.error("",e);
//		}
//	}
	
	@RequestMapping(value={"/index/getSessionid"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getSessionid(HttpServletRequest request, HttpServletResponse response) {
		try {
			ResponseUtils.json(response, 200, request.getSession().getId(), null);
		} catch (Exception e) {
			log.error("",e);
		}
	}   
	
	
//	@RequestMapping(value={"/test1"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
//	public void test1(HttpServletRequest request, HttpServletResponse response) {
//		String t = request.getParameter("t");
//		//
//	}
	
	
	//获取当次进入过的独立房间UUID
	@RequestMapping(value={"/index/getJoinedIndependenceRoomUUID"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getJoinedIndependenceRoomUUID(HttpServletRequest request, HttpServletResponse response) {
		try {
			Object independenceRoomUUID = request.getSession().getAttribute("IndependenceRoomUUID");
			if(null!=independenceRoomUUID&&!StringUtils.isEmpty(independenceRoomUUID.toString())) {
				RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(independenceRoomUUID.toString());
				if(rb.getIndependence()==1) {  
					ResponseUtils.json(response, 200, "/#/group/chat/"+independenceRoomUUID, null);
				} else {
					ResponseUtils.json(response, 200, "", null);
				}
			} else {
				ResponseUtils.json(response, 200, "", null);
			}
		} catch (Exception e) {
			log.error("",e);
		}
	}
	
	//独立房间
	@RequestMapping(value={"/room/{roomid}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
	public void goIndependenceRoom(@PathVariable String roomid,HttpServletRequest request, HttpServletResponse response) {
		try {
			if(StringUtils.isEmpty(roomid)) return;
			Room room = roomService.get("roomId", roomid);
			if (null == room){
				return;
			}
			if(room.getIndependence()==1) { 
				request.getSession().setAttribute("IndependenceRoomUUID", room.getId());//用于作为已进入独立房间的标记
			}
			response.sendRedirect("/#/group/chat/"+room.getId());
		} catch (Exception e) { 
			log.error("",e);
		}
	}
	
	
	
	
	
	
	/**
	@RequestMapping(value={"/test"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void test(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ResponseUtils.json(response, 200, "1111", null);
	}
	**/

}
