package com.imservices.im.bmm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.Employee;
import com.imservices.im.bmm.entity.IpListEntity;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.EmployeeService;
import com.imservices.im.bmm.service.IpListService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.utils.web.WebUtilsWeb;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class IpListServiceImpl implements IpListService {


    private final BaseService<IpListEntity, Long> baseService;

    private final EmployeeService employeeService;

    private final MemberService memberService;


    @Override
    public Pager findByPager(String[] properties, Object[] vals, Pager Pager) throws Exception {
        return baseService.findByPager(IpListEntity.class, properties, vals, Pager);
    }

    @Override
    public void delete(IpListEntity o) throws Exception {
        baseService.delete(o);
    }

    @Override
    public void update(IpListEntity o) throws Exception {
        baseService.update(o);
    }

    @Override
    public void save(IpListEntity o) throws Exception {
        baseService.save(o);
    }

    @Override
    public IpListEntity get(Long id) throws Exception {
        return baseService.get(IpListEntity.class, id);
    }

    @Override
    public void delete(Long[] ids) throws Exception {
        baseService.delete(IpListEntity.class, ids);
    }

    @Override
    public List<IpListEntity> getList(String[] ps, Object[] vs) throws Exception {
        return baseService.getList(IpListEntity.class, ps, vs,"id", Pager.OrderType.desc);
    }

    @Override
    public Object sum(String hql) throws Exception {
        return baseService.sum(hql);
    }

    @Override
    public void delete(Long id) throws Exception {
        baseService.delete(IpListEntity.class, id);
    }

    @Override
    public Long count(String[] ps, Object[] vs) throws Exception {
        return baseService.getTotalCount(IpListEntity.class, ps, vs);
    }

    @Override
    public IpListEntity get(String p, String v) throws Exception {
        return baseService.get(IpListEntity.class, p, v);
    }

    @Override
    public boolean isInWhiteIp(String employeeId, HttpServletRequest request) throws Exception {
        List<IpListEntity> ipListEntities = this.getList(new String[]{"ip_address", "type"}, new Object[]{WebUtilsWeb.getIpAddr(request), "0"});
        return ipListEntities.size() >0;
    }

    @Override
    public boolean isInBlackIps(String memberId, HttpServletRequest request) throws Exception {
        Member member = memberService.get("memberId", memberId);
        if (member == null) {
            return false;
        }
        List<IpListEntity> ipListEntities = this.getList(new String[]{"ip_address", "type"}, new Object[]{WebUtilsWeb.getIpAddr(request), "1 "});
        return ipListEntities.size() > 0;
    }


    @Override
    public boolean isInBlackIps( HttpServletRequest request) throws Exception {
        List<IpListEntity> ipListEntities = this.getList(new String[]{"ip_address", "type"}, new Object[]{WebUtilsWeb.getIpAddr(request), "1 "});
        return ipListEntities.size() > 0;
    }

    @Override
    public boolean enableRequest(Member member,HttpServletRequest request){
        if (member == null) {
            return false;
        }
        try {
            Employee employee = employeeService.getByMemberId(member.getMemberId());
            if (employee == null) {
                // 普通用户看黑名单
                return !this.isInBlackIps(member.getMemberId(),request);
            }else {
                // 特权用户看白名单
//                return this.isInWhiteIp(member.getMemberId(),request);
                return true;
            }
        }catch (Exception e) {
            log.error(",member:{}", JSONObject.toJSONString(member),e);
            return  true;
        }

    }
}
