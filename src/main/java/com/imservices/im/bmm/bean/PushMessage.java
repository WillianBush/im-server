package com.imservices.im.bmm.bean;

public class PushMessage {
	private String title;
    private String content;
    // 用户角色
    private String userRole;
    
    // 其他对象
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

}
