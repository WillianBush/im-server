package com.imservices.im.bmm.utils;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.entity.WebConfig;
import com.imservices.im.bmm.entity.WebConfig.SMS_PLATFORM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsUtil {
	 static String httpUrl = "http://api.smsbao.com/sms";
	  static String testUsername = "test02";
	  static String testPassword = "test02";
	  static String sign = "【疯狂的红包】";
	  
	  public static void main(String[] args)
	  {
	    //send("18300057255", "【虾米任务】您的验证码是1111。如非本人操作，请忽略本短信","9e2a74a9e01e71fb4de93c1de3022499");
		  Map<String, String> map = new HashMap<String, String>();
		  map.put("action", "sendtemplate");
		  map.put("username", "qaz987");
		  map.put("password", MD5.MD5Encode("qaz987").toUpperCase().substring(0, 32));
		  map.put("token", "0cd3c6d1");
		  map.put("templateid", "244BB44D");
		  map.put("param", "18300057255|88888");
		  map.put("rece", "json");
		  map.put("timestamp", new Date().getTime()+"");
		  String str ="action="+map.get("action")+"&username="+map.get("username")+"&password="+map.get("password")+"&token="+map.get("token")+"&timestamp="+map.get("timestamp");
		  map.put("sign", MD5.MD5Encode("str"));
		  String s = getPost("http://www.lokapi.cn/smsUTF8.aspx",map);
		  
	  }
	  
	  public static void send(String phone, String content,String apikey) {
		  try {
			String str = JavaSmsApi.sendSms(apikey, content, phone);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  
	  
	  public static String getPost(String urls,Map<String, String> params){
	        String message="";
	        try {
	        	//创建URL实例，打开URLConnection
	            URL url=new URL(urls);
	            // 通过远程url连接对象打开连接
	            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
	            //设置连接参数
	            connection.setRequestMethod("POST");//提交方式
	            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
	            connection.setDoOutput(true);
	            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
	            connection.setDoInput(true);
	            connection.setUseCaches(false);
	            // 设置连接主机服务器超时时间
	            connection.setConnectTimeout(30000);
	            // 设置读取主机服务器返回数据超时时间
	            connection.setReadTimeout(30000);
	            // 设置接收数据的格式:json格式数据
	            connection.setRequestProperty("Accept", "application/json;charset=utf-8;"); 
	            //设置请求数据类型：json格式数据
	            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded;charset=utf-8;");
	            //链接并发送
	            connection.connect();
	            // 通过连接对象获取一个输出流
	            OutputStream out = connection.getOutputStream(); 
	            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
				out.write(JSONObject.toJSONBytes(params));
				out.flush();
				out.close();
				
	            // 通过连接对象获取一个输入流，向远程读取
	            InputStream inputStream=connection.getInputStream();
	            byte[] data=new byte[1024];
	            StringBuffer sb1=new StringBuffer();
	            int length=0;
	            while ((length=inputStream.read(data))!=-1){
	                String s=new String(data, 0,length);
	                sb1.append(s);
	            }
	            message=sb1.toString();
	            inputStream.close();
	            //关闭连接
	            connection.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return message;
	    }

	public static void sendAuthCode(String tel,String code, WebConfig wc) {
		if(wc.getSms_platform()==SMS_PLATFORM.loktong) {
			Loktong_sms.sendAuthCode(tel, code, wc.getSms_loktong_un(), wc.getSms_loktong_pwd(), wc.getSms_loktong_token(), wc.getSms_loktong_templateid());
		}
	}
}
