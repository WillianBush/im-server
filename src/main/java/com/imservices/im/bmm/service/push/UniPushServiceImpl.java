package com.imservices.im.bmm.service.push;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
//@Slf4j
public class UniPushServiceImpl implements UniPushService {
	
//	private String host="http://api.getui.com/apiex.htm";
//	private  String appId="PY23ZekQ3a8LXXV6NM9vG6";
//	private  String appKey="mwk6G0grdE9h6Rhzty39u9";
//	private  String appSecret="CT9d9WKiAh8p0JImHVvju7";
//	private  String masterSecret="GY9xHh8KBX5X1oyMt9b1N1";
//	private  String app_component="uni.UNI0A3411E";
	 
    @Value("${gexin.host}") 
    private String host="";
    @Value("${gexin.appId}")
    private  String appId="";
    @Value("${gexin.appkey}")
    private  String appKey="";
    @Value("${gexin.appSecret}")
    private  String appSecret="";
    @Value("${gexin.masterSecret}")
    private  String masterSecret="";
    @Value("${app.component}")
    private  String app_component="";

/****
    @Override
    public IPushResult singlePush(AppPush ap,PushMessage pm,Integer isAlias) throws NullPointerException  { //isAlias，1是根据cid推送，2是别名推送，  type的值：1为离线模板透传，2为通知
    	
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        
        SingleMessage message = new SingleMessage();
//        message.setStrategyJson("{\"default\":4,\"ios\":4,\"st\":4}");//1:个推通道优先，在线经个推通道下发，离线经厂商下发(默认);但是uniapp，没法这样离线推送
//        离线有效时间，单位为毫秒，可选，但是有时候离线会失效？
        message.setOffline(true);
        message.setOfflineExpireTime(72 * 3600 * 1000);
        boolean result = false;
		try {
			result = setMessageData(message, pm, ap);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        if(!result){
            return null;
        }
        
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        if(isAlias==1){
            target.setClientId(ap.getClientid());
        } else if(isAlias==2){
            target.setAlias(ap.getClientid());
        }
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();//推送失败时，重推。
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        
        if (ret != null) {
            
        } else {
            
        }
        return ret;
    }

    //别名绑定
    @Override
    public IAliasResult bindAlias(String alias, String clientId) {
    	
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        IAliasResult iAliasResult = push.bindAlias(appId, alias, clientId);
//        
        return iAliasResult;
    }
    
    
    //别名解除绑定
    @Override
    public IAliasResult unBindAliasAll(String alias) {
    	
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        
        IAliasResult iAliasResult = push.unBindAliasAll(appId, alias);
//        
        return iAliasResult;
    }

    //根据别名查询cid
    @Override
    public IAliasResult queryClientId(String appId, String alias) {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        return push.queryClientId(appId, alias);
    }

    //根据cid查询用户状态
    @Override
    public IQueryResult getClientIdStatus(String appId, String cid) {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        return  push.getClientIdStatus(appId, cid);
    }


    //包装的一个判断用户cid状态离线还是在线的方法，代替了之前用type决定推送模板的方式
    @Override
    public String CodeStatus(String alias){
        IAliasResult iAliasResult = queryClientId(appId, alias);
        
        
        if(!iAliasResult.getResponse().get("result").equals("ok")){
            return "null";
        }
        List<String> clientIdList = iAliasResult.getClientIdList();
        if (clientIdList.size()==0){
            return "null";
        }
        String cid = clientIdList.get(0);
        IQueryResult clientIdStatus = getClientIdStatus(appId, cid);
        Object result = clientIdStatus.getResponse().get("result");
        return result.toString();
    }
    //设置消息数据
    private boolean setMessageData(Message message, PushMessage pm,AppPush ap) {
        try {
			String result = CodeStatus(ap.getUid());
			
			if(result.equals("Offline")){
				
			    TransmissionTemplate template = getTransmissionTemplate(pm);//离线模板1
			    message.setData(template);
			    return true;
			}
			if(result.equals("Online")){
				
				//APP在线时，APP处于后台才会推送
				if(ap.isAppHide()) {
					
				    NotificationTemplate template =  notificationTemplateDemo(pm);   //通知模板 ，再多来两个模板我就要整switch了
				    
				    message.setData(template);
				    
				    return true;
				}
				return false;
			}
			else return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return false;
    }


    //通知模板设置
    private NotificationTemplate notificationTemplateDemo(PushMessage pm) {

        NotificationTemplate template = new NotificationTemplate();
        
        template.setAppId(appId);
        template.setAppkey(appKey);
        
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        
        //透传内容，不在通知栏中展示，自定义内容，开发者自行处理，不支持转义字符
        template.setTransmissionContent(pm.getContent());
        
        Style0 style = new Style0();
        
        // 设置通知栏标题与内容
        style.setTitle(pm.getTitle());
        style.setText(pm.getContent());
        // 配置通知栏图标
        style.setLogo("icon.png");
        
        // 配置通知栏网络图标
//        style.setLogoUrl(pushMessage.getIconUrl());
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        style.setChannel("默认channel");
        style.setChannelName("默认channel名称");
        style.setChannelLevel(3);//3：有声音，有震动，锁屏和通知栏中都显示，通知唤醒屏幕。（推荐）
        
        template.setStyle(style);
        
        // 设置定时展示时间，安卓机型可用
        // template.setDuration("2019-08-16 11:40:00", "2019-08-16 12:24:00");
        // 消息覆盖
        //template.setNotifyid(pushMessage.getMsgId()); // 在消息推送的时候设置自定义的notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
        return template;
    }

    //离线通知模板1 -- ios和安卓一起推送，自动识别手机厂商，如果不是苹果会忽视APN。
    private TransmissionTemplate getTransmissionTemplate(PushMessage pm) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionType(1);//搭配transmissionContent使用，可选值为1、2；1：立即启动APP（不推荐使用，影响客户体验）2：客户端收到消息后需要自行处理
        template.setTransmissionContent(pm.getContent()); //透传内容
        template.setAPNInfo(getAPNPayload(pm)); //ios消息推送，用于设置标题、内容、语音、多媒体、VoIP（基于IP的语音传输）等。离线走APNs时起效果
        Notify notify = new Notify();
        notify.setTitle(pm.getTitle());
        notify.setContent(pm.getContent());
        
//        intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component=你的包名/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=你的标题（必填）;S.content=你的内容（必填）;S.payload=你的app页面路由（必填）;end
        
        notify.setIntent("intent:#Intent;action=android.intent.action.oppopush;launchFlags=0x14000000;component="+app_component+"/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title="+pm.getTitle()+";S.content="+pm.getContent()+";S.payload=index;end");
       //上面的参数可以去看官方的API文档，里面有写离线厂商推送配置的具体。
        notify.setType(GtReq.NotifyInfo.Type._intent);
        template.set3rdNotifyInfo(notify);//设置第三方通知
        return template;
    }


    //ios离线推送APN设置
    private  APNPayload getAPNPayload(PushMessage pm) {
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("+1");
        payload.setContentAvailable(0);//0是有通知栏消息，1是无通知栏消息
        //ios 12.0 以上可以使用 Dictionary 类型的 sound
        payload.setSound("default");
        payload.setCategory("$由客户端定义");//在客户端通知栏触发特定的action和button显示
        payload.addCustomMsg("msg",pm.getContent());//增加自定义的数据,Key-Value形式
//        是否用字典模式?
        payload.setAlertMsg(getDictionaryAlertMsg(pm));
        return payload;
    }


    //获取字典模式对象。
    private APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(PushMessage pm) {
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setBody(pm.getContent());
        alertMsg.setTitle(pm.getTitle());
        return alertMsg;
    }

	@Override
	public void test() {
		PushMessage pm = new PushMessage();
		pm.setTitle("信息提示");
		pm.setContent( "11给你发消息"); 
		pm.setUserRole(""); 
//		singlePush("21fa1b1ade8a05d5013ccf24381c16aa", "2c9a9c55754a885d0175593c9908000b", pm, 1);
	} 
	
	public static void main(String[] args) {
//		IGtPush push = new IGtPush("http://api.getui.com/apiex.htm", "mwk6G0grdE9h6Rhzty39u9", "GY9xHh8KBX5X1oyMt9b1N1");
//		IQueryResult iQueryResult = push.getClientIdStatus("PY23ZekQ3a8LXXV6NM9vG6", "806a85a296a072a23728eb0bfdcb1f51");
//		
////		  IAliasResult iAliasResult = push.bindAlias("PY23ZekQ3a8LXXV6NM9vG6", "12345678912", "806a85a296a072a23728eb0bfdcb1f51");
//		
//		 
//	        
		UniPushService us = new UniPushServiceImpl();
		PushMessage pm = new PushMessage();
		pm.setTitle("信息提示");
		pm.setContent( "11给你发消息"); 
		pm.setUserRole(""); 
		IQueryResult r = us.getClientIdStatus("PY23ZekQ3a8LXXV6NM9vG6", "21fa1b1ade8a05d5013ccf24381c16aa");
		
		//us.singlePush("21fa1b1ade8a05d5013ccf24381c16aa", "2c9a9c55754a885d0175593c9908000b", pm, 1);
//		us.unBindAliasAll("12345678912");
	}

**/
}
