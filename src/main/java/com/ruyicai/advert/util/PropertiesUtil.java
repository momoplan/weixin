package com.ruyicai.advert.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertiesUtil {

	//力美
	@Value("${limei.ip}")
	private String limei_ip;
	public String getLimei_ip() {
		return limei_ip;
	}
	
	@Value("${limei.notifyUrl}")
	private String limei_notifyUrl;
	public String getLimei_notifyUrl() {
		return limei_notifyUrl;
	}
	
	@Value("${yyb.debug}")
	private String yyb_debug;
	public String getYybDebug() {
		return yyb_debug;
	}
	
	@Value("${limei.android.aduid}")
	private String limeiAndroidAduid;
	public String getLimeiAndroidAduid() {
		return limeiAndroidAduid;
	}

	//点乐
	@Value("${dianjoy.salt}")
	private String dianjoy_salt;
	public String getDianjoy_salt() {
		return dianjoy_salt;
	}

	@Value("${dianjoy.notifyUrl}")
	private String dianjoy_notifyUrl;
	public String getDianjoy_notifyUrl() {
		return dianjoy_notifyUrl;
	}
	
	//点入
	@Value("${dianru.ip}")
	private String dianru_ip;
	public String getDianru_ip() {
		return dianru_ip;
	}
	
	@Value("${dianru.notifyUrl}")
	private String dianru_notifyUrl;
	public String getDianru_notifyUrl() {
		return dianru_notifyUrl;
	}
	
	//多盟
	@Value("${domob.ip}")
	private String domobIp;
	public String getDomobIp() {
		return domobIp;
	}
	
	@Value("${domob.notifyUrl}")
	private String domob_notifyUrl;
	public String getDomob_notifyUrl() {
		return domob_notifyUrl;
	}
	
	//米迪
	@Value("${miidi.notifyUrl}")
	private String miidi_notifyUrl;
	public String getMiidi_notifyUrl() {
		return miidi_notifyUrl;
	}
	
	//软猎
	@Value("${ruanlie.ip}")
	private String ruanlieIp;
	public String getRuanlieIp() {
		return ruanlieIp;
	}
	
	@Value("${ruanlie.notifyUrl}")
	private String ruanlie_notifyUrl;
	public String getRuanlie_notifyUrl() {
		return ruanlie_notifyUrl;
	}
	
	//磨盘
	@Value("${mopan.ip}")
	private String mopanIp;
	public String getMopanIp() {
		return mopanIp;
	}
	
	@Value("${mopan.notifyUrl}")
	private String mopan_notifyUrl;
	public String getMopan_notifyUrl() {
		return mopan_notifyUrl;
	}
	
	//网域
	@Value("${wangyu.ip}")
	private String wangyuIp;
	public String getWangyuIp() {
		return wangyuIp;
	}
	
	@Value("${wangyu.notifyUrl}")
	private String wangyu_notifyUrl;
	public String getWangyu_notifyUrl() {
		return wangyu_notifyUrl;
	}
	
	//易积分
	@Value("${yijifen.ip}")
	private String yijifenIp;
	public String getYijifenIp() {
		return yijifenIp;
	}
	
	@Value("${yijifen.notifyUrl}")
	private String yijifen_notifyUrl;
	public String getYijifen_notifyUrl() {
		return yijifen_notifyUrl;
	}
	
	@Value("${lotteryUrl}")
	private String lotteryUrl;
	public String getLotteryUrl() {
		return lotteryUrl;
	}
	
}
