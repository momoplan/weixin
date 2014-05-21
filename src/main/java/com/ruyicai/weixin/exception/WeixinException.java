package com.ruyicai.weixin.exception;

public class WeixinException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ErrorCode errorCode;

	public WeixinException(String msg) {
		super(msg);
	}

	public WeixinException(String msg, Throwable e) {
		super(msg, e);
	}

	public WeixinException(ErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
