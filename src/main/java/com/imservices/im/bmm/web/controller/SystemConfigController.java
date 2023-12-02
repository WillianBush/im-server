package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.Config;
import com.imservices.im.bmm.bean.CreateRoomPrice;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Friends;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.entity.Notice;
import com.imservices.im.bmm.entity.WebConfig;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.NoticeService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping(value = "/sysConfig/json")
@CrossOrigin
public class SystemConfigController {
	
	@Autowired
	private WebConfigService configService;
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private NoticeService noticeService;
	@Autowired 
	private StoreComponent  storeComponent;
	
	
	@RequestMapping(value={"/notice/list"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void notice_list(HttpServletRequest request,HttpServletResponse response) throws Exception {
		List<Notice> list = noticeService.getList(new String[] {"status"}, new Object[] {0});
		ResponseUtils.json(response, 200,list , null);
	}





	@RequestMapping(value={"/getSysVersion"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getSysVersion(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		String xAccessClient = (String) request.getSession().getAttribute("x-access-client");
		if(!StringUtils.isEmpty(xAccessClient)) {
			if("android".equals(xAccessClient)||"ios".equals(xAccessClient)) {
				String appType = request.getParameter("appType");
				if(!StringUtils.isEmpty(appType)) {
					if("android".equals(appType)) {
						map.put("version", wc.getSysVersion_android());
						map.put("url", wc.getAndroid_url());
						map.put("details", wc.getAndroid_updDetails());
					} else {
						map.put("version", wc.getSysVersion_ios());
						map.put("url", wc.getIos_url());
						map.put("details", wc.getIos_updDetails());
					}
				} else {
					map.put("version", "");
					map.put("url", "");
				}
			} else if("H5".equals(xAccessClient)) {
				//map.put("version", wc.getSysVersion_h5());
				map.put("version", "");
				map.put("url", "");
			} else if("PC".equals(xAccessClient)) {
				map.put("version", wc.getSysVersion_pc());
				map.put("url", wc.getPc_url());
				map.put("details", wc.getPc_updDetails());
			} else {
				map.put("version", "");
				map.put("url", "");
			}
		} else {
			map.put("version", "");
			map.put("url", "");
		}
		ResponseUtils.json(response, 200,map , null);
	}

//	@RequestMapping(value={"/getChatDataKeepDays"}, method={org.springframework.web.bind.annotation.RequestMethod.POST,RequestMethod.OPTIONS})
//	public void getChatDataKeepDays(HttpServletRequest request,HttpServletResponse response) throws Exception {
//		WebConfig wc = configService.get();
//		Map<String,Object> map = new HashMap<String, Object>();
//		map.put("chatDataKeepDays", wc.getClientChatDataKeepDays());
//		ResponseUtils.json(response, 200,map , null);
//	}


	@RequestMapping(value={"/getBankInfo"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getBankInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("bankCardNum", wc.getBankCardNum());
		map.put("bankName", wc.getBankName());
		map.put("bankAccountrer", wc.getBankAccountrer());
		ResponseUtils.json(response, 200,map , null);
	}

	@RequestMapping(value={"/getShimingCfg"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getShimingCfg(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("shiming", wc.getShiming());
		map.put("sendRed_sm", wc.getSendRed_sm());
		map.put("transfer_sm", wc.getTransfer_sm());
		ResponseUtils.json(response, 200,map , null);
	}


//	@RequestMapping(value={"/getSignInCfg"}, method={org.springframework.web.bind.annotation.RequestMethod.POST,RequestMethod.OPTIONS})
//	public void getSignInCfg(HttpServletRequest request,HttpServletResponse response) throws Exception {
//		WebConfig wc = configService.get();
//		Map<String,Object> map = new HashMap<String, Object>();
//		map.put("shiming", wc.getShiming());
//		map.put("sendRed_sm", wc.getSendRed_sm());
//		map.put("transfer_sm", wc.getTransfer_sm());
//		ResponseUtils.json(response, 200,map , null);
//	}





	@RequestMapping(value={"/getRoomCfg"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getRoomCfg(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("lookGroupMemberDetailForRole", wc.getLookGroupMemberDetailForRole());
		ResponseUtils.json(response, 200,map , null);
	}

	@RequestMapping(value={"/getChatCfg"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getChatCfg(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("chat_msg_undo_sec", wc.getChat_msg_undo_sec());
		map.put("registerCode", wc.getRegisterCode());
		map.put("useRegisterCode", wc.getUseRegisterCode());
		map.put("chatUrlTxtCanLink", wc.getChatUrlTxtCanLink());
		map.put("chatBackgroundImage", wc.getChatBackgroundImage());


		map.put("showUserOnline", wc.getShowUserOnline());
		map.put("showUserMsgReadStatus", wc.getShowUserMsgReadStatus());
		ResponseUtils.json(response, 200,map , null);
	}


	@RequestMapping(value={"/getRegCfg"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getRegCfg(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("registerCode", wc.getRegisterCode());
		map.put("useInviteCode", wc.getUseInviteCode());
		map.put("useRegisterCode", wc.getUseRegisterCode());
		map.put("reg_sms", wc.getReg_sms());
		ResponseUtils.json(response, 200,map , null);
	}


	@RequestMapping(value={"/getFooterHotItem"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getFooterHotItem(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("show", wc.getImFooterHotItem_show());
		map.put("show_type", wc.getImFooterHotItem_show_type());
		map.put("name", wc.getImFooterHotItem_name());
		map.put("url", wc.getImFooterHotItem_url());
		map.put("logo", wc.getImFooterHotItem_logo());

		ResponseUtils.json(response, 200,map , null);
	}


	@RequestMapping(value={"/getH5MOBAN"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getH5MOBAN(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		ResponseUtils.json(response, 200, wc.getH5_moban().name(), null);
	}

	@RequestMapping(value={"/wxAutoLogin"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void wxAutoLogin(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		String str = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		if(StringUtils.isEmpty(MEMBERID)) {
			ResponseUtils.json(response, 200, wc.getWxAutoLogin()+"", null);
		} else {
			MemberBean bean = BeanUtils.memberToBeanSimple(memberService.get(MEMBERID));
			ResponseUtils.json(response, 1, bean, null);
		}


	}

	@ResponseBody
	@RequestMapping(value = "/get",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public String get(HttpServletRequest request) throws Exception {
		WebConfig wc = configService.get();
		Config config = new Config();
		config.setWeblogo(wc.getWeblogo());
		config.setWebsiteName(wc.getWebsiteName());
		config.setWebsiteUrl(wc.getWebsiteUrl());
		config.setWelcomeStr(wc.getWelcomeStr());
		config.setWanfa(wc.getWanfa());
		config.setKefuStr(wc.getKefuStr());
		config.setTxMoneyMax(wc.getTxMoneyMax());
		config.setTxMoneyMin(wc.getTxMoneyMin());
		config.setSite1_url(wc.getSite1_url());
		return JsonUtil.getJSONString(config);
	}


	@RequestMapping(value = "/getSimple",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void getSimple(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Config config = new Config();
		config.setWeblogo(wc.getWeblogo());
		config.setWebsiteName(wc.getWebsiteName());
		config.setWebsiteUrl(wc.getWebsiteUrl());
		config.setWelcomeStr(wc.getWelcomeStr());
		config.setWanfa(wc.getWanfa());
		config.setKefuStr(wc.getKefuStr());
		config.setTxMoneyMax(wc.getTxMoneyMax());
		config.setTxMoneyMin(wc.getTxMoneyMin());
		config.setSite1_url(wc.getSite1_url());
		ResponseUtils.json(response, 200,config , null);
	}

	@RequestMapping(value={"/getRechargeMinMax/v1"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getRechargeMinMax_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		Map<String,Double> map = new HashMap<String, Double>();
		map.put("min", wc.getCzMoneyMin());
		map.put("max", wc.getCzMoneyMax());
		ResponseUtils.json(response, 200,map , null);
	}

	@RequestMapping(value={"/getNotice/v1"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getNotice_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		ResponseUtils.json(response, 200,wc.getNoticeStr() , null);
	}


	@RequestMapping(value={"/getCreateRoomPriceConf"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getCreateRoomPriceConf(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		if(!StringUtils.isEmpty(wc.getCreateRoomPriceConf())) {
			List<CreateRoomPrice> list = new ArrayList<CreateRoomPrice>();
			String[] arrs = wc.getCreateRoomPriceConf().split("#");
			for(String o : arrs) {
				if(StringUtils.isEmpty(o)) continue;
				CreateRoomPrice crp = new CreateRoomPrice();
				String[] arrs1 = o.split("=");
				if(ArrayUtils.isEmpty(arrs1)||arrs1.length<2) continue;
				crp.setDay(arrs1[0]);
				crp.setPrice(arrs1[1]);
				list.add(crp);
			}
			ResponseUtils.json(response, 200,list , null);
		} else {
			ResponseUtils.json(response, 500,"" , null);
		}



	}



	@RequestMapping(value={"/getAddFriendAble/v1"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getAddFriendAble_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		ResponseUtils.json(response, 200,wc.getAddNewFriendAble().toString() , null);
	}


	@RequestMapping(value={"/getCsId/v1"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getCsId_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		if(StringUtils.isEmpty(wc.getCsMemberId())) {
			ResponseUtils.json(response, 500,"客服不存在" , null);
			return;
		} else {
			Member member = memberService.get("memberId", wc.getCsMemberId());
			if(null==member) {
				ResponseUtils.json(response, 500,"客服不存在" , null);
				return;
			}
			String uid = request.getParameter("uid");
			if(StringUtils.isEmpty(uid)) {
				ResponseUtils.json(response, 500,"错误" , null);
				return;
			}

			Long l = memberService.friendsCount(new String[]{"mid","friendid"}, new Object[]{uid,member.getId()});
			if(null==l||l<=0) {
				Friends firends1 = new Friends();
				firends1.setCreateDate(new Date());
				firends1.setFriendid(uid);
				firends1.setMid(member.getId());
				memberService.saveFriends(firends1);

				Friends firends2 = new Friends();
				firends2.setCreateDate(new Date());
				firends2.setFriendid(member.getId());
				firends2.setMid(uid);
				memberService.saveFriends(firends2);
			}




			ResponseUtils.json(response, 200,member.getId(), null);
		}
	}


	@RequestMapping(value={"/getCsBean/v1"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void getCsBean_v1(HttpServletRequest request,HttpServletResponse response) throws Exception {
		WebConfig wc = configService.get();
		if(StringUtils.isEmpty(wc.getCsMemberId())) {
			ResponseUtils.json(response, 500,null, null);
			return;
		} else {
			Member member = memberService.get("memberId", wc.getCsMemberId());
			if(null==member) {
				ResponseUtils.json(response, 500,null, null);
				return;
			}
			ResponseUtils.json(response, 200,BeanUtils.memberToBeanSimple(member), null);
		}
	}
	
	
}
