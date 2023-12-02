package com.imservices.im.bmm.websocket;

import lombok.ToString;

@ToString
public class Message implements Cloneable {
	
	public  enum CLIENT_TYPE {
		PC,H5,android,ios
	}
	
	public enum CMD_ENUM {
		FRIENDSADD/**添加好友**/
		,AR_INSERT/**联系记录插入(首页)**/
		,AR_UPDATE/**联系记录更新(首页)**/
		,PUTSESSION/**添加session联系**/
		,USER_CHAT_SEND_TXT/**用户单聊发信息**/
		,GROUP_CHAT_SEND_TXT/**群组发信息**/
		,GROUP_CHAT_SEND_RED/**群发红包**/
		,GROUP_CHAT_OPEN_RED/**群抢红包**/
		,LOGIN/**用户登陆**/,ERROR/**错误信息**/
		,CHAT_SYS_TXT/**系统信息**/
		,GROUP_CHAT_RED_FINISH/**群红包抢完并结算完成**/
		,GROUP_CHAT_SEND_RED_SUCCESS/**群发包完成**/
		,CHAT_RED_EXPIRED/**红包过期**/
		,RED_DETAIL_LIST/**抢包详细列表**/
		,GROUP_CHAT_RED_OPEN_END/**群红包被抢完了**/
		,GROUP_CHAT_RED_OPENED/**此红包已抢过**/
		,GROUP_CHAT_RED_OPEN_SUCCESS/**抢包成功**/
		,GROUP_CHAT_RED_BROADCAST_UPDATE/**传播更新红包状态、数量等等,一般用于玩家抢包后，需要更新同房间的其他会员此红包的属性**/
		,STARTGAME/**开始游戏(牛牛/三公为抢庄)**/
		,NIUNIU_SENDER_PASSTIME/**牛牛庄家时间已过,可重新抢庄**/
		,NIUNIU_RE_SENDER/**牛牛可重新抢庄**/
		,JIELONG_RE_SENDER/**接龙可重新发开始民**/
		,LOGIN_USER_REMOVE/**登陆用户被删除**/
		,SANGONG_SENDER_PASSTIME/**三公庄家时间已过,可重新抢庄**/
		,SANGONG_RE_SENDER/**三公可重新抢庄**/
		,G28_SENDER_PASSTIME/**28杠庄家时间已过,可重新抢庄**/
		,G28_RE_SENDER/**28杠可重新抢庄**/
		,LOGIN_USER_STATUS_BAN/**登陆用户状态被修改为禁止**/
		,LOGIN_USER_MODIFY_HEADPIC/**登陆用户头像被修改**/
		,LOGIN_USER_MODIFY_NN/**登陆用户昵称被修改**/
		,OTHER_LOGIN/**别处登陆冲线**/
		,UPDATE_ADDRESS_BOOK/**通知客户端更新通讯录好友数据**/
		,USER_CHAT_SEND_VOICE,GROUP_CHAT_SEND_VOICE/**发语音**/
		,USER_CHAT_MESSAGE,GROUP_CHAT_MESSAGE/****/
		,GROUP_MEMBER_REMOVE/**群组成员移除**/
		,PING/**心跳**/
		,ROOMADD/**群成员申请**/
		,CHAT_MSG_UNDO/**信息撤消**/
		,CHAT_MSG_UNDO_MGR/**管理信息撤消**/
		,CHAT_SEND_RED_SUCCESS/**发红包成功**/
		,USER_CHAT_SEND_RED/**用户私发红包 **/
		,USER_CHAT_OPEN_RED/**好友抢包**/
		,CHAT_RED_OPEN_SUCCESS
		,CHAT_RED_BROADCAST_UPDATE
		,RED_MUST_UPDATE_FOR_MEMBER/**用户红包更新，主要是用户收到红包后下线。下次上次再更新**/
		,USER_CHAT_TRANSFER/**转账**/
		,CHAT_SEND_TRANSFER_SUCCESS/**发送转账成功**/
		,CHAT_TRANSFER_EXPIRED/**转账过期**/
		,TRANSFER_MUST_UPDATE_FOR_MEMBER/**转账更新，主要是用户收到转账后下线。下次上次再更新**/
		,CHAT_TRANSFER_FINISHED/**转账完成**/
		,USER_CHAT_TRANSFER_RECEIVE/**转账接收**/
		,CHAT_SEND_CARD/**好友发名片**/
		,FRIEND_OFFLINE/**朋友下线**/
		,FRIEND_ONLINE/**朋友上线**/
		,CLEARCHATMSG_SINGLE_CLOUD/**清空云端聊天记录**/
		,CLEARCHATMSG/**清空聊天记录**/
		,SHOW_INPUT_ING/**显示输入中**/
		,HIDE_INPUT_ING/**取消输入中**/
		,CHAT_MSG_READED/**信息已读**/
		,APP_PUSH_USER_INFO/**APP推送个人clientid等信息保存**/
		,ROOM_YAOQI/**群组邀请**/,AITE/**@-群会员**/
		,APP_HIDE_SHOW/**app处于前台还是后台**/
		,CLEAR_CHAT_MSG_DATA_MGR/**清空聊天记录,一般用于后台执行清空命令后，通知客户端清空操作**/
	}
	
	//无、云端数据,发送检验
	public enum MessageAct {
		none,cloudStorageData,checkSendData
	}
	
	private String CMD;
	private Object body; 
	private String act= MessageAct.none.name();//默认为空,当为cloudStorageData时为云端数据存储
	
	
	
	
	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	@Override
	public Message clone() {
		Message o = null;
		try{   
			o = (Message)super.clone();
			//
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public String getCMD() {
		return CMD;
	}
	public void setCMD(String cMD) {
		CMD = cMD;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
}
