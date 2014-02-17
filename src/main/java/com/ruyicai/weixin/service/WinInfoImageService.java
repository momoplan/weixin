package com.ruyicai.weixin.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.im4java.core.IM4JavaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.weixin.dto.lottery.ResponseData;
import com.ruyicai.weixin.util.DateUtil;
import com.ruyicai.weixin.util.FileUtil;
import com.ruyicai.weixin.util.ImageMagickTools;
import com.ruyicai.weixin.util.JsonMapper;

@Service
public class WinInfoImageService {

	private Logger logger = LoggerFactory.getLogger(WinInfoImageService.class);
	
	@Value("${lotteryurl}")
	private String lotteryurl;
	
	@Value("${imageLocation}")
	private String imageLocation;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ImageMagickTools imageMagickTools;
	
	/**
	 * 下载开奖信息图片
	 * @param lotno			彩种
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	@SuppressWarnings("unchecked")
	public void downLoadWinInfo(String lotno, HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException, IM4JavaException {
		if(StringUtils.isBlank(lotno)) {
			throw new IllegalArgumentException("The argument lotno is required.");
		}
		logger.info("lotno="+lotno);
		String batchcode;
		String winbasecode;
		String winspecialcode;
		Date opentime;
		String opentimeStr;
		String info;
		String[] infos;
		String[] amts;
		String[] prizes;
		String dest;
		String url = lotteryurl + "/select/getTwininfoBylotno";
		String json = Request.Post(url).bodyForm(Form.form().add("lotno", lotno).add("issuenum", "1").build())
				.execute().returnContent().asString();
		ResponseData responseData = JsonMapper.fromJson(json, ResponseData.class);
		if (responseData.getErrorCode().equals("0")) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) responseData.getValue();
			if(list != null && list.size() == 1) {
				Map<String, Object> map = list.get(0);
				Map<String, Object> id = (Map<String, Object>) map.get("id");
				batchcode = (String) id.get("batchcode");	//期号
				winbasecode = (String) map.get("winbasecode");	//基本号码
				winspecialcode = (String) map.get("winspecialcode");	//特殊号码
				opentime = new Date((Long)map.get("opentime"));	//开奖时间 
				opentimeStr = DateUtil.format(opentime);
				info = (String) map.get("info");//40468407200_40468407200_3649141100,1_47_526300100;2_447_4148000;3_4923_300000;4_156874_20000;5_2170766_1000;6_13727660_500;7_0_0;8_0_0;9_0_0;10_0_0
				if(StringUtils.isNotBlank(info)) {
					infos = info.split(",");
					amts = infos[0].split("_");	//销售总额_销售总额_奖池累计金额
					prizes = infos[1].split(";"); //奖项_中奖注数_单注奖金
					dest = imageLocation + "\\wininfo\\" + lotno + "_" + batchcode + ".png";
				} else {
					info = "0_0_0,1_0_0;2_0_0;3_0_0;4_0_0;5_0_0;6_0_0;7_0_0;8_0_0;9_0_0;10_0_0";
					infos = info.split(",");
					amts = infos[0].split("_");	
					prizes = infos[1].split(";"); 
					dest = imageLocation + "\\wininfo\\" + lotno + "_" + batchcode + "_nullinfo.png";
				}
				
				dest = FileUtil.formatePath(dest);
				File file = new File(dest);
				if(file.exists() == true) {
					fileService.downloadFile(dest, request, response);
				} else {
					if(lotno.equals("F47104")) {
						imageMagickTools.drawSSQWinInfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("F47103")) {
						imageMagickTools.draw3DWinInfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("F47102")) {
						imageMagickTools.drawQLCWinInfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("T01001")) {
						imageMagickTools.drawDLTWininfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("T01002")) {
						imageMagickTools.drawPLSWininfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("T01011")) {
						imageMagickTools.drawPLWWininfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("T01009")) {
						imageMagickTools.drawQXCWininfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else if(lotno.equals("T01013")) {
						imageMagickTools.drawESEXWWininfo(dest, batchcode, winbasecode, winspecialcode, opentimeStr, amts, prizes);
					} else {
						throw new IllegalArgumentException("The argument lotno is illegal.");
					}
					fileService.downloadFile(dest, request, response);
				}
			} else {
				throw new IllegalArgumentException("Argument error");
			}
		}
	}
}
