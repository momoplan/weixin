package com.ruyicai.weixin.dao;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.domain.RequestMessageDetail;
import com.ruyicai.weixin.dto.RequestMessage;

@Component
public class RequestMessageDetailDao {

	@PersistenceContext
	private EntityManager entityManager;

	public RequestMessageDetail findRequestMessageDetail(String id, boolean lock) {
		RequestMessageDetail detail = this.entityManager.find(RequestMessageDetail.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return detail;
	}

	@Transactional
	public RequestMessageDetail createRequestMessageDetail(RequestMessage requestMessage, String requestBody) {
		if ((requestMessage == null) || StringUtils.isBlank(requestBody)) {
			throw new IllegalArgumentException("The argument requestMessage or body is required");
		}
		RequestMessageDetail detail = new RequestMessageDetail();
		detail.setFromUserName(requestMessage.getFromUserName());
		detail.setToUserName(requestMessage.getToUserName());
		detail.setMsgType(requestMessage.getMsgType());
		detail.setMsgId(requestMessage.getMsgId());
		detail.setCreateTime(requestMessage.getCreateTime());
		detail.setRequestBody(requestBody);
		this.entityManager.persist(detail);
		return detail;
	}
}
