package com.imservices.im.bmm.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;


public class Loktong_sms {
	private static String url = "http://www.lokapi.cn/smsUTF8.aspx"; 
	public static void main(String[] args) throws ParseException {
	
		long timestamp = System.currentTimeMillis();
		
		
		
		String beforSign = "action=sendtemplate&username=qaz987&password="+getMD5String("qaz987")+"&token=0cd3c6d1&timestamp="+timestamp;
		String postData = "action=sendtemplate&username=qaz987&password="+getMD5String("qaz987")+"&token=0cd3c6d1&templateid=244BB44D&param=18300057255|666666&rece=json&timestamp="+timestamp+"&sign="+getMD5String(beforSign);
		String result = sendPost(url,postData); 
		
		
	}  
	
	public static String sendAuthCode(String tel,String code,String username,String  password,String token,String templateid) {
		long timestamp = System.currentTimeMillis();
		String beforSign = "action=sendtemplate&username="+username+"&password="+getMD5String(password)+"&token="+token+"&timestamp="+timestamp;
		String postData = "action=sendtemplate&username="+username+"&password="+getMD5String(password)+"&token="+token+"&templateid="+templateid+"&param="+
					tel+"|"+code+"&rece=json&timestamp="+timestamp+"&sign="+getMD5String(beforSign);
		String result = sendPost(url,postData); 
		return result;
	}
	
	
	public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    

	
	
	
	public static String getMD5String(String rawString){    //用来计算MD5的函数
		String[] hexArray = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(rawString.getBytes());
			byte[] rawBit = md.digest();
			String outputMD5 = " ";
			for(int i = 0; i<16; i++){
				outputMD5 = outputMD5+hexArray[rawBit[i]>>>4& 0x0f];
				outputMD5 = outputMD5+hexArray[rawBit[i]& 0x0f];
			}
			return outputMD5.trim();
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 生成秘钥
	 * 
	 * @param tm
	 * @param key
	 * @return
	 */
	public static String createSign(TreeMap<String, String> tm, String key) {
		StringBuffer buf = new StringBuffer(key);
		for (Map.Entry<String, String> en : tm.entrySet()) {
			String name = en.getKey();
			String value = en.getValue();
			if (!"sign".equals(name) && !"param".equals(name) && value != null && value.length() > 0 && !"null".equals(value)) {
				buf.append(name).append('=').append(value).append('&');
			}
		}
		String _buf = buf.toString();
		return _buf.substring(0, _buf.length() - 1);
	}

	/**
	  * 将文件转成base64 字符串
	  * @param path文件路径
	  * @return  * 
	  * @throws Exception
	  */

	 public static String encodeBase64File(String path) throws Exception {
		  File file = new File(path);;
		  FileInputStream inputFile = new FileInputStream(file);
		  byte[] buffer = new byte[(int) file.length()];
		  inputFile.read(buffer);
		  inputFile.close();
		  //return new BASE64Encoder().encode(buffer);
return "";
	 }
	 

}