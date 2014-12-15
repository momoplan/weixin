package com.ruyicai.weixin.jms.listener;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.camel.Header;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.service.SubscribeLotService;

/**
 * 活动监听
 * 
 * @author Administrator
 *
 */
@Service
public class jingCaiMatchesResultListener {
    @Autowired
    SubscribeLotService subscribeLotService;

    @Autowired
    SubscriberInfoDao subscriberInfoDao;

    @Value("${lotteryurl}")
    private String lotteryurl;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

    private static Logger logger = LoggerFactory.getLogger(jingCaiMatchesResultListener.class);

    public void process(@Header("EVENT") String event) {
        try {
            logger.info("竞彩赛果更新jms start event={}", event);
            // UpdateCacheThread ut = new UpdateCacheThread("event");
            new Thread(new UpdateCacheThread(event)).start();
        } catch (Exception e) {
            logger.error("竞彩赛果更新的jms发生异常", e);
        }
    }

    private class UpdateCacheThread implements Runnable {

        private String event;

        public UpdateCacheThread(String event) {
            this.event = event;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10000); // 眠10秒，等待lottery更新缓存
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String jingCaiType = ""; // 竞彩类型
            String day = ""; // 日期
            String weekId = ""; // 星期
            String teamId = ""; // 场次
            if (event != null) {
                String[] events = event.split("_");
                if (events.length == 4) {
                    jingCaiType = events[0];
                    day = events[1];
                    weekId = events[2];
                    teamId = events[3];
                }
            }
            String valueObject = null;
            if (jingCaiType.equals("1")) { // 竞彩足球
                logger.info("jingCaiType:" + jingCaiType);

                valueObject = getJingcaimatches("J00001", day, weekId, teamId);
                System.out.println("valueObject:" + valueObject);
                JSONObject jbValue = JSONObject.fromObject(valueObject);

                if (jbValue.get("errorCode").equals("0")) {
                    logger.info("errorCode:" + jbValue.get("errorCode"));

                    JSONObject jso = JSONObject.fromObject(jbValue.get("value"));
                    JSONObject jso_result = JSONObject.fromObject(jso.get("result"));
                    JSONObject jso_matches = JSONObject.fromObject(jso.get("matches"));
                    String teamshortname = jso_matches.get("team").toString();
                    System.out.println(teamshortname);
                    String result = jso_result.get("result").toString();
                    System.out.println(result);

                    String strMatchID = day + "|" + weekId + "|" + teamId;
                    List<OrderLotInfo> lotInfo = orderLotInfoDao.findSubscriberInfoByBetcode(strMatchID);
                    java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String date = "";
                    List<SubscriberInfo> lstSubscriberInfo = null;
                    String userno = "";

                    List<String> list = new ArrayList<String>();

                    if (lotInfo.size() > 0) {
                        System.out.println("lotInfo.size():" + lotInfo.size());

                        for (int i = 0; i < lotInfo.size(); i++) {

                            date = format_open.format(lotInfo.get(i).getCreattime().getTime());

                            userno = lotInfo.get(i).getUserno();
                            if (list.contains(userno))
                                continue;
                            else
                                list.add(userno);
                            System.out.println("userno:" + userno);
                            // 优化
                            lstSubscriberInfo = subscriberInfoDao.findSubscriberByUserno(userno, "J00000");

                            if (lstSubscriberInfo.size() > 0) {
                                System.out.println("userno1:" + userno);
                                subscribeLotService.sendOpenInfo(userno, day + weekId + teamId, teamshortname + "  "
                                        + result, "竞彩足球", "http://wx.ruyicai.com/html/lottery/jczqlot.html", lotInfo
                                        .get(i).getAmt(), date);
                                System.out.println("lotInfo.get(0).getUserno()" + userno + ",teamshortname+result="
                                        + teamshortname + result);

                            }

                            System.out.println("发送消息userno:" + userno + ":" + day + weekId + teamId + ":"
                                   + teamshortname + result);

                        }
                    }
                }

                logger.info("valueObject:" + valueObject);
                System.out.println(valueObject);
            }
        }
    }

    /**
     * 获取竞彩某场比赛的信息
     * 
     * @param lotno
     * @param day
     * @param weekid
     * @param teamid
     * @return
     */
    public String getJingcaimatches(String lotNo, String day, String weekId, String teamId) {
        StringBuilder paramStr = new StringBuilder();
        paramStr.append("lotno=" + lotNo);
        paramStr.append("&day=" + day);
        paramStr.append("&weekid=" + weekId);
        paramStr.append("&teamid=" + teamId);

        String url = lotteryurl + "/select/getjingcaimatchesWithLetpoint";
        String result = HttpUtil.sendRequestByPost(url, paramStr.toString(), true);
        logger.info("获取竞彩某场比赛的信息的返回:{},paramStr:{}", result, paramStr.toString());
        return result;
    }

}
