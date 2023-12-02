package com.imservices.im.bmm.utils;


import com.imservices.im.bmm.utils.spring.PropertiesConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil
{
	
  public static enum FTP_FOLDER {	
	  img_sys,img_member,chat_img,chat_video,chat_voice,friendscircle
  }
	
  private static String ftpIp = "";
  private static String ftpUser = "";
  private static String ftpPass = "";
  
  private String ip;
  private int port;
  private String user;
  private String pwd;
  private FTPClient ftpClient;
  
  public FTPUtil(String ip, int port, String user, String pwd)
  {
    this.ip = ip;
    this.port = port;
    this.user = user;
    this.pwd = pwd;
  }
  
  
  public static boolean uploadFile(String remotePath,List<File> fileList,String[] delFileNames)
		    throws IOException
  {
		    FTPUtil ftpUtil = new FTPUtil(PropertiesConfig.ftp_server_ip, 21, PropertiesConfig.ftp_user, PropertiesConfig.ftp_pass);
//	  		FTPUtil ftpUtil = new FTPUtil("127.0.0.1", 21,"test1", "123456");
		    boolean result = ftpUtil.upload(remotePath, fileList,delFileNames);
		    return result;
	
  }
  
  
  private boolean upload(String remotePath, List<File> fileList,String[] delFileNames)
    throws IOException
  {
    boolean uploaded = true;
    
    FileInputStream fis = null;
    if (connectServer(this.ip, this.port, this.user, this.pwd)) {
      try
      {
        this.ftpClient.setBufferSize(1024);
        this.ftpClient.setControlEncoding("UTF-8");
        this.ftpClient.setFileType(2);
        this.ftpClient.enterLocalPassiveMode();
        
        
      //该级路径不存在就创建并切换
        if (!ftpClient.changeWorkingDirectory(remotePath)) {
        	 this.ftpClient.makeDirectory(remotePath);
        	 this.ftpClient.changeWorkingDirectory(remotePath);
        }
        if(!ArrayUtils.isEmpty(delFileNames)) {
        	for(String delFileName : delFileNames) {
        		if(!StringUtils.isEmpty(delFileName)) {
        			 this.ftpClient.dele(delFileName);
        		}
        	}
        }
        
        for (File fileItem : fileList)
        {
          fis = new FileInputStream(fileItem);
          
          this.ftpClient.storeFile(fileItem.getName(), fis);
        }
      }
      catch (IOException e)
      {
        uploaded = false;
        
        e.printStackTrace();
      }
      finally
      {
        fis.close();
        
        this.ftpClient.disconnect();
      }
    }
    return uploaded;
  }
  
  private boolean connectServer(String ip, int port, String user, String pwd)
  {
    boolean isSuccess = false;
    
    this.ftpClient = new FTPClient();
    try
    {
      this.ftpClient.connect(ip);
      
      isSuccess = this.ftpClient.login(user, pwd);
    }
    catch (IOException e)
    {
//    	System.out.println("连接FTP服务器异常"+e);
    }
    return isSuccess;
  }
  

  
  /**
   * @MethodName  deleteFile
   * @Description 删除ftp文件夹下面的视频文件
   * @param
   * @return 
   * 
   */
  public static boolean deleteFile(String path,String name){
  	boolean isAppend = false;
  FTPUtil ftpUtil = new FTPUtil(PropertiesConfig.ftp_server_ip, 21, PropertiesConfig.ftp_user, PropertiesConfig.ftp_pass);
//  	FTPUtil ftpUtil = new FTPUtil("127.0.0.1", 21,"test1", "123456");
  	try {
  		 if (ftpUtil.connectServer(ftpUtil.ip, ftpUtil.port, ftpUtil.user, ftpUtil.pwd)) {
  	  		System.out.println(path);
  	  		System.out.println(name);
  	  		//path = new String(path.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
  	  		ftpUtil.ftpClient.changeWorkingDirectory(path);
  	  		//name = new String(name.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
  	  		ftpUtil.ftpClient.dele(name);
  	  		ftpUtil.ftpClient.logout();
  	      	isAppend = true;
  		 }
  		
		} catch (Exception e) {
			 e.printStackTrace();
		} finally {
          if ( ftpUtil.ftpClient.isConnected()) {
              try {
            	  ftpUtil.ftpClient.disconnect();
              } catch (IOException ioe) {}
          }
      }
  	return isAppend;
  }
  
  
  /**
   * @MethodName  deleteFolder
   * @Description 删除ftp文件夹
   * @param
   * @return 
   * 
   */
  public static boolean deleteFolder(String folder){
  	boolean isAppend = false;
    FTPUtil ftpUtil = new FTPUtil(PropertiesConfig.ftp_server_ip, 21, PropertiesConfig.ftp_user, PropertiesConfig.ftp_pass);
//  	FTPUtil ftpUtil = new FTPUtil("127.0.0.1", 21,"test1", "123456");
  	try {
  		//folder = new String(folder.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
  		isAppend = ftpUtil.ftpClient.removeDirectory(folder);
  		ftpUtil.ftpClient.logout();
		} catch (Exception e) {
			 e.printStackTrace();
		}finally {
          if (ftpUtil.ftpClient.isConnected()) {
              try {
            	  ftpUtil.ftpClient.disconnect();
              } catch (IOException ioe) {
              }
          }
      }
  	return isAppend;
  }
  
  /**
   * @MethodName  reName
   * @Description 修改文件名称
   * @param
   * @return 
   * 
   */
  public static boolean reName(String path,String name){
  	boolean isAppend = false;
  	FTPUtil ftpUtil = new FTPUtil(PropertiesConfig.ftp_server_ip, 21, PropertiesConfig.ftp_user, PropertiesConfig.ftp_pass);
//	FTPUtil ftpUtil = new FTPUtil("127.0.0.1", 21,"test1", "123456");
  	try {
          //path = new String(path.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
         // name = new String(name.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
          isAppend = ftpUtil.ftpClient.rename(path, name);
          ftpUtil.ftpClient.logout();
		} catch (Exception e) {
			 e.printStackTrace();
		}finally {
          if (ftpUtil.ftpClient.isConnected()) {
              try {
            	  ftpUtil.ftpClient.disconnect();
              } catch (IOException ioe) {
              }
          }
      }
  	return isAppend;
  }

  
  public static void main(String[] args) throws Exception {
//	  FTPUtil.uploadFile("a",Lists.newArrayList(
//			  new File("d:/tm1.png")
//			  ),new String[] {"base11.gif"});
	  deleteFile("a","tm1.png");
	  
	  
  }
}
