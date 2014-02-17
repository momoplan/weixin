package com.ruyicai.weixin.service.msghandle;

import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;

/**
 * 请求消息处理接口
 */
public interface IMsgHandler {

	/**
	 * 处理微信传递过来的消息
	 * 
	 * @param requestMessage
	 * @return
	 */
	public ResponseBaseMessage handler(RequestMessage requestMessage);
}
