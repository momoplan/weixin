package com.ruyicai.weixin.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.util.DateUtil;
import com.ruyicai.weixin.util.RandomPacketUtil;

@Service
public class PacketActivityService {

	private Logger logger = LoggerFactory.getLogger(PacketActivityService.class);

	@Autowired
	PacketDao packetDao;
	
	@Autowired
	private PuntPacketDao puntPacketDao;
	
	@Autowired
	LotteryService lotteryService;
	
	/**
	 * 创建红包
	 * 
	 * @param packetUserno 发送红包用户编号
	 * @param parts 份数
	 * @param punts 注数
	 * @param greetings 祝福语
	 */
	public Packet doCreatePacket(String packetUserno, int parts,
			int punts, String greetings) {
		// 扣款
		lotteryService.deductAmt(packetUserno, String.valueOf(punts * 200), "ryc001", "0000", "如意彩微信公众帐号送红包活动");
		// 扣款成功，生成红包
		String openid = null;
		Packet packet = packetDao.createPacket(openid, packetUserno, parts, punts, greetings);
		// 随机分配注数
		int[] puntArry = RandomPacketUtil.getRandomPunt(punts, parts);
		int packetId = packet.getId();
		for(int randomPunts : puntArry)
		{
			if (randomPunts > 0)
				puntPacketDao.createPuntPacket(packetId, randomPunts);
		}
		return packet;
	}
	
	public PuntPacket findPunt(String packet_id) {
		PuntPacket puntPacket = puntPacketDao.findPunt(packet_id);
		if (puntPacket == null) {
			logger.error("appUser is null");
			return null;
		}
		
		return puntPacket;
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
				map.put("paket_date", DateUtil.format(packet.getCreatetime().getTime()));
				
				// 红包领取人数
				int grabs = puntPacketDao.findPuntPacketGrabed(packet.getId());
				map.put("get_punts", grabs);
				
				// 查询中奖人数
				map.put("win_persons", 0);
				
				arry.put(map);
			}
			return arry.toString();
		} else
		{
			return "无记录";
		}
	}
	
}
