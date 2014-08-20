package com.ruyicai.weixin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "punt_list", versionField = "")
public class PuntList {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "punt_id")
	private int punt_id;

	@Column(name = "orderid")
	private String orderid;

	@Column(name = "betcode")
	private String betcode;

	@Column(name = "batchcode")
	private String batchcode;

	@Column(name = "createtime")
	private Date createtime;

	@Column(name = "opentime")
	private Date opentime;

	@Column(name = "orderprizeamt")
	private int orderprizeamt;

}
