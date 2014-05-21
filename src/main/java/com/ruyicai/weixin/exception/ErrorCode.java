package com.ruyicai.weixin.exception;

public enum ErrorCode {

	CASELOTUSERINFO_NOT_EXISTS("501", "活动用户不存在"),
	CASELOTUSERINFO_CHANCES_NOT_ENOUGH("502", "参与次数不足"),
	ACTIVITY_REMAININGSHARE_NOT_ENOUGH("503", "活动份数不足"),
	CHANCEDETAIL_HAVE_EXISTS("504", "连接点击纪录已存在"),
	CHANCEDETAIL_HAVE_ADD("505", "连接点击纪录已增加机会");

	public String value;

	public String memo;

	ErrorCode(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
}
