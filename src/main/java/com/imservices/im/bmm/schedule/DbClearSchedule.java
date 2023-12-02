package com.imservices.im.bmm.schedule;

import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class DbClearSchedule {

    private MemberService memberService;

    @Scheduled(cron = "0 0 2 * * ?")   //定时器定义，设置执行时间：每天凌晨2:00
    private void process() {
        try {
//            memberService.clearMember();
        } catch (Exception e) {
            log.error("DbClearSchedule:{}", e);
        }
    }
}
