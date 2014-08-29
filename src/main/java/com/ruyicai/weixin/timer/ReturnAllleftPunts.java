package com.ruyicai.weixin.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruyicai.weixin.service.PacketActivityService;

@Component
public class ReturnAllleftPunts {

	private Logger logger = LoggerFactory.getLogger(ReturnAllleftPunts.class);
	
	@Autowired
	PacketActivityService packetActivityService;
	
	public void process() 
	{
		logger.info("===========执行24小时返还红包开始==========");
		packetActivityService.returnAllLeftPunts();
		logger.info("===========执行24小时返还红包结束==========");
	}
}
