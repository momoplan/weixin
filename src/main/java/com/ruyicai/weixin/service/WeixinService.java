package com.ruyicai.weixin.service;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.menu.Menu;
import com.ruyicai.weixin.util.JsonMapper;

@Service
public class WeixinService {

	private Logger logger = LoggerFactory.getLogger(WeixinService.class);

	private static String AccessToken_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	private static String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	private static String SELECT_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

	private static String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

	/**
	 * 微信认证appId,在InitAppUserService初始化
	 */
	private String appId;

	/**
	 * 微信认证appSecret,在InitAppUserService初始化
	 */
	private String appSecret;

	@SuppressWarnings("unchecked")
	public String getAccessToken() {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
			throw new IllegalArgumentException("The argument appId or appSecret is required");
		}
		String url = AccessToken_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
		String token = null;
		try {
			String json = Request.Get(url).connectTimeout(2000).socketTimeout(1000).execute().returnContent()
					.asString();
			logger.info("Http Response:" + json);
			HashMap<String, Object> map = JsonMapper.fromJson(json, HashMap.class);
			if (map.containsKey("access_token")) {
				token = (String) map.get("access_token");
			}
		} catch (Exception e) {
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
		// 拼装创建菜单的url
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", accessToken);
		// 将菜单对象转换成json字符串
		String jsonMenu = JsonMapper.toJson(menu);
		// 调用接口创建菜单
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
		// 拼装创建菜单的url
		String url = SELECT_MENU_URL.replace("ACCESS_TOKEN", accessToken);
		// 调用接口创建菜单
		String json = null;
		try {
			json = Request.Get(url).execute().returnContent().asString();
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		return json;
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
		// 拼装创建菜单的url
		String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", accessToken);
		// 调用接口创建菜单
		String json = null;
		try {
			json = Request.Get(url).execute().returnContent().asString();
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		return json;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
}
