package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.NoticeItem;
import com.imservices.im.bmm.bean.NoticeList;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.bean.Pager.OrderType;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.entity.Notice;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.NoticeService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@Controller("NoticeController")
@RequestMapping(value = "/notice")
@CrossOrigin
public class NoticeController {

	
	@Autowired
	private WebConfigService configService;
	@Autowired 
	private StoreComponent  storeComponent;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private MemberService memberService;
	
	
	@RequestMapping(value={"/detail"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void notice_detail(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Notice notice = noticeService.get(id);
		ResponseUtils.json(response, 200,notice , null);
	}
	
	
	@RequestMapping(value={"/list"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void notice_list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		List<Notice> list = noticeService.findByPager(new String[] {"status"}, new Object[] {0}, new Pager(1, 100, "createDate", OrderType.desc)).getList();
		List<NoticeList> list1 = new ArrayList<NoticeList>();
		SimpleDateFormat s1 = new SimpleDateFormat("MM-dd");
		for(Notice o : list) {
			NoticeList nl = null;
			boolean flag = true;
			for(NoticeList l : list1) {
				if(l.getDateStr().equals(s1.format(o.getCreateDate()))) {
					nl = l;
					flag = false;
					break;
				}
			}
			if(flag) {
				nl = new NoticeList();
				nl.setDateStr(s1.format(o.getCreateDate()));
				list1.add(nl);
			}
			
			
			
			SimpleDateFormat s2 = new SimpleDateFormat("HH:mm");
			NoticeItem ni = new NoticeItem();
			ni.setId(o.getId());
			ni.setTitle(o.getTitle());
			ni.setTimeStr(s2.format(o.getCreateDate()));
			if(o.getCreateDate().getHours()<=12) {
				ni.setAmOrPm("上午");
			} else if(o.getCreateDate().getHours()<=17) {	
				ni.setAmOrPm("下午");
			} else {
				ni.setAmOrPm("晚上");
			}
			
			nl.getList().add(ni);
			
		}
		ResponseUtils.json(response, 200,list1 , null);
	}
	
	
//	@RequestMapping(value={"/zixun_check"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
//	public void zixun_check(HttpServletRequest request,HttpServletResponse response) throws Exception {
//		String kefu_id = request.getParameter("kefu_id");
//		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
//		Kefu kefu = kefuService.get(kefu_id);
//		if(null==kefu||kefu.getStatus().intValue()==1) {
//			ResponseUtils.json(response, 500,"客服不存在" , null);
//			return;
//		}
//		
//		Long l = memberService.count(Friends.class, new String[] {"mid","friendid"}, new Object[] {MEMBERID,kefu.getId()});
//		if(null==l||l<=0) {
//			Friends f = new Friends();
//			f.setFriendid(kefu.getId());
//			f.setMid(MEMBERID);
//			memberService.saveFriends(f);
//		}
//		
//		l = memberService.count(Friends.class, new String[] {"mid","friendid"}, new Object[] {MEMBERID,kefu.getId()});
//		
//		
//		
//		ResponseUtils.json(response, 200,mlist , null);
//	}
	 
	

}
