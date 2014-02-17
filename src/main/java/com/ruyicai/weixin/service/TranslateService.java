package com.ruyicai.weixin.service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;
import com.ruyicai.weixin.util.JaxbMapper;

@Service
public class TranslateService {

	private Logger logger = LoggerFactory.getLogger(TranslateService.class);

	/**
	 * 微信认证token,在InitAppUserService初始化
	 */
	private String token;

	public void setToken(String token) {
		this.token = token;
	}

	@Autowired
	private Map<String, IMsgHandler> msgHandlers;

	public String processRequest(RequestMessage requestMessage) throws JAXBException {
		if ((requestMessage == null) || StringUtils.isBlank(requestMessage.getMsgType())) {
			logger.error("转换数据为空或消息类型为空");
			return null;
		}
		ResponseBaseMessage responseMessage = null;
		String msgType = requestMessage.getMsgType();
		IMsgHandler msgHandler = msgHandlers.get(msgType);
		if (msgHandler != null) {
			responseMessage = msgHandler.handler(requestMessage);
		}
		if (responseMessage != null) {
			return JaxbMapper.toXml(responseMessage, true);
		} else {
			return null;
		}
	}

	public String configAuth(String timestamp, String nonce) {
		logger.info("token:" + token);
		String[] array = new String[] { token, timestamp, nonce };
		Arrays.sort(array);
		StringBuffer sb = new StringBuffer();
		for (String str : array) {
			sb.append(str);
		}
		return SHA1Encode(sb.toString());
	}

	public String test() {
		StringBuilder sb = new StringBuilder();
		for (String key : msgHandlers.keySet()) {
			sb.append(key + "\n");
		}
		return sb.toString();
	}

	/**
	 * sha1加密
	 * 
	 * @param sourceString
	 * @return
	 */
	private String SHA1Encode(String sourceString) {
		String resultString = null;
		try {
			resultString = new String(sourceString);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			resultString = byte2hexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
		}
		return resultString;
	}

	private String byte2hexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (byte b : bytes) {
			if ((b & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(b & 0xff, 16));
		}
		return buf.toString().toUpperCase();
	}
}
