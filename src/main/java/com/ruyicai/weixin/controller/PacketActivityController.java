package com.ruyicai.weixin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.StringUtil;

@RequestMapping(value = "/packetactivity")
@Controller
public class PacketActivityController {

	private Logger logger = LoggerFactory.getLogger(PacketActivityController.class);
	
	@Autowired
	PacketActivityService packetActivityService;
	
	@RequestMapping(value = "/createPacket", method = RequestMethod.GET)
	@ResponseBody
	public String createPacket(@RequestParam(value = "packet_userno", required = true) String packet_userno,
			@RequestParam(value = "persons", required = true) String persons,
			@RequestParam(value = "punts", required = true) String punts,
			@RequestParam(value = "greetings", required = true) String greetings,
			@RequestParam(value = "callBackMethod", required = false) String callback)
	{
		logger.info("红包活动创建红包：packet_userno:{},persons:{},punts:{},punts{},greetings{}", 
				packet_userno, persons, punts, punts, greetings);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(packet_userno) || StringUtil.isEmpty(persons) || StringUtil.isEmpty(punts))
		{
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
		}
		
		int puntsInt = Integer.valueOf(punts);
		if (puntsInt > 1000)
		{
			rd.setErrorCode("10002");
			rd.setValue("创建红包注数不能大于1000注");
		}
		
		int personsInt = Integer.valueOf(persons);
		if (personsInt > puntsInt)
		{
			rd.setErrorCode("10003");
			rd.setValue("红包份数不能大于红包注数");
		}
		
		packetActivityService.doCreatePacket(packet_userno, personsInt, puntsInt, greetings);
		return JsonMapper.toJsonP(callback, rd);
	}
}
