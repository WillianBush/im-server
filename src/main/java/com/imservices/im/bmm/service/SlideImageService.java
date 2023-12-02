package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.SlideImage;

public interface SlideImageService {

	public abstract Pager findByPager(String[] properties, Object[] vals, Pager Pager ) throws Exception;
	public abstract void delete(SlideImage si) throws Exception;
	public abstract void update(SlideImage si) throws Exception;
	public abstract void save(SlideImage si) throws Exception;
	public abstract SlideImage get(String id) throws Exception;
    
}
