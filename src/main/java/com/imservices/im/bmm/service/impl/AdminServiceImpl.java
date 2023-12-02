package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Admin;
import com.imservices.im.bmm.service.AdminService;
import com.imservices.im.bmm.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

	@Autowired
	private BaseService<Admin, String> baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Admin get(String id) throws Exception {
		return baseService.get(Admin.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public Admin get(String propertyName, Object value) throws Exception {
		return baseService.get(Admin.class,propertyName ,value);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Admin> getList(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getList(Admin.class, properties, vals);
	}

	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] ps, Object[] vs, Pager pager)
			throws Exception {
		return baseService.findByPager(Admin.class, ps, vs, pager);
	}

	@Override
	@Transactional
	public void save(Admin admin) throws Exception {
		baseService.save(admin);
	}

	@Override
	@Transactional
	public void update(Admin admin) throws Exception {
		baseService.update(admin);
	}
	@Override
	@Transactional(readOnly = true)
	public Long count(String[] properties, Object[] values) throws Exception {
		return baseService.getTotalCount(Admin.class, properties, values);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Admin.class, ids);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Admin.class, id); 
	}
}
