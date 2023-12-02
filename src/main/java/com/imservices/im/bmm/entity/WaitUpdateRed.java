package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  主要针对已经接收到红包了，但是下线了没有在线的用户，上线后红包状态的更新
 *
 */
@Entity
@Table(name="waitupdatered")
@SuppressWarnings("all")
public class WaitUpdateRed extends BaseEntity {
	
	private String uid;
	private String redid;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getRedid() {
		return redid;
	}
	public void setRedid(String redid) {
		this.redid = redid;
	}
	
	
	
}
