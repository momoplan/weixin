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
import com.ruyicai.weixin.dao.AsiaFootballPacketInfoDao;
import com.ruyicai.weixin.dao.MoneyEnvelopeDao;
import com.ruyicai.weixin.dao.MoneyEnvelopeGetInfoDao;
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.AsiaFootballPacketInfo;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.MoneyEnvelopeGetInfo;
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

@RequestMapping(value = "/asiaPacket")
@Controller
public class AsiaFootballPacketInfoController {

    private Logger logger = LoggerFactory.getLogger(AsiaFootballPacketInfoController.class);

    @Autowired
    AsiaFootballPacketInfoDao asiaFootballPacketInfoDao;

    @Autowired
    CommonService commonService;

    // 创建红包
    @RequestMapping(value = "/createMoneyEnvelope", method = RequestMethod.GET)
    @ResponseBody
    public String createMoneyEnvelope(@RequestParam(value = "get_userno", required = true) String userno,
            @RequestParam(value = "money", required = true) int money,
            @RequestParam(value = "packet_userno", required = true) String packet_userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {http://www.ruyicai.com/weixin/service
            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            String ret = "";
            List<AsiaFootballPacketInfo> lstAsiaPacketInfo = asiaFootballPacketInfoDao
                    .getAsiaFootballPacketInfoByGetUserno(userno);
            if (lstAsiaPacketInfo.size() == 0) {
                AsiaFootballPacketInfo asiaFootballPacketInfo = asiaFootballPacketInfoDao.createMoneyEnvelope(userno,
                        money, packet_userno);
                logger.info("total_use_money:" + money + ",get_userno:" + userno+ ",packet_userno:" + packet_userno);
                try {
                    ret = commonService.presentDividend(userno, String.valueOf(money), "11012", "亚洲杯客户端分享微信赠送彩金");
                    logger.info("客户端分享赠送彩金返回" + ret + ",total_use_money:" + money + ",get_userno:" + userno);
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                }

                if (!packet_userno.equals("0")) {
                    Long lngCount = asiaFootballPacketInfoDao.getTotalGetUsers(packet_userno);

                    if (lngCount == 10 || lngCount == 30) {
                        AsiaFootballPacketInfo AsiaPacketInfo = asiaFootballPacketInfoDao
                                .getAsiaFootballPacketInfoByGetUserno(packet_userno).get(0);
                        AsiaPacketInfo.setAwardMoney(AsiaPacketInfo.getAwardMoney() + 100);
                        AsiaPacketInfo.merge();
                        logger.info("total_use_money:100,get_userno:" + packet_userno);
                        ret = commonService.presentDividend(packet_userno, "100", "11012", "亚洲杯客户端分享微信赠送彩金100");
                        logger.info("客户端分享赠送彩金返回" + ret + ",total_use_money:100,get_userno:"
                                + AsiaPacketInfo.getPacketUserno() + ",id:" + AsiaPacketInfo.getId());

                    }
                }
                rd.setValue(asiaFootballPacketInfo);
            } else
                rd.setValue(lstAsiaPacketInfo.get(0));
            // iMap.put("pid", pid);

        } catch (WeixinException e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }
        
 

        return JsonMapper.toJsonP(callback,rd);
    }

    // 抢发模板消息
    @RequestMapping(value = "/getUserCount", method = RequestMethod.GET)
    @ResponseBody
    public String getUserCount(@RequestParam(value = "packet_userno", required = true) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            Long lngCount = asiaFootballPacketInfoDao.getTotalGetUsers(userno);
            // iMap.put("moneyEnvelope",
            // subscribeLotService.sendTemplateMsg(json));
            iMap.put("counts", lngCount);
            iMap.put("packet_userno", userno);

            rd.setValue(lngCount);
        } catch (WeixinException e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        if (callback.equals(""))
            return JsonMapper.toJson(rd);
        else
            return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/getTotalUserCount", method = RequestMethod.GET)
    @ResponseBody
    public String getTotalUserCount(

    @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            Long lngCount = asiaFootballPacketInfoDao.getTotalGetUsers();
            // iMap.put("moneyEnvelope",
            // subscribeLotService.sendTemplateMsg(json));
            iMap.put("counts", lngCount);

            rd.setValue(lngCount);
        } catch (WeixinException e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("findReturnPacketList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }

        if (callback.equals(""))
            return JsonMapper.toJson(rd);
        else
            return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            AsiaFootballPacketInfo AsiaPacketInfo = asiaFootballPacketInfoDao.getAsiaFootballPacketInfoByUserno(userno)
                    .get(0);
           // Long lngCount = asiaFootballPacketInfoDao.getTotalGetUsers();
            // iMap.put("moneyEnvelope",
            // subscribeLotService.sendTemplateMsg(json));
            iMap.put("asiaPacketInfo", AsiaPacketInfo);

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

        if (callback.equals(""))
            return JsonMapper.toJson(rd);
        else
            return JsonMapper.toJsonP(callback, rd);
    }
    
    @RequestMapping(value = "/findGetUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public String findGetUserInfo(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();
            AsiaFootballPacketInfo AsiaPacketInfo = asiaFootballPacketInfoDao.getAsiaFootballPacketInfoByGetUserno(userno)
                    .get(0);
           // Long lngCount = asiaFootballPacketInfoDao.getTotalGetUsers();
            // iMap.put("moneyEnvelope",
            // subscribeLotService.sendTemplateMsg(json));
            iMap.put("asiaPacketInfo", AsiaPacketInfo);

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

        if (callback.equals(""))
            return JsonMapper.toJson(rd);
        else
            return JsonMapper.toJsonP(callback, rd);
    }


}
