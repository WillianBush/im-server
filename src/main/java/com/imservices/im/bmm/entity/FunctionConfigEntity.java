package com.imservices.im.bmm.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 
 * @author wang<fangyuan.co@outlook.com>
 */
@Table(name = "function_config")
@Entity
@Data
public class FunctionConfigEntity extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "站点ID")
	private String org_id;
	
	@ApiModelProperty(value = "禁止网页/客户端注册 0否1是")
	private Integer web_app_regist;
	
	@ApiModelProperty(value = "网页/客户端显示IP 0否1是")
	private Integer web_app_ip_show;
	
	@ApiModelProperty(value = "单账号多段登录，网页/客户端最大登录数量")
	private Integer max_login_counts;
	
	@ApiModelProperty(value = "安全登录开关0关1开")
	private Integer safe_login;
	
	@ApiModelProperty(value = "安全登录，最大登录重试次数")
	private Integer safe_login_retry_counts;
	
	@ApiModelProperty(value = "安全登录，是否适用IP黑名单功能 0否1是")
	private Integer safe_login_apply_blacklist;
	
	@ApiModelProperty(value = "禁止普通用户添加好友0否1是")
	private Integer add_friend;
	
	@ApiModelProperty(value = "禁止普通用户建群0否1是")
	private Integer create_room;
	
	@ApiModelProperty(value = "开启手机号验证0否1是")
	private Integer phone_verify;
	
	@ApiModelProperty(value = "群主/群管理踢人是否删除被踢人信息0否1是")
	private Integer out_room_del_message;
	
	@ApiModelProperty(value = "隐藏群主/群管理的管理消息（禁言、撤回消息等）0否1是")
	private Integer miss_room_maneger_msg;

	@ApiModelProperty(value = "安全登陆：是否使用账号冻结功能 o:否;1:是")
	private Integer is_frozen;

	@ApiModelProperty(value = "安全登陆：是否使用IP黑名单功能 o:否;1:是")
	private Integer ip_black;

	@ApiModelProperty(value = "安全登陆：是否使用IP Web白名单功能 0否1是")
	private Integer ip_white;

	@ApiModelProperty(value = "用户外链开关：是否允许用户访问外链 0否1是")
	private Integer out_link_status;

}
