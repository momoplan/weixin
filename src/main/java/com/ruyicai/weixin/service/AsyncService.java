package com.ruyicai.weixin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.RequestMessageDetailDao;
import com.ruyicai.weixin.dao.SubscriberDao;
import com.ruyicai.weixin.dto.RequestMessage;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private RequestMessageDetailDao requestMessageDetailDao;

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

	@Async
	public void unsubscribe(String userno, String weixinno) {
		logger.info("取消订阅:userno:" + userno + ",weixinno:" + weixinno);
		try {
			subscriberDao.unsubscribe(userno, weixinno);
		} catch (Exception e) {
			logger.error("unsubscribe异常:userno:" + userno + ",weixinno:" + weixinno, e);
		}
	}
}
