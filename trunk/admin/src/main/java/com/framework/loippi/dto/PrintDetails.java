package com.framework.loippi.dto;

import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.vo.order.ShopOrderVo;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.ToString;

/**
 * Created by longbh on 2017/7/22.
 */
@Data
@ToString
public class PrintDetails {

    /** 发货商品 */
    private List<ShopOrderLogistics> shopOrderLogisticsList;
    /** 订单编号 */
    private String orderSn;
    /** 买家姓名 */
    private String buyerName;
    /** 收货人*/
    private String trueName;
    /** 订单生成时间 */
    private java.util.Date createTime;
    /** 打印时间 */
    private java.util.Date printTime=new Date();
    /** 订单留言 */
    private String orderMessage;
    /** 优惠总金额 */
    private java.math.BigDecimal discount;
    /** 订单金额 */
    private java.math.BigDecimal money;

    public static PrintDetails build(List<ShopOrderLogistics> shopOrderLogisticsList,ShopOrderVo orderVo) {
        PrintDetails printDetails=new PrintDetails();
         if (shopOrderLogisticsList.size()>0){
             printDetails.setShopOrderLogisticsList(shopOrderLogisticsList);
         }
        printDetails.setOrderSn(orderVo.getOrderSn());
        printDetails.setBuyerName(orderVo.getBuyerName());
        printDetails.setTrueName(orderVo.getAddress().getTrueName());
        printDetails.setCreateTime(orderVo.getCreateTime());
        printDetails.setOrderMessage(orderVo.getOrderMessage());
        printDetails.setDiscount(orderVo.getDiscount());
        printDetails.setMoney(orderVo.getOrderAmount().add(orderVo.getPointRmbNum()));
        return printDetails;
    }


}
