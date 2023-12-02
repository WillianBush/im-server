package com.imservices.im.bmm.bean;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.UUID;

public class ChatBaseBean implements Cloneable {

	protected String uuid;
	protected String fromUid;
	protected String fromHeadpic;
	protected String fromName;
	protected String date;
	protected Long dateTime;
	protected String toUid;
	protected String toGroupid;
	protected String simple_content;//用于前端消息列表最后的消息
	protected String chatid="";
	protected Integer read = 0;//-1未发送成功【一般在发送消息时设置】 0未读 1已读
	
	
	
	public Integer getRead() {
		if(null==read) return 0;
		return read;
	}
	public void setRead(Integer read) {
		this.read = read;
	}
	public String getChatid() {
		if(StringUtils.isEmpty(chatid)) {
			if(StringUtils.isEmpty(toUid)) return toGroupid;
			return toUid;
		}
		return chatid;
	}
	
	
	
	public void setChatid(String chatid) {
		this.chatid = chatid;
	}



	public String getSimple_content() {
		return simple_content;
	}
	public void setSimple_content(String simple_content) {
		this.simple_content = simple_content;
	}
	public Long getDateTime() {
		return dateTime;
	}
	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}
	public String getUuid() {
		if(StringUtils.isEmpty(uuid)) {
			setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getFromUid() {
		return fromUid;
	}
	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}
	public String getFromHeadpic() {
		return fromHeadpic;
	}
	public void setFromHeadpic(String fromHeadpic) {
		this.fromHeadpic = fromHeadpic;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		setDateTime(new Date().getTime());
		this.date = date;
	}
	public String getToUid() {
		return toUid;
	}
	public void setToUid(String toUid) {
		this.toUid = toUid;
	}
	public String getToGroupid() {
		return toGroupid;
	}
	public void setToGroupid(String toGroupid) {
		this.toGroupid = toGroupid;
	}
	
	
	
}
