package com.ruyicai.weixin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.util.RandomPacketUtil;

@Service
public class PacketActivityService {

	private Logger logger = LoggerFactory.getLogger(PacketActivityService.class);

	@Autowired
	PacketDao packetDao;
	
	public void doCreatePacket(String packetUserno, int persons,
			int punts, String greetings) {
		// 扣款
		
		// 扣款成功，生成红包
		String openid = null;
		packetDao.createPacket(openid, packetUserno, persons, punts, greetings);
		// 随机分配注数
		int[] puntArry = RandomPacketUtil.getRandomPunt(punts, persons);
		for(int punt : puntArry)
		{
			
		}
		
		
	}
}
