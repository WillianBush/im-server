package com.imservices.im.bmm.bean;

import java.util.ArrayList;
import java.util.List;

public class FriendList {

	private String h;
	private List<FriendsBean> list = new ArrayList<FriendsBean>();
	
	public String getH() {
		return h;
	}
	public void setH(String h) {
		this.h = h;
	}
	public List<FriendsBean> getList() {
		return list;
	}
	public void setList(List<FriendsBean> list) {
		this.list = list;
	}
	
}
