package com.framework.loippi.controller.report;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.jpush.Jpush;
import com.framework.loippi.entity.report.ShopOrderDailyReport;
import com.framework.loippi.entity.report.ShopOrderMonthReport;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.report.ShopOrderDailyReportService;
import com.framework.loippi.service.report.ShopOrderMonthReportService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 订单报表控制类
 */
@Controller("adminOrderReportController")
@RequestMapping("/admin/report")
@Slf4j
public class OrderReportController extends GenericController {
    @Resource
    private ShopOrderDailyReportService shopOrderDailyReportService;
    @Resource
    private ShopOrderMonthReportService shopOrderMonthReportService;

    /**
     * 查询订单日收入统计
     *
     */
    @RequestMapping("/daily/list")
    public String dailyList(ModelMap model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                       @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                       @ModelAttribute ShopOrderDailyReport shopOrderDailyReport) {
        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        pager.setOrderProperty("report_time");
        pager.setOrderDirection(Order.Direction.DESC);
        if(shopOrderDailyReport.getSearchTimeLeft()!=null&&!shopOrderDailyReport.getSearchTimeLeft().equals("")){
            shopOrderDailyReport.setSearchTimeLeft(shopOrderDailyReport.getSearchTimeLeft()+" 00:00:00");
        }
        if(shopOrderDailyReport.getSearchTimeRight()!=null&&!shopOrderDailyReport.getSearchTimeRight().equals("")){
            shopOrderDailyReport.setSearchTimeRight(shopOrderDailyReport.getSearchTimeRight()+" 23:59:59");
        }
        pager.setParameter(shopOrderDailyReport);
        Page<ShopOrderDailyReport> page = shopOrderDailyReportService.findByPage(pager);
        model.addAttribute("page", page);
        return "/statisc/income/dailysReport";
    }

    /**
     * 查询订单月收入统计
     *
     */
    @RequestMapping("/month/list")
    public String monthList(ModelMap model,
                            @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                            @ModelAttribute ShopOrderMonthReport shopOrderMonthReport) {
        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        pager.setOrderProperty("report_time");
        pager.setOrderDirection(Order.Direction.DESC);
        if(shopOrderMonthReport.getSearchTimeLeft()!=null&&!shopOrderMonthReport.getSearchTimeLeft().equals("")){
            shopOrderMonthReport.setSearchTimeLeft(shopOrderMonthReport.getSearchTimeLeft()+"-01 00:00:00");
        }
        if(shopOrderMonthReport.getSearchTimeRight()!=null&&!shopOrderMonthReport.getSearchTimeRight().equals("")){
            String reportCode=shopOrderMonthReport.getSearchTimeRight();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            Date date = null;
            try {
                date = dateFormat.parse(reportCode);
                calendar.setTime(date);
                int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),actualMaximum,23,59,59);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastMonthBig = format.format(calendar.getTime());
                shopOrderMonthReport.setSearchTimeRight(lastMonthBig);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        pager.setParameter(shopOrderMonthReport);
        Page<ShopOrderMonthReport> page = shopOrderMonthReportService.findByPage(pager);
        model.addAttribute("page", page);
        return "/statisc/income/monthlyReport";
    }
}
