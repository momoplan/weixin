package com.ruyicai.weixin.domain;

import java.io.Serializable;

import javax.persistence.Column;

import org.springframework.roo.addon.jpa.identifier.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class CaseLotUserinfoPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "userno", length = 50)
	private String userno;

	@Column(name = "orderid", length = 100)
	private String orderid;

}
