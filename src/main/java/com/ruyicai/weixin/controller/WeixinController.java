package com.ruyicai.weixin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.service.AsyncService;
import com.ruyicai.weixin.service.TranslateService;
import com.ruyicai.weixin.util.JaxbMapper;

@Controller
public class WeixinController {

	private Logger logger = LoggerFactory.getLogger(WeixinController.class);

	@Autowired
	private TranslateService translateService;

	@Autowired
	private AsyncService asyncService;

	@RequestMapping(value = "/service", method = RequestMethod.GET)
	@ResponseBody
	public String serviceByGet(String signature, String timestamp, String nonce, String echostr) {
		logger.info("signature:{},timestamp:{},nonce:{},echostr:{}", signature, timestamp, nonce, echostr);
		String authStr = translateService.configAuth(timestamp, nonce);
		if (StringUtils.isNotBlank(authStr) && authStr.equalsIgnoreCase(signature)) {
			return echostr;
		} else {
			return "error";
		}
	}

	@RequestMapping(value = "/service", method = RequestMethod.POST)
	@ResponseBody
	public String serviceByPost(HttpServletRequest request, HttpServletResponse response) {
		String result = null;
		String requestBody = null;
		RequestMessage requestMessage = null;
		try {
			requestBody = this.readStreamParameter(request.getInputStream());
			if (StringUtils.isBlank(requestBody)) {
				return result;
			}
			logger.info("请求信息:" + requestBody);
			requestMessage = JaxbMapper.fromXml(requestBody, RequestMessage.class);
			// asyncService.createRequestMessageDetail(requestMessage, requestBody);
			result = translateService.processRequest(requestMessage);
		} catch (IOException e) {
			logger.error("获取request.getInputStream()异常", e);
		} catch (JAXBException e) {
			logger.error("JAXB处理异常", e);
		}
		logger.info("返回信息:" + result);
		return result;
	}

	// 从输入流读取post参数
	public String readStreamParameter(ServletInputStream in) {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			logger.error("io error", e);
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("io error", e);
				}
			}
		}
		return buffer.toString();
	}
}
