package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.PuntPacket;

@Component
public class PuntPacketDao {

	@PersistenceContext
	private EntityManager entityManager;



	public PuntPacket findPunt(String packet_id) {
		if (StringUtils.isBlank(packet_id)) {
			throw new IllegalArgumentException("The argument packet_id  is required");
		}
		
		PuntPacket puntPacket = new PuntPacket();
		puntPacket.setPacketId(Integer.parseInt(packet_id));		
		PuntPacket iPunt = this.entityManager.find(PuntPacket.class, puntPacket);
		return iPunt;
	}
	
	@Transactional
	public PuntPacket createPuntPacket(int packetId, int randomPunts)
	{
		PuntPacket puntPacket = new PuntPacket();
		puntPacket.setPacketId(packetId);
		puntPacket.setRandomPunts(randomPunts);
		puntPacket.persist();
		return puntPacket;
	}
	
	public List<PuntPacket> findPuntPacketGrabedList(int packetId)
	{
		TypedQuery<PuntPacket> q = entityManager.createQuery("select o from PuntPacket o where o.getUserno != null and o.packetId = ? ", PuntPacket.class)
				.setParameter(1, packetId);
		return q.getResultList();
	}
	
}
