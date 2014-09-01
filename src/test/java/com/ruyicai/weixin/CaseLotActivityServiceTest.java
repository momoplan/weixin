package com.ruyicai.weixin;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.service.CaseLotActivityService;

@ContextConfiguration(locations = { "classpath*:/META-INF/spring/applicationContext.xml" })
public class CaseLotActivityServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private CaseLotActivityService caseLotActivityService;

	@Test
	public void testActivity() throws InterruptedException {
		CaseLotUserinfo u1 = caseLotActivityService.findOrCreateCaseLotUserinfo("00007", "HM00001", "test1", "hehe1","");
		Assert.assertNotNull(u1);
		Assert.assertEquals(u1.getChances(), 1);
		CaseLotUserinfo u2 = caseLotActivityService.findOrCreateCaseLotUserinfo("00008", "HM00001", "test2", "hehe2","");
		Assert.assertNotNull(u2);
		Assert.assertEquals(u2.getChances(), 1);
		CaseLotUserinfo u3 = caseLotActivityService.joinActivity("00007", "HM00001", null);
		Assert.assertEquals(u3.getChances(), 0);
		Assert.assertEquals(u3.getJoinTimes(), u1.getJoinTimes() + 1);

		caseLotActivityService.createChanceDetail("00007", "00008", "HM00001");
		CaseLotUserinfo u4 = caseLotActivityService.joinActivity("00008", "HM00001", "00007");
		Assert.assertEquals(u4.getChances(), 0);
		Thread.sleep(3 * 1000);
		CaseLotUserinfo u5 = caseLotActivityService.findOrCreateCaseLotUserinfo("00007", "HM00001", "test1", "hehe1","");
		Assert.assertEquals(u5.getLinkTimes(), u1.getLinkTimes() + 1);
	}
	
	

}
