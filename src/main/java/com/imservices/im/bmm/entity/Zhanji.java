package com.imservices.im.bmm.entity;

import com.imservices.im.bmm.entity.Member.MEMBER_TYPE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;


@Entity
@Table(name="zhanji")
@SuppressWarnings("all")
public class Zhanji extends BaseEntity {

	private String member_id;
	private String member_uuid;
	private String room_uuid;
	private String room_id;
	private MEMBER_TYPE memberType;
	
	private Double sendMoney=0.0;//发包金额
	private Integer sendNumber=0;//发包数量
	private Double openMoney=0.0;//抢包金额
	private Integer openNumber=0;//抢包数量
	private Double yingMoney=0.0;//赢金额
	private Double shuMoney=0.0;//输金额
   	  
	  
	 
	@Enumerated
	public MEMBER_TYPE getMemberType() {
		return memberType;
	}
	public void setMemberType(MEMBER_TYPE memberType) {
		this.memberType = memberType;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public String getMember_uuid() {
		return member_uuid;
	}
	public void setMember_uuid(String member_uuid) {
		this.member_uuid = member_uuid;
	}
	public String getRoom_uuid() {
		return room_uuid;
	}
	public void setRoom_uuid(String room_uuid) {
		this.room_uuid = room_uuid;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getSendMoney() {
		return sendMoney;
	}
	public void setSendMoney(Double sendMoney) {
		this.sendMoney = sendMoney;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getOpenMoney() {
		return openMoney;
	}
	public void setOpenMoney(Double openMoney) {
		this.openMoney = openMoney;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getYingMoney() {
		return yingMoney;
	}
	public void setYingMoney(Double yingMoney) {
		this.yingMoney = yingMoney;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getShuMoney() {
		return shuMoney;
	}
	public void setShuMoney(Double shuMoney) {
		this.shuMoney = shuMoney;
	}
	public Integer getSendNumber() {
		return sendNumber;
	}
	public void setSendNumber(Integer sendNumber) {
		this.sendNumber = sendNumber;
	}
	public Integer getOpenNumber() {
		return openNumber;
	}
	public void setOpenNumber(Integer openNumber) {
		this.openNumber = openNumber;
	}
	
	
	
	
}
