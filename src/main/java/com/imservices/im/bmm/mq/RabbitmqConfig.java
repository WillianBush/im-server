package com.imservices.im.bmm.mq;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
@SpringBootConfiguration
public class RabbitmqConfig {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${server.port}")
    private String serverPort;

    public static final String BASIC_EXCHANGE = "im.message.exchange";
    public static final String USER_QUEUE = "im.message.queue";
    public static final String GROUP_QUEUE = "im.message.group.queue";
    public static final String USER_ROUTING_KEY = "im.message.routing.key";

    public static final String GROUP_ROUTING_KEY = "im.message.group.routing.key";

    public static final String MSG_HISTORY_QUEUE = "im.message.history.msg.queue";
    public static final String MSG_HISTORY_ROUTING_KEY = "im.message.history.msg.routing.key";
    public static final String WAIT_MSG_QUEUE = "im.message.waitmsg.queue";
    public static final String WAIT_MSG_ROUTING_KEY = "im.message.waitmsg.routing.key";

    public static final String GROUP_EXCEPTION_QUEUE = "im.message.group.exception.queue";
    public static final String GROUP_EXCEPTION_ROUTING_KEY = "im.message.group.exception.routing.key";

    /***
     * 处理CMD消息队列
     */
    public static final String CMD_MESSAGE_QUEUE = "im.message.cmd.message.queue";
    public static final String CMD_MESSAGE_ROUTING_KEY = "im.message.cmd.message.routing.key";

    /***
     * 处理发送给自己的消息，针对多用户登录消息同步
     */
    public static final String SELF_MESSAGE_QUEUE = "im.message.self.message.queue";
    public static final String SELF_MESSAGE_ROUTING_KEY = "im.message.self.message.routing.key";


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    /***
     * 动态队列名，实现动态绑定机器，适应集群状态下的消息定点发送
     * @return
     */
    public static String getUserQueueName() {
        return USER_QUEUE + "." + getLocalIP();
    }

    public static String getGroupQueueName() {
        return GROUP_QUEUE + "." + getLocalIP();
    }


    public static String getUserRoutingKey() {
        return USER_ROUTING_KEY + "." + getLocalIP();
    }

    public static String getGroupRoutingKey() {
        return GROUP_ROUTING_KEY + "." + getLocalIP();
    }

    public static String getCmdMessageQueueName() {
        return CMD_MESSAGE_QUEUE + "." + getLocalIP();
    }

    public static String getSelfMsgQueueName() {
        return SELF_MESSAGE_QUEUE + "." + getLocalIP();
    }

    public static String getSelfMsgRoutingKey() {
        return SELF_MESSAGE_ROUTING_KEY + "." + getLocalIP();
    }

    //用户对用户聊天队列
    @Bean
    public Queue UserDirectQueue() {
        return new Queue(getUserQueueName() + serverPort,true);
    }

    //群组聊天队列
    @Bean
    public Queue GroupDirectQueue() {
        return new Queue(getGroupQueueName() + serverPort,true);
    }

    //不用发到特定队列
    @Bean
    public Queue WaitMsgQueue(){return new Queue(WAIT_MSG_QUEUE,true);}

    //不用发到特定队列
    @Bean
    public Queue GroupExceptionMsgQueue(){return new Queue(GROUP_EXCEPTION_QUEUE,true);}

    @Bean
    public Queue MsgHistoryQueue(){return new Queue(MSG_HISTORY_QUEUE,true);}

    //群组聊天队列
    @Bean
    public Queue CmdMessageQueue() {
        return new Queue(getCmdMessageQueueName() + serverPort,true);
    }

    @Bean
    public Queue SelfMessageQueue(){return new Queue(getSelfMsgQueueName() + serverPort,true);}

    //交换机
    @Bean
    DirectExchange Exchange() {
        return new DirectExchange(BASIC_EXCHANGE);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键
    @Bean
    Binding bindingUser() {
        return BindingBuilder.bind(UserDirectQueue()).to(Exchange()).with(getUserRoutingKey()+serverPort);
    }

    @Bean
    Binding bindingGroup() {
        return BindingBuilder.bind(GroupDirectQueue()).to(Exchange()).with(getGroupRoutingKey()+serverPort);
    }

    @Bean
    Binding bindingWaitMsg() {
        return BindingBuilder.bind(WaitMsgQueue()).to(Exchange()).with(WAIT_MSG_ROUTING_KEY);
    }

    @Bean
    Binding bindingGroupExceptionMsg() {
        return BindingBuilder.bind(GroupExceptionMsgQueue()).to(Exchange()).with(GROUP_EXCEPTION_ROUTING_KEY);
    }

    @Bean
    Binding bindingMsgHistoryMsg() {
        return BindingBuilder.bind(MsgHistoryQueue()).to(Exchange()).with(MSG_HISTORY_ROUTING_KEY);
    }

    @Bean
    Binding bindingCmdMsg() {
        return BindingBuilder.bind(CmdMessageQueue()).to(Exchange()).with(CMD_MESSAGE_ROUTING_KEY);
    }

    @Bean
    Binding bindingSelfMsg() {
        return BindingBuilder.bind(SelfMessageQueue()).to(Exchange()).with(getSelfMsgRoutingKey()+serverPort);
    }

    /**
     * 取当前系统站点本地地址 linux下 和 window下可用
     *
     * @return
     */
    public static String getLocalIP() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        }catch (Exception e){
            log.error("RabbitmqConfig:{}",e);
        }
       return "";
    }

    /**
     * 判断当前系统是否windows
     *
     * @return boolean
     */
    public static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }


}

