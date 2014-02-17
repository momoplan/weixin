package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 订阅的用户信息
 */
@RooJavaBean
@RooToString
@RooJpaEntity
public class Subscriber {

	@EmbeddedId
	private SubscriberPK id;

	@Column(name = "HASSUBSCRIBE")
	private int hasSubscribe;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "LASTMODIFYTIME")
	private Date lastModifyTime;
}
