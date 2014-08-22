package com.ruyicai.weixin.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
	
	public int findPuntPacketGrabed(int packetId)
	{
		int q = entityManager.createQuery("select count(*) from PuntPacket p where p.getUserno != null and p.packetId = ? ", Long.class)
				.setParameter(1, packetId).getSingleResult().intValue();
		return q;
	}
	
}
