package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityResult;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.SubscriberInfo;

// @SqlResultSetMapping(name="srsm2",   
//      entities={@EntityResult(entityClass=SubscriberInfo.class)},   
//          columns={@ColumnResult(name="lot_type"),@ColumnResult(name="count_user")}   
// )




@NamedNativeQuery(
        name = "ReturnOrderListWithFullScalarType",
        query = "SELECT lot_type,count(*) as sub_type FROM subscriber_info group by lot_type", resultSetMapping = "ReturnOrderListWithFullScalarType"
        )




@SqlResultSetMapping(
        name = "ReturnOrderListWithFullScalarType",
        entities = {},
        columns ={
                @ColumnResult(name = "lot_type"),
                @ColumnResult(name = "sub_type")
        }
        )

@Component
public class SubscriberInfoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public SubscriberInfo createOrEditSubscriberInfo(String userno, String lot_type, String sub_status, String sub_type) {

        List<SubscriberInfo> lstSubscriberInfo = findSubscriberInfoByUserno(userno,lot_type);
        if (lstSubscriberInfo.size() == 0) {
            SubscriberInfo subInfo = new SubscriberInfo();
            subInfo.setUserno(userno);
            subInfo.setSubStatus(Integer.parseInt(sub_status));
            subInfo.setSubType(Integer.parseInt(sub_type));
            subInfo.setLotType(lot_type);
            subInfo.persist();
            return subInfo;
        } else {
            SubscriberInfo subInfo = lstSubscriberInfo.get(0);

            subInfo.setSubStatus(Integer.parseInt(sub_status));
            subInfo.setSubType(Integer.parseInt(sub_type));
            subInfo.setLotType(lot_type);
            subInfo.merge();
            return subInfo;

        }

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
    public List findLotSubUsers() {
        @SuppressWarnings("unchecked")
        String sql = "SELECT lot_type,count(*) as count_user FROM `subscriber_info` group by lot_type";

        // Query q = entityManager.createNativeQuery(sql, "srsm2");
        Query q = entityManager.createNativeQuery(sql);

        List lst = q.getResultList();
        
        return lst;
    }
}
