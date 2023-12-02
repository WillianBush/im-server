package com.imservices.im.bmm.bean;

public class FriendsAddBean {

	private String id;
	private String to_headpic;
	private String to_name;
	private String to_member_uuid;
	private String to_memberid;
	private String from_headpic;
	private String from_name;
	private String from_member_uuid;
	private String from_memberid;
	private String content;
	private String status;
	
	private Integer fromMemberRead;//1已读
	private Integer toMemberRead;//1已读
	
	
	public Integer getFromMemberRead() {
		return fromMemberRead;
	}
	public void setFromMemberRead(Integer fromMemberRead) {
		this.fromMemberRead = fromMemberRead;
	}
	public Integer getToMemberRead() {
		return toMemberRead;
	}
	public void setToMemberRead(Integer toMemberRead) {
		this.toMemberRead = toMemberRead;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTo_headpic() {
		return to_headpic;
	}
	public void setTo_headpic(String to_headpic) {
		this.to_headpic = to_headpic;
	}
	public String getTo_name() {
		return to_name;
	}
	public void setTo_name(String to_name) {
		this.to_name = to_name;
	}
	public String getTo_member_uuid() {
		return to_member_uuid;
	}
	public void setTo_member_uuid(String to_member_uuid) {
		this.to_member_uuid = to_member_uuid;
	}
	public String getTo_memberid() {
		return to_memberid;
	}
	public void setTo_memberid(String to_memberid) {
		this.to_memberid = to_memberid;
	}
	public String getFrom_headpic() {
		return from_headpic;
	}
	public void setFrom_headpic(String from_headpic) {
		this.from_headpic = from_headpic;
	}
	public String getFrom_name() {
		return from_name;
	}
	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}
	public String getFrom_member_uuid() {
		return from_member_uuid;
	}
	public void setFrom_member_uuid(String from_member_uuid) {
		this.from_member_uuid = from_member_uuid;
	}
	public String getFrom_memberid() {
		return from_memberid;
	}
	public void setFrom_memberid(String from_memberid) {
		this.from_memberid = from_memberid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
	
}
