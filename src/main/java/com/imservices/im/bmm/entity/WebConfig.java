package com.imservices.im.bmm.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="webconfig")
@SuppressWarnings("all")
public class WebConfig extends BaseEntity implements Serializable {
	
	//游戏模板
	public enum GAME_H5_MOBAN {
		mb1,mb2
	}
	
	//短信接口平台
	//loktong乐迅通
	public enum SMS_PLATFORM {
		loktong
	}
	
	
	public static final String IMAGE_PATH = "/images/uploadfile/";

	
	private Integer member_id_len;//用户ID长度 至少4位
	private Integer room_id_len;//群ID长度 至少4位
	
	//unipush
	private String appId_uni;
	private String appKey_uni;
	private String masterSecret_uni;
	
	private Integer transfer_expire_sec=120;//转账过期秒数
	private Integer hongbao_expire_sec=120;//红包过期秒数
	
	private String dailiUrlRegisteredTo;//通过代理链接注册后跳转地址
	private String weblogo = "";
	private String websiteName = "";
	private String websiteUrl = "";
	private String phone = "";
	private String email = "";
	private String wx = "";
	private String qq = "";
	private Double winRate=0.0;//赢率%
	private Double loseRate=0.0;//输率%
	private Integer controlWinLose=0;//控制输赢 1开 0关
	private Integer agentBuy=0;//代理人购买 1开 0关
	private Integer agentAddFen=0;//代理人上分 1允许 0不允许
	private Integer agentSubFen=0;//代理人下分 1允许 0不允许
	private String agentRedFanDian="";//红包代理返点
	private Double userRegisterGiveGold=0.0;//新用户注册赠送的初始金额
	private Integer toSpeak=0;//发言 1开 0关
	private Integer transferMoney=0;//转账 1开 0关
	
	
	private String wanfa ="";//玩法介绍
	private String kefuStr="";//客服信息
	private Double txMoneyMin = 0.0;//提现金额最小
	private Double txMoneyMax = 0.0;//提现金额最大
	private Double txFee = 0.0;//提现服务费率%
	private Double czMoneyMin = 0.0;//充值金额最小
	private Double czMoneyMax = 0.0;//充值金额最大
	
	private String dailiRulePic;//代理规则图片
	private String dailiPosters;//代理海报图片
	private Double dailiPostersQrcode_top=0.0;//代理海报二维码位置 %
	private Double dailiPostersQrcode_left=0.0;//代理海报二维码位置 %
	private Double dailiPostersQrcode_width=0.0;//代理海报二维码width %
	private Double dailiPostersQrcode_height=0.0;//代理海报二维码height %
	 
	private String smsContent;
	private String smsApiKey;
	
	private Integer gameDataKeepDay=1;//相关记录数据保留天数【资金明细，抢发包记录】
	
	
	//码支付信息
	private String fateqq_token;//token令牌
	private String fateqq_codepay_id;//codepay_id
	private String fateqq_key;//秘钥
	
	private Integer showGameJieSuan = 1;//是否显示游戏结算信息
	private GAME_H5_MOBAN h5_moban = GAME_H5_MOBAN.mb1;
	private String csMemberId = "";//客服人员member_id   
	private String noticeStr;//公告
	private Integer undeadHideMoneyWS = 0;//是否隐藏免死抢到金额的位数
	private Double openRedMoneyMaxScale = 20.0;//单个抢包金额占总红包金额的最大比例
	
	
	//微信信息
	private String wx_appid;
	private String wx_secret;
	private Integer wxAutoLogin = 0;//1是
	private String wx_domain;
	//QQ信息
	private String qq_appid;
	private String qq_appkey;
	private String qq_domain;
	   
	private String createRoomPriceConf;//创建房间配置 格式：天数=多少元#天数=多少元

	private String site1_url;
	private Integer chat_msg_undo_sec;//聊天信息多少秒内可撤消
	
	private String registerCode;//注册码
	private Integer useRegisterCode=0;//是否启用注册码 0否 1是
	private String superUser="";//超级用户  用户ID,用户ID
	private String forbidRegister4ip;//禁止IP注册
	private Integer addNewFriendAble = 1;//是否能添加新好友 0不能 1能 
	private String addNewFriendAble_others = "";//查看好友权限例外ID组合 多个用,分开
	private String newRegAddFriends = "";//新注册用户自动添加好友列表，多个ID用,分开
	private String newRegAddFriends_welcomeWord="";//新注册用户自动添加好友后，收到的欢迎语
	private Integer room_members_limit=300;//群成员人数上限
	private Integer userHongBaoMaxPrice = 200;//私人红包金额上限，超过此金额只能转账
	//哪个角色可查看群成员详细 0全体 1仅群主 2群主和群管理员
	private Integer lookGroupMemberDetailForRole = 0;
	
	private Integer reg_sms = 0;//注册短信验证
	private SMS_PLATFORM sms_platform = SMS_PLATFORM.loktong;//短信平台 默认:乐讯通“loktong”
	private String sms_loktong_un;
	private String sms_loktong_pwd;
	private String sms_loktong_token;
	private String sms_loktong_templateid;
	
	
	
	/***IM平台底部热门导航设置***/
	private Integer imFooterHotItem_show=1;//0否 1是
	private String imFooterHotItem_name="";
	private String imFooterHotItem_url="";
	private String imFooterHotItem_logo="";
	//1：模式1【图标不凸起，链接跳转保留底部菜单】  2：模式2【图标凸起，链接跳转不保留底部菜单】
	private Integer imFooterHotItem_show_type=1;
	
	
	//实名认证
	private Integer shiming = 0;//是否打开实名认证  0否 1是
	private Integer sendRed_sm = 0;//发红包是否需要实名 0否 1是
	private Integer openRed_sm = 0;//抢红包是否需要实名 0否 1是
	private Integer transfer_sm = 0;//转账是否需要实名 0否 1是
	private Integer receivetransfer_sm = 0;//接收转账是否需要实名 0否 1是
	
	private Integer newFriendAuditOverdue = 12;//新朋友验证过期时间(时)
	private Integer sendRederCanOpen4UserChat = 0;//好友间发红包。发包者是否能开包  0不可以 1可以
	private Integer chatUrlTxtCanLink = 0;//聊天内容链接文本是否可点击跳转 0否 1是
	
	private String chatBackgroundImage="";//聊天内容窗体背景 空则为默认
	
	private Integer showUserOnline = 0;//是否显示好友在线状态 0隐藏 1显示
	private Integer showUserMsgReadStatus = 0;//是否显示好友间信息阅读状态  0隐藏 1显示
	
	
	private String sysVersion_android;//安卓平台版本号
	private String sysVersion_ios;//ios平台版本号
//	private String sysVersion_h5;//h5平台版本号
	private String sysVersion_pc;//pc平台版本号
	private String android_url;//安卓APP下载链接
	private String ios_url;//ios下载链接
	private String pc_url;//pc下载链接
	private String android_updDetails;//安卓版本更新描述
	private String ios_updDetails;//ios版本更新描述
	private String pc_updDetails;//pc版本更新描述
  
	
	private Integer useInviteCode=0;//是否启用邀请码 0否 1是
	
	private String mySecretaryName = "我的小秘书";//小秘书昵称
	private String welcomeStr ="";//欢迎语
	
	private Integer useSignIn;//启动签到 0否 1是
	private String signInCnf;//签到配置
	private Double signIn1DayCoin=0.0;//签到所得金币数，如果符合签到配置所达到的要求，则当天所得金币数会覆盖这个数
	private String signInBtnBgColor_index = "#F87002";//首页签到按钮背景色
	
	
	private Integer cloudUserChatDataKeepDays = 10;//好友云端聊天记录保留多少天
	private Integer cloudGroupChatDataKeepDays = 10;//群组云端聊天记录保留多少天
	
	
//	private Integer clientUserChatMsgShowCount = 50;//客户端好友聊天记录显示多少条
//	private Integer clientGroupChatMsgShowCount = 50;//客户端群组聊天记录显示多少条
	
	 
	//线下收款信息
	private String bankCardNum;//银行卡号
	private String bankName;//银行名称
	private String bankAccountrer;//开户者姓名
	




	public String getBankCardNum() {
		return bankCardNum;
	}

	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccountrer() {
		return bankAccountrer;
	}

	public void setBankAccountrer(String bankAccountrer) {
		this.bankAccountrer = bankAccountrer;
	}

	public Integer getCloudUserChatDataKeepDays() {
		if(null==cloudUserChatDataKeepDays) return 10;
		return cloudUserChatDataKeepDays;
	}

	public void setCloudUserChatDataKeepDays(Integer cloudUserChatDataKeepDays) {
		this.cloudUserChatDataKeepDays = cloudUserChatDataKeepDays;
	}

	public Integer getCloudGroupChatDataKeepDays() {
		if(null==cloudGroupChatDataKeepDays) return 10;
		return cloudGroupChatDataKeepDays;
	}

	public void setCloudGroupChatDataKeepDays(Integer cloudGroupChatDataKeepDays) {
		this.cloudGroupChatDataKeepDays = cloudGroupChatDataKeepDays;
	}


	public String getSignInBtnBgColor_index() {
		if(StringUtils.isEmpty(signInBtnBgColor_index)) return "#F87002";
		return signInBtnBgColor_index;
	}

	public void setSignInBtnBgColor_index(String signInBtnBgColor_index) {
		this.signInBtnBgColor_index = signInBtnBgColor_index;
	}

	public Integer getUseSignIn() {
		if(null==useSignIn) return 0;
		return useSignIn;
	}

	public void setUseSignIn(Integer useSignIn) {
		this.useSignIn = useSignIn;
	}

	public Double getSignIn1DayCoin() {
		if(null==signIn1DayCoin) return 0.0;
		return signIn1DayCoin;
	}

	public void setSignIn1DayCoin(Double signIn1DayCoin) {
		this.signIn1DayCoin = signIn1DayCoin;
	}

	@Column(length=2000)
	public String getSignInCnf() {
		if(null==signInCnf) return "";
		return signInCnf;
	}

	public void setSignInCnf(String signInCnf) {
		this.signInCnf = signInCnf;
	}

	public Integer getMember_id_len() {
		if(null==member_id_len||member_id_len<4) return 4;
		return member_id_len;
	}

	public void setMember_id_len(Integer member_id_len) {
		this.member_id_len = member_id_len;
	}
 
	public Integer getRoom_id_len() {
		if(null==room_id_len||room_id_len<4) return 4;
		return room_id_len;
	}

	public void setRoom_id_len(Integer room_id_len) {
		this.room_id_len = room_id_len;
	}

	public String getMySecretaryName() {
		if(StringUtils.isEmpty(mySecretaryName)) return "我的小秘书";
		return mySecretaryName;
	}

	public void setMySecretaryName(String mySecretaryName) {
		this.mySecretaryName = mySecretaryName;
	}

	public Integer getUseInviteCode() {
		if(null==useInviteCode) return 0;
		return useInviteCode;
	}

	public void setUseInviteCode(Integer useInviteCode) {
		this.useInviteCode = useInviteCode;
	}

	@Column(length=2000)
	public String getAndroid_updDetails() {
		return android_updDetails;
	}

	public void setAndroid_updDetails(String android_updDetails) {
		this.android_updDetails = android_updDetails;
	}

	@Column(length=2000)
	public String getIos_updDetails() {
		return ios_updDetails;
	}

	public void setIos_updDetails(String ios_updDetails) {
		this.ios_updDetails = ios_updDetails;
	}

	@Column(length=2000)
	public String getPc_updDetails() {
		return pc_updDetails;
	}

	public void setPc_updDetails(String pc_updDetails) {
		this.pc_updDetails = pc_updDetails;
	}

	public String getAndroid_url() {
		if(null==android_url) return "";
		return android_url;
	}

	public void setAndroid_url(String android_url) {
		this.android_url = android_url;
	}

	public String getIos_url() {
		if(null==ios_url) return "";
		return ios_url;
	}

	public void setIos_url(String ios_url) {
		this.ios_url = ios_url;
	}

	public String getPc_url() {
		if(null==pc_url) return "";
		return pc_url;
	}

	public void setPc_url(String pc_url) {
		this.pc_url = pc_url;
	}

	public String getSysVersion_android() {
		if(null==sysVersion_android) return "";
		return sysVersion_android;
	}

	public void setSysVersion_android(String sysVersion_android) {
		this.sysVersion_android = sysVersion_android;
	}

	public String getSysVersion_ios() {
		if(null==sysVersion_ios) return "";
		return sysVersion_ios;
	}

	public void setSysVersion_ios(String sysVersion_ios) {
		this.sysVersion_ios = sysVersion_ios;
	}

//	public String getSysVersion_h5() {
//		if(null==sysVersion_h5) return "";
//		return sysVersion_h5;
//	}
//
//	public void setSysVersion_h5(String sysVersion_h5) {
//		this.sysVersion_h5 = sysVersion_h5;
//	}

	public String getSysVersion_pc() {
		if(null==sysVersion_pc) return "";
		return sysVersion_pc;
	}

	public void setSysVersion_pc(String sysVersion_pc) {
		this.sysVersion_pc = sysVersion_pc;
	}

	public Integer getImFooterHotItem_show_type() {
		if(null==imFooterHotItem_show_type) return 1;
		return imFooterHotItem_show_type;
	}

	public void setImFooterHotItem_show_type(Integer imFooterHotItem_show_type) {
		this.imFooterHotItem_show_type = imFooterHotItem_show_type;
	}

	public Integer getShowUserOnline() {
		if(null==showUserOnline) return 0;
		return showUserOnline;
	}

	public void setShowUserOnline(Integer showUserOnline) {
		this.showUserOnline = showUserOnline;
	}

	public Integer getShowUserMsgReadStatus() {
		if(null==showUserMsgReadStatus) return 0;
		return showUserMsgReadStatus;
	}

	public void setShowUserMsgReadStatus(Integer showUserMsgReadStatus) {
		this.showUserMsgReadStatus = showUserMsgReadStatus;
	}

	@Enumerated
	public SMS_PLATFORM getSms_platform() {
		if(null==sms_platform) return SMS_PLATFORM.loktong;
		return sms_platform;
	}

	public void setSms_platform(SMS_PLATFORM sms_platform) {
		this.sms_platform = sms_platform;
	}

	public String getSms_loktong_un() {
		return sms_loktong_un;
	}

	public void setSms_loktong_un(String sms_loktong_un) {
		this.sms_loktong_un = sms_loktong_un;
	}

	public String getSms_loktong_pwd() {
		return sms_loktong_pwd;
	}

	public void setSms_loktong_pwd(String sms_loktong_pwd) {
		this.sms_loktong_pwd = sms_loktong_pwd;
	}

	public String getSms_loktong_token() {
		return sms_loktong_token;
	}

	public void setSms_loktong_token(String sms_loktong_token) {
		this.sms_loktong_token = sms_loktong_token;
	}

	public String getSms_loktong_templateid() {
		return sms_loktong_templateid;
	}

	public void setSms_loktong_templateid(String sms_loktong_templateid) {
		this.sms_loktong_templateid = sms_loktong_templateid;
	}

	public String getChatBackgroundImage() {
		if(null==chatBackgroundImage) return "";
		return chatBackgroundImage;
	}

	public void setChatBackgroundImage(String chatBackgroundImage) {
		this.chatBackgroundImage = chatBackgroundImage;
	}

	public String getImFooterHotItem_logo() {
		if(null==imFooterHotItem_logo) return "";
		return imFooterHotItem_logo;
	}

	public void setImFooterHotItem_logo(String imFooterHotItem_logo) {
		this.imFooterHotItem_logo = imFooterHotItem_logo;
	}

	public Integer getChatUrlTxtCanLink() {
		if(null==chatUrlTxtCanLink) return 0;
		return chatUrlTxtCanLink;
	}

	public void setChatUrlTxtCanLink(Integer chatUrlTxtCanLink) {
		this.chatUrlTxtCanLink = chatUrlTxtCanLink;
	}

	public Integer getReg_sms() {
		if(null==reg_sms) return 0;
		return reg_sms;
	}

	public void setReg_sms(Integer reg_sms) {
		this.reg_sms = reg_sms;
	}

	public Integer getSendRederCanOpen4UserChat() {
		if(null==sendRederCanOpen4UserChat) return 0;
		return sendRederCanOpen4UserChat;
	}

	public void setSendRederCanOpen4UserChat(Integer sendRederCanOpen4UserChat) {
		this.sendRederCanOpen4UserChat = sendRederCanOpen4UserChat;
	}

	public Integer getNewFriendAuditOverdue() {
		if(null==newFriendAuditOverdue) return 12;
		return newFriendAuditOverdue;
	}

	public void setNewFriendAuditOverdue(Integer newFriendAuditOverdue) {
		this.newFriendAuditOverdue = newFriendAuditOverdue;
	}

	public Integer getOpenRed_sm() {
		if(null==openRed_sm) return 0;
		return openRed_sm;
	}

	public void setOpenRed_sm(Integer openRed_sm) {
		this.openRed_sm = openRed_sm;
	}

	public Integer getReceivetransfer_sm() {
		if(null==receivetransfer_sm) return 0;
		return receivetransfer_sm;
	}

	public void setReceivetransfer_sm(Integer receivetransfer_sm) {
		this.receivetransfer_sm = receivetransfer_sm;
	}

	public Integer getShiming() {
		if(null==shiming) return 0;
		return shiming;
	}

	public void setShiming(Integer shiming) {
		this.shiming = shiming;
	}

	public Integer getSendRed_sm() {
		if(null==sendRed_sm) return 0;
		return sendRed_sm;
	}

	public void setSendRed_sm(Integer sendRed_sm) {
		this.sendRed_sm = sendRed_sm;
	}

	public Integer getTransfer_sm() {
		if(null==transfer_sm) return 0;
		return transfer_sm;
	}

	public void setTransfer_sm(Integer transfer_sm) {
		this.transfer_sm = transfer_sm;
	}

	public Integer getLookGroupMemberDetailForRole() {
		if(null==lookGroupMemberDetailForRole) return 0;
		return lookGroupMemberDetailForRole;
	}

	public void setLookGroupMemberDetailForRole(Integer lookGroupMemberDetailForRole) {
		this.lookGroupMemberDetailForRole = lookGroupMemberDetailForRole;
	}

	public Integer getRoom_members_limit() {
		if(null==room_members_limit) return 300;
		return room_members_limit;
	}

	public void setRoom_members_limit(Integer room_members_limit) {
		this.room_members_limit = room_members_limit;
	}

	public Integer getUserHongBaoMaxPrice() {
		if(null==userHongBaoMaxPrice) return 0;
		return userHongBaoMaxPrice;
	}

	public void setUserHongBaoMaxPrice(Integer userHongBaoMaxPrice) {
		this.userHongBaoMaxPrice = userHongBaoMaxPrice;
	}

	public Integer getImFooterHotItem_show() {
		if(null==imFooterHotItem_show) return 0;
		return imFooterHotItem_show;
	}

	public void setImFooterHotItem_show(Integer imFooterHotItem_show) {
		this.imFooterHotItem_show = imFooterHotItem_show;
	}

	public String getImFooterHotItem_name() {
		return imFooterHotItem_name;
	}

	public void setImFooterHotItem_name(String imFooterHotItem_name) {
		this.imFooterHotItem_name = imFooterHotItem_name;
	}

	public String getImFooterHotItem_url() {
		return imFooterHotItem_url;
	}

	public void setImFooterHotItem_url(String imFooterHotItem_url) {
		this.imFooterHotItem_url = imFooterHotItem_url;
	}

	public String getNewRegAddFriends_welcomeWord() {
		return newRegAddFriends_welcomeWord;
	}

	public void setNewRegAddFriends_welcomeWord(String newRegAddFriends_welcomeWord) {
		this.newRegAddFriends_welcomeWord = newRegAddFriends_welcomeWord;
	}

	@Column(length=5000)
	public String getNewRegAddFriends() {
		if(null==newRegAddFriends) return "";
		return newRegAddFriends;
	}

	public void setNewRegAddFriends(String newRegAddFriends) {
		this.newRegAddFriends = newRegAddFriends;
	}

	@Column(length=5000)
	public String getAddNewFriendAble_others() {
		if(null==addNewFriendAble_others) return "";
		return addNewFriendAble_others;
	}

	public void setAddNewFriendAble_others(String addNewFriendAble_others) {
		this.addNewFriendAble_others = addNewFriendAble_others;
	}

	public Integer getAddNewFriendAble() {
		if(null==addNewFriendAble) return 1;
		return addNewFriendAble;
	}

	public void setAddNewFriendAble(Integer addNewFriendAble) {
		this.addNewFriendAble = addNewFriendAble;
	}

	@Column(length=10000)
	public String getForbidRegister4ip() {
		return forbidRegister4ip;
	}

	public void setForbidRegister4ip(String forbidRegister4ip) {
		this.forbidRegister4ip = forbidRegister4ip;
	}

	@Column(length=5000)
	public String getSuperUser() {
		if(null==superUser) return "";
		return superUser;
	}

	public void setSuperUser(String superUser) {
		this.superUser = superUser;
	}

	public String getRegisterCode() {
		return registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public Integer getUseRegisterCode() {
		if(null==useRegisterCode) return 0;
		return useRegisterCode;
	}

	public void setUseRegisterCode(Integer useRegisterCode) {
		this.useRegisterCode = useRegisterCode;
	}

	public Integer getTransfer_expire_sec() {
		if(null==transfer_expire_sec) return 120;
		return transfer_expire_sec;
	}

	public void setTransfer_expire_sec(Integer transfer_expire_sec) {
		this.transfer_expire_sec = transfer_expire_sec;
	}

	public Integer getHongbao_expire_sec() {
		if(null==hongbao_expire_sec) return 120;
		return hongbao_expire_sec;
	}

	public void setHongbao_expire_sec(Integer hongbao_expire_sec) {
		this.hongbao_expire_sec = hongbao_expire_sec;
	}

	public Integer getChat_msg_undo_sec() {
		if(null==chat_msg_undo_sec) return 0;
		return chat_msg_undo_sec;
	}

	public void setChat_msg_undo_sec(Integer chat_msg_undo_sec) {
		this.chat_msg_undo_sec = chat_msg_undo_sec;
	}

	public Double getTxFee() {
		if(null==txFee) return 0.0;
		return txFee;
	}

	public void setTxFee(Double txFee) {
		this.txFee = txFee;
	}

	public String getSite1_url() {
		return site1_url;
	}

	public void setSite1_url(String site1_url) {
		this.site1_url = site1_url;
	}

	public String getQq_appkey() {
		return qq_appkey;
	}

	@Column(length=5000)
	public String getCreateRoomPriceConf() {
		if(StringUtils.isEmpty(createRoomPriceConf)) return "";
		return createRoomPriceConf;
	}

	public void setCreateRoomPriceConf(String createRoomPriceConf) {
		this.createRoomPriceConf = createRoomPriceConf;
	}

	public void setQq_appkey(String qq_appkey) {
		this.qq_appkey = qq_appkey;
	}

	public String getQq_appid() {
		return qq_appid;
	}

	public void setQq_appid(String qq_appid) {
		this.qq_appid = qq_appid;
	}

	

	public String getQq_domain() {
		return qq_domain;
	}

	public void setQq_domain(String qq_domain) {
		this.qq_domain = qq_domain;
	}

	public String getWx_domain() {
		return wx_domain;
	}

	public void setWx_domain(String wx_domain) {
		this.wx_domain = wx_domain;
	}


	public Integer getWxAutoLogin() {
		if(null==wxAutoLogin) return 0;
		return wxAutoLogin;
	}

	public void setWxAutoLogin(Integer wxAutoLogin) {
		this.wxAutoLogin = wxAutoLogin;
	}

	public String getWx_secret() {
		if(null==wx_secret) return "";
		return wx_secret;
	}

	public void setWx_secret(String wx_secret) {
		this.wx_secret = wx_secret;
	}

	public String getWx_appid() {
		if(null==wx_appid) return "";
		return wx_appid;
	}

	public void setWx_appid(String wx_appid) {
		this.wx_appid = wx_appid;
	}

	public Double getOpenRedMoneyMaxScale() {
		if(null==openRedMoneyMaxScale) return 0.0;
		return openRedMoneyMaxScale;
	}

	public void setOpenRedMoneyMaxScale(Double openRedMoneyMaxScale) {
		this.openRedMoneyMaxScale = openRedMoneyMaxScale;
	}

	public Integer getUndeadHideMoneyWS() {
		if(null==undeadHideMoneyWS) return 0;
		return undeadHideMoneyWS;
	}

	public void setUndeadHideMoneyWS(Integer undeadHideMoneyWS) {
		this.undeadHideMoneyWS = undeadHideMoneyWS;
	}

	public String getCsMemberId() {
		return csMemberId;
	}

	public void setCsMemberId(String csMemberId) {
		this.csMemberId = csMemberId;
	}

	@Column(length=1000)
	public String getNoticeStr() {
		if(StringUtils.isEmpty(noticeStr)) return "";
		return noticeStr;
	}

	public void setNoticeStr(String noticeStr) {
		this.noticeStr = noticeStr;
	}
	

	public GAME_H5_MOBAN getH5_moban() {
		if(null==h5_moban) return GAME_H5_MOBAN.mb1;
		return h5_moban;
	}

	public void setH5_moban(GAME_H5_MOBAN h5_moban) {
		this.h5_moban = h5_moban;
	}

	public Integer getShowGameJieSuan() {
		if(null==showGameJieSuan) return 1;
		return showGameJieSuan;
	}

	public void setShowGameJieSuan(Integer showGameJieSuan) {
		this.showGameJieSuan = showGameJieSuan;
	}

	public String getDailiUrlRegisteredTo() {
		return dailiUrlRegisteredTo;
	}

	public void setDailiUrlRegisteredTo(String dailiUrlRegisteredTo) {
		this.dailiUrlRegisteredTo = dailiUrlRegisteredTo;
	}

	public Integer getGameDataKeepDay() {
		if(null==gameDataKeepDay||gameDataKeepDay<=0) return 1;
		return gameDataKeepDay;
	}

	public void setGameDataKeepDay(Integer gameDataKeepDay) {
		this.gameDataKeepDay = gameDataKeepDay;
	}

	public Double getCzMoneyMin() {
		return czMoneyMin;
	}

	public void setCzMoneyMin(Double czMoneyMin) {
		this.czMoneyMin = czMoneyMin;
	}

	public Double getCzMoneyMax() {
		return czMoneyMax;
	}

	public void setCzMoneyMax(Double czMoneyMax) {
		this.czMoneyMax = czMoneyMax;
	}

	public String getFateqq_key() {
		return fateqq_key;
	}

	public void setFateqq_key(String fateqq_key) {
		this.fateqq_key = fateqq_key;
	}

	public String getFateqq_token() {
		return fateqq_token;
	}

	public void setFateqq_token(String fateqq_token) {
		this.fateqq_token = fateqq_token;
	}

	public String getFateqq_codepay_id() {
		return fateqq_codepay_id;
	}

	public void setFateqq_codepay_id(String fateqq_codepay_id) {
		this.fateqq_codepay_id = fateqq_codepay_id;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public String getSmsApiKey() {
		return smsApiKey;
	}

	public void setSmsApiKey(String smsApiKey) {
		this.smsApiKey = smsApiKey;
	}

	public Double getDailiPostersQrcode_top() {
		return dailiPostersQrcode_top;
	}

	public void setDailiPostersQrcode_top(Double dailiPostersQrcode_top) {
		this.dailiPostersQrcode_top = dailiPostersQrcode_top;
	}

	public Double getDailiPostersQrcode_left() {
		return dailiPostersQrcode_left;
	}

	public void setDailiPostersQrcode_left(Double dailiPostersQrcode_left) {
		this.dailiPostersQrcode_left = dailiPostersQrcode_left;
	}

	public Double getDailiPostersQrcode_width() {
		return dailiPostersQrcode_width;
	}

	public void setDailiPostersQrcode_width(Double dailiPostersQrcode_width) {
		this.dailiPostersQrcode_width = dailiPostersQrcode_width;
	}

	public Double getDailiPostersQrcode_height() {
		return dailiPostersQrcode_height;
	}

	public void setDailiPostersQrcode_height(Double dailiPostersQrcode_height) {
		this.dailiPostersQrcode_height = dailiPostersQrcode_height;
	}

	public String getDailiPosters() {
		return dailiPosters;
	}

	public void setDailiPosters(String dailiPosters) {
		this.dailiPosters = dailiPosters;
	}

	public String getDailiRulePic() {
		return dailiRulePic;
	}

	public void setDailiRulePic(String dailiRulePic) {
		this.dailiRulePic = dailiRulePic;
	}

	@Column(length=5000)
	public String getKefuStr() {
		return kefuStr;
	}

	public void setKefuStr(String kefuStr) {
		this.kefuStr = kefuStr;
	}

	@Column(length=10000)
	public String getWanfa() {
		return wanfa;
	}

	public void setWanfa(String wanfa) {
		this.wanfa = wanfa;
	}

	public String getWelcomeStr() {
		return welcomeStr;
	}

	public void setWelcomeStr(String welcomeStr) {
		this.welcomeStr = welcomeStr;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getWinRate() {
		if(null==winRate) return 0.0;
		return winRate;
	}

	public void setWinRate(Double winRate) {
		this.winRate = winRate;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getLoseRate() {
		if(null==loseRate) return 0.0;
		return loseRate;
	}

	public void setLoseRate(Double loseRate) {
		this.loseRate = loseRate;
	}

	public Integer getControlWinLose() {
		if(null==controlWinLose) return 0;
		return controlWinLose;
	}

	public void setControlWinLose(Integer controlWinLose) {
		this.controlWinLose = controlWinLose;
	}

	public Integer getAgentBuy() {
		if(null==agentBuy) return 0;
		return agentBuy;
	}

	public void setAgentBuy(Integer agentBuy) {
		this.agentBuy = agentBuy;
	}

	public Integer getAgentAddFen() {
		if(null==agentAddFen) return 0;
		return agentAddFen;
	}

	public void setAgentAddFen(Integer agentAddFen) {
		this.agentAddFen = agentAddFen;
	}

	public Integer getAgentSubFen() {
		if(null==agentSubFen) return 0;
		return agentSubFen;
	}

	public void setAgentSubFen(Integer agentSubFen) {
		this.agentSubFen = agentSubFen;
	}
	
	@Column(length=2000)
	public String getAgentRedFanDian() {
		return agentRedFanDian;
	}

	public void setAgentRedFanDian(String agentRedFanDian) {
		this.agentRedFanDian = agentRedFanDian;
	}

	@Column(columnDefinition = "double(10,2)")
	public Double getUserRegisterGiveGold() {
		if(null==userRegisterGiveGold) return 0.0;
		return userRegisterGiveGold;
	}

	public void setUserRegisterGiveGold(Double userRegisterGiveGold) {
		this.userRegisterGiveGold = userRegisterGiveGold;
	}

	public Integer getToSpeak() {
		if(null==toSpeak) return 0;
		return toSpeak;
	}

	public void setToSpeak(Integer toSpeak) {
		this.toSpeak = toSpeak;
	}

	public Integer getTransferMoney() {
		if(null==transferMoney) return 0;
		return transferMoney;
	}

	public void setTransferMoney(Integer transferMoney) {
		this.transferMoney = transferMoney;
	}

	public String getWeblogo() {
		return weblogo;
	}

	public void setWeblogo(String weblogo) {
		this.weblogo = weblogo;
	}
	

	public String getWebsiteName() {
		return websiteName;
	}

	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWx() {
		return wx;
	}

	public void setWx(String wx) {
		this.wx = wx;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getTxMoneyMin() {
		if(null==txMoneyMin) return 0.0;
		return txMoneyMin;
	}

	public void setTxMoneyMin(Double txMoneyMin) {
		this.txMoneyMin = txMoneyMin;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getTxMoneyMax() {
		if(null==txMoneyMax) return 0.0;
		return txMoneyMax;
	}

	public void setTxMoneyMax(Double txMoneyMax) {
		this.txMoneyMax = txMoneyMax;
	}

	public String getAppId_uni() {
		return appId_uni;
	}

	public void setAppId_uni(String appId_uni) {
		this.appId_uni = appId_uni;
	}

	public String getAppKey_uni() {
		return appKey_uni;
	}

	public void setAppKey_uni(String appKey_uni) {
		this.appKey_uni = appKey_uni;
	}

	public String getMasterSecret_uni() {
		return masterSecret_uni;
	}

	public void setMasterSecret_uni(String masterSecret_uni) {
		this.masterSecret_uni = masterSecret_uni;
	}

	
	
}