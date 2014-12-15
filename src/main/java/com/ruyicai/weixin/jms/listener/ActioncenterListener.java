package com.ruyicai.weixin.jms.listener;

import java.util.Map;

import org.apache.camel.Header;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.LotteryService;

/**
 * 购彩活动监听
 * 
 * @author Administrator
 *
 */
@Service
public class ActioncenterListener {

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    CaseLotActivityService caseLotActivityService;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

    private static Logger logger = LoggerFactory.getLogger(ActioncenterListener.class);

    public void process(@Header("TTRANSACTIONID") String ttransactionid,
            @Header("LADDERPRESENTFLAG") Long ladderpresentflag, @Header("USERNO") String userno,
            @Header("TYPE") Integer type, @Header("BUSINESSID") String businessId,
            @Header("BUSINESSTYPE") Integer businessType, @Header("AMT") Long amt, @Header("BANKID") String bankid,@Header(value = "") String header) {
        try {
//            logger.info("活动监听 start userno:" + userno + ";type:" + type + ";businessType:" + businessType + ";amt:"
//                    + amt + "businessId:" + businessId+"TTRANSACTIONID:"+TTRANSACTIONID+"amt:"+amt);
//            System.out.println("活动监听 start userno:" + userno + ";type:" + type + ";businessType:" + businessType
//                    + ";amt:" + amt);

            long startMillis = System.currentTimeMillis();
            if (type == null) {

                logger.info("活动监听 end");
                return;
            }

            if (ttransactionid == null)
                ttransactionid = businessId;

            Map<String, Object> ttransactioninfo = lotteryService.findBetInfoByTransactionID(ttransactionid);

//            logger.info("ttransactionid:{}", ttransactionid);
//
//            logger.info("ttransactioninfo == null:{}", ttransactioninfo == null);
//
//            logger.info("type==2:{}", type == 2);

            String batchcode = "";
            String betcode = "";
            String lotno = "";

            if (ttransactioninfo != null && type == 2) {

                batchcode = ttransactioninfo.containsKey("batchcode") ? (String) ttransactioninfo.get("batchcode") : "";
                betcode = ttransactioninfo.containsKey("betcode") ? (String) ttransactioninfo.get("betcode") : "";
                if(betcode.equals(""))
                    betcode = ttransactioninfo.containsKey("orderinfo") ? (String) ttransactioninfo.get("orderinfo") : "";
                    
                lotno = ttransactioninfo.containsKey("lotno") ? (String) ttransactioninfo.get("lotno") : "";
//                lotno = ttransactioninfo.containsKey("lotno") ? (String) ttransactioninfo.get("lotno") : "";
                

//                logger.info("lotno:", lotno);

                if (lotno.equals("F47104") || lotno.equals("T01001") || lotno.equals("J00003")
                        || lotno.equals("T01007") || lotno.equals("J00004") || lotno.equals("J00011")
                        || lotno.equals("J00013") || lotno.equals("J00001") || lotno.equals("J00002")) {

                    try {
                      logger.info("活动监听 start userno:" + userno + ";type:" + type + ";businessType:" + businessType + ";amt:"
                      + amt + "businessId:" + businessId+"TTRANSACTIONID:"+ttransactionid+"amt:"+amt);
                        CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(userno,
                                Const.WX_PACKET_ACTIVITY);
                        if (null != caseLotUserinfo) {
                            OrderLotInfo lotinfo = orderLotInfoDao.createOrderlotinfo(userno, batchcode, lotno,
                                    ttransactionid, betcode,amt.intValue());
//                            logger.info("lotinfo.getBatchcode()", lotinfo.getBatchcode());
                        }
                    } catch (Exception ex) {
                        logger.info(ex.getMessage());
                        System.out.println(ex.getMessage());
                    }
                }
            }

            long endMillis = System.currentTimeMillis();
//            logger.info("活动监听 end,用时:" + (endMillis - startMillis) + " userno:" + userno + ";type:" + type
//                    + ";businessType:" + businessType + ";amt:" + amt);
        } catch (Exception e) {
            logger.info("活动监听发生异常,userno:" + userno, e);
        }
    }

}
