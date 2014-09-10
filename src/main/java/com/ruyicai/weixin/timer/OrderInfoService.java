package com.ruyicai.weixin.timer;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ruyicai.weixin.dao.PuntListDao;
import com.ruyicai.weixin.domain.PuntList;
import com.ruyicai.weixin.domain.PuntPacket;
import com.ruyicai.weixin.exception.WeixinException;
import com.ruyicai.weixin.service.CommonService;
import com.ruyicai.weixin.service.PacketActivityService;
import com.ruyicai.weixin.util.DateUtil;

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
		{
			logger.info("===========定时更新投注订单中奖金额开始===========");
			String opentime = DateUtil.format("yyyy-MM-dd",new Date());
			List<PuntList> puntList = puntListDao.findPuntListNotPrized(opentime);
			if (puntList != null && puntList.size() > 0)
			{
				StringBuilder orderids = new StringBuilder();
				for (PuntList punt : puntList)
					orderids.append(punt.getOrderid()).append(",");
				
				orderids.delete(orderids.length() - 1, orderids.length());
				getOrdersInfo(orderids.toString());
			} else
			{
				logger.info("无投注订单可更新");
			}
			logger.info("===========定时更新投注订单中奖金额结束===========");
		}
	}

	/**
	 * 更新中奖金额
	 * 
	 * @param orderid
	 * @param prizeAmt
	 */
	@Async
	public void doUpdatePrizeAmt(String orderid, BigDecimal prizeAmt)
	{
		try
		{
			logger.info("更新订单中奖金额  orderid:{} prizeAmt:{} ", orderid, prizeAmt);
			PuntList puntList = puntListDao.findPuntListByOrderid(orderid);
			// 更新中奖金额
			puntListDao.merge(puntList, prizeAmt.intValue());

//			if (BigDecimal.ZERO.compareTo(prizeAmt) < 0)
//			{
//				// 发送中奖信息
//				PuntPacket puntPacket = PuntPacket.findPuntPacket(puntList.getPuntId());
//				packetActivityService.sendBetInfo(puntPacket.getGetUserno(), String.valueOf(prizeAmt.intValue()/100));
//			}
		}
		catch (WeixinException we)
		{
			logger.info(we.getErrorCode().value);
		}
		catch (Exception e)
		{
			logger.error("更新中奖金额异常", e);
		}
	}

	/**
	 * 获取订单中奖详情
	 * </p>prizestate 中奖状态, 3-不中奖; 4-中奖; 5-中奖
	 * 
	 * @param orderids 
	 * @return
	 */
	public void getOrdersInfo(String orderids)
	{
		Map<String, JSONObject> json = commonService.doGetOrdersInfo(orderids);
		if (json != null)
		{
			for (Entry<String, JSONObject> orderEnt : json.entrySet())
			{
				JSONObject order = orderEnt.getValue();
				BigDecimal prizestate = new BigDecimal(order.get("prizestate").toString()); // 开奖状态
				if (new BigDecimal(3).compareTo(prizestate) <= 0)
				{
					String orderid = orderEnt.getKey();
					BigDecimal prizeAmt = new BigDecimal(order.get("orderprizeamt").toString()); // 中奖金额
					doUpdatePrizeAmt(orderid, prizeAmt);
				}
			}
		} else
		{
			logger.info("获取订单详情为空");
		}
	}

}
