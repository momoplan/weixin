package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "ChancesDetail")
public class ChancesDetail {

	@EmbeddedId
	private ChancesDetailPK id;

	private int state;

	private Date createTime;

	private Date successTime;

	public static ChancesDetail createChancesDetail(String userno, String fromUserno, String orderid) {
		ChancesDetail detail = new ChancesDetail();
		detail.setId(new ChancesDetailPK(userno, fromUserno, orderid));
		detail.setState(0);
		detail.setCreateTime(new Date());
		return detail;
	}
}
