package com.imservices.im.bmm.utils.web;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.entity.IpAddrEntity;
import com.imservices.im.bmm.utils.OKhttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * web工具类
 * @author wang<fangyuan.co@outlook.com>
 */
@Slf4j
public class WebUtilsWeb {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebUtilsWeb.class);

	/**
	 * 获取IP地址
	 *
	 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = null, unknown = "unknown", seperator = ",";
		int maxLength = 15;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (!StringUtils.isEmpty(ip) && ip.contains(seperator)){
				String[] ips = ip.split(seperator);
				ip=ips[0];
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			LOGGER.error("WebUtils.getIpAddr ERROR ", e);
		}

		// 使用代理，则获取第一个IP地址
		if (StringUtils.isEmpty(ip) && ip.length() > maxLength) {
			int idx = ip.indexOf(seperator);
			if (idx > 0) {
				ip = ip.substring(0, idx);
			}
		}

		return ip;
	}

	public static String getIpAddrErrorLog(HttpServletRequest request) {
		String ip = null, unknown = "unknown", seperator = ",";
		int maxLength = 15;
		try {
			ip = request.getHeader("x-forwarded-for");
			log.error("ip x-forwarded-for:{}",ip);
			if (!StringUtils.isEmpty(ip) && ip.contains(seperator)){
				String[] ips = ip.split(seperator);
				ip=ips[0];
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
				log.error("ip:{}",ip);
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
				log.error("ip:{}",ip);
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
				log.error("ip:{}",ip);
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
				log.error("ip:{}",ip);
			}
			if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
				log.error("ip:{}",ip);
			}
		} catch (Exception e) {
			LOGGER.error("WebUtils.getIpAddr ERROR ", e);
		}

		// 使用代理，则获取第一个IP地址
		if (StringUtils.isEmpty(ip) && ip.length() > maxLength) {
			int idx = ip.indexOf(seperator);
			if (idx > 0) {
				ip = ip.substring(0, idx);
			}
		}

		return ip;
	}

	/**
	 * 查询IP归属地信息
	 * */
	public static String getIpAddress(String ip) {
		Map<String, String> queryParams = new HashMap<>();
//		log.info("查询IP{}归属地信息", ip);
		queryParams.put("ip", ip);
		try {
			String rep = OKhttpUtil.httpGet("https://ip.useragentinfo.com/json", queryParams);
			if (!StringUtils.isEmpty(rep)) {
				IpAddrEntity ipAddr =	JSONObject.parseObject(rep, IpAddrEntity.class);
				if (!StringUtils.isEmpty(ipAddr.getProvince())) {
					return ipAddr.getCountry() + "-" + ipAddr.getProvince();
				}else {
					return ipAddr.getCountry();
				}

			}
		} catch (Exception e) {
			log.error("查询IP归属地报错", e);
		}

		return "";
	}

}
