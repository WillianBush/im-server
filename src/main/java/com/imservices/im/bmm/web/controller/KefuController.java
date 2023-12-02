package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.entity.Kefu;
import com.imservices.im.bmm.service.KefuService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Controller("KefuController")
@RequestMapping(value = "/kefu")
@CrossOrigin
public class KefuController {

	@Autowired 
	private StoreComponent  storeComponent;
	@Autowired
	private KefuService kefuService;


	@RequestMapping(value={"/list"}, method={RequestMethod.POST, RequestMethod.OPTIONS})
	public void kefu_list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		List<Kefu> list = kefuService.findByPager(new String[] {"status"}, new Object[] {0}, new Pager(1, 50, "orderList", OrderType.asc)).getList();
		List<MemberBean> mlist = new ArrayList<MemberBean>();
		for(Kefu o : list) {
			MemberBean mb = storeComponent.getMemberBeanFromMapDB(o.getMuuid());
			mlist.add(mb);
		}
		ResponseUtils.json(response, 200,mlist , null);
	}

}
