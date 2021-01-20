package com.framework.loippi.service.impl.report;

import com.framework.loippi.dao.report.ShopOrderDailyReportDao;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.report.ShopOrderDailyReport;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.report.ShopOrderDailyReportService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Service
public class ShopOrderDailyReportServiceImpl extends GenericServiceImpl<ShopOrderDailyReport, Long> implements ShopOrderDailyReportService {
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopOrderDailyReportDao shopOrderDailyReportDao;
    @Resource
    private ShopRefundReturnService shopRefundReturnService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 统计平台订单日交易量
     */
    @Override
    public void getDailyReport() {
        ShopOrderDailyReport dailyReport = new ShopOrderDailyReport();
        //1.获取昨日时间确定reportCode
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Date time = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String reportCode = format.format(time);
        HashMap<String, Object> map = new HashMap<>();
        String start=reportCode+" 00:00:00";
        String end=reportCode+" 23:59:59";
        map.put("start",start);
        map.put("end",end);
        dailyReport.setId(twiterIdService.getTwiterId());
        dailyReport.setReportCode(reportCode);
        dailyReport.setReportTime(time);
        dailyReport.setCreateTime(new Date());
        Integer payNum=shopOrderService.findPayNumYesterday(map);//昨日支付订单数(支付时间为昨日，支付状态为已支付)
        Integer refundNum=0;
        Integer cancel=shopOrderService.findCancelNumYesterday(map);//昨日取消已支付订单数
        Integer afterSale=shopRefundReturnService.findAfterSaleYesterday(map);//昨日售后退款订单数
        refundNum=refundNum+cancel+afterSale;
        dailyReport.setPayNum(payNum);
        dailyReport.setRefundNum(refundNum);
        BigDecimal wechatIncome = BigDecimal.ZERO;
        BigDecimal alipayIncome = BigDecimal.ZERO;
        BigDecimal allinpayIncome = BigDecimal.ZERO;
        BigDecimal pointIncome = BigDecimal.ZERO;
        BigDecimal decimalpointIncome=shopOrderService.getSumPoint(map);
        if(decimalpointIncome!=null){
            pointIncome=pointIncome.add(decimalpointIncome);
        }
        map.put("paymentCode","weixinMobilePaymentPlugin");
        BigDecimal decimalwechatIncome=shopOrderService.getSumIncome(map);
        if(decimalwechatIncome!=null){
            wechatIncome=wechatIncome.add(decimalwechatIncome);
        }
        map.put("paymentCode","alipayMobilePaymentPlugin");
        BigDecimal decimalalipayIncome=shopOrderService.getSumIncome(map);
        if(decimalalipayIncome!=null){
            alipayIncome=alipayIncome.add(decimalalipayIncome);
        }
        map.put("paymentCode","weixinAppletsPaymentPlugin");
        BigDecimal decimalallinpayIncome=shopOrderService.getSumIncome(map);
        if(decimalallinpayIncome!=null){
            allinpayIncome=allinpayIncome.add(decimalallinpayIncome);
        }
        dailyReport.setWechatIncome(wechatIncome);
        dailyReport.setAlipayIncome(alipayIncome);
        dailyReport.setAllinpayIncome(allinpayIncome);
        dailyReport.setPointIncome(pointIncome);
        dailyReport.setCashTotal(wechatIncome.add(alipayIncome).add(allinpayIncome));
        dailyReport.setTotal(wechatIncome.add(alipayIncome).add(allinpayIncome).add(pointIncome));
        System.out.println(dailyReport);
        shopOrderDailyReportDao.insert(dailyReport);
    }
}
