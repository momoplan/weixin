package com.ruyicai.weixin.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 响应新闻消息
 */
@XmlRootElement(name = "xml")
public class ResponseNewsMessage extends ResponseBaseMessage {

	// 图文消息个数，限制为10条以内
	@SuppressWarnings("unused")
	private int ArticleCount;

	// 多条图文消息信息，默认第一个item为大图
	private List<Article> Articles;

	@XmlElement(name = "ArticleCount")
	public int getArticleCount() {
		return ArticleCount = Articles == null ? 0 : Articles.size();
	}

	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}

	@XmlElementWrapper(name = "Articles")
	@XmlElement(name = "item")
	public List<Article> getArticles() {
		return Articles;
	}

	public void setArticles(List<Article> articles) {
		Articles = articles;
	}
}
