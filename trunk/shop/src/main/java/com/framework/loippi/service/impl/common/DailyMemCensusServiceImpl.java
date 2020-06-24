package com.framework.loippi.service.impl.common;


import com.framework.loippi.entity.common.MemberShippingBehavior;
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

import com.framework.loippi.dao.common.DailyMemCensusDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.entity.common.DailyMemCensus;
import com.framework.loippi.pojo.common.MemCensusVo;
import com.framework.loippi.pojo.common.RankNumVo;
import com.framework.loippi.service.common.DailyMemCensusService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.Paramap;


/**
 * 日会员信息统计
 */
@Service
@Slf4j
@Transactional
public class DailyMemCensusServiceImpl extends GenericServiceImpl<DailyMemCensus, Long> implements DailyMemCensusService {
    @Resource
    private DailyMemCensusDao dailyMemCensusDao;
    @Resource
    private RdMmBasicInfoDao rdMmBasicInfoDao;
    @Resource
    private RdMmRelationDao rdMmRelationDao;
    private static Logger logger = Logger.getLogger(DailyMemCensusServiceImpl.class.getName());
    /**
     * 获取统计当日app会员信息
     */
    @Override
    public void getDailyMemCensus() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DATE,-1);
        Date time = instance.getTime();
        //1获取时间确定reportCode
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String reportCode = format.format(time);
        HashMap<String, Object> map = new HashMap<>();
        String start=reportCode+" 00:00:00";
        String end=reportCode+" 23:59:59";
        map.put("start",start);
        map.put("end",end);
        //2.查询是否存在reportCode数据
        List<DailyMemCensus> list = dailyMemCensusDao.findByParams(Paramap.create().put("reportCode", reportCode));
        if(list!=null&&list.size()>0){
            System.out.println(reportCode+"日已存在记录");
            return;
        }
        //3.生成当日会员信息统计记录
        DailyMemCensus dailyMemCensus = new DailyMemCensus();
        dailyMemCensus.setReportCode(reportCode);
        dailyMemCensus.setStatisticalTime(new Date());
        Long count = rdMmBasicInfoDao.count();
        dailyMemCensus.setMemTotal(count);
        Long dailyMem=rdMmBasicInfoDao.findNewMem(map);
        dailyMemCensus.setDailyMem(dailyMem);
        map.put("NOFlag",1);
        Long dailyNewMem=rdMmBasicInfoDao.findNewMem(map);
        dailyMemCensus.setDailyNewMem(dailyNewMem);
        map.put("NOFlag",2);
        Long dailyOldMem=rdMmBasicInfoDao.findNewMem(map);
        dailyMemCensus.setDailyOldMem(dailyOldMem);
        Long newMem = rdMmRelationDao.count(Paramap.create().put("nOFlag",1));
        dailyMemCensus.setNewMem(newMem);
        Long oldMem = rdMmRelationDao.count(Paramap.create().put("nOFlag",2));
        dailyMemCensus.setOldMem(oldMem);
        map.put("rank",0);
        MemCensusVo memCensusVo=rdMmRelationDao.getMemAtotal(map);
        if(memCensusVo!=null){
            dailyMemCensus.setPayCommonMem(memCensusVo.getNum());
            dailyMemCensus.setPayCommonUnitPrice(memCensusVo.getAmountTotal().divide(new BigDecimal(Long.toString(memCensusVo.getNum())),2,BigDecimal.ROUND_HALF_UP));
        }else {
            dailyMemCensus.setPayCommonMem(0L);
            dailyMemCensus.setPayCommonUnitPrice(new BigDecimal("0.00"));
        }
        Long noPayCommonMem=rdMmRelationDao.getNoPayCommonMem();
        dailyMemCensus.setNoPayCommonMem(noPayCommonMem);
        dailyMemCensus.setD0Num(0L);
        dailyMemCensus.setD1Num(0L);
        dailyMemCensus.setD2Num(0L);
        dailyMemCensus.setD3Num(0L);
        dailyMemCensus.setD4Num(0L);
        dailyMemCensus.setD5Num(0L);
        dailyMemCensus.setD6Num(0L);
        dailyMemCensus.setD7Num(0L);
        dailyMemCensus.setD8Num(0L);
        dailyMemCensus.setD9Num(0L);
        List<RankNumVo> rankNumVos=rdMmRelationDao.findRankNum();
        if(rankNumVos!=null&&rankNumVos.size()>0){
            for (RankNumVo rankNumVo : rankNumVos) {
                if(rankNumVo.getRank()==0){
                    dailyMemCensus.setD0Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==1){
                    dailyMemCensus.setD1Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==2){
                    dailyMemCensus.setD2Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==3){
                    dailyMemCensus.setD3Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==4){
                    dailyMemCensus.setD4Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==5){
                    dailyMemCensus.setD5Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==6){
                    dailyMemCensus.setD6Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==7){
                    dailyMemCensus.setD7Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==8){
                    dailyMemCensus.setD8Num(rankNumVo.getNum());
                }
                if(rankNumVo.getRank()==9){
                    dailyMemCensus.setD9Num(rankNumVo.getNum());
                }
            }
        }
        //4.写入数据
        dailyMemCensusDao.insert(dailyMemCensus);
    }

    @Override
    public void getExcelByTime(String s, String s1) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("timeLeft",s);
        map.put("timeRight",s1);
        List<DailyMemCensus> list=dailyMemCensusDao.findByTime(map);
        System.out.println(list.size());
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = buildDataSheet(workbook);
        //构建每行的数据内容
        int rowNum = 1;
        for (Iterator<DailyMemCensus> it = list.iterator(); it.hasNext(); ) {
            DailyMemCensus data = it.next();
            if (data == null) {
                continue;
            }
            //输出行数据
            Row row = sheet.createRow(rowNum++);
            convertDataToRow(data, row);
        }
        FileOutputStream fileOut = null;
        try {
            String exportFilePath = "E:\\DailyMemCensus.xlsx";
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
    private static void convertDataToRow(DailyMemCensus data, Row row){
        int cellNum = 0;
        Cell cell;
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getReportCode()).orElse(""));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getMemTotal()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getDailyMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getDailyNewMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getDailyOldMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getNewMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getOldMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getPayCommonMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getNoPayCommonMem()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getPayCommonUnitPrice().toString()).orElse("0.00"));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD0Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD1Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD2Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD3Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD4Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD5Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD6Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD7Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD8Num()).orElse(0L));
        cell = row.createCell(cellNum++);
        cell.setCellValue(Optional.ofNullable(data.getD9Num()).orElse(0L));
    }

    private static List<String> CELL_HEADS; //列头

    static{
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("时间");
        CELL_HEADS.add("会员总数");
        CELL_HEADS.add("新增会员数");
        CELL_HEADS.add("新增新会员数");
        CELL_HEADS.add("新增老会员数");
        CELL_HEADS.add("新会员总数");
        CELL_HEADS.add("老会员总数");
        CELL_HEADS.add("已消费普通会员数");
        CELL_HEADS.add("未消费普通会员数");
        CELL_HEADS.add("已消费普通会员客单价");
        CELL_HEADS.add("普通会员人数");
        CELL_HEADS.add("vip人数");
        CELL_HEADS.add("代理会员人数");
        CELL_HEADS.add("初级代理店人数");
        CELL_HEADS.add("一级代理店人数");
        CELL_HEADS.add("二级代理店人数");
        CELL_HEADS.add("三级代理店人数");
        CELL_HEADS.add("旗舰店人数");
        CELL_HEADS.add("高级旗舰店人数");
        CELL_HEADS.add("超级旗舰店人数");
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
