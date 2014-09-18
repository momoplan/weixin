package com.ruyicai.weixin.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.AppUserDao;
import com.ruyicai.weixin.domain.AppUser;

@Service
public class InitAppUserService {

	private Logger logger = LoggerFactory.getLogger(InitAppUserService.class);

	@Autowired
	private AppUserDao appUserDao;

	@Value("${weixinName}")
	private String weixinName;

	@Autowired
	private TranslateService translateService;

	@Autowired
	private WeixinService weixinService;
	
	@Autowired
	PacketActivityService packetActivityService;

	@PostConstruct
	public void init() {
		logger.info("初始化" + weixinName + "用户");
		AppUser appUser = appUserDao.findAppUser(weixinName);
		if (appUser == null) {
			logger.error("appUser is null");
		} else {
			logger.info(appUser.toString());
			translateService.setToken(appUser.getToken());
			weixinService.setAppId(appUser.getAppId());
			weixinService.setAppSecret(appUser.getAppSecret());
		}
		
//		packetActivityService = new PacketActivityService();
		//packetActivityService.timerReturnPunt();


	}
}
