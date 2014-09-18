package com.ruyicai.weixin.service;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.StringUtil;

@Service
public class CommonService {
	
	private Logger logger = LoggerFactory.getLogger(CommonService.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	LotserverService lotserverService;
	
	/**
	 * 赠送彩金
	 * 
	 * @param userNo
	 * @param point
	 * @param channel
	 * @param memo
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
	 * 赠送彩金
	 * 
	 * @param userNo
	 * @param point
	 * @param channel
	 * @param memo
	 * @return
	 */
	public String presentDividendReturnJson(String userNo, String point, String channel, String memo) {
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
		
		return fromObject.toString();
	}
	
	/**
	 * 投注
	 * 
	 * @param userNo
	 * @param amount
	 * @param channel
	 * @param bet_code
	 * @param batchcode
	 * @return
	 */
	public JSONObject getDoubleDallBet(String userNo, String amount, String channel, String bet_code, String batchcode)
	{
		String result = lotserverService.DoubleDallBet( userNo,  amount,  channel,  bet_code, batchcode);
		if (StringUtil.isEmpty(result)) {
			return null;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return null;
		}
		return fromObject;
	}
	
	/**
	 * 查期号和开奖日期
	 * 
	 * @return
	 */
	public JSONObject getBatchInfo()
	{
		String result = lotserverService.doGetBatchInfo();
		if (StringUtil.isEmpty(result)) {
			return null;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return null;
		}
		
		return fromObject;
	}
	
	/**
	 * 根据期号查开奖日期
	 * 
	 * @return
	 */
	public String getBatchInfo(String batchCode)
	{
		String result = lotserverService.doGetBatchInfo(batchCode);
		if (StringUtil.isEmpty(result)) {
			return null;
		}
		 
		
		return result;
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
				if ("0000".equals(fromObject.get("error_code")))
				{
					json = JSONObject.fromObject(fromObject.get("result"));
				}
			}
		}
		return json;
	}
	
	/**
	 * 根据购彩订单号批量获取订单详情
	 * 
	 * @param orderids 逗号分隔
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, JSONObject> doGetOrdersInfo(String orderids)
	{
		Map<String, JSONObject> json = null;
		String result = lotteryService.getTorderByIds(orderids);
		if (!StringUtil.isEmpty(result))
		{
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject != null)
			{
				if ("0".equals(fromObject.get("errorCode")))
				{
					json = JSONObject.fromObject(fromObject.get("value"));
				}
			}
		}
		return json;
	}
	
	/**
	 * 获取上一期信息
	 * 
	 * @return
	 */
	public JSONObject getPreBatchInfo()
	{
		JSONObject json = null;
		try
		{
			String result = lotserverService.doGetPreBatchInfo();
			if (!StringUtil.isEmpty(result))
			{
				JSONObject fromObject = JSONObject.fromObject(result);
				if (fromObject != null)
				{
					if ("0000".equals(fromObject.get("error_code")))
					{
						JSONArray array = (JSONArray) fromObject.get("result");
						if (array != null && array.size() > 0)
						{
							json = JSONObject.fromObject(array.get(0));
						}
					}
				}
			}
		} catch (Exception e)
		{
			logger.error("获取上一期信息异常", e);
		}

		return json;
	}
	
}
