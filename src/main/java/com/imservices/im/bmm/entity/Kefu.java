package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="Kefu")
@SuppressWarnings("all")
public class Kefu extends BaseEntity {

	private String mid;
	private String muuid;
	private String name;
	private Integer status=0;//0显示 1不显示
	private Integer orderList = 50;
	
	
	public Integer getOrderList() {
		return orderList;
	}
	public void setOrderList(Integer orderList) {
		this.orderList = orderList;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMuuid() {
		return muuid;
	}
	public void setMuuid(String muuid) {
		this.muuid = muuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

	
	
	  
}
