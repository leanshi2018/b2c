package com.framework.loippi.param.companyWithdrawal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyWithdrawalParm {
    /**
     * 公司名称
     */
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;
    /**
     * 统一社会信用代码
     */
    @NotEmpty(message = "统一社会信用代码不能为空")
    private String creditCode;
    /**
     * 公司注册地址
     */
    @NotEmpty(message = "公司注册地址不能为空")
    private String companyAdd;
    /**
     * 公司电话
     */
    @NotEmpty(message = "公司电话不能为空")
    private String companyPhone;
    /**
     * 开户银行支行
     */
    @NotEmpty(message = "开户银行支行不能为空")
    private String bankName;
    /**
     * 对公银行账号
     */
    @NotEmpty(message = "对公银行账号不能为空")
    private String bankId;
    /**
     * 营业执照
     */
    @NotEmpty(message = "营业执照不能为空")
    private String businessLicense;
    /**
     * 开户许可证照片
     */
    @NotEmpty(message = "开户许可证照片不能为空")
    private String permissionLicense;
}
