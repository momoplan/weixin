package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.CaseLotUserinfo;

@Component
public class CaseLotUserInfoDao {

    @PersistenceContext
    private EntityManager entityManager;


    
    @Transactional
    public List<CaseLotUserinfo> findCaseLotUserinfo(String openid,String userno,String nickname) {
        String sql = "SELECT * FROM case_lot_userinfo where orderid = 'HM00002' ";
        if(null != openid && !openid.equals(""))
            sql +=" AND openid = '"+openid+"'";
        
        if(null != userno && !userno.equals(""))
            sql +=" AND userno = '"+userno+"'";
        
        if(null != nickname && !nickname.equals(""))
            sql +=" AND nickname LIKE '%"+nickname+"%'";
            
        @SuppressWarnings("unchecked")
        List<CaseLotUserinfo> q = entityManager.createNativeQuery(sql, CaseLotUserinfo.class).getResultList();
        return q;
    }
}
