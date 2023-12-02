package com.imservices.im.bmm.schedule;

import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.entity.Room;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class RedisClearSchedule {

    private RoomService roomService;
    private RedisService redisService;
    private ChatStoreComponent chatStoreComponent;

    @Scheduled(cron = "0 0 2 * * ?")   //定时器定义，设置执行时间：每天凌晨2:00
    private void process() {
        try {
//            redisService.deleteAll();
//            //把群房间列表存入缓存 中
//            List<Room> room_list = roomService.getAll();
//            for (Room room : room_list) {
//                RoomBean roombean = BeanUtils.roomToBeanSimple(room);
//                chatStoreComponent.putRoomBeanMap(room.getId(), roombean);
//            }
        } catch (Exception e) {
            log.error("RedisClearSchedule:{}", e);
        }
    }
}
