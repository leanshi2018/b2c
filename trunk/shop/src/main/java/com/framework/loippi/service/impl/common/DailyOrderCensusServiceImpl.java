package com.framework.loippi.service.impl.common;

import com.framework.loippi.entity.common.DailyMemCensus;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.dao.common.DailyOrderCensusDao;
import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.pojo.common.CensusVo;
import com.framework.loippi.service.common.DailyOrderCensusService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.utils.Paramap;

/**
 * 日会员订单信息统计
 */
@Service
@Slf4j
@Transactional
public class DailyOrderCensusServiceImpl extends GenericServiceImpl<DailyOrderCensus, Long> implements DailyOrderCensusService {

    @Resource
    private DailyOrderCensusDao dailyOrderCensusDao;
    @Resource
    private ShopOrderService shopOrderService;

    private static Logger logger = Logger.getLogger(DailyOrderCensusServiceImpl.class.getName());

    @Override
    public void getDailyOrderCensus() {
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
        //2.判断对应reportCode是否已经存在对应的DailyOrderCensus记录 如果存在记录，则结束方法
        List<DailyOrderCensus> list = dailyOrderCensusDao.findByParams(Paramap.create().put("reportCode",reportCode));
        if(list!=null&&list.size()>0){
            System.out.println(reportCode+"日已存在记录");
            return;
        }
        //3.生成当日会员订单记录
        DailyOrderCensus orderCensus = new DailyOrderCensus();
        orderCensus.setReportCode(reportCode);
        orderCensus.setStatisticalTime(new Date());
        Integer orderNum=0;
        Integer yesNum=shopOrderService.findOrderYesterdayNum(map);//昨日订单总数，包含已支付和未支付订单
        if(yesNum!=null){
            orderNum=yesNum;
        }
        orderCensus.setOrderNum(orderNum);
        Integer effectiveOrderNum=0;
        Integer eYesNum=shopOrderService.findEffectiveOrderYesterdayNum(map);//昨日已支付订单数
        if(eYesNum!=null){
            effectiveOrderNum=eYesNum;
        }
        orderCensus.setEffectiveOrderNum(effectiveOrderNum);
        Integer invalidOrderNum=0;
        Integer iYesNum=shopOrderService.findInvalidOrderYesterdayNum(map);//昨日未支付订单数
        if(iYesNum!=null){
            invalidOrderNum=iYesNum;
        }
        orderCensus.setInvalidOrderNum(invalidOrderNum);
        map.put("orderPlatform", OrderState.PLATFORM_APP);//app
        Integer orderNumApp=0;
        Integer appYesNum=shopOrderService.findPlatformOrderYesterdayNum(map);//有效订单中app支付订单数 即order_platform = 1
        if(appYesNum!=null){
            orderNumApp=appYesNum;
        }
        orderCensus.setOrderNumApp(orderNumApp);
        map.put("orderPlatform", OrderState.PLATFORM_WECHAT);//wechat
        Integer orderNumWechat=0;
        Integer wechatYesNum=shopOrderService.findPlatformOrderYesterdayNum(map);//有效订单中app支付订单数 即order_platform = 0
        if(wechatYesNum!=null){
            orderNumWechat=wechatYesNum;
        }
        orderCensus.setOrderNumWechat(orderNumWechat);
        BigDecimal orderIncomeTotal=BigDecimal.ZERO;
        BigDecimal incomeTotal=shopOrderService.findYesIncomeTotal(map);
        if(incomeTotal!=null){
            orderIncomeTotal=orderIncomeTotal.add(incomeTotal);
        }
        orderCensus.setOrderIncomeTotal(orderIncomeTotal);
        if(orderIncomeTotal.compareTo(BigDecimal.ZERO)==1){
            BigDecimal orderPointTotal=shopOrderService.findYesPointTotal(map);
            BigDecimal pointProportion = orderPointTotal.divide(orderIncomeTotal,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
            orderCensus.setPointProportion(pointProportion);
        }else {
            orderCensus.setPointProportion(new BigDecimal("0.00"));
        }
        if(orderCensus.getEffectiveOrderNum()==0){
            orderCensus.setUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setUnitPrice(orderCensus.getOrderIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getEffectiveOrderNum())),2,BigDecimal.ROUND_HALF_UP));
        }
        map.put("orderType",1);
        CensusVo censusVoRetail=shopOrderService.findCensusData(map);
        if(censusVoRetail.getAmountTotal()!=null){
            orderCensus.setRetailIncomeTotal(censusVoRetail.getAmountTotal());
        }else {
            orderCensus.setRetailIncomeTotal(new BigDecimal("0.00"));
        }
        orderCensus.setRetailOrderNum(censusVoRetail.getOrderNum());
        if(censusVoRetail.getOrderNum()==null||censusVoRetail.getOrderNum()==0){
            orderCensus.setRetailUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setRetailUnitPrice(orderCensus.getRetailIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getRetailOrderNum())),2,BigDecimal.ROUND_HALF_UP));
        }
        map.put("orderType",2);
        CensusVo censusVoVip=shopOrderService.findCensusData(map);
        if(censusVoVip.getAmountTotal()!=null){
            orderCensus.setVipIncomeTotal(censusVoVip.getAmountTotal());
        }else {
            orderCensus.setVipIncomeTotal(new BigDecimal("0.00"));
        }
        orderCensus.setVipOrderNum(censusVoVip.getOrderNum());
        if(censusVoVip.getOrderNum()==null||censusVoVip.getOrderNum()==0){
            orderCensus.setVipUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setVipUnitPrice(orderCensus.getVipIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getVipOrderNum())),2,BigDecimal.ROUND_HALF_UP));
        }
        map.put("orderType",3);
        CensusVo censusVoBig=shopOrderService.findCensusData(map);
        if(censusVoBig.getAmountTotal()!=null){
            orderCensus.setBigIncomeTotal(censusVoBig.getAmountTotal());
        }else {
            orderCensus.setBigIncomeTotal(new BigDecimal("0.00"));
        }
        orderCensus.setBigOrderNum(censusVoBig.getOrderNum());
        if(censusVoBig.getOrderNum()==null||censusVoBig.getOrderNum()==0){
            orderCensus.setBigUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setBigUnitPrice(orderCensus.getBigIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getBigOrderNum())),2,BigDecimal.ROUND_HALF_UP));
        }
        //4.将日订单列表统计数据插入数据库
        dailyOrderCensusDao.insert(orderCensus);
    }

    @Override
    public void getExcelByTime(String s, String s1) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timeLeft",s);
        map.put("timeRight",s1);
        List<DailyOrderCensus> list=dailyOrderCensusDao.findByTime(map);
        System.out.println(list.size());
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = buildDataSheet(workbook);
        //构建每行的数据内容
        int rowNum = 1;
        for (Iterator<DailyOrderCensus> it = list.iterator(); it.hasNext(); ) {
            DailyOrderCensus data = it.next();
            if (data == null) {
                continue;
            }
            //输出行数据
            Row row = sheet.createRow(rowNum++);
            convertDataToRow(data, row);
        }
        FileOutputStream fileOut = null;
        try {
            String exportFilePath = "E:\\DailyOrderCensus.xlsx";
            File exportFile = new File(exportFilePath);
            if (!exportFile.exists()) {
                exportFile.createNewFile();
            }

            fileOut = new FileOutputStream(exportFilePath);
            workbook.write(fileOut);
            fileOut.flush();
        } catch (Exception e) {
            logger.warning("输出Excel时发生错误，错误原因：" + e.getMessage());
        } finally {
            try {
                if (null != fileOut) {
                    fileOut.close();
                }
                /*if (null != workbook) {
                    workbook.close();
                }*/
            } catch (IOException e) {
                logger.warning("关闭输出流时发生错误，错误原因：" + e.getMessage());
            }
        }
    }
    private static void convertDataToRow(DailyOrderCensus data, Row row){
        int cellNum = 0;
        Cell cell;
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getReportCode()).orElse(""));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getOrderNum()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getEffectiveOrderNum()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getInvalidOrderNum()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getOrderNumApp()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getOrderNumWechat()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getOrderIncomeTotal().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getPointProportion().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getUnitPrice().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getRetailOrderNum()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getRetailIncomeTotal().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getRetailUnitPrice().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getVipOrderNum()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getVipIncomeTotal().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getVipUnitPrice().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getBigOrderNum()).orElse(0));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getBigIncomeTotal().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getBigUnitPrice().toString()).orElse("0.00"));
    }

    private static List<String> CELL_HEADS; //列头

    static{
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("时间");
        CELL_HEADS.add("总订单数量");
        CELL_HEADS.add("有效订单数");
        CELL_HEADS.add("无效订单数");
        CELL_HEADS.add("app订单数");
        CELL_HEADS.add("微信小程序订单数");
        CELL_HEADS.add("订单总收入");
        CELL_HEADS.add("积分占比");
        CELL_HEADS.add("平均客单价");
        CELL_HEADS.add("零售订单数");
        CELL_HEADS.add("零售订单总额");
        CELL_HEADS.add("零售订单客单价");
        CELL_HEADS.add("会员订单数");
        CELL_HEADS.add("会员订单总额");
        CELL_HEADS.add("会员订单客单价");
        CELL_HEADS.add("大单数");
        CELL_HEADS.add("大单总额");
        CELL_HEADS.add("大单客单价");
    }
    private static Sheet buildDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        // 设置列头宽度
        for (int i=0; i<CELL_HEADS.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }
        // 设置默认行高
        sheet.setDefaultRowHeight((short) 400);
        // 构建头单元格样式
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        // 写入第一行各列的数据
        Row head = sheet.createRow(0);
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(CELL_HEADS.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }


    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        /*style.setAlignment(HorizontalAlignment.CENTER);
        //边框颜色和宽度设置
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 下边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 右边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 上边框
        //设置背景颜色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);*/
        return style;
    }
}
