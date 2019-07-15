package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by longbh on 2017/3/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithDrawForm {

    /**
     * 提现金额
     */
    private Double amount;

    /**
     * 提现账户人名称
     */
    private String account;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 银行分类
     */
    private String brand;

    /**
     * 银行
     */
    private String bank;

    /**
     * 备注
     */
    private String remark;


    /**提现类型1为支付，2银行 */
    private String pdcType;

}
