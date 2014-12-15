package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.weixin.consts.SubaccountType;

@RooJavaBean
@RooJson
@RooToString
public class Torder {

	private String id;//订单号

	private String batchcode;

	private String lotno;

	private BigDecimal amt;

	private BigDecimal paytype;

	private BigDecimal orderstate;

	private BigDecimal bettype;

	private BigDecimal prizestate;

	private BigDecimal orderprizeamt;

	private BigDecimal orderpreprizeamt;

	/** @deprecated 是否有战绩 0：没有，1：有 */
	private BigDecimal hasachievement;

	private String winbasecode;

	private BigDecimal ordertype;

	private String tsubscribeflowno;

	private String tlotcaseid;

	private Date createtime;

	private String userno;

	private String buyuserno;

	private String memo;

	private String subaccount;

	private BigDecimal betnum;

	private Date canceltime;

	private Date endtime;

	private String desc;

	private String betcode;

	private BigDecimal alreadytrans;

	private BigDecimal lotmulti;

	private String prizeinfo;

	private String orderinfo;

	private String body;

	private BigDecimal instate;

	private BigDecimal paystate;

	/** 方案类型 */
	private BigDecimal lotsType;

	private Date encashtime;

	private String eventcode;

	private String agencyno;

	private String channel;

	private String subchannel;

	private String playtype;

	private String latedteamid;

	private Date lastprinttime;

	private String errorcode;

	private Date successtime;
	
	private String  caselotStarter;

	private transient BigDecimal orderamt;

	private transient Date modifytime;

	private transient BigDecimal orderprize;

	public BigDecimal getOrderpreprizeamt() {
		return orderpreprizeamt;
	}

	public void setOrderpreprizeamt(BigDecimal orderpreprizeamt) {
		this.orderpreprizeamt = orderpreprizeamt;
	}

	public BigDecimal getHasachievement() {
		return hasachievement;
	}

	public void setHasachievement(BigDecimal hasachievement) {
		this.hasachievement = hasachievement;
	}

	public BigDecimal getOrderamt() {
		return orderamt;
	}

	public void setOrderamt(BigDecimal orderamt) {
		this.orderamt = orderamt;
	}

	public Date getModifytime() {
		return modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}

	public BigDecimal getOrderprize() {
		return orderprize;
	}

	public void setOrderprize(BigDecimal orderprize) {
		this.orderprize = orderprize;
	}

	public BigDecimal getLotsType() {
		return lotsType;
	}

	public void setLotsType(BigDecimal lotsType) {
		this.lotsType = lotsType;
	}

	public SubaccountType getSubaccountType() {
		if (StringUtils.isEmpty(subaccount)) {
			return null;
		}
		try {
			return SubaccountType.valueOf(subaccount);
		} catch (Exception e) {
			return null;
		}
	}

	public BigDecimal getPaystate() {
		return paystate;
	}

	public void setPaystate(BigDecimal paystate) {
		this.paystate = paystate;
	}

	public String getPlaytype() {
		return playtype;
	}

	public void setPlaytype(String playtype) {
		this.playtype = playtype;
	}

	public BigDecimal getPrizestate() {
		return prizestate;
	}

	public void setPrizestate(BigDecimal prizestate) {
		this.prizestate = prizestate;
	}

	public String getLatedteamid() {
		return latedteamid;
	}

	public void setLatedteamid(String latedteamid) {
		this.latedteamid = latedteamid;
	}

	public Date getLastprinttime() {
		return lastprinttime;
	}

	public void setLastprinttime(Date lastprinttime) {
		this.lastprinttime = lastprinttime;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public Date getSuccesstime() {
		return successtime;
	}

	public void setSuccesstime(Date successtime) {
		this.successtime = successtime;
	}
}
