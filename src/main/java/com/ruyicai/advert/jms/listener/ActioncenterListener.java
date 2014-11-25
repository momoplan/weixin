package com.ruyicai.advert.jms.listener;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.Header;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

//import com.ruyicai.advert.domain.QqTaskProgress;
//import com.ruyicai.advert.domain.QqUserInfo;

/**
 * 活动监听
 * @author Administrator
 *
 */
@Service
public class ActioncenterListener {

	private Logger logger = Logger.getLogger(ActioncenterListener.class);
	
	public void process(@Header("USERNO") String userno, @Header("TYPE") Integer type, 
			@Header("BUSINESSTYPE") Integer businessType, @Header("AMT") Long amt) {
		try {
			logger.info("活动监听 start userno:"+userno+";type:"+type+";businessType:"+businessType+";amt:"+amt);
			
//			if(QqUserInfo.findByUserNo(userno) == null)
//			{
//				logger.info("QqUserInfo.findByUserNo(userno) is null");
//				return;
//			}
//			
//			long startMillis = System.currentTimeMillis();
//			if (type==null) {
//				return;
//			}
//			
//			if (type==2) { //1:充值;2:购彩
//				updateByType(userno, 1, amt);//购彩
//				if (businessType==null) {
//					return;
//				}
//				if (businessType==3) { //1:订单投注或追号购买;3:合买投注
//					updateByType(userno, 2, amt);//合买投注
//				}
//			}
//			long endMillis = System.currentTimeMillis();
//			logger.info("活动监听 end,用时:"+(endMillis-startMillis)+" userno:"+userno+";type:"+type+
//					";businessType:"+businessType+";amt:"+amt);
		} catch (Exception e) {
			logger.error("活动监听发生异常,userno:"+userno, e);
		}
	}
	
	private void updateByType(String userno, Integer type, long amt) {
		try {
//			QqTaskProgress taskProgress = QqTaskProgress.findByUsernoType(userno, type);
//			if (taskProgress==null) {
//				taskProgress = new QqTaskProgress();
//				taskProgress.setUserno(userno);
//				taskProgress.setType(type);
//				taskProgress.setAmt(new BigDecimal(amt));
//				taskProgress.setCreatetime(new Date());
//				taskProgress.persist();
//			} else {
//				taskProgress.setAmt(taskProgress.getAmt().add(new BigDecimal(amt)));
//				taskProgress.setUpdatetime(new Date());
//				taskProgress.merge();
//			}
		} catch (Exception e) {
			logger.error("活动监听-updateByType发生异常,userno:"+userno+";type:"+type, e);
		}
	}
	
}
