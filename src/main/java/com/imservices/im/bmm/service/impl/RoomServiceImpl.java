package com.imservices.im.bmm.service.impl;

import com.alibaba.fastjson.JSON;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.entity.Room;
import com.imservices.im.bmm.entity.RoomAdd;
import com.imservices.im.bmm.mq.RabbitmqConfig;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@SuppressWarnings("all")
public class RoomServiceImpl implements RoomService {

	@Autowired
	private BaseService baseService;
	@Autowired
	private SendUtils sendUtils;
	@Autowired
	private RedisService redisService;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Override  
	@Transactional(readOnly = true)
	public Room get(String id) throws Exception {
		return (Room) baseService.get(Room.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public Room get(String propertyName, Object value) throws Exception {
		return (Room) baseService.get(Room.class,propertyName ,value);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Room> getList(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getList(Room.class, properties, vals);
	}

	@Override
	@Transactional
	public void update(Room room) throws Exception  {
		baseService.update(room);
	}

	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Room.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Room.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public Long count(String[] properties, Object[] values) throws Exception {
		return baseService.getTotalCount(Room.class, properties, values);
	}
 

	@Override
	@Transactional
	public void save(Room room) throws Exception {
		baseService.save(room); 
	}

	@Override
	@Transactional
	public void delete(Room room) throws Exception {
		baseService.delete(room);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Room> getAll() throws Exception {
		return baseService.getAll(Room.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getTotalCount(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getTotalCount(Room.class, properties, vals);
	}  


//	@Override
//	@Transactional
//	public void update(String[] ps, Object[] vs, String where) throws Exception {
//		baseService.update(Room.class, ps, vs, where);
//	}

	@Override
	@Transactional
	public Long roomAddCount(String[] ps, Object[] vs) throws Exception {
		return baseService.getTotalCount(RoomAdd.class, ps, vs);
	}

	@Transactional
	@Override
	public void saveRoomAdd(RoomAdd room_add) throws Exception {
		baseService.save(room_add);
	}

	@Override
	@Transactional
	public List<RoomAdd> roomAddList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(RoomAdd.class, ps, vs, "createDate", OrderType.desc);
	}

	@Override
	@Transactional
	public RoomAdd getRoomAdd(String raid) throws Exception {
		return (RoomAdd) baseService.get(RoomAdd.class, raid);
	}

	@Transactional
	@Override
	public void deleteRoomAdd(RoomAdd ra) throws Exception {
		baseService.delete(ra);
	}

	@Override
	public void sendRemoveMsg(String roomid, String mid) {
		try {
			List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(mid);
			Message message = new Message();
			message.setBody(roomid);
			message.setCMD(Message.CMD_ENUM.GROUP_MEMBER_REMOVE.name());
			if (ws_list.isEmpty()) {
				Object ipObj = redisService.hGet(SessionStore.REDIS_WSS_KEY, mid);
				if (ipObj != null) {
					Map para=new HashMap();
					para.put("id",mid);
					para.put("message",message);
					rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
							RabbitmqConfig.USER_ROUTING_KEY + "." + ipObj.toString(), JSON.toJSONString(para));
				}
			} else {
				for (WebSocketSession ws : ws_list) {
					sendUtils.send(ws, message);
				}
			}
		} catch (Exception e) {
			log.error("sendRemoveMsg", e);
		}
	}
}
