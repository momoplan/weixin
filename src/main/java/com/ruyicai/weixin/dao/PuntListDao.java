package com.ruyicai.weixin.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.PuntList;

@Component
public class PuntListDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	public List<PuntList> findPuntListGrabedList(int puntId)
	{
		TypedQuery<PuntList> q = entityManager.createQuery("select o from PuntList o where o.puntId = ? ", PuntList.class)
				.setParameter(1, puntId);
		return q.getResultList();
	}
	
	@Transactional
	public PuntList merge(PuntList punt, int orderprizeamt)
	{
		punt.setOrderprizeamt(orderprizeamt);
		punt.merge();
		punt.flush();
		return punt;
	}
	
	public List<PuntList> findPuntListNotPrized(String opentime)
	{
		TypedQuery<PuntList> q = entityManager.createQuery("select o from PuntList o where DATE_FORMAT(o.opentime,'%Y-%m-%d') = ? and o.orderprizeamt is null ", PuntList.class)
				.setParameter(1, opentime);
		return q.getResultList();
	}
	
	@Transactional
	public PuntList createPuntList(String batchcode, Calendar cal_open, String betcode, int puntId, String orderId)
	{
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
	public PuntList getBetMoeny(String opentime)
	{
		String sql = "SELECT b.get_userno,sum(a.orderprizeamt) total_money FROM `punt_list` a inner join punt_packet b on a.punt_id = b.id WHERE .a.orderprizeamt is not null and a.orderprizeamt > 0 and a.opentime = '"+opentime+"' group by b.get_userno";
		PuntList pList = new PuntList();
		List<?> lst = entityManager.createNativeQuery(sql).getResultList();
		System.out.println(lst.get(0));
		return pList;
	}
}
