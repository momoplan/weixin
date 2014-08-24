package com.ruyicai.weixin.service;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;

@Service
public class CommonService {
	
	@Autowired
	private LotteryService lotteryService;
	
	/**
	 * 赠送彩金
	 * @param userNo
	 * @param point
	 * @return
	 */
	public String presentDividend(String userNo, String point, String channel, String memo) {
		String result = lotteryService.presentDividend(userNo, point, channel, memo);
		if (StringUtils.isBlank(result)) {
			throw new WeixinException (ErrorCode.DIRECT_CHARGE_FAIL);
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject == null) {
			throw new WeixinException (ErrorCode.DIRECT_CHARGE_FAIL);
		}
		String errorCode = fromObject.getString("errorCode");
		if (!"0".equals(errorCode))
			throw new WeixinException (ErrorCode.DIRECT_CHARGE_FAIL);
		
		return fromObject.getString("errorCode");
	}
	
	/**
	 * 投注
	 * @param userNo
	 * @param point
	 * @return
	 */
	public String getDoubleDallBet(String userNo, String amount, String channel, String bet_code)
	{
		String result = lotteryService.DoubleDallBet( userNo,  amount,  channel,  bet_code);
		if (StringUtils.isBlank(result)) {
			return "";
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return "";
		}
		//return fromObject.getString("error_code");
		return fromObject.toString();
	}
	
	/**
	 * 投注
	 * @param userNo
	 * @param point
	 * @return
	 */
	public String getBatchInfo()
	{
		String result = lotteryService.doGetBatchInfo();
		if (StringUtils.isBlank(result)) {
			return "";
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return "";
		}
		//return fromObject.getString("error_code");
		return fromObject.toString();
	}
	
	/**
	 * 投注
	 * @param userNo
	 * @param point
	 * @return
	 */
	public String getOpenInfo(String batchcode)
	{
		String result = lotteryService.doGetBatchInfo();
		if (StringUtils.isBlank(result)) {
			return "";
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return "";
		}
		//return fromObject.getString("error_code");
		return fromObject.toString();
	}

}
