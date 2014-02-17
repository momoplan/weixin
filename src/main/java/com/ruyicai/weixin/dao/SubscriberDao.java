package com.ruyicai.weixin.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.Subscriber;
import com.ruyicai.weixin.domain.SubscriberPK;

@Component
public class SubscriberDao {

	@PersistenceContext
	private EntityManager entityManager;

	public Subscriber findSubscriber(String userno, String weixinno) {
		if (StringUtils.isBlank(userno) || StringUtils.isBlank(weixinno)) {
			throw new IllegalArgumentException("The argument userno or weixinno is required");
		}
		SubscriberPK pk = new SubscriberPK(userno, weixinno);
		return entityManager.find(Subscriber.class, pk);
	}

	@Transactional
	public void subscribe(String userno, String weixinno) {
		Subscriber subscriber = this.findSubscriber(userno, weixinno);
		if (subscriber == null) {
			subscriber = new Subscriber();
			SubscriberPK pk = new SubscriberPK(userno, weixinno);
			subscriber.setId(pk);
			subscriber.setHasSubscribe(1);
			subscriber.setCreateTime(new Date());
			this.entityManager.persist(subscriber);
		} else {
			subscriber.setHasSubscribe(1);
			subscriber.setLastModifyTime(new Date());
			this.entityManager.merge(subscriber);
			this.entityManager.flush();
		}
	}

	@Transactional
	public void unsubscribe(String userno, String weixinno) {
		Subscriber subscriber = this.findSubscriber(userno, weixinno);
		if (subscriber != null) {
			subscriber.setHasSubscribe(0);
			subscriber.setLastModifyTime(new Date());
			this.entityManager.merge(subscriber);
			this.entityManager.flush();
		}
	}
}
