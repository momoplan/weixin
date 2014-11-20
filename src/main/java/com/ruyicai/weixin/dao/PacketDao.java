package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.domain.Packet;

@Component
public class PacketDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Packet createPacket(String openid, String packetUserno, int parts, int punts, String greetings) {
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

    @Transactional
    public Packet createPacket(String openid, String packetUserno, int parts, int punts, String greetings, int status,
            String Status_memo, String award_userno) {
        Packet packet = new Packet();
        packet.setOpenid(openid);
        packet.setPacketUserno(packetUserno);
        packet.setTotalPersons(parts);
        packet.setTotalPunts(punts);
        packet.setRealParts(parts);
        packet.setGreetings(greetings);
        packet.setCreatetime(Calendar.getInstance());
        packet.setStatus(status);
        packet.setStatusMemo(Status_memo);
        packet.setAwardUserno(award_userno);

        packet.persist();
        return packet;
    }

    public List<Packet> findPacketListByUserno(String userno) {
        TypedQuery<Packet> q = entityManager.createQuery(
                "select o from Packet o where o.packetUserno = ? order by o.id desc", Packet.class).setParameter(1,
                userno);
        return q.getResultList();
    }

//    public List<Packet> findPacketListByAwardUserno(String userno) {
//        // TypedQuery<Packet> q =
//        // entityManager.createQuery("select o from Packet o where o.awardUserno = ? order by o.id desc",
//        // Packet.class)
//        // .setParameter(1, userno);
//        //
//        // return q.getResultList();
//
//        String sql = "select * from packet  where award_userno = '" + userno + "' AND status = 1000";
//        @SuppressWarnings("unchecked")
//        List<Packet> q = entityManager.createNativeQuery(sql, Packet.class).getResultList();
//
//        return q;
//    }
    
    public List<Packet> findPacketListByAwardUserno(String userno) {
        // TypedQuery<Packet> q =
        // entityManager.createQuery("select o from Packet o where o.awardUserno = ? order by o.id desc",
        // Packet.class)
        // .setParameter(1, userno);
        //
        // return q.getResultList();

        String sql = "select * from packet  where award_userno = '" + userno + "' AND status = 1000 AND total_punts = 20 AND real_parts = 20";
        @SuppressWarnings("unchecked")
        List<Packet> q = entityManager.createNativeQuery(sql, Packet.class).getResultList();

        return q;
    }
    

    
    public List<Packet> findGetPacketByAwardUserno(String userno) {

//        String sql = "SELECT a.* FROM packet a  inner join punt_packet b on a.id = b.packet_id where ((a.status = 1000 and a.total_punts <> 3 AND a.real_parts <> 3) or a.greetings like '%20141001activity%') and b.get_userno = '"+userno+"'";
        String sql = "SELECT a.* FROM packet a  inner join punt_packet b on a.id = b.packet_id where (a.status = 1000  or a.greetings like '%20141001activity%') and b.get_userno = '"+userno+"'";
        @SuppressWarnings("unchecked")
        List<Packet> q = entityManager.createNativeQuery(sql, Packet.class).getResultList();

        return q;

    }

    public Packet findPacket(Integer id, Integer pageIndex) {

        if (id == null)
            return null;

        pageIndex = (30 * (pageIndex - 1));

        if (pageIndex != 0) {

        }

        String sql = "select * from packet  where id = " + id + " AN order by id desc LIMIT 30";
        Packet q = (Packet) entityManager.createNativeQuery(sql, Packet.class).getSingleResult();

        return q;
    }

    public List<Packet> findPacketListByUserno(String userno, int pageIndex) {
        pageIndex = (30 * (pageIndex - 1));

        @SuppressWarnings("unchecked")
        List<Packet> q = entityManager.createNativeQuery(
                "select * from packet  where packet_userno = '" + userno + "'  or award_userno='"+userno+"' order by id desc LIMIT " + pageIndex
                        + ",30", Packet.class).getResultList();

        return q;
    }

    public List<Packet> findPacketListByUsernoAndPacketID(String userno, String packet_id) {
        @SuppressWarnings("unchecked")
        List<Packet> q = entityManager.createNativeQuery(
                "SELECT * FROM packet where packet_userno = '" + userno + "' and id = " + packet_id + " FOR UPDATE",
                Packet.class).getResultList();
        return q;
    }

    public List<Packet> findReturnPacketList() {
        @SuppressWarnings("unchecked")
        List<Packet> q = entityManager.createNativeQuery(
                "SELECT * FROM packet where createtime <= date_sub(NOW(), interval 15 day) and return_punts is null AND packet_userno <> '"+Const.SYS_USERNO+"'",
                Packet.class).getResultList();
        return q;
    }
}
