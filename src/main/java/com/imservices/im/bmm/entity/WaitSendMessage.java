package com.imservices.im.bmm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name="waitsendmessage")
@SuppressWarnings("all")
public class WaitSendMessage extends BaseEntity {
	
	private String uuid;
	private String sub_txt;//特殊情况下使用 如语音需要记录多少秒
	private String psr="";//解析方式  默认使用rich-text   还有其它的选择uparse voice(语音) video(视频)
	private String chatid;//用于标识此信息属于与哪个用户或群的聊天记录
	private String type;
	private String toUid;
	private String toGroupid;
	private String fromUid;
	private String headpic;
	private String name; 
	private String content;
	private String oldContent;
	private String date;
	private String cmd;
	private String simple_content;
	
	//针对红包
	private Integer number;
	private BigDecimal money;//玩家能抢的金额之和
	private BigDecimal sendMoney;//发包的总金额
	private String descri;
	private String descri1;
	private Integer redType=1;//只针对群发红包 1：普通红包，每个人的金额是一样的  2拼手气红包
	private String redUUID;//红包数据库ID
	
	//针对转账
	private String utid;//userTransfer的ID
	private String expiredTimeStr = "";//过期 如1天 1分钟 20秒等等，主要显示于确认收款时。告诉用户多长时间过期
	
	//针对名片
	private String mheadpic;
	private String mname;
	private String mid;
	private String muuid;
	
	

	
	public String getMheadpic() {
		return mheadpic;
	}
	public void setMheadpic(String mheadpic) {
		this.mheadpic = mheadpic;
	}
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMuuid() {
		return muuid;
	}
	public void setMuuid(String muuid) {
		this.muuid = muuid;
	}
	public String getExpiredTimeStr() {
		return expiredTimeStr;
	}
	public void setExpiredTimeStr(String expiredTimeStr) {
		this.expiredTimeStr = expiredTimeStr;
	}
	public String getUtid() {
		return utid;
	}
	public void setUtid(String utid) {
		this.utid = utid;
	}
	@Column(length=5000)
	public String getSimple_content() {
		return simple_content;
	}
	public void setSimple_content(String simple_content) {
		this.simple_content = simple_content;
	}
	public String getToGroupid() {
		return toGroupid;
	}
	public void setToGroupid(String toGroupid) {
		this.toGroupid = toGroupid;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public BigDecimal getSendMoney() {
		return sendMoney;
	}
	public void setSendMoney(BigDecimal sendMoney) {
		this.sendMoney = sendMoney;
	}
	public String getDescri() {
		return descri;
	}
	public void setDescri(String descri) {
		this.descri = descri;
	}
	public String getDescri1() {
		return descri1;
	}
	public void setDescri1(String descri1) {
		this.descri1 = descri1;
	}
	public Integer getRedType() {
		return redType;
	}
	public void setRedType(Integer redType) {
		this.redType = redType;
	}
	public String getRedUUID() {
		return redUUID;
	}
	public void setRedUUID(String redUUID) {
		this.redUUID = redUUID;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Column(length=5000)
	public String getOldContent() {
		return oldContent;
	}
	public void setOldContent(String oldContent) {
		this.oldContent = oldContent;
	}
	public String getSub_txt() {
		return sub_txt;
	}
	public void setSub_txt(String sub_txt) {
		this.sub_txt = sub_txt;
	}
	public String getPsr() {
		return psr;
	}
	public void setPsr(String psr) {
		this.psr = psr;
	}
	public String getChatid() {
		return chatid;
	}
	public void setChatid(String chatid) {
		this.chatid = chatid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getToUid() {
		return toUid;
	}
	public void setToUid(String toUid) {
		this.toUid = toUid;
	}
	public String getFromUid() {
		return fromUid;
	}
	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}
	public String getHeadpic() {
		return headpic;
	}
	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(length=10000)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	
	
}
