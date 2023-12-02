package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="friends")
@SuppressWarnings("all")
public class Friends extends BaseEntity {


	private String mid; //-> memberId
	private String friendid;//朋友ID
	
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getFriendid() {
		return friendid;
	}
	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}
	  
}
