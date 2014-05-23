package com.ruyicai.weixin.service;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.domain.Activity;
import com.ruyicai.weixin.domain.ActivityDetail;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.CaseLotUserinfoPK;
import com.ruyicai.weixin.domain.ChancesDetail;
import com.ruyicai.weixin.domain.ChancesDetailPK;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;

@Service
public class CaseLotActivityService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private WeixinService weixinService;

	@Transactional
	public CaseLotUserinfo findOrCreateCaseLotUserinfo(String userno, String orderid, String nickname, String headimgurl) {
		logger.info("createCaseLotUserinfo userno:{} orderid:{} nickname:{} headimgurl:{}", userno, orderid, nickname,
				headimgurl);
		if (StringUtils.isEmpty(userno)) {
			throw new IllegalArgumentException("the argument userno is require.");
		}
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(userno, orderid));
		if (caseLotUserinfo == null) {
			findActivityByOrderid(orderid);
			caseLotUserinfo = CaseLotUserinfo.createCaseLotUserinfo(userno, orderid, nickname, headimgurl);
		}
		return caseLotUserinfo;
	}

	@Transactional
	public Activity findActivityByOrderid(String orderid) {
		logger.info("findActivityByOrderid orderid:{}", orderid);
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		Activity activity = Activity.findActivity(orderid);
		if (activity == null) {
			throw new WeixinException(ErrorCode.CASELOT_NOT_EXISTS);
		}
		return activity;
	}

	@Transactional
	public CaseLotUserinfo caseLotchances(String userno, String orderid) {
		logger.info("selectActivityDetail orderid:{},userno:{}", orderid, userno);
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		if (StringUtils.isEmpty(userno)) {
			throw new IllegalArgumentException("the argument userno is require.");
		}
		CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(userno, orderid));
		if (caseLotUserinfo == null) {
			throw new WeixinException(ErrorCode.CASELOTUSERINFO_NOT_EXISTS);
		}
		return caseLotUserinfo;
	}

	@Transactional
	public CaseLotUserinfo joinActivity(String userno, String orderid, String linkUserno) {
		if (StringUtils.isEmpty(userno)) {
			throw new IllegalArgumentException("the argument userno is require.");
		}
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		logger.info("合买活动免费领取彩票：userno:{},orderid:{},linkUserno:{}", userno, orderid, linkUserno);
		Activity activity = Activity.findActivity(orderid, true);
		if (activity == null) {
			throw new WeixinException(ErrorCode.CASELOT_NOT_EXISTS);
		}
		Integer remainingShare = activity.getRemainingShare();
		logger.info("start  remainingShare{}", remainingShare);
		if (remainingShare <= 0) {
			throw new WeixinException(ErrorCode.ACTIVITY_REMAININGSHARE_NOT_ENOUGH);
		}
		activity.setRemainingShare(remainingShare - 1);
		logger.info("remainingShare{}", remainingShare);
		activity.merge();
		logger.info("activity{}", activity);
		CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(userno, orderid),
				true);
		if (caseLotUserinfo == null) {
			throw new WeixinException(ErrorCode.CASELOTUSERINFO_NOT_EXISTS);
		}
		int chances = caseLotUserinfo.getChances();
		if (chances <= 0) {
			throw new WeixinException(ErrorCode.CASELOTUSERINFO_CHANCES_NOT_ENOUGH);
		}
		caseLotUserinfo.setChances(chances - 1);
		caseLotUserinfo.setJoinTimes(caseLotUserinfo.getJoinTimes() + 1);
		caseLotUserinfo.merge();
		logger.info("caseLotUserinfo{}", caseLotUserinfo);
		ActivityDetail.createActivityDetail(userno, orderid, linkUserno);
		if (StringUtils.isNotEmpty(linkUserno)) {
			asyncService.addChanceDetail(linkUserno, userno, orderid);
		}
		return caseLotUserinfo;
	}

	public ChancesDetail createChanceDetail(String linkUserno, String joinUserno, String orderid) {
		ChancesDetail chancesDetail = ChancesDetail.findChancesDetail(new ChancesDetailPK(linkUserno, joinUserno,
				orderid));
		if (chancesDetail != null) {
			if (chancesDetail.getState() == 1) {
				throw new WeixinException(ErrorCode.CHANCEDETAIL_HAVE_ADD);
			}
			throw new WeixinException(ErrorCode.CHANCEDETAIL_HAVE_EXISTS);
		}
		findActivityByOrderid(orderid);
		return ChancesDetail.createChancesDetail(linkUserno, joinUserno, orderid);
	}

	public void wxuserinfo(String openid) {
		logger.info("获取用户信息参数:openid:" + openid);
		try {
			String accessToken = weixinService.getAccessToken();
			String weixinuser = weixinService.findUserinfoByOpenid(accessToken,openid);
			JSONObject userinfo = new JSONObject(weixinuser);
			String nickname = null;
			String headimgurl = null;
			if (userinfo.has("nickname")) {
				logger.info("获取到用户的基本信息：" + userinfo);
				nickname = userinfo.getString("nickname");
				headimgurl = userinfo.getString("headimgurl");
			}
			String useropenid = userinfo.getString("openid");
			if (StringUtils.isEmpty(useropenid)) {
				logger.error("获取用户openid失败");
				return;
			}
			String userno = lotteryService.findOrCreateBigUser(useropenid, nickname, Const.DEFAULT_BIGUSER_TYPE);
			this.findOrCreateCaseLotUserinfo(userno, "HM00001", nickname, headimgurl);
		} catch (Exception e) {
			logger.error("关注时同步执行 增加 HM00001的活动账户失败:", e.getMessage());
		}
	}
}
