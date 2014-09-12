package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.ruyicai.weixin.domain.ChannelPacket;

@Component
public class ChannelPacketDao {
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<ChannelPacket> findChannelPacketListByPacketID(String channel_id)
	{		
		@SuppressWarnings("unchecked")
		List<ChannelPacket> q = entityManager.createNativeQuery("SELECT * FROM channel_packet where  id = "+channel_id + " FOR UPDATE", ChannelPacket.class).getResultList();
		return q;
	}

}
