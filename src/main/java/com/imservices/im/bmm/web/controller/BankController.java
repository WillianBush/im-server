package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Bank;
import com.imservices.im.bmm.service.BankService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller("BankController")
@RequestMapping(value = "/user/bank/json")
@CrossOrigin
public class BankController {

	@Autowired
	private WebConfigService configService;
	@Autowired
	private MemberService memberService; 
	@Autowired
	private BankService bankService; 
	
	@AuthPassport    
	@RequestMapping(value = "/getList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		List<Bank> list = bankService.getList(new String[] {"uid"}, new Object[] {MEMBERID});
		ResponseUtils.json(response,200,list,null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/add",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void add(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String name = request.getParameter("name");
		String belonger = request.getParameter("belonger");
		String code = request.getParameter("code");
		String cardCode = request.getParameter("cardCode");
		
		Bank o = new Bank();
		o.setUid(MEMBERID);
		o.setBelonger(belonger);
		o.setCardCode(cardCode);
		o.setCode(code);
		o.setName(name);
		bankService.save(o);
		ResponseUtils.json(response,200,o,null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/update",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void update(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String belonger = request.getParameter("belonger");
		String code = request.getParameter("code");
		String cardCode = request.getParameter("cardCode");
		
		Bank o = bankService.get(id);
		if(!o.getUid().equals(MEMBERID)) {
			ResponseUtils.json(response,500,"没有权限",null); 
			return;
		}
		o.setBelonger(belonger);
		o.setCardCode(cardCode);
		o.setCode(code);
		o.setName(name);
		bankService.update(o);
		ResponseUtils.json(response,200,o,null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/remove",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void remove(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String id = request.getParameter("id");
		bankService.delete(id);
		ResponseUtils.json(response,200,"",null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/load",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void load(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String id = request.getParameter("id");
		Bank bank = bankService.get(id);
		ResponseUtils.json(response,200,bank,null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/getDefault",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getDefault(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String id = request.getParameter("id");
		if(!StringUtils.isEmpty(id)) {
			Bank bank = bankService.get(id);
			ResponseUtils.json(response,200,bank,null); 
			return;
		}
		List<Bank> list = bankService.findByPager(new String[] {"uid"}, new Object[] {MEMBERID}, new Pager(1, 1, "createDate", OrderType.asc)).getList();
		if(!list.isEmpty()) {
			ResponseUtils.json(response,200,list.get(0),null); 
			return;
		} else {
			ResponseUtils.json(response,201,"",null); 
			return; 
		}
		
	} 
	
	

}
