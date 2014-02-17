package com.ruyicai.weixin.util;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * ImageMagick工具的简单封装，实现一些基本的图片操作。
 * 如，生成双色球开奖号码的图片。
 * 
 * @author LiChenxing
 * @date 2013年10月16日 上午9:42:08
 */
@Service
public class ImageMagickTools {
	
	
	@Value("${imageMagickPath}")
	public String imageMagickPath;
	
	/**
	 * 根据坐标裁剪图片
	 * @param srcPath			要裁剪的图片路径
	 * @param newPath		裁剪后图片的路径
	 * @param x					起始横坐标
	 * @param y					起始纵坐标
	 * @param x1					结束横坐标
	 * @param y1					结束纵坐标
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public void cutImage(String srcPath, String newPath, int x, int y, int x1, int y1) throws IOException, InterruptedException, IM4JavaException {
		int width = x1 - x;
		int height = y1 - y;
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		
		op.crop(width, height, x, y);
		op.addImage(newPath);
		
		ConvertCmd cmd = new ConvertCmd();
		//Linux下不要设置该值
		if(StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}
		
		cmd.run(op);
	}
	
	/**
	 * 根据尺寸缩放图片
	 * @param srcPath			源图路径
	 * @param newPath		缩放后图片的路径
	 * @param width				缩放后图片的宽度
	 * @param height			缩放后图片的高度
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public void cutImage(String srcPath, String newPath, int width, int height) throws IOException, InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		
		op.resize(width, height);
		op.addImage(newPath);
		
		ConvertCmd cmd = new ConvertCmd();
		//Linux下不要设置该值
		if(StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}
		
		cmd.run(op);
	}
	
	/**
	 * 根据宽度缩放
	 * @param srcPath			源图路径
	 * @param newPath		缩放后图片路径
	 * @param width				缩放后图片宽度
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public void cutImage(String srcPath, String newPath, int width) throws IOException, InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		
		op.resize(width, null);
		op.addImage(newPath);
		
		ConvertCmd cmd = new ConvertCmd();
		//Linux下不要设置该值
		if(StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}
		
		cmd.run(op);
	}
	
	/**
	 * 给图片增加水印
	 * @param srcPath		源图路径
	 * @param text			水印文字
	 * @param fontSize		文字大小 20
	 * @param color 			颜色（水印#BCBFC8）
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public void addImageText(String srcPath, int fontSize, String color, String text) throws IOException, InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font("宋体").pointsize(fontSize).fill(color).draw("text 100,100 " + text);
		op.addImage();
		op.addImage();
		
		ConvertCmd cmd = new ConvertCmd();
		//Linux下不要设置该值
		if(StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}
		
		cmd.run(op, srcPath, srcPath);
	}
	
	/**
	 * 双色球开奖图片生成
	 * @param srcPath							源图地址
	 * @param newPath						生成图片地址
	 * @param winbasecodes				红球号码
	 * @param winspecialcodes				蓝球号码
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public void addDrawNumberToBall(String srcPath, String newPath, String[] winbasecodes, String[] winspecialcodes) throws IOException, InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font("宋体").pointsize(20).fill("#ffffff");
		int x = 12;
		int y = 33;
		for(int t = 0; t < winbasecodes.length; t++) {
			for(int i = 0; i < 12;) {
				op.draw("text " + x + "," + y + " '" + winbasecodes[t].substring(i, i + 2) + "'");
				i = i + 2;
				x = x + 42;
			}
			op.draw("text " + x + "," + y + " '" + winspecialcodes[t] + "'");
			x = 12;
			y = y + 30;
		}
		op.addImage();
		op.addImage();
		
		ConvertCmd cmd = new ConvertCmd();
		//Linux下不要设置该值
		if(StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}
		
		cmd.run(op,srcPath,newPath);
	}
	
	/**
	 * 生成双色球开奖图片
	 * @param newPath
	 * @param batchcode
	 * @param winbasecode
	 * @param winspecialcode
	 * @param opentimeStr
	 * @param amts
	 * @param prizes
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public void drawSSQWinInfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		op.fill("#ffffff");
		x = 346;
		y = 152;
		for(int i = 0; i < 12;) {
			op.draw("text " + x + "," + y + " '" + winbasecode.substring(i, i + 2) + "'");
			i = i + 2;
			x = x + 41;
		}
		op.draw("text " + x + "," + y + " '" + winspecialcode + "'");
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(32);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 60;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "二等奖" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("F47104.png"), newPath);
	}
	
	/**
	 * 福彩3D开奖图片生成
	 */
	public void draw3DWinInfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		op.fill("#ffffff");
		x = 346;
		y = 152;
		for(int i = 0; i < 6;) {
			op.draw("text " + x + "," + y + " '" + winbasecode.substring(i, i + 2) + "'");
			i = i + 2;
			x = x + 41;
		}
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 60;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "组三" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("F47103.png"), newPath);
	}
	
	/**
	 * 七乐彩开奖图片生成
	 */
	public void drawQLCWinInfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		op.fill("#ffffff");
		x = 346;
		y = 152;
		for(int i = 0; i < 14;) {
			op.draw("text " + x + "," + y + " '" + winbasecode.substring(i, i + 2) + "'");
			i = i + 2;
			x = x + 41;
		}
		op.draw("text " + x + "," + y + " '" + winspecialcode + "'");
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 60;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "二等奖" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("F47102.png"), newPath);
	}
	
	/**
	 * 生成大乐透开奖图片
	 */
	public void drawDLTWininfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		String[] wincodes = winbasecode.split("\\+");
		String[] winbasecodes = wincodes[0].split(" ");
		String[] winspecialcodes = wincodes[1].split(" ");
		
		op.fill("#ffffff");
		x = 346;
		y = 152;
		for(int i = 0; i < 5; i++) {
			op.draw("text " + x + "," + y + " '" + winbasecodes[i] + "'");
			x = x + 41;
		}
		op.draw("text " + x + "," + y + " '" + winspecialcodes[0] + "'");
		x = x + 41;
		op.draw("text " + x + "," + y + " '" + winspecialcodes[1] + "'");
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 50;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖基本" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 50;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "二等奖基本" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("T01001.png"), newPath);
	}
	
	/**
	 * 生成排列三图片
	 * */
	public void drawPLSWininfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		op.fill("#ffffff");
		x = 355;
		y = 152;
		for(int i = 0; i < 3; i++) {
			op.draw("text " + x + "," + y + " '" + winbasecode.substring(i, i + 1) + "'");
			x = x + 41;
		}
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 60;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "组三" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("T01002.png"), newPath);
	}
	
	/**
	 * 生成排列五图片
	 * */
	public void drawPLWWininfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		op.fill("#ffffff");
		x = 355;
		y = 152;
		for(int i = 0; i < 5; i++) {
			op.draw("text " + x + "," + y + " '" + winbasecode.substring(i, i + 1) + "'");
			x = x + 41;
		}
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("T01011.png"), newPath);
	}
	
	/**
	 * 生成七星彩开奖图片
	 * */
	public void drawQXCWininfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		op.fill("#ffffff");
		x = 355;
		y = 152;
		for(int i = 0; i < 7; i++) {
			op.draw("text " + x + "," + y + " '" + winbasecode.substring(i, i + 1) + "'");
			x = x + 41;
		}
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 60;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "二等奖" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("T01009.png"), newPath);
	}
	
	/**
	 * 生成二十二选五开奖图片
	 * */
	public void drawESEXWWininfo(String newPath, String batchcode,
			String winbasecode, String winspecialcode, String opentimeStr,
			String[] amts, String[] prizes) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font(FileUtil.getFontPath()).pointsize(27).fill("#000000");
		int x = 345;
		int y = 83;
		op.draw("text " + x + "," + y + " '" + batchcode + "'");
		
		y = 117;
		op.draw("text " + x + "," + y + " '" + opentimeStr + "'");
		
		String[] winbasecodes = winbasecode.split(" ");
		
		op.fill("#ffffff");
		x = 348;
		y = 152;
		for(int i = 0; i < 5; i++) {
			op.draw("text " + x + "," + y + " '" + winbasecodes[i] + "'");
			x = x + 41;
		}
		
		op.fill("#000000");
		x = 444;
		y = 190;
		Long saleTotal = Long.valueOf(amts[0]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(saleTotal.toString()) + "元" + "'");
		
		x = 392;
		y = 224;
		Long prizePool = Long.valueOf(amts[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(prizePool.toString()) + "元" + "'");
		
		x = 60;
		y = 313;
		op.fill("#ffffff").pointsize(28);
		op.draw("text " + x + "," + y + " '" + "一等奖" + "'");
		x = 268;
		String[] prize_1 = prizes[0].split("_");
		op.draw("text " + x + "," + y + " '" + prize_1[1] + "注" + "'");
		x = 505;
		Long firstPrize = Long.valueOf(prize_1[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(firstPrize.toString()) + "元" + "'");
		
		x = 60;
		y = 353;
		op.draw("text " + x + "," + y + " '" + "二等奖" + "'");
		x = 268;
		String[] prize_2 = prizes[1].split("_");
		op.draw("text " + x + "," + y + " '" + prize_2[1] + "注" + "'");
		x = 505;
		Long secondPrize = Long.valueOf(prize_2[2]) / 100;
		op.draw("text " + x + "," + y + " '" + prizeFormate(secondPrize.toString()) + "元" + "'");
		
		op.addImage();
		op.addImage();

		ConvertCmd cmd = new ConvertCmd();
		// Linux下不要设置该值
		if (StringUtils.isNotBlank(imageMagickPath)) {
			cmd.setSearchPath(imageMagickPath);
		}

		cmd.run(op, FileUtil.getWininfoImagePath("T01013.png"), newPath);
	}
	
	public String prizeFormate(String str) {
		DecimalFormat df=new DecimalFormat(",###");
		return df.format(Integer.valueOf(str));
	}
}
