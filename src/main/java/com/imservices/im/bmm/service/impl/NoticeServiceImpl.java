package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Notice;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private BaseService baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(Notice.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(Notice o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(Notice o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(Notice o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public Notice get(String id) throws Exception {
		return (Notice) baseService.get(Notice.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(Notice.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Notice> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(Notice.class, ps, vs);
	}


	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(Notice.class, id);
	}


}
