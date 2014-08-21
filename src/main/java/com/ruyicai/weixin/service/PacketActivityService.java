package com.ruyicai.weixin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntPacket;
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
	 * 抢红包处理
	 * 
	 * @param packetUserno 发送红包用户编号
	 * @param parts 份数
	 * @param punts 注数
	 * @param greetings 祝福语
	 */
	public Packet doCreatePacket(String packetUserno, int parts,
			int punts, String greetings) {
		// 扣款
		lotteryService.deductAmt(packetUserno, String.valueOf(punts * 200), "ryc001", "0000", "微信如意彩公众平台送红包活动");
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
}
