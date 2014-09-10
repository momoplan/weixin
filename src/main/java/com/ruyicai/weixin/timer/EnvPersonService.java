package com.ruyicai.weixin.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.domain.Packet;

public class EnvPersonService {
private Logger logger = LoggerFactory.getLogger(EnvPersonService.class);
	
 
	
	public void process() 
	{
		Const.Env_Person = (int) Packet.countPackets();
		logger.info("更新Const.Env_Person{}",Const.Env_Person);
	}
}
