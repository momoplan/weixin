package com.ruyicai.weixin.dao;

import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.ruyicai.weixin.domain.Packet;

@Component
public class PacketDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Packet createPacket(String openid, String packetUserno, int persons, int punts, String greetings)
	{
		Packet packet = new Packet();
		packet.setOpenid(openid);
		packet.setPacketUserno(packetUserno);
		packet.setTotalPersons(persons);
		packet.setTotalPunts(punts);
		packet.setGreetings(greetings);
		packet.setCreatetime(Calendar.getInstance());
		return packet;
	}
}
