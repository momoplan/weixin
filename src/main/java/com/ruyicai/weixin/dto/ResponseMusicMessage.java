package com.ruyicai.weixin.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 响应音乐消息
 */
@XmlRootElement(name = "xml")
public class ResponseMusicMessage extends ResponseBaseMessage {

	// 音乐
	private Music Music;

	@XmlElement(name = "Music")
	public Music getMusic() {
		return Music;
	}

	public void setMusic(Music music) {
		Music = music;
	}
}