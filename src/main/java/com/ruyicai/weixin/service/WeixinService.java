package com.ruyicai.weixin.service;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.wxpay.WxToken;
import com.ruyicai.weixin.dto.WeixinUserDTO;
import com.ruyicai.weixin.dto.menu.Menu;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.DateUtil;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.MyFluentResponseHandler;
import com.ruyicai.weixin.util.StringUtil;

@Service
public class WeixinService {

	private Logger logger = LoggerFactory.getLogger(WeixinService.class);

	private static String AccessToken_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	private static String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	private static String SELECT_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

	private static String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

	private static String OPEN_oauth2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	private static String SELECT_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	private static String USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	private static String ACCESS_TOKEN_KEY = "accesstoken";

	private static PassiveExpiringMap<String, String> ACCESS_TOKEN_MAP = new PassiveExpiringMap<String, String>(3600000);

	/**
	 * 微信认证appId,在InitAppUserService初始化
	 */
	private String appId;

	/**
	 * 微信认证appSecret,在InitAppUserService初始化
	 */
	private String appSecret;
	
	@Autowired
	MemcachedService<WxToken> memcachedService;
	
	private static String CACHE_TOKEN_KEY = "wx_pub_access_token";

	public String getAccessToken() {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
			throw new IllegalArgumentException("The argument appId or appSecret is required");
		}
		return getAccessToken(CACHE_TOKEN_KEY);
	}
	
	public String getAccessToken(String key)
	{
		String token = null;
		WxToken wt = memcachedService.get(key);
		Date now = new Date();
		if (wt == null)
			token = getRealTimeToken(key, now);

		Date preDate = wt.getAccessDate();
		if ((DateUtil.getUnixTime(now) - DateUtil.getUnixTime(preDate)) > wt.getExpireTime())
			token = getRealTimeToken(key, now);

		if (StringUtil.isEmpty(token))
			token = wt.getAccessToken();
		
		logger.info("token = " + token);
		return token;
	}

	@SuppressWarnings("unchecked")
	public String getRealTimeToken(String key, Date now)
	{
		String token = null;
		String url = AccessToken_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
		try
		{
			String json = Request.Get(url).connectTimeout(2000).socketTimeout(1000).execute().returnContent()
					.asString();
			logger.info("实时获取token Response:" + json);
			HashMap<String, Object> map = JsonMapper.fromJson(json, HashMap.class);
			if (map.containsKey("access_token"))
			{
				token = (String) map.get("access_token");

				logger.info("缓存中添加 token:{}", token);
				String expires_in =  map.containsKey("expires_in") ? map.get("expires_in").toString() : "";
				WxToken wt = new WxToken();
				wt.setAccessDate(now);
				wt.setAccessToken(token);
				wt.setExpireTime(Long.valueOf(expires_in));
				memcachedService.set(key, wt);
			}
		}
		catch (Exception e)
		{
			logger.error("请求微信异常url=" + url, e);
		}
		return token;
	}
	
	/**
	 * 创建菜单
	 * 
	 * @param menu
	 *            菜单实例
	 * @param accessToken
	 *            有效的access_token
	 * @return 0表示成功，其他值表示失败
	 */
	public String createMenu(Menu menu, String accessToken) {
		logger.info("accessToken:{},menu:{}", accessToken, menu);
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", accessToken);
		String jsonMenu = JsonMapper.toJson(menu);
		String json = null;
		try {
			json = Request.Post(url).bodyString(jsonMenu, ContentType.APPLICATION_JSON).execute().returnContent()
					.asString();
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		return json;
	}

	/**
	 * 查询菜单
	 * 
	 * @param accessToken
	 *            有效的access_token
	 * @return
	 */
	public String selectMenu(String accessToken) {
		logger.info("accessToken:{}", accessToken);
		String url = SELECT_MENU_URL.replace("ACCESS_TOKEN", accessToken);
		String json = null;
		try {
			json = Request.Get(url).execute().handleResponse(new MyFluentResponseHandler());
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		return json;
	}

	/**
	 * 通过code换取网页授权access_token
	 * 
	 * @param code
	 * @return json
	 */
	public String getOauth(String code) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
			throw new IllegalArgumentException("The argument appId or appSecret is required");
		}
		String url = OPEN_oauth2_URL.replace("APPID", appId).replace("SECRET", appSecret).replace("CODE", code);
		String json = null;
		try {
			json = Request.Get(url).execute().returnContent().asString();
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		logger.info("getOauth result:{}", json);
		return json;
	}

	/**
	 * 获取网页授权用户信息
	 * 
	 * @param token
	 * @param openid
	 * @return
	 */
	public WeixinUserDTO getOauthWeixinUser(String token, String openid) {
		WeixinUserDTO dto = null;
		try {
			String url = SELECT_USERINFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
			String json = Request.Get(url).connectTimeout(2000).execute().handleResponse(new MyFluentResponseHandler());
			logger.info("Http 获取用户信息:" + json);
			if (json.contains("errcode")) {
				logger.error("获取用户信息失败 openid:{} error:{}", openid, json);
				throw new WeixinException(json);
			}
			dto = WeixinUserDTO.fromJsonToWeixinUserDTO(json);
		} catch (Exception e) {
			logger.error("findUserinfoByOpenid error", e);
		}
		return dto;
	}

	/**
	 * 删除菜单
	 * 
	 * @param accessToken
	 *            有效的access_token
	 * @return
	 */
	public String deleteMenu(String accessToken) {
		logger.info("accessToken:{}", accessToken);
		String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", accessToken);
		String json = null;
		try {
			json = Request.Get(url).execute().returnContent().asString();
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		return json;
	}

	/**
	 * 获取用户基本信息
	 * 
	 * @param accessToken
	 * @param openid
	 * @return
	 */
	public WeixinUserDTO findUserinfoByOpenid(String accessToken, String openid) {
		WeixinUserDTO dto = null;
		try {
			String userurl = USERINFO_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
			String json = Request.Get(userurl).connectTimeout(2000).execute()
					.handleResponse(new MyFluentResponseHandler());
			logger.info("findUserinfoByOpenid result json:{}" + json);
			if (json.contains("errcode")) {
				logger.error("获取用户信息失败 openid:{} error:{}", openid, json);
				throw new WeixinException(json);
			}
			dto = WeixinUserDTO.fromJsonToWeixinUserDTO(json);
		} catch (Exception e) {
			logger.error("findUserinfoByOpenid error", e);
		}
		return dto;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
}
