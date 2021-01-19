package com.framework.loippi.service.impl.report;

import com.framework.loippi.dao.report.ShopOrderMonthReportDao;
import com.framework.loippi.entity.report.ShopOrderDailyReport;
import com.framework.loippi.entity.report.ShopOrderMonthReport;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.report.ShopOrderDailyReportService;
import com.framework.loippi.service.report.ShopOrderMonthReportService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Service
public class ShopOrderMonthReportServiceImpl extends GenericServiceImpl<ShopOrderMonthReport, Long> implements ShopOrderMonthReportService {
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopOrderMonthReportDao shopOrderMonthReportDao;
    @Resource
    private ShopRefundReturnService shopRefundReturnService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 统计平台订单月交易量
     */
    @Override
    public void getMonthReport() {
        ShopOrderMonthReport monthReport = new ShopOrderMonthReport();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = calendar.getTime();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        String reportCode = format2.format(time);
        String lastMonth = format.format(calendar.getTime());
        System.out.println(lastMonth);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(actualMaximum);
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),actualMaximum,23,59,59);
        String lastMonthBig = format.format(calendar.getTime());
        System.out.println(lastMonthBig);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        String lastMonthSmall = format1.format(calendar.getTime());
        System.out.println(lastMonthSmall);
        HashMap<String, Object> map = new HashMap<>();
        map.put("start",lastMonthSmall);
        map.put("end",lastMonthBig);
        monthReport.setId(twiterIdService.getTwiterId());
        monthReport.setReportCode(reportCode);
        monthReport.setReportTime(time);
        monthReport.setCreateTime(new Date());
        Integer payNum=shopOrderService.findPayNumYesterday(map);//昨日支付订单数(支付时间为昨日，支付状态为已支付)
        Integer refundNum=0;
        Integer cancel=shopOrderService.findCancelNumYesterday(map);//昨日取消已支付订单数
        Integer afterSale=shopRefundReturnService.findAfterSaleYesterday(map);//昨日售后退款订单数
        refundNum=refundNum+cancel+afterSale;
        monthReport.setPayNum(payNum);
        monthReport.setRefundNum(refundNum);
        BigDecimal wechatIncome = BigDecimal.ZERO;
        BigDecimal alipayIncome = BigDecimal.ZERO;
        BigDecimal allinpayIncome = BigDecimal.ZERO;
        BigDecimal pointIncome = BigDecimal.ZERO;
        pointIncome=shopOrderService.getSumPoint(map);
        map.put("paymentCode","weixinMobilePaymentPlugin");
        wechatIncome=shopOrderService.getSumIncome(map);
        map.put("paymentCode","alipayMobilePaymentPlugin");
        alipayIncome=shopOrderService.getSumIncome(map);
        map.put("paymentCode","weixinAppletsPaymentPlugin");
        allinpayIncome=shopOrderService.getSumIncome(map);
        monthReport.setWechatIncome(wechatIncome);
        monthReport.setAlipayIncome(alipayIncome);
        monthReport.setAllinpayIncome(allinpayIncome);
        monthReport.setPointIncome(pointIncome);
        monthReport.setCashTotal(wechatIncome.add(alipayIncome).add(allinpayIncome));
        monthReport.setTotal(wechatIncome.add(alipayIncome).add(allinpayIncome).add(pointIncome));
        shopOrderMonthReportDao.insert(monthReport);
    }
}
