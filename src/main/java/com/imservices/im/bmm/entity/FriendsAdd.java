package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="friendsadd")
@SuppressWarnings("all")
public class FriendsAdd extends BaseEntity {

	//等待中,成功/已添加,拒绝/失败,过期
	public enum Status {
		wait,success,faile,pass
	}

	private String mid;
	private String friendid;//朋友ID
	private String content;
	private Status status;
	
	
	
	 
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
	public String getFriendid() {
		return friendid;
	}
	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
