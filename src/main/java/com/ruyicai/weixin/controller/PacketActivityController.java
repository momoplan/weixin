package com.ruyicai.weixin.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map; 

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import Decoder.BASE64Decoder;

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.CaseLotActivityService;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.StringUtil;

@RequestMapping(value = "/packetactivity")
@Controller
public class PacketActivityController {

	private Logger logger = LoggerFactory
			.getLogger(PacketActivityController.class);

	@Autowired
	PacketActivityService packetActivityService;

	@Autowired
	CaseLotActivityService caseLotActivityService;

	@RequestMapping(value = "/createPacket", method = RequestMethod.GET)
	@ResponseBody
	public String createPacket(
			@RequestParam(value = "packet_userno", required = true) String packet_userno,
			@RequestParam(value = "parts", required = true) String parts,
			@RequestParam(value = "punts", required = true) String punts,
			@RequestParam(value = "greetings", required = true) String greetings,
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		logger.info(
				"微信公众帐号红包活动创建红包：packet_userno:{},parts:{},punts:{},greetings:{}",
				packet_userno, parts, punts, greetings);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(packet_userno) || StringUtil.isEmpty(parts)
				|| StringUtil.isEmpty(punts)) {
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}

		int puntsInt = Integer.valueOf(punts);
		if (puntsInt > 1000) {
			rd.setErrorCode("10002");
			rd.setValue("创建红包注数不能大于1000注");
			return JsonMapper.toJsonP(callback, rd);
		} else if (puntsInt < 1) {
			rd.setErrorCode("10003");
			rd.setValue("创建红包注数不能小于1注");
			return JsonMapper.toJsonP(callback, rd);
		}

		int partsInt = Integer.valueOf(parts);
		if (partsInt > puntsInt) {
			rd.setErrorCode("10004");
			rd.setValue("红包份数不能大于红包注数");
			return JsonMapper.toJsonP(callback, rd);
		} else if (partsInt < 1) {
			rd.setErrorCode("10005");
			rd.setValue("红包份数不能小于1份");
			return JsonMapper.toJsonP(callback, rd);
		}

		try {
			Packet packet = packetActivityService.doCreatePacket(packet_userno,
					partsInt, puntsInt, greetings);
			Map<String, String> json = new HashMap<String, String>();
			json.put("userno", packet.getPacketUserno());
			json.put("packet_id", String.valueOf(packet.getId()));
			json.put("punts", String.valueOf(packet.getTotalPunts()));
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(json);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("createPacket error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@RequestMapping(value = "/getPuntsFromPacket", method = RequestMethod.GET)
	@ResponseBody
	public String getpuntsfrompacket(
			@RequestParam(value = "award_userno", required = false) String award_userno,
			@RequestParam(value = "channel", required = false) String channel,
			@RequestParam(value = "packet_id", required = false) String packet_id,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("getPuntsFromPacket award_userno:{} packet_id：{} ",
				award_userno, packet_id);

		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(award_userno)
					|| StringUtils.isEmpty(packet_id)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid or userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}

			// 判断用户是否存在
			caseLotActivityService.caseLotchances(award_userno,
					Const.WX_PACKET_ACTIVITY);
			int status = packetActivityService.getPacketStatus(award_userno,
					packet_id);
			if (status == 0) {
				Map<String, Object> imap = packetActivityService.getPunts(
						award_userno, channel, packet_id);

				rd.setErrorCode(ErrorCode.OK.value);
				rd.setValue(imap);
			} else {
				String value = "";
				if (status == 1) {
					value = "红包已抢完";
				} else if (status == 2) {
					value = "你已抢过红包";
				} else {
					value = "不能抢自己发的红包";
				}
				rd.setValue(value);
				rd.setErrorCode(String.valueOf(status));
			}
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("getPuntsFromPacket error2", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPacketStatus", method = RequestMethod.GET)
	@ResponseBody
	public String getPacketStatus(
			@RequestParam(value = "award_userno", required = false) String award_userno,
			@RequestParam(value = "packet_id", required = false) String packet_id,
			@RequestParam(value = "callBackMethod", required = false) String callback) {
		logger.info("getPacketStatus award_userno:{} packet_id：{} ",
				award_userno, packet_id);
		ResponseData rd = new ResponseData();
		try {
			if (StringUtils.isEmpty(award_userno)
					|| StringUtils.isEmpty(packet_id)) {
				rd.setErrorCode("10001");
				rd.setValue("参数错误the argument orderid or userno is require.");
				return JsonMapper.toJsonP(callback, rd);
			}

			Map<String, String> map = packetActivityService.doGetPacketStus(
					award_userno, packet_id);

			rd.setErrorCode(ErrorCode.OK.value);

			rd.setValue(map);

		} catch (WeixinException e) {
			logger.error("getPacketStatus error1", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("getPacketStatus error2", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	// 送到的红包列表
	@RequestMapping(value = "/getPacketList", method = RequestMethod.GET)
	@ResponseBody
	public String getPacketList(
			@RequestParam(value = "packet_userno", required = true) String packet_userno,
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		logger.info("getPacketList packet_userno:{}", packet_userno);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(packet_userno)) {
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}

		try {
			rd.setValue(packetActivityService.doGetPacketList(packet_userno));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("getPacketList error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@RequestMapping(value = "/getPacketInfo", method = RequestMethod.GET)
	@ResponseBody
	public String getPacketInfo(
			@RequestParam(value = "userno", required = true) String userno,
			@RequestParam(value = "packet_id", required = true) String packet_id,
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		logger.info("getPacketInfo userno:{}, packet_id:{}", userno, packet_id);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(userno) || StringUtil.isEmpty(packet_id)) {
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}

		try {
			rd.setValue(packetActivityService
					.doGetPacketInfo(userno, packet_id));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("getPacketInfo error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@RequestMapping(value = "/thankTa", method = RequestMethod.GET)
	@ResponseBody
	public String thankTa(
			@RequestParam(value = "award_userno", required = true) String award_userno,
			@RequestParam(value = "thank_words", required = true) String thank_words,
			@RequestParam(value = "packet_id", required = true) String packet_id,
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		logger.info("thankTa award_userno:{}, thank_words:{}, packet_id:{}",
				award_userno, award_userno, packet_id);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(award_userno) || StringUtil.isEmpty(thank_words)
				|| StringUtil.isEmpty(packet_id)) {
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}

		try {
			packetActivityService.doThankTa(award_userno, thank_words,
					packet_id);
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(ErrorCode.OK.memo);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("thankTa error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	// 领取的列表
	@RequestMapping(value = "/getMyPunts", method = RequestMethod.GET)
	@ResponseBody
	public String getMyPunts(
			@RequestParam(value = "award_userno", required = true) String award_userno,
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		logger.info("getMyPunts award_userno:{}", award_userno);
		ResponseData rd = new ResponseData();
		if (StringUtil.isEmpty(award_userno)) {
			rd.setErrorCode("10001");
			rd.setValue("参数不能为空");
			return JsonMapper.toJsonP(callback, rd);
		}

		try {
			rd.setValue(packetActivityService.doGetMyPunts(award_userno));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("getMyPunts error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@RequestMapping(value = "/getActivityEnv", method = RequestMethod.GET)
	@ResponseBody
	public String getActivityEnv(
			@RequestParam(value = "callBackMethod", required = true) String callback) {
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(packetActivityService.doGetActivityEnv());
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (WeixinException e) {
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getErrorCode().memo);
		} catch (Exception e) {
			logger.error("getActivityEnv error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(ErrorCode.ERROR.memo);
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	@RequestMapping(value = "/returnAllLeftPunts", method = RequestMethod.GET)
	@ResponseBody
	public String returnAllLeftPunts(
			@RequestParam(value = "callBackMethod", required = true) String callback) {

		ResponseData rd = new ResponseData();
		try {

			int returnPunts = packetActivityService.returnAllLeftPunts();
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(returnPunts);

		} catch (WeixinException e) {
			logger.error("findReturnPacketList error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("findReturnPacketList error", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return JsonMapper.toJsonP(callback, rd);
	}

	public String readTxtFile(String filePath) {
		String ret = "";
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
					ret += lineTxt;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

		return ret;

	}

	public boolean GenerateImage(String imgStr, String path) { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String imgFilePath = path;// 新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
	}

	@RequestMapping(value = "/upload")
	@ResponseBody
	public void uploadorginimg(
			@RequestParam(value = "data", required = false) String file,
			@RequestParam(value = "userno", required = false) String userno,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("开始");
		String data = "";
		if (file.length() > 0)
			data = file;
		else
			data = readTxtFile("c:\\Dev\\canvas.txt");

		// logger.info("data:"+data);
		logger.info("userno:" + userno);

		String path = this.getClass().getResource("/../../").toString();
		logger.info("path:" + path);
		path = path.replace("file:/", "");
		logger.info("path.indexOf(:)==0:"
				+ String.valueOf(path.indexOf(":") == 0));
		if (path.indexOf(":") == -1) {
			path = "/" + path;
			logger.info("path2:" + path);
		}

		path = path.replace("weixin", "settingimg");
		logger.info("path5:" + path);
		System.out.println("path4:" + path);

		String format = "jpg";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
		String name = sdf.format(new Date());
		String allPath = path + name + "." + format;

		logger.info("GenerateImage pre");
		if (GenerateImage(data, allPath))
			;
		{
			logger.info("GenerateImage done");
			try {
				CaseLotUserinfo clUserInfo = caseLotActivityService
						.findOrCreateCaseLotUserinfo(userno, "HM00002", "", "");
				clUserInfo.setSettingImgurl(name + "." + format);
				clUserInfo.merge();
			} catch (Exception ex) {
				logger.info("caseLotActivityService.findOrCreateCaseLotUserinfo userno:"
						+ userno);
				throw new WeixinException(ErrorCode.ERROR);
			}
			String redirectURL = "http://192.168.30.80:8080/html5/wechart/wxpay/sendfriend.html?urlname="
					+ "http://"
					+ request.getLocalAddr()
					+ ":"
					+ request.getLocalPort()
					+ "/settingimg/"
					+ name
					+ "."
					+ format;

			try {
				response.sendRedirect(redirectURL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// return
			// "http://"+request.getLocalAddr()+":"+request.getLocalPort()+"/weixin/"+
			// name+"."+format;
		}

	}

	@RequestMapping(value = "/uploadorginimg")
	@ResponseBody
	public void upload(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "userno", required = false) String userno,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("开始");
		String path = this.getClass().getResource("/../../").toString();
		logger.info("path:" + path);
		path = path.replace("file:/", "");
		logger.info("path.indexOf(:)==0:"
				+ String.valueOf(path.indexOf(":") == 0));
		if (path.indexOf(":") == -1) {
			path = "/" + path;
			logger.info("path2:" + path);
		}

		logger.info("");
		path = path.replace("weixin", "images");
		logger.info("path5:" + path);
		System.out.println("path4:" + path);
		BufferedImage input;
		String allPath = "";
		String name = "";
		String format = "jpg";
		try {
			CommonsMultipartFile cf = (CommonsMultipartFile) file;
			DiskFileItem fi = (DiskFileItem) cf.getFileItem();
			File f1 = fi.getStoreLocation();
			input = ImageIO.read(f1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
			name = sdf.format(new Date());

			allPath = path + name + "." + format;
			System.out.println("allPath:" + allPath);
			File f = new File(allPath);
			ImageIO.write(input, format, f);
		} catch (IOException e) {
			logger.info("/uploadorginimg1:" + e.getMessage());
			e.printStackTrace();
		}

		System.out.println(allPath);

		String redirectURL = "http://" + request.getLocalAddr() + ":"
				+ request.getLocalPort() + "/uploadimg/paipai.html?userno="
				+ userno + "&urlname=" + "http://" + request.getLocalAddr()
				+ ":" + request.getLocalPort() + "/images/" + name + "."
				+ format;

		try {
			response.sendRedirect(redirectURL);
		} catch (IOException e) {
			logger.info("/uploadorginimg:" + e.getMessage());
			e.printStackTrace();
		}

		// return
		// "<script>parent.callback('http://"+request.getLocalAddr()+":"+request.getLocalPort()+"/weixin/"+
		// name+"."+format +"')</script>";
	}

}
