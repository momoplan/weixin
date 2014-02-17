package com.ruyicai.weixin;


import java.io.IOException;

import org.im4java.core.IM4JavaException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.ruyicai.weixin.service.WinInfoImageService;

@ContextConfiguration(locations = { "classpath*:/META-INF/spring/applicationContext.xml" })
public class WinInfoImageServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private WinInfoImageService winInfoImageService;
	@Test
	public void test() {
		try {
			winInfoImageService.downLoadWinInfo("F47104", null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IM4JavaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
