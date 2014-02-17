package com.ruyicai.weixin.service.msghandle.impl;

import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;

/**
 * 地理位置消息处理
 * 
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>1351776360</CreateTime>
 * <MsgType><![CDATA[location]]></MsgType>
 * <Location_X>23.134521</Location_X>
 * <Location_Y>113.358803</Location_Y>
 * <Scale>20</Scale>
 * <Label><![CDATA[位置信息]]></Label>
 * <MsgId>1234567890123456</MsgId>
 * </xml>
 * 
 */
@Service(value = "location")
public class LocationHandler implements IMsgHandler {

	@Override
	public ResponseBaseMessage handler(RequestMessage requestMessage) {
		return null;
	}

}
