package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="roomadd")
@SuppressWarnings("all")
public class RoomAdd extends BaseEntity {

	//等待中,成功/已添加,拒绝/失败
	public enum Status {
		wait,success,faile
	}

	private String mid;
	private String roomid;//朋友ID
	private String room_owner_id;
	private String content;
	private Status status;
	private String yaoqing_id;//邀请者ID
	
	
	
	 
	public String getYaoqing_id() {
		return yaoqing_id;
	}
	public void setYaoqing_id(String yaoqing_id) {
		this.yaoqing_id = yaoqing_id;
	}
	public String getRoom_owner_id() {
		return room_owner_id;
	}
	public void setRoom_owner_id(String room_owner_id) {
		this.room_owner_id = room_owner_id;
	}
	@Enumerated
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
