package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.entity.MsgHistoryEntity;
import com.imservices.im.bmm.entity.WaitSendMessage;
import com.imservices.im.bmm.entity.WaitUpdateRed;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@SuppressWarnings("all")
@Lazy
public class ChatServiceImpl implements ChatService {

    @Autowired
    private BaseService baseService;
    @Autowired
    private SendUtils sendUtils;

    @Override
    public List<WaitSendMessage> getWSMList(String[] ps, Object[] vs) throws Exception {
        return baseService.getList(WaitSendMessage.class, ps, vs, "createDate", OrderType.asc);
    }

    @Override
    @Transactional
    public void deleteWSM(WaitSendMessage o) throws Exception {
        baseService.delete(o);
    }

    @Override
    @Transactional
    public void deleteWSM(String[] ids) throws Exception {
        baseService.delete(WaitSendMessage.class, ids);
    }

    @Override
    @Transactional
    public void deleteWSM(String id) throws Exception {
        baseService.delete(WaitSendMessage.class, id);
    }

    @Override
    public void deleteByToUid(String toUid) throws Exception {
        baseService.delete("delete from waitsendmessage where toUid ='"+toUid+"'");
    }

    @Override
    @Transactional
    public void saveWSM(WaitSendMessage o) throws Exception {
        baseService.save(o);
    }

    @Transactional
    @Override
    public void deleteWSMbyHql(String hql) throws Exception {
        baseService.delete(hql);
    }

    @Transactional
    @Override
    public void saveWUR(WaitUpdateRed o) throws Exception {
        baseService.save(o);
    }

    @Transactional
    @Override
    public Long wsmCount(String[] ps, Object[] vs) throws Exception {
        return baseService.getTotalCount(WaitSendMessage.class, ps, vs);
    }

    @Transactional
    @Override
    public void saveMsgHistory(MsgHistoryEntity msgHistoryEntity) throws Exception {
        baseService.save(msgHistoryEntity);
    }

    @Override
    public Pager<MsgHistoryEntity> getMsgHistoryPageList(String forMemberId, String toMmeberId, Pager pager) throws Exception {
        return baseService.findByPager(MsgHistoryEntity.class, " from MsgHistoryEntity  m where (fromUid='" + forMemberId + "' and toUid='" + toMmeberId + "') or (fromUid='" + toMmeberId + "' and toUid='" + forMemberId + "')", pager);
    }


}
