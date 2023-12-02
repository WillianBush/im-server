package com.imservices.im.bmm.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="room")
@SuppressWarnings("all")
public class Room extends BaseEntity {
	//游戏种类
	public enum GameType {
		jielong,niuniu,saolei,jinqiang,sangong,g28
	}
	//控制模式
	public enum ControlModel {
		s1
	}
	
	public Room(){};
	public Room(String id){setId(id);};
	
	
	
	
	private String roomId;
	private String name;
	private String subname;
	private String headImg;
	private String descri;
	private Integer status=0;//0正常  1停止
	private Integer gameStatus=0;//0开机  1关机 【关机下所有不能抢包和发包】
	private Double sendPrice=0.0;//发包总额【玩家】
	private Double openerPrice=0.0;//抢包总额【玩家】
	
	private GameType gameType;
	private String owner="";//房主ID，留空则平台为房主
	private String owner_UUID="";//房主uuID，留空则平台为房主
	private Integer limitNum=100;//人数限制
	private Double sendFeeAdd=0.0;//发包服务费率
	private Double openFeeAdd=0.0;//抢包服务费率
	private Double winFeeAdd=0.0;//赢包服务费率【发包与抢包赢】
	private Double sendFeeSum=0.0;//发包服务总费
	private Double openFeeSum=0.0;//抢包服务总费
	private Double winFeeSum=0.0;//赢包服务总费
	private Integer sendCount=0;//发包总数
	private String unDead;//免死号，多个以,分开
	private String psw="";//房间密码。默认空
	private Integer controlGame=0;//启用游戏控制 1：是  0：否
	private ControlModel controlModel= ControlModel.s1;//控制模式
	private Double playerSendWinRate_s1;//【模式1】玩家发包赢率
	private Double playerOpenWinRate_s1;//【模式1】玩家抢包赢率
	private String props;//房间属性
	private String fixedRobotIds;//禁抢玩家固定抢包机器人memberId集合，以,分开
	private String rulePic;//玩家规则图片
	private Date endDate;//到期时间  注：只适用于用户创建 的房间
	
	private Integer independence=0;//是否为独立房间 0:否 1：是
	private String agentRedFanDian="";//红包代理返点
	
	private Integer maxRobotCount = 0;//可用机器人最大数量【独立房间时使用】
	private Integer openRedDelayStart = 0;//开包延迟最低秒数【独立房间时使用】
	private Integer openRedDelayEnd = 0;//开包延迟最高秒数【独立房间时使用】
	
	private Integer robotRule=0;//0默认 1只抢不发
	
	//码支付信息
	private String fateqq_token;//token令牌
	private String fateqq_codepay_id;//codepay_id
	private String fateqq_key;//秘钥
	
	private String domain = "";
	
	
	private String member_ids;//成员ID集合  id#id#id#
	private String stopspeak_member_ids;//禁言成员ID集合  id#id#id#【针对单个用户禁言】
	private Integer yaoqingAble=0;//是否允许邀请 0否  1是  当否时只能管理员才邀请
	private Integer yaoqingAuditAble=0;//邀请是否需要核实 0否  1是  
	private String memberMgr_ids = "";//群管理 ID集合  id#id#id#
	
	
	private Integer useCustomHeadpic = 0;//使用用户自定义图片 0否  1是
	
	
	
	public Integer getUseCustomHeadpic() {
		if(null==useCustomHeadpic) return 0;
		return useCustomHeadpic;
	}
	public void setUseCustomHeadpic(Integer useCustomHeadpic) {
		this.useCustomHeadpic = useCustomHeadpic;
	}
	
	
	public Integer getYaoqingAble() {
		if(null==yaoqingAble) return 0;
		return yaoqingAble;
	}
	public void setYaoqingAble(Integer yaoqingAble) {
		this.yaoqingAble = yaoqingAble;
	}
	public Integer getYaoqingAuditAble() {
		if(null==yaoqingAuditAble) return 0;
		return yaoqingAuditAble;
	}
	public void setYaoqingAuditAble(Integer yaoqingAuditAble) {
		this.yaoqingAuditAble = yaoqingAuditAble;
	}
	
	@Column(length=10000)
	public String getMemberMgr_ids() {
		if(null==memberMgr_ids) return "";
		return memberMgr_ids;
	}
	public void setMemberMgr_ids(String memberMgr_ids) {
		this.memberMgr_ids = memberMgr_ids;
	}
	@Column(length=20000)
	public String getStopspeak_member_ids() {
		if(null==stopspeak_member_ids) return "";
		return stopspeak_member_ids;
	}
	public void setStopspeak_member_ids(String stopspeak_member_ids) {
		this.stopspeak_member_ids = stopspeak_member_ids;
	}
	@Column(length=20000)
	public String getMember_ids() {
		if(null==member_ids) return "";
		return member_ids;
	}
	public void setMember_ids(String member_ids) {
		this.member_ids = member_ids;
	}
	public Integer getRobotRule() {
		if(null==robotRule) return 0;
		return robotRule;
	}
	public void setRobotRule(Integer robotRule) {
		this.robotRule = robotRule;
	}
	public String getDomain() {
		if(null==domain) return "";
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
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
	public String getFateqq_key() {
		return fateqq_key;
	}
	public void setFateqq_key(String fateqq_key) {
		this.fateqq_key = fateqq_key;
	}
	public Integer getMaxRobotCount() {
		if(null==maxRobotCount) return 0;
		return maxRobotCount;
	}
	public void setMaxRobotCount(Integer maxRobotCount) {
		this.maxRobotCount = maxRobotCount;
	}
	public Integer getOpenRedDelayStart() {
		if(null==openRedDelayStart) return 0;
		return openRedDelayStart;
	}
	public void setOpenRedDelayStart(Integer openRedDelayStart) {
		this.openRedDelayStart = openRedDelayStart;
	}
	public Integer getOpenRedDelayEnd() {
		if(null==openRedDelayEnd) return 0;
		return openRedDelayEnd;
	}
	public void setOpenRedDelayEnd(Integer openRedDelayEnd) {
		this.openRedDelayEnd = openRedDelayEnd;
	}
	@Column(length=2000)
	public String getAgentRedFanDian() {
		if(null==agentRedFanDian) return "";
		return agentRedFanDian;
	}

	public void setAgentRedFanDian(String agentRedFanDian) {
		this.agentRedFanDian = agentRedFanDian;
	}
	
	    
	
	public Integer getIndependence() {
		if(null==independence) return 0;
		return independence;
	}
	public void setIndependence(Integer independence) {
		this.independence = independence;
	}
	@Transient
	public String getEndDateStr() {
		if(null==endDate) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(endDate);
	}
	
	public String getOwner_UUID() {
		if(null==owner_UUID) return "";
		return owner_UUID;
	}
	public void setOwner_UUID(String owner_UUID) {
		this.owner_UUID = owner_UUID;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getGameStatus() {
		if(null==gameStatus) return 0;
		return gameStatus;
	}
	public void setGameStatus(Integer gameStatus) {
		this.gameStatus = gameStatus;
	}
	public String getRulePic() {
		if(StringUtils.isEmpty(rulePic)) return "";
		return rulePic;
	}
	public void setRulePic(String rulePic) {
		this.rulePic = rulePic;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getWinFeeAdd() {
		if(null==winFeeAdd) return 0.0;
		return winFeeAdd;
	}
	public void setWinFeeAdd(Double winFeeAdd) {
		this.winFeeAdd = winFeeAdd;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getWinFeeSum() {
		return winFeeSum;
	}
	public void setWinFeeSum(Double winFeeSum) {
		this.winFeeSum = winFeeSum;
	}
	@Column(length=20000)
	public String getFixedRobotIds() {
		if(StringUtils.isEmpty(fixedRobotIds)) return "";
		return fixedRobotIds;
	}
	public void setFixedRobotIds(String fixedRobotIds) {
		this.fixedRobotIds = fixedRobotIds;
	}
	public String getSubname() {
		return subname;
	}
	public void setSubname(String subname) {
		this.subname = subname;
	}
	@Enumerated
	public GameType getGameType() {
		return gameType;
	}
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	public String getOwner() {
		if(null==owner||"undefined".equals(owner)) return "";
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Integer getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getSendFeeAdd() {
		if(null==sendFeeAdd) return 0.0;
		return sendFeeAdd;
	}
	public void setSendFeeAdd(Double sendFeeAdd) {
		this.sendFeeAdd = sendFeeAdd;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getOpenFeeAdd() {
		if(null==openFeeAdd) return 0.0;
		return openFeeAdd;
	}
	public void setOpenFeeAdd(Double openFeeAdd) {
		this.openFeeAdd = openFeeAdd;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getSendFeeSum() {
		return sendFeeSum;
	}
	public void setSendFeeSum(Double sendFeeSum) {
		this.sendFeeSum = sendFeeSum;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getOpenFeeSum() {
		return openFeeSum;
	}
	public void setOpenFeeSum(Double openFeeSum) {
		this.openFeeSum = openFeeSum;
	}
	public Integer getSendCount() {
		return sendCount;
	}
	public void setSendCount(Integer sendCount) {
		this.sendCount = sendCount;
	}
	@Column(length=20000)
	public String getUnDead() {
		if(null==unDead) return "";
		return unDead;
	}
	public void setUnDead(String unDead) {
		this.unDead = unDead;
	}
	public String getPsw() {
		if(null==psw) return "";
		return psw;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
	public Integer getControlGame() {
		if(null==controlGame) return 0; 
		return controlGame;
	}
	public void setControlGame(Integer controlGame) {
		this.controlGame = controlGame;
	}
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeadImg() {
		if(StringUtils.isEmpty(headImg)) return "";
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	@Column(length=5000)
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getSendPrice() {
		return sendPrice;
	}
	public void setSendPrice(Double sendPrice) {
		this.sendPrice = sendPrice;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getOpenerPrice() {
		return openerPrice;
	}
	public void setOpenerPrice(Double openerPrice) {
		this.openerPrice = openerPrice;
	}
	
	@Enumerated
	public ControlModel getControlModel() {
		return controlModel;
	}
	public void setControlModel(ControlModel controlModel) {
		this.controlModel = controlModel;
	}
	
	@Column(columnDefinition = "double(10,2)")
	public Double getPlayerSendWinRate_s1() {
		if(null==playerSendWinRate_s1) return 0.0;
		return playerSendWinRate_s1;
	}
	public void setPlayerSendWinRate_s1(Double playerSendWinRate_s1) {
		this.playerSendWinRate_s1 = playerSendWinRate_s1;
	}
	@Column(columnDefinition = "double(10,2)")
	public Double getPlayerOpenWinRate_s1() {
		if(null==playerOpenWinRate_s1) return 0.0;
		return playerOpenWinRate_s1;
	}
	public void setPlayerOpenWinRate_s1(Double playerOpenWinRate_s1) {
		this.playerOpenWinRate_s1 = playerOpenWinRate_s1;
	}

	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	@Transient
	public String getGameTypeName() {
		if(this.gameType == GameType.jielong) return "接龙";
		else if(this.gameType == GameType.niuniu) return "牛牛";
		else if(this.gameType == GameType.saolei) return "扫雷";
		else if(this.gameType == GameType.jinqiang) return "禁抢";
		else if(this.gameType == GameType.sangong) return "三公";
		else if(this.gameType == GameType.g28) return "28杠";
		return "";
	}
	 
	   
	@Column(length=50000)
	public String getProps() {
		if(StringUtils.isEmpty(props)) return "";
		return props;
	}
	public void setProps(String props) {
		this.props = props;
	}

	
	  
}
