package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Role;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private BaseService<Role, String> baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Role get(String id) throws Exception {
		return baseService.get(Role.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public Role get(String propertyName, Object value) throws Exception {
		return baseService.get(Role.class,propertyName ,value);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Role> getList(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getList(Role.class, properties, vals);
	}

	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] ps, Object[] vs, Pager pager)
			throws Exception {
		return baseService.findByPager(Role.class, ps, vs, pager);
	}

	@Override
	@Transactional
	public void save(Role role) throws Exception {
		baseService.save(role);
	}

	@Override
	@Transactional
	public void update(Role role) throws Exception {
		baseService.update(role);
	}
	@Override
	@Transactional(readOnly = true)
	public Long count(String[] properties, Object[] values) throws Exception {
		return baseService.getTotalCount(Role.class, properties, values);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Role.class, ids);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Role.class, id); 
	}

	@Override
	@Transactional
	public void delete(Role r) throws Exception {
		baseService.delete(r);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Role> getAll() throws Exception {
		return baseService.getAll(Role.class);
	}
}
