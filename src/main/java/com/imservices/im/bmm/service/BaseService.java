package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("all")
public abstract interface BaseService<T, PK extends Serializable> {
  //根据ID获取某实体
  public abstract T get(Class entityClass, PK pk ) throws Exception;
  //根据ID获取某实体
  public abstract T load(Class entityClass, PK PK) throws Exception;
  //根据ID数组获取某实体列表
  public abstract List<T> get(Class entityClass, PK[] pks ) throws Exception;
  //根据参数值获取某实体
  public abstract T get(Class entityClass, String propertyName, Object value) throws Exception;

  public abstract List<T> getLike(Class entityClass, String propertyName, Object value) throws Exception;

  //根据参数值获取某实体列表
  public abstract List<T> getList(Class entityClass, String propertyName, Object value) throws Exception;
  public abstract List<T> getList(Class entityClass, String[] properties, Object[] vals, String orderBy, Pager.OrderType orderType ) throws Exception;
  public abstract List<T> getList(Class entityClass, String[] properties, Object[] vals) throws Exception;
  //获取某实体列表
  public abstract List<T> getAll(Class entityClass) throws Exception;
  //获取某实体记录数
  public abstract Long getTotalCount(Class entityClass) throws Exception;
  //查询某实体更换参数值后是否唯一
  public abstract boolean isUnique(Class entityClass, String propertyName, Object oldValue, Object newValue) throws Exception;
  //根据参数与值查询某实体是否存在
  public abstract boolean isExist(Class entityClass, String propertyName, Object value) throws Exception;
  //插入
  public abstract PK save(T T) throws Exception;
  //更新
  public abstract void update(T T) throws Exception;
  //更新
  public abstract void update(String hql) throws Exception;
  //删除
  @Transactional
  public abstract void delete(T T) throws Exception;
  //删除
  public abstract void delete(String hql) throws Exception;
  //删除
  public abstract void delete(Class entityClass, PK PK) throws Exception;
  //删除
  public abstract void delete(Class entityClass, PK[] pks) throws Exception;
  public abstract void flush() throws Exception;
  public abstract void clear() throws Exception;
  public abstract void evict(Object Object) throws Exception;
  //根据pager对象获取信息
  public abstract Pager findByPager(Class entityClass, Pager Pager ) throws Exception;
  //根据pager对象和参数值获取信息
  public abstract Pager findByPager(Class entityClass, String[] properties, Object[] vals, Pager Pager ) throws Exception;
  public abstract Pager findByPager(Class entityClass, Pager Pager, DetachedCriteria DetachedCriteria ) throws Exception;
  public abstract Long getTotalCount(Class entityClass, String[] properties, Object[] vals) throws Exception;
  public abstract Object getTotalCount(String hql) throws Exception;
  public abstract Pager findByPager(Class entityClass, String hql, Pager Pager ) throws Exception;
  public abstract List<Object> getList(String hql, Class entityClass ) throws Exception;
  public abstract Object sum(String hql) throws Exception;
  public abstract T getIP(Class entityClass, String propertyName, Object value) throws Exception;

  /***
   * 执行存储过程
   * @param proc
   */
  public void callProcedure(String proc);
}