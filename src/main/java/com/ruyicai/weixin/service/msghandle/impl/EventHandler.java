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
import com.ruyicai.weixin.dto.ResponseTextMessage;
import com.ruyicai.weixin.service.AsyncService;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.msghandle.IMsgHandler;
import com.ruyicai.weixin.util.DateUtil;

/**
 * 事件推送消息处理
 * 
 * <xml><ToUserName><![CDATA[gh_3ece47134dbe]]></ToUserName>
 * <FromUserName><![CDATA[o2pZYt5nfeKCvSGWPI-BVnoObaMY]]></FromUserName>
 * <CreateTime>1381740946</CreateTime> <MsgType><![CDATA[event]]></MsgType>
 * <Event><![CDATA[subscribe]]></Event> <EventKey><![CDATA[]]></EventKey> </xml>
 */
@Service(value = "event")
public class EventHandler implements IMsgHandler {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private CaseLotActivityService caseLotActivityService;

    public ResponseBaseMessage handler(RequestMessage requestMessage) {
        String event = requestMessage.getEvent();
        if (event.equalsIgnoreCase("subscribe")) {
            asyncService.subscribe(requestMessage.getFromUserName(), requestMessage.getToUserName());
//             ResponseTextMessage rnm = new ResponseTextMessage();
//             rnm.setFromUserName(requestMessage.getToUserName());
//             rnm.setToUserName(requestMessage.getFromUserName());
//             rnm.setCreateTime(new Date().getTime());
//             rnm.setMsgType(ResponseMsgType.TEXT);
//             //
////             rnm.setContent("主人，我已在您微信通讯录；搜 “如意彩” 随时找我\r\n\r\n送彩票，买彩票，看账户 —— 方便安全");
//             //
////             rnm.setContent("主人，我已在您微信通讯录；搜 “如意彩” 随时找我\r\n\r\n几块钱红包送不出手，不如送几个彩票。点击屏幕下方功能键");
//             rnm.setContent("主人，我已在您微信通讯录；搜 “如意彩” 随时找我\r\n\r\n免费领10注彩票送好友！【马上领取】\r\nhttps://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/huodong/redbag-101/index.html?packet_id=8c1aba71e36ad2ce21414862750783bd&response_type=code&scope=snsapi_base&state=1#wechat_redirect");

            ResponseNewsMessage rnm = new ResponseNewsMessage();
            rnm.setFromUserName(requestMessage.getToUserName());
            rnm.setToUserName(requestMessage.getFromUserName());
            rnm.setMsgType(ResponseMsgType.NEWS);
            rnm.setCreateTime(new Date().getTime());
            
            List<Article> list = new ArrayList<Article>();
            Article a = new Article();
             a.setTitle("欢迎关注如意彩官方微信");
            a.setDescription("支持中国队，如意彩送您彩金！\r\n\r\n将此机会分享给好友或朋友圈，TA和您都能再得彩金，拿钱拿到手软！【马上分享】");
             a.setPicUrl("http://www.ruyicai.com/uploadimg/asiaFootball.jpg");
            a.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/asiacup_weixinactivity/index.html?response_type=code&scope=snsapi_base&state=1&from=singlemessage&isappinstalled=0#wechat_redirect");
            list.add(a);
            rnm.setArticles(list);
            
//            List<Article> list = new ArrayList<Article>();
//            Article a = new Article();
//             a.setTitle("欢迎关注如意彩官方微信");
//            a.setDescription("主人，我已在您微信通讯录；搜  “如意彩” 随时找我！\r\n\r\n将彩票红包分享给好友或朋友圈，TA中奖，你分钱！【马上分享】");
//             a.setPicUrl("http://www.ruyicai.com/uploadimg/1121.jpg");
//            a.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://wx.ruyicai.com/huodong/redbag-101/index.html?packet_id=8c1aba71e36ad2ce21414862750783bd&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
//            list.add(a);
            
            rnm.setArticles(list);

            // ResponseNewsMessage rnm = new ResponseNewsMessage();
            // rnm.setFromUserName(requestMessage.getToUserName());
            // rnm.setToUserName(requestMessage.getFromUserName());
            // rnm.setMsgType(ResponseMsgType.NEWS);
            // rnm.setCreateTime(new Date().getTime());
            // List<Article> list = new ArrayList<Article>();
            // Article a = new Article();
            // //a.setTitle("欢迎关注如意彩官方微信");
            // a.setDescription("主人，我已在您微信通讯录；搜  “如意彩” 随时找我！\r\n中秋送彩票“财”是送运气");
            // //a.setPicUrl("http://www.ruyicai.com/weixin/images/login.png");
            // a.setUrl("http://iphone.ruyicai.com/index.html");
            // list.add(a);
            // rnm.setArticles(list);
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=F47104&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=F47103&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=F47102&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01001&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01002&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01011&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01009&timestamp="
                    + DateUtil.gettimeStamp());
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
            a.setPicUrl("http://www.ruyicai.com/weixin/static/wininfo?lotno=T01013&timestamp="
                    + DateUtil.gettimeStamp());
            a.setUrl("http://3g.ruyicai.com/w3g/winInfo/selectWinInfoCenter");
            list.add(a);
            rnm.setArticles(list);
            return rnm;
        }
        return null;
    }

}
