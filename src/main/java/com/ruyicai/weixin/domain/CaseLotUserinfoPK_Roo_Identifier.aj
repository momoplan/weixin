// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.CaseLotUserinfoPK;
import javax.persistence.Embeddable;

privileged aspect CaseLotUserinfoPK_Roo_Identifier {
    
    declare @type: CaseLotUserinfoPK: @Embeddable;
    
    public CaseLotUserinfoPK.new(String userno, String orderid) {
        super();
        this.userno = userno;
        this.orderid = orderid;
    }

    private CaseLotUserinfoPK.new() {
        super();
    }

    public String CaseLotUserinfoPK.getUserno() {
        return userno;
    }
    
    public String CaseLotUserinfoPK.getOrderid() {
        return orderid;
    }
    
}
