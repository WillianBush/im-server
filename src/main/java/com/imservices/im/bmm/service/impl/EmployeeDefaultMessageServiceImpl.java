package com.imservices.im.bmm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Employee;
import com.imservices.im.bmm.entity.EmployeeDefaultMessage;
import com.imservices.im.bmm.service.BaseService;
import com.imservices.im.bmm.service.EmployeeDefaultMessageService;
import com.imservices.im.bmm.service.EmployeeService;
import com.imservices.im.bmm.utils.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("all")
public class EmployeeDefaultMessageServiceImpl implements EmployeeDefaultMessageService {

	private final BaseService baseService;

	private final EmployeeService employeeService;

	private final RedisService redisService;

	@Autowired
	public EmployeeDefaultMessageServiceImpl(BaseService baseService, EmployeeService employeeService, RedisService redisService) {
		this.baseService = baseService;
		this.employeeService = employeeService;
		this.redisService = redisService;
	}

	@Override
	@Transactional(readOnly = true)
	public Pager findByPager(String[] properties, Object[] vals, Pager Pager)
			throws Exception {
		return baseService.findByPager(EmployeeDefaultMessage.class, properties, vals, Pager);
	}

	@Override
	@Transactional
	public void delete(EmployeeDefaultMessage o) throws Exception {
		baseService.delete(o);
	}

	@Override
	@Transactional
	public void update(EmployeeDefaultMessage o) throws Exception {
		baseService.update(o);
	}

	@Override
	@Transactional
	public void save(EmployeeDefaultMessage o) throws Exception {
		baseService.save(o);
	}

	@Override
	@Transactional(readOnly = true)
	public EmployeeDefaultMessage get(String id) throws Exception {
		return (EmployeeDefaultMessage) baseService.get(EmployeeDefaultMessage.class, id);
	}

	@Override
	@Transactional
	public void delete(String[] ids) throws Exception {
		baseService.delete(EmployeeDefaultMessage.class,ids);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeDefaultMessage> getList(String[] ps, Object[] vs) throws Exception {
		return baseService.getList(EmployeeDefaultMessage.class, ps, vs);
	}

//	@Override
//	@Transactional
//	public void update(String[] ps, Object[] vs, String where) throws Exception {
//		baseService.update(EmployeeDefaultMessage.class, ps, vs, where);
//	}

	@Override
	@Transactional(readOnly = true)
	public Object sum(String hql) throws Exception {
		return baseService.sum(hql);
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		baseService.delete(EmployeeDefaultMessage.class, id);
	}

	@Override
	@Transactional
	public Long count(String[] ps, Object[] vs) throws Exception {
		return baseService.getTotalCount(EmployeeDefaultMessage.class, ps, vs);
	}

	@Override
	@Transactional
	public EmployeeDefaultMessage get(String p, String v) throws Exception {
		return (EmployeeDefaultMessage) baseService.get(EmployeeDefaultMessage.class, p, v);
	}

	@Override
	public List<EmployeeDefaultMessage> getMyHelloMessage(String memberId) throws Exception {
		Employee employee  = employeeService.get("member_id",memberId);
		if (employee == null) {
			return null;
		}
		List<EmployeeDefaultMessage> dataList = redisService.getJsonListFromJsonString(MemberConstant.REDIS_EMPLOYEE_DEFAULT_MESSAGE +employee.getId(),EmployeeDefaultMessage.class);
		if (dataList == null || dataList.isEmpty()) {
			dataList = baseService.getList(EmployeeDefaultMessage.class,"employee_id", employee.id);
			if (!dataList.isEmpty()) {
//				for (EmployeeDefaultMessage employeeDefaultMessage : dataList) {
//					redisService.setDays(MemberConstant.REDIS_EMPLOYEE_DEFAULT_MESSAGE +employeeDefaultMessage.getId(), JSONObject.toJSONString(employeeDefaultMessage),60);
//				}
				redisService.setDays(MemberConstant.REDIS_EMPLOYEE_DEFAULT_MESSAGE + memberId, JSONObject.toJSONString(dataList),60);
			}
		}
		return dataList;
	}

	@Override
	public EmployeeDefaultMessage getMyHelloMessage(String memberId, String InviteCode) throws Exception {
		List<EmployeeDefaultMessage> dataList = this.getMyHelloMessage(memberId);
		if (dataList.isEmpty()){
			return null;
		}
		for (EmployeeDefaultMessage employeeDefaultMessage : dataList) {
			if ( !StringUtils.isEmpty(employeeDefaultMessage.getInvite_code()) || employeeDefaultMessage.getInvite_code().equals(InviteCode)){
				return employeeDefaultMessage;
			}
		}
		return null;
	}

	@Override
	public boolean isEmployId(String memberId) throws Exception {

		boolean isEmployee = redisService.sIsMember(MemberConstant.REDIS_EMPLOYEE_SET,memberId);

		if (!isEmployee) {
			Employee employee  = employeeService.get("member_id",memberId);
			if (employee == null) {
				return false;
			}
			redisService.sAdd(MemberConstant.REDIS_EMPLOYEE_SET,memberId);
		}
		return true;
	}


}
