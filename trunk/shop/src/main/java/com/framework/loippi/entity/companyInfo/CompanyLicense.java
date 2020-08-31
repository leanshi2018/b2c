package com.framework.loippi.entity.companyInfo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zc
 * 2020-08-28
 * 商户提现公司信息记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_company_license")
public class CompanyLicense implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "id")
    private Long id;

    /**
     * 会员编号
     */
    @Column(name = "mm_code")
    private String mCode;

    /**
     * 公司名称
     */
    @Column(name = "company_name")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @Column(name = "credit_code")
    private String creditCode;

    /**
     * 公司注册地址
     */
    @Column(name = "company_add")
    private String companyAdd;

    /**
     * 公司电话
     */
    @Column(name = "company_phone")
    private String companyPhone;

    /**
     * 开户银行支行
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 对公银行账号
     */
    @Column(name = "bank_id")
    private String bankId;

    /**
     * 营业执照
     */
    @Column(name = "business_license")
    private String businessLicense;

    /**
     * 开户许可证照片
     */
    @Column(name = "permission_license")
    private String permissionLicense;

    /**
     * 记录状态 1:待认证 2：已认证 -1：已驳回
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 提交时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private Date auditTime;

    /**
     * 审核人编号
     */
    @Column(name = "audit_code")
    private String auditCode;

    /**
     * 审核备注
     */
    @Column(name = "audit_remark")
    private String auditRemark;

    //查询字段
    private Integer noStatus;
}
