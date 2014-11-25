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

/**
 *  通知第三方的jms
 * @author Administrator
 *
 */
@Service
public class NotifyThirdPartyListener {

	private Logger logger = Logger.getLogger(NotifyThirdPartyListener.class);
	
//	@Autowired
//	private AdvertiseUtil advertiseUtil;
	
	public void notify(@Header("imei") String imei, @Header("platform") String platform, @Header("mac") String mac) {
		try {
			logger.info("注册通知第三方的Jms start "+"imei="+imei+";platform="+platform+";mac="+mac);
			long startMillis = System.currentTimeMillis();
//			if (StringUtil.isEmpty(mac)) {
//				return;
//			}
//			//查用户表,防止通过其他渠道激活的用户通知第三方
//			List<UserInf> list = UserInf.getListByMacPlatform(mac, Platform.iPhone.value());
//			if (list!=null&&list.size()>0) { //有激活记录
//				UserInf userInf = list.get(0);
//				String source = AdvertiseSource.getSourceByCoopId(userInf.getChannel());
//				if (StringUtil.isEmpty(source)) { //通过其他渠道激活
//					return;
//				}
//				if (StringUtils.equals(source, AdvertiseSource.ruanlie.value())) { //按激活算的
//					return;
//				}
//				//通知第三方积分墙
//				AdvertiseInfo advertiseInfo = advertiseUtil.getValidAdvertiseInfo(mac, source);
//				advertiseUtil.notifyThirdParty(advertiseInfo);
//			} else { //由于客户端的原因,没有激活记录就注册的,也要通知第三方
//				AdvertiseInfo advertiseInfo = advertiseUtil.getValidAdvertiseInfo(mac);
//				if (advertiseInfo==null) {
//					return;
//				}
//				String source = advertiseInfo.getSource();
//				if (StringUtils.equals(source, AdvertiseSource.ruanlie.value())) { //按激活算的
//					return;
//				}
//				advertiseUtil.notifyThirdParty(advertiseInfo); //通知第三方
//			}
			long endMillis = System.currentTimeMillis();
			logger.info("注册通知第三方,用时:"+(endMillis-startMillis)+",mac="+mac);
		} catch (Exception e) {
			logger.error("通知第三方时发生异常imei="+imei+",mac="+mac, e);
		}
	}
	
}
