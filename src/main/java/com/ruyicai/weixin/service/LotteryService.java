package com.ruyicai.weixin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.lottery.ResponseData;
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

	@SuppressWarnings("unchecked")
	public String selectTwininfoBylotno(String lotno, String issuenum) {
		StringBuilder result = new StringBuilder();
		String url = lotteryurl + "/select/getTwininfoBylotno";
		try {
			String json = Request.Post(url).bodyForm(Form.form().add("lotno", lotno).add("issuenum", issuenum).build())
					.execute().returnContent().asString();
			ResponseData responseData = JsonMapper.fromJson(json, ResponseData.class);
			if (responseData.getErrorCode().equals("0")) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) responseData.getValue();
				if (list != null) {
					String lotName = map.get(lotno);
					for (Map<String, Object> map : list) {
						Map<String, Object> id = (Map<String, Object>) map.get("id");
						String batchcode = (String) id.get("batchcode");
						String winbasecode = (String) map.get("winbasecode");
						String winspecialcode = (String) map.get("winspecialcode");
						result.append(lotName + "第" + batchcode + "期开奖号码" + winbasecode + " " + winspecialcode + "\n");
					}
				}
			}
		} catch (Exception e) {
			logger.error("请求lottery异常url:" + url + ",params:lotno=" + lotno + "&issuenum=" + issuenum, e);
		}
		return result.toString();
	}
	/**
	 * 注册微信账户
	 * 
	 * @param accessToken
	 *            有效的access_token
	 *            
	 * @return
	 */
	public String dowxregister(String username,String nickname) {
		String userno = "";
		try {
			if("".equals(username)){
				logger.info("参数username为空，查询用户信息失败");
				return null;
			}
			//获取到用户的username之后查询此用户名是否注册过
			String lotteryuserinfourl = lotteryurl + "/tuserinfoes?json&find=ByUserName&userName="+username;
			String userinfo = Request.Get(lotteryuserinfourl).execute().returnContent().asString();
			logger.info("查询用户"+username+"的详细信息请求返回内容："+userinfo);
			JSONObject userinfojson = new JSONObject(userinfo).getJSONObject("value");
			
			if(userinfojson == null){
				String url =lotteryurl +"/tuserinfoes/register"; 
				// 调用接口创建菜单
				logger.info("用户详细信息请求连接"+url);
				String	json = Request.Post(url).bodyForm(Form.form().add("userName", username)
						.add("password", "123456").add("nickname", nickname).add("accesstype", "WX").add("agencyno", "000000").add("certid", "")
						.add("channel", "").add("info", "weixin").add("leave", "1").add("type", "1").build())
						.execute().returnContent().asString();
				
				logger.info("调用lottery注册接口， 注册结果返回："+json);
				JSONObject registerobj= new JSONObject(json);
				if(registerobj.getString("errorCode").equals("0")){
				   JSONObject user = registerobj.getJSONObject("value");
				   userno =user.getString("userno");
				}
				
			}else{
				   userno =userinfojson.getString("userno");
			}
		} catch (Exception e) {
			logger.error("微信创建如意彩用户失败：", e);
		}
		return userno;
	}
	
	public String selectUserinfoByOpenid(String openid) {
		String url = lotteryurl + "/tbiguserinfoes?json&find=BigUser&outuserno="+openid+"&type=weixin";
		String userno = "";
		try {
			
			String json = Request.Get(url).execute().returnContent().asString();
			ResponseData responseData = JsonMapper.fromJson(json, ResponseData.class);
			if (responseData.getErrorCode().equals("0")) {
				JSONObject value = (JSONObject) responseData.getValue();
				userno = "";
			}
		} catch (Exception e) {
			logger.error("请求lottery异常url:" + url + ",params:openid=" + openid, e);
		}
		return userno; 
	}
	public String bingUserByOpenid(String openid) {
		StringBuilder result = new StringBuilder();
		String url = lotteryurl + "/select/getTwininfoBylotno";
		try {
			String json = Request.Post(url).bodyForm(Form.form().add("openid", openid).build())
					.execute().returnContent().asString();
			ResponseData responseData = JsonMapper.fromJson(json, ResponseData.class);
			if (responseData.getErrorCode().equals("0")) {
				
			}
		} catch (Exception e) {
			logger.error("请求lottery异常url:" + url + ",params:openid=" + openid, e);
		}
		return result.toString();
	}

}
