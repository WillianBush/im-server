package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.IpListEntity;
import com.imservices.im.bmm.entity.Member;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IpListService {

    Pager findByPager(String[] properties, Object[] vals, Pager Pager) throws Exception;

    void delete(IpListEntity o) throws Exception;

    void update(IpListEntity o) throws Exception;


    void save(IpListEntity o) throws Exception;

    IpListEntity get(Long id) throws Exception;

    void delete(Long[] ids) throws Exception;

    List<IpListEntity> getList(String[] ps, Object[] vs) throws Exception;

    Object sum(String hql) throws Exception;

    void delete(Long id) throws Exception;

    Long count(String[] ps, Object[] vs) throws Exception;

    IpListEntity get(String p, String v) throws Exception;

    /**
     *
     * @param employeeId
     * @param request
     * @return  true 在白名单; false ip 未加白
     * @throws Exception
     */
    boolean isInWhiteIp(String employeeId, HttpServletRequest request) throws Exception;

    /**
     *
     * @param memberId
     * @param request
     * @return  true ip已经拉黑; false ip未拉黑
     * @throws Exception
     */
    boolean isInBlackIps(String memberId,HttpServletRequest request) throws Exception;


    boolean isInBlackIps(HttpServletRequest request) throws Exception;


    /**
     *
     * @param member
     * @param request
     * @return  true 可以访问
     */
    boolean enableRequest(Member member, HttpServletRequest request);

}
