package com.ruyicai.weixin.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 请求信息记录
 */
@RooJavaBean
@RooToString
@RooJpaEntity
public class RequestMessageDetail {

	@Id
	@GeneratedValue
	private Long id;

	// 开发者微信号
	@Column(name = "TOUSERNAME", length = 50)
	private String ToUserName;

	// 发送方帐号(一个OpenID)
	@Column(name = "FROMUSERNAME", length = 50)
	private String FromUserName;

	// 消息创建时间 (整型)
	@Column(name = "CREATETIME")
	private Long CreateTime;

	// 消息类型(text/image/location/link)
	@Column(name = "MSGTYPE", length = 20)
	private String MsgType;

	// 消息id，64位整型
	@Column(name = "MSGID")
	private Long MsgId;

	// 消息体
	@Column(name = "REQUESTBODY", columnDefinition = "text")
	private String requestBody;

}
