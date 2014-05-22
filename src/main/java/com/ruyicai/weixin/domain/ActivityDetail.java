package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "ActivityDetail", versionField = "")
public class ActivityDetail {

	@Column(name = "userno", length = 50)
	private String userno;

	@Column(name = "orderid", length = 50)
	private String orderid;

	@Column(name = "fromUserno", length = 50)
	private String fromUserno;

	private Date joinTime;

	public static ActivityDetail createActivityDetail(String userno, String orderid, String fromUserno) {
		ActivityDetail detail = new ActivityDetail();
		detail.setUserno(userno);
		detail.setOrderid(orderid);
		detail.setJoinTime(new Date());
		detail.setFromUserno(fromUserno);
		detail.persist();
		return detail;
	}
}
