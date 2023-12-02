package com.imservices.im.bmm.dao.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.dao.BaseDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
@SuppressWarnings("all")
@Transactional
@Slf4j
public class BaseDAOImpl implements BaseDAO {

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    public BaseDAOImpl() {
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Object get(Class entityClass, Serializable id) {
        Assert.notNull(id, "id is required");
        return getSession().get(entityClass, id);
    }

    public Object load(Class entityClass, Serializable id) {
        Assert.notNull(id, "id is required");
        return getSession().load(entityClass, id);
    }

    public List get(Class entityClass, Serializable ids[]) {
        Assert.notEmpty(ids, "ids must not be empty");
        String hql = (new StringBuilder("from ")).append(entityClass.getName())
                .append(" as model where model.id in(:ids)").toString();
        return getSession().createQuery(hql).setParameterList("ids", ids)
                .list();
    }

    public Object get(Class entityClass, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        String hql = (new StringBuilder("from ")).append(entityClass.getName())
                .append(" as model where model.").append(propertyName)
                .append(" = '"+value+"'").toString();
        return getSession().createQuery(hql)
                .uniqueResult();
//        return getSession().createQuery(hql).setParameter(0, value)
//                .uniqueResult();
    }

    @Override
    public List getLike(Class entityClass, String propertyName, Object value) throws Exception {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        String hql = (new StringBuilder("from ")).append(entityClass.getName())
                .append(" as model where model.").append(propertyName)
                .append(" like '%"+value+"%'").toString();
        return getSession().createQuery(hql)
                .list();
    }

    public List getList(Class entityClass, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        String hql = (new StringBuilder("from ")).append(entityClass.getName())
                .append(" as model where model.").append(propertyName)
                .append(" = '"+value+"'").toString();
//        return getSession().createQuery(hql).setParameter(0, value).list();
        return getSession().createQuery(hql).list();
    }

    public List getAll(Class entityClass) {
        String hql = (new StringBuilder("from ")).append(entityClass.getName())
                .toString();
        return getSession().createQuery(hql).list();
    }

    public Long getTotalCount(Class entityClass) {
        String hql = (new StringBuilder("select count(*) from ")).append(
                entityClass.getName()).toString();
        return (Long) getSession().createQuery(hql).uniqueResult();
    }

    public boolean isUnique(Class entityClass, String propertyName,
                            Object oldValue, Object newValue) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(newValue, "newValue is required");
        if (newValue == oldValue || newValue.equals(oldValue))
            return true;
        if ((newValue instanceof String) && oldValue != null
                && StringUtils.equalsIgnoreCase((String) oldValue, (String) newValue))
            return true;
        Object object = get(entityClass, propertyName, newValue);
        return object == null;
    }

    public boolean isExist(Class entityClass, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        Object object = get(entityClass, propertyName, value);
        return object != null;
    }

    public Serializable save(Object entity) {
        Assert.notNull(entity, "entity is required");
        Serializable serializable = getSession().save(entity);
        flush();
        return serializable;
    }

    public void update(Object entity) {
        Session session = getSession();
        Assert.notNull(entity, "entity is required");
        session.saveOrUpdate(entity);
        flush();
    }

    public void delete(Object entity) {
        Assert.notNull(entity, "entity is required");
        getSession().delete(entity);
        flush();
    }

    public void delete(Class entityClass, Serializable id) {
        Assert.notNull(id, "id is required");
        Object entity = load(entityClass, id);
        getSession().delete(entity);
        flush();
    }

    public void delete(Class entityClass, Serializable ids[]) {
        Assert.notEmpty(ids, "ids must not be empty");
        Serializable aserializable[];
        int j = (aserializable = ids).length;
        for (int i = 0; i < j; i++) {
            Serializable id = aserializable[i];
            Object entity = load(entityClass, id);
            getSession().delete(entity);
        }
        flush();
    }

    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }

    public void evict(Object object) {
        Assert.notNull(object, "object is required");
        getSession().evict(object);
    }

    public Pager findByPager(Class entityClass, Pager pager) {
        if (pager == null)
            pager = new Pager();
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(entityClass);
        return findByPager(pager, detachedCriteria);
    }

    public Pager findByPager(Pager pager, DetachedCriteria detachedCriteria) {
        if (pager == null)
            pager = new Pager();
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String property = pager.getProperty();
        String keyword = pager.getKeyword();
        String orderBy = pager.getOrderBy();
        com.imservices.im.bmm.bean.Pager.OrderType orderType = pager.getOrderType();
        Criteria criteria = detachedCriteria
                .getExecutableCriteria(getSession());
        if (StringUtils.isNotEmpty(property) && StringUtils.isNotEmpty(keyword)) {
            String propertyString = "";
            if (property.contains(".")) {
                String propertyPrefix = StringUtils.substringBefore(property, ".");
                String propertySuffix = StringUtils.substringAfter(property, ".");
                criteria.createAlias(propertyPrefix, "model");
                propertyString = (new StringBuilder("model.")).append(propertySuffix).toString();
            } else {
                propertyString = property;
            }
            criteria.add(Restrictions.like(propertyString, (new StringBuilder("%"))
                    .append(keyword).append("%").toString()));
        }
        Integer totalCount = (Integer) criteria.setProjection(Projections.rowCount()).uniqueResult();
        criteria.setProjection(null);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        criteria.setFirstResult((pageNumber.intValue() - 1) * pageSize.intValue());
        criteria.setMaxResults(pageSize.intValue());
        if (StringUtils.isNotEmpty(orderBy) && orderType != null)
            if (orderType == com.imservices.im.bmm.bean.Pager.OrderType.asc) criteria.addOrder(Order.asc(orderBy));
            else criteria.addOrder(Order.desc(orderBy));
        pager.setTotalCount(totalCount);
        pager.setList(criteria.list());
        return pager;
    }

    public Pager findByPager(Class entityClass, String properties[], Object vals[], Pager pager) throws Exception {
        if (pager == null) pager = new Pager();
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql = createHql(entityClass, properties, vals, pager.getOrderBy(), pager.getOrderType());
        Query query = getSession().createQuery(hql);
        Query cquery = getSession().createQuery((new StringBuilder("SELECT COUNT(*) ")).append(hql).toString());
        ParameterValueSet(query, cquery, entityClass, properties, vals);
        Integer totalCount = Integer.valueOf(((Long) cquery.iterate().next()).intValue());
        query.setFirstResult((pageNumber.intValue() - 1) * pageSize.intValue());
        query.setMaxResults(pageSize.intValue());
        pager.setTotalCount(totalCount);
        pager.setList(query.list());
        return pager;
    }

    public List getList(Class entityClass, String properties[], Object vals[],
                        String orderBy, com.imservices.im.bmm.bean.Pager.OrderType orderType) throws Exception {
        Query query = getSession().createQuery(createHql(entityClass, properties, vals, orderBy, orderType));
        ParameterValueSet(query, entityClass, properties, vals);
        return query.list();
    }


    //参数值赋值
    private void ParameterValueSet(Query query, Class entityClass, String properties[], Object vals[]) throws Exception {
        for (int i = 0; properties != null && i < properties.length; i++) {
            properties[i] = properties[i].replaceAll("\\+", "").replaceAll("\\-", "");
            if (null != vals[i] && vals[i].getClass().isArray()) {
                Object objs[] = (Object[]) vals[i];
                for (int k = 0; k < objs.length; k++) {
                    //如果条件值为NULL或不为空则跳过参数赋值，因为值已在创建HQL时已设置好
                    if (null == objs[k] || BaseDAO.NOTNULL.equals(objs[k])) continue;
                    objs[k] = ValueConver(objs[k], properties[i].replaceAll(BaseDAO.OR, "").replaceAll(BaseDAO.LEFT_KH, "").replaceAll(BaseDAO.RIGHT_KH, ""), entityClass);
                    query.setParameter("p" + i + "_" + k, objs[k]);
                }
            } else {
                //如果条件值为NULL或不为空则跳过参数赋值，因为值已在创建HQL时已设置好
                if (null == vals[i] || BaseDAO.NOTNULL.equals(vals[i])) continue;
                vals[i] = ValueConver(vals[i], properties[i].replaceAll(BaseDAO.OR, "").replaceAll(BaseDAO.LEFT_KH, "").replaceAll(BaseDAO.RIGHT_KH, ""), entityClass);
                query.setParameter("p" + i, vals[i]);
                //
            }
        }
    }


    //参数值赋值
    private void ParameterValueSet(Query query1, Query query2, Class entityClass, String properties[], Object vals[]) throws Exception {
        for (int i = 0; properties != null && i < properties.length; i++) {
            if (null != vals[i] && vals[i].getClass().isArray()) {
                Object objs[] = (Object[]) vals[i];
                for (int k = 0; k < objs.length; k++) {
                    //如果条件值为NULL或不为空则跳过参数赋值，因为值已在创建HQL时已设置好
                    if (null == objs[k] || BaseDAO.NOTNULL.equals(objs[k])) continue;
                    objs[k] = ValueConver(objs[k], properties[i].replaceAll(BaseDAO.OR, "").replaceAll(BaseDAO.LEFT_KH, "").replaceAll(BaseDAO.RIGHT_KH, ""), entityClass);
                    query1.setParameter("p" + i + "_" + k, objs[k]);
                    query2.setParameter("p" + i + "_" + k, objs[k]);
                }
            } else {
                //如果条件值为NULL或不为空则跳过参数赋值，因为值已在创建HQL时已设置好
                if (null == vals[i] || BaseDAO.NOTNULL.equals(vals[i])) continue;
                vals[i] = ValueConver(vals[i], properties[i].replaceAll(BaseDAO.OR, "").replaceAll(BaseDAO.LEFT_KH, "").replaceAll(BaseDAO.RIGHT_KH, ""), entityClass);
                query1.setParameter("p" + i, vals[i]);
                query2.setParameter("p" + i, vals[i]);
            }
        }
    }


    //参数值转换
    private Object ValueConver(Object val, String propertie, Class entityClass) throws Exception {
        //把参数值 中的标志常量值清空
        if (val.toString().indexOf(BaseDAO.NOTEQUALS) == 0)
            val = val.toString().replaceFirst(BaseDAO.NOTEQUALS, "");
        if (val.toString().indexOf(BaseDAO.MoreThanOrEqualTo) == 0)
            val = val.toString().replaceFirst(BaseDAO.MoreThanOrEqualTo, "");
        if (val.toString().indexOf(BaseDAO.IsLessThanOrEqualTo) == 0)
            val = val.toString().replaceFirst(BaseDAO.IsLessThanOrEqualTo, "");
        if (val.toString().indexOf(BaseDAO.NotLike) == 0)
            val = val.toString().replaceFirst(BaseDAO.NotLike, "");
        Class typeClass = null;//参数所属类型
        if (propertie.indexOf(".") > 0) {
            //参数是关联类下的属性
            String temp = propertie;
            String rs[] = temp.split("\\.");
            try {
                typeClass = entityClass.getDeclaredField(rs[0]).getType().getDeclaredField(rs[1]).getType();
            } catch (NoSuchFieldException e) {
                typeClass = entityClass.getDeclaredField(rs[0]).getType().getSuperclass().getDeclaredField(rs[1]).getType();
            }
        } else {
            try {
                typeClass = entityClass.getDeclaredField(propertie).getType();
            } catch (NoSuchFieldException e) {
                typeClass = entityClass.getSuperclass().getDeclaredField(propertie).getType();
            }
        }
        //以下参数类型需要特殊转换处理，否则交给程序 自己判断转换容易出问题
        if (typeClass == Date.class) {
            //如果参数为日期类型
            if (val instanceof String) {
                if (val.toString().indexOf(":") > 0) {
                    //值 为日期+时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    val = sdf.parse(val.toString());
                } else {
                    //值仅 为日期
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    val = sdf.parse(val.toString());
                }
            }

        } else if (typeClass == Integer.class) {
            val = Integer.valueOf(val.toString());
        } else if (typeClass == Long.class) {
            val = Long.valueOf(val.toString());
        } else if (typeClass == Boolean.class) {
            val = Boolean.valueOf(val.toString());
        } else if (typeClass == Double.class) {
            val = Double.valueOf(val.toString());
        } else if (typeClass == Float.class) {
            val = Float.valueOf(val.toString());
        }
        return val;
    }


    //根据实体类和参数，值 等信息。生成更新hql
    protected static String createUpdateHql(Class entityClass, String properties[], Object vals[]) {
        StringBuilder hql = new StringBuilder("update ").append(entityClass.getName()).append(" ");//更新hql

        if (!ArrayUtils.isEmpty(properties)) {
            hql.append(" set ");
            for (int i = 0; properties != null && i < properties.length; i++) {
                hql.append(" ").append(properties[i].replaceAll("\\+", "").replaceAll("\\-", "")).append(" = ");
                if (properties[i].indexOf("+") >= 0) {
                    hql.append(properties[i].replaceAll("\\+", "")).append("+");
                } else if (properties[i].indexOf("-") >= 0) {
                    hql.append(properties[i].replaceAll("\\-", "")).append("-");
                }
                hql.append(":p").append(i);

                if (i < properties.length - 1) hql.append(",");
            }
        }
        return hql.toString();
    }


    //根据实体类和参数，值 等信息。生成查询hql
    protected static String createHql(Class entityClass, String properties[],
                                      Object vals[], String orderBy, com.imservices.im.bmm.bean.Pager.OrderType orderType) {
        String entityAlias = new StringBuilder(entityClass.getSimpleName().toUpperCase()).append("_alias").toString();//表别名
        StringBuilder hql = new StringBuilder("from ").append(entityClass.getName()).append(" as ").append(entityAlias).append(" ");//查询hql

        if (!ArrayUtils.isEmpty(properties)) {
            hql.append(" where ");
            for (int i = 0; properties != null && i < properties.length; i++) {

                if (null != vals[i] && vals[i].getClass().isArray()) {
                    //此参数值为数组
                    Object objs[] = (Object[]) vals[i];//对应的参数值
                    for (int k = 0; k < objs.length; k++) {


                        if (objs[k] == null || "".equals(objs[k].toString())) {
                            //为空查询
                            hql.append("(" + entityAlias).append(".").append(properties[i]).append(" is null ");
                            hql.append(" or ").append(entityAlias).append(".").append(properties[i]).append("='') ");
                            if (k < objs.length - 1) hql.append(" or ");
                        } else if (BaseDAO.NOTNULL.equals(objs[k].toString().trim())) {
                            //不为空查询
                            hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(" is not null");
                            hql.append(" and ").append(entityAlias).append(".").append(properties[i]).append("!='' ");
                            if (k < objs.length - 1) hql.append(" or ");
                        } else {
                            boolean flag = false;


                            if (objs[k].toString().indexOf("%") >= 0) {
                                //如果为模糊查询
                                if (objs[k].toString().indexOf(BaseDAO.NotLike) >= 0) {
                                    //not like查询
                                    hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(" not like :")
                                            .append("p").append(i + "_" + k);
                                } else {
                                    //like查询
                                    hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(" like :")
                                            .append("p").append(i + "_" + k);
                                }
                            } else if (objs[k].toString().indexOf(BaseDAO.NOTEQUALS) >= 0) {
                                //不相等查询
                                hql.append(" ").append(entityAlias).append(".").append(properties[i]).append("!=:")
                                        .append("p").append(i + "_" + k);
                            } else if (objs[k].toString().indexOf(BaseDAO.MoreThanOrEqualTo) >= 0) {
                                //大于或先玩查询
                                hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(">=:")
                                        .append("p").append(i + "_" + k);
                            } else if (objs[k].toString().indexOf(BaseDAO.IsLessThanOrEqualTo) >= 0) {
                                //小于或等于查询
                                hql.append(" ").append(entityAlias).append(".").append(properties[i]).append("<=:")
                                        .append("p").append(i + "_" + k);
                            } else {
                                //其它为等于查询
                                hql.append(" ").append(entityAlias).append(".").append(properties[i]).append("=:")
                                        .append("p").append(i + "_" + k);
                            }
                            if (k < objs.length - 1)
                                hql.append(" or ");
                        }
                    }
                } else {
                    entityAlias = entityAlias.replaceAll("\\(", "");
                    if (properties[i].indexOf(BaseDAO.LEFT_KH) >= 0) {
                        entityAlias = "(" + entityAlias;
                    }
//					
                    if (vals[i] == null || "".equals(vals[i].toString())) {
                        //为空查询
                        hql.append("(" + entityAlias).append(".").append(properties[i]).append(" is null ");
                        hql.append(" or ").append(entityAlias).append(".").append(properties[i]).append("='' )");
                    } else if (BaseDAO.NOTNULL.equals(vals[i].toString().trim())) {
                        //不为空查询
                        hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(" is not null");
                        hql.append(" and ").append(entityAlias).append(".").append(properties[i]).append("!='' ");
                    } else {
                        if (vals[i].toString().indexOf("%") >= 0) {
                            //如果为模糊查询
                            if (vals[i].toString().indexOf(BaseDAO.NotLike) >= 0) {
                                //not like查询
                                hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(" not like :")
                                        .append("p").append(i);
                            } else {
                                //like查询
                                hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(" like :")
                                        .append("p").append(i);
                            }
                        } else if (vals[i].toString().indexOf(BaseDAO.NOTEQUALS) >= 0) {
                            //不相等查询
                            hql.append(" ").append(entityAlias).append(".").append(properties[i]).append("!=:")
                                    .append("p").append(i);
                        } else if (vals[i].toString().indexOf(BaseDAO.MoreThanOrEqualTo) >= 0) {
                            //大于或先玩查询
                            hql.append(" ").append(entityAlias).append(".").append(properties[i]).append(">=:")
                                    .append("p").append(i);
                        } else if (vals[i].toString().indexOf(BaseDAO.IsLessThanOrEqualTo) >= 0) {
                            //小于或等于查询
                            hql.append(" ").append(entityAlias).append(".").append(properties[i]).append("<=:")
                                    .append("p").append(i);
                        } else {
                            //其它为等于查询
                            hql.append(" ").append(entityAlias).append(".").append(properties[i]).append("=:")
                                    .append("p").append(i);
                        }
                    }


                    if (properties[i].indexOf(BaseDAO.RIGHT_KH) >= 0) {
                        hql.append(" ) ");
                    }

                }
                if (i < properties.length - 1)
                    hql.append(properties[i].endsWith(BaseDAO.OR) ? " or " : " and ");


            }
        }
        //排序
        if (!StringUtils.isEmpty(orderBy) && orderType != null) {
            hql.append(" order by ").append(entityAlias).append(".").append(orderBy).append(" ").append(orderType);
        }

        if (hql.indexOf(BaseDAO.OR) > 0) {
            return hql.toString().replaceAll(BaseDAO.OR, "").replaceAll(BaseDAO.LEFT_KH, "").replaceAll(BaseDAO.RIGHT_KH, "");
        } else
            return hql.toString();
    }


    public Object getTotalCount(Class entityClass, String properties[], Object vals[]) throws Exception {
        Query query = getSession().createQuery((new StringBuilder("SELECT COUNT(*) ")).append(createHql(entityClass, properties, vals, null,
                null)).toString());
        ParameterValueSet(query, entityClass, properties, vals);
        return query.uniqueResult();
    }

    public Object getTotalCount(String hql) {
        Query query = getSession().createQuery(hql);
        return query.uniqueResult();
    }

    public Pager findByPager(String hql, Pager pager) {
        if (pager == null)
            pager = new Pager();
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        Query query = getSession().createQuery(hql);
        Query cquery = getSession().createQuery(
                (new StringBuilder("SELECT COUNT(*) ")).append(hql).toString());
        Integer totalCount = Integer.valueOf(((Long) cquery.iterate().next())
                .intValue());
        query.setFirstResult((pageNumber.intValue() - 1) * pageSize.intValue());
        query.setMaxResults(pageSize.intValue());
        pager.setTotalCount(totalCount);
        pager.setList(query.list());
        return pager;
    }

    public List getList(String hql) {
        Query query = getSession().createQuery(hql);
        return query.list();
    }

    public void delete(String hql) {
        getSession().createQuery(hql).executeUpdate();
        flush();
    }

    public void update(String hql) {
        getSession().createQuery(hql).executeUpdate();
        flush();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

//    @Override
//    @Transactional
//    public void update(Class entityClass, String[] ps, Object[] vs, String where) {
//        getSession().createQuery(createUpdateHql(entityClass, ps, vs) + " " + where);
////        Session session = getSession();
////        Transaction txn = session.beginTransaction();
////        Query query = session.createQuery(createUpdateHql(entityClass, ps, vs) + " " + where);
////        try {
////            ParameterValueSet(query, entityClass, ps, vs);
////        } catch (Exception e) {
////            log.error("", e);
////        }
////        query.executeUpdate();
////        txn.commit();
//    }

    @Override
    public Object sum(String hql) throws Exception {
        Query query = getSession().createQuery(hql);
        return query.uniqueResult();
    }

    public Object getIP(Class entityClass, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        String hql = (new StringBuilder("from ")).append(entityClass.getName())
                .append(" as model where model.").append(propertyName)
                .append(" = '"+value+"'")
                .append(" order by createDate desc").toString();
        return getSession().createQuery(hql).setMaxResults(1)
                .uniqueResult();
//        return getSession().createQuery(hql).setParameter(0, value)
//                .uniqueResult();
    }

    @Override
    public void callProcedure(String proc){
        getSession().createSQLQuery("{call "+proc+"()}").executeUpdate();
    }
}
