package com.ruyicai.weixin.domain;

import java.io.Serializable;

import javax.persistence.Column;

import org.springframework.roo.addon.jpa.identifier.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class SubscriberPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "WEIXINNO", length = 50)
	private String weixinno;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SubscriberPK)) {
			return false;
		}
		SubscriberPK other = (SubscriberPK) obj;
		if (userno == null) {
			if (other.userno != null) {
				return false;
			}
		} else if (!userno.equals(other.userno)) {
			return false;
		}
		if (weixinno == null) {
			if (other.weixinno != null) {
				return false;
			}
		} else if (!weixinno.equals(other.weixinno)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = (prime * result) + (userno == null ? 0 : userno.hashCode());
		result = (prime * result) + (weixinno == null ? 0 : weixinno.hashCode());
		return result;
	}
}
