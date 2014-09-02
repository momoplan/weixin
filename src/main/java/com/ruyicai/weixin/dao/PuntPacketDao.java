package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.StringUtil;

@Component
public class PuntPacketDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public PuntPacket createPuntPacket(int packetId, int randomPunts)
	{
		PuntPacket puntPacket = new PuntPacket();
		puntPacket.setPacketId(packetId);
		puntPacket.setRandomPunts(randomPunts);
		puntPacket.persist();
		return puntPacket;
	}
	
	@Transactional
	public void updatePuntPacket(PuntPacket puntPacket, String award_userno)
	{
		puntPacket.setGetUserno(award_userno);
		puntPacket.setGetTime(Calendar.getInstance());
		puntPacket.merge();
		puntPacket.flush();
	}
	
	@Transactional
	public void updatePuntPacket(PuntPacket puntPacket, String award_userno,short get_status)
	{
		puntPacket.setGetUserno(award_userno);
		puntPacket.setGetStatus(get_status);	 
		puntPacket.setGetTime(Calendar.getInstance());
		puntPacket.merge();
		puntPacket.flush();
	}
	
	public List<PuntPacket> findPuntPacketGrabedList(int packetId)
	{
		TypedQuery<PuntPacket> q = entityManager.createQuery("select o from PuntPacket o where o.getUserno != null and o.packetId = ? order by o.id desc", PuntPacket.class)
				.setParameter(1, packetId);
		return q.getResultList();
	}
	
	@Transactional
	public PuntPacket thankWord(String awardUserno, String thankWords, String packetId)
	{
		PuntPacket puntPacket = findPuntPacketByUsernoAndPacketId(awardUserno, Integer.valueOf(packetId));
		if (puntPacket != null)
		{
			if (!StringUtil.isEmpty(puntPacket.getThankWords()))
				throw new WeixinException(ErrorCode.THANKS_WORDS_EXISTS);
				
			puntPacket.setThankWords(thankWords);
			puntPacket.merge();
		} else
		{
			throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
		}
		return puntPacket;
	}
	
	public PuntPacket findPuntPacketByUsernoAndPacketId(String awardUserno, int packetId)
	{
		TypedQuery<PuntPacket> q = entityManager.createQuery("select o from PuntPacket o where o.getUserno = ? and o.packetId = ? ", PuntPacket.class)
				.setParameter(1, awardUserno).setParameter(2, packetId);
		return q.getSingleResult();
	}
	
	public List<PuntPacket> findPuntPacketByUserno(String userno)
	{
		TypedQuery<PuntPacket> q = entityManager.createQuery("select o from PuntPacket o where o.getUserno = ? order by o.getTime desc", PuntPacket.class)
				.setParameter(1, userno);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Packet> findOneNotAawardPart(String packet_id) {
		String sql = "select * from packet p where p.id = ? for update";
		return entityManager.createNativeQuery(sql, Packet.class).setParameter(1, packet_id).getResultList();
    }
	
	@SuppressWarnings("unchecked")
	public List<PuntPacket> findByGetUserno(String getUserno,String packet_id) {
		String sql = "SELECT * FROM punt_packet WHERE get_userno = ? AND packet_id = ?";
		return entityManager.createNativeQuery(sql, PuntPacket.class).setParameter(1, getUserno).setParameter(2, packet_id).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<PuntPacket> findLeftParts(String packet_id) {
    	String sql = "SELECT * FROM punt_packet WHERE get_userno IS NULL AND packet_id = ?";
    	return entityManager.createNativeQuery(sql, PuntPacket.class).setParameter(1, packet_id).getResultList();
    }
	
	@SuppressWarnings("unchecked")
	public List<PuntPacket> findSinglePuntPart(String packet_id) {
    	String sql = "SELECT * FROM punt_packet WHERE get_userno IS NULL AND packet_id = ? limit 1";
    	return entityManager.createNativeQuery(sql, PuntPacket.class).setParameter(1, packet_id).getResultList();
    }
}
