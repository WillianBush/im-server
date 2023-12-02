package com.imservices.im.bmm.utils.web;

import com.imservices.im.bmm.entity.Admin;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("all")
public class EntityUtils {

	 
	//根据前台属性更新
	public static void updateAttributes(Object entity,HttpServletRequest request) throws Exception {
		Map<String,String[]> map = request.getParameterMap();
		Field[] fields = entity.getClass().getDeclaredFields();
		if(ArrayUtils.isEmpty(fields)) return;
		for(Field field : fields) { 
			String key = field.getName();
			if(!map.containsKey(key)) continue;
			field.setAccessible(true);//允许私有属性访问
			if(StringUtils.isEmpty(map.get(key)[0])) {
				field.set(entity, null);
				continue;
			}
			Object value = null;
			if(field.getType() == Integer.class) {value = Integer.valueOf( map.get(key)[0]);}
			else if(field.getType() == Double.class) {value = Double.valueOf( map.get(key)[0]);}
			else if(field.getType() == Float.class) {value = Float.valueOf( map.get(key)[0]);}
			else if(field.getType() == Long.class) {value = Long.valueOf( map.get(key)[0]);}
			else if(field.getType() == Boolean.class) {value = Boolean.valueOf( map.get(key)[0]);}
			else if(field.getType() == Date.class) {
				value = Integer.valueOf( map.get(key)[0]);
				//如果参数为日期类型
				if (value.toString().indexOf(":") > 0) {
					//值 为日期+时间
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					value = sdf.parse(value.toString());
				} else {
					//值仅 为日期
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					value = sdf.parse(value.toString());
				}
			}
			else {
				value = map.get(key)[0];
			}
			field.set(entity, value);
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		Admin admin = new Admin();
		Field field = admin.getClass().getDeclaredField("username");
		field.setAccessible(true);
		//
		//
		//
		field.set(admin,"liruikai");
		//
	}
	
}
