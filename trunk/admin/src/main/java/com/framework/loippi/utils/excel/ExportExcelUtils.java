package com.framework.loippi.utils.excel;

import com.ibm.icu.text.DateFormat;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

public class ExportExcelUtils {
	//header 自定义表头如传入（headers = { "商品ID", "商品名称", "商品分类id", "商品类型,1为全新、2为二手", "商品副标题","商品店铺价格"}）
	public static String export(List<?> list,String url,String[] header) throws Exception{
		 return export(list,null,url, header, "");
	}
	
	/**
	 * 
	 * @param list
	 * @param url
	 * @param header
	 * @param footer
	 * @return
	 * @throws Exception
	 */
	public static String export(List<?> list,String url,String[] header, String footer) throws Exception{
		 return export(list,null,url, header, footer);
	}
	
	//导出Excal
	public static String export(List<?> list, List<String> propertyNames, String url,String[] header, String footer) throws Exception{
		
        HSSFWorkbook wb = new HSSFWorkbook();  
        String excelurl="";//路径
      	Long times=System.currentTimeMillis();//定义时间戳
        wb = exportCell(list, propertyNames, header, footer);  
        //将文件存到指定位置  
        try  
        {  
        	File file=new File(url); 
        	//如果文件夹不存在则创建    
        	if  (!file .exists()  && !file .isDirectory())      
        	{       
        	     file .mkdirs();
        	}
            FileOutputStream fout = new FileOutputStream(url+times+".xls");
            wb.write(fout); 
            excelurl=times+".xls";
            fout.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return excelurl;
	}
	
	public static HSSFWorkbook exportCell(List<?> list, List<String> propertyNames,String[] header, String footer) throws Exception{
		Object obj = list.get(0);
		int rowNum = list.size();
		// 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet(obj.getClass().getSimpleName() + "表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
        //获取list中的对象所有属性的集合
        Field[] fields = obj.getClass().getDeclaredFields();        
        //获得这个对象属性的数目
      	int num = fields.length;
      	//判断属性名字是不是为空
      	if(propertyNames != null &&  num == propertyNames.size()){      		
      		for(int i = 0; i < num; i++){
            	HSSFCell cell = row.createCell((short) i);  
                cell.setCellValue(propertyNames.get(i));  
                cell.setCellStyle(style); 
            }
      	} else {
      		  if(header.length!=0){
      			for (short i = 0; i < header.length; i++) {  
                    HSSFCell cell = row.createCell(i);  
                    cell.setCellStyle(style);  
                    HSSFRichTextString text = new HSSFRichTextString(header[i]);  
                    cell.setCellValue(text);  
                }  
      		  }else{//当header为空时读取的是类属性
      			 for(int i = 0; i < num; i++){
                   	HSSFCell cell = row.createCell((short) i);  
                       cell.setCellValue(fields[i].getName());  
                       cell.setCellStyle(style); 
                   }
      		  }
      	}
        for (int i = 0; i < rowNum; i++){  
        	//拿到当前的对象
        	obj = list.get(i);
        	//拿到全类名,通过反射获取这个类
            Class<?> clazz = Class.forName(obj.getClass().getCanonicalName());
            //得到所有属性的集合
            fields = clazz.getDeclaredFields();       	       	
        	//创建一行
            row = sheet.createRow((int) i + 1);  
            //创建单元格，并设置值
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			for(int j = 0; j < fields.length; j++){
            	Field field = fields[j];
            	PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            	Method rm = pd.getReadMethod();
            	if (rm.invoke(obj)  instanceof Integer) {
            		Integer number = (Integer) rm.invoke(obj);		
            		row.createCell((short) j).setCellValue(number);
				}
            	if(rm.invoke(obj)  instanceof String){
            		String str = (String) rm.invoke(obj)	;
            		row.createCell((short) j).setCellValue(str);
            	}
            	if(rm.invoke(obj)  instanceof Timestamp){
            		String str = rm.invoke(obj) + "";
            		row.createCell((short) j).setCellValue(str);
            	}
				if (rm.invoke(obj) instanceof Date) {
            		String str = dateFormat.format((Date) rm.invoke(obj));
            		row.createCell((short)j).setCellValue(str);
				}
				if(rm.invoke(obj)  instanceof BigDecimal){
            		BigDecimal bd = (BigDecimal) rm.invoke(obj);
            		String str = String.valueOf(bd.doubleValue());
            		row.createCell((short) j).setCellValue(str);
            	}
            }
        }
        if(!"".equals(footer)){
        	row = sheet.createRow(rowNum + 1);
        	HSSFCellStyle style1 = wb.createCellStyle();  
        	style1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            HSSFCell cell = row.createCell(1);  
            cell.setCellStyle(style1);  
            HSSFRichTextString text = new HSSFRichTextString(footer);  
            cell.setCellValue(text);
            sheet.addMergedRegion(new CellRangeAddress(     
            	rowNum + 1, //first row (0-based)  from 行     
            	rowNum + 1, //last row  (0-based)  to 行     
                1, //first column (0-based) from 列     
                header.length-1  //last column  (0-based)  to 列     
            ));  
        }
        return wb;
	}
}
