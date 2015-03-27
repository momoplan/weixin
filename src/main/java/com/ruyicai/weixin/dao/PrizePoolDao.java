package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.NumAction;
import com.ruyicai.weixin.domain.PrizePool;


@Component
public class PrizePoolDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public PrizePool createPrizePool(String lotno , String  batchcode,int prize,int status) {
        PrizePool prizePool = new PrizePool();
        prizePool.setLotno(lotno);
        prizePool.setBatchcode(batchcode);
        prizePool.setPrize(prize);
        prizePool.setStatus(status);       
        Calendar cal = Calendar.getInstance();
        prizePool.setCreatetime(cal);        
        prizePool.persist();
        return prizePool;
    }
    
    
    @Transactional
    public String getTotalPrize(String lotno) {
        String sql = "SELECT SUM(PRIZE) FROM prize_pool  where lotno = '"+lotno+"' and status = 0";
        return entityManager.createNativeQuery(sql).getSingleResult().toString();
       
      
//       return  entityManager.createQuery(sql, Long.class).setParameter("lotno", lotno).getSingleResult().intValue();
//        return entityManager.createNativeQuery("SELECT SUM(Prize) FROM PrizePool o WHERE lotno = ? AND status = ?", Long.class).setParameter(1, lotno).setParameter(2, 0).getSingleResult();

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
                "SELECT * FROM num_action where  batchcode = '" + batchcode + "' and award = '0' and lottype = '"+lottype+"'", NumAction.class).getResultList();
        return q;
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    public List<NumAction> findHaveBet(String userno,String batchcode,String lottype) {
        String sql = "select * from num_action p where p.userno = ? and p.batchcode = ? and p.lottype = ?";
        return entityManager.createNativeQuery(sql, NumAction.class).setParameter(1, userno).setParameter(2, batchcode).setParameter(3, lottype).getResultList();
    }
    
   
}
