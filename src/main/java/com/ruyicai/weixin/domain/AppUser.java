package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 微信的账号信息
 */
@RooJavaBean
@RooToString
@RooJpaEntity(table = "appuser")
public class AppUser {

	@Id
	@Column(name = "WEIXINNAME", length = 50)
	private String weixinName;

	@Column(name = "APPID", length = 50)
	private String appId;

	@Column(name = "APPSECRET", length = 100)
	private String appSecret;

	@NotNull
	@Column(name = "TOKEN", length = 100)
	private String token;

	@Column(name = "MEMO", length = 100)
	private String memo;

	@Column(name = "CREATETIME", length = 100)
	private Date createTime;
}
