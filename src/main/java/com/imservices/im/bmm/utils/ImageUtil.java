package com.imservices.im.bmm.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

//import sun.misc.BASE64Decoder;

public class ImageUtil {

	//通过base64来转化图片并保存指定路径
	public static String BASE64ImageSaveTo(String BASE64Image,String fn,String filepath,HttpServletRequest request) {
		if(!StringUtils.isEmpty(BASE64Image)) {
			//通过base64来转化图片
	        String imageFileStr = BASE64Image.replaceAll("data:image/jpeg;base64,", "");      
	        //BASE64Decoder decoder = new BASE64Decoder();
	        Decoder decoder=Base64.getMimeDecoder();//注不要使用.getDecoder();
	        // Base64解码      
	        byte[] imageByte = null;
	        try {
	            //imageByte = decoder.decodeBuffer(imageFileStr);      
	        	imageByte = decoder.decode(imageFileStr);
	            for (int i = 0; i < imageByte.length; ++i) {      
	                if (imageByte[i] < 0) {// 调整异常数据      
	                    imageByte[i] += 256;      
	                }      
	            }      
	        } catch (Exception e) {
	             e.printStackTrace(); 
	        }  
	        try {
	            // 生成文件         
	        	String filename = filepath+fn;
	            File imageFile = new File(request.getRealPath(filename));
	            imageFile.createNewFile();
	               if(!imageFile.exists()){
	            	   imageFile.createNewFile();
	                }
	                OutputStream imageStream = new FileOutputStream(imageFile);
	                imageStream.write(imageByte);
	                imageStream.flush();
	                imageStream.close();  
	                return "/"+filename;
	        } catch (Exception e) {         
	            e.printStackTrace();      
	        }
		}
		return "";
	}
	
}
