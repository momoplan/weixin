package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * 微信支付属性
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment {
	
	//交易金额
	private String TotalFee;
	
	//商品描述
	private String Body;
	
	//用户浏览器端 IP
	private String BillCreateIP;

	public String getBillCreateIP() {
		return BillCreateIP;
	}

	public void setBillCreateIP(String billCreateIP) {
		BillCreateIP = billCreateIP;
	}

	public String getBody() {
		return Body;
	}

	public void setBody(String body) {
		Body = body;
	}

	public String getTotalFee() {
		return TotalFee;
	}

	public void setTotalFee(String totalFee) {
		TotalFee = totalFee;
	}

	

}
