package com.ruyicai.weixin;

import java.math.BigDecimal;
import java.util.List;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.util.JsonUtil;

public class testMoeny {

    public static void main(String[] args) {
        String orderJson = "{\"agencyno\":null,\"alreadytrans\":0,\"amt\":200,\"batchcode\":null,\"betcode\":\"502@20141215|1|002|3^20141215|1|004|3^\",\"betnum\":1,\"bettype\":2,\"body\":null,\"buyuserno\":\"03187412\",\"canceltime\":null,\"caselotStarter\":null,\"channel\":null,\"createtime\":1418635177898,\"desc\":null,\"encashtime\":null,\"endtime\":null,\"errorcode\":null,\"eventcode\":\"1_20141215_1_004\",\"hasachievement\":0,\"id\":\"BJ2014121559754537\",\"instate\":1,\"lastprinttime\":1418658480000,\"latedteamid\":\"1_20141215_1_002\",\"lotmulti\":1,\"lotno\":\"J00001\",\"lotsType\":0,\"memo\":null,\"modifytime\":null,\"orderamt\":null,\"orderinfo\":\"502@20141215|1|002|3^20141215|1|004|3^_1_200_200\",\"orderpreprizeamt\":367,\"orderprize\":null,\"orderprizeamt\":367,\"orderstate\":1,\"ordertype\":0,\"paystate\":3,\"paytype\":1,\"playtype\":\"502\",\"prizeinfo\":\"\",\"prizestate\":5,\"subaccount\":null,\"subaccountType\":null,\"subchannel\":\"00092493\",\"successtime\":1418635261478,\"tlotcaseid\":null,\"tsubscribeflowno\":null,\"userno\":\"03187412\",\"visible\":1,\"winbasecode\":\" \"}";
        // TODO Auto-generated method stub

        System.out.println("中奖派奖通知,orderJson:" + orderJson);
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
 

            System.out.println("中奖派奖通知,orderJson:" + orderJson);
            SubscriberInfoDao subscriberInfoDao = new SubscriberInfoDao();
            OrderLotInfoDao orderLotInfoDao = new OrderLotInfoDao();

            String userno = order.getUserno();
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/html/user/lotterydetail.html?clientShare=true%26orderid="
                    + order.getId()
                    + "%26userno="
                    + userno
                    + "&from=singlemessage&isappinstalled=0&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
            // String url =
            // "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/html/user/cathecticlist.html?from=singlemessage&isappinstalled=0&response_type=code&scope=snsapi_base&state=1#wechat_redirect";

            if (order.getOrderprizeamt().intValue() == 0)
                return;
            if (lotno.startsWith("J000"))//
            {
                // 这个彩种的订阅用户
                List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, lotno);

                if (lstSubscriber.size() > 0) {
                    List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByTransactionID(userno,
                            order.getId());
                    if (lstSublot.size() > 0)
                    {
//                        subscribeLotService.sendBetInfo(userno,
//                                String.valueOf(order.getOrderprizeamt().intValue() / 100), "竞彩足球", url);
                    }
                }
            }

        } catch (Exception e) {
//            logger.error("活动监听发生异常,orderJson:" + orderJson, e.getMessage());
        }

    }

}
