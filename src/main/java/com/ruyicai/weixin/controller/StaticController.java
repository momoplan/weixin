package com.ruyicai.weixin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.dao.SubscriberDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.Subscriber;
import com.ruyicai.weixin.dto.WeixinUserDTO;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.FileService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.service.SubscriberService;
import com.ruyicai.weixin.service.WeixinService;
import com.ruyicai.weixin.service.WinInfoImageService;
import com.ruyicai.weixin.util.JsonMapper;

@RequestMapping(value = "/static")
@Controller
public class StaticController {
    private Logger logger = LoggerFactory.getLogger(StaticController.class);

    @Autowired
    private WinInfoImageService wininfoImageService;

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private CaseLotActivityService caseLotActivityService;

    @Autowired
    private FileService fileService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private SubscriberDao subscriberDao;

    @RequestMapping(value = "/wininfo")
    public @ResponseBody ResponseData wininfo(@RequestParam(value = "lotno", required = false) String lotno,
            HttpServletRequest request, HttpServletResponse response) {
        logger.info("/static/wininfo lotno:{}", new Object[] { lotno });
        ResponseData rd = new ResponseData();
        try {
            Long startTime = System.currentTimeMillis();
            wininfoImageService.downLoadWinInfo(lotno, request, response);
            logger.info("运行时间：" + (System.currentTimeMillis() - startTime));
            rd.setErrorCode("0");
        } catch (Exception e) {
            logger.error("/static/wininfo error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }
        return rd;
    }

    @RequestMapping(value = "/image")
    public void image(@RequestParam(value = "fileName", required = false) String fileName,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height, HttpServletRequest request,
            HttpServletResponse response) {
        logger.info("/static/image fileName:{},width:{},height:", fileName, width, height);
        try {
            fileService.getImage(fileName, width, height, request, response);
        } catch (Exception e) {
            logger.error("下载图片出错", e);
        }
    }

    /**
     * 注册联合用户，并创建caselotUser
     * 
     * @param openid
     * @param orderid
     * @param callback
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/createBigUserAndCaseLotUserinfo")
    @ResponseBody
    public String createBigUserAndCaseLotUserinfo(@RequestParam(value = "openid") String openid,
            @RequestParam(value = "orderid") String orderid, @RequestParam(value = "callBackMethod") String callback,
            HttpServletRequest request, HttpServletResponse response) {
        ResponseData rd = new ResponseData();
        try {
            logger.info("/static/createBigUserAndCaseLotUserinfo openid:{} orderid:{}", openid, orderid);
            Map<String, Object> cuiMap = caseLotActivityService.createBigUserAndCaseLotUserinfo(openid, orderid);

            String subscriber = String.valueOf(cuiMap.get("subscribe"));
            CaseLotUserinfo caselotuserinfo = (CaseLotUserinfo) cuiMap.get("caseLotUserinfo");
            String nickName = "";
            if (caselotuserinfo != null)
                nickName = caselotuserinfo.getNickname();
            logger.info("subscriber: {} caselotuserinfo:{}", subscriber, nickName);

            int SubscribeState = subscriber.equals("1") ? 1 : 0;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("openid", openid);
            map.put("subscribe", SubscribeState);
            map.put("caselotuserinfo", caselotuserinfo);
            map.put("name", cuiMap.get("name"));
            map.put("certid", cuiMap.get("certid"));
            map.put("mobileid", cuiMap.get("mobileid"));
            rd.setValue(map);
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (Exception e) {
            logger.error("createBigUserAndCaseLotUserinfo error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    /**
     * 根据openid查询微信用户信息
     * 
     * @param openid
     * @param callback
     * @return
     */
    // @RequestMapping(value = "/findUserinfoByOpenid")
    // @ResponseBody
    // public String findUserinfoByOpenid(@RequestParam(value = "openid",
    // required = false) String openid,
    // @RequestParam(value = "callBackMethod") String callback) {
    // logger.info("/static/findUserinfoByOpenid openid:{}", openid);
    // ResponseData rd = new ResponseData();
    // try {
    // if (StringUtils.isEmpty(openid)) {
    // rd.setErrorCode("10001");
    // rd.setValue("参数错误the argument orderid is require.");
    // return JsonMapper.toJsonP(callback, rd);
    // }
    // String accessToken = weixinService.getAccessToken();
    // WeixinUserDTO weixinUserDTO =
    // weixinService.findUserinfoByOpenid(accessToken, openid);
    // rd.setErrorCode(ErrorCode.OK.value);
    // rd.setValue(weixinUserDTO);
    // } catch (Exception e) {
    // logger.error("createActivity error", e);
    // rd.setErrorCode(ErrorCode.ERROR.value);
    // rd.setValue(e.getMessage());
    // }
    // return JsonMapper.toJsonP(callback, rd);
    // }

    /**
     * 根据code查询微信用户信息
     * 
     * @param openid
     * @param callback
     * @return
     */
    @RequestMapping(value = "/findUserinfoByCode")
    @ResponseBody
    public String findUserinfoByCode(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "callBackMethod") String callback) {
        logger.info("/static/findUserinfoByCode code:{}", code);
        ResponseData rd = new ResponseData();
        try {
            if (StringUtils.isEmpty(code)) {
                rd.setErrorCode("10001");
                rd.setValue("参数错误the argument code is require.");
                return JsonMapper.toJsonP(callback, rd);
            }
            String rejson = weixinService.getOauth(code);
            if (rejson.contains("errcode")) {
                rd.setErrorCode(ErrorCode.ERROR.value);
                rd.setValue(rejson);
            } else {
                JSONObject js = new JSONObject(rejson);
                String openid = (String) js.get("openid");

                String accessToken = weixinService.getAccessToken();
                WeixinUserDTO weixinUserDTO = weixinService.findUserinfoByOpenid(accessToken, openid);
                rd.setErrorCode(ErrorCode.OK.value);
                rd.setValue(weixinUserDTO);
            }
        } catch (Exception e) {
            logger.error("findUserinfoByCode error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    /**
     * 根据code查询微信用户信息
     * 
     * @param openid
     * @param callback
     * @return
     */
    @RequestMapping(value = "/findSubscribleInfo")
    @ResponseBody
    public String findSubscribleInfo(@RequestParam(value = "openid", required = false) String openid,
            @RequestParam(value = "callBackMethod") String callback) {
        logger.info("/static/findUserinfoByCode code:{}", openid);
        ResponseData rd = new ResponseData();
        try {
            Subscriber subscriber = subscriberDao.findSubscriber(openid, "gh_894976f750e3");

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(subscriber);

        } catch (Exception e) {
            logger.error("findUserinfoByCode error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }
}
