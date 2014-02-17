package com.ruyicai.weixin.dto.lottery;

public class ResponseData {

	private String errorCode;

	private Object value;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
