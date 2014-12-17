// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.MoneyEnvelopeGetInfo;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

privileged aspect MoneyEnvelopeGetInfo_Roo_DbManaged {
    
    @Column(name = "get_userno", length = 255)
    private String MoneyEnvelopeGetInfo.getUserno;
    
    @Column(name = "money")
    private Integer MoneyEnvelopeGetInfo.money;
    
    @Column(name = "envelope_id")
    private Integer MoneyEnvelopeGetInfo.envelopeId;
    
    @Column(name = "createtime")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Calendar MoneyEnvelopeGetInfo.createtime;
    
    @Column(name = "get_time")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Calendar MoneyEnvelopeGetInfo.getTime;
    
    @Column(name = "expire_days")
    @NotNull
    private Integer MoneyEnvelopeGetInfo.expireDays;
    
    @Column(name = "expire_status")
    @NotNull
    private Integer MoneyEnvelopeGetInfo.expireStatus;
    
    public String MoneyEnvelopeGetInfo.getGetUserno() {
        return getUserno;
    }
    
    public void MoneyEnvelopeGetInfo.setGetUserno(String getUserno) {
        this.getUserno = getUserno;
    }
    
    public Integer MoneyEnvelopeGetInfo.getMoney() {
        return money;
    }
    
    public void MoneyEnvelopeGetInfo.setMoney(Integer money) {
        this.money = money;
    }
    
    public Integer MoneyEnvelopeGetInfo.getEnvelopeId() {
        return envelopeId;
    }
    
    public void MoneyEnvelopeGetInfo.setEnvelopeId(Integer envelopeId) {
        this.envelopeId = envelopeId;
    }
    
    public Calendar MoneyEnvelopeGetInfo.getCreatetime() {
        return createtime;
    }
    
    public void MoneyEnvelopeGetInfo.setCreatetime(Calendar createtime) {
        this.createtime = createtime;
    }
    
    public Calendar MoneyEnvelopeGetInfo.getGetTime() {
        return getTime;
    }
    
    public void MoneyEnvelopeGetInfo.setGetTime(Calendar getTime) {
        this.getTime = getTime;
    }
    
    public Integer MoneyEnvelopeGetInfo.getExpireDays() {
        return expireDays;
    }
    
    public void MoneyEnvelopeGetInfo.setExpireDays(Integer expireDays) {
        this.expireDays = expireDays;
    }
    
    public Integer MoneyEnvelopeGetInfo.getExpireStatus() {
        return expireStatus;
    }
    
    public void MoneyEnvelopeGetInfo.setExpireStatus(Integer expireStatus) {
        this.expireStatus = expireStatus;
    }
    
}
