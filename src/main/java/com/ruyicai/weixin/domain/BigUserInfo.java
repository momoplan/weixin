package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 微信用户和如意彩用户对照表
 */
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "BigUserInfo", versionField = "")
public class BigUserInfo {

	@Id
	@Column(name = "openid", length = 50)
	private String openid;

	@NotNull
	@Column(name = "userno", length = 50)
	private String userno;

	@Column(name = "createTime", length = 50)
	private Date createTime;

	public static BigUserInfo createBigUserInfo(String openid, String userno) {
		BigUserInfo bigUserInfo = new BigUserInfo();
		bigUserInfo.setOpenid(openid);
		bigUserInfo.setUserno(userno);
		bigUserInfo.setCreateTime(new Date());
		bigUserInfo.persist();
		return bigUserInfo;
	}

}
