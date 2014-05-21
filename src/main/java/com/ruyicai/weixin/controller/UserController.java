package com.ruyicai.weixin.controller;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.domain.ActivityDetail;
import com.ruyicai.weixin.domain.TogetorActivity;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.service.ActivityService;
import com.ruyicai.weixin.service.FileService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.service.WeixinService;
import com.ruyicai.weixin.util.JsonMapper;


@RequestMapping(value = "/user")
@Controller
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private LotteryService lotteryService;
	@Autowired
	private WeixinService weixinService;
	@Autowired
	private ActivityService activityService;
	@RequestMapping(value = "/wxregister")
	public @ResponseBody
	      String wxregister(
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
		return JsonMapper.toJsonP(callback, rd);
	}
	
	@RequestMapping(value = "/activitydetail")
	public @ResponseBody
	 String activitydetail(
			 @RequestParam(value="orderid") String orderid,
			 @RequestParam(value="callBackMethod") String callback,
			  HttpServletRequest request,HttpServletResponse response
				){
		logger.info("/user/activitydetail orderid:{}", new Object[] { orderid });
		ResponseData rd = new ResponseData();
		try {
			ActivityDetail activityDetail= activityService.getActivityDetail(orderid);
			rd.setValue(activityDetail);
			rd.setErrorCode("0");
		} catch (Exception e) {
			rd.setErrorCode("9999");
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}
	
	@RequestMapping(value = "/receive")
	public @ResponseBody
	 ResponseData receive(
			 @RequestParam(value="userno") String userno,
			 @RequestParam(value="fromuserno") String fromuserno,
			 @RequestParam(value="username") String username,
			 @RequestParam(value="callBackMethod") String callback,
			 HttpServletRequest request,HttpServletResponse response){
		logger.info("/user/receivec 参数:{}", new Object[] { userno,});
		ResponseData rd = new ResponseData();
		try {//根据userno查询领取彩票信息，获取此人的领取数
			if(StringUtils.isBlank(fromuserno)){
				fromuserno = userno;
			}
			TogetorActivity activitybean =	activityService.getTogetorActivityByuserno(userno);
			if(activitybean != null){
				rd.setErrorCode("10001");
				rd.setValue("已经领取过了");
			}else{
				//执行插入信息
				TogetorActivity activity =activityService.createTogethers(userno, username, fromuserno, 1);
				rd.setErrorCode("0000");
				rd.setValue(activity);
			}
		} catch (Exception e) {
			logger.error("领取彩票异常", e);
			 rd.setErrorCode("9999");
		}
		return rd;
	
	}
}
