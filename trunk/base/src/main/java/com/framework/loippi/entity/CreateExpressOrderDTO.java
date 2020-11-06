package com.framework.loippi.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 顺丰下单成功返回的信息封装的实体
 */
@Data
public class CreateExpressOrderDTO implements Cloneable{

    /** 运单号 */
    private String mailNo;
    /** 原寄递地代码 */
    private String zipCode;
    /** 目的地的代码 */
    private String destCode;
    /** 备注 */
    private String mainRemark;
    /** 寄件人姓名 */
    private String deliverName;
    /** 寄件人电话*/
    private String deliverMobile;
    /** 寄件人省份 */
    private String deliverProvince;
    /** 寄件人城市 */
    private String deliverCity;
    /** 寄件人区县 */
    private String deliverCounty;
    /** 寄件人地址 */
    private String deliverAddress;
    /** 寄件人公司 */
    private String deliverCompany;
    /** 收件人姓名 */
    private String consignerName;
    /** 收件人电话 */
    private String consignerMobile;
    /** 收件人省份 */
    private String consignerProvince;
    /** 收件人城市 */
    private String consignerCity;
    /** 收件人区县 */
    private String consignerCounty;
    /** 收件人地址 */
    private String consignerAddress;
    /** 月结卡号 */
    private String custId;
    /** 快递类别 1：顺丰标快 */
    private String expressType;
    /** 包裹数量 大于1则会生成子单号*/
    private Integer parcelQuantity;
    /** 丰密运单相关设置 */
    private List<RlsInfoDto> rlsInfoDtoList;

    @Override
    public Object clone() {
        CreateExpressOrderDTO obj = null;
        try {
            obj = (CreateExpressOrderDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        obj.rlsInfoDtoList = new ArrayList<>(1);
        obj.rlsInfoDtoList.add((RlsInfoDto) rlsInfoDtoList.get(0).clone());
        return obj;
    }
}
