package com.imservices.im.bmm.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 临时表【主要场景为WX扫码登陆】
 */
@Entity
@Table(name="tempet")
@SuppressWarnings("all")
public class TempEt extends BaseEntity {
	private String val1;//值
	private String val2;//值
	private String val3;//值
	private String val4;//值 
	
	public String getVal4() {
		return val4;
	}
	public void setVal4(String val4) {
		this.val4 = val4;
	}
	public String getVal1() {
		return val1;
	}
	public void setVal1(String val1) {
		this.val1 = val1;
	}
	public String getVal2() {
		return val2;
	}
	public void setVal2(String val2) {
		this.val2 = val2;
	}
	public String getVal3() {
		if(StringUtils.isEmpty(val3)) return "";
		return val3;
	}
	public void setVal3(String val3) {
		this.val3 = val3;
	}

	
}
