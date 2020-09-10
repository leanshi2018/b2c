package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelCostDao;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.travel.RdTravelCost;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelCostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class RdTravelCostServiceImpl extends GenericServiceImpl<RdTravelCost, Long>
        implements RdTravelCostService {
    @Resource
    private RdTravelCostDao rdTravelCostDao;

    @Override
    public void export(RdTravelCost costInfo) {
        HashMap<String, Object> map = new HashMap<>();
        if(costInfo.getActivityId()!=null){
            map.put("activityId",costInfo.getActivityId());
        }else {
            map.put("activityId",null);
        }
        if(costInfo.getMmCode()!=null&&!"".equals(costInfo.getMmCode())){
            map.put("mmCode",costInfo.getMmCode());
        }else {
            map.put("mmCode",null);
        }
        List<RdTravelCost> rdTravelCosts = rdTravelCostDao.findByParams(map);
        if(rdTravelCosts!=null&&rdTravelCosts.size()>0){
            Workbook workbook = new SXSSFWorkbook();
            Sheet sheet = buildDataSheet(workbook);
            //构建每行的数据内容
            int rowNum = 1;
            for (Iterator<RdTravelCost> it = rdTravelCosts.iterator(); it.hasNext(); ) {
                RdTravelCost data = it.next();
                if (data == null) {
                    continue;
                }
                //输出行数据
                Row row = sheet.createRow(rowNum++);
                convertDataToRow(data, row);
            }
            FileOutputStream fileOut = null;
            try {
                String exportFilePath = "E:\\travelCost.xlsx";
                File exportFile = new File(exportFilePath);
                if (!exportFile.exists()) {
                    exportFile.createNewFile();
                }

                fileOut = new FileOutputStream(exportFilePath);
                workbook.write(fileOut);
                fileOut.flush();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (null != fileOut) {
                        fileOut.close();
                    }
                /*if (null != workbook) {
                    workbook.close();
                }*/
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
    private static void convertDataToRow(RdTravelCost data, Row row){
        Optional<RdTravelCost> optional = Optional.ofNullable(data);
        int cellNum = 0;
        Cell cell;
        cell = row.createCell(cellNum++);
        //1.编号
        cell.setCellValue(optional.map(RdTravelCost::getId).orElse(null));
        cell = row.createCell(cellNum++);
        //2.旅游活动id
        cell.setCellValue(optional.map(RdTravelCost::getActivityId).orElse(null));
        cell = row.createCell(cellNum++);
        //3.旅游活动名称
        cell.setCellValue(optional.map(RdTravelCost::getActivityName).orElse(null));
        cell = row.createCell(cellNum++);
        //4.会员编号
        cell.setCellValue(optional.map(RdTravelCost::getMmCode).orElse(null));
        cell = row.createCell(cellNum++);
        //5.会员昵称
        cell.setCellValue(optional.map(RdTravelCost::getMNickName).orElse(null));
        cell = row.createCell(cellNum++);
        //6.参团人数
        cell.setCellValue(optional.map(RdTravelCost::getJoinNum).orElse(null));
        cell = row.createCell(cellNum++);
        //7.旅游券id
        cell.setCellValue(optional.map(RdTravelCost::getTicketId).orElse(null));
        cell = row.createCell(cellNum++);
        //8.旅游券面值
        cell.setCellValue((optional.map(RdTravelCost::getTicketPrice).orElse(BigDecimal.ZERO)).toString());
        cell = row.createCell(cellNum++);
        //9.旅游券使用数量
        cell.setCellValue(optional.map(RdTravelCost::getUseNum).orElse(null));
        cell = row.createCell(cellNum++);
        //10.旅游活动总费用
        cell.setCellValue((optional.map(RdTravelCost::getMoneyTotal).orElse(BigDecimal.ZERO)).toString());
        cell = row.createCell(cellNum++);
        //11.旅游券抵扣金额
        cell.setCellValue((optional.map(RdTravelCost::getMoneyTicket).orElse(BigDecimal.ZERO)).toString());
        cell = row.createCell(cellNum++);
        //12.需补差价
        cell.setCellValue((optional.map(RdTravelCost::getMoenyFill).orElse(BigDecimal.ZERO)).toString());
        cell = row.createCell(cellNum++);
        //13.已补差价
        cell.setCellValue((optional.map(RdTravelCost::getMoenyYet).orElse(BigDecimal.ZERO)).toString());
        cell = row.createCell(cellNum++);
        //14.剩余应补差价
        cell.setCellValue((optional.map(RdTravelCost::getMoneyResidue).orElse(BigDecimal.ZERO)).toString());
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
    private static List<String> CELL_HEADS; //列头
    static{
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("编号");
        CELL_HEADS.add("旅游活动id");
        CELL_HEADS.add("旅游活动名称");
        CELL_HEADS.add("会员编号");
        CELL_HEADS.add("会员昵称");
        CELL_HEADS.add("参团人数");
        CELL_HEADS.add("旅游券id");
        CELL_HEADS.add("旅游券面值");
        CELL_HEADS.add("旅游券使用数量");
        CELL_HEADS.add("旅游活动总费用");
        CELL_HEADS.add("旅游券抵扣金额");
        CELL_HEADS.add("需补差价");
        CELL_HEADS.add("已补差价");
        CELL_HEADS.add("剩余应补差价");
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
