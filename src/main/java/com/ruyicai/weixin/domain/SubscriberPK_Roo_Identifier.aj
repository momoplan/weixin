// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.SubscriberPK;
import javax.persistence.Embeddable;

privileged aspect SubscriberPK_Roo_Identifier {
    
    declare @type: SubscriberPK: @Embeddable;
    
    public SubscriberPK.new(String userno, String weixinno) {
        super();
        this.userno = userno;
        this.weixinno = weixinno;
    }

    private SubscriberPK.new() {
        super();
    }

    public String SubscriberPK.getUserno() {
        return userno;
    }
    
    public String SubscriberPK.getWeixinno() {
        return weixinno;
    }
    
}
