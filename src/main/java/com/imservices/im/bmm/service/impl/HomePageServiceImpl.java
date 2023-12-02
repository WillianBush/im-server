package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.HomepageEntity;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.HomePageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HomePageServiceImpl implements HomePageService {

    private final BaseService<HomepageEntity, String> baseService;


    @Override
    public Pager findByPager(String[] properties, Object[] vals, Pager Pager) throws Exception {
        return baseService.findByPager(HomepageEntity.class, properties, vals, Pager);
    }

    @Override
    public void delete(HomepageEntity o) throws Exception {
        baseService.delete(o);
    }

    @Override
    public void update(HomepageEntity o) throws Exception {
        baseService.update(o);
    }

    @Override
    public void save(HomepageEntity o) throws Exception {
        baseService.save(o);
    }

    @Override
    public HomepageEntity get(String id) throws Exception {
        return baseService.get(HomepageEntity.class, id);
    }

    @Override
    public void delete(String[] ids) throws Exception {
        baseService.delete(HomepageEntity.class,ids);
    }

    @Override
    public List<HomepageEntity> getList(String[] ps, Object[] vs) throws Exception {
        return baseService.getList(HomepageEntity.class, ps, vs);
    }

    @Override
    public List<HomepageEntity> getListAll() throws Exception {
        return baseService.getAll(HomepageEntity.class);
    }

    @Override
    public Object sum(String hql) throws Exception {
        return baseService.sum(hql);
    }

    @Override
    public void delete(String id) throws Exception {
        baseService.delete(HomepageEntity.class, id);
    }

    @Override
    public Long count(String[] ps, Object[] vs) throws Exception {
        return baseService.getTotalCount(HomepageEntity.class, ps, vs);
    }

    @Override
    public HomepageEntity get(String p, String v) throws Exception {
        return baseService.get(HomepageEntity.class, p, v);
    }
}
