package com.imservices.im.bmm.dao;

import com.imservices.im.bmm.bean.Pager;
import org.hibernate.criterion.DetachedCriteria;

import java.io.Serializable;
import java.util.List;

/**
 * DAO接口规范
 * @author 开云团队
 *
 * @param <T>
 * @param <PK>
 */
@SuppressWarnings("all")
public abstract interface BaseDAO<T, PK extends Serializable> {

	public static final String NOTNULL = "#NOTNULL#";//不为空
	public static final String NOTEQUALS = "#NOTEQUALS#";//不相等
	public static final String MoreThanOrEqualTo = "#MoreThanOrEqualTo#";//大于等于
	public static final String IsLessThanOrEqualTo = "#IsLessThanOrEqualTo#";//小于等于
	public static final String OR = "#or#";//或
	public static final String NotLike = "#NotLike#";//反模糊查询   
	public static final String LEFT_KH = "#LEFT_KH#";//左括号
	public static final String RIGHT_KH = "#RIGHT_KH#";//右括号 
	
	//根据ID获取某实体
	public abstract T get(Class entityClass, PK PK)  throws Exception;
	//根据ID获取某实体
	public abstract T load(Class entityClass, PK PK)  throws Exception;
	//根据ID数组获取某实体列表
	public abstract List<T> get(Class entityClass, PK[] paramArrayOfPK)  throws Exception;
	//根据参数值获取某实体
	public abstract T get(Class entityClass, String propertyName,Object value)  throws Exception;

	public abstract List<T> getLike(Class entityClass, String propertyName,Object value)  throws Exception;

	//根据参数值获取某实体列表
	public abstract List<T> getList(Class entityClass, String propertyName,Object value)  throws Exception;
	//获取某实体列表
	public abstract List<T> getAll(Class entityClass) throws Exception;
	//获取某实体记录数
	public abstract Long getTotalCount(Class entityClass)  throws Exception;
	//查询某实体更换参数值后是否唯一
	public abstract boolean isUnique(Class entityClass, String propertyName,Object oldValue, Object newValue)  throws Exception;
	//根据参数与值查询某实体是否存在
	public abstract boolean isExist(Class entityClass, String propertyName,Object value) throws Exception;
	//插入
	public abstract PK save(T paramT)  throws Exception;
	//更新
//	public abstract void update(Class entityClass, String[] ps,Object[] vs,String where) ;
	//更新
	public abstract void update(T paramT)  throws Exception;
	//删除
	public abstract void delete(T paramT)  throws Exception;
	//删除
	public abstract void delete(Class entityClass, PK pk)  throws Exception;
	//删除
	public abstract void delete(Class entityClass, PK[] pks)  throws Exception;
	public abstract void flush()  throws Exception;
	public abstract void clear()  throws Exception;
	public abstract void evict(Object object)  throws Exception;
	//根据pager对象获取信息
	public abstract Pager findByPager(Class entityClass, Pager pager)  throws Exception;
	public abstract Pager findByPager(Pager pager,DetachedCriteria detachedCriteria)  throws Exception;
	//根据pager对象和参数值获取信息
	public abstract Pager findByPager(Class entityClass,String[] properties, Object[] vals,Pager pager)  throws Exception;
	public abstract List<T> getList(Class entityClass,String[] properties, Object[] vals,String orderBy, Pager.OrderType OrderType)  throws Exception;
	public abstract Object getTotalCount(Class entityClass,String[] properties, Object[] vals)  throws Exception;
	public abstract Object getTotalCount(String hql)  throws Exception;
	public abstract Pager findByPager(String hql, Pager pager)  throws Exception;
	public abstract List<Object> getList(String hql)  throws Exception;
	public abstract void delete(String hql)  throws Exception;
	public abstract void update(String hql)  throws Exception;
	public abstract Object sum(String hql) throws Exception;
	public abstract Object getIP(Class entityClass, String propertyName,Object value)  throws Exception;
    /**
     * 执行存储过程
     */
    public abstract void callProcedure(String proc);
}