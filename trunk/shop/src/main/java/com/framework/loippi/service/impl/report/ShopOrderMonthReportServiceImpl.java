package com.framework.loippi.service.impl.report;

import com.framework.loippi.entity.report.ShopOrderDailyReport;
import com.framework.loippi.entity.report.ShopOrderMonthReport;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.report.ShopOrderDailyReportService;
import com.framework.loippi.service.report.ShopOrderMonthReportService;
import org.springframework.stereotype.Service;

@Service
public class ShopOrderMonthReportServiceImpl extends GenericServiceImpl<ShopOrderMonthReport, Long> implements ShopOrderMonthReportService {
}
