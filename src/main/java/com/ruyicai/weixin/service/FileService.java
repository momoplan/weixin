package com.ruyicai.weixin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.im4java.core.IM4JavaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.util.ImageMagickTools;

@Service
public class FileService {

	private Logger logger = LoggerFactory.getLogger(FileService.class);

	@Value("${imageLocation}")
	private String imageLocation;

	@Autowired
	private ImageMagickTools imageMagickTools;

	/**
	 * 下载图片文件夹下面的图片
	 * 
	 * @param fileName lcx.jpg
	 * @param width
	 * @param height
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void getImage(String fileName, Integer width, Integer height, HttpServletRequest request,
			HttpServletResponse response) throws IOException, InterruptedException, IM4JavaException {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("The argument fileName is required.");
		}
		String originalSrc = imageLocation + "\\" + fileName;
		String src = "";
		if (width == null && height == null) {
			src = originalSrc;
		} else {
			if (width == null || height == null) {
				throw new IllegalArgumentException("图片的宽度必须同时存在，或者同时为null。");
			} else {
				String[] splits = fileName.split("\\.");
				src = imageLocation + "\\" + splits[0] + "_" + width + "_" + height + "." + splits[1];
			}
		}
		File file = new File(src);
		if (file.exists() == false) { // 目标尺寸图片不存在，则由原图创建目标尺寸
			file = new File(originalSrc);
			if (file.exists() == false) {
				throw new IllegalArgumentException("请求的文件不存在");
			}
			imageMagickTools.cutImage(originalSrc, src, width, height);
		}

		// 读到流中
		InputStream inStream = new FileInputStream(src);
		// 设置输出的格式
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		// 循环取出流中的数据
		byte[] b = new byte[100];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			logger.error("输出文件错误", e);
		}
	}

	/**
	 * 文件下载方法
	 * 
	 * @param src 文件绝对路径
	 * @param request
	 * @param response
	 * @throws FileNotFoundException
	 */
	public void downloadFile(String src, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		File file = new File(src);
		if (file.exists() == false) {
			throw new IllegalArgumentException("不存在" + src);
		}

		logger.info("图片目录src：" + src);// /home/appusr/images_weixin/wininfo/T01001_2014045.png
		String filename = src.substring(src.lastIndexOf("/"), src.length());
		// 读到流中
		InputStream inStream = new FileInputStream(src);
		// 设置输出的格式
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		// 循环取出流中的数据
		byte[] b = new byte[100];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			logger.error("输出文件错误", e);
		}
	}
}
