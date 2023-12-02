package com.imservices.im.bmm.utils;

import com.imservices.im.bmm.bean.AppPush;
import com.imservices.im.bmm.bean.PushMessage;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//NotificationTemplate
//为【通知模板】打开应用首页 在通知栏显示一条通知，用户点击后打开应用的首页。
//针对沉默用户，发送推送消息，点击消息栏的通知可直接激活启动应用，提升应用的转化率。

//TransmissionTemplate
//【透传模板】自定义消息
//透传消息是指消息传递到客户端只有消息内容，展现形式由客户端自行定义。客户端可自定义通知的展现形式，也可自定义通知到达之后的动作，或者不做任何展现。

//因为我们需求是。如果用户APP在前台。则不需要推送，APP在后台需要推送。所以选择TransmissionTemplates模板，由APP决定是否创建通知栏消息

public class AppPushUtils {
	private static String url = "http://api.getui.com/apiex.htm";

    public static void main(String[] args) throws IOException {
//    	pushMessage("测试一下");
    }
	
    
    
    
    
    /**
     * 推送消息
     */
    public static Map<String, Object> pushMessageToSimpleApp(PushMessage pm, AppPush appPush) {
        IGtPush push = new IGtPush(url, appPush.getAppkey(),appPush.getMasterSecret());

        
        // 使用透传模板
        TransmissionTemplate template = getTransmissionTemplate(pm,appPush);
        
        String intent = "intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;package=io.shike.app;component=xxx;end";
        Notify notify = new Notify();
        notify.setPayload("前台跳转链接内容");
        // 通知栏显示标题
        notify.setTitle(pm.getTitle());
        // 通知栏内容
        notify.setContent(pm.getContent());
        notify.setIntent(intent);
        // 设置第三方通知
        template.set3rdNotifyInfo(notify);

        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线
        message.setOffline(true);
        // 离线消息有效时间为7天
        message.setOfflineExpireTime(1000 * 3600 * 24 * 7);
        //添加要推送的终端
        Target target = new Target();
        target.setAppId(appPush.getAppid());
        target.setClientId(appPush.getClientid());
        IPushResult result;
        Map<String, Object> response = null;
        // 执行推送
        try {
            result = push.pushMessageToSingle(message, target); 
        } catch (RequestException e) {
            e.printStackTrace();
            result = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (result != null) {
        	//
            response = result.getResponse();
        }
        return response;
    }
     
    
    public static TransmissionTemplate getTransmissionTemplate(PushMessage pm, AppPush appPush) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appPush.getAppid());
        template.setAppkey(appPush.getAppkey());
//        搭配transmissionContent使用，可选值为1、2；
//        1：立即启动APP（不推荐使用，影响客户体验）
//        2：客户端收到消息后需要自行处理
        template.setTransmissionType(2);
        template.setTransmissionContent(pm.getContent());
        //template.setAPNInfo(getAPNPayload()); //详见本页iOS通知样式设置
        return template;
    }
    
//    需要注意的是：如果使用NotificationTemplate 也就是普通模板，如果推送的app在线，用户点击推送消息是不会跳转到app的，他只对沉默用户有用，所以建议使用透传摸版。
//	public static void pushMessageToSimpleApp(PushMessage pm, AppPush appPush) {
//		 	IGtPush push = new IGtPush(url, appPush.getAppkey(), appPush.getMasterSecret());
//
//	        Style0 style = new Style0();
//	        // STEP2：设置推送标题、推送内容
//	        style.setTitle(pm.getTitle());
//	        style.setText(pm.getContent());
////	        style.setLogo("push.png");  // 设置推送图标
//	        // STEP3：设置响铃、震动等推送效果
//	        style.setRing(true);  // 设置响铃
//	        style.setVibrate(true);  // 设置震动
//
//
//	        // STEP4：选择通知模板
//	        NotificationTemplate template = new NotificationTemplate();
//	        template.setAppId(appPush.getAppid());
//	        template.setAppkey(appPush.getAppkey());
//	        template.setStyle(style);
//
//
//	        SingleMessage message = new SingleMessage();
//	        message.setData(template);
//	        // 设置消息离线
//	        message.setOffline(true);
//	        // 离线消息有效时间为7天
//	        message.setOfflineExpireTime(1000 * 600);  // 时间单位为毫秒
//
//	        Target target = new Target();
//	        target.setAppId(appPush.getAppid());
//	        target.setClientId(appPush.getClientid());
//	        
//	        // STEP6：执行推送
//	        IPushResult ret = push.pushMessageToSingle(message,target);
//	        
//		
//	}
	 
	 
	public static void pushMessageToAllApp(PushMessage pm,String appId,String appKey,String masterSecret) {
	 	IGtPush push = new IGtPush(url, appKey, masterSecret); 

        Style0 style = new Style0();
        // STEP2：设置推送标题、推送内容
        style.setTitle(pm.getTitle());
        style.setText(pm.getContent());
//        style.setLogo("push.png");  // 设置推送图标
        // STEP3：设置响铃、震动等推送效果
        style.setRing(true);  // 设置响铃
        style.setVibrate(true);  // 设置震动


        // STEP4：选择通知模板
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setStyle(style);


        // STEP5：定义"AppMessage"类型消息对象,设置推送消息有效期等推送参数
        List<String> appIds = new ArrayList<String>();
        appIds.add(appId);
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);  // 时间单位为毫秒

        
        // STEP6：执行推送
        IPushResult ret = push.pushMessageToApp(message);
        
	
}
	
//	ap.getAppid():WtHz5HIBMQA1mAl1cOPvL2
//	ap.getAppkey():pLYCPZD3cu9CQYwkv0oBn4
//	ap.getClientid():1c3cd3638a7c15192537611ac6e1052e
//	ap.getAppid():WtHz5HIBMQA1mAl1cOPvL2
//	ap.getAppkey():pLYCPZD3cu9CQYwkv0oBn4
//	ap.getClientid():1c3cd3638a7c15192537611ac6e1052e
    
//	public static void main(String[] args) {
//		PushMessage pm = new PushMessage();
//		pm.setTitle("信息提示");
//		pm.setContent("给你发红包");
//		pm.setUserRole("");
//		
//		AppPush ap = new AppPush();
//		ap.setAppid("WtHz5HIBMQA1mAl1cOPvL2");
//		ap.setAppkey("pLYCPZD3cu9CQYwkv0oBn4");
//		ap.setClientid("1c3cd3638a7c15192537611ac6e1052e");
//		List<AppPush> list = Lists.newArrayList();
//		list.add(ap);
//		AppPushUtils.pushMessage(pm,list);
//	}

}
