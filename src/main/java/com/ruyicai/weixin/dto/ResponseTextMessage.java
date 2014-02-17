package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ruyicai.weixin.util.CDataAdapter;

/**
 * 响应文本消息
 */
@XmlRootElement(name = "xml")
public class ResponseTextMessage extends ResponseBaseMessage {

	// 回复的消息内容
	private String Content;

	@XmlElement(name = "Content")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
