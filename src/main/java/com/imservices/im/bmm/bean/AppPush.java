package com.imservices.im.bmm.bean;

public class AppPush {
	private String appid;//appid
    private String appkey;//appkey
    private String masterSecret;//masterSecret
    
    
    private String clientid;//clientid
    private boolean appHide = false;//是否把APP切换到后台
    private String uid;
    private String userName;//账户
    private String userRole;//用户角色
    
    
  
	public boolean isAppHide() {
		return appHide;
	}
	public void setAppHide(boolean appHide) {
		this.appHide = appHide;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMasterSecret() {
		return masterSecret;
	}
	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}
	// 其他对象
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

    
}
