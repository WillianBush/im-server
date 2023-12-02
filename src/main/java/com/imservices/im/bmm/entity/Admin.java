package com.imservices.im.bmm.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="admin")
@SuppressWarnings("all")
public class Admin extends BaseEntity {
	public static final String ADMINIDSESSION = "admin_id_session";
	
	public static final String ADMINMD5KEY = "admin_md5_key";
	private String username;
	private String password;
	private String email;//邮箱
	private String name;//姓名
	private String department;//部门
	private Boolean isAccountEnabled;//帐号可用
	private Integer loginFailureCount;//登陆失败次数
	private Date lockedDate;//锁定时间
	private Date loginDate;//最近登陆时间
	private String loginIp;//登陆ip
	private Role role;//角色
	
	
	
	
	@Transient
	public String getRoleName() {
		if(null!=getRole()) {
			return getRole().getName();
		}
		return "";
	}
	
	@Transient
	public String getRoleid() {
		if(null!=getRole()) {
			return getRole().getId();
		}
		return "";
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Role getRole() {
		return this.role;
	}

	public static void main(String[] args) {
		//
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Column(updatable = false, nullable = false, unique = true)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(nullable = false)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Column(nullable = false)
	public Boolean getIsAccountEnabled() {
		return this.isAccountEnabled;
	}

	public void setIsAccountEnabled(Boolean isAccountEnabled) {
		this.isAccountEnabled = isAccountEnabled;
	}


	@Column(nullable = false)
	public Integer getLoginFailureCount() {
		return this.loginFailureCount;
	}

	public void setLoginFailureCount(Integer loginFailureCount) {
		this.loginFailureCount = loginFailureCount;
	}

	@Column
	public Date getLockedDate() {
		return this.lockedDate;
	}

	public void setLockedDate(Date lockedDate) {
		this.lockedDate = lockedDate;
	}

	@Column
	public Date getLoginDate() {
		return this.loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Column
	public String getLoginIp() {
		return this.loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
 
}