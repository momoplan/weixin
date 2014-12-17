package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.OrderLotInfo;


@Component
public class OrderLotInfoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public OrderLotInfo createOrderlotinfo(String userno, String batchcode, String lotno, String transactionId,String betcode,int amt) {     
        OrderLotInfo orderLotInfo = new OrderLotInfo();
        orderLotInfo.setUserno(userno);
        orderLotInfo.setBatchcode(batchcode);
        orderLotInfo.setLotno(lotno);
        orderLotInfo.setTransactionId(transactionId);
        orderLotInfo.setBetcode(betcode);
        orderLotInfo.setAmt(amt);        
        orderLotInfo.persist();
        return orderLotInfo;
    }
    
    
    @Transactional
    public List<OrderLotInfo> findSubscriberInfoByUserno(String userno,String batchcode) {
        @SuppressWarnings("unchecked")
        List<OrderLotInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM order_lot_info where  userno = '" + userno + "' AND batchcode='"+batchcode+"'", OrderLotInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<OrderLotInfo> findSubscriberInfoByUserno(String userno,String batchcode,String lotno) {
        @SuppressWarnings("unchecked")
        List<OrderLotInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM order_lot_info where  userno = '" + userno + "' AND batchcode='"+batchcode+"' AND lotno = '"+ lotno+"'", OrderLotInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<OrderLotInfo> findSubscriberInfoByTransactionID(String userno,String transid) {
        @SuppressWarnings("unchecked")
        List<OrderLotInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM order_lot_info where  userno = '" + userno + "' AND transaction_id='"+transid+"'", OrderLotInfo.class).getResultList();
        return q;
    }
    
    @Transactional
    public List<OrderLotInfo> findSubscriberInfoByBetcode(String betcode) {
        @SuppressWarnings("unchecked")
        List<OrderLotInfo> q = entityManager.createNativeQuery(
                "SELECT * FROM order_lot_info where betcode LIKE '%"+betcode+"%'", OrderLotInfo.class).getResultList();
        return q;
    }
}