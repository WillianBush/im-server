package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.MsgHistoryEntity;
import com.imservices.im.bmm.entity.WaitSendMessage;
import com.imservices.im.bmm.entity.WaitUpdateRed;

import java.util.List;

public interface ChatService {

	  List<WaitSendMessage> getWSMList(String[] ps, Object[] vs) throws Exception;
	  void deleteWSM(WaitSendMessage o) throws Exception;
	  void saveWSM(WaitSendMessage o) throws Exception;
	  void deleteWSMbyHql(String hql) throws Exception;
	  void saveWUR(WaitUpdateRed o) throws Exception;
	  Long wsmCount(String[] ps, Object[] vs) throws Exception;
	  void saveMsgHistory(MsgHistoryEntity msgHistoryEntity) throws Exception;
	  void deleteWSM(String[] ids) throws Exception;
	  void deleteWSM(String id) throws Exception;
	  void deleteByToUid(String toUid) throws Exception;

	/**
	 * 根据发送和接收ID，获取消息记录
	 * @param forMemberId
	 * @param toMmeberId
	 * @return
	 */
	Pager<MsgHistoryEntity> getMsgHistoryPageList(String forMemberId,String toMmeberId,Pager pager)throws Exception;
}
