package com.ruyicai.weixin.dto;

import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJson
@RooJavaBean
@RooToString
public class WeixinUserDTO {

	private int subscribe;

	private String openid;

	private String nickname;

	private int sex;

	private String city;

	private String province;

	private String language;

	private String headimgurl;

	private Date subscribe_time;

}
