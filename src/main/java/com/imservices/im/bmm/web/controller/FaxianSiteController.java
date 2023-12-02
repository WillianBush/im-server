package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.FaxianSite;
import com.imservices.im.bmm.entity.HomepageEntity;
import com.imservices.im.bmm.entity.HomepageUserEntity;
import com.imservices.im.bmm.service.FunctionConfigService;
import com.imservices.im.bmm.service.HomePageService;
import com.imservices.im.bmm.service.HomePageUserService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller("FxsController")
@RequestMapping(value = "/fxs/json")
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class FaxianSiteController {

	private HomePageService homePageService;
	private HomePageUserService homePageUserService;

	private MemberService memberService;

	private FunctionConfigService functionConfigService;
	private RedisService redisService;
	
//	@AuthPassport
	@RequestMapping(value = "/getList",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getList(HttpServletRequest request,HttpServletResponse response)  {
//		log.info("/getLis ..... homepage");
		try {
//			List list = fxsService.findByPager(new String[] {"status"}
//					, new Object[] {0},new Pager(1, 10, "orderList", OrderType.asc)).getList();
			List<FaxianSite> list = redisService.getAndSetArray(MemberConstant.HOME_PAGE+"1",10,FaxianSite.class,()->{
				List<HomepageEntity> homepageEntityList = null;
				try {
					homepageEntityList = homePageService.getListAll();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				List<FaxianSite> faxianSites = new ArrayList<>();
				for (HomepageEntity homepage: homepageEntityList) {
					FaxianSite faxianSite = new FaxianSite();
					faxianSite.setLogo(homepage.getIcon());
					faxianSite.setOrderList(homepage.getSort());
					faxianSite.setName(homepage.getName());
					faxianSite.setId(homepage.getId());
					faxianSite.setUrl(homepage.getLink());
					faxianSite.setStatus(0);
					faxianSites.add(faxianSite);
				}
				return faxianSites;
			});

			ResponseUtils.json(response,200,list,null);
		}catch (Exception e) {
			log.error("getLis",e);
			ResponseUtils.json(response,500,"",null);
		}

	}



	@RequestMapping(value = "/getListWithMid",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getListWithMid(HttpServletRequest request,HttpServletResponse response) throws Exception {
//		String member_id = request.getParameter("mid");
		String id = request.getParameter("x-access-uid");
//		if(StringUtils.isEmpty(member_id)) return;
		if(StringUtils.isEmpty(id)) return;
		String mid = memberService.get(id).getMemberId();
		Integer outLinkStatus = functionConfigService.get().getOut_link_status();
		try {
//			List list = fxsService.findByPager(new String[] {"status"}
//					, new Object[] {0},new Pager(1, 10, "orderList", OrderType.asc)).getList();
			List<FaxianSite> list = redisService.getAndSetArray(MemberConstant.HOME_PAGE+"1",10,FaxianSite.class,()->{
				List<HomepageEntity> homepageEntityList = null;
				try {
					homepageEntityList = homePageService.getListAll();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				List<FaxianSite> faxianSites = new ArrayList<>();
				List<HomepageUserEntity> homepageUserEntities = homePageUserService.getHomepageUserInfo(mid);
				Boolean flag = false;
				for (HomepageEntity homepage: homepageEntityList) {
					FaxianSite faxianSite = new FaxianSite();
					faxianSite.setLogo(homepage.getIcon());
					faxianSite.setOrderList(homepage.getSort());
					faxianSite.setName(homepage.getName());
					faxianSite.setId(homepage.getId());
					faxianSite.setUrl(homepage.getLink());
					faxianSite.setStatus(0);
					if (outLinkStatus == 0) {
						faxianSite.setStatus_user(1);
					} else if (homepageUserEntities.isEmpty()) {
						faxianSite.setStatus_user(0);
					} else {
						for (HomepageUserEntity homepageUserEntity : homepageUserEntities) {
							if (homepageUserEntity.getOut_link_id() == homepage.getId() ) {
								faxianSite.setStatus_user(homepageUserEntity.getStatus());
								flag = true;
							}
						}
						if (!flag) {
							faxianSite.setStatus_user(0);
						}else {
							flag = false;
						}
					}
					faxianSites.add(faxianSite);
				}
				return faxianSites;
			});

			ResponseUtils.json(response,200,list,null);
		}catch (Exception e) {
			log.error("getLis",e);
			ResponseUtils.json(response,500,"",null);
		}

	}


}
