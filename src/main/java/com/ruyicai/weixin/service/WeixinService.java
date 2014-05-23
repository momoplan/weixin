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
	private static String OPEN_oauth2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	private static String SELECT_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    public String USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
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
	 * 获取授权
	 * 
	 * @param accessToken
	 *            有效的access_token
	 *            
	 *            https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
	 * @return
	 */
	public String toauth2(String code) {
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
			throw new IllegalArgumentException("The argument appId or appSecret is required");
		}
		// 拼装创建菜单的url
		String url = OPEN_oauth2_URL.replace("APPID", appId).replace("SECRET", appSecret).replace("CODE", code);
		// 调用接口创建菜单
		logger.info("toauth2请求连接"+url);
		String json = null;
		try {
			json = Request.Get(url).execute().returnContent().asString();
		} catch (Exception e) {
			logger.error("请求微信异常url=" + url, e);
		}
		return json;
	}
	/**
	 * 获取用户信息
	 * 
	 * @param accessToken
	 *            有效的access_token
	 *            
	 * @return
	 */
	public String getuserinfo(String token,String openid) {
		// 拼装创建菜单的url
		String url = SELECT_USERINFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
		// 调用接口创建菜单
		logger.info("用户详细信息请求连接"+url);
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
	
	/**
	 * 通过全局的access_token获取用户信息
	 * @param openid
	 * @return
	 */
    public String userinfoByAccess_token(String openid){
    	String url = AccessToken_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
		String token = null;
		String json,userjson ="";
		try {
			json = Request.Post(url).connectTimeout(2000).socketTimeout(1000).execute().returnContent()
						.asString();
			logger.info("Http 获取用户access_token:" + json);
			HashMap<String, Object> map = JsonMapper.fromJson(json, HashMap.class);
			if (map.containsKey("access_token")) {
				token = (String) map.get("access_token");
			}
			String userurl = USERINFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openid);
			userjson = Request.Post(userurl).connectTimeout(2000).socketTimeout(1000).execute().returnContent()
					.asString();
			logger.info("Http 获取用户信息:" + userjson);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return userjson;
    }
	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
}
