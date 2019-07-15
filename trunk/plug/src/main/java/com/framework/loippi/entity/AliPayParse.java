package com.framework.loippi.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 
 * @author ihui
 * 支付公用参数
 */
@Data
@ToString
public class AliPayParse implements Serializable {
	 /**
	   * 流水号
	   */
	  private String serialNumber;
	  /**
	   * 收款方账号
	   */
	  private String payeeCountNumber;
	  /**
	   * 收款方姓名
	   */
	  private String payeeName;
	  /**
	   * 付款金额
	   */
	  private String payAmount;
	  /**
	   * 成功或者失败标示，成功S，失败F
	   */
	  private String sfFlag;
	  /**
	   * 成功或者失败原因，成功S，失败F
	   */
	  private String sfReason;
	  /**
	   * 支付宝内部流水号
	   */
	  private String alipaySnumber;
	  /**
	   * 完成时间
	   */
	  private String finshTime;
  
  
}
