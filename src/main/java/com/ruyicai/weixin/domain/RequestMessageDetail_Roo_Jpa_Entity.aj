// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.RequestMessageDetail;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

privileged aspect RequestMessageDetail_Roo_Jpa_Entity {
    
    declare @type: RequestMessageDetail: @Entity;
    
    @Version
    @Column(name = "version")
    private Integer RequestMessageDetail.version;
    
    public Integer RequestMessageDetail.getVersion() {
        return this.version;
    }
    
    public void RequestMessageDetail.setVersion(Integer version) {
        this.version = version;
    }
    
}
