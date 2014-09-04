package com.ruyicai.weixin.controller;

import java.util.Map;

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

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.domain.Activity;
import com.ruyicai.weixin.domain.ActivityDetail;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.ChancesDetail;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.Page;

@RequestMapping(value = "/activity")
@Controller
public class CaselotActivityController {

	private Logger logger = LoggerFactory.getLogger(CaselotActivityController.class);

	@Autowired
	private CaseLotActivityService caseLotActivityService;

	@Autowired
	private LotteryService lotteryService;

	/**
	 * 查询活动参与情况带分页
	 * 
	 * @param orderid
	 * @param startLine
	 * @param endLine
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/findActivityDetail", method = RequestMethod.GET)
	@ResponseBody
	public String findActivityDetail(@RequestParam(value = "orderid", required = false) String orderid,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "10") int endLine,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("findActivityDetail orderid:{} startLine:{} endLine:{}", orderid, startLine, endLine);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(orderid)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			Page<ActivityDetail> page = new Page<ActivityDetail>(startLine, endLine);
			ActivityDetail.findActivityDetailByPage(orderid, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			logger.error("findActivityDetail error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("findActivityDetail error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 查询推广人推广成功列表
	 * 
	 * @param orderid
	 * @param linkUserno
	 * @param startLine
	 * @param endLine
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/findChancesDetail", method = RequestMethod.GET)
	@ResponseBody
	public String findChancesDetail(@RequestParam(value = "orderid", required = false) String orderid,
			@RequestParam(value = "linkUserno", required = false) String linkUserno,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "10") int endLine,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("findChancesDetail orderid:{} linkUserno：{} startLine:{} endLine:{}", orderid, linkUserno,
				startLine, endLine);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(orderid) || StringUtils.isEmpty(linkUserno)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid or userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			Page<ChancesDetail> page = new Page<ChancesDetail>(startLine, endLine);
			ChancesDetail.findChancesDetailByPage(orderid, linkUserno, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			logger.error("findChancesDetail error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("findChancesDetail error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 查找或创建如意彩联合登录用户
	 * 
	 * @param openid
	 * @param nickname
	 * @param callback
	 * @return 如意彩用户编号
	 */
	@RequestMapping(value = "/findOrCreateBigUser", method = RequestMethod.GET)
	@ResponseBody
	public String findOrCreateBigUser(@RequestParam(value = "openid", required = false) String openid,
			@RequestParam(value = "nickname", required = false) String nickname,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("findOrCreateBigUser openid:{} nickname:{}", openid, nickname);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(openid)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument openid is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			Map<String, Object> tbiguserinfo = lotteryService.findOrCreateBigUser(openid, nickname, Const.DEFAULT_BIGUSER_TYPE);
			String userno = null;
			if (tbiguserinfo != null)
				userno = tbiguserinfo.containsKey("userno") ? (String) tbiguserinfo.get("userno") : "";
				
			rd.setValue(userno);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			logger.error("findOrCreateBigUser error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("findOrCreateBigUser error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 参与活动
	 * 
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
				rd.setValue("参数错误the argument userno or orderid is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			CaseLotUserinfo caselotuserinfo = caseLotActivityService.findOrCreateCaseLotUserinfo(userno, orderid,
					nickname, headimgurl,"");
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
		} catch (Exception e) {
			logger.error("/activity/join error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 创建用户机会
	 * 
	 * @param linkUserno 推广用户
	 * @param joinUserno 点击链接的用户
	 * @param orderid
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/createChanceDetail", method = RequestMethod.GET)
	@ResponseBody
	public String createChanceDetail(@RequestParam(value = "linkUserno", required = false) String linkUserno,
			@RequestParam(value = "joinUserno", required = false) String joinUserno,
			@RequestParam(value = "orderid", required = false) String orderid,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("createChanceDetail linkUserno:{},joinUserno:{},orderid:{}", linkUserno, joinUserno, orderid);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(linkUserno) || StringUtils.isEmpty(joinUserno) || StringUtils.isEmpty(orderid)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument linkUserno or joinUserno or orderid is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			ChancesDetail createChanceDetail = caseLotActivityService.createChanceDetail(linkUserno, joinUserno,
					orderid);
			rd.setErrorCode("0");
			rd.setValue(createChanceDetail);
		} catch (WeixinException e) {
			logger.error("/activity/createChanceDetail error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("/activity/createChanceDetail error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
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
			logger.error("/activity/activitydetail error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("/activity/activitydetail error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	/**
	 * 创建合买 用户
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
				rd.setValue("参数错误the argument userno or orderid is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			CaseLotUserinfo caseLotUserinfo = caseLotActivityService.findOrCreateCaseLotUserinfo(userno, orderid,
					nickname, headimgurl,"");
			rd.setErrorCode("0");
			rd.setValue(caseLotUserinfo);
		} catch (WeixinException e) {
			logger.error("/activity/createcaselotuserinfo error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("/activity/createcaselotuserinfo error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
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
			logger.error("/activity/chances error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("/activity/chances error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

}
