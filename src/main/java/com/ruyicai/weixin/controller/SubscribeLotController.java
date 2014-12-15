package com.ruyicai.weixin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

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
import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.dao.SubscriberInfoDao;
import com.ruyicai.weixin.domain.OrderLotInfo;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.SubscribeLotService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@RequestMapping(value = "/subsribeLot")
@Controller
public class SubscribeLotController {

    @Autowired
    private SubscriberInfoDao subscriberInfoDao;

    private Logger logger = LoggerFactory.getLogger(SubscribeLotController.class);

    @Autowired
    SubscribeLotService subscribeLotService;

    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

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
    public String getUsers(@RequestParam(value = "callBackMethod", required = false) String callback) {

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

    @RequestMapping(value = "/getLotUer", method = RequestMethod.GET)
    @ResponseBody
    public String getLotUer(@RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);

            // Map<String, Object> iMap = new HashMap<String, Object>();
            //
            // orderLotInfoDao.createOrderlotinfo("1", "1", "1", "1", "1",0);
            //
            //
            // iMap.put("SubInfo", subscriberInfoDao.findLotSubUsers());
            //
            // rd.setValue(iMap);

            String day = "20141208";
            String weekId = "1";
            String teamId = "007";

            String valueObject = "{\"value\":{\"result\":{\"id\":null,\"result\":\"0:2\",\"endtime\":null,\"audit\":null,\"starttime\":null,\"cancel\":null,\"letpoint\":\"-1\",\"basepoint\":null,\"firsthalfresult\":\"0:2\",\"b0\":null,\"b1\":null,\"b2\":null,\"b3\":null,\"b4\":null,\"b5\":null,\"b6\":null,\"auditname\":null},\"matches\":{\"state\":0,\"type\":1,\"time\":1418061600000,\"day\":\"20141208\",\"shortname\":\"意甲\",\"endtime\":1418054100000,\"audit\":0,\"team\":\"卡利亚里:切沃\",\"weekid\":1,\"teamid\":\"002\",\"league\":\"意大利甲级联赛\",\"saleflag\":0,\"unsupport\":\"J00001_0,J00013_0\",\"letpoint\":\"-1\",\"matchid\":\"61603\",\"teamshortname\":\"卡利亚里:切沃\",\"ctstate\":0,\"ishot\":0}},\"errorCode\":\"0\",\"isDepreciated\":false}";

            JSONObject jbValue = JSONObject.fromObject(valueObject);

            if (jbValue.get("errorCode").equals("0")) {
                logger.info("errorCode:" + jbValue.get("errorCode"));

                JSONObject jso = JSONObject.fromObject(jbValue.get("value"));
                JSONObject jso_result = JSONObject.fromObject(jso.get("result"));
                JSONObject jso_matches = JSONObject.fromObject(jso.get("matches"));
                String teamshortname = jso_matches.get("teamshortname").toString();
                System.out.println(teamshortname);
                String result = jso_result.get("result").toString();
                System.out.println(result);

                String strMatchID = day + "|" + weekId + "|" + teamId;
                List<OrderLotInfo> lotInfo = orderLotInfoDao.findSubscriberInfoByBetcode(strMatchID);

                java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");

                String date = format_open.format(lotInfo.get(0).getCreattime().getTime());

                List<SubscriberInfo> lstSubscriberInfo = null;

                String userno = "";

                if (lotInfo.size() > 0) {
                    System.out.println("lotInfo.size():"+lotInfo.size());

                    for (int i = 0; i < lotInfo.size(); i++) {

                        userno = lotInfo.get(i).getUserno();
                        System.out.println("userno:"+userno);
                        // 优化
                        lstSubscriberInfo = subscriberInfoDao.findSubscriberByUserno(userno, "J00000");

                        if (lstSubscriberInfo.size() > 0) {
                            System.out.println("userno1:"+userno);
                            subscribeLotService.sendOpenInfo(userno, day + weekId + teamId, teamshortname + result,
                                    "竟彩足球", "http://wx.ruyicai.com/html/lottery/jczqlot.html", lotInfo.get(i).getAmt(),
                                    date);
                            System.out.println("lotInfo.get(0).getUserno()" + userno + ",teamshortname+result="
                                    + teamshortname + result);
                        }

                        System.out.println("发送消息userno:" + userno + ":" + day + weekId + teamId + ":" + teamshortname + result);

                    }
                }
            }
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
