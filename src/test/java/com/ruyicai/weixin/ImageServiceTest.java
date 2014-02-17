package com.ruyicai.weixin;


import java.io.IOException;

import org.im4java.core.IM4JavaException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.ruyicai.weixin.service.FileService;

@ContextConfiguration(locations = { "classpath*:/META-INF/spring/applicationContext.xml" })
public class ImageServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private FileService imageService;
	
	@Test
	public void test() {
		try {
			long startMills = System.currentTimeMillis();
			imageService.getImage("Koala.jpg", 640, 480, null, null);
			System.out.println(System.currentTimeMillis() - startMills);
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
	
	@Test
	public void test2() {
		String str = "lcx.jpg";
		String[] arr = str.split("\\.");
		System.out.println(arr.length);
	}

}
