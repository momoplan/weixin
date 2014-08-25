package com.ruyicai.weixin.service;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.StringUtil;

@Service
public class CommonService {
	
	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	LotserverService lotserverService;
	
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
		String result = lotserverService.DoubleDallBet( userNo,  amount,  channel,  bet_code);
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
		String result = lotserverService.doGetBatchInfo();
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
		String result = lotserverService.doGetBatchInfo();
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
	 * 根据购彩订单号获取订单详情
	 * 
	 * @param orderid
	 * @return
	 */
	public JSONObject getOrderInfo(String orderid)
	{
		JSONObject json = null;
		String result = lotserverService.doGetOrderInfo(orderid);
		if (!StringUtil.isEmpty(result))
		{
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject != null)
			{
				if ("0".equals(fromObject.get("error_code")))
				{
					json = JSONObject.fromObject(fromObject.get("result"));
				}
			}
		}
		return json;
	}
}
