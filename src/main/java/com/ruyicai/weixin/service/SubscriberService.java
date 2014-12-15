package com.ruyicai.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.SubscriberDao;
import com.ruyicai.weixin.domain.Subscriber;

@Service
public class SubscriberService {

	@Autowired
	SubscriberDao subscriberDao;
	
	public Subscriber findSubscriber(String userno, String weixinno)
	{
		return subscriberDao.findSubscriber(userno, weixinno);
	}
}
