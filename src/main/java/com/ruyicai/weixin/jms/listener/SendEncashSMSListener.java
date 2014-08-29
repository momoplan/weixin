package com.ruyicai.weixin.jms.listener;

import java.math.BigDecimal;

import org.apache.camel.Body;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.weixin.util.JsonUtil;

@Service
public class SendEncashSMSListener {

	private Logger logger = LoggerFactory.getLogger(SendEncashSMSListener.class);
	
	public void encashCustomer(@Body String body) {
		if (StringUtils.isBlank(body)) {
			return;
		}
		logger.info("body= " + body);
		Torder order = JsonUtil.fromJson(body, Torder.class);
		if (order == null || order.getUserno() == null || order.getLotno() == null
				|| order.getOrderstate().compareTo(BigDecimal.ONE) != 0) {
			return;
		}
	}
}
