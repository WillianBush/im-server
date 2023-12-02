package com.imservices.im.bmm.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name="message_history")
@Data
public class MsgHistoryEntity extends BaseEntity{

    /**日期*/
    private String date;
    /**头像*/
    private String fromHeadpic;
    /**未加工过的消息，如果是媒体消息，则是不带域名的上传地址*/
    private String oldTxt;
    /**接收对象的ID*/
    private String toUid;
    private String toGroupid;
    private String chatid;
    /**解析方式  默认使用rich-text   还有其它的选择uparse voice(语音) video(视频)*/
    private String psr;
    /**加工过的消息，如果是媒体消息，则是带域名的上传地址*/
    private String txt;
    private String aite;
    /**发送人*/
    private String fromUid;
    private String fromName;
    /**如果是媒体消息，则是多媒体格式 "simple_content": "[图片]"*/
    private String simple_content;
}
