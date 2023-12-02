package com.imservices.im.bmm.bean;

public class AccessRecordBean {

	private String arid;
	
	private String id;//用于标识此记录,便于更新状态,时间等信息
	private String title;
	private String content;
	private Integer aiteCount = 0;//@-你的数量
	private String createDate;
	private Long createDateTime; 
	private String img;
	private String typeid;//1房间 2用户
	private Integer unread=0;//未读信息数量
	private String subname;
	
	private MemberBean mb;
	
	private String roomMoney = "";
	
	private Integer top = 50;//越小越靠前，由前端修改
	private Integer online = 0;//是否在线【只针对用户】 0否 1是
	private String lastLoginDate;//针对用户 上次在线时间，当用户离线 时需要
	
	
	
	
	public Integer getAiteCount() {
		return aiteCount;
	}
	public void setAiteCount(Integer aiteCount) {
		this.aiteCount = aiteCount;
	}
	public String getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Integer getOnline() {
		if(null==online) return 0;
		return online;
	}
	public void setOnline(Integer online) {
		this.online = online;
	}
	public Long getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
	}
	public Integer getTop() {
		return top;
	}
	public void setTop(Integer top) {
		this.top = top;
	}
	public String getArid() {
		return arid;
	}
	public void setArid(String arid) {
		this.arid = arid;
	}
	public String getRoomMoney() {
		return roomMoney;
	}
	public void setRoomMoney(String roomMoney) {
		this.roomMoney = roomMoney;
	}
	public MemberBean getMb() {
		return mb;
	}
	public void setMb(MemberBean mb) {
		this.mb = mb;
	}
	public String getSubname() {
		return subname;
	}
	public void setSubname(String subname) {
		this.subname = subname;
	}
	public Integer getUnread() {
		return unread;
	}
	public void setUnread(Integer unread) {
		this.unread = unread;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
	
}
