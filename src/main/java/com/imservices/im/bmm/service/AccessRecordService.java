package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.AccessRecordBean;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.AccessRecord;

import java.util.List;

public interface AccessRecordService {

    public List<AccessRecord> getList( String[] properties, Object[] vals) throws Exception;
    public void update(AccessRecord ar) throws Exception ;
	public Pager<AccessRecord> findByPager(String[] properties, Object[] vals, Pager<AccessRecord> pager) throws Exception ;
	public void delete(String id)  throws Exception;
	public Long count(String[] properties, Object[] values) throws Exception;
	public void save(AccessRecord ar) throws Exception;
	public void delete(AccessRecord ar) throws Exception;
	public void updateHeadpic(String eid, String headpic) throws Exception;
	public void updateByEid(String eid, String[] ps, String[] vs) throws Exception;
	public AccessRecord get(String id) throws Exception;
	public void deleteByHql(String hql) throws Exception;

	AccessRecordBean getAccessRecordBean(AccessRecord e );
    
}
 