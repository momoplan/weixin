package com.ruyicai.weixin.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.weixin.util.Page;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "ChancesDetail", versionField = "")
public class ChancesDetail {

	@EmbeddedId
	private ChancesDetailPK id;

	private int state;

	private Date createTime;

	private Date successTime;

	private transient CaseLotUserinfo joinCaseLotUserinfo;

	public static ChancesDetail createChancesDetail(String userno, String fromUserno, String orderid) {
		ChancesDetail detail = new ChancesDetail();
		detail.setId(new ChancesDetailPK(userno, fromUserno, orderid));
		detail.setState(0);
		detail.setCreateTime(new Date());
		detail.persist();
		return detail;
	}

	public static void findChancesDetailByPage(String orderid, String linkUserno, Page<ChancesDetail> page) {
		String sql = "SELECT o FROM ChancesDetail o WHERE 1=1 AND o.id.orderid = :orderid AND o.id.linkUserno = :linkUserno AND o.state = 1 ORDER BY o.joinTime DESC";
		String countSql = "SELECT count(*) FROM ChancesDetail o WHERE 1=1 AND o.id.orderid = :orderid AND o.id.linkUserno = :linkUserno AND o.state = 1 ";
		List<ChancesDetail> resultList = entityManager().createQuery(sql, ChancesDetail.class)
				.setParameter("orderid", orderid).setParameter("linkUserno", linkUserno).getResultList();
		int count = entityManager().createQuery(countSql, Long.class).setParameter("orderid", orderid)
				.setParameter("linkUserno", linkUserno).getSingleResult().intValue();
		for (ChancesDetail detail : resultList) {
			detail.setJoinCaseLotUserinfo(CaseLotUserinfo.findCaseLotUserinfo(new CaseLotUserinfoPK(detail.getId()
					.getJoinUserno(), orderid)));
		}
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
