package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 接收消息基类
 */
@XmlRootElement(name = "xml")
public class RequestMessage {

	/** ==========Base Message========== */

	// 开发者微信号
	private String ToUserName;

	// 发送方帐号(一个OpenID)
	private String FromUserName;

	// 消息创建时间 (整型)
	private Long CreateTime;

	// 消息类型(text/image/location/link)
	private String MsgType;

	// 消息id，64位整型
	private Long MsgId;

	/** ==========MsgType text========== */
	// 消息内容
	private String Content;

	/** ==========MsgType image========== */
	// 图片链接
	@XmlElement(name = "PicUrl")
	private String PicUrl;

	/** ==========MsgType location========== */
	// 地理位置维度
	private String Location_X;

	// 地理位置经度
	private String Location_Y;

	// 地图缩放大小
	private String Scale;

	// 地理位置信息
	private String Label;

	/** ==========MsgType link========== */
	// 消息标题
	private String Title;

	// 消息描述
	private String Description;

	// 消息链接
	private String Url;

	/** ==========MsgType event========== */
	// 事件类型，subscribe(订阅)、unsubscribe(取消订阅)、CLICK(自定义菜单点击事件)
	private String Event;

	// 事件KEY值，与自定义菜单接口中KEY值对应
	private String EventKey;

	@XmlElement(name = "ToUserName")
	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	@XmlElement(name = "FromUserName")
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

	@XmlElement(name = "MsgId")
	public Long getMsgId() {
		return MsgId;
	}

	public void setMsgId(Long msgId) {
		MsgId = msgId;
	}

	@XmlElement(name = "Content")
	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	@XmlElement(name = "PicUrl")
	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	@XmlElement(name = "Location_X")
	public String getLocation_X() {
		return Location_X;
	}

	public void setLocation_X(String location_X) {
		Location_X = location_X;
	}

	@XmlElement(name = "Location_Y")
	public String getLocation_Y() {
		return Location_Y;
	}

	public void setLocation_Y(String location_Y) {
		Location_Y = location_Y;
	}

	@XmlElement(name = "Scale")
	public String getScale() {
		return Scale;
	}

	public void setScale(String scale) {
		Scale = scale;
	}

	@XmlElement(name = "Label")
	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	@XmlElement(name = "Title")
	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	@XmlElement(name = "Description")
	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	@XmlElement(name = "Url")
	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	@XmlElement(name = "Event")
	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	@XmlElement(name = "EventKey")
	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}