package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.IndependentRoomUserPrice;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.IRUPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 *
 */
@Service
@SuppressWarnings("all")
public class IRUPServiceImpl implements IRUPService {
	
	@Autowired
	private BaseService baseService;
	
 
	
	
	@Override
	@Transactional(readOnly = true)
	public IndependentRoomUserPrice get(String id) throws Exception {
		return (IndependentRoomUserPrice) baseService.get(IndependentRoomUserPrice.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public IndependentRoomUserPrice get(String propertyName, Object value) throws Exception {
		return (IndependentRoomUserPrice) baseService.get(IndependentRoomUserPrice.class,propertyName ,value);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndependentRoomUserPrice> getList(String[] properties, Object[] vals)
			throws Exception {
		return baseService.getList(IndependentRoomUserPrice.class, properties, vals);
	}

	@Override
	@Transactional
	public void update(IndependentRoomUserPrice irup) throws Exception  {
		baseService.update(irup);
	}

	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(IndependentRoomUserPrice.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(IndependentRoomUserPrice.class, id);
	}
 
	@Override
	@Transactional(readOnly = true)
	public Long count(String[] properties, Object[] values) throws Exception {
		return baseService.getTotalCount(IndependentRoomUserPrice.class, properties, values);
	}
 

	@Override
	@Transactional
	public void save(IndependentRoomUserPrice irup) throws Exception {
		baseService.save(irup); 
	}

	@Override
	@Transactional
	public void delete(IndependentRoomUserPrice irup) throws Exception {
		baseService.delete(irup);
	}


	@Override 
	@Transactional
	public void moneyUp(String uid,String roomid, double money) throws Exception {
		baseService.update("update IndependentRoomUserPrice as u set u.price = u.price+"+money +" where u.user_uuid='"+uid+"' and u.room_uuid='"+roomid+"' ");
	}  
	@Override
	@Transactional
	public void moneyDown(String uid,String roomid, double money) throws Exception {
		baseService.update("update IndependentRoomUserPrice as u set u.price = u.price-"+Math.abs(money) +" where u.user_uuid='"+uid+"' and u.room_uuid='"+roomid+"' ");
	}


	@Override
	@Transactional
	public void deleteByHql(String hql) throws Exception {
		baseService.delete(hql);
	}

	
	
}
