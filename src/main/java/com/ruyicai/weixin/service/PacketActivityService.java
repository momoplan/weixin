package com.ruyicai.weixin.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntList;
import com.ruyicai.weixin.domain.PuntPacket;
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
	LotteryService lotteryService;

	@Autowired
	private CommonService commonService;

	/**
	 * 送红包处理
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
		lotteryService.deductAmt(packetUserno, String.valueOf(punts * 200),
				"ryc001", "0000", "微信如意彩公众平台送红包活动");
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
}
