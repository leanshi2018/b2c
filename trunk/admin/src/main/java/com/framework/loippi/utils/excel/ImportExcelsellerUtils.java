package com.framework.loippi.utils.excel;

import com.framework.loippi.utils.Dateutil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ImportExcelsellerUtils {

    /**
     * 读取Excel表格表头的内容
     *
     * @param in
     * @return String 表头内容的数组
     * @throws Exception
     */
    public static List<?> readExcelTitle(InputStream in, Object obj) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        //获得object类的属性
        Class<?> clazz = Class.forName(obj.getClass().getCanonicalName());
        Field[] field = clazz.getDeclaredFields();

        //标题总列数
        int colNum = row.getPhysicalNumberOfCells();//Excal中有多少列
        //判断对象属性和excal的属性的数目是否相同
        if (field.length != colNum) {
            return null;
        }
        //创建List容器
        List<Object> list = new ArrayList<Object>();
        //循环,将Excal中的数据放入到对象中
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            //得到excal内的一行
            row = sheet.getRow(i + 1);
            for (int j = 0; j < colNum; j++) {
                field[j].setAccessible(true);
                //得到Excal中的一个单元格
                HSSFCell cell = row.getCell(j);
                //System.out.println("cell=====" + cell + "====type====" + cell.getCellType());
                //判断是否和类中的属性的类型相同,如果相同那么将excal中的值赋给obj
                //PropertyDescriptor pd = new PropertyDescriptor(field[j].getName(), clazz);
                //System.out.println(field[j].getName());
                //Method method = pd.getWriteMethod();
                try {
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC://如果是数字
                            // 处理日期格式、时间格式
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                SimpleDateFormat sdf = null;
                                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                                    sdf = new SimpleDateFormat("HH:mm");
                                } else {// 日期
                                    sdf = new SimpleDateFormat(Dateutil.NORMAL_FORMAT);
                                }
                                Date date = cell.getDateCellValue();
                                System.out.println(sdf.format(date) + "==========111==" + cell);
                                field[j].set(obj, Dateutil.strToLong(sdf.format(date), null));
                                break;
                            } else if (cell.getCellStyle().getDataFormat() == 58) {
                                // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                double value = cell.getNumericCellValue();
                                Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                            } else {
                                double value = cell.getNumericCellValue();
                                CellStyle style = cell.getCellStyle();
                                DecimalFormat format = new DecimalFormat();
                                String temp = style.getDataFormatString();
                                // 单元格设置成常规
                                if (temp.equals("General")) {
                                    format.applyPattern("#");
                                }
                            }
                            Double d = cell.getNumericCellValue();
                            //商品和规格导入
                            if (j == 1 || j == 5 || j == 6 || j == 7) {
                                if (d > d.intValue()) {
                                    field[j].set(obj, d.toString());
                                } else {
                                    Integer s = d.intValue();
                                    field[j].set(obj, s.toString());
                                }
                            } else {
                                if (d > d.intValue()) {
                                    field[j].set(obj, d);
                                } else {
                                    field[j].set(obj, d.intValue());
                                }
                            }
                            break;

                        case HSSFCell.CELL_TYPE_STRING://如果是字符串
                            String str = cell.getRichStringCellValue().getString();
                            field[j].set(obj, str);
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA://如果是日期
                            String time = cell.getCellFormula();
                            System.out.print("time:-------------" + time);
                            log.debug(time);
                            field[j].set(obj, Timestamp.valueOf(time));
                    }
                } catch (Exception e) {
                    try {
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            String time = cell.getRichStringCellValue().getString();
                            field[j].set(obj, Timestamp.valueOf(time));
                            continue;
                        }
                    } catch (Exception e2) {
                        try {
                            if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                String decimal = cell.getRichStringCellValue().getString();
                                BigDecimal bd = new BigDecimal(decimal);
                                field[j].set(obj, bd);
                                continue;
                            }
                        } catch (Exception e3) {
                            field[j].set(obj, null);
                            continue;
                        }

                    }
                    field[j].set(obj, null);
                }
            }
            list.add(obj);
            obj = obj.getClass().newInstance();
        }
        return list;
    }

}
