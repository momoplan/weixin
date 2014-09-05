package com.ruyicai.weixin.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.weixin.consts.Const;
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
import com.ruyicai.weixin.util.StringUtil;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@Service
public class PacketActivityService {

	private Logger logger = LoggerFactory.getLogger(PacketActivityService.class);

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
	private WeixinService weixinService;

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
		// 判断用户是否存在
		CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(packetUserno,
				Const.WX_PACKET_ACTIVITY);

		// 扣款
		lotteryService.deductAmt(packetUserno, String.valueOf(punts * 200),
				"ryc001", "0000", "如意彩微信公众帐号送红包活动");
		// 扣款成功，生成红包
		String openid = null;
		Packet packet = packetDao.createPacket(openid, packetUserno, parts,
				punts, greetings);
		// 随机分配注数
		int[] puntArry = RandomPacketUtil.getRandomPunt(punts, parts);
		int packetId = packet.getId();
		for (int randomPunts : puntArry) {
			if (randomPunts > 0)
				addPuntPacket(packetId, randomPunts);
		}
		
		sendBuyInfo(caseLotUserinfo.getOpenid(),String.valueOf(parts),String.valueOf(punts),String.valueOf(packetId));
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
	 
	public Map<String, Object> getPunts(String award_userno, String channel, String packet_id) {
		// 获取红包
		PuntPacket puntPacket = getPuntPacket(award_userno, channel, packet_id);
		
		// 生成注码
		String[] result = generatePunts(award_userno, channel, puntPacket);
		
		// 获取最新期开奖时间
		JSONObject ret = commonService.getBatchInfo();
		Calendar cal_open = Calendar.getInstance();
		java.text.DateFormat format1 = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		try {
			Date dt = format1.parse("20" + ret.getString("endtime"));
			cal_open.setTime(dt);
		} catch (ParseException e) {
			throw new WeixinException(ErrorCode.ERROR);
		}

		String pund = "43260902000"; // 奖池值
		JSONObject preOrderInfo = commonService.getPreBatchInfo();
		if (preOrderInfo != null)
			pund = preOrderInfo.getString("prizePoolAmount");

		Map<String, Object> iMap = new HashMap<String, Object>();
		iMap.put("punts", String.valueOf(puntPacket.getRandomPunts()));
		iMap.put("lottery_type", "双色球");
		iMap.put("lottery_date", format1.format(cal_open.getTime()));
		iMap.put("pund", pund);
		iMap.put("puntlist", result);
		
		sendGrabInfo(award_userno,String.valueOf(puntPacket.getRandomPunts()),format1.format(cal_open.getTime()),packet_id);
		
		return iMap;
	}
	
	/**
	 * 生成注码并投注
	 * 
	 * @param award_userno
	 * @param channel
	 * @param puntPacket
	 * @return
	 */
	public String[] generatePunts(String award_userno, String channel, PuntPacket puntPacket) {		 
		int puntId = puntPacket.getId();
		int punts = puntPacket.getRandomPunts();
		String[] result = DoubleBall.getDoubleBallsByString(punts);
		for (int i = 0; i < punts; i++) {
			doCreatePuntList(result[i], award_userno, channel, puntId);
		}

		return result;
	}
	
	/**
	 * 获取红包
	 * 
	 * @param award_userno
	 * @param channel
	 * @param packet_id
	 * @return
	 */
	@Transactional
	public PuntPacket getPuntPacket(String award_userno, String channel, String packet_id)
	{
		List<Packet> packetList = puntPacketDao.findOneNotAawardPart(packet_id);
		if (packetList != null && packetList.size() > 0) {
			List<PuntPacket> puntPacketList = puntPacketDao.findSinglePuntPart(packet_id);
			if (puntPacketList == null || puntPacketList.size() == 0)
			{
				throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
			}
			PuntPacket puntPacket = puntPacketList.get(0);
			processPuntPacket(puntPacket, award_userno, channel, 0);
			return puntPacket;
		} else {
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}
	
	/**
	 * 更新获取的红包
	 * 
	 * @param puntPacket
	 * @param award_userno
	 * @param channel
	 * @param get_status 红包状态 0-抢;1-返还
	 */
	public void processPuntPacket(PuntPacket puntPacket, String award_userno, String channel,int get_status)
	{
		int punts = puntPacket.getRandomPunts();

		// 送彩金接口
		commonService.presentDividend(award_userno, String.valueOf(200 * punts), channel, "微信号服务号抢红包奖励");

		// 更新每份红包
		puntPacketDao.updatePuntPacket(puntPacket, award_userno, get_status);
	}
	
	/**
	 * 抢红包状态判断
	 * 
	 * @param award_userno
	 *            发送红包用户编号
	 * @param packet_id
	 *            红包ID
	 */
	@SuppressWarnings({ "rawtypes" })
	public Map doGetPacketStus(String award_userno, String packet_id) {
//		List<?> lst = puntListDao.getBetMoeny("2014-08-26");
//		System.out.println(lst);
		String packetEncrypt = packet_id;
		packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密
		if (StringUtil.isEmpty(packet_id))
			throw new WeixinException(ErrorCode.ERROR);
		
		packet_id = packet_id.trim();
		Map<Integer, Object> status = getPacketStatus(award_userno, packet_id);

		int k = 1;
		Object v = "";
		for (Entry<Integer, Object> entry : status.entrySet())
		{
			k = entry.getKey();
			v = entry.getValue();
			break;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("award_userno", award_userno);
		map.put("packet_id", packetEncrypt);
		map.put("status", String.valueOf(k));
		map.put("status_info", v);
		try {
			Packet packet = Packet.findPacket(Integer.parseInt(packet_id));
			CaseLotUserinfo userInfo = caseLotActivityService.caseLotchances(
					packet.getPacketUserno(), Const.WX_PACKET_ACTIVITY);
			
			String imgurl = userInfo.getSettingImgurl();
			if(StringUtil.isEmpty(imgurl))
				imgurl = userInfo.getHeadimgurl();
			
			map.put("headimgurl", imgurl);
			map.put("nickname", userInfo.getNickname());
		} catch (Exception ex) {
			logger.info("caseLotActivityService.caseLotchances award_userno{}",
					award_userno);
			throw new WeixinException(ErrorCode.ERROR);
		}

		return map;
	}
	
	/**
	 * 获取红包当前状态
	 * 
	 * @param award_userno
	 * @param packet_id
	 * @return
	 */
	public Map<Integer, Object> getPacketStatus(String award_userno, String packet_id)
	{
		
		Map<Integer, Object> status = new HashMap<Integer, Object>();
		Map<String, Object> iMap = new HashMap<String, Object>();
		Packet packet = Packet.findPacket(Integer.parseInt(packet_id));
		if (null == packet)
		{
			logger.info("Packet.findPacket-packet_id:{}", packet_id);
			throw new WeixinException(ErrorCode.PACKET_NOT_EXIST);
		} else
		{
			try
			{
				iMap.put("packet_userno", packet.getPacketUserno());
				iMap.put("total_punts", packet.getTotalPunts());
//				if (packet.getPacketUserno().equals(award_userno))
//				{
//					logger.info("不能抢自己送的红包 - packet_id:{} award_userno:{}", packet_id, award_userno);
//					status.put(3, "不能抢自己发的红包");
//					return status;
//				}

				List<PuntPacket> lstPuntPacket = puntPacketDao.findByGetUserno(award_userno, packet_id);
				if (lstPuntPacket != null && lstPuntPacket.size() > 0)
				{
					logger.info("红包已抢过 - packet_id:{} award_userno:{}", packet_id, award_userno);
					
					// 返回已获得的红包详情
					String pund = "43260902000"; // 奖池值
					JSONObject preOrderInfo = commonService.getPreBatchInfo();
					if (preOrderInfo != null)
						pund = preOrderInfo.getString("prizePoolAmount");

					PuntPacket puntPacket = lstPuntPacket.get(0);
					List<PuntList> puntList = puntListDao.findPuntListGrabedList(puntPacket.getId());
					String[] result = new String[puntPacket.getRandomPunts()];
					Date lottery_date = null;
					if (puntList != null && puntList.size() > 0)
					{
						for (int i = 0; i< puntList.size(); i++)
						{
							PuntList punt = puntList.get(i);
							result[i] = punt.getBetcode();
							Calendar cal = punt.getOpentime();
							if (lottery_date != null) {
								if (lottery_date.after(cal.getTime())) {
									lottery_date = cal.getTime();
								}
							} else {
								lottery_date = cal.getTime();
							}
						}
					}
					
					iMap.put("punts", String.valueOf(puntPacket.getRandomPunts()));
					iMap.put("lottery_type", "双色球");
					iMap.put("lottery_date", lottery_date == null ? "" : DateUtil.format("yyyy-MM-dd", lottery_date));
					iMap.put("pund", pund);
					iMap.put("puntlist", result);
					iMap.put("ret_msg", "红包已抢过");
					
					status.put(2, iMap);
					return status;
				}

				List<PuntPacket> puntPacket = puntPacketDao.findLeftParts(packet_id);
				if (null == puntPacket || puntPacket.size() == 0)
				{
					logger.info("红包已抢完 - packet_id:{} award_userno:{}", packet_id, award_userno);
					iMap.put("ret_msg", "红包已抢完");
					
					status.put(1, iMap);
					return status;
				}

			} catch (WeixinException ex)
			{
				logger.info("packet.getPacketUserno-award_userno:{}",
						award_userno);
				throw new WeixinException(ErrorCode.PACKET_STATUS_EXIST);
			}
		}
		iMap.put("ret_msg", "红包可抢");
		status.put(0, iMap);
		return status; // 有剩下
	}

	/**
	 * 查询用户送出红包列表
	 * 
	 * @param packetUserno
	 *            送红包用户编号
	 * @return
	 * @throws Exception
	 */
	public Object doGetPacketList(String packetUserno) throws Exception {
		List<Packet> list = packetDao.findPacketListByUserno(packetUserno);
		if (list != null && list.size() > 0) {
			JSONArray arry = new JSONArray();
			for (Packet packet : list) {
				JSONObject map = new JSONObject();
				// 红包详情
				map.put("packet_id", ToolsAesCrypt.Encrypt(String.valueOf(packet.getId()), Const.PACKET_KEY)); //加密
				
				map.put("total_punts", packet.getTotalPunts());
				map.put("total_parts", packet.getTotalPersons());
				map.put("paket_date", DateUtil.format("yyyy-MM-dd", packet
						.getCreatetime().getTime()));
				if(null == packet.getReturnPunts())
					map.put("return_punts", 0);
				else
					map.put("return_punts", packet.getReturnPunts());
				// 红包领取份数
				List<PuntPacket> grabList = puntPacketDao
						.findPuntPacketGrabedList(packet.getId());

				int get_punts = 0; // 领取注数
				// 查询中奖人数
				int win_persons = 0;
				if (grabList != null && grabList.size() > 0) {
					for (PuntPacket puntPacket : grabList) {
						get_punts += puntPacket.getRandomPunts();
						
						List<PuntList> puntList = puntListDao
								.findPuntListGrabedList(puntPacket.getId());
						if (puntList != null && puntList.size() > 0) {
							for (PuntList punt : puntList) {
								if (punt.getOrderprizeamt() != null
										&& punt.getOrderprizeamt() > 0)
									++win_persons;
							}
						}
					}
				}

				map.put("get_punts", get_punts);
				map.put("win_persons", win_persons);

				arry.put(map);
			}
			return arry.toString();
		} else {
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}

	/**
	 * 查红包详情
	 * 
	 * @param userno
	 *            用户编号
	 * @param packetId
	 *            红包id
	 * @return
	 * @throws Exception
	 */
	public Object doGetPacketInfo(String userno, String packetId)
			throws Exception {
		packetId = ToolsAesCrypt.Decrypt(packetId, Const.PACKET_KEY); // 解密
		
		Packet packet = Packet.findPacket(Integer.valueOf(packetId));
		if (packet != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			String packetUserno = packet.getPacketUserno();
			map.put("packet_id", ToolsAesCrypt.Encrypt(String.valueOf(packet.getId()), Const.PACKET_KEY)); // 加密
			map.put("from_userno", packetUserno);
			map.put("total_parts", packet.getTotalPersons());
			map.put("total_punts", packet.getTotalPunts());
			map.put("orderdate",
					DateUtil.format("MM月dd日", packet.getCreatetime().getTime()));
			map.put("greetings", packet.getGreetings());
			String nickName = "";
			String headimg = "";
			CaseLotUserinfo userInfo = caseLotActivityService.caseLotchances(
					packetUserno, Const.WX_PACKET_ACTIVITY);
			if (userInfo != null) {
				nickName = userInfo.getNickname();
			 
				headimg = userInfo.getSettingImgurl();
				if (StringUtil.isEmpty(headimg))
					headimg = userInfo.getHeadimgurl();
			}
			map.put("nickname", nickName);
			map.put("headimg", headimg);
			String is_self = "0"; // 非本人
			String can_thank = "0"; // 是否可答谢,0-不可答谢;1-可答谢
			if (userno.equals(packetUserno)) {
				is_self = "1";
			}
			map.put("is_self", is_self);

			// 红包领取份数
			List<PuntPacket> grabList = puntPacketDao
					.findPuntPacketGrabedList(packet.getId());
			map.put("get_parts", grabList.size()); // 已领取份数

			// 用户领取详情
			int get_punts = 0; // 已领取注数
			int userno_punts = 0; // userno 参数抢到注数
			String is_thanks = "0"; // 已答谢状态,0-未答谢;1-已答谢
			String thank_words = "";
			boolean isMe = false;
			int totalPunts = 0;
			String isOpen = "0"; // 是否开奖,0-未开奖:1-已开奖
			int packet_user_award = 0;
			JSONArray arry = new JSONArray();
			Map<String, Object> packet_user_Map = new HashMap<String, Object>();
			Date return_lottery_date = null;
			if (grabList != null && grabList.size() > 0) {
				for (PuntPacket puntPacket : grabList) {
					get_punts += puntPacket.getRandomPunts();
					
					if(puntPacket.getGetUserno().equals(packetUserno) && puntPacket.getGetStatus() == 1)
					{
						totalPunts += puntPacket.getRandomPunts();
						
						List<PuntList> puntList = puntListDao
								.findPuntListGrabedList(puntPacket.getId());
						if (puntList != null && puntList.size() > 0) {
							for (PuntList punt : puntList) {
								if (punt.getOrderprizeamt() != null) {
									isOpen = "1";
								} else
								{
									Calendar cal = punt.getOpentime();
									if (return_lottery_date != null) {
										if (return_lottery_date.after(cal.getTime())) {
											return_lottery_date = cal.getTime();
										}
									} else {
										return_lottery_date = cal.getTime();
									}
								}
								
								if (punt.getOrderprizeamt() != null
										&& punt.getOrderprizeamt() > 0) {
									packet_user_award += punt.getOrderprizeamt();
								}
							}
						}
						
						continue;
					}
					
					Map<String, Object> grapMap = new HashMap<String, Object>();
					grapMap.put("punts", puntPacket.getRandomPunts());
					grapMap.put("acknowledge", StringUtil.isEmpty(puntPacket.getThankWords()) ? "" : puntPacket.getThankWords());
					if (userno.equals(puntPacket.getGetUserno()))
					{
						isMe = true;
						userno_punts = puntPacket.getRandomPunts();
						if (!StringUtil.isEmpty(puntPacket.getThankWords())) {
							thank_words = puntPacket.getThankWords();
							is_thanks = "1";
						} else
						{
							can_thank = "1";
						}
					}

					CaseLotUserinfo grabUserInfo = caseLotActivityService
							.caseLotchances(puntPacket.getGetUserno(),
									Const.WX_PACKET_ACTIVITY);
					
					if (grabUserInfo != null) {
						nickName = grabUserInfo.getNickname();
						headimg = grabUserInfo.getHeadimgurl();
					}
					if(isMe && StringUtil.isEmpty(nickName))
						nickName = "我";
					
					grapMap.put("nickname", nickName);
					grapMap.put("headimg", headimg);
					isMe = false;

					// 中奖详情
					int award = 0;
					Date lottery_date = null;
					List<PuntList> puntList = puntListDao
							.findPuntListGrabedList(puntPacket.getId());
					if (puntList != null && puntList.size() > 0) {
						for (PuntList punt : puntList) {
							if (punt.getOrderprizeamt() != null) {
								isOpen = "1";
							} else
							{
								Calendar cal = punt.getOpentime();
								if (lottery_date != null) {
									if (lottery_date.after(cal.getTime())) {
										lottery_date = cal.getTime();
									}
								} else {
									lottery_date = cal.getTime();
								}
							}
							
							if (punt.getOrderprizeamt() != null
									&& punt.getOrderprizeamt() > 0) {
								award += punt.getOrderprizeamt();
							}
						}
					}
					grapMap.put("award", award); // 中奖金额
					grapMap.put("isOpen", isOpen); // 是否开奖
					grapMap.put("lottery_date", lottery_date == null ? "" : DateUtil.format("MM月dd日", lottery_date));// 开奖时间

					arry.put(grapMap);
				}
				
				// 发红包用户返还详情
				packet_user_Map.put("isOpen", isOpen); // 是否开奖
				packet_user_Map.put("award", packet_user_award); // 中奖金额
				packet_user_Map.put("get_punts", totalPunts); // 领取注
				String strNickName = userInfo.getNickname();
				if ("1".equals(is_self) && StringUtil.isEmpty(strNickName))
					strNickName = "我";
				
				packet_user_Map.put("nickname", strNickName); // 用户昵称
				packet_user_Map.put("headimgurl", userInfo.getHeadimgurl()); // 用户头像				
				packet_user_Map.put("return_lottery_date", return_lottery_date == null ? "" : DateUtil.format("MM月dd日", return_lottery_date));// 开奖时间
				
			}

			map.put("packet_user_punts", packet_user_Map);
			map.put("punt_list", arry.toString());
			map.put("get_punts", get_punts);
			map.put("userno_punts", userno_punts);
			map.put("is_thanks", is_thanks);
			map.put("thank_words", thank_words);
			map.put("can_thank", can_thank);

			return map;
		} else {
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}
	
	 

	/**
	 * 答谢TA
	 * 
	 * @param awardUserno
	 *            用户编号
	 * @param thankWords
	 *            答谢语
	 * @param packetId
	 *            红包id
	 */
	public void doThankTa(String awardUserno, String thankWords, String packetId) {
		packetId = ToolsAesCrypt.Decrypt(packetId, Const.PACKET_KEY);// 解密
		PuntPacket puntPacket = puntPacketDao.thankWord(awardUserno,
				thankWords, packetId);
		if (puntPacket == null) {
			logger.info("答谢失败");
			throw new WeixinException(ErrorCode.THANKS_FAIL);
		}
	}

	/**
	 * 查询用户抢到红包列表
	 * 
	 * @param awardUserno
	 *            用户编号
	 * @return
	 */
	public String doGetMyPunts(String awardUserno) {
		List<PuntPacket> list = puntPacketDao.findPuntPacketByUserno(awardUserno);
		if (list != null && list.size() > 0) {
			JSONArray arry = new JSONArray();
			for (PuntPacket puntPacket : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 获取每份红包详情
				List<PuntList> puntList = puntListDao
						.findPuntListGrabedList(puntPacket.getId());
				if (puntList != null && puntList.size() > 0) {
					// 获取送红包人信息
					Packet packet = Packet.findPacket(puntPacket.getPacketId());
					String fromUserno = packet.getPacketUserno();
					CaseLotUserinfo userInfo = caseLotActivityService
							.caseLotchances(fromUserno,
									Const.WX_PACKET_ACTIVITY);
					map.put("nickname", userInfo.getNickname() == null ? ""
							: userInfo.getNickname());
					map.put("get_time", DateUtil.format("yyyy-MM-dd",
							puntPacket.getGetTime().getTime())); // 领取红包时间
					
					if(fromUserno.equals(awardUserno) && puntPacket.getGetStatus() == 1)
						map.put("isreturn", 1);
					else
						map.put("isreturn", 0);

					JSONArray puntArry = new JSONArray();
					for (PuntList punt : puntList) {
						Map<String, Object> puntMap = new HashMap<String, Object>();
						puntMap.put("betCode", punt.getBetcode());
						
						String openTime = "";
						Calendar cal = punt.getOpentime();
						if (cal.getTime() != null)
							openTime = DateUtil.format("MM月dd日", cal.getTime());
						
						String isOpen = "0"; // 是否开奖,0-未开奖:1-已开奖
						if (punt.getOrderprizeamt() != null) {
							isOpen = "1";
						}
						
						puntMap.put("orderprizeamt", punt.getOrderprizeamt() == null ? 0 : punt.getOrderprizeamt());
						puntMap.put("openTime", openTime);
						puntMap.put("isOpen", isOpen);

						puntArry.put(puntMap);
					}

					map.put("punt_list", puntArry);
					arry.put(map);
				}
			}

			return arry.toString();
		} else {
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
	}

	/**
	 * 查询我的活动
	 * 
	 * @return
	 */
	public Map<String, Object> doGetActivityEnv() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Packet> packetList = Packet.findAllPackets();
		
		int beanchmark = 10000;
		if (packetList != null)
			beanchmark += packetList.size() * 10;
		
		map.put("total_packet_person", beanchmark);
		map.put("total_get_person", 1000);
		map.put("total_win", (int)(beanchmark * 25 * 200 * 0.56));
		
		return map;
	}
	
	/**
	 * 异步生成投注信息
	 * 
	 * @param betcode
	 * @param award_userno
	 * @param channel
	 * @param puntId
	 */
	@Async
	public void doCreatePuntList(String betcode, String award_userno, String channel, int puntId)
	{
		JSONObject ret = commonService.getBatchInfo();
		String batchcode = "";
		Calendar cal_open = Calendar.getInstance();
		
		java.text.DateFormat format1 = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		try {
			Date dt = format1.parse("20" + ret.getString("endtime"));
			cal_open.setTime(dt);
			batchcode = ret.getString("batchcode");
		} catch (ParseException e) {
			throw new WeixinException(ErrorCode.ERROR);
		}
		
		int count = 0;
		for(;;)
		{
			++ count;
			if (count > 5)
				break;
			
			ret = commonService.getDoubleDallBet(award_userno, "200",
					channel, "0001" + betcode + "^_1_200_200", batchcode);

			if (ret != null)
			{
				String errorCode = ret.get("error_code").toString();
				if ("0000".equals(errorCode)) // 投注成功
				{
					String orderId = ret.getString("orderId");
					puntListDao.createPuntList(batchcode, cal_open, betcode, puntId, orderId);
					break;
				} else if ("1001".equals(errorCode)) // 该期已过期
				{
					batchcode = ret.getString("batchcode"); // 重新获得下一期期号
				}
			}
		}
	}
	
	@Async
	public void addPuntPacket(int packetId, int randomPunts)
	{
		puntPacketDao.createPuntPacket(packetId, randomPunts);
	}
	
	/**
	 * 24小时返还红包处理
	 * 
	 * @return
	 */
	@Transactional
	public int returnAllLeftPunts()
	{
		int ret = 0;
		//在packet表中查找超过指定时间范围的红包，取红包id,userno
		//List<PuntPacket> lstPuntPacket = puntPacketDao.findExpiredDatePuntPacket();
		try
		{
			List<Packet> lstPacket = packetDao.findReturnPacketList();
			if (lstPacket != null && lstPacket.size() > 0)
			{
				for(int i = 0;i <lstPacket.size();i++)
				{
					int totalReturnPunts = 0;
					Packet packet = lstPacket.get(i);
					String packet_userno = packet.getPacketUserno();
					String packet_id = String.valueOf(packet.getId());	
					List<PuntPacket> lstPuntPacket = puntPacketDao.findLeftParts(packet_id);

					if (lstPuntPacket != null && lstPuntPacket.size() > 0)
					{
						for (PuntPacket puntPacket : lstPuntPacket)
						{
							processPuntPacket(puntPacket, packet_userno, Const.WX_PACKET_CHANNEL,1);
							generatePunts(packet_userno, Const.WX_PACKET_CHANNEL, puntPacket);
							totalReturnPunts += puntPacket.getRandomPunts();
						}
					}

					packet.setReturnPunts(totalReturnPunts);
					packet.merge();
					
					if (totalReturnPunts != 0)
					{
						CaseLotUserinfo userInfo = caseLotActivityService.caseLotchances(packet_userno, Const.WX_PACKET_ACTIVITY);
						if (userInfo != null)
						{
							String openid = userInfo.getOpenid();
							if (!StringUtil.isEmpty(openid))
								sendReturnPacket(openid, "如意彩彩票返还通知：", "如意彩双色球彩票", DateUtil.format("yy年MM月dd日", new Date()), "返还原因：超过24小时未被领取。");
						}
					}
				}
			}
		}
		catch(Exception ex)
		{			
			logger.info("returnAllLeftPunts:"+ex.getMessage());
			throw new WeixinException(ErrorCode.ERROR);
		}
		//根据红包id给送红包userno抢红包
		return ret;
	}
	
	
	/**
	 * 抢红包信息模板
	 * 
	 * @return
	 */
	@Async
	public void sendGrabInfo(String userno,String punts,String opentime,String packet_id)
	{
		CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(userno,
				Const.WX_PACKET_ACTIVITY);
		
		
		String openid = "";
		openid = caseLotUserinfo.getOpenid();
		String json = "{\"touser\":\"\",\"template_id\":\"\","
				+"\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":\"\"}";
		
		String jsoBuy = "{\"first\": {\"value\":\"\",\"color\":\"\"},\"keyword1\": {\"value\":\"\",\"color\":\"\"},\"keyword2\": {\"value\":\"\",\"color\":\"\"},\"remark\": {\"value\":\"\",\"color\":\"\"}}";
		 
		String templateid = "PZ5ca34hQ8T7l3ggNkbbTIM3xo1u0SCZjdnvnjy7UC4";
		String url = "http://wx.ruyicai.com/wxpay/html/sendRedbag/baginfo.html?packet_id="+ToolsAesCrypt.Encrypt(packet_id, Const.PACKET_KEY);
		String topcolor = "#DA2828";
		String color = "#DA2828";
//		String betInfo = "您已抢到"+packet_user_nickname+"的红包，"+opentime+"开奖";
		Date dt = new Date();
		 
		java.text.DateFormat format1 = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
	    String getTime = format1.format(dt);
		
		String betInfo = "共"+punts+"注，"+(Integer.parseInt(punts) * 2)+"元";
		
		JSONObject jsono = JSONObject.fromObject(jsoBuy);
		
		JSONObject jsonoSub = JSONObject.fromObject(jsono.get("first"));
		jsonoSub.element("value", "你领取了一个彩票红包");
		jsonoSub.element("color", color);		
		jsono.element("first", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("keyword1"));
		jsonoSub.element("value", betInfo);
		jsonoSub.element("color", color);
		jsono.element("keyword1", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("keyword2"));
		jsonoSub.element("value", getTime+"； 开奖时间："+opentime);
		jsonoSub.element("color", color);
		jsono.element("keyword2", jsonoSub);		 
		
//		jsonoSub = JSONObject.fromObject(jsono.get("remark"));
//		jsonoSub.element("value", opentime+"开奖");
//		jsonoSub.element("color", color);
//		jsono.element("remark", jsonoSub);
		 	 
		JSONObject jsonoMain = JSONObject.fromObject(json);		 
		jsonoMain.element("touser", openid);		
		jsonoMain.element("template_id", templateid);
		jsonoMain.element("url", url);
		jsonoMain.element("topcolor", topcolor);
		jsonoMain.element("data", jsono);
		 
		System.out.println(jsonoMain);
		sendTemplateMsg(jsonoMain.toString());
		 
	}
	
	
	/**
	 * 中奖信息模板
	 * 
	 * @return
	 */
	@Async
	public void sendBetInfo(String openid,String total_money)
	{
		String json = "{\"touser\":\"\",\"template_id\":\"\","
				+"\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":\"\"}";
		
		String jsoBuy = "{\"title\": {\"value\":\"\",\"color\":\"\"},\"headinfo\": {\"value\":\"\",\"color\":\"\"},\"program\": {\"value\":\"\",\"color\":\"\"},\"result\": {\"value\":\"\",\"color\":\"\"},\"remark\": {\"value\":\"\",\"color\":\"\"}}";
		 
		String templateid = "HZt4Rp3WoeeEXqJ8SMO-W3Je_7yy7qUjdOIvZAvfYCw";
		String url = "http://wx.ruyicai.com/wxpay/html/sendRedbag/account.html?info=get";
		String topcolor = "#DA2828";
		String color = "#DA2828";
		String betInfo = "共1注中奖 中奖金额共"+total_money+"元";
		
		JSONObject jsono = JSONObject.fromObject(jsoBuy);
		
		JSONObject jsonoSub = JSONObject.fromObject(jsono.get("title"));
		jsonoSub.element("value", "如意彩彩票中奖通知：");
		jsonoSub.element("color", color);		
		jsono.element("title", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("headinfo"));
		jsonoSub.element("value", "恭喜你领取的如意彩票中奖啦！");
		jsonoSub.element("color", color);
		jsono.element("headinfo", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("program"));
		jsonoSub.element("value", "双色球");
		jsonoSub.element("color", color);
		jsono.element("program", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("result"));
		jsonoSub.element("value", betInfo);
		jsonoSub.element("color", color);
		jsono.element("result", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("remark"));
		jsonoSub.element("value", "");
		jsonoSub.element("color", color);
		jsono.element("remark", jsonoSub);
		 	 
		JSONObject jsonoMain = JSONObject.fromObject(json);		 
		jsonoMain.element("touser", openid);		
		jsonoMain.element("template_id", templateid);
		jsonoMain.element("url", url);
		jsonoMain.element("topcolor", topcolor);
		jsonoMain.element("data", jsono);
		 
		System.out.println(jsonoMain);
		sendTemplateMsg(jsonoMain.toString());
		 
	}
	
	/**
	 * 送红包信息模板
	 * 
	 * @return
	 */
	@Async
	public void sendBuyInfo(String openid,String totalPacketpunt,String total_punts,String packet_id)
	{
		String json = "{\"touser\":\"\",\"template_id\":\"\","
				+"\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":\"\"}";
		
		String jsoBuy = "{\"productType\": {\"value\":\"\",\"color\":\"\"},\"name\": {\"value\":\"\",\"color\":\"\"},\"number\": {\"value\":\"\",\"color\":\"\"},\"expDate\": {\"value\":\"\",\"color\":\"\"},\"remark\": {\"value\":\"\",\"color\":\"\"}}";
		 
		String templateid = "xYBPYEur-WrpGvUjMsLj2Iz_Kpsc4B_CvlB6OlGVI_w";
		String url = "http://wx.ruyicai.com/wxpay/html/sendRedbag/baginfo.html?packet_id="+ToolsAesCrypt.Encrypt(packet_id, Const.PACKET_KEY);
		String topcolor = "#DA2828";
		String color = "#DA2828";
		
		JSONObject jsono = JSONObject.fromObject(jsoBuy);
		
		JSONObject jsonoSub = JSONObject.fromObject(jsono.get("productType"));
		jsonoSub.element("value", "彩票名称");
		jsonoSub.element("color", "#000000");		
		jsono.element("productType", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("name"));
		jsonoSub.element("value", "双色球");
		jsonoSub.element("color", color);
		jsono.element("name", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("number"));
		jsonoSub.element("value", "共"+totalPacketpunt+"个红包,共"+total_punts+"注");
		jsonoSub.element("color", color);
		jsono.element("number", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("expDate"));
		jsonoSub.element("value", "24小时后未领取的彩票将返还到送红包账户");
		jsonoSub.element("color", color);
		jsono.element("expDate", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("remark"));
		jsonoSub.element("value", "");
		jsonoSub.element("color", color);
		jsono.element("remark", jsonoSub);
		 	 
		JSONObject jsonoMain = JSONObject.fromObject(json);		 
		jsonoMain.element("touser", openid);		
		jsonoMain.element("template_id", templateid);
		jsonoMain.element("url", url);
		jsonoMain.element("topcolor", topcolor);
		jsonoMain.element("data", jsono);
		 
		logger.info("jsonoMain:"+jsonoMain);
		
		sendTemplateMsg(jsonoMain.toString());
		 
	}
	
	/**
	 * 24小时返还红包信息模板
	 * 
	 * @param openid
	 * @param notifyStr 如意彩彩票返还通知：
	 * @param lotName 如意彩双色球彩票
	 * @param expDate xx年xx月xx日
	 * @param remark 返还原因：超过24小时未被领取。
	 */
	@Async
	public void sendReturnPacket(String openid, String notifyStr, String lotName, String expDate, String remark)
	{
		String json = "{\"touser\":\"\",\"template_id\":\"\","
				+"\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":\"\"}}";
		
		String jsoBuy = "{\"first\": {\"value\":\"\",\"color\":\"\"},\"name\": {\"value\":\"\",\"color\":\"\"},\"expDate\": {\"value\":\"\",\"color\":\"\"},\"remark\": {\"value\":\"\",\"color\":\"\"}}";
		 
		String templateid = "XkyKVvp4XHWhttII1s38Y_CW8C1c6_-U_y_hEOyASjQ";
		String url = "http://wx.ruyicai.com/wxpay/html/sendRedbag/account.html?info=get";
		String topcolor = "#DA2828";
		String color = "#DA2828";
		
		JSONObject jsono = JSONObject.fromObject(jsoBuy);
		
		JSONObject jsonoSub = JSONObject.fromObject(jsono.get("first"));
		jsonoSub.element("value", notifyStr);
		jsonoSub.element("color", "#000000");		
		jsono.element("first", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("name"));
		jsonoSub.element("value", lotName);
		jsonoSub.element("color", color);
		jsono.element("name", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("expDate"));
		jsonoSub.element("value", expDate);
		jsonoSub.element("color", color);
		jsono.element("expDate", jsonoSub);
		
		jsonoSub = JSONObject.fromObject(jsono.get("remark"));
		jsonoSub.element("value", remark);
		jsonoSub.element("color", color);
		jsono.element("remark", jsonoSub);
		 	 
		JSONObject jsonoMain = JSONObject.fromObject(json);		 
		jsonoMain.element("touser", openid);		
		jsonoMain.element("template_id", templateid);
		jsonoMain.element("url", url);
		jsonoMain.element("topcolor", topcolor);
		jsonoMain.element("data", jsono);
		
		logger.info("24小时返还 - >jsonoMain:"+jsonoMain);
		sendTemplateMsg(jsonoMain.toString());
	}
	
	public int sendTemplateMsg(String strContent)
	{
		int ret = 0;			
		String accessToken = weixinService.getAccessToken();
		String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		logger.info("sendUrl:"+sendUrl);
		String sendData = strContent.toString();
		String ret1 = HttpUtil.sendRequestByPost(sendUrl, sendData, true);
		logger.info("accessToken:"+accessToken+","+"result:"+ret1);
		
		return ret;
	}

}
