package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.NumAction;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.SubscriberInfo;
import com.ruyicai.weixin.util.StringUtil;


@Component
public class NumActionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public NumAction createNumAction(String userno, String  batchcode,String  betcode,String lottype) {
        NumAction numAction = new NumAction();
        numAction.setUserno(userno);    
        numAction.setBatchcode(batchcode);
        numAction.setAward("0");
        numAction.setLottype(lottype);
        numAction.setBetcode(betcode);
        Calendar cal = Calendar.getInstance();
        numAction.setCreatetime(cal);        
        numAction.persist();
        return numAction;
    }
    
 
    
    @Transactional
    public int updateAward(int id ,int award){
        
            
       String sql = "UPDATE num_action SET award = '"+award+"' WHERE action_id = "+id;  
       System.out.println("sql:"+sql);
       return entityManager.createNativeQuery(sql).executeUpdate();   
    }
    
    @Transactional
    public List<NumAction> findNumActionByUserno(String userno) {
        @SuppressWarnings("unchecked")
        List<NumAction> q = entityManager.createNativeQuery(
                "SELECT * FROM num_action where  userno = '" + userno + "'", NumAction.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<NumAction> findNumActionByBatchcode(String batchcode,String lottype) {
        @SuppressWarnings("unchecked")
        List<NumAction> q = entityManager.createNativeQuery(
                "SELECT * FROM num_action where  userno = '" + batchcode + "' and award = '0' and lottype = '"+lottype+"'", NumAction.class).getResultList();
        return q;
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    public List<NumAction> findHaveBet(String userno,String batchcode,String lottype) {
        String sql = "select * from num_action p where p.userno = ? and p.batchcode = ? and p.lottype = ?";
        return entityManager.createNativeQuery(sql, NumAction.class).setParameter(1, userno).setParameter(2, batchcode).setParameter(3, lottype).getResultList();
    }
    
    @Transactional
    public MoneyEnvelope createMoneyEnvelope(String actionID,String userno, int parts,int money, int expire_date, Calendar packet_exr_start_date,Calendar packet_exr_end_date) {
        MoneyEnvelope moneyEnvelope = new MoneyEnvelope();
        moneyEnvelope.setUserno(userno);
        moneyEnvelope.setChannelName(actionID);
        moneyEnvelope.setMoney(money);
        moneyEnvelope.setActionStatus(1);
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
