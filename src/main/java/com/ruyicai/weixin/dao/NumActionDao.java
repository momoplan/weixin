package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.NumAction;
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
    public int updatePrize(String  lotno,String award_batchcode){
        
            
       String sql = "UPDATE prize_pool SET status = 1,award_batchcode = '"+award_batchcode+"' WHERE lotno = '"+lotno+"' and status = 0";  
       System.out.println("sql:"+sql);
       return entityManager.createNativeQuery(sql).executeUpdate();   
    }
    
 
    
    @Transactional
    public int updateAward(int id ,int award,String wincode){
        
            
       String sql = "UPDATE num_action SET award = '"+award+"' WHERE action_id = "+id+" AND wincode = '" +wincode+"'";  
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
