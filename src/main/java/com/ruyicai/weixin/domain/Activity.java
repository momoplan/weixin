package com.ruyicai.weixin.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 微信合买活动详情
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "Activity")
public class Activity {

	@Id
	@NotNull
	@Column(name = "orderid", length = 50)
	private String orderid;

	@NotNull
	@Column(name = "remainingShare")
	private Integer remainingShare;

	@NotNull
	@Column(name = "remainingShare")
	private Integer allShare;

	public static Activity findActivity(String orderid, boolean lock) {
		Activity activity = entityManager().find(Activity.class, orderid,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return activity;
	}

	public static Activity createActivity(String orderid, Integer allShare) {
		if (StringUtils.isEmpty(orderid)) {
			throw new IllegalArgumentException("the argument orderid is require.");
		}
		if (allShare == null) {
			throw new IllegalArgumentException("the argument allShare is require.");
		}
		Activity activity = new Activity();
		activity.setOrderid(orderid);
		activity.setAllShare(allShare);
		activity.setRemainingShare(allShare);
		activity.persist();
		return activity;
	}
}