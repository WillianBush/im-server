package com.imservices.im.bmm.bean;

import com.imservices.im.bmm.entity.Member.MEMBER_TYPE;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MemberBean {

	private String id;
	private String username;
	private String memberId;
	private String telphone;
	private String nickName;
	private String sex;//性别
	private String headpic;
	private String parent;
	private Double money=0.0;//帐号余额
	private Double txMoneyIng=0.0;//提现中金额
	private String registIp;
	private String lastLoginIp;
	private Date lastLoginDate;  
	private Integer status;//0正常 1禁用
	private String qrCodeImg;//推广二维码
	private String url;//推广链接
	private Date createDate;
	private String preTxImg="";//最近一次提现使用的二维码
	private Integer preTxPay=1;//最近一次提现收款方式
	private MEMBER_TYPE member_type;
	private String parent_uuid;//上级UUID
	/**数据统计**/
	private Double rechargePriceSum = 0.0;//充值累计
	private Double txPriceSum = 0.0;//提现累计
	private Double losePriceSum = 0.0;//输金额累计
	private Double winPriceSum = 0.0;//赢金额累计
	private Double sendRedPriceSum = 0.0;//发红包金额累计
	private Double openRedPriceSum = 0.0;//抢红包金额累计
	private Integer sendRedCount = 0;//发包次数累计
	private Integer openRedCount = 0;//抢包次数累计
	private Double tichenPriceSum = 0.0;//提成金额累计  
	
	private Long unDoFriendAddCount = 0L;//未处理好友添加信息
	private Long unDoRoomAddCount = 0L;//未处理好友添加信息
	
	
	private String preBank_name;//开户行
	private String preBank_addr;//开户地
	private String preBank_belonger;//持卡人
	private String preBank_code;//银行卡号
	private Integer one_level_count=0;//直属下级人数
	
	
	private Map<String, Double> irupMap = new HashMap<String, Double>();//各独立房间的余额 《房间UUID，余额》
	
	private String inRoomid="";//正在哪个房间的id
	private Integer online=0;//是否在线 1是 0否
	private Integer shimingStatus = 0;//实名状态   0审核中 1成功 2失败
	
//	private Date imOffLineDate;//IM离线时间
//	
//	
//	//离线时间描述
//	public String imOffLineDateStr() {
//		if(null==imOffLineDate) return "";
//		Date now = new Date();
//		Long l = now.getTime() - imOffLineDate.getTime();
//		long day = l / (24 * 60 * 60 * 1000);
//        long hour = (l / (60 * 60 * 1000) - day * 24);
//        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
//        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//        //
//        
//	}
	
	
//	
//	public Date getImOffLineDate() {
//		return imOffLineDate;
//	}
//	public void setImOffLineDate(Date imOffLineDate) {
//		this.imOffLineDate = imOffLineDate;
//	}
	
	
	
	
	public Integer getOnline() {
		if(null==online) return 0;
		return online;
	}
	public Integer getShimingStatus() {
		if(null==shimingStatus) return 0;
		return shimingStatus;
	}
	public void setShimingStatus(Integer shimingStatus) {
		this.shimingStatus = shimingStatus;
	}
	public void setOnline(Integer online) {
		this.online = online;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getOne_level_count() {
		return one_level_count;
	}
	public void setOne_level_count(Integer one_level_count) {
		this.one_level_count = one_level_count;
	}
	public String getInRoomid() {
		if(null==inRoomid) return "";
		return inRoomid;
	}
	public void setInRoomid(String inRoomid) {
		this.inRoomid = inRoomid;
	}
	public Map<String, Double> getIrupMap() {
		return irupMap;
	}
	public void setIrupMap(Map<String, Double> irupMap) {
		this.irupMap = irupMap;
	}
	public String getPreBank_name() {
		return preBank_name;
	}
	public void setPreBank_name(String preBank_name) {
		this.preBank_name = preBank_name;
	}
	public String getPreBank_addr() {
		return preBank_addr;
	}
	public void setPreBank_addr(String preBank_addr) {
		this.preBank_addr = preBank_addr;
	}
	public String getPreBank_belonger() {
		return preBank_belonger;
	}
	public void setPreBank_belonger(String preBank_belonger) {
		this.preBank_belonger = preBank_belonger;
	}
	public String getPreBank_code() {
		return preBank_code;
	}
	public void setPreBank_code(String preBank_code) {
		this.preBank_code = preBank_code;
	}
	public String getParent_uuid() {
		return parent_uuid;
	}
	public void setParent_uuid(String parent_uuid) {
		this.parent_uuid = parent_uuid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public MEMBER_TYPE getMember_type() {
		return member_type;
	}
	public void setMember_type(MEMBER_TYPE member_type) {
		this.member_type = member_type;
	}
	public Long getUnDoFriendAddCount() {
		return unDoFriendAddCount;
	}
	public void setUnDoFriendAddCount(Long unDoFriendAddCount) {
		this.unDoFriendAddCount = unDoFriendAddCount;
	}
	
	
	
	
	
	
	
	public Long getUnDoRoomAddCount() {
		return unDoRoomAddCount;
	}
	public void setUnDoRoomAddCount(Long unDoRoomAddCount) {
		this.unDoRoomAddCount = unDoRoomAddCount;
	}
	public String getPreTxImg() {
		return preTxImg;
	}
	public void setPreTxImg(String preTxImg) {
		this.preTxImg = preTxImg;
	}
	public Integer getPreTxPay() {
		return preTxPay;
	}
	public void setPreTxPay(Integer preTxPay) {
		this.preTxPay = preTxPay;
	}
	public Double getTichenPriceSum() {
		if(null==tichenPriceSum) return 0.0;
		return tichenPriceSum;
	}
	public void setTichenPriceSum(Double tichenPriceSum) {
		this.tichenPriceSum = tichenPriceSum;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getQrCodeImg() {
		return qrCodeImg;
	}
	public void setQrCodeImg(String qrCodeImg) {
		this.qrCodeImg = qrCodeImg;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadpic() {
		return headpic;
	}
	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	//根据roombean来获取用户金额，如果房间不是独立房间，则返回平台金额
	public Double getMoney(RoomBean rb) {
		if(rb.getIndependence()==1&&this.member_type!=MEMBER_TYPE.ROBOT) {
			if(this.irupMap.containsKey(rb.getRoomUUID())) {
				return this.irupMap.get(rb.getRoomUUID());
			} else {
				return 0.0;
			}
		} else {
			return getMoney();
		}
	}
	
	//根据roombean来设置用户金额，如果房间不是独立房间，则设置平台金额
	public void setMoney(RoomBean rb,Double money) {
		if(rb.getIndependence()==1&&this.member_type!=MEMBER_TYPE.ROBOT) {
			this.irupMap.put(rb.getRoomUUID(),money);
		} else {
			setMoney(money);
		}
	}
	
	
	public Double getMoney() {
		return money;
	}
	
	public void setMoney(Double money) {
		this.money = money;
	}
	
	
	public Double getTxMoneyIng() {
		return txMoneyIng;
	}
	public void setTxMoneyIng(Double txMoneyIng) {
		this.txMoneyIng = txMoneyIng;
	}
	public String getRegistIp() {
		return registIp;
	}
	public void setRegistIp(String registIp) {
		this.registIp = registIp;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Double getRechargePriceSum() {
		return rechargePriceSum;
	}
	public void setRechargePriceSum(Double rechargePriceSum) {
		this.rechargePriceSum = rechargePriceSum;
	}
	public Double getTxPriceSum() {
		return txPriceSum;
	}
	public void setTxPriceSum(Double txPriceSum) {
		this.txPriceSum = txPriceSum;
	}
	public Double getLosePriceSum() {
		return losePriceSum;
	}
	public void setLosePriceSum(Double losePriceSum) {
		this.losePriceSum = losePriceSum;
	}
	public Double getWinPriceSum() {
		return winPriceSum;
	}
	public void setWinPriceSum(Double winPriceSum) {
		this.winPriceSum = winPriceSum;
	}
	public Double getSendRedPriceSum() {
		return sendRedPriceSum;
	}
	public void setSendRedPriceSum(Double sendRedPriceSum) {
		this.sendRedPriceSum = sendRedPriceSum;
	}
	public Double getOpenRedPriceSum() {
		return openRedPriceSum;
	}
	public void setOpenRedPriceSum(Double openRedPriceSum) {
		this.openRedPriceSum = openRedPriceSum;
	}
	public Integer getSendRedCount() {
		return sendRedCount;
	}
	public void setSendRedCount(Integer sendRedCount) {
		this.sendRedCount = sendRedCount;
	}
	public Integer getOpenRedCount() {
		return openRedCount;
	}
	public void setOpenRedCount(Integer openRedCount) {
		this.openRedCount = openRedCount;
	}
	
	
	
	
}
