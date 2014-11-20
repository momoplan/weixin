package com.ruyicai.weixin.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.ruyicai.weixin.service.CommonService;
import com.ruyicai.weixin.service.LotteryService;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.JsonMapper;
import com.ruyicai.weixin.util.StringUtil;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@RequestMapping(value = "/packetactivity")
@Controller
public class PacketActivity2Controller {

    private Logger logger = LoggerFactory.getLogger(PacketActivity2Controller.class);

    @Autowired
    PacketActivityService packetActivityService;

    @Autowired
    CommonService commonService;

    @Autowired
    LotteryService lotteryService;

    @Autowired
    CaseLotActivityService caseLotActivityService;

    @RequestMapping(value = "/createAawardPacketByPartAndPunt", method = RequestMethod.GET)
    @ResponseBody
    public String createAawardPacketByPartAndPunt(@RequestParam(value = "packet_userno", required = true) String packet_userno,
            @RequestParam(value = "parts", required = true) int parts,
            @RequestParam(value = "punts", required = true) int punts,
            @RequestParam(value = "callBackMethod", required = true) String callback) {

        logger.info("微信公众帐号红包活动创建红包：packet_userno:{}", packet_userno);

        ResponseData rd = new ResponseData();

        if (StringUtil.isEmpty(packet_userno)) {

            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            Packet packet = packetActivityService.doCreatePacket(packet_userno, parts, punts, "",
                    Integer.parseInt(ErrorCode.PACKET_STATUS.value), ErrorCode.PACKET_STATUS.memo);

            Map<String, String> json = new HashMap<String, String>();
            
            if (null == packet) {
                
                json.put("userno", packet_userno);
                rd.setErrorCode(ErrorCode.PACKET_STATUS_GETED.value);
                rd.setValue(json);               
            } else {
                
                json.put("userno", packet.getPacketUserno());
                json.put("packet_id", ToolsAesCrypt.Encrypt(String.valueOf(packet.getId()), Const.PACKET_KEY));
                json.put("punts", String.valueOf(packet.getTotalPunts()));
                rd.setErrorCode(ErrorCode.OK.value);
                rd.setValue(json);
            }
        } catch (WeixinException e) {
            
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            
            Map<String, String> json = new HashMap<String, String>();
            json.put("userno", packet_userno);
            rd.setErrorCode(ErrorCode.PACKET_STATUS_GETED.value);
            rd.setValue(json);
        }

        return JsonMapper.toJsonP(callback, rd);
    }
}
