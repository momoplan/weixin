package com.ruyicai.advert.jms.listener;

import java.util.List;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.ruyicai.advert.consts.AdvertiseSource;
//import com.ruyicai.advert.consts.Platform;
//import com.ruyicai.advert.domain.AdvertiseInfo;
//import com.ruyicai.advert.domain.UserInf;
//import com.ruyicai.advert.util.AdvertiseUtil;
//import com.ruyicai.advert.util.StringUtil;

@Service
public class IphoneScoreWallActiveListener {

	private Logger logger = Logger.getLogger(IphoneScoreWallActiveListener.class);
	
//	@Autowired
//	private AdvertiseUtil advertiseUtil;
	
	public void process(@Header("mac") String mac) {
		try {
//			logger.info("激活通知第三方的Jms start "+"mac="+mac);
//			long startMillis = System.currentTimeMillis();
//			if (StringUtil.isEmpty(mac)) {
//				return ;
//			}
//			//查用户表,防止通过其他渠道激活的用户通知第三方
//			List<UserInf> list = UserInf.getListByMacPlatform(mac, Platform.iPhone.value());
//			if (list!=null&&list.size()>0) { //有激活记录
//				UserInf userInf = list.get(0);
//				String source = AdvertiseSource.getSourceByCoopId(userInf.getChannel());
//				if (StringUtil.isEmpty(source)) { //通过其他渠道激活
//					return;
//				}
//				if (!StringUtils.equals(source, AdvertiseSource.ruanlie.value())) {
//					return;
//				}
//				//通知第三方积分墙
//				AdvertiseInfo advertiseInfo = advertiseUtil.getValidAdvertiseInfo(mac, source);
//				advertiseUtil.notifyThirdParty(advertiseInfo);
//			}
//			long endMillis = System.currentTimeMillis();
//			logger.info("激活通知第三方,用时:"+(endMillis-startMillis)+",mac="+mac);
		} catch (Exception e) {
			logger.error("激活通知第三方时发生异常,mac="+mac, e);
		}
	}
	
}
