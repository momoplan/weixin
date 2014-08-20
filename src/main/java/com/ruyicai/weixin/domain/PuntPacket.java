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
@RooJpaEntity(table = "punt_packet", versionField = "")
public class PuntPacket {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "packet_id")
	private int packet_id;

	@Column(name = "random_punts")
	private int random_punts;

	@Column(name = "openid")
	private String openid;

	@Column(name = "get_userno")
	private String get_userno;

	@Column(name = "get_time")
	private Date get_time;

	@Column(name = "thank_words")
	private String thank_words;

}
