package com.ruyicai.weixin.dao;

import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.Packet;

@Component
public class PacketDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public Packet createPacket(String openid, String packetUserno, int parts, int punts, String greetings)
	{
		Packet packet = new Packet();
		packet.setOpenid(openid);
		packet.setPacketUserno(packetUserno);
		packet.setTotalPersons(parts);
		packet.setTotalPunts(punts);
		packet.setRealParts(parts);
		packet.setGreetings(greetings);
		packet.setCreatetime(Calendar.getInstance());
		packet.persist();
		return packet;
	}
}
