package com.imservices.im.bmm.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="role")
@SuppressWarnings("all")
public class Role extends BaseEntity {
//	public static final Map<Enum, String> AuthMap;
	public static final Map<Enum, String> AuthMapRequestUri;

	static {

//		AuthMap = new HashMap<Enum, String>();
//		AuthMap.put(Product.list, "商品列表"); 

		AuthMapRequestUri = new HashMap<Enum, String>();
		AuthMapRequestUri.put(SystemMgr.config,"/admin/config,/admin/config/update");
		AuthMapRequestUri.put(SystemMgr.online_users,"/admin/member/online/list");
		AuthMapRequestUri.put(SystemMgr.slideImage,"/admin/slideImage/list");
		
		AuthMapRequestUri.put(UserMgr.user,"/admin/member/list?type=USER");
		AuthMapRequestUri.put(UserMgr.agent,"/admin/member/list?type=AGENT");
		AuthMapRequestUri.put(UserMgr.robot,"/admin/member/list?type=ROBOT");
		AuthMapRequestUri.put(UserMgr.login_log,"/admin/member/loginLog/list?type=USER");
		
		AuthMapRequestUri.put(RechargeWithdrawMgr.sys_money_opt,"/admin/member/moneyOpt/list");
		AuthMapRequestUri.put(RechargeWithdrawMgr.agent_money_opt,"/admin/member/moneyOpt/list?dataType=2");
		AuthMapRequestUri.put(RechargeWithdrawMgr.recharge,"/admin/member/recharge/list");
		AuthMapRequestUri.put(RechargeWithdrawMgr.withdraw,"/admin/member/withdraw/list");
		AuthMapRequestUri.put(RechargeWithdrawMgr.moneyDetailRecord,"/admin/money_detail_record/list");
		
		
		AuthMapRequestUri.put(RoomMgr.jielong,"/admin/room/list?gameType=jielong");
		AuthMapRequestUri.put(RoomMgr.niuniu,"/admin/room/list?gameType=niuniu");
		AuthMapRequestUri.put(RoomMgr.saolei,"/admin/room/list?gameType=saolei");
		AuthMapRequestUri.put(RoomMgr.jinqiang,"/admin/room/list?gameType=jinqiang");
		AuthMapRequestUri.put(RoomMgr.robot,"/admin/robot/list");
		
		
		AuthMapRequestUri.put(RedMgr.jielong_send,"/admin/redPacket/list?gameType=jielong");
		AuthMapRequestUri.put(RedMgr.niuniu_send,"/admin/redPacket/list?gameType=niuniu");
		AuthMapRequestUri.put(RedMgr.saolei_send,"/admin/redPacket/list?gameType=saolei");
		AuthMapRequestUri.put(RedMgr.jinqiang_send,"/admin/redPacket/list?gameType=jinqiang");
		AuthMapRequestUri.put(RedMgr.jielong_open,"/admin/redPacket/detail/list?gameType=jielong");
		AuthMapRequestUri.put(RedMgr.niuniu_open,"/admin/redPacket/detail/list?gameType=niuniu");
		AuthMapRequestUri.put(RedMgr.saolei_open,"/admin/redPacket/detail/list?gameType=saolei");
		AuthMapRequestUri.put(RedMgr.jinqiang_open,"/admin/redPacket/detail/list?gameType=jinqiang");
		
		AuthMapRequestUri.put(DataMgr.agent_tichen,"/admin/tichen/list");
		AuthMapRequestUri.put(DataMgr.user_zhanji,"/admin/zhanji/list?uType=USER");
		AuthMapRequestUri.put(DataMgr.robot_zhanji,"/admin/zhanji/list?uType=ROBOT");
		AuthMapRequestUri.put(DataMgr.user_reward,"/admin/reward/list?uType=USER");
		AuthMapRequestUri.put(DataMgr.robot_reward,"/admin/reward/list?uType=ROBOT");
		
		
		AuthMapRequestUri.put(AdminMgr.admin,"/admin/list");
		AuthMapRequestUri.put(AdminMgr.role,"/admin/role/list");
		
	}

	//平台管理
	public enum SystemMgr {
		config/**系统配置**/, online_users/**在线用户**/,slideImage/**图片轮番**/
	}
	
	//会员管理
	public enum UserMgr {
		  user/**会员管理**/,agent/**代理**/,robot/**机器人**/,login_log/**用户登陆日志**/
	}
	
	//充值提现管理
	public enum RechargeWithdrawMgr {
		sys_money_opt/**管理员手工加款扣款**/,agent_money_opt/**代理上下分记录**/,recharge/**充值记录**/,withdraw/**提现记录**/,moneyDetailRecord/**资金明细**/
	}
	
	//房间管理
	public enum RoomMgr {
		jielong/**接龙房间**/,niuniu/**牛牛房间**/,saolei/**扫雷房间**/,jinqiang/**禁抢房间**/,robot/**机器人分配**/
	}
	
	//红包管理
	public enum RedMgr {
		jielong_send/**接龙发包**/,niuniu_send/**牛牛发包**/,saolei_send/**扫雷发包**/,jinqiang_send/**禁抢发包**/
		,jielong_open/**接龙抢包**/,niuniu_open/**牛牛抢包**/,saolei_open/**扫雷抢包**/,jinqiang_open/**禁抢抢包**/
	}
	
	//数据统计
	public enum DataMgr {
		agent_tichen/**代理提成统计**/,user_zhanji/**玩家游戏统计**/,robot_zhanji/**机器游戏统计**/,user_reward/**玩家奖励统计**/,robot_reward/**机器奖励统计**/
	}
	
	//管理员管理
	public enum AdminMgr {
		admin/**管理员**/, role/**角色权限**/
	}
	
	public static final String ROLE_SESSION = "ROLE_SESSION";
	private String name;
	private String description;
	private Boolean isSystem=false;
	private String auths;
	private Set<Admin> adminSet;

	public Role(){}
	
	public Role(String rid) {
		setId(rid);
	}

	@Transient
	private  Enum getEnumByName(String name) {
		String[] as = name.split("\\.");
		Enum e = null;
		if(SystemMgr.class.getSimpleName().equals(as[0])) {
			e = SystemMgr.valueOf(as[1]);
		} else if(UserMgr.class.getSimpleName().equals(as[0])) {
			e = UserMgr.valueOf(as[1]);
		} else if(RechargeWithdrawMgr.class.getSimpleName().equals(as[0])) {
			e = RechargeWithdrawMgr.valueOf(as[1]);
		} else if(RoomMgr.class.getSimpleName().equals(as[0])) {
			e = RoomMgr.valueOf(as[1]);
		} else if(RedMgr.class.getSimpleName().equals(as[0])) {
			e = RedMgr.valueOf(as[1]);
		} else if(DataMgr.class.getSimpleName().equals(as[0])) {
			e = DataMgr.valueOf(as[1]);
		} else if(AdminMgr.class.getSimpleName().equals(as[0])) {
			e = AdminMgr.valueOf(as[1]);
		} 
		return e;
	}
	
	@Transient
	private String getEnumName(Enum e) {
		return e.getDeclaringClass().getSimpleName()+"."+e.name();
	}

	@Transient
	public List<Enum> getAuthList() {
		List<Enum> list = new ArrayList<Enum>();
		String names = "";
		if (StringUtils.isEmpty(auths))
			return list;
		String[] as = auths.split(",");
		for (String a : as) {
			if (StringUtils.isEmpty(a)) continue;
			list.add(getEnumByName(a));

		}
		return list;
	}
 
	@Transient
	public boolean containAuth(Enum e) {
		if (null != isSystem && isSystem) return true;// 超级管理员
		if (StringUtils.isEmpty(auths)) return false;
		if (auths.indexOf(getEnumName(e)) >= 0) {
			return true;
		}
		return false;
	}




	@Transient
	public void putAuth(Enum e) {
		String name = getEnumName(e);
		if (StringUtils.isEmpty(auths)) {
			auths = name;
		} else {
			auths += ("," + name);
		}
	}

	@Transient
	public void removeAuth(Enum e) {
		String name = getEnumName(e);
		if (StringUtils.isEmpty(auths)) {
			return;
		} else {
			if (auths.indexOf(name) >= 0) {
				auths = auths.replaceAll("," + name, "").replaceAll(name, "");
			}
		}
	} 

	// 是否有权限
	@Transient
	public boolean isAuth(String reqUri) {
		if (null != isSystem && isSystem) return true;// 超级管理员
		List<Enum> elist = getAuthList();
		if (null == elist || elist.isEmpty()) return false;
		for (Enum e : elist) {
			String urlStr = AuthMapRequestUri.get(e);
			if (urlStr.indexOf(reqUri) >= 0) {
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
	public Set<Admin> getAdminSet() {
		return this.adminSet;
	}

	public void setAdminSet(Set<Admin> adminSet) {
		this.adminSet = adminSet;
	}

	public Boolean getIsSystem() {
		return this.isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}

	@Column(length = 20000)
	public String getAuths() {
		if(StringUtils.isEmpty(auths)) return "";
		return auths;
	}

	public void setAuths(String auths) {
		this.auths = auths;
	}

}