package com.imservices.im.bmm.bean;

public class RoomRobotBean {

	public String room_id;
	public String room_uuid;  
	public String room_name;
	public int robotCount = 0;
	private String fixedRobotIds;//固定机器人memberId集合，以,分开
	private int waitRemoveRobotCount=0;//待清除机器人数量
	private int openRedDelayStart = 1;//开包延迟最低秒数
	private int openRedDelayEnd = 2;//开包延迟最高秒数
	
	
	public int getOpenRedDelayStart() {
		return openRedDelayStart;
	}
	public void setOpenRedDelayStart(int openRedDelayStart) {
		this.openRedDelayStart = openRedDelayStart;
	}
	public int getOpenRedDelayEnd() {
		return openRedDelayEnd;
	}
	public void setOpenRedDelayEnd(int openRedDelayEnd) {
		this.openRedDelayEnd = openRedDelayEnd;
	}
	public int getWaitRemoveRobotCount() {
		return waitRemoveRobotCount;
	}
	public void setWaitRemoveRobotCount(int waitRemoveRobotCount) {
		this.waitRemoveRobotCount = waitRemoveRobotCount;
	}
	public String getFixedRobotIds() {
		return fixedRobotIds;
	}
	public void setFixedRobotIds(String fixedRobotIds) {
		this.fixedRobotIds = fixedRobotIds;
	}
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public String getRoom_uuid() {
		return room_uuid;
	}
	public void setRoom_uuid(String room_uuid) {
		this.room_uuid = room_uuid;
	}
	public String getRoom_name() {
		return room_name;
	}
	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}
	public int getRobotCount() {
		return robotCount;
	}
	public void setRobotCount(int robotCount) {
		this.robotCount = robotCount;
	}
}
