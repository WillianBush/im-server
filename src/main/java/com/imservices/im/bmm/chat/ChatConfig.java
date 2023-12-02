package com.imservices.im.bmm.chat;

import java.util.concurrent.Semaphore;

public class ChatConfig {

	public static Integer cloudUserChatDataKeepDays = 30;//好友云端聊天记录保留多少天
	public static Integer cloudGroupChatDataKeepDays = 30;//群组云端聊天记录保留多少天
	public static Semaphore chat_send_semaphore = new Semaphore(1000000);//发送线程控制
	
	
}
