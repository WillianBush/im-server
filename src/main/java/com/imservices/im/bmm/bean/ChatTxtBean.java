package com.imservices.im.bmm.bean;

import java.io.Serializable;

public class ChatTxtBean  extends ChatBaseBean implements Serializable{

	private String txt;
	private String oldTxt;//也就是TXT未处理前的字符
	private String sub_txt;//特殊情况下使用 如语音需要记录多少秒
	private String psr="";//解析方式  默认使用rich-text   还有其它的选择uparse voice(语音) video(视频) url(超链接)
	private String aite="";//@群成员 多个ID用#分开
	
	
	public ChatTxtBean() {
		
	}

	
	
	public String getAite() {
		return aite;
	}



	public void setAite(String aite) {
		this.aite = aite;
	}



	public String getOldTxt() {
		return oldTxt;
	}

	public void setOldTxt(String oldTxt) {
		this.oldTxt = oldTxt;
	}

	public String getSub_txt() {
		return sub_txt;
	}

	public void setSub_txt(String sub_txt) {
		this.sub_txt = sub_txt;
	}

	public String getPsr() {
		return psr;
	}

	public void setPsr(String psr) {
		this.psr = psr;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
//		if(StringUtils.isEmpty(oldTxt)) setOldTxt(txt);
		this.txt = txt;
	}
	
	@Override
	public ChatTxtBean clone() {
		ChatTxtBean o = null;
		try{   
			o = (ChatTxtBean)super.clone();
			//
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
