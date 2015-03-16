// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.NumAction;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect NumAction_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager NumAction.entityManager;
    
    public static final List<String> NumAction.fieldNames4OrderClauseFilter = java.util.Arrays.asList("");
    
    public static final EntityManager NumAction.entityManager() {
        EntityManager em = new NumAction().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long NumAction.countNumActions() {
        return entityManager().createQuery("SELECT COUNT(o) FROM NumAction o", Long.class).getSingleResult();
    }
    
    public static List<NumAction> NumAction.findAllNumActions() {
        return entityManager().createQuery("SELECT o FROM NumAction o", NumAction.class).getResultList();
    }
    
    public static List<NumAction> NumAction.findAllNumActions(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM NumAction o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, NumAction.class).getResultList();
    }
    
    public static NumAction NumAction.findNumAction(Integer actionId) {
        if (actionId == null) return null;
        return entityManager().find(NumAction.class, actionId);
    }
    
    public static List<NumAction> NumAction.findNumActionEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM NumAction o", NumAction.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<NumAction> NumAction.findNumActionEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM NumAction o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, NumAction.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void NumAction.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void NumAction.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            NumAction attached = NumAction.findNumAction(this.actionId);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void NumAction.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void NumAction.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public NumAction NumAction.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        NumAction merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
