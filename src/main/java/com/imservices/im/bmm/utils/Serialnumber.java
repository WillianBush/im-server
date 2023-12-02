package com.imservices.im.bmm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Serialnumber {
	/**
     * 获取现在时间
     * @return返回字符串格式yyyyMMddHHmmss
     */
	  public static String getStringDate() {
		     Date currentTime = new Date();
		     SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		     String dateString = formatter.format(currentTime);
//		     //
		     return dateString;
		  }
	  /**
	   * 由年月日时分秒+3位随机数
	   * 生成流水号
	   * @return
	   */
	  public static String Getnum(){
		  String t = getStringDate();
		  int x=(int)(Math.random()*9000)+1000;
		  String serial = t + x;
		  return serial;
	  }
	  
	  //主方法测试
	public static void main(String[] args) {
		for(int i=0;i<10;i++) {
			String m= Getnum();
			//
		}
		
	}
}
