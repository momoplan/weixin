package com.ruyicai.weixin.dto.lottery;

import java.math.BigDecimal;
import java.util.Date;

public class Twininfo {
	private CompositePK id;

	private String playname;

	private String winbasecode;

	private String winspecialcode;

	private BigDecimal actsellamt;

	private BigDecimal validsellamt;

	private BigDecimal wingrade;

	private BigDecimal winmoney;

	private BigDecimal winnumber;

	private BigDecimal forwardamt;

	private String info;

	private BigDecimal state;

	private Date opentime;

	private Date agencyopentime;

	public CompositePK getId() {
		return id;
	}

	public void setId(CompositePK id) {
		this.id = id;
	}

	public String getPlayname() {
		return playname;
	}

	public void setPlayname(String playname) {
		this.playname = playname;
	}

	public String getWinbasecode() {
		return winbasecode;
	}

	public void setWinbasecode(String winbasecode) {
		this.winbasecode = winbasecode;
	}

	public String getWinspecialcode() {
		return winspecialcode;
	}

	public void setWinspecialcode(String winspecialcode) {
		this.winspecialcode = winspecialcode;
	}

	public BigDecimal getActsellamt() {
		return actsellamt;
	}

	public void setActsellamt(BigDecimal actsellamt) {
		this.actsellamt = actsellamt;
	}

	public BigDecimal getValidsellamt() {
		return validsellamt;
	}

	public void setValidsellamt(BigDecimal validsellamt) {
		this.validsellamt = validsellamt;
	}

	public BigDecimal getWingrade() {
		return wingrade;
	}

	public void setWingrade(BigDecimal wingrade) {
		this.wingrade = wingrade;
	}

	public BigDecimal getWinmoney() {
		return winmoney;
	}

	public void setWinmoney(BigDecimal winmoney) {
		this.winmoney = winmoney;
	}

	public BigDecimal getWinnumber() {
		return winnumber;
	}

	public void setWinnumber(BigDecimal winnumber) {
		this.winnumber = winnumber;
	}

	public BigDecimal getForwardamt() {
		return forwardamt;
	}

	public void setForwardamt(BigDecimal forwardamt) {
		this.forwardamt = forwardamt;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public BigDecimal getState() {
		return state;
	}

	public void setState(BigDecimal state) {
		this.state = state;
	}

	public Date getOpentime() {
		return opentime;
	}

	public void setOpentime(Date opentime) {
		this.opentime = opentime;
	}

	public Date getAgencyopentime() {
		return agencyopentime;
	}

	public void setAgencyopentime(Date agencyopentime) {
		this.agencyopentime = agencyopentime;
	}
}
