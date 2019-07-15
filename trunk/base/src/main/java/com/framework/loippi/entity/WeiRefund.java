package com.framework.loippi.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;


/**
 * 
 * 微信退款所需实体
 * 
 * @项目名称：leimingtech-entity
 * @类名称：Area
 * @类描述：
 * @修改备注：
 * @version
 */
@Data
@ToString
public class WeiRefund implements Serializable {
  /**
   * 退款单号，也叫微信支付单号
   */
  private String outrefundno;
  /**
   * 订单号
   */
  private String outtradeno;
  /**
   * 总金额
   */
  private Integer totalfee;
  /**
   * 退款金额
   */
  private Integer refundfee;
  
}
