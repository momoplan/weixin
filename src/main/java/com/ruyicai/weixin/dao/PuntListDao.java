package com.ruyicai.weixin.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

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
	
}
