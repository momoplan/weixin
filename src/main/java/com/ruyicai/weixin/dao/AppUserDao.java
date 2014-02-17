package com.ruyicai.weixin.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ruyicai.weixin.domain.AppUser;

@Component
public class AppUserDao {

	@PersistenceContext
	private EntityManager entityManager;

	public AppUser createAppUser(String weixinName, String appId, String appSecret, String token, String memo) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
			throw new IllegalArgumentException("The argument appId or appSecret is required");
		}
		AppUser appUser = new AppUser();
		appUser.setWeixinName(weixinName);
		appUser.setAppId(appId);
		appUser.setAppSecret(appSecret);
		appUser.setToken(token);
		appUser.setMemo(memo);
		appUser.setCreateTime(new Date());
		this.entityManager.persist(appUser);
		return appUser;
	}

	public void delAppUser(String weixinName) {
		if (StringUtils.isBlank(weixinName)) {
			throw new IllegalArgumentException("The argument appId  is required");
		}
		AppUser appUser = this.findAppUser(weixinName);
		if (appUser != null) {
			this.entityManager.remove(appUser);
		}
	}

	public AppUser findAppUser(String weixinName) {
		if (StringUtils.isBlank(weixinName)) {
			throw new IllegalArgumentException("The argument appId  is required");
		}
		AppUser appUser = this.entityManager.find(AppUser.class, weixinName);
		return appUser;
	}
}
