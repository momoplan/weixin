package com.ruyicai.weixin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.domain.Activity;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.AsyncService;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.util.JsonMapper;

@RequestMapping(value = "/activity")
@Controller
public class CaselotActivityController {

	private Logger logger = LoggerFactory.getLogger(CaselotActivityController.class);

	@Autowired
	private AsyncService asyncService;
	@Autowired
	private CaseLotActivityService caseLotActivityService;

	/**
	 * 参与活动
	 * @param userno
	 * @param orderid
	 * @param nickname
	 * @param headimgurl
	 * @param linkUserno
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/join", method = RequestMethod.GET)
	@ResponseBody
	public String joinActivity(@RequestParam(value = "userno", required = false) String userno,
			@RequestParam(value = "orderid", required = false) String orderid,
			@RequestParam(value = "nickname", required = false) String nickname,
			@RequestParam(value = "headimgurl", required = false) String headimgurl,
			@RequestParam(value = "linkUserno", required = false) String linkUserno,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("合买活动免费领取彩票：userno:{},orderid:{},linkUserno:{},nickname{},headimgurl{}", userno, orderid,
				linkUserno, nickname, headimgurl);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(userno) || StringUtils.isEmpty(orderid)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			CaseLotUserinfo caselotuserinfo = caseLotActivityService.findOrCreateCaseLotUserinfo(userno, orderid,
					nickname, headimgurl);
			logger.info("创建活动用户信息表：" + caselotuserinfo);
			if (caselotuserinfo != null) {
				caselotuserinfo = caseLotActivityService.joinActivity(userno, orderid, linkUserno);
			} else {
				rd.setErrorCode("10002");
				rd.setValue(caselotuserinfo);
			}
			rd.setErrorCode("0");
			rd.setValue(caselotuserinfo);
		} catch (WeixinException e) {
			logger.error("/activity/join error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 查询活动详情
	 * 
	 * @param orderid
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/activitydetail", method = RequestMethod.GET)
	@ResponseBody
	public String activitydetail(@RequestParam(value = "orderid", required = false) String orderid,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("合买活动免费领取彩票：orderid:{}", orderid);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(orderid)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			Activity activity = caseLotActivityService.findActivityByOrderid(orderid);
			rd.setErrorCode("0");
			rd.setValue(activity);
		} catch (WeixinException e) {
			logger.error("/activity/join error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 
	 * @param userno
	 * @param orderid
	 * @param nickname
	 * @param headimgurl
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/createcaselotuserinfo", method = RequestMethod.GET)
	@ResponseBody
	public String createcaselotuserinfo(@RequestParam(value = "userno", required = false) String userno,
			@RequestParam(value = "orderid", required = false) String orderid,
			@RequestParam(value = "nickname", required = false) String nickname,
			@RequestParam(value = "headimgurl", required = false) String headimgurl,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("合买createcaselotuserinfo orderid:{},userno:{}", orderid, userno);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(orderid) || StringUtils.isEmpty(userno)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			CaseLotUserinfo caseLotUserinfo = caseLotActivityService.findOrCreateCaseLotUserinfo(userno, orderid,
					nickname, headimgurl);
			rd.setErrorCode("0");
			rd.setValue(caseLotUserinfo);
		} catch (WeixinException e) {
			logger.error("/activity/createcaselotuserinfo error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 查询活动详情
	 * 
	 * @param orderid
	 * @param callback
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/chances", method = RequestMethod.GET)
	@ResponseBody
	public String chances(@RequestParam(value = "userno") String userno,
			@RequestParam(value = "orderid") String orderid, @RequestParam(value = "callBackMethod") String callback,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("合买活动免费领取彩票：orderid:{},userno:{}", orderid, userno);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(orderid) || StringUtils.isEmpty(userno)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}

			CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(userno, orderid);
			rd.setErrorCode("0");
			rd.setValue(caseLotUserinfo);
		} catch (WeixinException e) {
			logger.error("/activity/join error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

}
