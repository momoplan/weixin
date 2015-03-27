package com.ruyicai.weixin.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.AccessLogDao;
import com.ruyicai.weixin.dao.MoneyEnvelopeDao;
import com.ruyicai.weixin.dao.MoneyEnvelopeGetInfoDao;
import com.ruyicai.weixin.dao.NumActionDao;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.PrizePoolDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.MoneyEnvelopeGetInfo;
import com.ruyicai.weixin.domain.NumAction;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.CommonService;
import com.ruyicai.weixin.service.MoneyEnvelopeService;
import com.ruyicai.weixin.service.SubscribeLotService;
import com.ruyicai.weixin.util.HongBaoAlgorithm;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.JsonUtil;
import com.ruyicai.weixin.util.StringUtil;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@RequestMapping(value = "/numAction")
@Controller
public class NumActionController {

    @Autowired
    private NumActionDao numActionDao;

    private Logger logger = LoggerFactory.getLogger(NumActionController.class);

    @Autowired
    CommonService commonService;
    
    @Autowired
    private PrizePoolDao prizePoolDao;

    @Autowired
    SubscribeLotService subscribeLotService;

    @Autowired
    SubscriberInfoDao subscriberInfoDao;

    @Autowired
    MoneyEnvelopeGetInfoDao moneyEnvelopeGetInfoDao;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

    @Autowired
    MoneyEnvelopeService moneyEnvelopeService;

    @Autowired
    CaseLotActivityService caseLotActivityService;

    // 创建红包
    @RequestMapping(value = "/createNumAction", method = RequestMethod.GET)
    @ResponseBody
    public String createNumAction(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "batchcode", required = false) String batchcode,
            @RequestParam(value = "betcode", required = true) String betcode,
            @RequestParam(value = "lottype", required = true) String lottype,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            
            NumAction numAction = numActionDao.createNumAction(userno, batchcode, betcode,lottype);
            rd.setValue(numAction);
        } catch (WeixinException e) {
            logger.error("createNumAction error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("createNumAction error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        return JsonMapper.toJsonP(callback, rd);
       // return JsonMapper.toJson(rd);
    }
    
    // 参与详情
    @RequestMapping(value = "/getNumActionList", method = RequestMethod.GET)
    @ResponseBody
    public String getNumActionList(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        
        ResponseData rd = new ResponseData();
        try {
            rd.setErrorCode(ErrorCode.OK.value);
            
            List<NumAction> lstNumAction = numActionDao.findNumActionByUserno(userno);
            rd.setValue(lstNumAction);
        } catch (WeixinException e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }
    
    @RequestMapping(value = "/getHaveBet", method = RequestMethod.GET)
    @ResponseBody
    public String getHaveBet(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "batchcode", required = false) String batchcode,
            @RequestParam(value = "lottype", required = true) String lottype,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        
        ResponseData rd = new ResponseData();
        try {
            rd.setErrorCode(ErrorCode.OK.value);
            
            List<NumAction> lstNumAction = numActionDao.findHaveBet(userno, batchcode, lottype);
            
            Map<String, Object> iMap = new HashMap<String, Object>();
            iMap.put("size", lstNumAction.size());
            iMap.put("betinfo", lstNumAction);
            
            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }
    
    
    @RequestMapping(value = "/getLotProgressive", method = RequestMethod.GET)
    @ResponseBody
    public String getLotProgressive(
            @RequestParam(value = "lottype", required = true) String lottype,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        
        ResponseData rd = new ResponseData();
        try {
            rd.setErrorCode(ErrorCode.OK.value);
            
           // List<NumAction> lstNumAction = numActionDao.findHaveBet(userno, batchcode, lottype);
            
             
            
            Map<String, Object> iMap = new HashMap<String, Object>();
            iMap.put("lottype", lottype);
            iMap.put("progressive",  100);
//            iMap.put("progressive",  prizePoolDao.getTotalPrize(lottype).intValue());
            
            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }
    
    @RequestMapping(value = "/getLotProgressive1", method = RequestMethod.GET)
    @ResponseBody
    public String getLotProgressive1(
            @RequestParam(value = "lottype", required = true) String lottype,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        
        ResponseData rd = new ResponseData();
        try {
            rd.setErrorCode(ErrorCode.OK.value);
            
           
            
             
            
            Map<String, Object> iMap = new HashMap<String, Object>();
            iMap.put("lottype", lottype);
          
            iMap.put("progressive",  prizePoolDao.getTotalPrize(lottype));
            
            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getNumActionList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }
    
    

    // 抢红包
    @RequestMapping(value = "/getMoneyfromEnvelope", method = RequestMethod.GET)
    @ResponseBody
    public String getMoneyfromEnvelope(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        
        packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY);
        
        Map<String, Object> iMap_status =  moneyEnvelopeService.getPuntPacketStatus(userno, packet_id);
        ResponseData rd = new ResponseData();
        if(!iMap_status.get("status").equals("0"))
        {
            rd.setValue(iMap_status);
            rd.setErrorCode(ErrorCode.PACKET_EXPIRED.value);
            return JsonMapper.toJsonP(callback, rd);          
        }
      
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

            // MoneyEnvelope subscriberInfo =
            // moneyEnvelopeDao.createMoneyEnvelope(userno, parts, money,
            // exire_date, channelName);
            // iMap.put("moneyEnvelope", subscriberInfo);

            MoneyEnvelopeGetInfo moneyEnvelopeGetInfo = moneyEnvelopeService.getPuntPacket(userno, packet_id);
            iMap.put("moneyEnvelopeGetInfo", moneyEnvelopeGetInfo);

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getMoneyfromEnvelope error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getMoneyfromEnvelope error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }

    // 抢红包
    @RequestMapping(value = "/getMoneyStatus", method = RequestMethod.GET)
    @ResponseBody
    public String getMoneyStatus(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "packet_id", required = true) String packet_id,
//            @RequestParam(value = "action_id", required = true) String action_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY);

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

           

            Map<String, Object> iMap_status = new HashMap<String, Object>();
            iMap_status = moneyEnvelopeService.getPuntPacketStatus(userno, packet_id);
            if(iMap_status.get("status").equals("605"))
                rd.setErrorCode(ErrorCode.ACTION_EXPIRED.value);
            iMap.put("getMoenyStatus", iMap_status);

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getMoneyStatus error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getMoneyStatus error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }

    // 红包详情
    @RequestMapping(value = "/getMoneyEnvelopeList", method = RequestMethod.GET)
    @ResponseBody
    public String getMoneyEnvelopeList(@RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY);
        ResponseData rd = new ResponseData();
        try {
            rd.setErrorCode(ErrorCode.OK.value);
            Map<String, Object> iMap = new HashMap<String, Object>();
            moneyEnvelopeGetInfoDao.findMoneyEnveList(packet_id);

            iMap.put("MoneyEnveList", moneyEnvelopeGetInfoDao.findMoneyEnveList(packet_id));

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getMoneyEnvelopeList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getMoneyEnvelopeList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/getUserMoenyList", method = RequestMethod.GET)
    @ResponseBody
    public String getUserMoenyList(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {
            rd.setErrorCode(ErrorCode.OK.value);
            Map<String, Object> iMap = new HashMap<String, Object>();

            List<MoneyEnvelopeGetInfo> lstMoneyEnvelopeGetInfo = moneyEnvelopeGetInfoDao.findUserMoney(userno);

            java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");

            List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();

            String date = "";

            MoneyEnvelopeGetInfo moneyEnvelopeGetInfo = null;

            for (int i = 0; i < lstMoneyEnvelopeGetInfo.size(); i++) {
                moneyEnvelopeGetInfo = lstMoneyEnvelopeGetInfo.get(i);
                Map<String, Object> iMapList = new HashMap<String, Object>();
                iMapList.put("id", moneyEnvelopeGetInfo.getId());
                iMapList.put("money", moneyEnvelopeGetInfo.getMoney());

                date = format_open.format(moneyEnvelopeGetInfo.getCreatetime().getTime());
                iMapList.put("expire_date", date);
                iList.add(iMapList);

            }

            iMap.put("UserMoenyList", iList);

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getUserMoenyList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getUserMoenyList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        // return JsonMapper.toJson(rd);
        return JsonMapper.toJsonP(callback, rd);
    }

    // 抢发模板消息
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public String sendMsg(@RequestParam(value = "json", required = true) String json) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            // subscribeLotService.sendTemplateMsg(json);
            Map<String, Object> iMap = new HashMap<String, Object>();

            // MoneyEnvelope subscriberInfo =
            // moneyEnvelopeDao.createMoneyEnvelope(userno, parts, money,
            // exire_date, channelName);
            iMap.put("moneyEnvelope", subscribeLotService.sendTemplateMsg(json));

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("sendMsg error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("sendMsg error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        return JsonMapper.toJson(rd);
    }

    // 抢发模板消息
    @RequestMapping(value = "/getMoney", method = RequestMethod.POST)
    @ResponseBody
    public String getMoney(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "packet_id", required = true) int packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

            // iMap.put("moneyEnvelope",
            // subscribeLotService.sendTemplateMsg(json));

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("getMoney error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getMoney error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        if (StringUtil.isEmpty(callback))
            return JsonMapper.toJson(rd);
        else
            return JsonMapper.toJsonP(callback, rd);
    }

    // 抢发模板消息
    @RequestMapping(value = "/deductUserMoney", method = RequestMethod.GET)
    @ResponseBody
    public String deductUserMoney(@RequestParam(value = "ids", required = true) String ids,

    @RequestParam(value = "get_userno", required = false) String get_userno,
            @RequestParam(value = "total_use_money", required = false) String total_use_money,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();

        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            int result = moneyEnvelopeGetInfoDao.DeductUserMoney(ids);
            if (result > 0) {
                logger.info("total_use_money:" + total_use_money + ",get_userno:" + get_userno);
                String ret = commonService.presentDividend(get_userno, total_use_money, "11011", "客户端分享赠送彩金");
                logger.info("客户端分享赠送彩金返回" + ret + ",total_use_money:" + total_use_money + ",get_userno:" + get_userno);
                iMap.put("deduct_result", result);
                // subscribeLotService.sendTemplateMsg(json));
                rd.setValue(iMap);
            }
            else
            {
                rd.setErrorCode(ErrorCode.DEDUCT_AMT_FAIL.value);
                rd.setValue(ErrorCode.DEDUCT_AMT_FAIL.memo);
            }
                
          
        } catch (WeixinException e) {
            logger.error("deductUserMoney error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("deductUserMoney error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        return JsonMapper.toJsonP(callback, rd);

    }

    // 抢发模板消息
    @RequestMapping(value = "/testjz", method = RequestMethod.GET)
    @ResponseBody
    public String testjz() {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            String orderJson = "{\"agencyno\":null,\"alreadytrans\":0,\"amt\":200,\"batchcode\":null,\"betcode\":\"502@20141215|1|002|3^20141215|1|004|3^\",\"betnum\":1,\"bettype\":2,\"body\":null,\"buyuserno\":\"03187412\",\"canceltime\":null,\"caselotStarter\":null,\"channel\":null,\"createtime\":1418635177898,\"desc\":null,\"encashtime\":null,\"endtime\":null,\"errorcode\":null,\"eventcode\":\"1_20141215_1_004\",\"hasachievement\":0,\"id\":\"BJ2014121559754537\",\"instate\":1,\"lastprinttime\":1418658480000,\"latedteamid\":\"1_20141215_1_002\",\"lotmulti\":1,\"lotno\":\"J00001\",\"lotsType\":0,\"memo\":null,\"modifytime\":null,\"orderamt\":null,\"orderinfo\":\"502@20141215|1|002|3^20141215|1|004|3^_1_200_200\",\"orderpreprizeamt\":367,\"orderprize\":null,\"orderprizeamt\":367,\"orderstate\":1,\"ordertype\":0,\"paystate\":3,\"paytype\":1,\"playtype\":\"502\",\"prizeinfo\":\"\",\"prizestate\":5,\"subaccount\":null,\"subaccountType\":null,\"subchannel\":\"00092493\",\"successtime\":1418635261478,\"tlotcaseid\":null,\"tsubscribeflowno\":null,\"userno\":\"03187412\",\"visible\":1,\"winbasecode\":\" \"}";
            logger.info("中奖派奖通知,orderJson:" + orderJson);
            System.out.println("中奖派奖通知,orderJson:" + orderJson);
            if (null == orderJson) {
                System.out.println("null == orderJson");
            }

            try {

                Torder order = JsonUtil.fromJson(orderJson, Torder.class);

                if (order == null || order.getUserno() == null || order.getLotno() == null
                        || order.getOrderstate().compareTo(BigDecimal.ONE) != 0) {
                    System.out.println("order == null || order.getUserno() == null || order.getLotno() == null");

                }

                String lotno = order.getLotno();

                if (!(lotno.equals("F47104") || lotno.equals("T01001") || lotno.equals("J00003")
                        || lotno.equals("T01007") || lotno.equals("J00004") || lotno.equals("J00011")
                        || lotno.equals("J00013") || lotno.equals("J00001") || lotno.equals("J00002"))) {
                    System.out.println("lotno.equalsJ00001");
                }

                CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(order.getUserno(),
                        Const.WX_PACKET_ACTIVITY);

                if (null == caseLotUserinfo) {
                    System.out.println(null == caseLotUserinfo);
                }

                logger.info("中奖派奖通知,orderJson:" + orderJson);
                System.out.println("中奖派奖通知,orderJson:" + orderJson);

                String userno = order.getUserno();
                String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/html/user/lotterydetail.html?clientShare=true%26orderid="
                        + order.getId()
                        + "%26userno="
                        + userno
                        + "&from=singlemessage&isappinstalled=0&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
                // String url =
                // "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/html/user/cathecticlist.html?from=singlemessage&isappinstalled=0&response_type=code&scope=snsapi_base&state=1#wechat_redirect";

                if (order.getOrderprizeamt().intValue() == 0) {
                    System.out.println("order.getOrderprizeamt().intValue()==0");
                }

                if (lotno.equals("T01007")) {

                    List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, lotno);
                    logger.info("usrno--:" + userno + "lotno:" + lotno);

                    if (lstSubscriber.size() > 0) {
                        List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByUserno(userno,
                                order.getBatchcode(), lotno);

                        logger.info("usrno--:" + userno + "order.getBatchcode():" + order.getBatchcode() + "lotno:"
                                + lotno);

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
                                    String.valueOf(order.getOrderprizeamt().intValue() / 100),
                                    "双色球" + order.getBatchcode() + "期", url);

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
                                    String.valueOf(order.getOrderprizeamt().intValue() / 100),
                                    "大乐透" + order.getBatchcode() + "期", url);
                    }
                } else if (lotno.startsWith("J000"))//
                {
                    // 这个彩种的订阅用户
                    List<SubscriberInfo> lstSubscriber = subscriberInfoDao.findSubscriberByUserno(userno, "J00000");
                    System.out.println("lstSubscriber.size():" + lstSubscriber.size());

                    if (lstSubscriber.size() > 0) {
                        List<OrderLotInfo> lstSublot = orderLotInfoDao.findSubscriberInfoByTransactionID(userno,
                                order.getId());
                        System.out.println("lstSublot.size():" + lstSublot.size());
                        if (lstSublot.size() > 0)
                            subscribeLotService.sendBetInfo(userno,
                                    String.valueOf(order.getOrderprizeamt().floatValue() / 100), "竞彩足球", url);
                        System.out.println("subscribeLotService.sendBetInfo");
                    }
                }

            } catch (Exception e) {
                logger.error("活动监听发生异常,orderJson:" + orderJson, e.getMessage());
            }

            // iMap.put("moneyEnvelope",
            // subscribeLotService.sendTemplateMsg(json));

            rd.setValue(iMap);
        } catch (WeixinException e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        return JsonMapper.toJson(rd);

    }

}
