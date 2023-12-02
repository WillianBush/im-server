package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Favourite;

import java.util.List;

public interface FavouriteService {  

	public abstract Pager findByPager(String[] properties, Object[] vals, Pager Pager ) throws Exception;
	public abstract void delete(Favourite o) throws Exception;
	public abstract void update(Favourite o) throws Exception;
	public abstract void save(Favourite o) throws Exception;
	public abstract Favourite get(String id) throws Exception;
	public abstract void delete(String[] ids) throws Exception;
	public abstract List<Favourite> getList(String[] ps, Object[] vs) throws Exception;
	public abstract Object sum(String hql) throws Exception;
	public abstract void delete(String id) throws Exception;
    
}
