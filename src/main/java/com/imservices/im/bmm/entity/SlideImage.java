package com.imservices.im.bmm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="slideimage")
@SuppressWarnings("all")
public class SlideImage extends BaseEntity {

	private String imgSrc;
	private String title;
	private String targetUrl;
	private Integer orderList=50;
	private Integer status;//0显示 1隐藏
	
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public Integer getOrderList() {
		if(null==orderList) return 50;
		return orderList;
	}
	public void setOrderList(Integer orderList) {
		this.orderList = orderList;
	}
	public Integer getStatus() {
		if(null==status) return 0;
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	
	
}
