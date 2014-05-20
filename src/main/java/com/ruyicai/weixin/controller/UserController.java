package com.ruyicai.weixin.controller;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.service.FileService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.service.WeixinService;


@RequestMapping(value = "/user")
@Controller
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private LotteryService lotteryService;
	@Autowired
	private WeixinService weixinService;
	@Autowired
	private FileService fileService;

	@RequestMapping(value = "/wininfo")
	public @ResponseBody
	ResponseData wininfo(@RequestParam(value = "lotno", required = false) String lotno, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("/static/wininfo lotno:{}", new Object[] { lotno });
		ResponseData rd = new ResponseData();
		try {
			Long startTime = System.currentTimeMillis();
//			wininfoImageService.downLoadWinInfo(lotno, request, response);
			logger.info("运行时间：" + (System.currentTimeMillis() - startTime));
			rd.setErrorCode("0");
		} catch (Exception e) {
			logger.error("/static/wininfo error", e);
			rd.setErrorCode("500");
			rd.setValue(e.getMessage());
		}
		return rd;
	}

	@RequestMapping(value = "/wxregister")
	public @ResponseBody
	 ResponseData wxregister(
			@RequestParam(value="client_id") String appid,
			@RequestParam(value="client_secret") String appsecret,
			@RequestParam(value="platform") String platform,
			@RequestParam(value="redirect_uri") String redirect_uri,
			@RequestParam(value="code",required=true) String code,
			@RequestParam(value="callBackMethod") String callback,
			HttpServletRequest request,HttpServletResponse response) {
		ResponseData rd = new ResponseData();
		try {
				logger.info("获取到参数：client_id=" + appid+"&client_secret="+appsecret+"&platform="+platform
						+"&code="+code+"&redirect_uri="+redirect_uri);
				weixinService.setAppId(appid);
				weixinService.setAppSecret(appsecret);
				String rejson = weixinService.toauth2(code);
				logger.info("获得了openid参数：" + rejson);
			    JSONObject js =	new JSONObject(rejson);
				String access_token = (String) js.get("access_token");
				String openid = (String) js.get("openid");
				JSONObject userinfo=new JSONObject(weixinService.getuserinfo(access_token, openid));
				logger.info("获取到的微信用户信息："+userinfo);
				String nickname = userinfo.getString("nickname");
				String headimgurl = userinfo.getString("headimgurl");
				String userno = lotteryService.dowxregister(nickname, nickname);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("nickname", nickname);
				jsonObject.put("headimgurl", headimgurl);
				jsonObject.put("userno", userno);
			    rd.setErrorCode("0000");
			    rd.setValue(jsonObject);
		} catch (Exception e) {
			logger.error("获取openid异常", e);
			 rd.setErrorCode("9999");
		}
		return rd;
	}
}
