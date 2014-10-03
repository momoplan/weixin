package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.PuntList;
import com.ruyicai.weixin.domain.PuntPacket;

@Component
public class PuntListDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<PuntList> findPuntListGrabedList(int puntId) {
        // TypedQuery<PuntList> q =
        // entityManager.createQuery("select o from PuntList o where o.puntId = ?  order by col_0_0 desc",
        // PuntList.class)
        // .setParameter(1, puntId);
        //
        // return q.getResultList();
        @SuppressWarnings("unchecked")
        List<PuntList> q = entityManager.createNativeQuery(
                "SELECT * FROM punt_list WHERE punt_id = " + puntId + " ORDER BY id DESC", PuntList.class)
                .getResultList();
        return q;
    }

    @Transactional
    public PuntList merge(PuntList punt, int orderprizeamt) {
        punt.setOrderprizeamt(orderprizeamt);
        punt.merge();
        punt.flush();
        return punt;
    }

    public List<PuntList> findPuntListNotPrized(String opentime) {
        TypedQuery<PuntList> q = entityManager.createQuery(
                "select o from PuntList o where DATE_FORMAT(o.opentime,'%Y-%m-%d') = ? and o.orderprizeamt is null ",
                PuntList.class).setParameter(1, opentime);
        return q.getResultList();
    }

    @Transactional
    public PuntList createPuntList(String batchcode, Calendar cal_open, String betcode, int puntId, String orderId) {
        PuntList pList = new PuntList();
        pList.setBatchcode(batchcode);
        pList.setOpentime(cal_open);
        pList.setBetcode(betcode);
        pList.setPuntId(puntId);
        pList.setOrderid(orderId);
        pList.setCreatetime(Calendar.getInstance());
        pList.persist();
        return pList;
    }

    @Transactional
    public PuntList createPuntList(String batchcode, Calendar cal_open, String betcode, int puntId, String orderId,
            int status) {
        PuntList pList = new PuntList();
        pList.setBatchcode(batchcode);
        pList.setOpentime(cal_open);
        pList.setBetcode(betcode);
        pList.setPuntId(puntId);
        pList.setOrderid(orderId);
        pList.setCreatetime(Calendar.getInstance());
        pList.setStatus(status);
        pList.persist();
        return pList;
    }

    @Transactional
    public List<?> getBetMoeny(String opentime) {
        String sql = "SELECT b.get_userno,count(id),sum(a.orderprizeamt) total_money FROM punt_list a inner join punt_packet b on a.punt_id = b.id WHERE a.orderprizeamt is not null and a.orderprizeamt > 0 and a.opentime = '"
                + opentime + "' group by b.get_userno";
        // PuntList pList = new PuntList();
        List<?> lst = entityManager.createNativeQuery(sql).getResultList();
//        System.out.println(lst.get(0));
        return lst;
    }
    
    public  Integer countList(String date) {
        String sql = "select *  from punt_list where status = 1000 and createtime >= '"+date+"'";
        // PuntList pList = new PuntList();      
        @SuppressWarnings("unchecked")
        List<PuntList> lst =   entityManager.createNativeQuery(sql,PuntList.class).getResultList();
        if (null != lst)
//        System.out.println(lst.get(0));
        return lst.size();
        else
            return 0;
    }
    

    
    @Transactional
    public List<PuntList> findPuntListByUserno(String userno) {
        String sql = "SELECT  b.id,b.punt_id,b.betcode,b.orderid,b.batchcode,b.createtime,b.opentime,b.orderprizeamt,c.status from punt_list b INNER JOIN punt_packet a on b.punt_id = a.id INNER JOIN packet c on a.packet_id = c.id where (a.get_userno = '"+userno+"' or c.award_userno = '"+userno+"') and b.status = 1000";
//        String sql = "SELECT  b.* from punt_list b INNER JOIN punt_packet a on b.punt_id = a.id INNER JOIN packet c on a.packet_id = c.id where a.get_userno = '"+userno+"' and b.status = 1000";
        // PuntList pList = new PuntList();
        @SuppressWarnings("unchecked")
        List<PuntList> lst = entityManager.createNativeQuery(sql, PuntList.class).getResultList();
//        System.out.println(lst.get(0));
        return lst;
    }

    public PuntList findPuntListByOrderid(String orderid) {
        TypedQuery<PuntList> q = entityManager.createQuery("select o from PuntList o where o.orderid = ? ",
                PuntList.class).setParameter(1, orderid);
        return q.getSingleResult();
    }
    
    public List<PuntList> findPuntListByUsernoByPage() {
        
        String sql = "select b.* from punt_packet a inner join punt_list b on a.id = b.punt_id inner join packet c on a.packet_id = c.id where b.status = 1000 and b.orderprizeamt > 0 and c.award_userno is null";
        @SuppressWarnings("unchecked")
        List<PuntList> q = entityManager.createNativeQuery(sql, PuntList.class).getResultList();

        return q;
    }
}
