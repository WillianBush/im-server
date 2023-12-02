package com.imservices.im.bmm.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="employee")
@SuppressWarnings("all")
public class Employee extends BaseEntity  {
	
	private String name;//员工名称
	private String member_id;
	private String member_uuid;
	private String inviteCode;//邀请码
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getMember_uuid() {
		return member_uuid;
	}
	public void setMember_uuid(String member_uuid) {
		this.member_uuid = member_uuid;
	}
	public String getInviteCode() {
		return inviteCode;
	}
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

}
