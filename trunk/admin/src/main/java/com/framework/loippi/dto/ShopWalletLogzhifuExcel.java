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
public class ShopWalletLogzhifuExcel {
    private String id;
    private String lgMemberId;
    private String lgType;
    private BigDecimal lgAddAmount;
    private BigDecimal lgRdeAmount;
    private String orderSn;
    private String outComeType;
    private String lgMemberName;
    private String createTime;
    private String lgDesc;
    }



