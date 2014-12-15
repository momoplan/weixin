package com.ruyicai.weixin.jms;

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

    @Resource(name = "lotteryCamelContext")
    private CamelContext lotteryCamelContext;

    @PostConstruct
    public void init() {

        logger.info("init lottery camel routes");
        try {
            lotteryCamelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
//                     deadLetterChannel("jmsLottery:queue:dead").maximumRedeliveries(-1)
//                     .redeliveryDelay(3000);
                     from("jmsLottery:queue:VirtualTopicConsumers.weixin.actioncenter?concurrentConsumers=20").to(
                     "bean:actioncenterListener?method=process").routeId("活动通知");
                    from("jmsLottery:queue:VirtualTopicConsumers.weixin.drawLottery?concurrentConsumers=5").to(
                            "bean:winInfoListener?method=process").routeId("weixin开奖通知");
                    from("jmsLottery:queue:VirtualTopicConsumers.weixin.orderPirzeend?concurrentConsumers=20").to(
                            "bean:orderEncashListener?method=process").routeId("weixin中奖通知");
                    from("jmsLottery:queue:VirtualTopicConsumers.weixin.jingcairesult-topic?concurrentConsumers=5").to(
                            "bean:jingCaiMatchesResultListener?method=process").routeId("竟彩开奖通知");

                }
            });
        } catch (Exception e) {
            logger.error("lottery camel context start failed", e.getMessage());
            e.printStackTrace();
        }
    }
}
