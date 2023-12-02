package com.imservices.im.bmm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="trajectory")
@SuppressWarnings("all")
public class Trajectory extends BaseEntity {
	private String descri;//相关描述说明
	private String dateStr;//时间点
	private String member_uuid;
	private String member_id;
	
	
	
	public String getMember_uuid() {
		return member_uuid;
	}
	public void setMember_uuid(String member_uuid) {
		this.member_uuid = member_uuid;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	@Column(length=2000)
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	
	
}
