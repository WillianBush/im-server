package com.imservices.im.bmm.constant;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("all")
public class RoomConstant {
	
	//基本属性
	public final Map<String, String> BASE_PROPERTIES = new LinkedHashMap<String, String>(){{
		put("EXPIRETIME","红包过期秒数#INTEGER#60");
		put("STOPSPEAK","禁言#INTEGER#1");//0否 1是
	}};

	
	
	//根据游戏类型获取默认游戏属性
	public static Map<String, String>  getDefaultPros(String gameType) throws Exception {
		Field field1 = RoomConstant.class.getDeclaredField("BASE_PROPERTIES");
		Field field2 = RoomConstant.class.getDeclaredField(gameType.toUpperCase()+"_PROPERTIES");
		Map<String, String> map1 = (Map<String, String>) field1.get(RoomConstant.class.newInstance());
		Map<String, String> map2 = (Map<String, String>) field2.get(RoomConstant.class.newInstance());
		map1.putAll(map2);
		return map1;
	}

}
  