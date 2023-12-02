// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Pager.java

package com.imservices.im.bmm.bean;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public class Pager<T>
{
	public enum OrderType
	{
		asc,desc
	}


	public static final Integer MAX_PAGE_SIZE = Integer.valueOf(20000);
	private Integer pageNumber;
	private Integer pageSize;
	private Integer totalCount;
	private Integer pageCount;
	private String property;
	private String keyword;
	private String orderBy;
	private OrderType orderType;
	private List<T> list;

	public Pager()
	{
		pageNumber = Integer.valueOf(1);
		pageSize = Integer.valueOf(20);
		totalCount = Integer.valueOf(0);
		pageCount = Integer.valueOf(0);
		keyword = "";
		orderBy = "createDate";
		orderType = OrderType.desc;
	}

	public Pager(int pageNumber, int pageSize, String orderBy, OrderType orderType)
	{
		this.pageNumber = Integer.valueOf(1);
		this.pageSize = Integer.valueOf(20);
		totalCount = Integer.valueOf(0);
		pageCount = Integer.valueOf(0);
		keyword = "";
		this.orderBy = "createDate";
		this.orderType = OrderType.desc;
		this.orderType = orderType;
		this.pageNumber = Integer.valueOf(pageNumber);
		//
		this.pageSize = Integer.valueOf(pageSize);
		if (orderBy == null)
			this.orderBy = "createDate";
		else
			this.orderBy = orderBy;
	}

	public Integer getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber(String pn)
	{
		if(StringUtils.isEmpty(pn)||!NumberUtils.isNumber(pn)) {
			pn = "1";
		}
		try
		{
			pageNumber = Integer.valueOf((new StringBuilder(String.valueOf(pn))).toString());
			if (pageNumber.intValue() < 1)
				pageNumber = Integer.valueOf(1);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			pageNumber = Integer.valueOf(1);
		}
	}

	public Integer getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(Integer pageSize)
	{
		if (pageSize.intValue() < 1)
			pageSize = Integer.valueOf(1);
		else
		if (pageSize.intValue() > MAX_PAGE_SIZE.intValue())
			pageSize = MAX_PAGE_SIZE;
		this.pageSize = pageSize;
	}

	public Integer getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(Integer totalCount)
	{
		this.totalCount = totalCount;
	}

	public Integer getPageCount()
	{
		pageCount = Integer.valueOf(totalCount.intValue() / pageSize.intValue());
		if (totalCount.intValue() % pageSize.intValue() > 0)
			pageCount = Integer.valueOf(pageCount.intValue() + 1);
		if (pageCount.intValue() == 0)
			return Integer.valueOf(pageCount.intValue() + 1);
		else
			return pageCount;
	}

	public void setPageCount(Integer pageCount)
	{
		this.pageCount = pageCount;
	}

	public String getProperty()
	{
		return property;
	}

	public void setProperty(String property)
	{
		this.property = property;
	}

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	public OrderType getOrderType()
	{
		return orderType;
	}

	public void setOrderType(OrderType orderType)
	{
		this.orderType = orderType;
	}

	public List<T> getList()
	{
		return list;
	}

	public void setList(List<T> list)
	{
		this.list = list;
	}

}
