package com.ruyicai.weixin.jms.listener;

import java.math.BigDecimal;
import java.util.List;

import org.apache.camel.Body;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.SubscribeLotService;
import com.ruyicai.weixin.util.JsonUtil;

/**
 * 活动监听
 * 
 * @author Administrator
 *
 */
@Service
public class orderEncashListener {
    @Autowired
    SubscribeLotService subscribeLotService;

    @Autowired
    SubscriberInfoDao subscriberInfoDao;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

    @Autowired
    CaseLotActivityService caseLotActivityService;

    private static Logger logger = LoggerFactory.getLogger(orderEncashListener.class);

    public void process(@Body String orderJson) {

//         logger.info("中奖派奖通知,orderJson:" + orderJson);
//         System.out.println("中奖派奖通知,orderJson:" + orderJson);
        if (null == orderJson)
            return;

        try {

            Torder order = JsonUtil.fromJson(orderJson, Torder.class);
         

            if (order == null || order.getUserno() == null || order.getLotno() == null
                    || order.getOrderstate().compareTo(BigDecimal.ONE) != 0) {
                return;
            }

            String lotno = order.getLotno();

            if (!(lotno.equals("F47104") || lotno.equals("T01001") || lotno.equals("J00003") || lotno.equals("T01007")
                    || lotno.equals("J00004") || lotno.equals("J00011") || lotno.equals("J00013")
                    || lotno.equals("J00001") || lotno.equals("J00002"))) {
                return;
            }

            CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(order.getUserno(),
                    Const.WX_PACKET_ACTIVITY);

            if (null == caseLotUserinfo)
                return;

            logger.info("中奖派奖通知,orderJson:" + orderJson);
            System.out.println("中奖派奖通知,orderJson:" + orderJson);

            String userno = order.getUserno();
            String url =  "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/html/user/lotterydetail.html?clientShare=true%26orderid="+order.getId()+"%26userno="+userno+"&from=singlemessage&isappinstalled=0&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
           // String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/html/user/cathecticlist.html?from=singlemessage&isappinstalled=0&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
            
            if(order.getOrderprizeamt().intValue()==0)
                return;

            if (lotno.equals("T01007")) {

                List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, lotno);
                logger.info("usrno--:"+userno+"lotno:"+lotno);

                if (lstSubscriber.size() > 0) {
                    List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByUserno(userno,
                            order.getBatchcode(), lotno);
                    
                    logger.info("usrno--:"+userno+"order.getBatchcode():"+order.getBatchcode()+"lotno:"+lotno);
                    
                    if (lstSublot.size() > 0)
                        subscribeLotService.sendBetInfo(userno,
                                String.valueOf(order.getOrderprizeamt().intValue() / 100), "时时彩", url);

                }
            } else if (lotno.equals("F47104"))//

            {
                List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, lotno);

                if (lstSubscriber.size() > 0) {
                    List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByUserno(userno,
                            order.getBatchcode(), lotno);
                    if (lstSublot.size() > 0)
                        subscribeLotService.sendBetInfo(userno,
                                String.valueOf(order.getOrderprizeamt().intValue() / 100), "双色球"+order.getBatchcode()+"期", url);

                }
            } else if (lotno.equals("T01001"))//
            {
                // 这个彩种的订阅用户
                List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, lotno);

                if (lstSubscriber.size() > 0) {
                    List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByUserno(userno,
                            order.getBatchcode(), lotno);
                    if (lstSublot.size() > 0)
                        subscribeLotService.sendBetInfo(userno,
                                String.valueOf(order.getOrderprizeamt().intValue() / 100), "大乐透"+order.getBatchcode()+"期", url);
                }
            }
            else if (lotno.startsWith("J000"))//
            {
                // 这个彩种的订阅用户
                List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, "J00000");
                System.out.println("lstSubscriber.size():"+lstSubscriber.size());

                if (lstSubscriber.size() > 0) {
                    List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByTransactionID(userno,
                            order.getId());
                    System.out.println("lstSublot.size():"+lstSublot.size());
                    if (lstSublot.size() > 0)
                        subscribeLotService.sendBetInfo(userno,
                                String.valueOf(order.getOrderprizeamt().floatValue() / 100), "竞彩足球", url);
                    System.out.println("subscribeLotService.sendBetInfo");
                }
            }

        } catch (Exception e) {
            logger.error("活动监听发生异常,orderJson:" + orderJson, e.getMessage());
        }
    }

}
