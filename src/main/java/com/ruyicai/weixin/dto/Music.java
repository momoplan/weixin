package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.ruyicai.weixin.util.CDataAdapter;

/**
 * 响应音乐消息的音乐属性
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Music {

	// 音乐名称
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String Title;

	// 音乐描述
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String Description;

	// 音乐链接
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String MusicUrl;

	// 高质量音乐链接，WIFI环境优先使用该链接播放音乐
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String HQMusicUrl;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getMusicUrl() {
		return MusicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		MusicUrl = musicUrl;
	}

	public String getHQMusicUrl() {
		return HQMusicUrl;
	}

	public void setHQMusicUrl(String hQMusicUrl) {
		HQMusicUrl = hQMusicUrl;
	}

}