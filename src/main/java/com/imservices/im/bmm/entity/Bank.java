package com.imservices.im.bmm.entity;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="bank")
@SuppressWarnings("all")
public class Bank extends BaseEntity  {
	private String name;//银行名称
	private String belonger;//开户者
	private String code;//银行代码
	private String bankType="储蓄卡";//储蓄卡
	private String cardCode;//银行卡号
	private String uid;
	
	@Transient
	public String getSimpleCardCode() {
		if(StringUtils.isEmpty(cardCode)) return "";
		if(cardCode.length()<=4) {
			return cardCode;
		}
		else {
			return cardCode.substring(0, 4);
		}
	}
	 
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public String getCardCode() {
		return cardCode;
	}
	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBelonger() {
		return belonger;
	}
	public void setBelonger(String belonger) {
		this.belonger = belonger;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
