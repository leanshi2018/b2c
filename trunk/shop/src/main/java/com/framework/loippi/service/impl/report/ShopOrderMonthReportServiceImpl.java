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
import com.framework.loippi.utils.Paramap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
     * 统计平台订单月交易量 ceshi
     */
    @Override
    public void getMonthReport() {
        ShopOrderMonthReport monthReport = new ShopOrderMonthReport();
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MONTH,-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = calendar.getTime();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
        String reportCode = format2.format(time);
        List<ShopOrderMonthReport> params = shopOrderMonthReportDao.findByParams(Paramap.create().put("reportCode",reportCode));
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
        if(params!=null&&params.size()!=0){
            ShopOrderMonthReport shopOrderMonthReport = params.get(0);
            shopOrderMonthReport.setReportCode(reportCode);
            shopOrderMonthReport.setReportTime(time);
            shopOrderMonthReport.setCreateTime(new Date());
            Integer payNum=shopOrderService.findPayNumYesterday(map);
            Integer refundNum=0;
            Integer cancel=shopOrderService.findCancelNumYesterday(map);
            Integer afterSale=shopRefundReturnService.findAfterSaleYesterday(map);
            refundNum=refundNum+cancel+afterSale;
            shopOrderMonthReport.setPayNum(payNum);
            shopOrderMonthReport.setRefundNum(refundNum);
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
            shopOrderMonthReport.setWechatIncome(wechatIncome);
            shopOrderMonthReport.setAlipayIncome(alipayIncome);
            shopOrderMonthReport.setAllinpayIncome(allinpayIncome);
            shopOrderMonthReport.setPointIncome(pointIncome);
            shopOrderMonthReport.setCashTotal(wechatIncome.add(alipayIncome).add(allinpayIncome));
            shopOrderMonthReport.setTotal(wechatIncome.add(alipayIncome).add(allinpayIncome).add(pointIncome));
            shopOrderMonthReportDao.update(shopOrderMonthReport);
        }else {
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
            monthReport.setWechatIncome(wechatIncome);
            monthReport.setAlipayIncome(alipayIncome);
            monthReport.setAllinpayIncome(allinpayIncome);
            monthReport.setPointIncome(pointIncome);
            monthReport.setCashTotal(wechatIncome.add(alipayIncome).add(allinpayIncome));
            monthReport.setTotal(wechatIncome.add(alipayIncome).add(allinpayIncome).add(pointIncome));
            shopOrderMonthReportDao.insert(monthReport);
        }
    }
}
