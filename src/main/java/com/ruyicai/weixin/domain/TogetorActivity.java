package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 微信合买活动详情
 */
@RooJavaBean
@RooToString
@RooJpaEntity(table = "togetoractivity")
public class TogetorActivity {
	@Id
	@NotNull
	@Column(name = "userno", length = 50)
	private String userno;
	
	@NotNull
	@Column(name = "formuserno", length = 50)
	private String formuserno;

	@NotNull
	@Column(name = "orderid", length = 100)
	private String orderid;

	@NotNull
	@Column(name = "username", length = 100)
	private String username;

	@Column(name = "MEMO")
	private String memo;

	@NotNull
	@Column(name = "receivetime", length = 100)
	private Date receivetime;
	
	@NotNull
	@Column(name = "receiveport", length = 100)
	private int receiveport;
	
	@NotNull
	@Column(name = "state", length = 50)
	private int state;
	
	@NotNull
	@Column(name = "headimgurl", length = 50)
	private int headimgurl;
}
