// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.ActivityDetail;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ActivityDetail_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager ActivityDetail.entityManager;
    
    public static final EntityManager ActivityDetail.entityManager() {
        EntityManager em = new ActivityDetail().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long ActivityDetail.countActivityDetails() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ActivityDetail o", Long.class).getSingleResult();
    }
    
    public static List<ActivityDetail> ActivityDetail.findAllActivityDetails() {
        return entityManager().createQuery("SELECT o FROM ActivityDetail o", ActivityDetail.class).getResultList();
    }
    
    public static ActivityDetail ActivityDetail.findActivityDetail(Long id) {
        if (id == null) return null;
        return entityManager().find(ActivityDetail.class, id);
    }
    
    public static List<ActivityDetail> ActivityDetail.findActivityDetailEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ActivityDetail o", ActivityDetail.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void ActivityDetail.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void ActivityDetail.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            ActivityDetail attached = ActivityDetail.findActivityDetail(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void ActivityDetail.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void ActivityDetail.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public ActivityDetail ActivityDetail.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ActivityDetail merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
