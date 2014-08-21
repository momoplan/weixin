package com.ruyicai.weixin.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.JsonMapper;
import 

@RequestMapping(value = "/packetactivity")
@Controller
public class PacketActivityController {

	private Logger logger = LoggerFactory.getLogger(PacketActivityController.class);
	
	@RequestMapping(value = "/getPuntsFromPacket", method = RequestMethod.GET)
	@ResponseBody
	public String getpuntsfrompacket(@RequestParam(value = "award_userno", required = false) String award_userno,
			@RequestParam(value = "packet_id", required = false) String packet_id,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("getPuntsFromPacket award_userno:{} packet_id：{} ", award_userno, packet_id);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(award_userno) || StringUtils.isEmpty(packet_id)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid or userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}
			
			
			
		
		} catch (WeixinException e) {
			logger.error("findChancesDetail error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("findChancesDetail error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}
	
}
