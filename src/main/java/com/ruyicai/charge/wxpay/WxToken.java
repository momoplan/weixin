package com.ruyicai.charge.wxpay;

import java.io.Serializable;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 存储微信token相关信息
 * <p>因获取access_token一天内有频次限制,需统一管理access_token，每次获取的access_token有效期为2个小时,
 *    超过微信频次限制将无法处理,一天最多获取200次，需要所有用户共享值.
 * </p>
 * 
 * @author hzf
 *
 */
@RooJavaBean
@RooToString
@RooJson
public class WxToken implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String accessToken; // 微信token值
	private Date accessDate;    // 访问token时间
	private long expireTime = 7200;  // token过期时间,默认为7200秒
}
