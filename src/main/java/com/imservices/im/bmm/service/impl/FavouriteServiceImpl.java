package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Favourite;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class FavouriteServiceImpl implements FavouriteService {

	@Autowired
	private BaseService baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Favourite.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(Favourite o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(Favourite o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(Favourite o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public Favourite get(String id) throws Exception {
		return (Favourite) baseService.get(Favourite.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Favourite.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Favourite> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(Favourite.class, ps, vs);
	}


	@Override
	@Transactional(readOnly = true)
	public Object sum(String hql) throws Exception {
		return baseService.sum(hql);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Favourite.class, id);
	}


}
