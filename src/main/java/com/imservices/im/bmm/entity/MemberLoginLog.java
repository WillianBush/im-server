package com.imservices.im.bmm.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="memberloginlog")
@SuppressWarnings("all")
@Data
public class MemberLoginLog extends BaseEntity {

	private String mid;
	private String mtel;
	private String mnickName;
	private String ip;
	private String ipAddr;
	
}
