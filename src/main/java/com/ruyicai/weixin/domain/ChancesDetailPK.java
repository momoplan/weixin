package com.ruyicai.weixin.domain;

import java.io.Serializable;

import javax.persistence.Column;

import org.springframework.roo.addon.jpa.identifier.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class ChancesDetailPK implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 推广人用户编号 */
	@Column(name = "linkUserno", length = 50)
	private String linkUserno;

	/** 点击链接的人用户编号 */
	@Column(name = "joinUserno", length = 50)
	private String joinUserno;

	@Column(name = "orderid", length = 100)
	private String orderid;
}
