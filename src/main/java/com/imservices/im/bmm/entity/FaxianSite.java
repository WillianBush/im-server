package com.imservices.im.bmm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.Transient;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="faxiansite")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class FaxianSite extends BaseEntity {

	@ApiModelProperty(value = "logo")
	private String logo;

	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "url")
	private String url;

	@ApiModelProperty(value = "")
	private Integer orderList = 50;

	@ApiModelProperty(value = "")
	private Integer status = 1;//0显示 1隐藏

	@Transient
	private  Integer status_user; //用户是否可以打开外链的开关：0关闭 1打开


	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getOrderList() {
		return orderList;
	}
	public void setOrderList(Integer orderList) {
		this.orderList = orderList;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	
	
}
