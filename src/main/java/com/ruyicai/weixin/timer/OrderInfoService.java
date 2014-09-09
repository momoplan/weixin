package com.ruyicai.weixin.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ruyicai.weixin.dao.PuntListDao;
import com.ruyicai.weixin.domain.PuntList;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.exception.ErrorCode;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.CommonService;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.DateUtil;
import com.ruyicai.weixin.util.StringUtil;

@Component
public class OrderInfoService {

	private Logger logger = LoggerFactory.getLogger(OrderInfoService.class);

	@Autowired
	private CommonService commonService;


	@Autowired
	PacketActivityService packetActivityService;
	
	@Autowired
	PuntListDao puntListDao;

	public void process()
	{
		Calendar c = Calendar.getInstance();
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		if ((hourOfDay == 21 && minute > 35) || hourOfDay > 21)
//		if ((hourOfDay == 21 && minute > 35) || hourOfDay > 10)
		{
			logger.info("===========定时更新投注订单中奖金额开始===========");
			String opentime = DateUtil.format("yyyy-MM-dd",new Date());
			
//			String opentime = "2014-09-07";
			List<PuntList> puntList = puntListDao.findPuntListNotPrized(opentime);
			if (puntList != null && puntList.size() > 0)
			{
				for (PuntList punt : puntList)
				{
					doUpdatePrizeAmt(punt);
				}
			} else
			{
//				List<?> lst = puntListDao.getBetMoeny(opentime);
//				System.out.println(lst);
				logger.info("无投注订单可更新");
			}
			logger.info("===========定时更新投注订单中奖金额结束===========");
		}
	}

	@Async
	public void doUpdatePrizeAmt(PuntList punt)
	{
		try
		{
			int orderprizeamt = getPrizeAmt(punt.getOrderid());
			if(orderprizeamt > 0)
			{
				PuntPacket puntPacket = PuntPacket.findPuntPacket(punt.getPuntId());
				packetActivityService.sendBetInfo(puntPacket.getGetUserno(),String.valueOf(orderprizeamt/100));
			}
			// 更新中奖金额
			puntListDao.merge(punt, orderprizeamt);
		} catch (WeixinException we)
		{
			logger.info(we.getErrorCode().value);
		} catch (Exception e)
		{
			logger.error("更新中奖金额异常", e);
		}
	}

	/**
	 * 获取订单中奖金额
	 * 
	 * @param orderid
	 * @return
	 */
	public int getPrizeAmt(String orderid)
	{
		JSONObject json = commonService.getOrderInfo(orderid);
		if (json != null)
		{
			String cashTime = json.get("prizeAmt").toString();
			String winCode = json.get("winCode").toString();
			if (StringUtil.isEmpty(cashTime) || StringUtil.isEmpty(winCode))
			{
				throw new WeixinException(ErrorCode.ORDER_NOT_PRIZE);
			}

			String prizeAmt = json.get("prizeAmt").toString();
			return StringUtil.isEmpty(prizeAmt) ? 0 : Integer.valueOf(prizeAmt);
		}
		return 0;
	}

}
