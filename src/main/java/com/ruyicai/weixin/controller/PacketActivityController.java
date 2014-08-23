package com.ruyicai.weixin.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.StringUtil;

@RequestMapping(value = "/packetactivity")
@Controller
public class PacketActivityController {

	private Logger logger = LoggerFactory
			.getLogger(PacketActivityController.class);

	@Autowired
	PacketActivityService packetActivityService;

	@RequestMapping(value = "/createPacket", method = RequestMethod.GET)
	@ResponseBody
	public String createPacket(
			@RequestParam(value = "packet_userno", required = true) String packet_userno,
			@RequestParam(value = "parts", required = true) String parts,
			@RequestParam(value = "punts", required = true) String punts,
			@RequestParam(value = "greetings", required = true) String greetings,
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		logger.info(
				"微信公众帐号红包活动创建红包：packet_userno:{},parts:{},punts:{},greetings:{}",
				packet_userno, parts, punts, greetings);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(packet_userno) || StringUtil.isEmpty(parts)
				|| StringUtil.isEmpty(punts)) {
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}

		int puntsInt = Integer.valueOf(punts);
		if (puntsInt > 1000) {
			rd.setErrorCode("10002");
			rd.setValue("创建红包注数不能大于1000注");
			return JsonMapper.toJsonP(callback, rd);
		} else if (puntsInt < 1)
		{
			rd.setErrorCode("10003");
			rd.setValue("创建红包注数不能小于1注");
			return JsonMapper.toJsonP(callback, rd);
		}

		int partsInt = Integer.valueOf(parts);
		if (partsInt > puntsInt)
		{
			rd.setErrorCode("10004");
			rd.setValue("红包份数不能大于红包注数");
			return JsonMapper.toJsonP(callback, rd);
		} else if (partsInt < 1)
		{
			rd.setErrorCode("10005");
			rd.setValue("红包份数不能小于1份");
			return JsonMapper.toJsonP(callback, rd);
		}

		try {
			Packet packet = packetActivityService.doCreatePacket(packet_userno,
					partsInt, puntsInt, greetings);
			Map<String, String> json = new HashMap<String, String>();
			json.put("userno", packet.getPacketUserno());
			json.put("packet_id", String.valueOf(packet.getId()));
			json.put("punts", String.valueOf(packet.getTotalPunts()));
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(json);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("createPacket error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPuntsFromPacket", method = RequestMethod.GET)
	@ResponseBody
	public String getpuntsfrompacket(
			@RequestParam(value = "award_userno", required = false) String award_userno,
			@RequestParam(value = "channel", required = false) String channel,
			@RequestParam(value = "packet_id", required = false) String packet_id,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("getPuntsFromPacket award_userno:{} packet_id：{} ",
				award_userno, packet_id);

		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(award_userno)
					|| StringUtils.isEmpty(packet_id)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid or userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}

			Map<String, String> map = packetActivityService.doGetPacketStus(
					award_userno, packet_id);
			String status = map.get("status");
			if (status.equals("0")) {
				Map<String,Object> imap = packetActivityService.getPunts(
						award_userno, channel, packet_id);

				rd.setErrorCode(ErrorCode.OK.value);
				rd.setValue(imap);
			}
			else
			{
				rd.setErrorCode(ErrorCode.GET_PUNT_FAIL.value);//未抢到
				rd.setValue(map);
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPacketStatus", method = RequestMethod.GET)
	@ResponseBody
	public String getPacketStatus(
			@RequestParam(value = "award_userno", required = false) String award_userno,
			@RequestParam(value = "packet_id", required = false) String packet_id,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("getPuntsFromPacket award_userno:{} packet_id：{} ",
				award_userno, packet_id);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(award_userno)
					|| StringUtils.isEmpty(packet_id)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid or userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}

			Map<String, String> map = packetActivityService.doGetPacketStus(
					award_userno, packet_id);
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(map);

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
	
	@RequestMapping(value = "/getPacketList", method = RequestMethod.GET)
	@ResponseBody
	public String getPacketList(@RequestParam(value = "packet_userno", required = true) String packet_userno,
			@RequestParam(value = "callBackMethod", required = true) String callback)
	{
		logger.info("getPacketList packet_userno:{}", packet_userno);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(packet_userno))
		{
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}
		
		try
		{
			rd.setValue(packetActivityService.doGetPacketList(packet_userno));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e)
		{
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e)
		{
			logger.error("getPacketList error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}
	
	@RequestMapping(value = "/getPacketInfo", method = RequestMethod.GET)
	@ResponseBody
	public String getPacketInfo(@RequestParam(value = "userno", required = true) String userno,
			@RequestParam(value = "packet_id", required = true) String packet_id,
			@RequestParam(value = "callBackMethod", required = true) String callback)
	{
		logger.info("getPacketInfo userno:{}, packet_id:{}", userno, packet_id);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(userno) || StringUtil.isEmpty(packet_id))
		{
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}
		
		try
		{
			rd.setValue(packetActivityService.doGetPacketInfo(userno, packet_id));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e)
		{
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e)
		{
			logger.error("getPacketInfo error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}
	
	@RequestMapping(value = "/thankTa", method = RequestMethod.GET)
	@ResponseBody
	public String thankTa(@RequestParam(value = "award_userno", required = true) String award_userno,
			@RequestParam(value = "thank_words", required = true) String thank_words,
			@RequestParam(value = "packet_id", required = true) String packet_id,
			@RequestParam(value = "callBackMethod", required = true) String callback)
	{
		logger.info("thankTa award_userno:{}, thank_words:{}, packet_id:{}", award_userno, award_userno, packet_id);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(award_userno) || StringUtil.isEmpty(thank_words) || StringUtil.isEmpty(packet_id))
		{
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}
		
		try
		{
			packetActivityService.doThankTa(award_userno, thank_words, packet_id);
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(ErrorCode.OK.memo);
		} catch (WeixinException e)
		{
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e)
		{
			logger.error("thankTa error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}
	
	@RequestMapping(value = "/getMyPunts", method = RequestMethod.GET)
	@ResponseBody
	public String getMyPunts(@RequestParam(value = "award_userno", required = true) String award_userno,
			@RequestParam(value = "callBackMethod", required = true) String callback)
	{
		logger.info("getMyPunts award_userno:{}", award_userno);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(award_userno))
		{
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}
		
		try
		{
			rd.setValue(packetActivityService.doGetMyPunts(award_userno));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e)
		{
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e)
		{
			logger.error("getMyPunts error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}
}
