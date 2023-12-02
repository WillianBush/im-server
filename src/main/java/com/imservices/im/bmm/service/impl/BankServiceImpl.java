package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Bank;
import com.imservices.im.bmm.service.BankService;
import com.imservices.im.bmm.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class BankServiceImpl implements BankService {

	@Autowired
	private BaseService baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Bank.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(Bank o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(Bank o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(Bank o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public Bank get(String id) throws Exception {
		return (Bank) baseService.get(Bank.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Bank.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Bank> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(Bank.class, ps, vs);
	}

//	@Override
//	@Transactional
//	public void update(String[] ps, Object[] vs, String where) throws Exception {
//		baseService.update(Bank.class, ps, vs, where);
//	}

	@Override
	@Transactional(readOnly = true)
	public Object sum(String hql) throws Exception {
		return baseService.sum(hql);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Bank.class, id);
	}


}
