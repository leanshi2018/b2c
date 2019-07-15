package com.framework.loippi.result.sys;

import com.framework.loippi.utils.JacksonUtil;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

import lombok.Data;
import lombok.ToString;

/**
 * 订单设置vo
 */
@Data
@ToString
public class OrderSettingView {

    public static final String ORDER_SETTING_KEY = "orderSetting_key";

    /**
     * 根据运费模板
     */
    public static final int FREIGHT_TYPE_TRANSPORT_TEMPLATE = 1;

    /**
     * 满xx元免邮
     */
    public static final int FREIGHT_TYPE_OVER_FREE_FREIGHT = 2;

    /**
     * 固定xx元
     */
    public static final int FREIGHT_TYPE_FIXED_PRICE = 3;


    /***
     * 支付成功发短信通知
     */
    private Integer isSmsNotifyWhenSuccessPayment = 0;

    /***
     * 发货发送站内信
     */
    private Integer isSendPrivateMessageWhenDeliver = 0;

    /***
     * 是否开启自动审核订单
     */
    private Integer isOrderAutoAudit = 0;

    /***
     * 是否开启重复下单提示
     */
    private Integer isRemindHadRepeatOrder = 0;

    /***
     * 购物车默认选中
     */
    private Integer isSelectAllCart = 0;

    /***
     * 售后服务
     */
    private Integer isProvideAfterSale = 0;

    /***
     * 是否统计商品访问量
     */
    private Integer isStatisticsGoodsAccessed = 0;

    /***
     * 开启秒杀订单未付款自动取消订单
     */
    private Integer isAutoCancelNonPaymentSeckillOrder = 0;

    /***
     * 开启团购订单未成团自动取消订单
     */
    private Integer isAutoCancelNonGroupOrder = 0;

    /***
     * 开启订单自动确认收货
     */
    private Integer isAutoReceiveConfirm = 0;

    /***
     * 开启订单自动完结
     */
    private Integer isAutoFinishedOrder = 0;

    /***
     * 跨境贸易订单提交控制
     */
    private Integer isLimitCrossBorderOrderAmount = 0;

    /***
     * 订单运费设置 1:根据配送方式 2:满xxx免运费 3:默认xx元
     */
    private Integer freightType = 0;

    /***
     * xxx分钟未付款秒杀订单自动失败
     */
    private Integer minuteOfAutoCancelNonPaymentSeckillOrder = 0;

    /***
     * xxx分钟未付款团购订单自动失败
     */
    private Integer minuteOfAutoCancelNonPaymentGroupOrder = 0;

    /***
     * 发货后xxx天自动完结
     */
    private Integer dayOfAutoFinishedOrderStartFromDeliver = 0;

    /***
     * 确认收货后xxx天自动完结
     */
    private Integer dayOfFinishedOrderStartFromReceiveGoods = 0;

    /***
     * 订单金额最高xxx元
     */
    private Double maxOrderAmount;

    /***
     * 满xxx元免运费
     */
    private Double overFreeShipping;

    /***
     * 运费xxx元
     */
    private Double freight;

    /***
     * 服务内容说明
     */
    private String serviceContent;

    /**
     * 根据设置获取运费信息 如：满288包邮
     *
     * @param setting 本类josn
     */
    public static String getFreightInfo(String setting) {
        try {
            if (StringUtils.isNotBlank(setting) && !"null".equals(setting)) {
                Map<String, Object> settingMap = JacksonUtil.convertMap(setting);
                Object freightType = settingMap.get("freightType");
                Object overFreeShipping = settingMap.get("overFreeShipping");
                // 1:根据配送方式 2:满xxx免运费 3:默认xx元
                if (freightType != null) {
                    if ((Integer) freightType == FREIGHT_TYPE_OVER_FREE_FREIGHT && overFreeShipping != null) {
                        return "满" + overFreeShipping + "元包邮";
                    } else if ((Integer) freightType == FREIGHT_TYPE_FIXED_PRICE && settingMap.get("freight") != null) {
                        return "默认" + settingMap.get("freight") + "元";
                    } else if ((Integer) freightType == FREIGHT_TYPE_TRANSPORT_TEMPLATE) {
                        return "根据配送方式";
                    }
                }
            }
        } catch (Exception e) {
           // Log.error("", e);
        }

        return "";
    }
}