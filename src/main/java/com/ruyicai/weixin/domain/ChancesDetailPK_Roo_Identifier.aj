// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.ChancesDetailPK;
import javax.persistence.Embeddable;

privileged aspect ChancesDetailPK_Roo_Identifier {
    
    declare @type: ChancesDetailPK: @Embeddable;
    
    public ChancesDetailPK.new(String linkUserno, String joinUserno, String orderid) {
        super();
        this.linkUserno = linkUserno;
        this.joinUserno = joinUserno;
        this.orderid = orderid;
    }

    private ChancesDetailPK.new() {
        super();
    }

    public String ChancesDetailPK.getLinkUserno() {
        return linkUserno;
    }
    
    public String ChancesDetailPK.getJoinUserno() {
        return joinUserno;
    }
    
    public String ChancesDetailPK.getOrderid() {
        return orderid;
    }
    
}
