package com.imservices.im.bmm.bean;

public class MessageBean {
	
	public enum MessageType {
		USER_TXT,USER_RED,SYS_TXT,GAME_JIESUAN,SYSUSER_TXT,USER_TRANSFER,USER_CARD
	} 
	
	private String chatid;//用于标识此信息属于与哪个用户或群的聊天记录
	private String chatType;//1群 2用户
	private String type;
	private Object bean;//对象
	 
	
	 
	public String getChatType() {
		return chatType;
	}
	public void setChatType(String chatType) {
		this.chatType = chatType;
	}
	public String getChatid() {
		return chatid;
	}
	public void setChatid(String chatid) {
		this.chatid = chatid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getBean() {
		return bean;
	}
	public void setBean(Object bean) {
		this.bean = bean;
	}
	
	
}
