package com.framework.loippi.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 支付宝批量退货所需实体
 *
 * @项目名称：leimingtech-entity
 * @类名称
 * @类描述：
 * @修改备注：
 */
@Data
@ToString
public class AliPayBathBack implements Serializable {
    /**
     * 付款当天日期 格式：年[4位]月[2位]日[2位]，如：20100801
     */
    private String paydate;
    /**
     * 付款批次号 格式：当天日期[8位]+序列号[3至16位]，如：201008010000001
     */
    private String BatchCountNum;
    /**
     * 付款总金额 即参数detail_data的值中所有金额的总和
     */
    private String batchFee;
    /**
     * 退款笔数 最大支持1000笔
     */
    private String BatchNum;
    /**
     * 付款的详细数据，最多支持1000笔。 格式为：流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1|流水号2^收款方账号2^收款账号姓名2^付款金额2^备注说明2。 每条记录以“|”间隔。
     * 如：0315006^testture0002@126.com^常炜买家^20.00^hello
     */
    private String detailData;

    /**
     * 付款成功后回调路径
     */
    private String notifyurl;
}
