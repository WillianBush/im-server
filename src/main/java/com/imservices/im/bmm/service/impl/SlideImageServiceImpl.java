package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.SlideImage;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.SlideImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlideImageServiceImpl implements SlideImageService {

	@Autowired
	private BaseService<SlideImage, String> baseService;
	
	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(SlideImage.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(SlideImage si) throws Exception {
		baseService.delete(si);
	}

	@Override
	@Transactional
	public void update(SlideImage si) throws Exception {
		baseService.update(si);
	}

	@Override
	@Transactional
	public void save(SlideImage si) throws Exception {
		baseService.save(si);
	}

	@Override
	@Transactional(readOnly = true)
	public SlideImage get(String id) throws Exception {
		return baseService.get(SlideImage.class, id);
	}

}
