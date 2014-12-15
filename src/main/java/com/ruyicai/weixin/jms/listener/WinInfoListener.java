package com.ruyicai.weixin.jms.listener;

import java.util.List;

import org.apache.camel.Header;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class WinInfoListener {
    @Autowired
    SubscribeLotService subscribeLotService;

    @Autowired
    SubscriberInfoDao subscriberInfoDao;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

    private static Logger logger = LoggerFactory.getLogger(WinInfoListener.class);

    public void process(@Header("lotno") String lotNo, @Header("batchcode") String batchcode,
            @Header("wincode") String wincode, @Header("winbasecode") String winbasecode,
            @Header("winspecialcode") String winspecialcode) {
        try {
             logger.info("活动监听 start lotno:"+lotNo+";batchcode:"+batchcode+";wincode:"+wincode+";winbasecode:"+winbasecode+";winspecialcode:"+winspecialcode);
             System.out.println("活动监听 start lotno:"+lotNo+";batchcode:"+batchcode+";wincode:"+wincode+";winbasecode:"+winbasecode+";winspecialcode:"+winspecialcode);
            long startMillis = System.currentTimeMillis();

            if (lotNo == null) {
                return;
            }

            long endMillis = System.currentTimeMillis();

            if (lotNo.equals("T01007")) {
                List<SubscriberInfo> lstSubscriberInfo = subscriberInfoDao.findSubscriberInfoByLottype(lotNo, "1");
                String userno = "";
                List<OrderLotInfo> lstLotinfo = null;

                for (int i = 0; i < lstSubscriberInfo.size(); i++) {
                    userno = lstSubscriberInfo.get(i).getUserno();
                    lstLotinfo = orderLotInfoDao.findSubscriberInfoByUserno(userno, batchcode);

                    logger.info("T01007:" + userno + "|" + batchcode + "|" + batchcode);

                    if (lstLotinfo.size() > 0) {
                        java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");

                        String date = format_open.format(lstLotinfo.get(0).getCreattime().getTime());

                        StringBuffer sb = new StringBuffer();

                        sb.append(winbasecode);
                        sb.insert(1, "  ");
                        sb.insert(4, "  ");
                        sb.insert(7, "  ");
                        sb.insert(10, "  ");

                        wincode = sb.toString();

                        // char[] charWincode = wincode.toCharArray();
                        //
                        // for(int j = 0;j<charWincode.length;j++)
                        // {
                        // wincode = charWincode.toString() + " ";
                        // }

                        subscribeLotService.sendOpenInfo(userno, batchcode, wincode, "时时彩",
                                "http://wx.ruyicai.com/html/lottery/ssclot.html", lstLotinfo.get(0).getAmt(), date);
                    }
                    // System.out.println(list.get(i));
                }
            } else if (lotNo.equals("F47104"))//
            {
                // 订阅用户
                List<SubscriberInfo> lstSubscriberInfo = subscriberInfoDao.findSubscriberInfoByLottype(lotNo, "1");

                StringBuffer sb = new StringBuffer();
                sb.append(winbasecode);
                sb.insert(2, "  ");
                sb.insert(6, "  ");
                sb.insert(10, "  ");
                sb.insert(14, "  ");
                sb.insert(18, "  ");
                wincode = sb.toString();

                if (winspecialcode.length() > 2) {
                    sb.delete(0, sb.length());
                    sb.append(winspecialcode);
                    sb.insert(2, "，幸运蓝球：");
                    winspecialcode = sb.toString();
                }

                List<OrderLotInfo> lstLotinfo = null;
                String userno = "";

                for (int i = 0; i < lstSubscriberInfo.size(); i++) {
                    userno = lstSubscriberInfo.get(i).getUserno();
                    lstLotinfo = orderLotInfoDao.findSubscriberInfoByUserno(userno, batchcode);

                    logger.info("T01004:" + userno + "|" + batchcode + "|" + batchcode);
                    String date = "未投注";

                    int amt = 0;

                    if (lstLotinfo.size() > 0) {
                        java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                        date = format_open.format(lstLotinfo.get(0).getCreattime().getTime());
                        amt = lstLotinfo.get(0).getAmt();
                    }

                    subscribeLotService.sendOpenInfo(lstSubscriberInfo.get(i).getUserno(), batchcode, wincode + "  +  "
                            + winspecialcode, "双色球", "http://wx.ruyicai.com/wxpay/ssqKjgg.html", amt, date);
                    // System.out.println(list.get(i));
                }
            } else if (lotNo.equals("T01001"))//
            {
                List<SubscriberInfo> lstSubscriberInfo = subscriberInfoDao.findSubscriberInfoByLottype(lotNo, "1");

//                StringBuffer sb = new StringBuffer();
//                sb.append(winbasecode);
//                sb.insert(2, "  ");
//                sb.insert(6, "  ");
//                sb.insert(10, "  ");
//                sb.insert(14, "  ");
//
//                wincode = sb.toString();
//                sb.delete(0, sb.length());
//                sb.append(winspecialcode);
//                sb.insert(2, "  ");
//                winspecialcode = sb.toString();
                
                List<OrderLotInfo> lstLotinfo = null;
                String userno = "";
                
                for (int i = 0; i < lstSubscriberInfo.size(); i++) {
                    userno = lstSubscriberInfo.get(i).getUserno();
                    lstLotinfo = orderLotInfoDao.findSubscriberInfoByUserno(userno, batchcode);

                    logger.info("T01001:" + userno + "|" + batchcode + "|" + batchcode);
                    String date = "未投注";

                    int amt = 0;

                    if (lstLotinfo.size() > 0) {
                        java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
                        date = format_open.format(lstLotinfo.get(0).getCreattime().getTime());
                        amt = lstLotinfo.get(0).getAmt();
                    }

                    subscribeLotService.sendOpenInfo(lstSubscriberInfo.get(i).getUserno(), batchcode, winbasecode, "大乐透"+batchcode+"期", "http://wx.ruyicai.com/wxpay/dltKjgg.html", amt, date);
                    // System.out.println(list.get(i));
                }
            }

             logger.info("活动监听 end lotno:"+lotNo+";batchcode:"+batchcode+";wincode:"+wincode+";winbasecode:"+winbasecode+";winspecialcode:"+winspecialcode);
        } catch (Exception e) {
            logger.info("活动监听发生异常,lotno:" + lotNo+";batchcode:"+batchcode+";wincode:"+wincode+";winbasecode:"+winbasecode+";winspecialcode:"+winspecialcode, e.getMessage());
        }
    }
}
