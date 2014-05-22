package com.ruyicai.weixin.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public CaseLotUserinfo createCaseLotUserinfo(String userno, String orderid, String nickname, String headimgurl) {
		logger.info("createCaseLotUserinfo userno:{} orderid:{} nickname:{} headimgurl:{}", userno, orderid, nickname,
				headimgurl);
		if (StringUtils.isEmpty(userno)) {
			throw new IllegalArgumentException("the argument userno is require.");
		}
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(userno, orderid));
	    logger.info("查询是否有CaseLotUserinfo信息  caseLotUserinfo{}",caseLotUserinfo);
		if (caseLotUserinfo == null) {
			caseLotUserinfo = CaseLotUserinfo.createCaseLotUserinfo(userno, orderid, nickname, headimgurl);
		}
		return caseLotUserinfo;
	}
	@Transactional
	public Activity selectActivityDetail(String orderid) {
		logger.info("selectActivityDetail orderid:{}", orderid);
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		Activity activity = Activity.findActivity(orderid, true);
		if(activity == null){
			throw new WeixinException(ErrorCode.CASELOT_NOT_EXISTS);
		}
		return activity;
	}
	@Transactional
	public CaseLotUserinfo caseLotchances(String userno,String orderid) {
		logger.info("selectActivityDetail orderid:{},userno:{}", orderid,userno);
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		if (StringUtils.isEmpty(userno)) {
			throw new IllegalArgumentException("the argument userno is require.");
		}
		CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(userno, orderid),true);
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
		if(activity == null){
			throw new WeixinException(ErrorCode.CASELOT_NOT_EXISTS);
		}
		Integer remainingShare = activity.getRemainingShare();
		logger.info("start  remainingShare{}",remainingShare);
		if (remainingShare <= 0) {
			throw new WeixinException(ErrorCode.ACTIVITY_REMAININGSHARE_NOT_ENOUGH);
		}
		activity.setRemainingShare(remainingShare - 1);
		logger.info("remainingShare{}",remainingShare);
		activity.merge();
		logger.info("activity{}",activity);
		CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(userno, orderid),true);
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
		logger.info("caseLotUserinfo{}",caseLotUserinfo);
		ActivityDetail.createActivityDetail(userno, orderid);
		if (StringUtils.isNotEmpty(linkUserno)) {
			asyncService.addChanceDetail(linkUserno, userno, orderid);
		}
		return caseLotUserinfo;
	}

	public ChancesDetail createChanceDetail(String userno, String fromUserno, String orderid) {
		ChancesDetail chancesDetail = ChancesDetail.findChancesDetail(new ChancesDetailPK(userno, fromUserno, orderid));
		if (chancesDetail != null) {
			if (chancesDetail.getState() == 1) {
				throw new WeixinException(ErrorCode.CHANCEDETAIL_HAVE_ADD);
			}
			throw new WeixinException(ErrorCode.CHANCEDETAIL_HAVE_EXISTS);
		}
		return ChancesDetail.createChancesDetail(userno, fromUserno, orderid);
	}
}
