package com.ruyicai.weixin.dao;

import java.util.List;

import com.ruyicai.weixin.consts.Const;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	
	public List<PuntPacket> findPuntPacketGrabedList(int packetId)
	{
		TypedQuery<PuntPacket> q = entityManager.createQuery("select o from PuntPacket o where o.getUserno != null and o.packetId = ? ", PuntPacket.class)
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
		TypedQuery<PuntPacket> q = entityManager.createQuery("select o from PuntPacket o where o.getUserno = ? ", PuntPacket.class)
				.setParameter(1, userno);
		return q.getResultList();
	}
	
//	public List<PuntPacket> findExpiredDatePuntPacket()
//	{		
//		String strSQL = "SELECT * FROM `punt_packet` where get_userno is null and createtime <= date_sub(NOW(), interval "+ Const.WX_RETURN_DAY +" day) ;";
//		@SuppressWarnings("unchecked")
//		List<PuntPacket> q = entityManager.createNativeQuery(strSQL).getResultList();
//		return q;
//	}
}
