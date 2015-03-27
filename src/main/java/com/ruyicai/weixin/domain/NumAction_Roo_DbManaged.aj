// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.NumAction;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

privileged aspect NumAction_Roo_DbManaged {
    
    @Column(name = "userno", length = 8, unique = true)
    @NotNull
    private String NumAction.userno;
    
    @Column(name = "batchcode", length = 50, unique = true)
    @NotNull
    private String NumAction.batchcode;
    
    @Column(name = "award", length = 255)
    private String NumAction.award;
    
    @Column(name = "createtime")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Calendar NumAction.createtime;
    
    @Column(name = "betcode", length = 255)
    private String NumAction.betcode;
    
    @Column(name = "lottype", length = 50)
    private String NumAction.lottype;
    
    @Column(name = "wincode", length = 30)
    private String NumAction.wincode;
    
    public String NumAction.getUserno() {
        return userno;
    }
    
    public void NumAction.setUserno(String userno) {
        this.userno = userno;
    }
    
    public String NumAction.getBatchcode() {
        return batchcode;
    }
    
    public void NumAction.setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }
    
    public String NumAction.getAward() {
        return award;
    }
    
    public void NumAction.setAward(String award) {
        this.award = award;
    }
    
    public Calendar NumAction.getCreatetime() {
        return createtime;
    }
    
    public void NumAction.setCreatetime(Calendar createtime) {
        this.createtime = createtime;
    }
    
    public String NumAction.getBetcode() {
        return betcode;
    }
    
    public void NumAction.setBetcode(String betcode) {
        this.betcode = betcode;
    }
    
    public String NumAction.getLottype() {
        return lottype;
    }
    
    public void NumAction.setLottype(String lottype) {
        this.lottype = lottype;
    }
    
    public String NumAction.getWincode() {
        return wincode;
    }
    
    public void NumAction.setWincode(String wincode) {
        this.wincode = wincode;
    }
    
}
