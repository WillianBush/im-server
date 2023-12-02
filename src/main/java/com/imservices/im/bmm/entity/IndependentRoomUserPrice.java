package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="independentroomuserprice")
@SuppressWarnings("all")
public class IndependentRoomUserPrice extends BaseEntity {

	private String room_uuid;
	private String room_id;
	private String user_uuid;
	private String user_id;
	private Double price=0.0;
	
	
	public String getRoom_uuid() {
		return room_uuid;
	}
	public void setRoom_uuid(String room_uuid) {
		this.room_uuid = room_uuid;
	}
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public String getUser_uuid() {
		return user_uuid;
	}
	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	
	
}
