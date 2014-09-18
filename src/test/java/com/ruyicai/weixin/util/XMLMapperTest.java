package com.ruyicai.weixin.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.weixin.dto.Article;
import com.ruyicai.weixin.dto.Music;
import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.dto.ResponseMusicMessage;
import com.ruyicai.weixin.dto.ResponseNewsMessage;
import com.ruyicai.weixin.dto.ResponseTextMessage;

public class XMLMapperTest {

	private Logger logger = LoggerFactory.getLogger(XMLMapperTest.class);

	@Test
	public void xmlToBeanTest() throws JAXBException {
		String s = "<xml> <ToUserName><![CDATA[toUser]]></ToUserName> <FromUserName><![CDATA[fromUser]]></FromUserName> <CreateTime>1348831860</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[this is a test]]></Content> <MsgId>1234567890123456</MsgId> </xml>";
		RequestMessage xml = JaxbMapper.fromXml(s, RequestMessage.class);
		logger.info(xml.toString());
//		System.out.println(xml.toString());
	}
	
	@Test
	public void xmlToBeanTest1() throws JAXBException {
		String s = "<xml> <ToUserName><![CDATA[toUser]]></ToUserName> <FromUserName><![CDATA[fromUser]]></FromUserName> <CreateTime>1348831860</CreateTime> <MsgType><![CDATA[text]]></MsgType> <Content><![CDATA[this is a test]]></Content> <MsgId>1234567890123456</MsgId> </xml>";
		 
		RequestMessage xml = JaxbMapper.fromXml(s, RequestMessage.class);
		logger.info(xml.toString());
		
		
//		JSONObject array = JSONObject.fromObject(xml.toString());
		System.out.println(JaxbMapper.toXml(xml,true));
	}

	@Test
	public void beanToXmlTest() throws JAXBException {
		logger.info("RequestMessage");
		RequestMessage rm = new RequestMessage();
		rm.setContent("abc");
		rm.setFromUserName("123");
		rm.setToUserName("321");
		logger.info(JaxbMapper.toXml(rm, true));

		logger.info("ResponseBaseMessage");
		ResponseBaseMessage rbm = new ResponseBaseMessage();
		rbm.setFromUserName("rbm");
		rbm.setToUserName("rbm");
		rbm.setCreateTime(123123123L);
		rbm.setMsgType("text");
		logger.info(JaxbMapper.toXml(rbm, true));

		logger.info("ResponseMusicMessage");
		ResponseMusicMessage rmm = new ResponseMusicMessage();
		rmm.setFromUserName("sunyang");
		rmm.setToUserName("haojing");
		Music music = new Music();
		music.setTitle("好歌曲");
		music.setDescription("中国");
		music.setMusicUrl("www.123.com");
		music.setHQMusicUrl("www.hq.com");
		rmm.setMusic(music);
		logger.info(JaxbMapper.toXml(rmm, true));

		logger.info("ResponseTextMessage");
		ResponseTextMessage rtm = new ResponseTextMessage();
		rtm.setFromUserName("sunyang");
		rtm.setToUserName("haojing");
		rtm.setContent("response test message");
		logger.info(JaxbMapper.toXml(rtm, true));

		logger.info("ResponseNewsMessage");
		ResponseNewsMessage rnm = new ResponseNewsMessage();
		rnm.setFromUserName("sunyang");
		rnm.setToUserName("haojing");
		List<Article> list = new ArrayList<Article>();
		Article a = new Article();
		a.setTitle("new1");
		a.setDescription("new desc");
		a.setPicUrl("pic url");
		a.setUrl("urllllll");
		Article b = new Article();
		b.setTitle("new2");
		b.setDescription("new desc2");
		list.add(a);
		list.add(b);
		rnm.setArticles(list);
		logger.info(JaxbMapper.toXml(rnm, true));
	}

}
