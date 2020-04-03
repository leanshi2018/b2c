//package com.framework.loippi.job;
//
//import static com.framework.loippi.mybatis.ext.CurdTemplate.log;
//
//import com.framework.loippi.consts.ActivityGroupOrderType;
//import com.framework.loippi.consts.Constants;
//import com.framework.loippi.consts.PaymentTallyState;
//import com.framework.loippi.entity.activity.ShopActivityGrouponOrder;
//import com.framework.loippi.service.activity.ShopActivityGrouponOrderService;
//import com.framework.loippi.service.activity.ShopActivityGoodsService;
//import com.framework.loippi.service.activity.ShopActivityRemindMsgService;
//import com.framework.loippi.service.order.ShopOrderService;
//import com.framework.loippi.util.unionpay.pc.gwj.util.DateUtil;
//import java.util.Date;
//import java.util.List;
//import javax.annotation.Resource;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
///**
// * 功能：根据设置定时处理活动
// * 类名：
// * 日期：
// * 作者：
// * 详细说明：
// * 修改备注:
// */
//@Service
//@EnableScheduling
//@Lazy(false)
//public class ShopActivityJob {
//
//    @Resource
//    private ShopActivityRemindMsgService remindMsgService;
//
//    @Resource
//    private ShopActivityGrouponOrderService shopActivityGrouponOrderService;
//
//    @Resource
//    private ShopOrderService orderService;
//
//    @Resource
//    private ShopActivityGoodsService  objectService;
//
//
//    /**
//     * 秒杀活动消息提醒
//     */
//    @Scheduled(cron = "0 */5 * * * ?")  //每5分钟执行一次
//    public void remindMsgService() {
//        System.out.println("#################################################################");
//        System.out.println("#####################  开始执行-发送秒杀提醒信息 ###################");
//        System.out.println("#################################################################");
//        remindMsgService.remindJob();
//        System.out.println("#################################################################");
//        System.out.println("#####################  执行结束-发送秒杀提醒信息 ###################");
//        System.out.println("#################################################################");
//    }
//
//    /**
//     * 处理取消团购订单
//     */
//    @Scheduled(cron = "0 */3 * * * ?")  //每3分钟执行一次
//    private void  groupActivityJob(){
//        log.info("###############################################################################################################");
//        log.info("#####################  开始执行-识别团购订单失效情况，进行自动退款"+ DateUtil.getDate(new Date(),"yyyy-MM-dd HH:mm:ss")+" ###################");
//        log.info("################################################################################################################");
//
//        //获取需要取消订单到团购记录
//        List<ShopActivityGrouponOrder> resultList =  shopActivityGrouponOrderService.getGroupActivityOrder();
//        log.info("团购失败、正在团购的活动进行识别情况,本次共有"+resultList.size()+"个参团订单要进行识别处理。");
//        if(resultList.size() ==0){
//            return;
//        }
//
//        for(ShopActivityGrouponOrder item:resultList){
//            log.info("开始自动退款团购订单，订单号（orderId）："+item.getOrderId());
//            try{
//
//                //订单退款
//                orderService.updateCancelActivityOrder(item.getOrderId(), Constants.OPERATOR_TIME_TASK,item.getMemberId(),
//                        PaymentTallyState.PAYMENTTALLY_TREM_PC);
//
//                //更新团购退款状态
//                item.setOrderType(ActivityGroupOrderType.ACTIVITY_GROUP_ORDER_TYPE_FAIL_REFUND);
//                shopActivityGrouponOrderService.update(item);
//
//                //更新活动库存
//                Integer  goodsNum   = orderService.getGroupActivityGoodsBuyNum(item.getOrderId());
//                objectService.updateStock(item.getActivityId(),goodsNum);
//
//                log.info("完成自动退款团购订单，订单号（orderId）："+item.getOrderId());
//            }catch (Exception e){
//                log.info("团购订单自动退款失败,需要手工处理订单，orderId:"+item.getOrderId());
//                item.setOrderType(ActivityGroupOrderType.ACTIVITY_GROUP_ORDER_TYPE_EXCEPTION_STAY);//退款异常，待手工处理
//                shopActivityGrouponOrderService.update(item);
//                e.printStackTrace();
//                continue;
//            }
//        }
//
//        log.info("################################################################################################################");
//        log.info("#####################  执行结束-识别团购订单失效情况，进行自动退款"+ DateUtil.getDate(new Date(),"yyyy-MM-dd HH:mm:ss")+"  ###################");
//        log.info("################################################################################################################");
//    }
//
//
//
//}