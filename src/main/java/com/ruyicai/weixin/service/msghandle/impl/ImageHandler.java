package com.ruyicai.weixin.service.msghandle.impl;

import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;

/**
 * 图片消息
 * 
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>1348831860</CreateTime>
 * <MsgType><![CDATA[image]]></MsgType>
 * <PicUrl><![CDATA[this is a url]]></PicUrl>
 * <MsgId>1234567890123456</MsgId>
 * </xml>
 */
@Service(value = "image")
public class ImageHandler implements IMsgHandler {

	@Override
	public ResponseBaseMessage handler(RequestMessage requestMessage) {
		return null;
	}

}
