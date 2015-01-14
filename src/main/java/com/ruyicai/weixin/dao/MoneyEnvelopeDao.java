package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.util.StringUtil;


@Component
public class MoneyEnvelopeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MoneyEnvelope createMoneyEnvelope(String userno, int parts,int money, int expire_date, Calendar packet_exr_start_date,Calendar packet_exr_end_date) {
        MoneyEnvelope moneyEnvelope = new MoneyEnvelope();
        moneyEnvelope.setUserno(userno);
        moneyEnvelope.setMoney(money);
        moneyEnvelope.setPacketExrStartDate(packet_exr_start_date);
        moneyEnvelope.setPacketExrEndDate(packet_exr_end_date);
        moneyEnvelope.setExireDate(expire_date);
        moneyEnvelope.setActionStatus(0);
        Calendar cal = Calendar.getInstance();
        moneyEnvelope.setCreatetime(cal);
        moneyEnvelope.setParts(parts);    
        moneyEnvelope.persist();
        return moneyEnvelope;
    }
    
    @Transactional
    public MoneyEnvelope createMoneyEnvelope(String actionID,String userno, int parts,int money, int expire_date, Calendar packet_exr_start_date,Calendar packet_exr_end_date) {
        MoneyEnvelope moneyEnvelope = new MoneyEnvelope();
        moneyEnvelope.setUserno(userno);
        moneyEnvelope.setChannelName(actionID);
        moneyEnvelope.setMoney(money);
        moneyEnvelope.setActionStatus(0);
        moneyEnvelope.setPacketExrStartDate(packet_exr_start_date);
        moneyEnvelope.setPacketExrEndDate(packet_exr_end_date);
        moneyEnvelope.setExireDate(expire_date);
        Calendar cal = Calendar.getInstance();
        moneyEnvelope.setCreatetime(cal);
        moneyEnvelope.setParts(parts);    
        moneyEnvelope.persist();
        return moneyEnvelope;
    }
    
    @Transactional
    public int updateMoneyEnvelope(String action_status, String action_id,String packet_exr_end_date,String packet_exr_start_date) {
        String sql_sub = "";
        if (!StringUtil.isEmpty(action_status))
            sql_sub += "  action_status = "+action_status+",";
        if (!StringUtil.isEmpty(packet_exr_start_date))
            sql_sub += "  packet_exr_start_date = '"+packet_exr_start_date+"',";
        if (!StringUtil.isEmpty(packet_exr_end_date))
            sql_sub += "  packet_exr_end_date = '"+packet_exr_end_date+"',";
        
        sql_sub = sql_sub.substring(0, sql_sub.length()-1);
            
       String sql = "UPDATE money_envelope SET "+sql_sub+" WHERE channel_name = '"+action_id+"'";  
       System.out.println("sql:"+sql);
       return entityManager.createNativeQuery(sql).executeUpdate();   
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    public List<MoneyEnvelope> findOneNotAawardPart(String packet_id) {
        String sql = "select * from money_envelope p where p.id = ? for update";
        return entityManager.createNativeQuery(sql, MoneyEnvelope.class).setParameter(1, packet_id).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    public List<MoneyEnvelope> findEnvelopByPacketID(String packet_id) {
        String sql = "select * from money_envelope p where p.id = ?";
        return entityManager.createNativeQuery(sql, MoneyEnvelope.class).setParameter(1, packet_id).getResultList();
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
