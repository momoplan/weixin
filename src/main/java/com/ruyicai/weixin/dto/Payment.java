package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * 微信支付属性
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment {
	
	private String TotalFee;

	public String getTotalFee() {
		return TotalFee;
	}

	public void setTotalFee(String totalFee) {
		TotalFee = totalFee;
	}

	

}
