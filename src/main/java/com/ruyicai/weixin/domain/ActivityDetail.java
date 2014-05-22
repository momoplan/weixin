package com.ruyicai.weixin.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.weixin.util.Page;

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

	private transient CaseLotUserinfo caseLotUserinfo;

	public static ActivityDetail createActivityDetail(String userno, String orderid, String fromUserno) {
		ActivityDetail detail = new ActivityDetail();
		detail.setUserno(userno);
		detail.setOrderid(orderid);
		detail.setJoinTime(new Date());
		detail.setFromUserno(fromUserno);
		detail.persist();
		return detail;
	}

	public static void findActivityDetailByPage(String orderid, Page<ActivityDetail> page) {
		String sql = "SELECT o FROM ActivityDetail o WHERE 1=1 AND o.orderid = :orderid ORDER BY o.joinTime DESC";
		String countSql = "SELECT count(*) FROM ActivityDetail o WHERE 1=1 AND o.orderid = :orderid ";
		List<ActivityDetail> resultList = entityManager().createQuery(sql, ActivityDetail.class)
				.setParameter("orderid", orderid).getResultList();
		int count = entityManager().createQuery(countSql, Long.class).setParameter("orderid", orderid)
				.getSingleResult().intValue();
		for (ActivityDetail detail : resultList) {
			detail.setCaseLotUserinfo(CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(detail.getUserno(),
					detail.getOrderid())));
		}
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
