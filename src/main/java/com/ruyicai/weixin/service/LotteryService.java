package com.ruyicai.weixin.service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.advert.consts.Constants;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.JsonMapper;

@Service
public class LotteryService {

	private Logger logger = LoggerFactory.getLogger(LotteryService.class);

	private static Map<String, String> map = new HashMap<String, String>();

	static {
		map.put("F47104", "双色球");
		map.put("F47103", "3D");
		map.put("F47102", "七乐彩");
		map.put("F47107", "内蒙快三");
		map.put("F47108", "吉林快三");
		map.put("T01001", "超级大乐透");
		map.put("T01002", "排列三");
		map.put("T01011", "排列五");
		map.put("T01007", "时时彩");
		map.put("T01008", "单场");
		map.put("T01009", "七星彩");
		map.put("T01010", "多乐彩");
		map.put("T01003", "胜负彩");
		map.put("T01004", "任选九");
		map.put("T01005", "进球彩");
		map.put("T01006", "半全场");
		map.put("T01012", "十一运夺金");
		map.put("T01013", "22选五");
		map.put("T01014", "广东十一选五");
		map.put("T01015", "广东快乐十分");
		map.put("T01016", "重庆11选5");
		map.put("J00001", "竞彩足球胜平负");
		map.put("J00002", "竞彩足球比分");
		map.put("J00003", "竞彩足球总进球");
		map.put("J00004", "竞彩足球半场胜负平");
		map.put("J00005", "竞彩篮球胜负");
		map.put("J00006", "竞彩篮球让分胜负");
		map.put("J00007", "竞彩篮球胜负差");
		map.put("J00008", "竞彩篮球大小分");
		map.put("J00009", "冠军");
		map.put("J00010", "冠亚军");
		map.put("J00010", "竞彩冠亚军");
		map.put("J00011", "竞彩足球混合");
		map.put("J00012", "竞彩篮球混合");
		map.put("J00013", "竞彩足球让球胜平负");
		map.put("B00001", "北单胜平负");
		map.put("B00002", "北单总进球");
		map.put("B00003", "北单半全场");
		map.put("B00004", "北单上下盘单双");
		map.put("B00005", "北单单场比分");
	}

	public static Map<String, String> getMap() {
		return map;
	}

	public static String getName(String lotno) {
		return map.get(lotno);
	}

	@Value("${lotteryurl}")
	private String lotteryurl;

	@Value("${lotterycoreurl}")
	private String lotterycoreurl;

//	@Value("${testlotteryurl}")
//	private String testlotteryurl;
//
//	@Value("${testlotterycoreurl}")
//	private String testlotterycoreurl;
//
//	@Value("${testusernolist}")
//	private String testusernolist;

	@SuppressWarnings("unchecked")
	public String selectTwininfoBylotno(String lotno, String issuenum) {
		StringBuilder result = new StringBuilder();
		String url = lotteryurl + "/select/getTwininfoBylotno";
		try {
			String json = Request
					.Post(url)
					.bodyForm(
							Form.form().add("lotno", lotno)
									.add("issuenum", issuenum).build())
					.execute().returnContent().asString();
			ResponseData responseData = JsonMapper.fromJson(json,
					ResponseData.class);
			if (responseData.getErrorCode().equals("0")) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) responseData
						.getValue();
				if (list != null) {
					String lotName = map.get(lotno);
					for (Map<String, Object> map : list) {
						Map<String, Object> id = (Map<String, Object>) map
								.get("id");
						String batchcode = (String) id.get("batchcode");
						String winbasecode = (String) map.get("winbasecode");
						String winspecialcode = (String) map
								.get("winspecialcode");
						result.append(lotName + "第" + batchcode + "期开奖号码"
								+ winbasecode + " " + winspecialcode + "\n");
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求lottery异常url:" + url + ",params:lotno=" + lotno
					+ "&issuenum=" + issuenum, e);
		}
		return result.toString();
	}

	public String findOrCreateBigUser(String openid, String nickname,
			String type) {
		String userno = this.findBigUser(openid, type);
		if (StringUtils.isEmpty(userno)) {
			logger.info("创建联合用户 openid:{},nickname:{},type:{}", openid,
					nickname, type);
			userno = this.createBigUser(openid, nickname, type);
		}
		return userno;
	}

	@SuppressWarnings("unchecked")
	public String findBigUser(String openid, String type) {
		String userno = null;
		String url = "";
		url = lotteryurl + "/tbiguserinfoes?json&find=BigUser&outuserno="
				+ openid + "&type=" + type;
		try {
			String json = Request.Get(url).execute().returnContent().asString();
			Map<String, Object> map = JsonMapper.fromJson(json, HashMap.class);
			String errorCode = (String) map.get("errorCode");
			if (errorCode.equals("0")) {
				Map<String, Object> tbiguserinfo = (Map<String, Object>) map
						.get("value");
				if (tbiguserinfo.containsKey("userno")) {
					userno = (String) tbiguserinfo.get("userno");
				}
			} else {
				logger.error("查询大客户异常 openid:" + openid + " type:" + type
						+ " errorCode:" + errorCode);
			}
		} catch (Exception e) {
			logger.error("查询大客户异常 openid:" + openid + " type:" + type, e);
		}
		return userno;
	}

	/**
	 * 创建联合用户
	 * 
	 * @param openid
	 * @param nickname
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String createBigUser(String openid, String nickname, String type) {
		String userno = null;
		try {
			Form form = Form.form().add("userName", openid)
					.add("password", randomPwd(8)).add("channel", "2")
					.add("outuserno", openid).add("type", type);
			if (StringUtils.isNotEmpty(nickname)) {
				form.add("nickname", nickname);
			}
			String json = Request
					.Post(lotteryurl + "/tbiguserinfoes/registerBigUser")
					.bodyForm(form.build(), Charset.forName("UTF-8")).execute()
					.returnContent().asString();
			Map<String, Object> map = JsonMapper.fromJson(json, HashMap.class);
			String errorCode = (String) map.get("errorCode");
			if (errorCode.equals("0")) {
				Map<String, Object> tbiguserinfo = (Map<String, Object>) map
						.get("value");
				if (tbiguserinfo.containsKey("userno")) {
					userno = (String) tbiguserinfo.get("userno");
				}
			} else {
				logger.error("创建大客户失败 openid:" + openid + " nickname:"
						+ nickname + " type:" + type + " errorCode:"
						+ errorCode);
			}
		} catch (Exception e) {
			logger.error("创建大客户失败 openid:" + openid + " nickname:" + nickname
					+ " type:" + type, e);
		}
		return userno;
	}

	/**
	 * 直接扣款
	 * 
	 * @param userno
	 *            用户编号
	 * @param amt
	 *            单位：分
	 * @param bankid
	 * @param flowno
	 * @param memo
	 */
	public void deductAmt(String userno, String amt, String bankid,
			String flowno, String memo) {
		String json = null;
		try {
			String url = lotterycoreurl + "/taccounts/deductAmt";
			json = Request
					.Post(url)
					.bodyForm(
							Form.form().add("userno", userno).add("amt", amt)
									.add("bankid", bankid)
									.add("flowno", flowno).add("memo", memo)
									.build(), Charset.forName("UTF-8"))
					.execute().returnContent().asString();
		} catch (Exception e) {
			logger.error("扣款异常", e);
			throw new WeixinException(ErrorCode.ERROR);
		}
		ResponseData responseData = JsonMapper.fromJson(json,
				ResponseData.class);
		if (!"0".equals(responseData.getErrorCode())) {
			logger.info("扣款失败:errorCode=" + responseData.getErrorCode());
			throw new WeixinException(ErrorCode.DEDUCT_AMT_FAIL);
		}
	}

	private String randomPwd(int length) {
		String base = "ABCDEFGHIJKLMNPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 赠送彩金
	 * 
	 * @param userNo
	 * @param amount
	 * @return
	 */
	public String presentDividend(String userNo, String amount, String channel,
			String memo) {
		StringBuilder paramStr = new StringBuilder();
		paramStr.append("userno=" + userNo);
		paramStr.append("&amt=" + amount);
		paramStr.append("&accesstype=" + Constants.accessType);
		paramStr.append("&subchannel=" + Constants.subChannel);
		paramStr.append("&channel=" + channel);
		paramStr.append("&memo=" + memo);

		String url = lotterycoreurl + "/taccounts/doDirectChargeProcess";
		String result = HttpUtil.sendRequestByPost(url, paramStr.toString(),
				true);
		logger.info("赠送彩金返回:" + result + ",userNo:" + userNo + ";amount:"
				+ amount);
		return result;
	}

}
