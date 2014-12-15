package com.ruyicai.weixin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.ruyicai.weixin.dao.OrderLotInfoDao;
import com.ruyicai.weixin.service.LotteryService;

@ContextConfiguration(locations = { "classpath*:/META-INF/spring/applicationContext.xml" })
public class LotteryServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private LotteryService lotteryService;
	
	
    @Autowired
    private OrderLotInfoDao orderLotInfoDao;

	@Test
	public void selectTwininfoBylotnoTest() {
		String string = lotteryService.selectTwininfoBylotno("F47104", "3");
		System.out.println(string);
	}

//	@Test
//	public void testFindOrCreateBigUser() {
//		String userno = lotteryService.findOrCreateBigUser("123123123123", null, Const.DEFAULT_BIGUSER_TYPE);
//		System.out.println(userno);
//	}
	
	 @Test
  public void testFindOrCreateBigUser() {
	     
       
  }

}
