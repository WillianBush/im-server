package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Notice;

import java.util.List;

public interface NoticeService {  

	public abstract Pager findByPager(String[] properties, Object[] vals, Pager Pager ) throws Exception;
	public abstract void delete(Notice o) throws Exception;
	public abstract void update(Notice o) throws Exception;
	public abstract void save(Notice o) throws Exception;
	public abstract Notice get(String id) throws Exception;
	public abstract void delete(String[] ids) throws Exception;
	public abstract List<Notice> getList(String[] ps, Object[] vs) throws Exception;
	public abstract void delete(String id) throws Exception;
    
}
