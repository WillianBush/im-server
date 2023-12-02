package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.dao.BaseDAO;
import com.imservices.im.bmm.entity.BaseEntity;
import com.imservices.im.bmm.service.BaseService;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
@Transactional
@Lazy
public class BaseServiceImpl<T, PK extends Serializable> implements
		BaseService<T, PK> {

	@Resource
	public BaseDAO<T, PK> baseDao;

	
	public T get(Class entityClass, PK id) throws Exception {
		Object t = this.baseDao.get(entityClass, id);
		return (T) t;
	}

	public T load(Class entityClass, PK id) throws Exception {
		Object t = this.baseDao.load(entityClass, id);
		return (T) t;
	}

	public List<T> get(Class entityClass, PK[] ids) throws Exception {
		List list = this.baseDao.get(entityClass, ids);
		return list;
	}

	public T get(Class entityClass, String propertyName, Object value)
			throws Exception {
		Object t = this.baseDao.get(entityClass, propertyName, value);
		return (T) t;
	}

	public List<T> getLike(Class entityClass, String propertyName, Object value)
			throws Exception {
		return this.baseDao.getLike(entityClass, propertyName, value);
	}

	public List<T> getList(Class entityClass, String propertyName, Object value)
			throws Exception {
		List list = this.baseDao.getList(entityClass, propertyName, value);
		return list;
	}

	public List<T> getAll(Class entityClass) throws Exception {
		List list = this.baseDao.getAll(entityClass);
		return list;
	}

	public Long getTotalCount(Class entityClass) throws Exception {
		return this.baseDao.getTotalCount(entityClass);
	}

	public boolean isUnique(Class entityClass, String propertyName,
			Object oldValue, Object newValue) throws Exception {
		return this.baseDao.isUnique(entityClass, propertyName, oldValue,
				newValue);
	}

	public boolean isExist(Class entityClass, String propertyName, Object value)
			throws Exception {
		return this.baseDao.isExist(entityClass, propertyName, value);
	}

	@Transactional
	public PK save(T entity) throws Exception {
		return this.baseDao.save(entity);
	}

	public void update(T entity) throws Exception {
		if ((entity instanceof BaseEntity)) {
			((BaseEntity) entity).setModifyDate(new Date());
		}
		this.baseDao.update(entity);
	}

	public void delete(T entity) throws Exception {
		this.baseDao.delete(entity);
	}
	@Transactional
	public void delete(Class entityClass, PK id) throws Exception {
		this.baseDao.delete(entityClass, id);
	}

	@Transactional
	public void delete(Class entityClass, PK[] ids) throws Exception {
		this.baseDao.delete(entityClass, ids);
	}

	public void flush() throws Exception {
		this.baseDao.flush();
	}

	public void clear() throws Exception {
		this.baseDao.clear();
	}

	public void evict(Object object) throws Exception {
		this.baseDao.evict(object);
	}

	public Pager findByPager(Class entityClass, Pager pager) throws Exception {
		pager = this.baseDao.findByPager(entityClass, pager);
		return pager;
	}

	public Pager findByPager(Class entityClass, Pager pager,
			DetachedCriteria detachedCriteria) throws Exception {
		pager = this.baseDao.findByPager(pager, detachedCriteria);
		return pager;
	}

	public Pager findByPager(Class entityClass, String[] properties,
			Object[] vals, Pager pager) throws Exception {
		pager = this.baseDao.findByPager(entityClass, properties, vals, pager);
		return pager;
	}

	public List<T> getList(Class entityClass, String[] properties,
			Object[] vals, String orderBy, Pager.OrderType orderType)
			throws Exception {
		List list = this.baseDao.getList(entityClass, properties, vals,
				orderBy, orderType);
		return list;
	}

	public List<T> getList(Class entityClass, String[] properties, Object[] vals)
			throws Exception {
		List list = getList(entityClass, properties, vals, "createDate",
				Pager.OrderType.desc);
		return list;
	}

	public Long getTotalCount(Class entityClass, String[] properties,
			Object[] vals) throws Exception {
		return (Long) this.baseDao.getTotalCount(entityClass, properties, vals);
	}

	public Object getTotalCount(String hql) throws Exception {
		return this.baseDao.getTotalCount(hql);
	}

	public Pager findByPager(Class entityClass, String hql, Pager pager)
			throws Exception {
		pager = this.baseDao.findByPager(hql, pager);
		return pager;
	}

	public List<Object> getList(String hql, Class entityClass) throws Exception{
		List list = this.baseDao.getList(hql);
		return list;
	}

	public void delete(String hql) throws Exception {
		this.baseDao.delete(hql);
	}

	public void update(String hql) throws Exception {
		this.baseDao.update(hql);
	}

	public Object sum(String hql) throws Exception {
		return this.baseDao.sum(hql);
	}

	public T getIP(Class entityClass, String propertyName, Object value)
			throws Exception {
		Object t = this.baseDao.getIP(entityClass, propertyName, value);
		return (T) t;
	}

	@Override
	public void callProcedure(String proc){
		this.baseDao.callProcedure(proc);
	}

}