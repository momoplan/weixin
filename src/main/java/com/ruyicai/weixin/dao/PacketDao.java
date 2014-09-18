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
	
    
    public  Packet findPacket(Integer id,Integer pageIndex) {
        
        if (id == null) return null;
        
        pageIndex = (10 * (pageIndex-1));
        
        if(pageIndex != 0){
            
            
        }
        
        String sql = "select * from packet  where id = "+id+" AN order by id desc LIMIT 10";
        Packet q = (Packet)entityManager.createNativeQuery(sql, Packet.class).getSingleResult();
        
        return q;        
    }
	
	public List<Packet> findPacketListByUserno(String userno,int pageIndex)
	{		
		pageIndex = (10 * (pageIndex-1));
		
		@SuppressWarnings("unchecked")
		List<Packet> q = entityManager.createNativeQuery("select * from packet  where packet_userno = '"+userno+"' order by id desc LIMIT "+pageIndex+",10", Packet.class).getResultList();
		
		return q;
	}
	
	
	public List<Packet> findPacketListByUsernoAndPacketID(String userno,String packet_id)
	{		
		@SuppressWarnings("unchecked")
		List<Packet> q = entityManager.createNativeQuery("SELECT * FROM packet where packet_userno = '"+userno+"' and id = "+packet_id + " FOR UPDATE", Packet.class).getResultList();
		return q;
	}
	
	public List<Packet> findReturnPacketList()
	{
		@SuppressWarnings("unchecked")
		List<Packet> q = entityManager.createNativeQuery("SELECT * FROM packet where createtime <= date_sub(NOW(), interval 24 hour) and return_punts is null", Packet.class).getResultList();
		return q;
	}
}
