package com.ruyicai.weixin.service;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.SubscriberDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.Subscriber;

@Service
public class SubscribeLotService {

	@Autowired
	SubscriberDao subscriberDao;
	
    @Autowired
    private WeixinService weixinService;
	
    @Autowired
    CaseLotActivityService caseLotActivityService;
    
    private Logger logger = LoggerFactory.getLogger(SubscribeLotService.class);	
	 /**
     * 中奖信息模板
     * 
     * @return
     */
    @Async
    public void sendOpenInfo(String userno, String batchcode,String wincode,String lotName,String url,int amt,String betTime) {
        CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(userno, Const.WX_PACKET_ACTIVITY);
        String openid = caseLotUserinfo.getOpenid();
        
        if(openid == null || openid.equals(""))
        {
            logger.debug("openID 为空");
            return;
        }
        
        String json = "{\"touser\":\"\",\"template_id\":\"\"," + "\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":\"\"}";

        String jsoBuy = "{\"first\": {\"value\":\"\",\"color\":\"\"},\"issueInfo\": {\"value\":\"\",\"color\":\"\"},\"betTime\": {\"value\":\"\",\"color\":\"\"},\"fee\": {\"value\":\"\",\"color\":\"\"},\"drawTime\": {\"value\":\"\",\"color\":\"\"},\"remark\": {\"value\":\"\",\"color\":\"\"}}";

        String templateid = "2psUT5ynvWwXaXiBi8bMbVpJfpiR4hYoGxX7c9mmlK0";
       // String url = "http://wx.ruyicai.com/html/lottery/ssclot.html";
        String topcolor = "#DA2828";
        String color = "#DA2828";  
        String black = "#000000";

        JSONObject jsono = JSONObject.fromObject(jsoBuy);

        JSONObject jsonoSub = JSONObject.fromObject(jsono.get("first"));
        jsonoSub.element("value", "开奖结果通知（"+lotName+"）");
        jsonoSub.element("color", black);
        jsono.element("first", jsonoSub);

        jsonoSub = JSONObject.fromObject(jsono.get("issueInfo"));
        jsonoSub.element("value", batchcode);
        jsonoSub.element("color", black);
        jsono.element("issueInfo", jsonoSub);

        jsonoSub = JSONObject.fromObject(jsono.get("betTime"));
        jsonoSub.element("value", betTime);
        jsonoSub.element("color", black);
        jsono.element("betTime", jsonoSub);

        jsonoSub = JSONObject.fromObject(jsono.get("fee"));
        jsonoSub.element("value", String.valueOf(amt/100)+"元");
        jsonoSub.element("color", black);
        jsono.element("fee", jsonoSub);
        
        java.text.DateFormat format_open = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = new Date();      
        
        jsonoSub = JSONObject.fromObject(jsono.get("drawTime"));
        jsonoSub.element("value",format_open.format( dt));
        jsonoSub.element("color", black);
        jsono.element("drawTime", jsonoSub);
        
        jsonoSub = JSONObject.fromObject(jsono.get("remark"));
        jsonoSub.element("value", "开奖号码: "+wincode);  
        jsonoSub.element("color", color);
        jsono.element("remark", jsonoSub);
        
        JSONObject jsonoMain = JSONObject.fromObject(json);
        jsonoMain.element("touser", openid);
        jsonoMain.element("template_id", templateid);
        jsonoMain.element("url", url);
        jsonoMain.element("topcolor", topcolor);
        jsonoMain.element("data", jsono);
        logger.info("中奖信息 - >jsonoMain:" + jsonoMain);
        sendTemplateMsg(jsonoMain.toString());
    }
    
    @Async
    public void sendBetInfo(String userno, String total_money,String lotName,String url) {
        CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(userno, Const.WX_PACKET_ACTIVITY);
        String openid = caseLotUserinfo.getOpenid();
        String json = "{\"touser\":\"\",\"template_id\":\"\"," + "\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":\"\"}";

        String jsoBuy = "{\"title\": {\"value\":\"\",\"color\":\"\"},\"headinfo\": {\"value\":\"\",\"color\":\"\"},\"program\": {\"value\":\"\",\"color\":\"\"},\"result\": {\"value\":\"\",\"color\":\"\"},\"remark\": {\"value\":\"\",\"color\":\"\"}}";

        String templateid = "HZt4Rp3WoeeEXqJ8SMO-W3Je_7yy7qUjdOIvZAvfYCw";
//        String url = "http://wx.ruyicai.com/wxpay/v1/html/sendRedbag/account.html?info=get";
        String topcolor = "#DA2828";
        String color = "#DA2828";
        String black = "#000000";
        String betInfo = "中奖" + total_money + "元";
        

        JSONObject jsono = JSONObject.fromObject(jsoBuy);

        JSONObject jsonoSub = JSONObject.fromObject(jsono.get("title"));
        jsonoSub.element("value", "中奖结果通知（"+lotName+"）");
        jsonoSub.element("color", black);
        jsono.element("title", jsonoSub);

        jsonoSub = JSONObject.fromObject(jsono.get("headinfo"));
        jsonoSub.element("value", "恭喜您，中奖啦！");
        jsonoSub.element("color", color);
        jsono.element("headinfo", jsonoSub);

        jsonoSub = JSONObject.fromObject(jsono.get("program"));
        jsonoSub.element("value", lotName);
        jsonoSub.element("color", black);
        jsono.element("program", jsonoSub);

        jsonoSub = JSONObject.fromObject(jsono.get("result"));
        jsonoSub.element("value", betInfo);
        jsonoSub.element("color", color);
        jsono.element("result", jsonoSub);

//        jsonoSub = JSONObject.fromObject(jsono.get("remark"));
//        jsonoSub.element("value", "\r\n微信通讯录搜\"如意彩\"，就能找到我");
//
//        jsonoSub.element("color", color);
//        jsono.element("remark", jsonoSub);

        JSONObject jsonoMain = JSONObject.fromObject(json);
        jsonoMain.element("touser", openid);
        jsonoMain.element("template_id", templateid);
        jsonoMain.element("url", url);
        jsonoMain.element("topcolor", topcolor);
        jsonoMain.element("data", jsono);

        logger.info("中奖信息 - >jsonoMain:" + jsonoMain);
        sendTemplateMsg(jsonoMain.toString());

    }
    
    public int sendTemplateMsg(String strContent) {
        int ret = 0;
        String accessToken = weixinService.getAccessToken();
        String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        System.out.println(strContent);
        String sendData = strContent.toString();
        String ret1 = HttpUtil.sendRequestByPost(sendUrl, sendData, true);
        logger.info("sendUrl:" + sendUrl + "," + "result:" + ret1);
        
        System.out.println("sendUrl:" + sendUrl + "," + "result:" + ret1);

        return ret;
    }
}
