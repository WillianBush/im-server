package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.FaxianSite;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.FaxianSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class FaxianSiteServiceImpl implements FaxianSiteService {

	@Autowired
	private BaseService baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(FaxianSite.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(FaxianSite o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(FaxianSite o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(FaxianSite o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public FaxianSite get(String id) throws Exception {
		return (FaxianSite) baseService.get(FaxianSite.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(FaxianSite.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FaxianSite> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(FaxianSite.class, ps, vs);
	}


	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(FaxianSite.class, id);
	}


}
