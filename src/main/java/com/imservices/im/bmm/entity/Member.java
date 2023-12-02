package com.imservices.im.bmm.entity;

import com.imservices.im.bmm.utils.MD5;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name="member")
@SuppressWarnings("all")
public class Member extends BaseEntity {
	
	//普通用户,机器人,代理
	public enum MEMBER_TYPE {
		USER,ROBOT,AGENT
	} 

	private Integer sysGenerate;//1为自动生成
	private String pay_pwd;//支付密码
	private String memberId;
	private MEMBER_TYPE memberType = MEMBER_TYPE.USER;
	private String username;
	private String telphone;
	private String password;
	private String nickName;
	private String headpic;
	private String sex;//性别
	private String parent;//上级的UUID
	private String parentmid;//上级的会员ID
	private String parentPath;
	private Double money=0.0;//帐号余额
	private Double txMoneyIng=0.0;//提现中金额
	private String registIp;
	private String lastLoginIp;
	private Date lastLoginDate;  
	private Integer status;//0正常 1禁用
	private String qrCodeImg;//推广二维码
	private String preTxImg="";//最近一次提现使用的二维码
	private Integer preTxPay=1;//最近一次提现收款方式
	
	private String wxOpenId;
	private String qqOpenId;
	
	private String preBank_name;//开户行
	private String preBank_addr;//开户地
	private String preBank_belonger;//持卡人
	private String preBank_code;//银行卡号
	
	private Integer shimingStatus = 0;//实名状态   0审核中 1成功 2失败
	
	/**数据统计**/
	private Double rechargePriceSum = 0.0;//充值累计
	private Double txPriceSum = 0.0;//提现累计
	private Double losePriceSum = 0.0;//输金额累计
	private Double winPriceSum = 0.0;//赢金额累计
	private Double sendRedPriceSum = 0.0;//发红包金额累计
	private Double sendTransferPriceSum = 0.0;//转账金额累计
	private Double openRedPriceSum = 0.0;//抢红包金额累计
	private Integer sendTransferCount = 0;//转账次数累计
	private Integer sendRedCount = 0;//发包次数累计
	private Integer openRedCount = 0;//抢包次数累计
	private Double tichenPriceSum = 0.0;//提成金额累计
	
	private Integer one_level_count=0;//直属下级人数
	
	private Double rechargePrice_today = 0.0;//今日充值金额
	private Double txPrice_today = 0.0;//今日提现金额
	private Double tichenPrice_today = 0.0;//今日提成金额
	

	public Double getSendTransferPriceSum() {
		if(null==sendTransferPriceSum) return 0.0;
		return sendTransferPriceSum;
	}
	public Integer getSysGenerate() {
		if(null==sysGenerate) return 0;
		return sysGenerate;
	}
	public void setSysGenerate(Integer sysGenerate) {
		this.sysGenerate = sysGenerate;
	}
	public Integer getShimingStatus() {
		if(null==shimingStatus) return 0;
		return shimingStatus;
	}
	public void setShimingStatus(Integer shimingStatus) {
		this.shimingStatus = shimingStatus;
	}
	public void setSendTransferPriceSum(Double sendTransferPriceSum) {
		this.sendTransferPriceSum = sendTransferPriceSum;
	}
	public Integer getSendTransferCount() {
		if(null==sendTransferCount) return 0;
		return sendTransferCount;
	}
	public void setSendTransferCount(Integer sendTransferCount) {
		this.sendTransferCount = sendTransferCount;
	}
	public String getPay_pwd() {
		return pay_pwd;
	}
	public void setPay_pwd(String pay_pwd) {
		this.pay_pwd = pay_pwd;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getQqOpenId() {
		return qqOpenId;
	}
	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}
	public String getWxOpenId() {
		return wxOpenId;
	}
	public void setWxOpenId(String wxOpenId) {
		this.wxOpenId = wxOpenId;
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
	public Double getRechargePrice_today() {
		if(null==rechargePrice_today) return 0.0;
		return rechargePrice_today;
	}
	public void setRechargePrice_today(Double rechargePrice_today) {
		this.rechargePrice_today = rechargePrice_today;
	}
	public Double getTxPrice_today() {
		if(null==txPrice_today) return 0.0;
		return txPrice_today;
	}
	public void setTxPrice_today(Double txPrice_today) {
		this.txPrice_today = txPrice_today;
	}
	public Double getTichenPrice_today() {
		if(null==tichenPrice_today) return 0.0;
		return tichenPrice_today;
	}
	public void setTichenPrice_today(Double tichenPrice_today) {
		this.tichenPrice_today = tichenPrice_today;
	}
	public Integer getOne_level_count() {
		if(null==one_level_count) return 0;
		return one_level_count;
	}
	public void setOne_level_count(Integer one_level_count) {
		this.one_level_count = one_level_count;
	}
	public Member(){}
	public Member(String id){setId(id);}
	 
	
	
	
	
	@Enumerated
	public MEMBER_TYPE getMemberType() {
		return memberType;
	}
	public void setMemberType(MEMBER_TYPE memberType) {
		this.memberType = memberType;
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
	
	@Column(columnDefinition = "double(10,2)")
	public Double getRechargePriceSum() {
		if(null==rechargePriceSum) return 0.0;
		return rechargePriceSum;
	}
	public void setRechargePriceSum(Double rechargePriceSum) {
		this.rechargePriceSum = rechargePriceSum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getTxPriceSum() {
		if(null==txPriceSum) return 0.0;
		return txPriceSum;
	}
	public void setTxPriceSum(Double txPriceSum) {
		this.txPriceSum = txPriceSum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getLosePriceSum() {
		if(null==losePriceSum) return 0.0;
		return losePriceSum;
	}
	public void setLosePriceSum(Double losePriceSum) {
		this.losePriceSum = losePriceSum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getWinPriceSum() {
		if(null==winPriceSum) return 0.0;
		return winPriceSum;
	}
	public void setWinPriceSum(Double winPriceSum) {
		this.winPriceSum = winPriceSum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getSendRedPriceSum() {
		if(null==sendRedPriceSum) return 0.0;
		return sendRedPriceSum;
	}
	public void setSendRedPriceSum(Double sendRedPriceSum) {
		this.sendRedPriceSum = sendRedPriceSum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getOpenRedPriceSum() {
		if(null==openRedPriceSum) return 0.0;
		return openRedPriceSum;
	}
	public void setOpenRedPriceSum(Double openRedPriceSum) {
		this.openRedPriceSum = openRedPriceSum;
	}
	public Integer getSendRedCount() {
		if(null==sendRedCount) return 0;
		return sendRedCount;
	}
	public void setSendRedCount(Integer sendRedCount) {
		this.sendRedCount = sendRedCount;
	}
	public Integer getOpenRedCount() {
		if(null==openRedCount) return 0;
		return openRedCount;
	}
	public void setOpenRedCount(Integer openRedCount) {
		this.openRedCount = openRedCount;
	}
	
	
	
	
	@Column(columnDefinition = "double(10,3)")
	public Double getTichenPriceSum() {
		if(null==tichenPriceSum) return 0.0;
		return tichenPriceSum;
	}
	public void setTichenPriceSum(Double tichenPriceSum) {
		this.tichenPriceSum = tichenPriceSum;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadpic() {
		if(null==headpic) return "";
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
	@Column(length=20000)
	public String getParentPath() {
		if(StringUtils.isEmpty(parentPath)) return "";
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	@Column(columnDefinition = "double(10,3)")
	public Double getMoney() {
		if(null==money) return 0.0;
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getTxMoneyIng() {
		if(null==txMoneyIng) return 0.0;
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
		if(null==status) return 0;
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	} 
	public String getParentmid() {
		return parentmid;
	}
	public void setParentmid(String parentmid) {
		this.parentmid = parentmid;
	}
	
	
	public static void main(String[] args) {
		String p = "93897cc117a734be93733779051c9926";
		System.out.println(MD5.MD5Encode("123458"));
	}
	
}
