package com.imservices.im.bmm.bean;

import java.util.UUID;

//名片
public class ChatCardBean extends ChatBaseBean {

	private String mheadpic;
	private String mname;
	private String mid;
	private String muuid;
	
	
	public ChatCardBean() {
		setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		
	}


	public String getMheadpic() {
		return mheadpic;
	}


	public void setMheadpic(String mheadpic) {
		this.mheadpic = mheadpic;
	}


	public String getMname() {
		return mname;
	}


	public void setMname(String mname) {
		this.mname = mname;
	}


	public String getMid() {
		return mid;
	}


	public void setMid(String mid) {
		this.mid = mid;
	}


	public String getMuuid() {
		return muuid;
	}


	public void setMuuid(String muuid) {
		this.muuid = muuid;
	}
	
	@Override
	public ChatCardBean clone() {
		ChatCardBean o = null;
		try{   
			o = (ChatCardBean)super.clone();
			//
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	
}
