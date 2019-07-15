
package com.framework.loippi.utils.alipay.pc.china.util;

import java.util.ArrayList;
import java.util.List;

import com.framework.loippi.entity.AliPayParse;
import org.apache.commons.lang3.StringUtils;

/* *
 *类名：AlipayParseUtil
 *功能：自定义解析
 *详细：工具类，可以对支付宝处理后批量信息转换成相应的list
 *版本：3.3
 *日期：2015-12-16
 *说明：
 */
public class AlipayParseUtil {
    public static List<AliPayParse> getlist(String alipaystr) {
        List<AliPayParse> alipayparselist = new ArrayList<AliPayParse>();
        if (StringUtils.isNotEmpty(alipaystr)) {
            String[] str = alipaystr.split("\\|");
            String[] sdf = null;
            for (int j = 0; j < str.length; j++) {
                AliPayParse aliPayParse = new AliPayParse();
                if (StringUtils.isNotEmpty(str[j])) {
                    sdf = str[j].split("\\^");
                    aliPayParse.setSerialNumber(sdf[0]);//保存流水号
                    aliPayParse.setPayeeCountNumber(sdf[1]);//收款方账号
                    aliPayParse.setPayeeName(sdf[2]);//收款方姓名
                    aliPayParse.setPayAmount(sdf[3]);//付款金额
                    aliPayParse.setSfFlag(sdf[4]);//成功或者失败标示，成功S，失败F
                    aliPayParse.setSfReason(sdf[5]);//成功或者失败原因，成功S，失败F
                    aliPayParse.setAlipaySnumber(sdf[6]);//支付宝内部流水号
                    aliPayParse.setFinshTime(sdf[7]);//完成时间
                    alipayparselist.add(j, aliPayParse);
                }
            }
        }
        return alipayparselist;
    }
}
