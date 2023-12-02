package com.imservices.im.bmm.bean.store;

import com.imservices.im.bmm.bean.AppPush;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.websocket.Message;
import java.lang.reflect.Method;
import java.util.*;

public class ChatStore {
	public static Map<String,String> BLACK_LIST = new HashMap<String, String>();//黑名单列表  《《用户ID，黑名单IDS》》  
	public static Map<String,Map<String, MemberBean>> GROUP_MEMBER_MAP = new HashMap<String, Map<String, MemberBean>>();//群用户【《群ID，《用户ID，用户bean》》】这样设计的目的在于快速查找具体用户清除等操作也便于群发信息
	public static Map<String, MemberBean> USER_BEAN_MAP = new HashMap<String, MemberBean>();//根据用户ID保存  memberBean【平台登陆的玩家】
	public static Map<String, MemberBean> ROBOT_BEAN_MAP = new HashMap<String, MemberBean>();//根据用户ID保存  memberBean【已分配的机器人】
	public static Map<String, MemberBean> FREE_ROBOT_BEAN_MAP = new HashMap<String, MemberBean>();//根据用户ID保存  memberBean【未分配的机器人】

	public static Map<String,RoomBean> ROOMB_BEAN_MAP = new HashMap<String, RoomBean>();//根据房间ID保存 roomBean

	public static Map<String,String> ONLINE_SUPER_USER = new HashMap<String, String>();//在线的超级用户《用户ID， 空字符即可》
	public static Map<String,AppPush> USER_APPPUSH = new HashMap<String, AppPush>();//用户ID，appPush【用于APP推送】
	
	
	public static Map<String,List<Message>> WAIT_SEND_MESSAGE = new HashMap<String, List<Message>>();//用户ID，List<Message>【用于未端，发消息时，客户端临时断线引起的消息发送不的情况】
	


	
	public static void modifyMemberBeanProperties(String uuid,Map<String,Object> newProsMap) {
		
		try {
			List<MemberBean> mblist = new ArrayList<MemberBean>();
			for(String key : newProsMap.keySet()) {
				String method_name = "set" + key.substring(0, 1).toUpperCase() + key.substring(1, key.length());
				Class typeClass = MemberBean.class.getDeclaredField(key).getType();//参数所属类型
				Method m1 = null;
				if (typeClass == Integer.class) {
					m1 = MemberBean.class.getMethod(method_name, Integer.class);
				} else if (typeClass == Long.class) {
					m1 = MemberBean.class.getMethod(method_name,Long.class );
				} else if (typeClass == Boolean.class) {
					m1 = MemberBean.class.getMethod(method_name,Boolean.class );
				} else if (typeClass == Double.class) {
					m1 = MemberBean.class.getMethod(method_name, Double.class);
				} else if (typeClass == Float.class) {
					m1 = MemberBean.class.getMethod(method_name, Float.class);
				} else if (typeClass == Date.class) {
					m1 = MemberBean.class.getMethod(method_name, Date.class);
				} else {
					m1 = MemberBean.class.getMethod(method_name, String.class);
				}
				if(!mblist.isEmpty()) {
					for(MemberBean mb : mblist) {
						m1.invoke(mb, newProsMap.get(key));
					}
				} else {
					if(USER_BEAN_MAP.containsKey(uuid)) {
						MemberBean mb = USER_BEAN_MAP.get(uuid);
						m1.invoke(mb, newProsMap.get(key));
						mblist.add(mb);
					}
					if(ROBOT_BEAN_MAP.containsKey(uuid)) {
						MemberBean mb = ROBOT_BEAN_MAP.get(uuid);
						m1.invoke(mb, newProsMap.get(key));
						mblist.add(mb);
					}
					if(FREE_ROBOT_BEAN_MAP.containsKey(uuid)) {
						MemberBean mb = FREE_ROBOT_BEAN_MAP.get(uuid);
						m1.invoke(mb, newProsMap.get(key));
						mblist.add(mb);
					}
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	
	
	
}
 