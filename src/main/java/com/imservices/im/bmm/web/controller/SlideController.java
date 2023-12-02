package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.entity.SlideImage;
import com.imservices.im.bmm.service.SlideImageService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/slide/json")
@CrossOrigin
public class SlideController {
	
	@Autowired
	private SlideImageService siService;

	@RequestMapping(value={"/list/v1"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void list_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		List<SlideImage> list = siService.findByPager(new String[]{"status"}, new Object[]{0}, new Pager(1, 20, "orderList", OrderType.asc)).getList();
		ResponseUtils.json(response, 200,list, null);
	}
	
}
