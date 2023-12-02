package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Shiming;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.ShimingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class ShimingServiceImpl implements ShimingService {

	@Autowired
	private BaseService baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Shiming.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(Shiming o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(Shiming o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(Shiming o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public Shiming get(String id) throws Exception {
		return (Shiming) baseService.get(Shiming.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Shiming.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Shiming> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(Shiming.class, ps, vs);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Shiming.class, id);
	}

	@Override
	@Transactional
	public Shiming get(String p, Object v) throws Exception {
		return (Shiming) baseService.get(Shiming.class, p, v);
	}


}
