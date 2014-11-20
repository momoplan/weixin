package com.ruyicai.weixin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@RequestMapping(value = "/subsribeLot")
@Controller
public class SubscribeLotController {

    @Autowired
    private SubscriberInfoDao subscriberInfoDao;

    private Logger logger = LoggerFactory.getLogger(SubscribeLotController.class);

    @RequestMapping(value = "/findSubInfoByUserNo", method = RequestMethod.GET)
    @ResponseBody
    public String getSysPacket(@RequestParam(value = "userno", required = false) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

            List<SubscriberInfo> lstSubscriberInfo = subscriberInfoDao.findSubscriberInfoByUserno(userno);

            iMap.put("size", lstSubscriberInfo.size());
            iMap.put("SubInfo", lstSubscriberInfo);
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

        return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/editSubInfoByUserNo", method = RequestMethod.GET)
    @ResponseBody
    public String editSubInfoByUserNo(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "sub_type", required = false) String sub_type,
            @RequestParam(value = "sub_status", required = true) String sub_status,
            @RequestParam(value = "lot_type", required = true) String lot_type,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

            SubscriberInfo subscriberInfo = subscriberInfoDao.createOrEditSubscriberInfo(userno, lot_type, sub_status,
                    sub_type);
            iMap.put("SubInfo", subscriberInfo);

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

        return JsonMapper.toJsonP(callback, rd);
    }
    
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    @ResponseBody
    public String getUsers(
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            Map<String, Object> iMap = new HashMap<String, Object>();

           
            iMap.put("SubInfo", subscriberInfoDao.findLotSubUsers());

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

        return JsonMapper.toJsonP(callback, rd);
    }
}
