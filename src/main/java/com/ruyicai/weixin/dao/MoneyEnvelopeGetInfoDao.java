package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.MoneyEnvelopeGetInfo;
import com.ruyicai.weixin.domain.SubscriberInfo;


@Component
public class MoneyEnvelopeGetInfoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MoneyEnvelopeGetInfo createMoneyEnvelope(String get_userno, int packet_id,int get_money) {
        MoneyEnvelopeGetInfo moneyEnvelope = new MoneyEnvelopeGetInfo();
        moneyEnvelope.setGetUserno(get_userno);
        moneyEnvelope.setMoney(get_money);
        moneyEnvelope.setEnvelopeId(packet_id);      
        moneyEnvelope.persist();
        return moneyEnvelope;
    }
    
    @Transactional
    public List<SubscriberInfo> findSubscriberInfoByUserno(String userno) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  userno = '" + userno + "'", SubscriberInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<SubscriberInfo> findSubscriberInfoByUserno(String userno,String lot_type) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  userno = '" + userno + "' AND lot_type='"+lot_type+"'", SubscriberInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<SubscriberInfo> findSubscriberByUserno(String userno,String lot_type) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  userno = '" + userno + "' AND lot_type='"+lot_type+"' AND sub_status =1", SubscriberInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<SubscriberInfo> findSubscriberInfoByLottype(String lot_type,String sub_type) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  lot_type = '" + lot_type + "' AND sub_status = 1", SubscriberInfo.class).getResultList();
        return q;
    }

    @Transactional
    public List findLotSubUsers() {
        @SuppressWarnings("unchecked")
        String sql = "SELECT lot_type,count(*) as count_user FROM `subscriber_info` WHERE sub_status = 1 group by lot_type";

        // Query q = entityManager.createNativeQuery(sql, "srsm2");
        Query q = entityManager.createNativeQuery(sql);

        List lst = q.getResultList();
        
        return lst;
    }
}
