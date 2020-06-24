package com.framework.loippi.service.impl.common;

import com.framework.loippi.dao.common.MemberShippingBehaviorDao;
import com.framework.loippi.entity.common.MemberShippingBehavior;
import com.framework.loippi.pojo.common.MemberShippingBehaviorVo;
import com.framework.loippi.service.common.MemberShippingBehaviorService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.utils.Paramap;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
@Slf4j
@Transactional
public class MemberShippingBehaviorServiceImpl extends GenericServiceImpl<MemberShippingBehavior, Long> implements MemberShippingBehaviorService {
    @Resource
    private MemberShippingBehaviorDao memberShippingBehaviorDao;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopOrderService shopOrderService;

    private static Logger logger = Logger.getLogger(MemberShippingBehaviorServiceImpl.class.getName());

    /**
     * 定时任务执行生成每日新老会员购买行为统计
     */
    @Override
    public void getMemberShippingBehavior() {
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
        //2.查询当日是否已经存在记录
        List<MemberShippingBehavior> list = memberShippingBehaviorDao.findByParams(Paramap.create().put("reportCode", reportCode));
        if(list!=null&&list.size()>0){
            System.out.println(reportCode+"日已存在记录");
            return;
        }
        //3.生成记录
        MemberShippingBehavior behavior = new MemberShippingBehavior();
        behavior.setReportCode(reportCode);
        behavior.setStatisticalTime(new Date());
        //分别查询出老会员总数和已经升级为vip会员的新会员
        Long newVip=rdMmRelationService.findNewVipRankMoreOne();
        int intValue = newVip.intValue();
        List<MemberShippingBehaviorVo> voList=shopOrderService.findNewShippingBehavior();
        if(voList!=null&&voList.size()>0){
            Integer zero=0;
            Integer one=0;
            Integer twoMore=0;
            for (MemberShippingBehaviorVo memberShippingBehaviorVo : voList) {
                if(memberShippingBehaviorVo.getOrderNum()==1){
                    one++;
                }else {
                    twoMore++;
                }
            }
            zero=intValue-one-twoMore;
            behavior.setNewVipBuyZero(zero);
            behavior.setNewVipBuyOne(one);
            behavior.setNewVipBuyTwomore(twoMore);
            behavior.setBuyZeroRate(new BigDecimal(Integer.toString(zero)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(intValue)),2,BigDecimal.ROUND_HALF_UP));
            behavior.setBuyOneRate(new BigDecimal(Integer.toString(one)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(intValue)),2,BigDecimal.ROUND_HALF_UP));
            behavior.setBuyTwomoreRate(new BigDecimal(Integer.toString(twoMore)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(intValue)),2,BigDecimal.ROUND_HALF_UP));
        }else {
            behavior.setNewVipBuyZero(intValue);
            behavior.setNewVipBuyOne(0);
            behavior.setNewVipBuyTwomore(0);
            behavior.setBuyZeroRate(new BigDecimal("100.00"));
            behavior.setBuyOneRate(new BigDecimal("0.00"));
            behavior.setBuyTwomoreRate(new BigDecimal("0.00"));
        }
        Long oldMember=rdMmRelationService.count(Paramap.create().put("nOFlag",2));
        List<MemberShippingBehaviorVo> voList2=shopOrderService.findOldShippingBehavior();
        if(voList2!=null&&voList2.size()>0){
            Integer oldZero=0;
            Integer oldOne=0;
            Integer oldTwoMore=0;
            for (MemberShippingBehaviorVo memberShippingBehaviorVo : voList2) {
                if(memberShippingBehaviorVo.getOrderNum()==1){
                    oldOne++;
                }else {
                    oldTwoMore++;
                }
            }
            oldZero=oldMember.intValue()-oldOne-oldTwoMore;
            behavior.setOldMemBuyZero(oldZero);
            behavior.setOldMemBuyOne(oldOne);
            behavior.setOldMemBuyTwomore(oldTwoMore);
            behavior.setOldBuyZeroRate(new BigDecimal(Integer.toString(oldZero)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(oldMember.intValue())),2,BigDecimal.ROUND_HALF_UP));
            behavior.setOldBuyOneRate(new BigDecimal(Integer.toString(oldOne)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(oldMember.intValue())),2,BigDecimal.ROUND_HALF_UP));
            behavior.setOldBuyTwomoreRate(new BigDecimal(Integer.toString(oldTwoMore)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(oldMember.intValue())),2,BigDecimal.ROUND_HALF_UP));
        }else {
            behavior.setOldMemBuyZero(oldMember.intValue());
            behavior.setOldMemBuyOne(0);
            behavior.setOldMemBuyTwomore(0);
            behavior.setOldBuyZeroRate(new BigDecimal("100.00"));
            behavior.setOldBuyOneRate(new BigDecimal("0.00"));
            behavior.setOldBuyTwomoreRate(new BigDecimal("0.00"));
        }
        //4.写入数据
        memberShippingBehaviorDao.insert(behavior);
    }

    @Override
    public void getExcelByTime(String s, String s1) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timeLeft",s);
        map.put("timeRight",s1);
        List<MemberShippingBehavior> list=memberShippingBehaviorDao.findByTime(map);
        System.out.println(list.size());
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = buildDataSheet(workbook);
        //构建每行的数据内容
        int rowNum = 1;
        for (Iterator<MemberShippingBehavior> it = list.iterator(); it.hasNext(); ) {
            MemberShippingBehavior data = it.next();
            if (data == null) {
                continue;
            }
            //输出行数据
            Row row = sheet.createRow(rowNum++);
            convertDataToRow(data, row);
        }
        FileOutputStream fileOut = null;
        try {
            String exportFilePath = "E:\\poiTest.xlsx";
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
    private static void convertDataToRow(MemberShippingBehavior data, Row row){
        int cellNum = 0;
        Cell cell;
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == data.getReportCode() ? "" : data.getReportCode());
        cell = row.createCell(cellNum++);
        if (null != data.getNewVipBuyZero()) {
            cell.setCellValue(data.getNewVipBuyZero());
        } else {
            cell.setCellValue(0);
        }
        cell = row.createCell(cellNum++);
        if (null != data.getBuyZeroRate()) {
            cell.setCellValue(data.getBuyZeroRate().toString());
        } else {
            cell.setCellValue("0.00");
        }
        cell = row.createCell(cellNum++);
        if (null != data.getNewVipBuyOne()) {
            cell.setCellValue(data.getNewVipBuyOne());
        } else {
            cell.setCellValue(0);
        }
        cell = row.createCell(cellNum++);
        if (null != data.getBuyOneRate()) {
            cell.setCellValue(data.getBuyOneRate().toString());
        } else {
            cell.setCellValue("0.00");
        }
        cell = row.createCell(cellNum++);
        if (null != data.getNewVipBuyTwomore()) {
            cell.setCellValue(data.getNewVipBuyTwomore());
        } else {
            cell.setCellValue(0);
        }
        cell = row.createCell(cellNum++);
        if (null != data.getBuyTwomoreRate()) {
            cell.setCellValue(data.getBuyTwomoreRate().toString());
        } else {
            cell.setCellValue("0.00");
        }
        cell = row.createCell(cellNum++);
        if (null != data.getOldMemBuyZero()) {
            cell.setCellValue(data.getOldMemBuyZero());
        } else {
            cell.setCellValue(0);
        }
        cell = row.createCell(cellNum++);
        if (null != data.getOldBuyZeroRate()) {
            cell.setCellValue(data.getOldBuyZeroRate().toString());
        } else {
            cell.setCellValue("0.00");
        }
        cell = row.createCell(cellNum++);
        if (null != data.getOldMemBuyOne()) {
            cell.setCellValue(data.getOldMemBuyOne());
        } else {
            cell.setCellValue(0);
        }
        cell = row.createCell(cellNum++);
        if (null != data.getOldBuyOneRate()) {
            cell.setCellValue(data.getOldBuyOneRate().toString());
        } else {
            cell.setCellValue("0.00");
        }
        cell = row.createCell(cellNum++);
        if (null != data.getOldMemBuyTwomore()) {
            cell.setCellValue(data.getOldMemBuyTwomore());
        } else {
            cell.setCellValue(0);
        }
        cell = row.createCell(cellNum++);
        if (null != data.getOldBuyTwomoreRate()) {
            cell.setCellValue(data.getOldBuyTwomoreRate().toString());
        } else {
            cell.setCellValue("0.00");
        }
    }

    private static List<String> CELL_HEADS; //列头

    static{
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("时间");
        CELL_HEADS.add("0次：新会员升级到vip后，无购买行为的用户数量");
        CELL_HEADS.add("升级后购买0次占升级到vip及其以上等级的新会员比例");
        CELL_HEADS.add("1次：新会员升级到vip后，有过1次购买行为的用户数量");
        CELL_HEADS.add("升级后购买1次占升级到vip及其以上等级的新会员比例");
        CELL_HEADS.add("≥2次：新会员升级到vip后，有过2次或以上购买行为的用户数量");
        CELL_HEADS.add("升级后购买2次及其以上占升级到vip及其以上等级的新会员比例");
        CELL_HEADS.add("0次：老会员注册后，无购买行为的用户数量");
        CELL_HEADS.add("老会员注册后购买0次订单占总的老会员绑定人数的比例");
        CELL_HEADS.add("1次：老会员注册后，有过1次购买行为的用户数量");
        CELL_HEADS.add("老会员注册后购买1次订单占总的老会员绑定人数的比例");
        CELL_HEADS.add("≥2次：老会员注册后，有过2次或以上购买行为的用户数量");
        CELL_HEADS.add("老会员注册后购买2次及其以上订单占总的老会员绑定人数的比例");
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
