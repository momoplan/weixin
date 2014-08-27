package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
	
	public List<Packet> findPacketListByUserno(String userno)
	{
		TypedQuery<Packet> q = entityManager.createQuery("select o from Packet o where o.packetUserno = ? order by o.id desc", Packet.class)
				.setParameter(1, userno);
		return q.getResultList();
	}
	
	public List<Packet> findReturnPacketList()
	{
		@SuppressWarnings("unchecked")
		List<Packet> q = entityManager.createNativeQuery("SELECT * FROM packet where createtime <= date_sub(NOW(), interval 24 hour) and return_punts is null", Packet.class).getResultList();
		return q;
	}
}
