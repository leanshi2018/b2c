package com.framework.loippi.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字工具类
 */
public class NumberUtils {

    private static Pattern numberPatter = Pattern.compile("^[\\d\\.E\\,\\+\\-]*$");

    /**
     * 是否是数字
     */
    public static boolean isNumber(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return numberPatter.matcher(str).find();
    }

    /**
     * 是否是整形数据
     */
    public static boolean isInt(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 是否是整形数据
     */
    public static boolean isInteger(String str) {
        return isInt(str);
    }

    /**
     * 是否是长整形数据
     */
    public static boolean isLong(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        try {
            Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static BigDecimal getRandomBigDecimal(BigDecimal max, BigDecimal min) {
        if (max == null || min == null) {
            return BigDecimal.ZERO;
        }

        if (max.compareTo(min) == -1) {
            throw new RuntimeException("最大数不能小于最小数");
        }

        if (max.compareTo(min) == 0) {
            return min;
        }

        double v = (max.doubleValue() - min.doubleValue()) * new Random().nextDouble();
        return getsetScale(new BigDecimal(min.doubleValue() + v), 2);
    }

    /**
     * 四舍五入
     */
    public static double round(double v, int scale) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    private static long Seed = System.currentTimeMillis();

    private static Random rand = new Random();

    /**
     * 在0-max范围内获取随机整数
     */
    public static int getRandomInt(int max) {
        rand.setSeed(Seed);
        Seed++;
        return rand.nextInt(max);
    }

    /**
     * 二进制转为整形
     */
    public static int toInt(byte[] bs) {
        return toInt(bs, 0);
    }

    /**
     * 从指定位置开始读取4位二进制，转换为整形
     */
    public static int toInt(byte[] bs, int start) {
        int i = 0;
        i += (bs[start] & 255) << 24;
        i += (bs[start + 1] & 255) << 16;
        i += (bs[start + 2] & 255) << 8;
        i += (bs[start + 3] & 255);
        return i;
    }

    /**
     * 整形转为二进制
     */
    public static byte[] toBytes(int i) {
        byte[] bs = new byte[4];
        bs[0] = (byte) (i >> 24);
        bs[1] = (byte) (i >> 16);
        bs[2] = (byte) (i >> 8);
        bs[3] = (byte) (i & 255);
        return bs;
    }

    /**
     * 整形转为4位二进制数，写入到指定数组的指定位置
     */
    public static void toBytes(int i, byte[] bs, int start) {
        bs[start] = (byte) (i >> 24);
        bs[start + 1] = (byte) (i >> 16);
        bs[start + 2] = (byte) (i >> 8);
        bs[start + 3] = (byte) (i & 255);
    }

    /**
     * 读取2位二进制，转为短整形
     */
    public static short toShort(byte[] bs) {
        return toShort(bs, 0);
    }

    /**
     * 从指定数组的指定位置开始，读取2位二进制，转为短整形
     */
    public static short toShort(byte[] bs, int start) {
        short i = 0;
        i += (bs[start + 0] & 255) << 8;
        i += (bs[start + 1] & 255);
        return i;
    }

    /**
     * 短整形转为二进制
     */
    public static byte[] toBytes(short i) {
        byte[] bs = new byte[2];
        bs[0] = (byte) (i >> 8);
        bs[1] = (byte) (i & 255);
        return bs;
    }

    /**
     * 短整形转为2位二进制数，写入到指定数组的指定位置
     */
    public static void toBytes(short i, byte[] bs, int start) {
        bs[start + 0] = (byte) (i >> 8);
        bs[start + 1] = (byte) (i & 255);
    }

    /**
     * 长整形转为8位二进制
     */
    public static byte[] toBytes(long i) {
        byte[] bs = new byte[8];
        bs[0] = (byte) (i >> 56);
        bs[1] = (byte) (i >> 48);
        bs[2] = (byte) (i >> 40);
        bs[3] = (byte) (i >> 32);
        bs[4] = (byte) (i >> 24);
        bs[5] = (byte) (i >> 16);
        bs[6] = (byte) (i >> 8);
        bs[7] = (byte) (i & 255);
        return bs;
    }

    /**
     * 长整形转为8位二进制数，写入到指定数组的指定位置
     */
    public static void toBytes(long l, byte[] bs, int start) {
        byte[] arr = toBytes(l);
        for (int i = 0; i < 8; i++) {
            bs[start + i] = arr[i];
        }
    }

    /**
     * 二进制转长整形
     */
    public static long toLong(byte[] bs) {
        return toLong(bs, 0);
    }

    /**
     * 从指定数据的指定位置开始，读取8位二进制，转为长整形
     */
    public static long toLong(byte[] bs, int index) {
        return ((((long) bs[index] & 0xff) << 56) | (((long) bs[index + 1] & 0xff) << 48) | (
            ((long) bs[index + 2] & 0xff) << 40) | (((long) bs[index + 3] & 0xff) << 32) | (
            ((long) bs[index + 4] & 0xff) << 24) | (((long) bs[index + 5] & 0xff) << 16) | (
            ((long) bs[index + 6] & 0xff) << 8) | (((long) bs[index + 7] & 0xff) << 0));

    }

    /**
     * 负数变正数
     */
    public static String Mathabs(String amount) {

        double num = Math.abs(Double.valueOf(amount));

        return String.valueOf(num);
    }


    /**
     * String 类型转BigDecimal
     *
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimal(String param) {
        if (StringUtils.isBlank(param)) {
            param = "0";
        }
        return new BigDecimal(param);
    }

    /**
     * String 类型转BigDecimal
     *
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimalFmtCurrency(String param) {

        if (StringUtils.isBlank(param)) {
            param = "0";
        }
        param = getNonFmtCurrency(param);
        return new BigDecimal(param);
    }

    /**
     * 将格式化的金额（ 单位元）字符串转换为没格式的金额（单位分）
     */
    public static String getNonFmtCurrency(String aCurrency) {
        if (aCurrency == null) {
            return "";
        }

        BigDecimal tranamt = new BigDecimal(aCurrency.replaceAll(",", ""));
        tranamt = tranamt.multiply(new BigDecimal(100));
        DecimalFormat dFmt = new DecimalFormat("###0");

        return dFmt.format(tranamt);
    }


    /**
     * 将格式化的金额字符串转换为没格式的金额
     *
     * @param aCurrency 需要的值
     * @param fmt 需要格式的格式
     * @info 例如: getFmtCurrency("1","###0.00") 返回 1.00
     */
    public static String getFmtCurrency(String aCurrency, String fmt) {
        if (aCurrency == null) {
            return "";
        }
        if (StringUtils.isEmpty(fmt)) {
            fmt = "###0.00";
        }
        BigDecimal tranamt = new BigDecimal(aCurrency.replaceAll(",", ""));
        DecimalFormat dFmt = new DecimalFormat(fmt);
        return dFmt.format(tranamt);
    }

    /**
     * 四舍五入
     *
     * @return BigDecimal
     */
    public static BigDecimal getsetScale(BigDecimal decimal, int num) {
        BigDecimal bigDecimal = decimal.setScale(num, BigDecimal.ROUND_HALF_UP);
        return bigDecimal;
    }

    /**
     * 格式化
     */
    public static BigDecimal format(BigDecimal decimal) {
        if (decimal == null) {
            return new BigDecimal(0.00);
        }
        return getsetScale(decimal, 2);
    }

    /**
     * 格式化
     */
    public static BigDecimal format(Double number) {
        if (number == null) {
            return new BigDecimal(0.00);
        }
        return getsetScale(new BigDecimal(number), 2);
    }

    /**
     * 分转元
     *
     * @return String
     */
    public static String divide(String param) {
        param = getBigDecimal(param).divide(new BigDecimal("100")).toString();
        return param;
    }

    /**
     * 元转分
     *
     * @return String
     */
    public static String multiply(String param) {
        param = getBigDecimal(param).multiply(new BigDecimal("100")).toString();
        return param;
    }

    /**
     * 百分数小数点 * 100
     *
     * @return Double
     */
    public static Double multiplyDouble(String param) {

        Double dou = getBigDecimal(param).multiply(new BigDecimal("100")).doubleValue();
        return dou;
    }

    /**
     * 百分数小数点 * 100
     *
     * @return Double
     */
    public static long multiplyLong(String param) {

        Long log = getBigDecimal(param).multiply(new BigDecimal("100")).longValue();
        return log;
    }

    /**
     * 正则表达式 验证 整数是否是数字
     */
    public static boolean matches(String param) {
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(param);
        boolean b = m.matches();
        return b;
    }


    private static DecimalFormat df = new DecimalFormat("#");
    private static DecimalFormat df2 = new DecimalFormat("0.00000000");

    /**
     * 格式化数字，不保留小数，四舍五入，由元换算分
     */
    public static String formatNumToString(String str) {
        if (str == null || str.equals("")) {
            return "0";
        } else {
            return df.format(new Double(str) * 100);
        }
    }

    /**
     * 格式化数字，不保留小数，四舍五入，由元换算分
     */
    public static String formatNumToString2(String str) {
        if (str == null || str.equals("")) {
            return "0";
        } else {
            return (new Double(df2.format(new Double(str) / 100))).toString();
        }
    }

    /**
     * 小数点后面为0 的 自动抹掉，
     *
     * @param @param contracts
     * @return String 返回类型
     * @Title: getNumberString
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static String getNumberString(String contracts) {

        Double d = Double.parseDouble(contracts);
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        String parms = nf.format(d);
        return parms;
    }

    /**
     * @return Int 返回类型
     * @Title: 获取6位随机数
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public static int getRandomNumber() {
        int result = (int) ((Math.random() * 9 + 1) * 100000);
        return result;
    }

    public static void main(String[] args) {
        //		byte[] bs = NumberUtils.toBytes(Long.MAX_VALUE);
        //		System.out.println(toLong(bs));
        //		System.out.println(Long.MAX_VALUE);
        //		double s = NumberUtils.round(111.111, 1);
        //		System.out.println(s);

        System.out.println("===" + getFmtCurrency("1", null));
        System.out.println(isNumber("123.5"));


    }

}
