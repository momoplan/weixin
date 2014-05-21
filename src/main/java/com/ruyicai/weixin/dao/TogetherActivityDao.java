package com.ruyicai.weixin.dao;


import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ruyicai.weixin.domain.ActivityDetail;
import com.ruyicai.weixin.domain.TogetorActivity;

@Component
public class TogetherActivityDao {

	@PersistenceContext
	private EntityManager entityManager;
	public ActivityDetail findActivity(String orderid) {
		if (StringUtils.isBlank(orderid)) {
			throw new IllegalArgumentException("The argument orderid  is required");
		}
		ActivityDetail appUser = this.entityManager.find(ActivityDetail.class,orderid);
		return appUser;
	}
	public TogetorActivity getTogetorActivityByUserno(String userno) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The argument userno  is required");
		}
		TogetorActivity togetorActivity = this.entityManager.find(TogetorActivity.class,userno);
		return togetorActivity;
	}
	
	public TogetorActivity createTogether(String userno,String username,String fromuserno,int receiveport){
		if (StringUtils.isBlank(userno)||StringUtils.isBlank(username)||StringUtils.isBlank(fromuserno)) {
			throw new IllegalArgumentException("The argument userno username fromuserno receiveport or appSecret is required");
		}
		TogetorActivity activity = new TogetorActivity();
		activity.setUserno(userno);
		activity.setFormuserno(fromuserno);
		activity.setUsername(username);
		activity.setOrderid("0000001");
		activity.setReceiveport(receiveport);
		activity.setMemo("领取了一注");
		activity.setState(1);
		activity.setReceivetime(new Date());
		this.entityManager.persist(activity);
		return activity;
	}
}
