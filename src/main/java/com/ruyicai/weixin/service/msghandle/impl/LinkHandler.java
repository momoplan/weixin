package com.ruyicai.weixin.service.msghandle.impl;

import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;

/**
 * 链接消息处理
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>1351776360</CreateTime>
 * <MsgType><![CDATA[link]]></MsgType>
 * <Title><![CDATA[公众平台官网链接]]></Title>
 * <Description><![CDATA[公众平台官网链接]]></Description>
 * <Url><![CDATA[url]]></Url>
 * <MsgId>1234567890123456</MsgId>
 * </xml>
 */
@Service(value = "link")
public class LinkHandler implements IMsgHandler {

	public ResponseBaseMessage handler(RequestMessage requestMessage) {
		return null;
	}

}
