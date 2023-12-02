package com.imservices.im.bmm.utils.spring;

public class PropertiesConfig {

	public static String ftp_server_ip = "";
	public static String ftp_user = "";
	public static String ftp_pass = "";
	public static String ftp_server_http_prefix = "";
	

	
	public static String getResourceHttpPrefix() {
		return ftp_server_http_prefix;
	}
	
}
