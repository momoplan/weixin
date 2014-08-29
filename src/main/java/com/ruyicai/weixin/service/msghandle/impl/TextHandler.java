package com.ruyicai.weixin.service.msghandle.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ruyicai.weixin.consts.ResponseMsgType;
import com.ruyicai.weixin.dto.Article;
import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.dto.ResponseNewsMessage;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;
import com.ruyicai.weixin.util.DateUtil;

/**
 * 文本消息
 * 
 * <xml>
 * <ToUserName><![CDATA[toUser]]></ToUserName>
 * <FromUserName><![CDATA[fromUser]]></FromUserName>
 * <CreateTime>1348831860</CreateTime>
 * <MsgType><![CDATA[text]]></MsgType>
 * <Content><![CDATA[this is a test]]></Content>
 * <MsgId>1234567890123456</MsgId>
 * </xml>
 */
@Service(value = "text")
public class TextHandler implements IMsgHandler {

	public ResponseBaseMessage handler(RequestMessage requestMessage) {
		String content = requestMessage.getContent();
		if (content.equalsIgnoreCase("双色球")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("双色球开奖公告");
			a.setDescription("双色球开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=F47104&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("3D")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("福彩3D开奖公告");
			a.setDescription("福彩3D开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=F47103&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("七乐彩")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("七乐彩开奖公告");
			a.setDescription("七乐彩开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=F47102&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("大乐透")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("大乐透开奖公告");
			a.setDescription("大乐透开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01001&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("排列三")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("排列三开奖公告");
			a.setDescription("排列三开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01002&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("排列五")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("排列五开奖公告");
			a.setDescription("排列五开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01011&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("七星彩")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("七星彩开奖公告");
			a.setDescription("七星彩开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01009&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (content.equalsIgnoreCase("22选5")) {
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("22选5开奖公告");
			a.setDescription("22选5开奖公告");
			a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01013&timestamp=" + DateUtil.gettimeStamp());
			a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		}
		return null;
	}

}
