package com.imservices.im.bmm.bean;

import com.imservices.im.bmm.entity.Room.ControlModel;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RoomBean {

	private String id;
	private String name;
	private String subname;
	private String roomid;
	private String roomUUID;
	private String img;
	private String descri;
	private String gameType;
	private Integer gameStatus=0;//0开机  1关机 【关机下所有不能抢包和发包】
	private String properties;//房间属性
	private Double sendFeeAdd=0.0;//发包服务费率
	private Double openFeeAdd=0.0;//抢包服务费率
	private Double winFeeAdd=0.0;//赢包服务费率【发包与抢包赢】
	
	private String unDead;//免死号，多个以,分开
	
	private Integer status=0;//0正常  1停止
	private String psw="";//房间密码。默认空
	private Integer controlGame=0;//启用游戏控制 1：是  0：否
	private ControlModel controlModel=ControlModel.s1;//控制模式
	private Double playerSendWinRate_s1;//【模式1】玩家发包赢率
	private Double playerOpenWinRate_s1;//【模式1】玩家抢包赢率
	private String owner="";//房主ID，留空则平台为房主
	private String owner_UUID="";//房主uuID，留空则平台为房主
	private String ownerName = "";
	private String ownerImg = "";
	private Integer limitNum=100;//人数限制
	private String fixedRobotIds;//禁抢玩家固定抢包机器人memberId集合，以,分开
	private String rulePic;//玩家规则图片
	
	private Date endDate;//到期时间  注：只适用于用户创建 的房间
//	put("STOPSPEAK","禁言#INTEGER#1");//0否 1是
	private Integer stopSpeak;//禁言
	private Integer independence=0;//是否为独立房间 0:否 1：是
	private String agentRedFanDian="";//红包代理返点
	
	private Integer robotEnable = 0;//机器人是否启用  0：否  1：是 【独立房间时使用】
	
	private Integer maxRobotCount = 0;//可用机器人最大数量【独立房间时使用】
	private Integer robotNum = 0;//机器人数量【独立房间时使用】
	private Integer openRedDelayStart = 0;//开包延迟最低秒数【独立房间时使用】
	private Integer openRedDelayEnd = 0;//开包延迟最高秒数【独立房间时使用】
	 
	private Double sendFeeSum=0.0;
	private Double winFeeSum=0.0;
	
	
	private String domain = "";
	private Integer robotRule=0;//0默认 1只抢不发
	
	private String member_ids;//成员ID集合  id#id#id#
	private List<String> top5Hp;//最前5个头像
	
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
		return yaoqingAble;
	}
	public void setYaoqingAble(Integer yaoqingAble) {
		this.yaoqingAble = yaoqingAble;
	}
	public Integer getYaoqingAuditAble() {
		return yaoqingAuditAble;
	}
	public void setYaoqingAuditAble(Integer yaoqingAuditAble) {
		this.yaoqingAuditAble = yaoqingAuditAble;
	}
	public String getMemberMgr_ids() {
		if(null==memberMgr_ids) return "";
		return memberMgr_ids;
	}
	public void setMemberMgr_ids(String memberMgr_ids) {
		this.memberMgr_ids = memberMgr_ids;
	}
	public String getStopspeak_member_ids() {
		if(null==stopspeak_member_ids) return "";
		return stopspeak_member_ids;
	}
	public void setStopspeak_member_ids(String stopspeak_member_ids) {
		this.stopspeak_member_ids = stopspeak_member_ids;
	}



	public String getOwnerImg() {
		return ownerImg;
	}


	public void setOwnerImg(String ownerImg) {
		this.ownerImg = ownerImg;
	}


	public String getOwnerName() {
		return ownerName;
	}


	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}


	public List<String> getTop5Hp() {
		return top5Hp;
	}


	public void setTop5Hp(List<String> top5Hp) {
		this.top5Hp = top5Hp;
	}


	public Integer getMemberCount() {
		if(StringUtils.isEmpty(member_ids)) return 0;
		return member_ids.split("#").length;
	}
	
	
	public String getMember_ids() {
		if(null==member_ids) return "";
		return member_ids;
	}
	public void setMember_ids(String member_ids) {
		this.member_ids = member_ids;
	}
	 
	public Integer getRobotRule() {
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
	
	
	public Double getSendFeeSum() {
		return sendFeeSum;
	}
	public void setSendFeeSum(Double sendFeeSum) {
		this.sendFeeSum = sendFeeSum;
	}
	public Double getWinFeeSum() {
		return winFeeSum;
	}
	public void setWinFeeSum(Double winFeeSum) {
		this.winFeeSum = winFeeSum;
	}
	public Integer getRobotNum() {
		if(null==robotNum) return 0;
		return robotNum;
	}
	public void setRobotNum(Integer robotNum) {
		this.robotNum = robotNum;
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
	
	public Integer getRobotEnable() {
		if(null==robotEnable) return 0;
		return robotEnable;
	}

	public void setRobotEnable(Integer robotEnable) {
		this.robotEnable = robotEnable;
	}

	public String getAgentRedFanDian() {
		if(null==agentRedFanDian) return "";
		return agentRedFanDian;
	}

	public void setAgentRedFanDian(String agentRedFanDian) {
		this.agentRedFanDian = agentRedFanDian;
	}
	
	public Integer getIndependence() {
		return independence;
	}

	public void setIndependence(Integer independence) {
		this.independence = independence;
	}

	public String getEndDateTime() {
		if(null==endDate) return "";
		return endDate.getTime()-new Date().getTime()+"";
	}
	
	public String getEndDateStr() {
		if(null==endDate) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(endDate);
	}
	
	public Integer getStopSpeak() {
		if(null==stopSpeak) return 0;
		return stopSpeak;
	}
	public void setStopSpeak(Integer stopSpeak) {
		this.stopSpeak = stopSpeak;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getOwner_UUID() {
		if(null==owner_UUID) return "";
		return owner_UUID;
	}
	public void setOwner_UUID(String owner_UUID) {
		this.owner_UUID = owner_UUID;
	}
	public Integer getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(Integer gameStatus) {
		this.gameStatus = gameStatus;
	}
	public String getRulePic() {
		return rulePic;
	}
	public void setRulePic(String rulePic) {
		this.rulePic = rulePic;
	}
	public Double getWinFeeAdd() {
		return winFeeAdd;
	}
	public void setWinFeeAdd(Double winFeeAdd) {
		this.winFeeAdd = winFeeAdd;
	}
	public String getFixedRobotIds() {
		return fixedRobotIds;
	}
	public void setFixedRobotIds(String fixedRobotIds) {
		this.fixedRobotIds = fixedRobotIds;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getPsw() {
		return psw;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
	public Integer getControlGame() {
		return controlGame;
	}
	public void setControlGame(Integer controlGame) {
		this.controlGame = controlGame;
	}
	public ControlModel getControlModel() {
		return controlModel;
	}
	public void setControlModel(ControlModel controlModel) {
		this.controlModel = controlModel;
	}
	public Double getPlayerSendWinRate_s1() {
		return playerSendWinRate_s1;
	}
	public void setPlayerSendWinRate_s1(Double playerSendWinRate_s1) {
		this.playerSendWinRate_s1 = playerSendWinRate_s1;
	}
	public Double getPlayerOpenWinRate_s1() {
		return playerOpenWinRate_s1;
	}
	public void setPlayerOpenWinRate_s1(Double playerOpenWinRate_s1) {
		this.playerOpenWinRate_s1 = playerOpenWinRate_s1;
	}
	public String getOwner() {
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
	public String getUnDead() {
		if(null==unDead) return ""; 
		return unDead;
	}
	public void setUnDead(String unDead) {
		this.unDead = unDead;
	}
	public Double getSendFeeAdd() {
		return sendFeeAdd;
	}
	public void setSendFeeAdd(Double sendFeeAdd) {
		this.sendFeeAdd = sendFeeAdd;
	}
	public Double getOpenFeeAdd() {
		return openFeeAdd;
	}
	public void setOpenFeeAdd(Double openFeeAdd) {
		this.openFeeAdd = openFeeAdd;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String getSubname() {
		return subname;
	}
	public void setSubname(String subname) {
		this.subname = subname;
	}
	public String getDescri1() {
		String str = descri.replaceAll("\n", "<br/>");
		return str;
	} 
	public String getDescri() { 
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getRoomUUID() {
		return roomUUID;
	}
	public void setRoomUUID(String roomUUID) {
		this.roomUUID = roomUUID;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
