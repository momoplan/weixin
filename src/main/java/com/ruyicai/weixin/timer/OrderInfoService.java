package com.ruyicai.weixin.timer;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ruyicai.weixin.dao.NumActionDao;
import com.ruyicai.weixin.dao.PuntListDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.NumAction;
import com.ruyicai.weixin.domain.PuntList;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.CommonService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.DateUtil;

@Component
public class OrderInfoService {

    private Logger logger = LoggerFactory.getLogger(OrderInfoService.class);

    @Autowired
    private CommonService commonService;

    @Autowired
    PacketActivityService packetActivityService;
    
    @Autowired
    private NumActionDao numActionDao;

    @Autowired
    PuntListDao puntListDao;

    @Autowired
    PuntPacketDao puntPacketDao;

    @Autowired
    LotteryService lotteryService;

    public void process() {
        logger.info("===========定时更新投注订单中奖金额开始===========");
        String opentime = DateUtil.format("yyyy-MM-dd", new Date());
        List<PuntList> puntList = puntListDao.findPuntListNotPrized(opentime);
        if (puntList == null || puntList.size() == 0) {
            logger.info("无投注订单可更新");
            return;
        }

        StringBuilder orderids = new StringBuilder();
        int count = 0;
        for (PuntList punt : puntList) {
            ++count;
            orderids.append(punt.getOrderid()).append(",");

            if (count == 100) {
                orderids.delete(orderids.length() - 1, orderids.length());
                getOrdersInfo(orderids.toString());
                // 重新初始化
                count = 0;
                orderids = new StringBuilder();
            }
        }

        if (count != 0) {
            orderids.delete(orderids.length() - 1, orderids.length());
            getOrdersInfo(orderids.toString());
        }

        logger.info("===========定时更新投注订单中奖金额结束===========");
        
        
        Calendar cal = Calendar.getInstance();
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        String lottype = "";
        
        if(w == 0 || w== 2||w==4)
        {
            lottype ="F47104";
        }
        else if(w == 1 || w== 3||w==6)
        {
            lottype ="T01001";
        }
      
      
        JSONObject preOrderInfo = commonService.getPreBatchInfo(lottype);
        String batchCode = "";   
        String winCode = "";   
         
        batchCode = preOrderInfo.getString("batchCode");
        winCode = preOrderInfo.getString("winCode");
        String betcode = "";
        
        
        List<NumAction> lstNumAction = numActionDao.findNumActionByBatchcode(batchCode,lottype);
        
        for (NumAction numAction : lstNumAction) {
        
            betcode = numAction.getBetcode();
           
            
            getAward(lottype,betcode,winCode);
            
            
        }
 
    }
    
    private int getAward(String lottype,String betcode,String winCode)
    {
        if(lottype.equals("F47104"))
        {
            String[] nums = new String[6];
        }
        return 0;
    }

    /**
     * 更新中奖金额
     * 
     * @param orderid
     * @param prizeAmt
     */
    public void doUpdatePrizeAmt(String orderid, BigDecimal prizeAmt) {
        try {
            logger.info("更新订单中奖金额  orderid:{} prizeAmt:{} ", orderid, prizeAmt);
            PuntList puntList = puntListDao.findPuntListByOrderid(orderid);
            // 更新中奖金额
            puntListDao.merge(puntList, prizeAmt.intValue());

            if (puntList.getStatus() == 1000) {
                List<PuntPacket> puntPacket = puntPacketDao.findUsersByUsernoByPage(puntList.getId());
                if (puntPacket.size() > 0) {

                    String packet_userno = puntPacket.get(0).getAwardUserno();

                    String given_userno = puntPacket.get(0).getGetUserno();

                    if (null != packet_userno) {

                        List<PuntPacket> lstPuntPacketPerson = puntPacketDao.findGetPersons(puntPacket.get(0)
                                .getPacketId().toString());
                        int getPersons = lstPuntPacketPerson.size();
                        float given = (float) 0.5;

                        if (getPersons < 40) {
                            given += 0.01 * getPersons;
                        } else
                            given = (float) 0.9;

                        String ret = lotteryService.addDrawableMoney(packet_userno,
                                String.valueOf((int) (prizeAmt.intValue() * given)), "1101", "20141001活动中奖分配");
                        logger.info("20141001活动中奖分配1{},{},ret:{}", packet_userno,
                                String.valueOf((int) (prizeAmt.intValue() * given)), ret);                                             

                        ret = lotteryService.addDrawableMoney(given_userno,
                                String.valueOf((int) (prizeAmt.intValue() * (1 - given))), "1101", "20141001活动中奖分配");
                        logger.info("20141001活动中奖分配2{},{},{},ret:{}", given_userno, ret,
                                String.valueOf((int) (prizeAmt.intValue() * (1 - given))), ret);
                        puntList.setGetPercent((int)(given*100));
                        puntList.merge();

                    } else {
                        String ret = lotteryService.addDrawableMoney(given_userno, String.valueOf(prizeAmt.intValue()),
                                "1101", "20141001活动中奖分配");
                        logger.info("20141001活动中奖分配3{},{},ret:{}", given_userno, String.valueOf(prizeAmt.intValue()),
                                ret);

                    }
                }
            }

            // if (BigDecimal.ZERO.compareTo(prizeAmt) < 0) {
            // // 发送中奖信息
            // PuntPacket puntPacket =
            // PuntPacket.findPuntPacket(puntList.getPuntId());
            // packetActivityService.sendBetInfo(puntPacket.getGetUserno(),
            // String.valueOf(prizeAmt.intValue() / 100));
            // }
        } catch (WeixinException we) {
            logger.info(we.getErrorCode().value);
        } catch (Exception e) {
            logger.error("更新中奖金额异常", e);
        }
    }

    /**
     * 获取订单中奖详情 </p>prizestate 中奖状态, 3-不中奖; 4-中奖; 5-中奖
     * 
     * @param orderids
     *            逗号分隔
     * @return
     */
    @Async
    public void getOrdersInfo(String orderids) {
        Map<String, JSONObject> json = commonService.doGetOrdersInfo(orderids);
        if (json == null) {
            logger.info("获取订单详情为空");
            return;
        }

        for (Entry<String, JSONObject> orderEnt : json.entrySet()) {
            JSONObject order = orderEnt.getValue();
            BigDecimal prizestate = new BigDecimal(order.get("prizestate").toString()); // 开奖状态
            if (new BigDecimal(3).compareTo(prizestate) <= 0) {
                String orderid = orderEnt.getKey();
                BigDecimal prizeAmt = new BigDecimal(order.get("orderprizeamt").toString()); // 中奖金额
                doUpdatePrizeAmt(orderid, prizeAmt);
            }
        }
    }

}
