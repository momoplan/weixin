package com.ruyicai.weixin.util;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileUtil {

	/**
	 * 获得webapp/images下的文件
	 * 
	 * @param fileName 图片名称 lcx.jpg
	 * @return
	 * @throws IOException
	 */
	public static File getResourceImage(String fileName) throws IOException {
		String path = ImageMagickTools.class.getClassLoader().getResource("").toString();
		Integer index = path.indexOf("/WEB-INF/classes/");
		path = path.substring(6, index);
		Resource image = new FileSystemResource("/" + path + "/images/" + fileName);
		return image.getFile();
	}

	/**
	 * 获得开奖信息图片的绝对路径
	 * 
	 * @param fileName 图片名称
	 * @return
	 */
	public static String getWininfoImagePath(String fileName) {
		return getRootPath() + "/images/wininfo/" + fileName;
	}

	/**
	 * 获得字体文件的绝对路径
	 * 
	 * @return
	 */
	public static String getFontPath() {
		return getRootPath() + "/fonts/msyh.ttf";
	}

	public static String getRootPath() {
		String classPath = FileUtil.class.getClassLoader().getResource("/").getPath();
		String rootPath = "";
		// windows下
		if ("\\".equals(File.separator)) {
			rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("/", "\\");
		}
		// linux下
		if ("/".equals(File.separator)) {
			rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("\\", "/");
		}
		return rootPath;
	}

	public static String formatePath(String path) {
		// windows下
		if ("\\".equals(File.separator)) {
			path = path.replace("/", "\\");
		}
		// linux下
		if ("/".equals(File.separator)) {
			path = path.replace("\\", "/");
		}
		return path;
	}
}
