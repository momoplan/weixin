package com.ruyicai.weixin.service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.AppUserDao;
import com.ruyicai.weixin.dao.TogetherActivityDao;
import com.ruyicai.weixin.domain.ActivityDetail;
import com.ruyicai.weixin.domain.AppUser;
import com.ruyicai.weixin.domain.TogetorActivity;
import com.ruyicai.weixin.util.JsonMapper;

@Service
public class ActivityService {

	private Logger logger = LoggerFactory.getLogger(ActivityService.class);
    
	
	@Autowired
	private TogetherActivityDao togetherActivityDao;

	@Value("${weixinName}")
	private String weixinName;
	@Autowired
	private WeixinService weixinService;
	
	
	
	public ActivityDetail getActivityDetail(String orderid) {
		if (StringUtils.isBlank(orderid)) {
			throw new IllegalArgumentException("The argument orderid or appSecret is required");
		}
			ActivityDetail activityDetail = new ActivityDetail();
		try {
	     	activityDetail =	togetherActivityDao.findActivity(orderid);  
		} catch (Exception e) {
			logger.error("活动详情查询异常", e);
		}
		return activityDetail;
	}
	/**
	 * 添加活动信息
	 * @param userno
	 * @param username
	 * @param fromuserno
	 * @param receiveport
	 */
	public TogetorActivity createTogethers(String userno,String username,String fromuserno,int receiveport){
		return togetherActivityDao.createTogether(userno, username, fromuserno, receiveport);
	}
	/**
	 * 根据userno 获取合买信息
	 * @param userno
	 * @return
	 */
	public TogetorActivity   getTogetorActivityByuserno(String userno){
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The argument userno or appSecret is required");
		}
		return  togetherActivityDao.getTogetorActivityByUserno(userno);
	} 
	
}
