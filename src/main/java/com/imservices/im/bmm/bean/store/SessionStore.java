package com.imservices.im.bmm.bean.store;


import com.google.common.collect.Lists;
import com.imservices.im.bmm.utils.redis.RedisService;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * 本地缓存
 */
public class SessionStore {

    /**根据用户ID保存  WebSocketSession*/
	private static Map<String, WebSocketSession> USERID_WS_MAP = new HashMap<>();
    /**根据用户ID保存 前端的APP_UUID*/
	public static Map<String, String> USERID_APPUUID_MAP = new HashMap<>();
    /**redis根据WebSocketSession保存用户ID*/
	public static Map<WebSocketSession, String> WS_USERID_MAP = new HashMap<>();
	/**redis中存放已经生成websocket的user所在服务器地址信息，提供远程调用*/
	public static final String REDIS_WSS_KEY="websocket:userid:address";
    /**在线用户*/
	public static final String ONLINE_MEMBER="online:member";
    /** 根据用户ID保存  WebSocketSession   userId#h5-<sessionId,session> </> */
    public static Map<String, Map<String,WebSocketSession>> USERID_SESSION_WS_MAP = new HashMap<>();
    /*** 保存sessionid 对应memberid*/
    public static Map<String, String> SESSIONID_MEMBERID_LIST_MAP = new HashMap<>();

	public static void USERID_WS_MAP_REMOVE(String key) {
		
		USERID_WS_MAP.remove(key);
	}

	/***需要优化**/
	public static boolean USERID_WS_MAP_containsKey(String key) {
		return USERID_WS_MAP.containsKey(key);
	}

	/***
	 * 获取本服务是否在线
	 * @param uid
	 * @return
	 */
	public static boolean isMemberOnline(String uid) {
		List<String> keys=USERID_SESSION_WS_MAP.keySet().stream().filter(key->key.indexOf(uid)>=0).collect(Collectors.toList());
		if(keys.size()>0){
			return true;
		}
		return true;
	}

	
	public static void USERID_WS_MAP_PUT(String key,WebSocketSession wss) {
		
		USERID_WS_MAP.put(key, wss);
	}

	/**
	 * 根据用户ID获取所有属于该用户的长链接
	 * @param uid
	 * @return
	 */
	public static List<WebSocketSession> USERID_WS_MAP_GET_ByUid(String uid) {
		List<WebSocketSession> list = Lists.newArrayList();
		if(USERID_SESSION_WS_MAP.containsKey(uid+"#android")) {
			list.addAll(getUserWebSocketList(uid+"#android"));
		}
		if(USERID_SESSION_WS_MAP.containsKey(uid+"#ios")) {
			list.addAll(getUserWebSocketList(uid+"#ios"));
		}
		if(USERID_SESSION_WS_MAP.containsKey(uid+"#H5")) {
			list.addAll(getUserWebSocketList(uid+"#H5"));
		}
		if(USERID_SESSION_WS_MAP.containsKey(uid+"#PC")) {
			list.addAll(getUserWebSocketList(uid+"#PC"));
		}
		return list;
	}
	


	/**
	 * 根据用户ID获取长链接，由于有特权用户的存在，所以返回的是集合
	 * @param key
	 * @return
	 */
	public static List<WebSocketSession> USERID_WS_MAP_GET_ByKey(String key) {
		if(USERID_SESSION_WS_MAP.containsKey(key)){
		return USERID_SESSION_WS_MAP.get(key).values().stream().collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
//	public static WebSocketSession USERID_WS_MAP_GET_ByKey(String key) {
//		return USERID_WS_MAP.get(key);
//	}

    /**
     * 存放用户WebSocketSession集合
     * @param memberId
     * @param sessionId
     * @param wss
     */
    public static void putUserWebSocketList(String memberId, String sessionId, WebSocketSession wss) {
        Map<String,WebSocketSession> wsMap=USERID_SESSION_WS_MAP.get(memberId);
        if(wsMap==null||wsMap.isEmpty()){
            wsMap=new HashMap<>();
            wsMap.put(sessionId,wss);
        }else{
            wsMap.put(sessionId,wss);
        }
        USERID_SESSION_WS_MAP.put(memberId,wsMap);
    }

    /**
     * 根据用户ID获取本地ws
     * @param memberId
     * @return
     */
    public static List<WebSocketSession> getUserWebSocketList(String memberId){
        Map<String,WebSocketSession> wsMap=USERID_SESSION_WS_MAP.get(memberId);
        if(wsMap==null||wsMap.isEmpty()){
            return null;
        }
        return new ArrayList<>(wsMap.values());
    }

    /***
     * 移除本地已经关闭的长链接，同时返回一个移除列表
     * @param memberId
     * @return
     */
    public static List<String> removerWebSocketIsClose(String memberId){
        List<WebSocketSession> list =USERID_WS_MAP_GET_ByUid(memberId);
        List<String> closeWsIds=new ArrayList<>();
        //对于已经关闭的长链接，移除出去
        for(int i=0;i<list.size();i++){
            WebSocketSession ws=list.get(i);
            if(!ws.isOpen()){
               closeWsIds.add(ws.getId());
                list.remove(i);
            }
        }
        return closeWsIds;
    }

	/**
	 * 移除存放在本地的长链接，同时需要移除注册在服务器的信息
	 * @param memberId
	 * @param sessionId
	 */
	public static void removerUserWebSocketList(String memberId, String sessionId) {
		Map wsMap=USERID_SESSION_WS_MAP.get(memberId);
		if(wsMap!=null||!wsMap.isEmpty()){
			wsMap.remove(sessionId);
			//如果已经完全移除，则同时把本地集合同时删除
			if(wsMap.isEmpty()){
				USERID_SESSION_WS_MAP.remove(memberId);
			}
		}
	}
	
}
 