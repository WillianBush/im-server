package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Role;

import java.util.List;

public interface RoleService {

	public Role get(String id) throws Exception;
    public Role get(String propertyName, Object value) throws Exception;
    public List<Role> getList( String[] properties, Object[] vals) throws Exception;
	public Pager findByPager(String[] ps, Object[] vs, Pager pager) throws Exception;
	public void save(Role role) throws Exception;
	public void update(Role role) throws Exception;
	public Long count(String[] properties, Object[] values) throws Exception;
	public void delete(String[] ids) throws Exception;
	public void delete(String id) throws Exception;
	public void delete(Role r) throws Exception;
	public List<Role> getAll() throws Exception;
}
