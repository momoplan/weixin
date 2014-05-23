package com.ruyicai.weixin.service;

import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.dao.RequestMessageDetailDao;
import com.ruyicai.weixin.dao.SubscriberDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.CaseLotUserinfoPK;
import com.ruyicai.weixin.domain.ChancesDetail;
import com.ruyicai.weixin.domain.ChancesDetailPK;
import com.ruyicai.weixin.dto.RequestMessage;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private RequestMessageDetailDao requestMessageDetailDao;
//    @Autowired
//    private WeixinService weixinService;
//    @Autowired
//    private LotteryService lotteryService;
	@Autowired
	private SubscriberDao subscriberDao;

	@Async
	public void createRequestMessageDetail(RequestMessage requestMessage, String requestBody) {
		try {
			requestMessageDetailDao.createRequestMessageDetail(requestMessage, requestBody);
		} catch (Exception e) {
			logger.error("存储requestBody异常:" + requestBody, e);
		}
	}

	@Async
	public void subscribe(String userno, String weixinno) {
		logger.info("增加订阅:userno:" + userno + ",weixinno:" + weixinno);
		try {
			subscriberDao.subscribe(userno, weixinno);
		} catch (Exception e) {
			logger.error("subscribe异常:userno:" + userno + ",weixinno:" + weixinno, e);
		}
	}
//	@Async
//	public void wxuserinfo(String openid) {
//		logger.info("获取用户信息参数:openid:" + openid);
//		try {
//			weixinService.setAppId("wx6919f6fac2525c5f");
//			weixinService.setAppSecret("4888a5883fb856751d52629b4923d11d");
//			String weixinuser = weixinService.userinfoByAccess_token(openid);
//			CaseLotUserinfo caseLotUserinfo=  lotteryService.caseLotUserinfo(new JSONObject(weixinuser), "HM00001");
//		    logger.info("关注时同步执行 增加 HM00001的活动账户：caseLotUserinfo:{}",caseLotUserinfo);
//		} catch (Exception e) {
//			logger.error("关注时同步执行 增加 HM00001的活动账户失败:" , e.getMessage());
//		}
//	}

	@Async
	public void unsubscribe(String userno, String weixinno) {
		logger.info("取消订阅:userno:" + userno + ",weixinno:" + weixinno);
		try {
			subscriberDao.unsubscribe(userno, weixinno);
		} catch (Exception e) {
			logger.error("unsubscribe异常:userno:" + userno + ",weixinno:" + weixinno, e);
		}
	}

	@Async
	@Transactional
	public void addChanceDetail(String linkUserno, String joinUserno, String orderid) {
		logger.info("addChanceDetail linkUserno：{} joinUserno:{} orderid:{}", linkUserno, joinUserno, orderid);
		try {
			ChancesDetail chancesDetail = ChancesDetail.findChancesDetail(new ChancesDetailPK(linkUserno, joinUserno,
					orderid));
			if (chancesDetail != null) {
				if (chancesDetail.getState() == 0) {
					chancesDetail.setState(1);
					chancesDetail.setSuccessTime(new Date());
					chancesDetail.merge();
					CaseLotUserinfo caseLotUserinfo = CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(
							linkUserno, orderid), true);
					if (caseLotUserinfo != null) {
						int linkTimes = caseLotUserinfo.getLinkTimes() + 1;
						if (linkTimes % 3 == 0) {
							caseLotUserinfo.setChances(caseLotUserinfo.getChances() + 1);
							caseLotUserinfo.setLinkTimes(linkTimes);
							logger.info("增加用户抽奖机会 linkUserno:{} joinUserno:{} orderid:{}", linkUserno, joinUserno,
									orderid);
						} else {
							caseLotUserinfo.setLinkTimes(linkTimes);
							logger.info("增加用户链接次数 linkUserno:{} joinUserno:{} orderid:{}", linkUserno, joinUserno,
									orderid);
						}
						caseLotUserinfo.merge();
					}
				} else {
					logger.info("已增加过机会，不再增加");
				}
			} else {
				logger.info("未找到记录");
			}
		} catch (Exception e) {
			logger.error("增加用户领取次数异常", e);
		}
	}
}
