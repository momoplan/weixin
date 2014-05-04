package com.ruyicai.weixin.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.service.FileService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.service.WeixinService;
import com.ruyicai.weixin.service.WinInfoImageService;

@RequestMapping(value = "/static")
@Controller
public class StaticController {
	private Logger logger = LoggerFactory.getLogger(StaticController.class);

	@Autowired
	private WinInfoImageService wininfoImageService;
	@Autowired
	private WeixinService weixinService;
	@Autowired
	private LotteryService lotteryService;

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
			wininfoImageService.downLoadWinInfo(lotno, request, response);
			logger.info("运行时间：" + (System.currentTimeMillis() - startTime));
			rd.setErrorCode("0");
		} catch (Exception e) {
			logger.error("/static/wininfo error", e);
			rd.setErrorCode("500");
			rd.setValue(e.getMessage());
		}
		return rd;
	}

	@RequestMapping(value = "/image")
	public void image(@RequestParam(value = "fileName", required = false) String fileName,
			@RequestParam(value = "width", required = false) Integer width,
			@RequestParam(value = "height", required = false) Integer height, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("/static/image fileName:{},width:{},height:", fileName, width, height);
		try {
			fileService.getImage(fileName, width, height, request, response);
		} catch (Exception e) {
			logger.error("下载图片出错", e);
		}
	}
	
	
	/**
	 * 
	 * @param code
	 * @param state
	 * @param request
	 * @param response
	 * 
	 * {"access_token":"OezXcEiiBSKSxW0eoylIeJpeWyhE033MrDpXPnlmpWUbsBSGiN
	 * IzaE8m1jn5eViLk_6XnOyMhuu8NX5MfiR6Xcln1Cnf336mFG9MtTvdwQgowSwzvy
	 * leTsWafgoQzLXA6bnT480WwL8PvYIr6vUaBQ","expires_in":7200,
	 * "refresh_token":"OezXcEiiBSKSxW0eoylIeJpeWyhE033MrDpXPnlmpWUbsBSGiNIza
	 * E8m1jn5eViLtcp0snZTC9l_g3v2yWnpvLys3zX5LYzVimBf2CbOfeQwQwMUwVHLmWo36
	 * kpx1XM7yGStfUTO8rw4Dfk3ta75Uw",
	 * "openid":"oFYzzjiOOgWzTi7NQhmpwNwCB5g8","scope":"snsapi_base"}
	 */
	@RequestMapping(value = "/bind")
	public void bind(
			@RequestParam(value="code", required = false) String code,
			@RequestParam(value="state",defaultValue="1", required = false) String state,
			HttpServletRequest request,HttpServletResponse response) {
		try {
			logger.info("获取到code参数：" + code);
			weixinService.setAppId("wx6919f6fac2525c5f");
			weixinService.setAppSecret("4888a5883fb856751d52629b4923d11d");
			String rejson = weixinService.toauth2(code);
			logger.info("bind 获得了openid参数：" + rejson);
		    JSONObject js =	new JSONObject(rejson);
		    String access_token = (String) js.get("access_token");
		    String openid = (String) js.get("openid");
//		    String  s= lotteryService.selectUserinfoByOpenid(openid);
		    JSONObject userinfo=new JSONObject(weixinService.getuserinfo(access_token, openid));
		    logger.info("获取到的微信用户信息："+userinfo);
		    response.sendRedirect("http://iphone.ruyicai.com/html/tog.html?openid="+openid);
		} catch (Exception e) {
			logger.error("获取openid异常", e);
		}
		
	}
	@RequestMapping(value = "/getopenid")
	public void getopenid(
			@RequestParam(value="code") String code,
			@RequestParam(value="callBackMethod") String callback,
			HttpServletRequest request,HttpServletResponse response) {
		try {
			logger.info("获取到code参数：" + code);
			weixinService.setAppId("wx6919f6fac2525c5f");
			weixinService.setAppSecret("4888a5883fb856751d52629b4923d11d");
			String rejson = weixinService.toauth2(code);
			logger.info("bind 获得了openid参数：" + rejson);
			    JSONObject js =	new JSONObject(rejson);
				String access_token = (String) js.get("access_token");
				String openid = (String) js.get("openid");
				JSONObject userinfo=new JSONObject(weixinService.getuserinfo(access_token, openid));
				logger.info("获取到的微信用户信息："+userinfo);
				response.setContentType("text/javascript");
				PrintWriter out = response.getWriter();
				out.println(callback + "(" + userinfo + ")");
		} catch (Exception e) {
			logger.error("获取openid异常", e);
		}
	}
}
