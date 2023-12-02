package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.entity.RoomMemberEntity;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.RoomMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoomMemberServiceImpl implements RoomMemberService {
    @Resource
    private BaseService<RoomMemberEntity, String> baseService;
    @Override
    public void save(RoomMemberEntity room) throws Exception {
        baseService.save(room);
    }

    @Override
    public void deleteByHql(RoomMemberEntity room) throws Exception {
        baseService.delete("delete from RoomMemberEntity where room_id='"+room.getRoom_id()+"' and member_id='"+room.getMember_id()+"'");
    }

    @Override
    public void deleteByRoomIdHql(String roomId) throws Exception {
        baseService.delete("delete from RoomMemberEntity where room_id='"+roomId+"'");
    }

    @Override
    public List<RoomMemberEntity> getRoomMemberList(String[] properties, Object[] vals)throws Exception{
       return baseService.getList(RoomMemberEntity.class,properties,vals);
    }

    @Override
    public void update(RoomMemberEntity room) throws Exception {
        baseService.update(room);
    }

    @Override
    public void updateMgrByHql(RoomMemberEntity member) throws Exception{
        baseService.update("update RoomMemberEntity set is_manager="+member.getIs_manager()+
                " where room_id='"+member.getRoom_id()+"' and member_id='"+member.getMember_id()+"'");
    }

    @Override
    public void updateSpeakerByHql(RoomMemberEntity member) throws Exception{
        baseService.update("update RoomMemberEntity set is_stop_speaker="+member.getIs_stop_speaker()+
                " where room_id='"+member.getRoom_id()+"' and member_id='"+member.getMember_id()+"'");
    }



    @Override
    public Long count(String[] properties, Object[] values) throws Exception {
        return baseService.getTotalCount(RoomMemberEntity.class, properties, values);
    }

    @Override
    public String getRoomMemberIdsByRoomId(String[] properties, Object[] vals)throws Exception{
        List<RoomMemberEntity> memberEntities= baseService.getList(RoomMemberEntity.class,properties,vals);
        return memberEntities.stream().map(m->m.getMember_id()).collect(Collectors.joining("#","","#"));
    }

    @Override
    public RoomMemberEntity get(String propertyName, Object value) throws Exception {
        return  baseService.get(RoomMemberEntity.class,propertyName ,value);
    }

    @Override
    public List<Object> getMemberByRoomId(String roomId)throws Exception {
        return baseService.getList("select m from Member m inner join RoomMemberEntity rm on m.id=rm.member_id " +
                "where m.memberType=0 and room_id='"+roomId+"'",Member.class);
    }
}
