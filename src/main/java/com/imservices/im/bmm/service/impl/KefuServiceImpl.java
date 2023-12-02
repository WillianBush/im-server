package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Kefu;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.KefuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class KefuServiceImpl implements KefuService {

	@Autowired
	private BaseService baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Kefu.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(Kefu o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(Kefu o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(Kefu o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public Kefu get(String id) throws Exception {
		return (Kefu) baseService.get(Kefu.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Kefu.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Kefu> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(Kefu.class, ps, vs);
	}


	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Kefu.class, id);
	}


}
