package com.ruyicai.weixin.service;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.util.DoubleBall;
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
	 * 送红包处理
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
	
	/**
	 * 抢红包处理
	 * 
	 * @param packetUserno 发送红包用户编号
	 * @param parts 份数
	 * @param punts 注数
	 * @param greetings 祝福语
	 */
	public PuntPacket getPunts(String award_userno,String channel,String packet_id) {
		PuntPacket puntPacket = PuntPacket.findOneNotAawardPart(packet_id);
		int punts = puntPacket.getRandomPunts();
		 
		// 送彩金接口
//		String presentResult = commonService.presentDividend(award_userno,
//				String.valueOf(200 * punts), channel, "微信号服务号抢红包奖励");
//		if (!StringUtils.equals(presentResult, "0")) {
//			//throw new QqException(QqErrorCode.awardGiveFail);
//		}


		for (int i = 0; i < punts; i++) {
			// 生成投注数字
			  int result[] = DoubleBall.getDoubleBallNums();
			// 投注
		}
		
//		PuntList pList = new PuntList();
//		pList.setBatchcode("");
		
		
		puntPacket.setGetUserno(award_userno);			
		Date date=new Date();
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		puntPacket.setGetTime(cal);			
		puntPacket.persist();
		
		return puntPacket;
	}
	
	/**
	 * 抢红包处理
	 * 
	 * @param award_userno 发送红包用户编号
	 * @param packet_id 红包ID
	 */
	public void doGetPunts(String award_userno,String packet_id)
	{
		
	}
}
