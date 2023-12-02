package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="accessrecord")
@SuppressWarnings("all")
public class AccessRecord extends BaseEntity {

	private String img;
	private String title;
	private String subname;
	private String typeid;//1房间 2用户
	private String entityid;//实体ID
	private String uid;//所属玩家ID
  	
	  
	public String getSubname() {
		return subname;
	}
	public void setSubname(String subname) {
		this.subname = subname;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getEntityid() {
		return entityid;
	}
	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}
	
	
	  
}
