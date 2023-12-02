package com.imservices.im.bmm.utils.web;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("all")
public class PagerUtils {

	public static Pager createPager(HttpServletRequest request) {
		String pageNumber = request.getParameter("pageNumber");
		String pageSize = request.getParameter("pageSize");
		String orderBy = request.getParameter("orderBy");
		String orderTypeStr = request.getParameter("orderType");
		String property = request.getParameter("property");
		String keyword = request.getParameter("keyword");
		OrderType orderType = OrderType.desc;
		if(StringUtils.isEmpty(pageNumber)||!NumberUtils.isNumber(pageNumber)) {
			pageNumber = "1";
		}
		if(StringUtils.isEmpty(pageSize)||!NumberUtils.isNumber(pageSize)) {
			pageSize = "30";
		}
		if(StringUtils.isEmpty(orderBy)) {
			orderBy = "createDate";
		}
		if(!StringUtils.isEmpty(orderTypeStr)) {
			try {
				orderType = OrderType.valueOf(orderTypeStr);
			} catch (IllegalArgumentException e) {
				orderType = OrderType.desc;
			}
		}
		Pager pager = new Pager(Integer.valueOf(pageNumber),Integer.valueOf(pageSize),orderBy,orderType);
		if(!StringUtils.isEmpty(property)) {
			pager.setProperty(property);
		}
		if(!StringUtils.isEmpty(keyword)) {
			pager.setKeyword(keyword);
		}
		return pager;
	}
}
