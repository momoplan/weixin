package com.ruyicai.advert.jms;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {
	
	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Resource(name="camelContext")
	private CamelContext camelContext;
	
	@Resource(name = "lotteryCamelContext")
	private CamelContext lotteryCamelContext;
	
	@PostConstruct
	public void init() {
		try {
			logger.info("init advert camel routes");
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					from("jms:queue:VirtualTopicConsumers.advert.notifyThirdParty?concurrentConsumers=10").to(
							"bean:notifyThirdPartyListener?method=notify").routeId("注册请求第三方的通知");
					from("jms:queue:VirtualTopicConsumers.advert.iphoneScoreWallActive?concurrentConsumers=10").to(
							"bean:iphoneScoreWallActiveListener?method=process").routeId("激活请求第三方的通知");
				}
			});
		} catch (Exception e) {
			logger.error("advert camel context start failed", e.getMessage());
			e.printStackTrace();
		}
		
		logger.info("init lottery camel routes");
		try {
            lotteryCamelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jmsLottery:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					from("jmsLottery:queue:VirtualTopicConsumers.advert.actioncenter?concurrentConsumers=20").to(
							"bean:actioncenterListener?method=process").routeId("活动通知");
				}
			});
		} catch (Exception e) {
			logger.error("lottery camel context start failed", e.getMessage());
			e.printStackTrace();
		}
	}
}
