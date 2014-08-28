package com.ruyicai.weixin.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileSystemView;

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

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.PacketDao;
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

			Map<String, String> map = packetActivityService.doGetPacketStus(
					award_userno, packet_id);

			rd.setErrorCode(ErrorCode.OK.value);

			rd.setValue(map);

		} catch (WeixinException e) {
			logger.error("getPacketStatus error", e);
			rd.setErrorCode(e.getErrorCode().value);
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("getPacketStatus error", e);
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

	@RequestMapping(value = "/upload")
	@ResponseBody
	public String upload(
			@RequestParam(value = "file", required = false) MultipartFile file) {
		System.out.println("开始");
		// String path = "";
		String path = "c:\\Dev\\upload";
		String fileName = file.getOriginalFilename();
		int x = 0;
		int y = 0;
		int w = 200;
		int h = 200;

		BufferedImage input;
		try {
			
			 
	        CommonsMultipartFile cf= (CommonsMultipartFile)file; 
	        DiskFileItem fi = (DiskFileItem)cf.getFileItem(); 
	        File f1 = fi.getStoreLocation();
			input = ImageIO.read(f1);

			BufferedImage saveImage = input.getSubimage(x, y, w, h);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
			String name = sdf.format(new Date());

			String format = "jpg";
			File f = new File(path + File.separator + name + "." + format);

			ImageIO.write(saveImage, format, f);

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(fileName);
		// System.out.println(path);
		// // String fileName = new Date().getTime()+".jpg";
		// // System.out.println(path);
		// File targetFile = new File(path, fileName);
		// if (!targetFile.exists()) {
		// targetFile.mkdirs();
		// }
		// //
		// // 保存
		// try {
		// file.transferTo(targetFile);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// model.addAttribute("fileUrl",
		// request.getContextPath()+"/upload/"+fileName);
		//
		return "result";
	}

}
