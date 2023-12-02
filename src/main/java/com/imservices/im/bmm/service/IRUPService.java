package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.IndependentRoomUserPrice;

import java.util.List;

public interface IRUPService {
	

	public IndependentRoomUserPrice get(String id) throws Exception;
    public IndependentRoomUserPrice get(String propertyName, Object value) throws Exception;
    public List<IndependentRoomUserPrice> getList( String[] properties, Object[] vals) throws Exception;
    public void update(IndependentRoomUserPrice irup) throws Exception ;
	public Pager findByPager(String[] properties, Object[] vals, Pager pager) throws Exception ;
	public void delete(String id)  throws Exception;
	public Long count(String[] properties, Object[] values) throws Exception;
	public void save(IndependentRoomUserPrice irup) throws Exception;
	public void delete(IndependentRoomUserPrice irup) throws Exception;
	public void moneyUp(String uid,String roomid, double money) throws Exception;
	public void moneyDown(String uid,String roomid, double money) throws Exception;
	public void deleteByHql(String hql) throws Exception;
}
 