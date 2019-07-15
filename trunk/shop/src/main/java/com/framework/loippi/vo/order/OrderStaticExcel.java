package com.framework.loippi.vo.order;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class OrderStaticExcel implements Serializable{
	/**
	 * 订单编号，商城内部使用
	 */
	private String orderSn;

    /**
     *买家名称
     */
    private String membername;
    
    /**
     *订单金额
     */
    private String ordertotalprice;

    /**
     *支付金额
     */
    private String orderamount;

    /**
     *支付方式
     */
    private String paymentcode;
    /**
     *订单状态
     */
    private String orderstate;
    /**
     *生成时间
     */
    private String createTime;
   
    
}
