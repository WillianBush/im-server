package com.imservices.im.bmm.bean;

import java.util.ArrayList;
import java.util.List;

public class NoticeList {

	private String dateStr;
	private List<NoticeItem> list = new ArrayList<NoticeItem>();
	
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public List<NoticeItem> getList() {
		return list;
	}
	public void setList(List<NoticeItem> list) {
		this.list = list;
	}
	
	
}
