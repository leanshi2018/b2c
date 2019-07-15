package com.framework.loippi.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by longbh on 2017/12/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopWalletIncomeExcel {
    private String id;
    private String paymentSn;
    private String buyerName;
    private BigDecimal paymentAmount;
    private String paymentCode;
    private String zhifuzhanghao;

    private String createTime;
    private String remark;

    }



