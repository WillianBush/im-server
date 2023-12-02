package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Admin;

import java.util.List;

public interface AdminService {

	public Admin get(String id) throws Exception;
    public Admin get(String propertyName, Object value) throws Exception;
    public List<Admin> getList( String[] properties, Object[] vals) throws Exception;
	public Pager findByPager(String[] ps, Object[] vs, Pager pager) throws Exception;
	public void save(Admin admin) throws Exception;
	public void update(Admin admin) throws Exception;
	public Long count(String[] properties, Object[] values) throws Exception;
	public void delete(String[] ids) throws Exception;
	public void delete(String id) throws Exception;
}
