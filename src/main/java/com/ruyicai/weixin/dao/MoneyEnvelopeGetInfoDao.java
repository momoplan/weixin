package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.MoneyEnvelopeGetInfo;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.domain.SubscriberInfo;


@Component
public class MoneyEnvelopeGetInfoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MoneyEnvelopeGetInfo createMoneyEnvelopeGetInfo(String get_userno, int packet_id,int get_money) {
        MoneyEnvelopeGetInfo moneyEnvelope = new MoneyEnvelopeGetInfo();
       
        moneyEnvelope.setGetUserno(get_userno);
        moneyEnvelope.setMoney(get_money);
        moneyEnvelope.setEnvelopeId(packet_id); 
        Calendar cal = Calendar.getInstance();
        moneyEnvelope.setCreatetime(cal);
       
        moneyEnvelope.persist();
        return moneyEnvelope;
    }
    
    @Transactional
    public MoneyEnvelopeGetInfo createMoneyEnvelopeGetInfo(int packet_id,int get_money,int expire_days) {
        MoneyEnvelopeGetInfo moneyEnvelope = new MoneyEnvelopeGetInfo();
        moneyEnvelope.setMoney(get_money);
        moneyEnvelope.setEnvelopeId(packet_id); 
        moneyEnvelope.setExpireDays(expire_days);
        moneyEnvelope.setExpireStatus(0);
        Calendar cal = Calendar.getInstance();
        moneyEnvelope.setCreatetime(cal);
        moneyEnvelope.persist();
        return moneyEnvelope;
    }
    
    
    @Transactional
    public List<MoneyEnvelopeGetInfo> findLeftMoney(String packet_id) {
        @SuppressWarnings("unchecked")
        List<MoneyEnvelopeGetInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM money_envelope_get_Info where  get_userno is null and envelope_id = " + packet_id , MoneyEnvelopeGetInfo.class).getResultList();
        return q;
    }
    
    
    @Transactional
    public List<MoneyEnvelopeGetInfo> findUserMoney(String userno) {
        @SuppressWarnings("unchecked")
        List<MoneyEnvelopeGetInfo> q = entityManager.createNativeQuery(
                "SELECT id,get_userno,money,envelope_id,DATE_ADD(get_time,  INTERVAL expire_days DAY) as createtime,get_time,expire_days,expire_status FROM `money_envelope_get_info` where expire_status = 0 AND DATE_ADD(get_time,  INTERVAL expire_days DAY) >= NOW() and get_userno = '"+userno+"' ", MoneyEnvelopeGetInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<MoneyEnvelope> findEnvelopeExpired(String packet_id) {
        @SuppressWarnings("unchecked")
        List<MoneyEnvelope> q = entityManager.createNativeQuery(
                "SELECT * FROM `money_envelope` where  packet_exr_end_date >= NOW() and id = "+packet_id, MoneyEnvelope.class).getResultList();
        return q;
    }
    
    @Transactional
    public int DeductUserMoney(String getinfo_ids) {
       String sql = "UPDATE money_envelope_get_info SET  expire_status = 1 WHERE id IN ("+getinfo_ids+") AND expire_status = 0";       
       return entityManager.createNativeQuery(sql).executeUpdate();   
    }
        
    @SuppressWarnings("unchecked")
    public List<MoneyEnvelopeGetInfo> findByGetUserno(String getUserno, String packet_id) {
        String sql = "SELECT * FROM money_envelope_get_info WHERE get_userno = ? AND envelope_id = ?";
        return entityManager.createNativeQuery(sql, MoneyEnvelopeGetInfo.class).setParameter(1, getUserno)
                .setParameter(2, packet_id).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<MoneyEnvelopeGetInfo> findByGetUsernoAndActionID(String getUserno,String action_id) {
        String sql = "SELECT a.* FROM money_envelope_get_info a INNER JOIN money_envelope b ON a.envelope_id = b.id WHERE a.get_userno = ? AND b.channel_name = ?";
        return entityManager.createNativeQuery(sql, MoneyEnvelopeGetInfo.class).setParameter(1, getUserno)
                .setParameter(2, action_id).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<MoneyEnvelopeGetInfo> findByGetUserno(String getUserno) {
        String sql = "SELECT * FROM money_envelope_get_info WHERE get_userno = ? ";
        return entityManager.createNativeQuery(sql, MoneyEnvelopeGetInfo.class).setParameter(1, getUserno)
                .getResultList();
    }
    
//    @Transactional
//    public List<SubscriberInfo> findSubscriberInfoByUserno(String userno) {
//        @SuppressWarnings("unchecked")
//        List<SubscriberInfo> q = entityManager.createNativeQuery(
//                "SELECT * FROM Subscriber_Info where  userno = '" + userno + "'", SubscriberInfo.class).getResultList();
//        return q;
//    }
    
    
    @SuppressWarnings("unchecked")
    public List<MoneyEnvelopeGetInfo> findSinglePuntPart(String packet_id) {
        String sql = "SELECT * FROM money_envelope_get_info WHERE get_userno IS NULL AND envelope_id = ? limit 1";
        return entityManager.createNativeQuery(sql, MoneyEnvelopeGetInfo.class).setParameter(1, packet_id).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<MoneyEnvelopeGetInfo> findMoneyEnveList(String packet_id) {
        String sql = " SELECT a.money,b.nickname,b.headimgurl,FROM_UNIXTIME(UNIX_TIMESTAMP(a.get_time),'%Y-%m-%d %H:%i:%s') FROM money_envelope_get_info a LEFT  JOIN case_lot_userinfo b on a.get_userno = b.userno WHERE get_userno IS NOT NULL AND orderid = 'HM00002' AND envelope_id = ? ";
//        return entityManager.createNativeQuery(sql, MoneyEnvelopeGetInfo.class).setParameter(1, packet_id).getResultList();  
        // Query q = entityManager.createNativeQuery(sql, "srsm2");
        Query q = entityManager.createNativeQuery(sql).setParameter(1, packet_id);
        @SuppressWarnings("rawtypes")
        List lst = q.getResultList();
        
        return lst;
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
