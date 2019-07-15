package com.framework.loippi.dto;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.vo.order.OrderStatisticsVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单商品分物流--返回订单统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsSysDto {
    //用于分组时间
    private String orderDate;

    //待支付订单数量
    private String noPatmentNum;
    //待发货订单数量 STATE_UNFILLED
    private String stateUnfilledNum;
    //待收货订单数量 NOT_RECEIVING
    private String notReceivingNum;
    //已完成订单数量 STATE_FINISH
    private String stateFinishNum;
    //已完成订单数量 STATE_FINISH
    private String stateSaleNum;
    //已取消订单数量 CANCLE
    private String cancleNum;

     //订单总数量
     private Integer count;
    /**
     * 订单状态：0:已取消;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认;
     */
    private Integer orderState;

    private Double money;
    private String averageMoney;

    public static List <OrderStatisticsSysDto> buildList( List<OrderStatisticsVo> orderStatisticsVoList) {
        List <OrderStatisticsSysDto> orderStatisticsSysDtoList=new ArrayList<>();
          String orderDate="";
        for (int i = 0,length=orderStatisticsVoList.size(); i <length ; i++) {
            int count=0;
            OrderStatisticsSysDto orderStatisticsSysDto=new OrderStatisticsSysDto();
            OrderStatisticsVo orderStatisticsVo=orderStatisticsVoList.get(i);
            String  date=orderStatisticsVo.getOrderDate();
            if (orderDate.equals(date)){
                continue;
            }else{
                    orderStatisticsSysDto.setOrderDate(date);
                    orderDate=date;
            }

            for (OrderStatisticsVo item:orderStatisticsVoList) {
                if (item.getOrderDate().equals(orderStatisticsVo.getOrderDate())){
                    count+=Integer.parseInt(item.getNum());
                    switch (item.getOrderState()) {
                        case 0:
                            orderStatisticsSysDto.setCancleNum(item.getNum());
                            break;
                        case 10:
                            orderStatisticsSysDto.setNoPatmentNum(item.getNum());
                            break;
                        case 20:
                            orderStatisticsSysDto.setStateUnfilledNum(item.getNum());
                            break;
                        case 30:
                            orderStatisticsSysDto.setNotReceivingNum(item.getNum());
                            break;
                        case 40:
                            orderStatisticsSysDto.setStateFinishNum(item.getNum());
                            break;
                        case 70:
                            orderStatisticsSysDto.setStateSaleNum(item.getNum());
                            break;

                    }
                }
            }
            orderStatisticsSysDto.setCount(count);
            orderStatisticsSysDtoList.add(orderStatisticsSysDto);
        }
        return orderStatisticsSysDtoList;
    }
    public static List <OrderStatisticsSysDto> buildList2( List<OrderStatisticsVo> orderStatisticsVoList,Long day) {
        List <OrderStatisticsSysDto> orderStatisticsSysDtoList=new ArrayList<>();
        String orderDate="";
        DecimalFormat df   = new DecimalFormat("######0.00");
        for (int i = 0,length=orderStatisticsVoList.size(); i <length ; i++) {
            double averageMoney=0;
            double money=0;
            OrderStatisticsSysDto orderStatisticsSysDto=new OrderStatisticsSysDto();
            OrderStatisticsVo orderStatisticsVo=orderStatisticsVoList.get(i);
            if (orderDate.equals(orderStatisticsVo.getOrderDate())){
                continue;
            }else{
                orderStatisticsSysDto.setOrderDate(orderStatisticsVo.getOrderDate());
                orderDate=orderStatisticsVo.getOrderDate();
            }
            orderStatisticsSysDto.setOrderDate(orderStatisticsVo.getOrderDate());
            for (OrderStatisticsVo item:orderStatisticsVoList) {
                if (item.getOrderDate().equals(orderStatisticsVo.getOrderDate())){
                    if(item.getOrderState()==10 || item.getOrderState()==0){
                        continue;
                    }
                    money+=Double.parseDouble(Optional.ofNullable(item.getOrderPayPrice()).orElse("0"))+Double.parseDouble(Optional.ofNullable(item.getPointRmbNum()).orElse("0"));
                }
            }
            if (money!=0D){
                averageMoney=money/day;
            }
            orderStatisticsSysDto.setMoney(money);
            orderStatisticsSysDto.setAverageMoney(df.format(averageMoney));
            orderStatisticsSysDtoList.add(orderStatisticsSysDto);
        }
        return orderStatisticsSysDtoList;
    }
}
