package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Favourite;
import com.imservices.im.bmm.service.FavouriteService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller("FavouriteController")
@RequestMapping(value = "/user/favourite/json")
@CrossOrigin
public class FavouriteController {

	@Autowired
	private WebConfigService configService;
	@Autowired
	private MemberService memberService; 
	@Autowired
	private FavouriteService favouriteService; 
	
	@AuthPassport    
	@RequestMapping(value = "/getList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String p = request.getParameter("p");
		String kw = request.getParameter("kw");
		if(StringUtils.isEmpty(p)||!NumberUtils.isNumber(p)) p = "1";
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		List<Favourite> list = favouriteService.findByPager(new String[] {"uid","jsonstr"}, new Object[] {MEMBERID,"%"+kw+"%"},new Pager(Integer.valueOf(p), 10, "createDate", OrderType.desc)).getList();
		//
		ResponseUtils.json(response,200,list,null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/add",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void add(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String jsonstr = request.getParameter("jsonstr");
		Favourite o = new Favourite();
		o.setUid(MEMBERID);
		o.setJsonstr(jsonstr);
		favouriteService.save(o);
		ResponseUtils.json(response,200,"",null); 
	} 
	
	
	@AuthPassport    
	@RequestMapping(value = "/remove",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void remove(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String id = request.getParameter("id");
		favouriteService.delete(id);
		ResponseUtils.json(response,200,"",null); 
	} 
	
	@AuthPassport    
	@RequestMapping(value = "/load",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void load(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String id = request.getParameter("id");
		Favourite o = favouriteService.get(id);
		ResponseUtils.json(response,200,o,null); 
	} 
	

	
	

}
