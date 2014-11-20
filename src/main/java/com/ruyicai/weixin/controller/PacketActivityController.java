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
import java.util.List;
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
import com.ruyicai.weixin.dao.AccessLogDao;
import com.ruyicai.weixin.dao.CaseLotUserInfoDao;
import com.ruyicai.weixin.dao.PacketDao;
import com.ruyicai.weixin.dao.PuntPacketDao;
import com.ruyicai.weixin.domain.AccessLog;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.Packet;
import com.ruyicai.weixin.domain.PuntPacket;
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
public class PacketActivityController {

    private Logger logger = LoggerFactory.getLogger(PacketActivityController.class);

    @Autowired
    PacketActivityService packetActivityService;

    @Autowired
    CommonService commonService;

    @Autowired
    LotteryService lotteryService;

    @Autowired
    private AccessLogDao accessLogDao;

    @Autowired
    private PuntPacketDao puntPacketDao;
    
    @Autowired
    private PacketDao packetDao;

    @Autowired
    private CaseLotUserInfoDao caseLotUserInfoDao;

    @Autowired
    CaseLotActivityService caseLotActivityService;

    @RequestMapping(value = "/createAawardPacket", method = RequestMethod.GET)
    @ResponseBody
    public String createAawardPacket(@RequestParam(value = "packet_userno", required = true) String packet_userno,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("微信公众帐号红包活动创建红包：packet_userno:{}", packet_userno);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(packet_userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            Packet packet = packetActivityService.doCreatePacket(packet_userno, 3, 3, "",
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
            // logger.error("createPacket error", e);
            // rd.setErrorCode(ErrorCode.ERROR.value);
            // rd.setValue(ErrorCode.ERROR.memo);
        }

        return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/addCashTest", method = RequestMethod.GET)
    @ResponseBody
    public String addCashTest(@RequestParam(value = "userno", required = true) String packet_userno,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("微信公众帐号红包活动创建红包：packet_userno:{}", packet_userno);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(packet_userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {

            String ret = lotteryService.addDrawableMoney(packet_userno, "1", "1101", "增加可提现余额测试（1分）");

            // Map<String, String> json = new HashMap<String, String>();
            // json.put("userno", packet_userno);

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(ret);
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

    @RequestMapping(value = "/addCash", method = RequestMethod.GET)
    @ResponseBody
    public String addCash(@RequestParam(value = "userno", required = true) String packet_userno,
            @RequestParam(value = "point", required = true) String point,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("addCash：packet_userno:{}", packet_userno);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(packet_userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {

            // lotteryService.addDrawableMoney(packet_userno, "1", "1101",
            // "增加可提现余额测试（1分）");
            String ret = commonService.presentDividend(packet_userno, point, "1101", "20141001渠道红包充值");

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(ret);
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

    // @RequestMapping(value = "/createSysTestPacket", method =
    // RequestMethod.GET)
    // @ResponseBody
    // public String createSysTestPacket(@RequestParam(value = "parts", required
    // = true) String parts,
    // @RequestParam(value = "callBackMethod", required = true) String callback)
    // {
    //
    // ResponseData rd = new ResponseData();
    // if (StringUtil.isEmpty(parts)) {
    // rd.setErrorCode("10001");
    // rd.setValue("参数不能为空");
    // return JsonMapper.toJsonP(callback, rd);
    // }
    //
    // try {
    // Packet packet = packetActivityService.doCreatePacket("",
    // Integer.parseInt(parts), Integer.parseInt(parts),
    // "", Integer.parseInt(ErrorCode.PACKET_STATUS.value),
    // ErrorCode.PACKET_STATUS.memo);
    // Map<String, String> json = new HashMap<String, String>();
    // json.put("userno", packet.getPacketUserno());
    // json.put("packet_id",
    // ToolsAesCrypt.Encrypt(String.valueOf(packet.getId()), Const.PACKET_KEY));
    // json.put("punts", String.valueOf(packet.getTotalPunts()));
    // rd.setErrorCode(ErrorCode.OK.value);
    // rd.setValue(json);
    // } catch (WeixinException e) {
    // rd.setErrorCode(e.getErrorCode().value);
    // rd.setValue(e.getErrorCode().memo);
    // } catch (Exception e) {
    // logger.error("createPacket error", e);
    // rd.setErrorCode(ErrorCode.ERROR.value);
    // rd.setValue(ErrorCode.ERROR.memo);
    // }
    //
    // return JsonMapper.toJsonP(callback, rd);
    // }

    @RequestMapping(value = "/getAward", method = RequestMethod.GET)
    @ResponseBody
    public String getAward(@RequestParam(value = "award_userno", required = true) String userno,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("查询中奖金额：userno:{}", userno);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            Map<String, Object> json = new HashMap<String, Object>();
            json = packetActivityService.getPunlistByUserno(userno);
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

    @RequestMapping(value = "/createPacket", method = RequestMethod.GET)
    @ResponseBody
    public String createPacket(@RequestParam(value = "packet_userno", required = true) String packet_userno,
            @RequestParam(value = "parts", required = true) String parts,
            @RequestParam(value = "punts", required = true) String punts,
            @RequestParam(value = "greetings", required = true) String greetings,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("微信公众帐号红包活动创建红包：packet_userno:{},parts:{},punts:{},greetings:{}", packet_userno, parts, punts,
                greetings);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(packet_userno) || StringUtil.isEmpty(parts) || StringUtil.isEmpty(punts)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        int puntsInt = Integer.valueOf(punts);
        // if (puntsInt > 1000) {
        // rd.setErrorCode("10002");
        // rd.setValue("创建红包注数不能大于1000注");
        // return JsonMapper.toJsonP(callback, rd);
        // }

        if (puntsInt < 1) {
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
            Packet packet = packetActivityService.doCreatePacket(packet_userno, partsInt, puntsInt, greetings);
            Map<String, String> json = new HashMap<String, String>();
            json.put("userno", packet.getPacketUserno());
            json.put("packet_id", ToolsAesCrypt.Encrypt(String.valueOf(packet.getId()), Const.PACKET_KEY));
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getPuntsFromPacket", method = RequestMethod.GET)
    @ResponseBody
    public String getpuntsfrompacket(@RequestParam(value = "award_userno", required = false) String award_userno,
            @RequestParam(value = "channel", required = false) String channel,
            @RequestParam(value = "packet_id", required = false) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        logger.info(" 抢到用户userno:{} 红包ID：{} ", award_userno, packet_id);

        ResponseData rd = new ResponseData();

        try {
            if (StringUtils.isEmpty(award_userno) || StringUtils.isEmpty(packet_id)) {

                rd.setErrorCode("10001");
                rd.setValue("参数错误：the argument packet_id or award_userno is require.");
                return JsonMapper.toJsonP(callback, rd);
            }

            // 判断用户是否存在
            caseLotActivityService.caseLotchances(award_userno, Const.WX_PACKET_ACTIVITY);

            packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密

            if (StringUtil.isEmpty(packet_id))
                throw new WeixinException(ErrorCode.ERROR);

            // 查询红包状态
            Map<Integer, Object> status = packetActivityService.getPacketStatus(award_userno, packet_id.trim());

            for (Entry<Integer, Object> entry : status.entrySet()) {
                Map<String, Object> msg = new HashMap<String, Object>();
                int k = entry.getKey();
                Map<String, Object> v = (Map<String, Object>) entry.getValue();
                if (k == 0) {

                    Map<String, Object> imap = packetActivityService.getPunts(award_userno, channel, packet_id.trim());

                    for (Entry<String, Object> entryV : v.entrySet()) {
                        imap.put(entryV.getKey(), entryV.getValue());
                    }

                    msg.put("status_info", imap);
                    rd.setErrorCode(ErrorCode.OK.value);
                } else {
                    msg.put("status_info", v);
                    rd.setErrorCode(String.valueOf(k));
                }
                rd.setValue(msg);
                break;
            }
        } catch (WeixinException e) {
            if (ErrorCode.DATA_NOT_EXISTS.value.equals(e.getErrorCode().value)) {
                rd.setValue(1);
                rd.setErrorCode(String.valueOf(1));
            } else if (ErrorCode.PACKET_STATUS_GETED.value.equals(e.getErrorCode().value)) {
                Map<Integer, Object> status = packetActivityService.getPacketStatus(award_userno, packet_id.trim());
                for (Entry<Integer, Object> entry : status.entrySet()) {
                    Map<String, Object> msg = new HashMap<String, Object>();
                    int k = entry.getKey();
                    Map<String, Object> v = (Map<String, Object>) entry.getValue();
                    msg.put("status_info", v);
                    rd.setErrorCode(String.valueOf(k));
                    rd.setValue(msg);
                    break;
                }
            } else {
                rd.setErrorCode(e.getErrorCode().value);
                rd.setValue(e.getErrorCode().memo);
            }
        } catch (Exception e) {
            logger.error("getPuntsFromPacket error2", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getOnefrompacket", method = RequestMethod.GET)
    @ResponseBody
    public String getOnefrompacket(@RequestParam(value = "award_userno", required = false) String award_userno,
            @RequestParam(value = "channel", required = false) String channel,
            @RequestParam(value = "packet_id", required = false) String packet_id,
            @RequestParam(value = "betcode", required = false) String betcode,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        logger.info("getPuntsFromPacket award_userno:{} packet_id：{} ", award_userno, packet_id);

        ResponseData rd = new ResponseData();
        try {
            if (StringUtils.isEmpty(award_userno) || StringUtils.isEmpty(packet_id)) {
                rd.setErrorCode("10001");
                rd.setValue("参数错误the argument orderid or userno is require.");
                return JsonMapper.toJsonP(callback, rd);
            }

            // 判断用户是否存在
            caseLotActivityService.caseLotchances(award_userno, Const.WX_PACKET_ACTIVITY);

            packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密
            if (StringUtil.isEmpty(packet_id))
                throw new WeixinException(ErrorCode.ERROR);

            Map<Integer, Object> status = packetActivityService.getNewPacketStatus(award_userno, packet_id.trim());

            for (Entry<Integer, Object> entry : status.entrySet()) {
                Map<String, Object> msg = new HashMap<String, Object>();
                int k = entry.getKey();
                Map<String, Object> v = (Map<String, Object>) entry.getValue();
                if (k == 0) {

                    Map<String, Object> imap = packetActivityService.getPunts(award_userno, channel, packet_id.trim(),
                            betcode);

                    for (Entry<String, Object> entryV : v.entrySet()) {
                        imap.put(entryV.getKey(), entryV.getValue());
                    }

                    msg.put("status_info", imap);
                    rd.setErrorCode(ErrorCode.OK.value);
                } else {
                    msg.put("status_info", v);
                    rd.setErrorCode(String.valueOf(k));
                }
                rd.setValue(msg);
                break;
            }
        } catch (WeixinException e) {
            if (ErrorCode.DATA_NOT_EXISTS.value.equals(e.getErrorCode().value)) {
                rd.setValue(1);
                rd.setErrorCode(String.valueOf(1));
            } else if (ErrorCode.PACKET_STATUS_GETED.value.equals(e.getErrorCode().value)) {
                Map<Integer, Object> status = packetActivityService.getNewPacketStatus(award_userno, packet_id.trim());
                for (Entry<Integer, Object> entry : status.entrySet()) {
                    Map<String, Object> msg = new HashMap<String, Object>();
                    int k = entry.getKey();
                    Map<String, Object> v = (Map<String, Object>) entry.getValue();
                    msg.put("status_info", v);
                    rd.setErrorCode(String.valueOf(k));
                    rd.setValue(msg);
                    break;
                }
            } else {
                rd.setErrorCode(e.getErrorCode().value);
                rd.setValue(e.getErrorCode().memo);
            }
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
    public String getPacketStatus(@RequestParam(value = "award_userno", required = false) String award_userno,
            @RequestParam(value = "packet_id", required = false) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        logger.info("getPacketStatus award_userno:{} packet_id：{} ", award_userno, packet_id);
        ResponseData rd = new ResponseData();
        try {
            if (StringUtils.isEmpty(award_userno) || StringUtils.isEmpty(packet_id)) {
                rd.setErrorCode("10001");
                rd.setValue("参数错误the argument orderid or userno is require.");
                return JsonMapper.toJsonP(callback, rd);
            }

            Map<String, String> map = packetActivityService.doGetPacketStus(award_userno, packet_id);

            rd.setErrorCode(ErrorCode.OK.value);

            rd.setValue(map);

        } catch (WeixinException e) {
            logger.error("getPacketStatus error1", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getPacketStatus error2", e);

            rd.setErrorCode(ErrorCode.PACKET_STATUS_GETED.value);
            Map<String, String> map = new HashMap<String, String>();
            map.put("ret_msg", "红包已抢");
            rd.setValue(map);
            // rd.setErrorCode(ErrorCode.ERROR.value);
            // rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getNewPacketStatus", method = RequestMethod.GET)
    @ResponseBody
    public String getNewPacketStatus(@RequestParam(value = "award_userno", required = false) String award_userno,
            @RequestParam(value = "openid", required = false) String openid,
            @RequestParam(value = "packet_id", required = false) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        logger.info("getPacketStatus award_userno:{} packet_id：{} ", award_userno, packet_id);
        ResponseData rd = new ResponseData();
        try {
            if (StringUtils.isEmpty(award_userno) || StringUtils.isEmpty(packet_id)) {
                rd.setErrorCode("10001");
                rd.setValue("参数错误the argument orderid or userno is require.");
                return JsonMapper.toJsonP(callback, rd);
            }

            Map<String, String> map = packetActivityService.doNewGetPacketStus(award_userno, packet_id, openid);

            rd.setErrorCode(ErrorCode.OK.value);

            rd.setValue(map);

        } catch (WeixinException e) {
            logger.error("getPacketStatus error1", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getPacketStatus error2", e);

            rd.setErrorCode(ErrorCode.PACKET_STATUS_GETED.value);
            Map<String, String> map = new HashMap<String, String>();
            map.put("ret_msg", "红包已抢");
            rd.setValue(map);
            // rd.setErrorCode(ErrorCode.ERROR.value);
            // rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getMyPacketStatus", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPacketStatus(@RequestParam(value = "packet_userno", required = false) String packet_userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {
        logger.info("getPacketStatus award_userno:{} ", packet_userno);
        ResponseData rd = new ResponseData();
        try {
            if (StringUtils.isEmpty(packet_userno)) {
                rd.setErrorCode("10001");
                rd.setValue("参数错误the argument orderid or userno is require.");
                return JsonMapper.toJsonP(callback, rd);
            }

            Map<String, String> map = packetActivityService.doGetPacketStus(packet_userno);

            rd.setErrorCode(ErrorCode.OK.value);

            rd.setValue(map);

        } catch (WeixinException e) {
            logger.error("getMyPacketStatus error1", e);
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getMessage());
        } catch (Exception e) {
            logger.error("getMyPacketStatus error2", e);
            rd.setErrorCode(ErrorCode.PACKET_STATUS_GETED.value);
            Map<String, String> map = new HashMap<String, String>();
            map.put("ret_msg", "红包已抢");
            rd.setValue(map);
            // rd.setErrorCode(ErrorCode.ERROR.value);
            // rd.setValue(e.getMessage());
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    // 送到的红包列表
    @RequestMapping(value = "/getPacketList", method = RequestMethod.GET)
    @ResponseBody
    public String getPacketList(@RequestParam(value = "packet_userno", required = true) String packet_userno,
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
    
    // 送到的红包列表
    @RequestMapping(value = "/getNewPacketList", method = RequestMethod.GET)
    @ResponseBody
    public String getNewPacketList(@RequestParam(value = "packet_userno", required = true) String packet_userno,
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

    // 送到的红包列表
    @RequestMapping(value = "/getPacketListByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getPacketListByPage(@RequestParam(value = "packet_userno", required = true) String packet_userno,
            @RequestParam(value = "page_index", required = true) int page_index,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getPacketList packet_userno:{}", packet_userno);

        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(packet_userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            rd.setValue(packetActivityService.doGetPacketList(packet_userno, page_index));
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
    public String getPacketInfo(@RequestParam(value = "userno", required = true) String userno,
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
            rd.setValue(packetActivityService.doGetPacketInfo(userno, packet_id));
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

    /**
     * 红包详细
     * 
     * */
    @RequestMapping(value = "/getPacketInfoByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getPacketInfoByPage(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "page_index", required = true) int page_index,
            @RequestParam(value = "page_count", required = true) int page_count,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getPacketInfo userno:{}, packet_id:{}", userno, packet_id);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(userno) || StringUtil.isEmpty(packet_id)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            rd.setValue(packetActivityService.doGetPacketInfo(userno, packet_id, page_index, page_count));
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

    @RequestMapping(value = "/getMoneyUser", method = RequestMethod.GET)
    @ResponseBody
    public String getMoneyUser(@RequestParam(value = "userno", required = true) String userno,
            @RequestParam(value = "channel", required = true) String channel,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getPacketInfo userno:{}, channel:{}", userno, channel);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(userno) || StringUtil.isEmpty(channel)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            Map<String, String> map = packetActivityService.getMoneyUser(userno, channel);
            rd.setValue(map);
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (WeixinException e) {
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            logger.error("getMoneyUser error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(ErrorCode.ERROR.memo);
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/thankTa", method = RequestMethod.GET)
    @ResponseBody
    public String thankTa(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "thank_words", required = true) String thank_words,
            @RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("thankTa award_userno:{}, thank_words:{}, packet_id:{}", award_userno, award_userno, packet_id);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno) || StringUtil.isEmpty(thank_words) || StringUtil.isEmpty(packet_id)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            packetActivityService.doThankTa(award_userno, thank_words, packet_id);
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
    @RequestMapping(value = "/getMyPuntsByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPuntsByIndex(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "page_index", required = true) String page_index,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getMyPuntsByIndex award_userno:{}", award_userno);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            rd.setValue(packetActivityService.doGetMyPunts(award_userno, page_index));
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (WeixinException e) {
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            logger.error("getMyPuntsByIndex error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(ErrorCode.ERROR.memo);
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    // 领取的红包注数列表
    @RequestMapping(value = "/getMyPunts", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPunts(@RequestParam(value = "award_userno", required = true) String award_userno,
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
    
    // 领取的红包注数列表
    @RequestMapping(value = "/getNewMyPunts", method = RequestMethod.GET)
    @ResponseBody
    public String getNewMyPunts(@RequestParam(value = "award_userno", required = true) String award_userno,
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

    /**
     * 收到的红包列表
     * 
     * @param award_userno
     * @param callback
     * @return
     */
    @RequestMapping(value = "/getMyPuntPackets", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPuntPackets(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getMyPuntPackets award_userno:{}", award_userno);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            rd.setValue(packetActivityService.doGetMyPuntPackets(award_userno));
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (WeixinException e) {
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            logger.error("getMyPuntPackets error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(ErrorCode.ERROR.memo);
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    /**
     * 收到的红包列表
     * 
     * @param award_userno
     * @param callback
     * @return
     */
    @RequestMapping(value = "/getMyPuntPacketsByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPuntPacketsByPage(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "page_index", required = true) int page_index,
            @RequestParam(value = "callBackMethod", required = true) String callback) {

        logger.info("getMyPuntPackets award_userno:{}", award_userno);

        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno)) {

            rd.setErrorCode("10001");

            rd.setValue("参数不能为空");

            return JsonMapper.toJsonP(callback, rd);
        }

        try {

            rd.setValue(packetActivityService.doGetMyPuntPacketsByPage(award_userno, page_index));

            rd.setErrorCode(ErrorCode.OK.value);

        } catch (WeixinException e) {

            rd.setErrorCode(e.getErrorCode().value);

            rd.setValue(e.getErrorCode().memo);

        } catch (Exception e) {

            logger.error("getMyPuntPackets error", e);

            rd.setErrorCode(ErrorCode.ERROR.value);

            rd.setValue(ErrorCode.ERROR.memo);

        }

        return JsonMapper.toJsonP(callback, rd);
    }

    /**
     * 收到的红包对应的注码列表
     * 
     * @param award_userno
     * @param packet_id
     * @param callback
     * @return
     */
    @RequestMapping(value = "/getMyPuntList", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPuntList(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getMyPuntList award_userno:{} packet_id:{}", award_userno, packet_id);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno) || StringUtil.isEmpty(packet_id)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密
            rd.setValue(packetActivityService.doGetMyPuntList(award_userno, packet_id.trim()));
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (WeixinException e) {
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            logger.error("getMyPuntList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(ErrorCode.ERROR.memo);
        }
        return JsonMapper.toJsonP(callback, rd);
    }
    
    /**
     * 收到的红包对应的注码列表
     * 
     * @param award_userno
     * @param packet_id
     * @param callback
     * @return
     */
    @RequestMapping(value = "/getNewMyPuntList", method = RequestMethod.GET)
    @ResponseBody
    public String getNewMyPuntList(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getMyPuntList award_userno:{} packet_id:{}", award_userno, packet_id);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno) || StringUtil.isEmpty(packet_id)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密
            rd.setValue(packetActivityService.doGetMyPuntList(award_userno, packet_id.trim()));
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (WeixinException e) {
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            logger.error("getMyPuntList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(ErrorCode.ERROR.memo);
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    /**
     * 收到的红包对应的注码列表
     * 
     * @param award_userno
     * @param packet_id
     * @param callback
     * @return
     */
    @RequestMapping(value = "/getMyPuntListByPage", method = RequestMethod.GET)
    @ResponseBody
    public String getMyPuntListByPage(@RequestParam(value = "award_userno", required = true) String award_userno,
            @RequestParam(value = "packet_id", required = true) String packet_id,
            @RequestParam(value = "page_index", required = true) int page_index,
            @RequestParam(value = "callBackMethod", required = true) String callback) {
        logger.info("getMyPuntListByPage award_userno:{} packet_id:{}", award_userno, packet_id);
        ResponseData rd = new ResponseData();
        if (StringUtil.isEmpty(award_userno) || StringUtil.isEmpty(packet_id)) {
            rd.setErrorCode("10001");
            rd.setValue("参数不能为空");
            return JsonMapper.toJsonP(callback, rd);
        }

        try {
            packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密
            rd.setValue(packetActivityService.doGetMyPuntList(award_userno, packet_id.trim(), page_index));
            logger.info("award_userno:{},{},{}", award_userno, packet_id.trim(), page_index);
            rd.setErrorCode(ErrorCode.OK.value);
        } catch (WeixinException e) {
            rd.setErrorCode(e.getErrorCode().value);
            rd.setValue(e.getErrorCode().memo);
        } catch (Exception e) {
            logger.error("getMyPuntList error", e);
            rd.setErrorCode(ErrorCode.ERROR.value);
            rd.setValue(ErrorCode.ERROR.memo);
        }
        return JsonMapper.toJsonP(callback, rd);
    }

    @RequestMapping(value = "/getActivityEnv", method = RequestMethod.GET)
    @ResponseBody
    public String getActivityEnv(@RequestParam(value = "callBackMethod", required = true) String callback) {

        // packetActivityService.sendBetInfo("oFYzzjtSt5esrX6ai4gAKH4SKqxo",String.valueOf(7));
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
    public String returnAllLeftPunts(@RequestParam(value = "callBackMethod", required = true) String callback) {

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
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
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
            logger.info("生成jpeg图片");
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
    public void uploadorginimg(@RequestParam(value = "data", required = false) String file,
            @RequestParam(value = "userno", required = false) String userno, HttpServletRequest request,
            HttpServletResponse response) {
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
        logger.info("path.indexOf(:)==0:" + String.valueOf(path.indexOf(":") == 0));
        if (path.indexOf(":") == -1) {
            path = "/" + path;
            logger.info("path2:" + path);
        }

        path = path.replace("weixin", "settingimg");
        path = path.replace("settingimg_tomcat", "weixin_tomcat");
        logger.info("path5:" + path);
        System.out.println("path4:" + path);

        String format = "jpg";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        String name = sdf.format(new Date());
        String allPath = path + name + "." + format;

        logger.info("GenerateImage pre");
        if (GenerateImage(data, allPath)) {
            logger.info("GenerateImage done");
            try {
                CaseLotUserinfo clUserInfo = caseLotActivityService.findOrCreateCaseLotUserinfo(userno, "HM00002", "",
                        "", "");
                clUserInfo.setSettingImgurl("http://www.ruyicai.com/settingimg/" + name + "." + format);
                clUserInfo.merge();
                logger.info("setting img done");
            } catch (Exception ex) {
                logger.info("caseLotActivityService.findOrCreateCaseLotUserinfo userno:" + userno);
                throw new WeixinException(ErrorCode.ERROR);
            }
            String redirectURL = "http://wx.ruyicai.com/wxpay/sendfriend.html?urlname="
                    + "http://www.ruyicai.com/settingimg/" + name + "." + format;
            logger.info(redirectURL);

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
    public void upload(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "userno", required = false) String userno, HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("开始");
        String path = this.getClass().getResource("/../../").toString();
        logger.info("path:" + path);
        path = path.replace("file:/", "");
        logger.info("path.indexOf(:)==0:" + String.valueOf(path.indexOf(":") == 0));
        if (path.indexOf(":") == -1) {
            path = "/" + path;
            logger.info("path2:" + path);
        }

        logger.info("");
        path = path.replace("weixin", "images");
        path = path.replace("images_tomcat", "weixin_tomcat");

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

        // String redirectURL = "http://" + request.getLocalAddr() + ":"
        // + request.getLocalPort() + "/uploadimg/paipai.html?userno="
        // + userno + "&urlname=" + "http://" + request.getLocalAddr()
        // + ":" + request.getLocalPort() + "/images/" + name + "."
        // + format;

        String redirectURL = "http://www.ruyicai.com/uploadimg/paipai.html?userno=" + userno + "&urlname="
                + "http://www.ruyicai.com/images/" + name + "." + format;
        logger.info(redirectURL);
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

    @RequestMapping(value = "/sendTemplateMsg", method = RequestMethod.GET)
    @ResponseBody
    public String sendTemplateMsg(@RequestParam(value = "openid", required = true) String openid,
            @RequestParam(value = "callBackMethod", required = true) String callback) {

        ResponseData rd = new ResponseData();
        try {

            int returnPunts = packetActivityService.sendTemplateMsg(openid);
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

    @RequestMapping(value = "/GetOpenTime", method = RequestMethod.GET)
    @ResponseBody
    public String batchcode(@RequestParam(value = "batchcode", required = true) String batchcode,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            String ret = commonService.getBatchInfo(batchcode);

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(ret);

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

    @RequestMapping(value = "/rycct", method = RequestMethod.GET)
    @ResponseBody
    public String rycct(@RequestParam(value = "date", required = true) String date,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            long ret = packetActivityService.countList(date);

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(ret);

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

    // @RequestMapping(value = "/redoupate", method = RequestMethod.GET)
    // @ResponseBody
    // public String redoupate(
    // @RequestParam(value = "callBackMethod", required = false) String
    // callback) {
    //
    // ResponseData rd = new ResponseData();
    // try {
    //
    // packetActivityService.redoUpdatePrizeAmt();
    //
    // rd.setErrorCode(ErrorCode.OK.value);
    // rd.setValue("OK");
    //
    // } catch (WeixinException e) {
    // logger.error("findReturnPacketList error", e);
    // rd.setErrorCode(e.getErrorCode().value);
    // rd.setValue(e.getMessage());
    // } catch (Exception e) {
    // logger.error("findReturnPacketList error", e);
    // rd.setErrorCode(ErrorCode.ERROR.value);
    // rd.setValue(e.getMessage());
    // }
    //
    // return JsonMapper.toJsonP(callback, rd);
    // }

    @RequestMapping(value = "/addAccessLog", method = RequestMethod.GET)
    @ResponseBody
    public String addAccessLog(@RequestParam(value = "openid", required = true) String openid,
            @RequestParam(value = "packetid", required = true) String packetid,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            AccessLog ret = accessLogDao.createAccessLog(openid, packetid);

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(ret);

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

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo(@RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "openid", required = false) String openid,
            @RequestParam(value = "userno", required = false) String userno,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            // AccessLog ret = accessLogDao.createAccessLog(openid, packetid);

            if (openid == null)
                openid = "";

            if (userno == null)
                userno = "";

            if (nickname == null)
                nickname = "";

            List<CaseLotUserinfo> ret = caseLotUserInfoDao.findCaseLotUserinfo(openid, userno, nickname);

            rd.setErrorCode(ErrorCode.OK.value);
            rd.setValue(ret);

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

    @RequestMapping(value = "/getPacketPersons", method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo(@RequestParam(value = "packet_id", required = false) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);             
            Map<String, Object> iMap = new HashMap<String,Object>();
            List<PuntPacket> lstPuntPacket = puntPacketDao.findGetPersons(packet_id);           
            iMap.put("persons", lstPuntPacket.size());
            
            // rd.setValue(ret);

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
    
    @RequestMapping(value = "/getSysPacket", method = RequestMethod.GET)
    @ResponseBody
    public String getSysPacket(@RequestParam(value = "packet_id", required = false) String packet_id,
            @RequestParam(value = "callBackMethod", required = false) String callback) {

        ResponseData rd = new ResponseData();
        try {

            rd.setErrorCode(ErrorCode.OK.value);
            packet_id = ToolsAesCrypt.Decrypt(packet_id, Const.PACKET_KEY); // 解密
            Map<String, Object> iMap = new HashMap<String,Object>();
            Packet packet = Packet.findPacket(Integer.parseInt(packet_id));   
           
            if(null == packet.getAwardUserno() && packet.getPacketUserno().equals(Const.SYS_USERNO))
            {
                iMap.put("sys_packet", 1);
            }
            else
                iMap.put("sys_packet", 0);
            iMap.put("packetInfo", packet);
            rd.setValue(iMap);
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


}
