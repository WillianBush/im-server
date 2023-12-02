package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.HomepageEntity;

import java.util.List;

public interface HomePageService {

    Pager findByPager(String[] properties, Object[] vals, Pager Pager) throws Exception;

    void delete(HomepageEntity o) throws Exception;

    void update(HomepageEntity o) throws Exception;


    void save(HomepageEntity o) throws Exception;

    HomepageEntity get(String id) throws Exception;

    void delete(String[] ids) throws Exception;

    List<HomepageEntity> getList(String[] ps, Object[] vs) throws Exception;

    List<HomepageEntity> getListAll() throws Exception;

    Object sum(String hql) throws Exception;

    void delete(String id) throws Exception;

    Long count(String[] ps, Object[] vs) throws Exception;

    HomepageEntity get(String p, String v) throws Exception;

}
