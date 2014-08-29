package com.ruyicai.weixin.dao;

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
	
}
