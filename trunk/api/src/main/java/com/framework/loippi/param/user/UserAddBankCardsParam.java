package com.framework.loippi.param.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Param - 银行卡-新增
 *
 * @author Loippi team
 * @version 2.0
 * @description 银行卡-新增
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBankCardsParam {

    /**
     * 持卡人姓名
     */
    @NotEmpty
    private String accName;

    /**
     * 银行名称
     */
    @NotEmpty
    private String bankDetail;

    /**
     * 卡号
     */
    @NotEmpty
    private String accCode;

    /**
     * 手机号
     */
    @NotNull
    private String mobile;

    /**
     * 验证码
     */
    @NotNull
    private String code;
    /**
     * 身份证号码
     */
    @NotNull
    private String idCard;

}
