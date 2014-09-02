package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.LockModeType;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("deprecation")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "CaseLotUserinfo", versionField = "")
public class CaseLotUserinfo {

	@EmbeddedId
	private CaseLotUserinfoPK id;

	@Column(name = "chances")
	private int chances;

	@NotNull
	@Column(name = "nickname", length = 50)
	private String nickname;

	@Column(name = "headimgurl", length = 50)
	private String headimgurl;

	@Column(name = "joinTimes")
	private int joinTimes;

	@NotNull
	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "linkTimes")
	private int linkTimes;
	
	@Column(name = "settingImgurl")
	private String settingImgurl;
	
	@Column(name = "openid")
	private String openid;

	public static CaseLotUserinfo findCaseLotUserinfo(CaseLotUserinfoPK id, boolean lock) {
		CaseLotUserinfo caseLotUserinfo = entityManager().find(CaseLotUserinfo.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return caseLotUserinfo;
	}

	@Transactional
	public static CaseLotUserinfo createCaseLotUserinfo(String userno, String orderid, String nickname,
			String headimgurl,String openid) {
		CaseLotUserinfo caseLotUserinfo = new CaseLotUserinfo();
		caseLotUserinfo.setId(new CaseLotUserinfoPK(userno, orderid));
		caseLotUserinfo.setNickname(nickname);
		caseLotUserinfo.setHeadimgurl(headimgurl);
		caseLotUserinfo.setChances(1);
		caseLotUserinfo.setJoinTimes(0);
		caseLotUserinfo.setCreateTime(new Date());
		caseLotUserinfo.setLinkTimes(0);
		caseLotUserinfo.setOpenid(openid);
		caseLotUserinfo.persist();
		return caseLotUserinfo;
	}
}
