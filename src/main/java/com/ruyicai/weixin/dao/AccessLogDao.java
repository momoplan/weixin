package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.AccessLog;
import com.ruyicai.weixin.domain.ChannelPacket;

@Component
public class AccessLogDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public AccessLog createAccessLog(String openid, String packet_id) {
        AccessLog accessLog = new AccessLog();
        accessLog.setOpenid(openid);
        accessLog.setPacketId(packet_id);        
        accessLog.persist();
        return accessLog;
    }
    
    @Transactional
    public List<AccessLog> findAccessLogByOpenid(String openid) {
        @SuppressWarnings("unchecked")
        List<AccessLog> q = entityManager.createNativeQuery("SELECT * FROM Access_log where  openid = '"+openid + "' ORDER BY id DESC LIMIT 1 ", AccessLog.class).getResultList();
        return q;
    }
}
