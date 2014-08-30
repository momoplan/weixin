package com.ruyicai.weixin.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.advert.util.HttpUtil;

@Service
public class LotserverService {

	private Logger logger = LoggerFactory.getLogger(LotserverService.class);
	
	@Value("${lotserverurl}")
	private String lotserverurl;
	
	/**
	 * 双色球投注
	 * 
	 * @param userNo
	 * @param amount
	 * @param channel
	 * @param bet_code
	 * @return
	 */
	public String DoubleDallBet(String userNo, String amount, String channel, String bet_code) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("amount", amount);
		map.put("batchcode", "");
		map.put("batchnum", "1");
		map.put("bet_code", bet_code);
		map.put("bettype", "bet");
		map.put("command", "betLot");
		map.put("isSellWays", "1");
		map.put("lotmulti", "1");
		map.put("lotno", "F47104");		
		map.put("oneAmount", amount);
		map.put("prizeend", "0");
		map.put("userno", userNo);
		JSONObject json = JSONObject.fromObject(map);
			
		String url = lotserverurl + "/SendRequestServlet?parameter="+json.toString()+"&callBackMethod=";
		String result = HttpUtil.sendRequestByPost(url, "", true);
		logger.info("双色球投注返回:"+result+",userNo:"+userNo+";amount:"+amount);
		result = result.replace("(", "").replace(")", "");
		return result;
	}
	
	/**
	 * 查期号和开奖日期
	 * 
	 * @param userNo
	 * @param amount
	 * @return
	 */
	public String doGetBatchInfo() {
		String url = lotserverurl + "/SendRequestServlet?parameter={\"command\":\"QueryLot\",\"type\":\"highFrequency\",\"lotno\":\"F47104\"}&callBackMethod=";
		String result = HttpUtil.sendRequestByPost(url, "", true);
		logger.info("查期号和开奖日期:"+result);
		result = result.replace("(", "").replace(")", "");
		return result;
	}
	
	/**
	 * 获取上一期信息
	 * 
	 * @return
	 */
	public String doGetPreBatchInfo() {
		String url = lotserverurl + "/SendRequestServlet?parameter={\"command\":\"QueryLot\",\"type\":\"winInfoList\",\"lotno\":\"F47104\",\"pageindex\":\"1\",\"maxresult\":\"1\"}&callBackMethod=";
		String result = HttpUtil.sendRequestByPost(url, "", true);
		result = result.replace("(", "").replace(")", "");
		return result;
	}
	
	/**
	 * 根据期号查询开奖信息
	 * 
	 * @param batchcode 期号
	 * @return
	 */
	public String doGetOpenInfo(String batchcode) {
		String url = lotserverurl + "/SendRequestServlet?parameter={\"command\":\"AllQuery\",\"type\":\"winInfoDetail\",\"lotno\":\"F47104\",\"batchcode\":\""+batchcode+"\"}&callBackMethod=";
		String result = HttpUtil.sendRequestByPost(url, "", true);
		logger.info("doGetOpenInfo:"+result);
		result = result.replace("(", "").replace(")", "");
		return result;
	}
	
	/**
	 * 根据购彩订单号查询订单详情
	 * 
	 * @param orderid
	 * @return
	 */
	public String doGetOrderInfo(String orderid)
	{
		String url = lotserverurl + "/SendRequestServlet?parameter={\"command\":\"select\",\"requestType\":\"orderDetails\",\"coopid\":\"1101\",\"platform\":\"public\",\"id\":\""+orderid+"\"}&callBackMethod=";
		String result = HttpUtil.sendRequestByPost(url, "", true);
		logger.info("doGetOrderInfo:"+result);
		result = result.replace("(", "").replace(")", "");
		return result;
	}
	
}
