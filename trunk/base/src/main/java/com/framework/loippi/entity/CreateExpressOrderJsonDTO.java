package com.framework.loippi.entity;

import lombok.Data;

/**
 * 下单请求体
 */
@Data
public class CreateExpressOrderJsonDTO {

    private String orderId;
    // 快件类型  2  —————— T6 顺丰标快（陆运）
    private int expressType;
    // 是否下 call (1-下柯 0-不下柯)   0
    private int isDoCall;
    // 月结卡号
    private String monthlyCust;
    // 	包裹数量（如果大于 1 会生成子单号）
    private int packageNumber;
    // 	付款方式（1-寄付现结、2-寄付月结、3-到付现结、4-第三方付；注意：寄付月结类型必须传monthlyCust 月结卡号）
    private int payMethod;
    // 子母件类型,字母件类型 1：一件一票，2：子母件
    private int bspType;
    // 	1 -标准 xml 返回  其他-默认只返回运单号和子单号(只针对立即下单情况)
    private String returnType;
    // 	是否立即下单（1-是 其他-否）
    private String doOrderNow;
    // 	拖寄物内容
    private String productName;
    // 	备注（发货详情）
    private String remark;
    // 	保价金额（单位为分）
    private int insureAmount;

    /*寄件*/
    // 寄件公司名称
    private String jcompany;
    // 寄件联系电话
    private String jmobile;
    // 寄件所在省级行政区名称，必须是标准的省级行政区名称如：北京、广东省、广西壮族自治区等
    private String jprovince;
    // 寄件所在地级行政区名称，必须是标准的城市称谓 如：北京市、深圳市、大理白族自治州等
    private String jcity;
    // 寄件区县
    private String jcounty;
    // 寄件详细地址(包含省市区县)，若province/city 字段的值不传，此字段必须包
    //含省市信息，避免影响原寄地代码识别，如：广东省深圳市福田区新洲十一街万基商务大厦10楼；若需要生成电子运单，则为必填
    private String jaddress;
    // 寄件联系人
    private String jcontact;


    /*收件*/
    // 收件公司名称
    private String dcompany;
    // 收件联系电话
    private String dmobile;
    // 收件所在省级行政区名称，必须是标准的省级行政区名称如：北京、广东省、广西壮族自治区等
    private String dprovince;
    // 收件所在地级行政区名称，必须是标准的城市称谓 如：北京市、深圳市、大理白族自治州等
    private String dcity;
    // 收件所在地级行政区名称，必须是标准的城市称谓 如：北京市、深圳市、大理白族自治州等
    private String dcounty;
    // 收件详细地址(包含省市区县)，若province/city 字段的值不传，此字段必须包
    //含省市信息，避免影响原寄地代码识别，如：广东省深圳市福田区新洲十一街万基商务大厦10楼；若需要生成电子运单，则为必填
    private String daddress;
    // 收件联系人
    private String dcontact;
}
