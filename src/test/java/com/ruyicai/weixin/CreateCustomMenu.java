package com.ruyicai.weixin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.weixin.dao.AppUserDao;
import com.ruyicai.weixin.domain.AppUser;
import com.ruyicai.weixin.dto.menu.Button;
import com.ruyicai.weixin.dto.menu.ClickButton;
import com.ruyicai.weixin.dto.menu.ComplexButton;
import com.ruyicai.weixin.dto.menu.Menu;
import com.ruyicai.weixin.dto.menu.ViewButton;
import com.ruyicai.weixin.service.TranslateService;
import com.ruyicai.weixin.service.WeixinService;
import com.ruyicai.weixin.util.JsonMapper;

/**
 * 创建自定义菜单
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class CreateCustomMenu {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AppUserDao appUserDao;

	@Autowired
	private TranslateService translateService;

	@Autowired
	private WeixinService weixinService;

	@Before
	public void initWeixinService() {
		String weixinName = "ruyicai";
		AppUser appUser = appUserDao.findAppUser(weixinName);
		if (appUser == null) {
			logger.error("appUser is null");
			return;
		} else {
			logger.info(appUser.toString());
			translateService.setToken(appUser.getToken());
			weixinService.setAppId(appUser.getAppId());
			weixinService.setAppSecret(appUser.getAppSecret());
		}
	}

	@Test
	public void testfinduserBytoken() {
		String accessToken = weixinService.getAccessToken();
		weixinService.findUserinfoByOpenid(accessToken, "oFYzzjg0HzgaChtJlkhy4cbrhAno");
	}

	@Test
	public void createMenu() {
		Menu menu = getMenu();
		String jsonMenu = JsonMapper.toJson(menu);
		System.out.println(jsonMenu);
		String weixinName = "ruyicai";
		logger.info("初始化" + weixinName + "用户");
		AppUser appUser = appUserDao.findAppUser(weixinName);
		if (appUser == null) {
			logger.error("appUser is null");
			return;
		} else {
			logger.info(appUser.toString());
			translateService.setToken(appUser.getToken());
			weixinService.setAppId(appUser.getAppId());
			weixinService.setAppSecret(appUser.getAppSecret());
		}

		WeixinService ws = new WeixinService();
		ws.setAppId("wx6919f6fac2525c5f");
		ws.setAppSecret("4888a5883fb856751d52629b4923d11d");
		String token = ws.getAccessToken();
		System.out.println(token);

		String selectMenu = ws.selectMenu(token);

		logger.info("selectMenu:" + selectMenu);

		String result = ws.createMenu(menu, token);
		logger.info("result:" + result);
	}

	/**
	 * 组装菜单数据
	 * 
	 * @return
	 */
	private static Menu getMenu() {
		ClickButton b11 = new ClickButton();
		b11.setKey("KJHM-F47104");
		b11.setName("双色球");
		b11.setType("click");

		ClickButton b12 = new ClickButton();
		b12.setKey("KJHM-F47103");
		b12.setName("福彩3D");
		b12.setType("click");
		ClickButton b13 = new ClickButton();
		b13.setKey("KJHM-F47102");
		b13.setName("七乐彩");
		b13.setType("click");
		ClickButton b14 = new ClickButton();
		b14.setKey("KJHM-T01001");
		b14.setName("大乐透");
		b14.setType("click");
		ViewButton bother = new ViewButton();
		bother.setName("更多");
		bother.setType("view");
		bother.setUrl("http://iphone.ruyicai.com/html/lottery.html");

		ClickButton b15 = new ClickButton();
		b15.setKey("KJHM-T01002");
		b15.setName("排列三");
		b15.setType("click");
		ClickButton b16 = new ClickButton();
		b16.setKey("KJHM-T01011");
		b16.setName("排列五");
		b16.setType("click");
		ClickButton b17 = new ClickButton();
		b17.setKey("KJHM-T01009");
		b17.setName("七星彩");
		b17.setType("click");
		ClickButton b18 = new ClickButton();
		b18.setKey("KJHM-T01013");
		b18.setName("22选5");
		b18.setType("click");

		ViewButton b21 = new ViewButton();
		b21.setName("购彩大厅");
		b21.setType("view");
		b21.setUrl("http://iphone.ruyicai.com/index.html");

		ViewButton b22 = new ViewButton();
		b22.setName("合买中心");
		b22.setType("view");
		b22.setUrl("http://iphone.ruyicai.com/html/tog.html");

		// ViewButton b23 = new ViewButton();
		// b23.setName("合买活动");
		// b23.setType("view");
		// b23.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://iphone.ruyicai.com/html/tog.html&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
		// ViewButton b24 = new ViewButton();
		// b24.setName("UC绑定");
		// b24.setType("view");
		// b24.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6919f6fac2525c5f&redirect_uri=http://iphone.ruyicai.com/html/tog.html&response_type=code&scope=snsapi_base&state=1#wechat_redirect");

		ViewButton b31 = new ViewButton();
		b31.setName("彩民趣闻");
		b31.setType("view");
		b31.setUrl("http://iphone.ruyicai.com/html/more/newslist.html");
		ViewButton b32 = new ViewButton();
		b32.setName("专家推荐");
		b32.setType("view");
		b32.setUrl("http://iphone.ruyicai.com/html/more/newslist.html");

		ViewButton b33 = new ViewButton();
		b33.setName("优惠活动");
		b33.setType("view");
		b33.setUrl("http://iphone.ruyicai.com/html/more/active.html");

		ViewButton b34 = new ViewButton();
		b34.setName("APP下载");
		b34.setType("view");
		b34.setUrl("http://iphone.ruyicai.com/html/download.html");

		ViewButton b35 = new ViewButton();
		b35.setName("我要留言");
		b35.setType("view");
		b35.setUrl("http://iphone.ruyicai.com/html/more/tickling.html");

		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("开奖公告");
		mainBtn1.setSub_button(new Button[] { b11, b12, b13, b14, bother });

		ComplexButton mainBtn2 = new ComplexButton();
		mainBtn2.setName("购买彩票");
		mainBtn2.setSub_button(new ViewButton[] { b21, b22, b34 });

		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("彩票信息");
		mainBtn3.setSub_button(new ViewButton[] { b31, b32, b33, b35 });

		Menu menu = new Menu();
		menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });
		return menu;
	}
}
