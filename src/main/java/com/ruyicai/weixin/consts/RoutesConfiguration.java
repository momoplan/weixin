package com.ruyicai.weixin.consts;

import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {

	/*private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Resource(name = "lotteryCamelContext")
	private CamelContext lotteryCamelContext;
	
	@PostConstruct
	public void init() throws Exception{
		logger.info("init camel routes");
		lotteryCamelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:VirtualTopicConsumers.weixin.orderPirzeend?concurrentConsumers=20").to("bean:sendEncashSMSListener?method=encashCustomer").routeId("中奖通知");
			}
		});
	}*/
}
