package com.imservices.im.bmm.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="usertransfer")
@SuppressWarnings("all")
public class UserTransfer extends BaseEntity {

	private String sn;
	private Double money;//玩家能抢的金额之和
	private Integer status=0;//0可拆开 1已抢完 2过期
	
	private Date expiredDate;
	private Date finishDate;
	
	private Member member;
	
	private String m_uuid;//转账者ID
	private String m_id;
	
	
	private String friend_uuid;//接收者ID
	private String friend_id;
	private String friend_name;
	
	public UserTransfer(){}
	public UserTransfer(String id){setId(id);}
	
	
	@Transient
	public String getStatusName() {
		if(status==0) {return "待收款";}
		else if(status==1) {return "已收款";}
		else {
			return "已过期";
		}
	}
	
	@Transient
	public String getMemberId() {
		return member.getMemberId();
	}
	
	public Date getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	public String getM_uuid() {
		return m_uuid;
	}
	public void setM_uuid(String m_uuid) {
		this.m_uuid = m_uuid;
	}
	public String getM_id() {
		return m_id;
	}
	public void setM_id(String m_id) {
		this.m_id = m_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getFriend_uuid() {
		return friend_uuid;
	}
	public void setFriend_uuid(String friend_uuid) {
		this.friend_uuid = friend_uuid;
	}
	public String getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(String friend_id) {
		this.friend_id = friend_id;
	}
	public String getFriend_name() {
		return friend_name;
	}
	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}
	
	  
	
	
	
}
