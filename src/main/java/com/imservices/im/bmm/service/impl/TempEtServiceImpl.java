package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.entity.TempEt;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.TempEtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("all")
public class TempEtServiceImpl implements TempEtService {

	@Autowired
	private BaseService baseService;

	@Override
	@Transactional
	public void save(TempEt o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional
	public void delete(TempEt o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void delete(String hql) throws Exception {
		baseService.delete(hql);
	}

	@Override
	@Transactional
	public void update(TempEt o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional(readOnly = true)
	public TempEt get(String id) throws Exception {
		return (TempEt) baseService.get(TempEt.class, id);
	}
	

}
