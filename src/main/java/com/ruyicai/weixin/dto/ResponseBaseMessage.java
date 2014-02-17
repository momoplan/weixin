package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.ruyicai.weixin.util.CDataAdapter;

/**
 * 响应消息基类
 */
@XmlRootElement(name = "xml")
public class ResponseBaseMessage {

	// 接收方帐号(收到的OpenID)
	private String ToUserName;

	// 开发者微信号
	private String FromUserName;

	// 消息创建时间 (整型)
	private Long CreateTime;

	// 消息类型(text/music/news)
	private String MsgType;

	@XmlElement(name = "ToUserName")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	@XmlElement(name = "FromUserName")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	@XmlElement(name = "CreateTime")
	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	@XmlElement(name = "MsgType")
	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
