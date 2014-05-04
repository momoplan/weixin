package com.ruyicai.weixin.service.msghandle.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.consts.ResponseMsgType;
import com.ruyicai.weixin.dto.Article;
import com.ruyicai.weixin.dto.RequestMessage;
import com.ruyicai.weixin.dto.ResponseBaseMessage;
import com.ruyicai.weixin.dto.ResponseNewsMessage;
import com.ruyicai.weixin.service.AsyncService;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;
import com.ruyicai.weixin.util.DateUtil;

/**
 * 事件推送消息处理
 * 
 * <xml><ToUserName><![CDATA[gh_3ece47134dbe]]></ToUserName>
 * <FromUserName><![CDATA[o2pZYt5nfeKCvSGWPI-BVnoObaMY]]></FromUserName>
 * <CreateTime>1381740946</CreateTime>
 * <MsgType><![CDATA[event]]></MsgType>
 * <Event><![CDATA[subscribe]]></Event>
 * <EventKey><![CDATA[]]></EventKey>
 * </xml>
 */
@Service(value = "event")
public class EventHandler implements IMsgHandler {

	@Autowired
	private AsyncService asyncService;

	@Override
	public ResponseBaseMessage handler(RequestMessage requestMessage) {
		String event = requestMessage.getEvent();
		if (event.equalsIgnoreCase("subscribe")) {
			asyncService.subscribe(requestMessage.getFromUserName(), requestMessage.getToUserName());
			ResponseNewsMessage rnm = new ResponseNewsMessage();
			rnm.setFromUserName(requestMessage.getToUserName());
			rnm.setToUserName(requestMessage.getFromUserName());
			rnm.setMsgType(ResponseMsgType.NEWS);
			rnm.setCreateTime(new Date().getTime());
			List<Article> list = new ArrayList<Article>();
			Article a = new Article();
			a.setTitle("欢迎关注如意彩官方微信");
			a.setDescription("“如意彩”是经过我国福利彩票、体育彩票发行机构官方授权的正规合法的手机投注平台。");
			a.setPicUrl("http://www.ruyicai.com/weixin/images/login.png");
			a.setUrl("http://iphone.ruyicai.com/index.html");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (event.equalsIgnoreCase("unsubscribe")) {
			asyncService.unsubscribe(requestMessage.getFromUserName(), requestMessage.getToUserName());
			return null;
		}
		String eventKey = requestMessage.getEventKey();
		if (StringUtils.isBlank(eventKey)) {
			return null;
		}
		if (eventKey.equalsIgnoreCase("KJHM-F47104")) {
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
			a.setUrl("http://iphone.ruyicai.com/html/lottery/kjgg.html");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (eventKey.equalsIgnoreCase("KJHM-F47103")) {
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
			a.setUrl("http://iphone.ruyicai.com/html/lottery/dddlot.html");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (eventKey.equalsIgnoreCase("KJHM-F47102")) {
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
		} else if (eventKey.equalsIgnoreCase("KJHM-T01001")) {
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
			a.setUrl("http://iphone.ruyicai.com/html/lottery/dltlot.html");
			list.add(a);
			rnm.setArticles(list);
			return rnm;
		} else if (eventKey.equalsIgnoreCase("KJHM-T01002")) {
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
		} else if (eventKey.equalsIgnoreCase("KJHM-T01011")) {
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
		} else if (eventKey.equalsIgnoreCase("KJHM-T01009")) {
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
		} else if (eventKey.equalsIgnoreCase("KJHM-T01013")) {
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
