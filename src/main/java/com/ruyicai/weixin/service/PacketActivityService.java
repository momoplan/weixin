package com.ruyicai.weixin.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.dao.PuntListDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntList;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.DateUtil;
import com.ruyicai.weixin.util.DoubleBall;
import com.ruyicai.weixin.util.RandomPacketUtil;

@Service
public class PacketActivityService {

	private Logger logger = LoggerFactory
			.getLogger(PacketActivityService.class);

	@Autowired
	PacketDao packetDao;

	@Autowired
	private PuntPacketDao puntPacketDao;

	@Autowired
	PuntListDao puntListDao;
	
	@Autowired
	LotteryService lotteryService;

	@Autowired
	private CommonService commonService;

	
	@Autowired
	CaseLotActivityService caseLotActivityService;
	
	/**
	 * 创建红包
	 * 
	 * @param packetUserno
	 *            发送红包用户编号
	 * @param parts
	 *            份数
	 * @param punts
	 *            注数
	 * @param greetings
	 *            祝福语
	 */
	public Packet doCreatePacket(String packetUserno, int parts, int punts,
			String greetings) {
		// 扣款
		lotteryService.deductAmt(packetUserno, String.valueOf(punts * 200), "ryc001", "0000", "如意彩微信公众帐号送红包活动");
		// 扣款成功，生成红包
		String openid = null;
		Packet packet = packetDao.createPacket(openid, packetUserno, parts,
				punts, greetings);
		// 随机分配注数
		int[] puntArry = RandomPacketUtil.getRandomPunt(punts, parts);
		int packetId = packet.getId();
		for (int randomPunts : puntArry) {
			if (randomPunts > 0)
				puntPacketDao.createPuntPacket(packetId, randomPunts);
		}
		return packet;
	}

	/**
	 * 抢红包处理
	 * 
	 * @param packetUserno
	 *            发送红包用户编号
	 * @param parts
	 *            份数
	 * @param punts
	 *            注数
	 * @param greetings
	 *            祝福语
	 */
	public Map getPunts(String award_userno, String channel,
			String packet_id) {
		PuntPacket puntPacket = PuntPacket.findOneNotAawardPart(packet_id);
		int punts = puntPacket.getRandomPunts();
		Map<String,Object> iMap = new HashMap<String,Object>();
		
		// 送彩金接口
		String presentResult = commonService.presentDividend(award_userno,
				String.valueOf(200 * punts), channel, "微信号服务号抢红包奖励");
		System.out.println("presentResult:" + presentResult);

		// 生成投注数字
		String[] result = DoubleBall.getDoubleBallsByString(punts);
		Calendar cal = Calendar.getInstance();
		Calendar cal_now = Calendar.getInstance();
		cal_now.setTime(new Date());
		
		iMap.put("punts", String.valueOf(punts));
		iMap.put("lottery_type", "双色球");	
		String opentime = "";
		JSONObject fromObject = null;	
		String batchcode = "";
		String ret = commonService.getBatchInfo();
		fromObject = JSONObject.fromObject(ret);

		java.text.DateFormat format1 = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");

		try {
			 
			Date dt = format1.parse("20"+fromObject.getString("endtime"));
			batchcode = fromObject.getString("batchcode");		
			cal.setTime(dt);
			Date date = cal.getTime();
			String str=format1.format(date);  
			opentime = str;
			 		
//			ret = commonService.getOpenInfo(String.valueOf(Integer.parseInt(batchcode)-1));
//			fromObject = JSONObject.fromObject(ret);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

		for (int i = 0; i < punts; i++) {
			ret = commonService.getDoubleDallBet(award_userno, "200",
					"1001", "0001" + result[i] + "^_1_200_200");
			fromObject = JSONObject.fromObject(ret);
			String orderId = fromObject.getString("orderId");
			String error_code = fromObject.getString("error_code");		

			PuntList pList = new PuntList();
			pList.setBatchcode(batchcode);
			pList.setOpentime(cal);
			pList.setBetcode(result[i]);
			pList.setPuntId(puntPacket.getId());
			pList.setOrderid(orderId);		 
			pList.setCreatetime(cal_now);
			pList.persist();
			logger.info(error_code);
			 
		}
		
		iMap.put("lottery_date", opentime);
		iMap.put("pund", "10000");
		iMap.put("puntlist", result);

		puntPacket.setGetUserno(award_userno);
		cal.setTime(new Date());
		puntPacket.setGetTime(cal);
		puntPacket.persist();
		return iMap;
	}

	/**
	 * 抢红包处理
	 * 
	 * @param award_userno
	 *            发送红包用户编号
	 * @param packet_id
	 *            红包ID
	 */
	@SuppressWarnings("rawtypes")
	public Map doGetPacketStus(String award_userno, String packet_id) {
		int ret = 0;
		Packet packet = Packet.findPacket(Integer.parseInt(packet_id));
		if (packet.getPacketUserno().equals(award_userno)) {
			ret = 3; // 送红包的抢不了
		}

		if (ret == 0) {
			PuntPacket puntPacket = null;
			puntPacket = PuntPacket.findByGetUserno(award_userno,
					packet_id);
			if (null != puntPacket) {
				ret = 2; // 已抢
			}
		}

		if (ret == 0) {
			PuntPacket puntPacket = PuntPacket.findLeftParts(packet_id);
			if (null == puntPacket) {
				ret = 1; // 有剩下
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("award_userno", award_userno);
		map.put("packet_id", packet_id);
		map.put("status", String.valueOf(ret));

		return map;
	}
	
	/**
	 * 查询用户送出红包列表
	 * 
	 * @param packetUserno 送红包用户编号
	 * @return
	 * @throws Exception
	 */
	public Object doGetPacketList(String packetUserno) throws Exception
	{
		List<Packet> list = packetDao.findPacketListByUserno(packetUserno);
		if (list != null && list.size() > 0)
		{
			JSONArray arry = new JSONArray();
			for (Packet packet : list)
			{
				JSONObject map = new JSONObject();
				// 红包详情
				map.put("packet_id", packet.getId());
				map.put("total_punts", packet.getTotalPunts());
				map.put("total_parts", packet.getTotalPersons());
				map.put("paket_date", DateUtil.format("yyyy-MM-dd", packet.getCreatetime().getTime()));

				// 红包领取份数
				List<PuntPacket> grabList = puntPacketDao.findPuntPacketGrabedList(packet.getId());
				map.put("get_punts", grabList.size());

				// 查询中奖人数
				int win_persons = 0;
				if (grabList != null && grabList.size() > 0)
				{
					for (PuntPacket puntPacket : grabList)
					{
						List<PuntList> puntList = puntListDao.findPuntListGrabedList(puntPacket.getId());
						if (puntList != null && puntList.size() > 0)
						{
							for (PuntList punt : puntList)
							{
								if (punt.getOrderprizeamt() != null && punt.getOrderprizeamt() > 0 )
									++win_persons;
							}
						}
					}
				}

				map.put("win_persons", win_persons);

				arry.put(map);
			}
			return arry.toString();
		} else
		{
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}

	/**
	 * 查红包详情
	 * 
	 * @param userno 用户编号
	 * @param packetId 红包id
	 * @return
	 * @throws Exception
	 */
	public Object doGetPacketInfo(String userno, String packetId) throws Exception
	{
		Packet packet = Packet.findPacket(Integer.valueOf(packetId));
		if (packet != null)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			String packetUserno = packet.getPacketUserno();
			map.put("packet_id", packet.getId());
			map.put("from_userno", packetUserno);
			map.put("total_parts", packet.getTotalPersons());
			map.put("total_punts", packet.getTotalPunts());
			map.put("orderdate", DateUtil.format("yyyy-MM-dd", packet.getCreatetime().getTime()));
			map.put("greetings", packet.getGreetings());
			String nickName = "";
			String headimg = "";
			String wx_packet_activity = "HM00002";
			CaseLotUserinfo userInfo = caseLotActivityService.caseLotchances(packetUserno, wx_packet_activity);
			if (userInfo != null)
			{
				nickName = userInfo.getNickname();
				headimg = userInfo.getHeadimgurl();
			}
			map.put("nickname", nickName);
			map.put("headimg", headimg);
			String is_self = "0"; // 非本人
			if (userno.equals(packetUserno))
			{
				is_self = "1";
			}
			map.put("is_self", is_self);
			
			// 红包领取份数
			List<PuntPacket> grabList = puntPacketDao.findPuntPacketGrabedList(packet.getId());
			map.put("get_parts", grabList.size()); // 已领取份数

			// 用户领取详情
			if (grabList != null && grabList.size() > 0)
			{
				int get_punts = 0; // 已领取注数
				int userno_punts = 0; // userno 参数抢到注数
				
				JSONArray arry = new JSONArray();
				for (PuntPacket puntPacket : grabList)
				{
					Map<String, Object> grapMap = new HashMap<String, Object>();
					grapMap.put("punts", puntPacket.getRandomPunts());
					grapMap.put("acknowledge", puntPacket.getThankWords());
					
					get_punts += puntPacket.getRandomPunts();
					if (!userno.equals(packetUserno))
						userno_punts = puntPacket.getRandomPunts();
					
					CaseLotUserinfo grabUserInfo = caseLotActivityService.caseLotchances(puntPacket.getGetUserno(), wx_packet_activity);
					if (grabUserInfo != null)
					{
						nickName = grabUserInfo.getNickname();
						headimg = grabUserInfo.getHeadimgurl();
					}
					grapMap.put("nickname", nickName);
					grapMap.put("headimg", headimg);

					// 中奖详情
					int award = 0;
					Date lottery_date = null;
					List<PuntList> puntList = puntListDao.findPuntListGrabedList(puntPacket.getId());
					if (puntList != null && puntList.size() > 0)
					{
						for (PuntList punt : puntList)
						{
							if (punt.getOrderprizeamt() != null && punt.getOrderprizeamt() > 0 )
							{
								award += punt.getOrderprizeamt();
							}
							if (award == 0)
							{
								if (lottery_date != null)
								{
									if (lottery_date.after(punt.getOpentime().getTime()))
									{
										lottery_date = punt.getOpentime().getTime();
									}
								} else
								{
									lottery_date = punt.getOpentime().getTime();
								}
							}
						}
					}
					grapMap.put("award", award); // 中奖金额
					grapMap.put("lottery_date", lottery_date == null ? "" : DateUtil.format(lottery_date));// 开奖时间

					arry.put(grapMap);
				}
				map.put("punt_list", arry.toString());
				
				map.put("get_punts", get_punts);
				map.put("userno_punts", userno_punts);
			}

			return map;
		} else
		{
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}

	/**
	 * 答谢TA
	 * 
	 * @param awardUserno 用户编号
	 * @param thankWords 答谢语
	 * @param packetId 红包id
	 */
	public void doThankTa(String awardUserno, String thankWords, String packetId)
	{
		PuntPacket puntPacket = puntPacketDao.thankWord(awardUserno, thankWords, packetId);
		if (puntPacket == null)
		{
			logger.info("答谢失败");
			throw new WeixinException(ErrorCode.THANKS_FAIL);
		}
	}

	/**
	 * 查询用户抢到红包列表
	 *  
	 * @param awardUserno 用户编号
	 * @return
	 */
	public String doGetMyPunts(String awardUserno)
	{
		List<PuntPacket> list = puntPacketDao.findPuntPacketByUserno(awardUserno);
		if (list != null && list.size() > 0)
		{
			String wx_packet_activity = "HM00002";
			JSONArray arry = new JSONArray();
			for (PuntPacket puntPacket : list)
			{
				Map<String, Object> map = new HashMap<String, Object>();
				// 获取送红包人信息
				Packet packet = Packet.findPacket(puntPacket.getPacketId());
				String fromUserno = packet.getPacketUserno();
				CaseLotUserinfo userInfo = caseLotActivityService.caseLotchances(fromUserno, wx_packet_activity);
				map.put("nickname", userInfo.getNickname() == null ? "" : userInfo.getNickname());
				map.put("get_time", DateUtil.format("yyyy-MM-dd", puntPacket.getGetTime().getTime())); // 领取红包时间

				// 获取每份红包详情
				List<PuntList> puntList = puntListDao.findPuntListGrabedList(puntPacket.getId());
				if (puntList != null && puntList.size() > 0)
				{
					JSONArray puntArry = new JSONArray();
					for (PuntList punt : puntList)
					{
						Map<String, Object> puntMap = new HashMap<String, Object>();
						puntMap.put("betCode", punt.getBetcode());
						String openTime = "";
						if (punt.getOpentime().getTime() != null)
							openTime = DateUtil.format("yyyy-MM-dd", punt.getOpentime().getTime());
						
						puntMap.put("openTime", openTime);
						puntMap.put("orderprizeamt", punt.getOrderprizeamt() == null ? "0" : punt.getOrderprizeamt());
						String isOpen = "0"; // 是否开奖,0-未开奖:1-已开奖
						if (new Date().after(punt.getOpentime().getTime()))
						{
							isOpen = "1";
						}
						puntMap.put("isOpen", isOpen);
						
						puntArry.put(puntMap);
					}
					
					map.put("punt_list", puntArry);
				}
				
				arry.put(map);
			}
			
			return arry.toString();
		} else
		{
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}
	
}
