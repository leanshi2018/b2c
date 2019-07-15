package com.framework.loippi.vo.order;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author ihui
 * 获取订单ppv 和 金额
 */
@Data
@ToString
public class OrderSumPpv implements Serializable {
  /**
   * pv总值
   */
  private BigDecimal totalPpv;
  /**
   * 金额总值
   */
  private BigDecimal totalmoney;

}
