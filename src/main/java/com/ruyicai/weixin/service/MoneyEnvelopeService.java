package com.ruyicai.weixin.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.weixin.consts.Const;
import com.ruyicai.weixin.dao.MoneyEnvelopeDao;
import com.ruyicai.weixin.dao.MoneyEnvelopeGetInfoDao;
import com.ruyicai.weixin.domain.CaseLotUserinfo;
import com.ruyicai.weixin.domain.MoneyEnvelope;
import com.ruyicai.weixin.domain.MoneyEnvelopeGetInfo;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.util.HongBaoAlgorithm;
import com.ruyicai.weixin.util.ToolsAesCrypt;

@Service
public class MoneyEnvelopeService {

    @Autowired
    CaseLotActivityService caseLotActivityService;

    @Autowired
    private MoneyEnvelopeDao moneyEnvelopeDao;

    @Autowired
    private MoneyEnvelopeGetInfoDao moneyEnvelopeGetInfoDao;

    private Logger logger = LoggerFactory.getLogger(MoneyEnvelopeService.class);

    /**
     * 创建红包
     * 
     * @throws ParseException
     */
    public String doCreatePacket(String packetUserno, int parts, int money, int expire_date,
            String packet_exr_start_date, String packet_exr_end_date) throws ParseException {
        // 判断用户是否存在
//        CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(packetUserno, Const.WX_PACKET_ACTIVITY);
//
//        if (caseLotUserinfo == null)
//            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(packet_exr_start_date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        date = sdf.parse(packet_exr_end_date);
        Calendar calendar_end = Calendar.getInstance();
        calendar_end.setTime(date);

        MoneyEnvelope subscriberInfo = moneyEnvelopeDao.createMoneyEnvelope(packetUserno, parts, money, expire_date,
                calendar, calendar_end);

        long[] eachMoeny = HongBaoAlgorithm.generate(money, parts, money / 2, money / 8);
        int packetId = subscriberInfo.getId();

        for (int i = 0; i < eachMoeny.length; i++) {
            addPuntPacket(packetId, (int) eachMoeny[i], expire_date);
        }
        return ToolsAesCrypt.Encrypt(subscriberInfo.getId().toString(), Const.PACKET_KEY);
    }

    @Async
    public void addPuntPacket(int packetId, int get_money, int expire_days) {
        moneyEnvelopeGetInfoDao.createMoneyEnvelopeGetInfo(packetId, get_money, expire_days);
    }

    /**
     * 获取红包
     * 
     * @param award_userno
     * @param channel
     * @param packet_id
     * @return
     */
    @Transactional
    public MoneyEnvelopeGetInfo getPuntPacket(String award_userno, String packet_id) {

        CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(award_userno, Const.WX_PACKET_ACTIVITY);

        if (caseLotUserinfo == null)
            throw new WeixinException(ErrorCode.CASELOTUSERINFO_NOT_EXISTS);
        List<MoneyEnvelope> packetList = moneyEnvelopeDao.findOneNotAawardPart(packet_id);
        if (packetList != null && packetList.size() > 0) {
            List<MoneyEnvelopeGetInfo> lstPuntPacket = moneyEnvelopeGetInfoDao.findByGetUserno(award_userno, packet_id);
            if (lstPuntPacket != null && lstPuntPacket.size() > 0) {
                logger.info("红包已抢过 - award_userno:{} packet_id:{}", award_userno, packet_id);
                throw new WeixinException(ErrorCode.PACKET_STATUS_GETED);
            }

            List<MoneyEnvelopeGetInfo> puntPacketList = moneyEnvelopeGetInfoDao.findSinglePuntPart(packet_id);
            if (puntPacketList == null || puntPacketList.size() == 0) {
                logger.info("红包不存在1 award_userno:{} packet_id:{}", award_userno, packet_id);
                throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
            }

            MoneyEnvelopeGetInfo puntPacket = puntPacketList.get(0);
            Calendar date = Calendar.getInstance();
            puntPacket.setGetTime(date);
            puntPacket.setGetUserno(award_userno);
            puntPacket.merge();
            return puntPacket;
        } else {
            logger.info("红包不存在2 award_userno:{} packet_id:{}", award_userno, packet_id);
            throw new WeixinException(ErrorCode.DATA_NOT_EXISTS);
        }
    }

    /**
     * 获取红包状态
     * 
     * @param award_userno
     * @param channel
     * @param packet_id
     * @return
     */
    @Transactional
    public Map getPuntPacketStatus(String award_userno, String packet_id) {
        Map<String, Object> iMap = new HashMap<String, Object>();

        CaseLotUserinfo caseLotUserinfo = caseLotActivityService.caseLotchances(award_userno, Const.WX_PACKET_ACTIVITY);

        if (caseLotUserinfo == null)
            throw new WeixinException(ErrorCode.CASELOTUSERINFO_NOT_EXISTS);

        List<MoneyEnvelopeGetInfo> lstPuntPacket = moneyEnvelopeGetInfoDao.findByGetUserno(award_userno, packet_id);
        if (lstPuntPacket != null && lstPuntPacket.size() > 0) {
            iMap.put("getinfo", lstPuntPacket.get(0));
            iMap.put("status", "1");
            iMap.put("memo", "已抢");

        } else {

            List<MoneyEnvelopeGetInfo> puntPacketList = moneyEnvelopeGetInfoDao.findSinglePuntPart(packet_id);
            if (puntPacketList == null || puntPacketList.size() == 0) {
                logger.info("红包不存在1 award_userno:{} packet_id:{}", award_userno, packet_id);

                iMap.put("status", "3");
                iMap.put("memo", "已抢完");
            } else {

                iMap.put("status", "0");
                iMap.put("memo", "可抢");
            }
        }

        return iMap;
    }

}
