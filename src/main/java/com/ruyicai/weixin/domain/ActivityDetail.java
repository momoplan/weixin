package com.ruyicai.weixin.domain;

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
@RooJpaEntity(table = "activitydetail")
public class ActivityDetail {
	
	@Id
	@NotNull
	@Column(name = "orderid", length = 50)
	private String orderid;
	
	@NotNull
	@Column(name = "laveport", length = 100)
	private String laveport;
	
	@NotNull
	@Column(name = "allport", length = 100)
	private String allport;
}