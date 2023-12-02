package com.imservices.im.bmm.utils.web;

import com.imservices.im.bmm.bean.*;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BeanUtils {

	public static AccessRecordBean accessRecordToBean(AccessRecord e) {
		if(null==e) return null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
		Date now = new Date();
		Date yestoday = new Date();
		yestoday.setDate(yestoday.getDate()-1);
		
		AccessRecordBean o = new AccessRecordBean();
		o.setArid(e.getId());
		o.setContent("");
		o.setId(e.getEntityid());
		o.setTypeid(e.getTypeid());
		o.setImg(e.getImg());
		o.setTitle(e.getTitle());
		Date d = e.getCreateDate();
		if (null != d) {
			if (d.getYear() == now.getYear() && d.getMonth() == now.getMonth() && d.getDate() == now.getDate()) {
				o.setCreateDate(sdf1.format(d));
			} else if (d.getDate() == yestoday.getDate()) {
				o.setCreateDate("昨天");
			} else {
				o.setCreateDate(sdf2.format(d));
			}
			o.setCreateDateTime(d.getTime());
		}
		o.setContent("");
		o.setSubname(e.getSubname());
		
		if(SessionStore.isMemberOnline(e.getEntityid())) {
			o.setOnline(1);
		} 
		
		
		return o;
	}
	
	 
	public static MemberBean memberToBeanSimple(Member e) {
		if(null==e) return null;
		MemberBean mb = new MemberBean();
		mb.setMember_type(e.getMemberType());
		mb.setId(e.getId());
		if(StringUtils.isEmpty(e.getHeadpic())) {
			mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
		} else {
			mb.setHeadpic(e.getHeadpic());
		}
		mb.setMemberId(e.getMemberId());
		mb.setUsername(e.getUsername());
		mb.setNickName(e.getNickName());
		mb.setStatus(e.getStatus());
		mb.setSex(e.getSex());
		mb.setTelphone(e.getTelphone());
		mb.setMoney(e.getMoney()); 
		mb.setParent_uuid(e.getParent());
		mb.setOne_level_count(e.getOne_level_count());
		mb.setLastLoginDate(e.getLastLoginDate());
		mb.setShimingStatus(e.getShimingStatus());
		return mb;
	}
	
	
	public static RoomBean roomToBeanSimple(Room room) {
		if(null==room) return null;
		
		
		
		RoomBean b = new RoomBean();
		if(StringUtils.isEmpty(room.getHeadImg())) {
			b.setImg("/img_sys/group.png");
		} else {
			b.setImg(room.getHeadImg());
		}
		
		if(!StringUtils.isEmpty(room.getProps())) {
			  
			Map prosMap = JsonUtil.getMapFromJson(room.getProps());
			if(prosMap.containsKey("STOPSPEAK")) {
				b.setStopSpeak(Integer.valueOf(prosMap.get("STOPSPEAK").toString()));
			} else {
				b.setStopSpeak(0);
			}
			
		} else {
			b.setStopSpeak(0);
		}
		b.setUseCustomHeadpic(room.getUseCustomHeadpic());
		b.setMemberMgr_ids(room.getMemberMgr_ids());
		b.setYaoqingAble(room.getYaoqingAble());
		b.setYaoqingAuditAble(room.getYaoqingAuditAble());
		b.setMember_ids(room.getMember_ids());
		b.setStopspeak_member_ids(room.getStopspeak_member_ids());
		b.setId(room.getId());
		b.setRobotRule(room.getRobotRule());
		b.setGameStatus(room.getGameStatus());
		b.setRulePic(room.getRulePic()); 
		b.setName(room.getName());
		b.setRoomid(room.getRoomId());
		b.setRoomUUID(room.getId());
		b.setSubname(room.getSubname());
		b.setDescri(room.getDescri());
		b.setProperties(room.getProps());
//		b.setOpenFeeAdd(room.getOpenFeeAdd());
//		b.setSendFeeAdd(room.getSendFeeAdd());
//		b.setWinFeeAdd(room.getWinFeeAdd()); 
//		b.setUnDead(room.getUnDead()); 
//		b.setGameType(room.getGameType().name());
		b.setStatus(room.getStatus());
		b.setPsw(room.getPsw());
		b.setControlGame(room.getControlGame());
		b.setControlModel(room.getControlModel());
		b.setPlayerSendWinRate_s1(room.getPlayerSendWinRate_s1());
		b.setPlayerOpenWinRate_s1(room.getPlayerOpenWinRate_s1());
		b.setOwner(room.getOwner());
		b.setOwner_UUID(room.getOwner_UUID());
		b.setLimitNum(room.getLimitNum());
		b.setFixedRobotIds(room.getFixedRobotIds());
		b.setEndDate(room.getEndDate()); 
		b.setIndependence(room.getIndependence());
		b.setMaxRobotCount(room.getMaxRobotCount());
		b.setOpenRedDelayStart(room.getOpenRedDelayStart());
		b.setOpenRedDelayEnd(room.getOpenRedDelayEnd());
		b.setAgentRedFanDian(room.getAgentRedFanDian());
		return b;
	}
 

	
	public static Room roomBeanTransferToRoomSimple(RoomBean rb) {
		Room room = new Room();
		room.setHeadImg(rb.getImg());
		room.setRoomId(rb.getRoomUUID());
		room.setId(rb.getId());
		room.setDescri(rb.getDescri());
		room.setControlGame(rb.getControlGame());
		room.setControlModel(rb.getControlModel());
		room.setAgentRedFanDian(rb.getAgentRedFanDian());
		room.setDomain(rb.getDomain());
		room.setEndDate(rb.getEndDate());
		room.setMember_ids(rb.getMember_ids());
		room.setMemberMgr_ids(rb.getMemberMgr_ids());
		room.setLimitNum(rb.getLimitNum());
		room.setName(rb.getName());
		room.setOwner(rb.getOwner());
		room.setOwner_UUID(rb.getOwner_UUID());
		room.setLimitNum(rb.getLimitNum());
		room.setStatus(rb.getStatus());
		room.setYaoqingAble(rb.getYaoqingAble());
		room.setYaoqingAuditAble(rb.getYaoqingAuditAble());
		room.setUseCustomHeadpic(rb.getUseCustomHeadpic());
		room.setStopspeak_member_ids(rb.getStopspeak_member_ids());
		room.setSubname(rb.getSubname());
		room.setUnDead(rb.getUnDead());
		room.setPsw(rb.getPsw());
		return room;
	}

}
