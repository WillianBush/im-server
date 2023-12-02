package com.imservices.im.bmm.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name="room_member")
@SuppressWarnings("all")
public class RoomMemberEntity extends BaseEntity{

    /**
     * 群组ID
     */
    private String room_id;

    /**
     * 用户ID
     */
    private String member_id;

    /**
     * 是否管理员：0否，1是
     */
    private Integer is_manager;

    /**
     * 是否禁言：0否，1是
     */
    private Integer is_stop_speaker;

    public RoomMemberEntity() {
    }

    public RoomMemberEntity(String room_id, String member_id) {
        this.room_id = room_id;
        this.member_id = member_id;
        /***设置默认值*/
        this.is_manager=0;
        this.is_stop_speaker=0;
    }

    public RoomMemberEntity(String room_id, String member_id, Integer is_manager) {
        this.room_id = room_id;
        this.member_id = member_id;
        this.is_manager = is_manager;
    }
}
