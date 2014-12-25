package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.AsiaFootballPacketInfo;
import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.SubscriberInfo;

@Component
public class AsiaFootballPacketInfoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public AsiaFootballPacketInfo createMoneyEnvelope(String get_userno, int money, String packetUserno) {
//        List<AsiaFootballPacketInfo> lstAsiaFootballPacketInfo = getAsiaFootballPacketInfoByUserno(get_userno);
//        if (lstAsiaFootballPacketInfo.size() > 0)
//            return lstAsiaFootballPacketInfo.get(0);
//        else {

            AsiaFootballPacketInfo moneyEnvelope = new AsiaFootballPacketInfo();
            moneyEnvelope.setGetUserno(get_userno);
            moneyEnvelope.setGetMoney(money);
            moneyEnvelope.setAwardMoney(0);
            moneyEnvelope.setPacketUserno(packetUserno);
            Calendar createtime = Calendar.getInstance();
            moneyEnvelope.setCreatetime(createtime);
            moneyEnvelope.persist();
            return moneyEnvelope;
//        }
    }

    @Transactional
    public Long getTotalGetUsers(int packet_id) {
        // String sql =
        // "SELECT COUNT(*) from asia_football_packet_info where packet_userno = '"+packet_userno+"'";
        return entityManager.createQuery("SELECT COUNT(o) FROM AsiaFootballPacketInfo o WHERE id = ?", Long.class)
                .setParameter(1, packet_id).getSingleResult();

    }
    
    @Transactional
    public Long getTotalGetUsers() {
        // String sql =
        // "SELECT COUNT(*) from asia_football_packet_info where packet_userno = '"+packet_userno+"'";
        return entityManager.createQuery("SELECT COUNT(o) FROM AsiaFootballPacketInfo o", Long.class).getSingleResult();

    }

    @Transactional
    public List<AsiaFootballPacketInfo> getAsiaFootballPacketInfoByUserno(String userno) {
        // String sql =
        // "SELECT COUNT(*) from asia_football_packet_info where packet_userno = '"+packet_userno+"'";
        return entityManager
                .createQuery("SELECT o FROM AsiaFootballPacketInfo o WHERE packetUserno = ?",
                        AsiaFootballPacketInfo.class).setParameter(1, userno).getResultList();

    }

    
    @Transactional
    public List<AsiaFootballPacketInfo> getAsiaFootballPacketInfoByGetUserno(String userno) {
        // String sql =
        // "SELECT COUNT(*) from asia_football_packet_info where packet_userno = '"+packet_userno+"'";
        return entityManager
                .createQuery("SELECT o FROM AsiaFootballPacketInfo o WHERE getUserno = ?",
                        AsiaFootballPacketInfo.class).setParameter(1, userno).getResultList();

    }


    @Transactional
    public Long getTotalGetUsers(String packet_userno) {
        // String sql =
        // "SELECT COUNT(*) from asia_football_packet_info where packet_userno = '"+packet_userno+"'";
        return entityManager
                .createQuery("SELECT COUNT(o) FROM AsiaFootballPacketInfo o WHERE packetUserno = ?", Long.class)
                .setParameter(1, packet_userno).getSingleResult();

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<MoneyEnvelope> findOneNotAawardPart(String packet_id) {
        String sql = "select * from money_envelope p where p.id = ? for update";
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
    public List<SubscriberInfo> findSubscriberInfoByUserno(String userno, String lot_type) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  userno = '" + userno + "' AND lot_type='" + lot_type + "'",
                SubscriberInfo.class).getResultList();
        return q;
    }

    @Transactional
    public List<SubscriberInfo> findSubscriberByUserno(String userno, String lot_type) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  userno = '" + userno + "' AND lot_type='" + lot_type
                        + "' AND sub_status =1", SubscriberInfo.class).getResultList();
        return q;
    }

    @Transactional
    public List<SubscriberInfo> findSubscriberInfoByLottype(String lot_type, String sub_type) {
        @SuppressWarnings("unchecked")
        List<SubscriberInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM Subscriber_Info where  lot_type = '" + lot_type + "' AND sub_status = 1",
                SubscriberInfo.class).getResultList();
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
