// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.Subscriber;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

privileged aspect Subscriber_Roo_Jpa_Entity {
    
    declare @type: Subscriber: @Entity;
    
    @Version
    @Column(name = "version")
    private Integer Subscriber.version;
    
    public Integer Subscriber.getVersion() {
        return this.version;
    }
    
    public void Subscriber.setVersion(Integer version) {
        this.version = version;
    }
    
}
