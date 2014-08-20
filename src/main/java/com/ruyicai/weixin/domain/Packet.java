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
@RooJpaEntity(table = "packet", versionField = "")
public class Packet {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "openid")
	private String openid;

	@Column(name = "packet_userno")
	private String packet_userno;

	@Column(name = "total_persons")
	private int total_persons;

	@Column(name = "total_punts")
	private int total_punts;

	@Column(name = "real_parts")
	private int real_parts;

	@Column(name = "greetings")
	private String greetings;

	@Column(name = "createtime")
	private Date createtime;

}
