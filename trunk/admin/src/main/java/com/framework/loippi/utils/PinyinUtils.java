 package com.framework.loippi.utils;

 import net.sourceforge.pinyin4j.PinyinHelper;
 import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
 import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
 import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
 import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

 /**
  * Created by longbh on 16/7/29.
  */
 public class PinyinUtils {
     static HanyuPinyinOutputFormat format = null;

     public static enum Type {
         UPPERCASE,              //全部大写
         LOWERCASE,              //全部小写
         FIRSTUPPER              //首字母大写
     }

     static {
         format = new HanyuPinyinOutputFormat();
         format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
         format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
     }

     public static String toFirst(String str) {
         try {
             if (str == null || str.trim().length() == 0)
                 return "";
             format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
             String py = "";
             String[] t;
             for (int i = 0; i < 1; i++) {
                 char c = str.charAt(i);
                 if ((int) c <= 128)
                     py += c;
                 else {
                     t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                     if (t == null)
                         py += c;
                     else {
                         if (t[0].length() > 0) {
                             py += t[0].substring(0, 1);
                         }
                     }
                 }
             }
             return py.trim();
         } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
             badHanyuPinyinOutputFormatCombination.printStackTrace();
         }
         return str;
     }

     public static String toPinYin(String str) {
         try {
             return toPinYin(str, "", Type.UPPERCASE);
         } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
             badHanyuPinyinOutputFormatCombination.printStackTrace();
         }
         return str;
     }

     public static String toPinYin(String str, String spera) {
         try {
             return toPinYin(str, spera, Type.UPPERCASE);
         } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
             badHanyuPinyinOutputFormatCombination.printStackTrace();
         }
         return str;
     }

     /**
      * 将str转换成拼音，如果不是汉字或者没有对应的拼音，则不作转换
      * 如： 明天 转换成 MINGTIAN
      *
      * @param str
      * @param spera
      * @return
      * @throws BadHanyuPinyinOutputFormatCombination
      */
     public static String toPinYin(String str, String spera, Type type) throws BadHanyuPinyinOutputFormatCombination {
         if (str == null || str.trim().length() == 0)
             return "";
         if (type == Type.UPPERCASE)
             format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
         else
             format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

         String py = "";
         String temp = "";
         String[] t;
         for (int i = 0; i < str.length(); i++) {
             char c = str.charAt(i);
             if ((int) c <= 128)
                 py += c;
             else {
                 t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                 if (t == null)
                     py += c;
                 else {
                     temp = t[0];
                     if (type == Type.FIRSTUPPER)
                         temp = t[0].toUpperCase().charAt(0) + temp.substring(1);
                     py += temp + (i == str.length() - 1 ? "" : spera);
                 }
             }
         }
         return py.trim();
     }
 }
