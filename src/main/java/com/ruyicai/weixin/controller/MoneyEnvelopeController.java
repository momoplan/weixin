package com.ruyicai.weixin.controller;

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

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.AccessLogDao;
import com.ruyicai.weixin.dao.MoneyEnvelopeDao;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.SubscribeLotService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@RequestMapping(value = "/moneyEnvelope")
@Controller
public class MoneyEnvelopeController {

    @Autowired
    private MoneyEnvelopeDao moneyEnvelopeDao;

    private Logger logger = LoggerFactory.getLogger(MoneyEnvelopeController.class);

    @Autowired
    SubscribeLotService subscribeLotService;
    
    @Autowired
    SubscriberInfoDao subscriberInfoDao;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

    //创新红包
    @RequestMapping(value = "/createMoneyEnvelope", method = RequestMethod.GET)
    @ResponseBody
    public String createMoneyEnvelope(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "parts", required = false) int parts,
            @RequestParam(value = "money", required = true) int money,
            @RequestParam(value = "channelName", required = true) String channelName,
            @RequestParam(value = "exire_date", required = true) String exire_date) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

            MoneyEnvelope subscriberInfo = moneyEnvelopeDao.createMoneyEnvelope(userno, parts, money, exire_date, channelName);
            iMap.put("moneyEnvelope", subscriberInfo);

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
        
        return JsonMapper.toJson( rd);
    }
    
    //抢红包
    @RequestMapping(value = "/getMoneyfromEnvelope", method = RequestMethod.GET)
    @ResponseBody
    public String getMoneyfromEnvelope(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "packet_id", required = true) int packet_id) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

//            MoneyEnvelope subscriberInfo = moneyEnvelopeDao.createMoneyEnvelope(userno, parts, money, exire_date, channelName);
//            iMap.put("moneyEnvelope", subscriberInfo);

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
        
        return JsonMapper.toJson( rd);
    }
    
    //抢红包
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public String sendMsg(@RequestParam(value = "json", required = true) String json) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);
            
            //subscribeLotService.sendTemplateMsg(json);
            Map<String, Object> iMap = new HashMap<String, Object>();

//            MoneyEnvelope subscriberInfo = moneyEnvelopeDao.createMoneyEnvelope(userno, parts, money, exire_date, channelName);
            iMap.put("moneyEnvelope",  subscribeLotService.sendTemplateMsg(json));

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
        
        return JsonMapper.toJson( rd);
    }
}
