package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Shiming;

import java.util.List;

public interface ShimingService {  

	public abstract Pager findByPager(String[] properties, Object[] vals, Pager Pager ) throws Exception;
	public abstract void delete(Shiming o) throws Exception;
	public abstract void update(Shiming o) throws Exception;
	public abstract void save(Shiming o) throws Exception;
	public abstract Shiming get(String id) throws Exception;
	public abstract void delete(String[] ids) throws Exception;
	public abstract List<Shiming> getList(String[] ps, Object[] vs) throws Exception;
	public abstract void delete(String id) throws Exception;
	public abstract Shiming get(String p, Object v) throws Exception;
    
}
