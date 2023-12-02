package com.imservices.im.bmm.service;

import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.entity.RoomMemberEntity;

import java.util.List;

public interface RoomMemberService {
     /**
      * 保存群组跟成员对应关系
      * @param room
      * @throws Exception
      */
     void save(RoomMemberEntity room) throws Exception;

     /**
      * 根据群组和成员删除
      * @param room
      * @throws Exception
      */
     void deleteByHql(RoomMemberEntity room) throws Exception;

     /***
      * 根据群ID 删除掉群
      * @param roomId
      * @throws Exception
      */
     void deleteByRoomIdHql(String roomId) throws Exception;
     /***
      * 查询群组和成员集合
      * @param properties
      * @param vals
      * @return
      * @throws Exception
      */
     List<RoomMemberEntity> getRoomMemberList(String[] properties, Object[] vals)throws Exception;

     /***
      * 修改
      * @param member
      * @throws Exception
      */
     void update(RoomMemberEntity member) throws Exception;

     /**
      * 修改群管理员
      * @param member
      * @throws Exception
      */
     void updateMgrByHql(RoomMemberEntity member) throws Exception;

     /**
      * 修改禁言发言状态
      * @param member
      * @throws Exception
      */
     void updateSpeakerByHql(RoomMemberEntity member) throws Exception;

     /***
      * 获取条数
      */
     Long count(String[] properties, Object[] values) throws Exception;

     /**
      * 根据群ID查询群成员，把群成员ID组装成字符串返回
      * */
     String getRoomMemberIdsByRoomId(String[] properties, Object[] values)throws Exception;

     /**
      * 获取单个群成员
      * @param propertyName
      * @param value
      * @return
      * @throws Exception
      */
     RoomMemberEntity get(String propertyName, Object value) throws Exception;

     /***
      * 获取群组里的真实用户
      * @return
      */
     List<Object> getMemberByRoomId(String roomId)throws Exception;
}
