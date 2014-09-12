package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.ChannelPacketUserInfo;
import com.ruyicai.weixin.domain.PuntPacket;

@Component
public class ChannelPacketUserInfoDao {
	@PersistenceContext
	private EntityManager entityManager;
	
	
	public ChannelPacketUserInfo createChannelPacketUserInfo(String get_userno, int get_money, String order_id,int channel_id)
	{
		ChannelPacketUserInfo channelPacketUserInfo = new ChannelPacketUserInfo();
		channelPacketUserInfo.setGetUserno(get_userno);
		channelPacketUserInfo.setGetMoney(get_money);
		channelPacketUserInfo.setOrderId(order_id);	
		channelPacketUserInfo.setChannelId(channel_id); 
		channelPacketUserInfo.persist();
		return channelPacketUserInfo;
	}
	
	public ChannelPacketUserInfo findChannelPacketUserInfoByUsernoAndPacketId(String awardUserno, int channelID)
	{
		TypedQuery<ChannelPacketUserInfo> q = entityManager.createQuery("select o from ChannelPacketUserInfo o where o.getUserno = ? and o.channelId = ? ", ChannelPacketUserInfo.class)
				.setParameter(1, awardUserno).setParameter(2, channelID);
		return q.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ChannelPacketUserInfo> findByGetUserno(String getUserno,String channel_id) {
		String sql = "SELECT * FROM channel_packet_user_info WHERE get_userno = ? AND channel_id = ?";
		return entityManager.createNativeQuery(sql, PuntPacket.class).setParameter(1, getUserno).setParameter(2, channel_id).getResultList();
	}
}
