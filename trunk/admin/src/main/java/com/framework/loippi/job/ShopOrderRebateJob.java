//package com.framework.loippi.job;
//
//import com.framework.loippi.entity.activity.ShopActivityRebateSetting;
//import com.framework.loippi.entity.activity.ShopActivityRecommendRebate;
//import com.framework.loippi.entity.activity.ShopOrderRebate;
//import com.framework.loippi.entity.order.ShopOrder;
//import com.framework.loippi.entity.order.ShopOrderGoods;
//import com.framework.loippi.entity.user.ShopMember;
//import com.framework.loippi.entity.walet.LgTypeEnum;
//import com.framework.loippi.entity.walet.ShopWalletLog;
//import com.framework.loippi.service.TUserSettingService;
//import com.framework.loippi.service.TwiterIdService;
//import com.framework.loippi.service.activity.ShopActivityRecommendRebateService;
//import com.framework.loippi.service.activity.ShopOrderRebateService;
//import com.framework.loippi.service.order.ShopOrderGoodsService;
//import com.framework.loippi.service.user.ShopMemberService;
//import com.framework.loippi.service.walet.ShopWalletLogService;
//import com.framework.loippi.util.JsonUtil;
//import com.framework.loippi.vo.activity.ShopOrderRebateVo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.*;
//
//import static com.framework.loippi.mybatis.ext.CurdTemplate.log;
//
///**
// * 功能： 订单商品计算 推荐返佣
// * 类名：
// * 日期：
// * 作者：
// * 详细说明：
// * 修改备注:
// */
//@Service
//@EnableScheduling
//@Lazy(false)
//public class ShopOrderRebateJob {
//
//    @Resource
//    private TwiterIdService twiterIdService;
//
//    @Autowired
//    private TUserSettingService tUserSettingService;
//
//    @Resource
//    private ShopOrderGoodsService orderGoodsService;
//
//    @Resource
//    private ShopMemberService memberService;
//
//    @Resource
//    private ShopActivityRecommendRebateService recommendRebateService;
//
//    @Resource
//    private ShopOrderRebateService orderRebateService;
//
//    @Resource
//    private ShopWalletLogService walletLogService;
//
//
//    /**
//     * 计算返佣
//     */
//    @Scheduled(cron = "0 */2 * * * ?")  //每1分钟执行一次
//    public void orderRebateService() {
//        log.info("#################################################################");
//        log.info("#####################  开始执行-返佣处理 ###################");
//        log.info("#################################################################");
//
//        //获取返佣的区间设置
//        String set = tUserSettingService.read("rebate_setting") + "";
//        ShopActivityRebateSetting rebateSetting = null;
//        if (set == null || "null".equals(set) || "".equals(set)) {
//            rebateSetting = new ShopActivityRebateSetting();
//        } else {
//            rebateSetting = JsonUtil.fromJson(set,ShopActivityRebateSetting.class);
//        }
//
//        //获取返佣列表
//        List<ShopOrderRebateVo> shopOrderRebates = recommendRebateService.findRebateOrder();
//        if(shopOrderRebates == null || shopOrderRebates.size()==0){
//            log.info("没有需要处理的返佣信息!");
//            log.info("#################################################################");
//            log.info("#####################  执行结束-识别订单返佣情况,进行自动存款,记录流水  ###################");
//            log.info("#################################################################");
//            return;
//        }
//
//        /**
//         * 会员映射关系
//         * 批量查找推荐人的会员信息
//         */
//        Map<Long,ShopMember> memberMap = new HashMap<>();
//        /**
//         * 被推荐人，与返佣信息映射关系
//         */
//        Map<Long,ShopActivityRecommendRebate> rebateMap = new HashMap<>();
//        /**
//         * 返佣订单商品记录集--批量插入用
//         */
//        List<ShopOrderRebate> isDealRebatedList = new ArrayList<>();
//        /**
//         * 返佣流水集--批量插入用
//         */
//        List<ShopWalletLog> walletLogList = new ArrayList<>();
//        /**
//         * 被推荐人id集
//         */
//        List<Long> rebateRecommendedIds = new ArrayList<>();
//        /**
//         * 订单商品id集
//         */
//        for(ShopOrderRebateVo rebate : shopOrderRebates){
//            rebateRecommendedIds.add(rebate.getRebateRecommendedid());
//        }
//
//        List<ShopMember> members = recommendRebateService.findMemberByRecommendedId(rebateRecommendedIds);
//        List<ShopActivityRecommendRebate> recommendRebates = recommendRebateService.findRecommended(rebateRecommendedIds);
//        dealShopMember(members,memberMap);
//        dealRecommendRebate(recommendRebates,rebateMap);
//
//        log.info("###########################   开始处理返佣  ##############################");
//        dealOrderGoods(shopOrderRebates,rebateSetting,memberMap,rebateMap,isDealRebatedList,walletLogList);
//        //end deal
//        /**
//         * 从memberMap中获取已处理推荐人list集--批量更新用
//         */
//        List<ShopMember> memberList = new ArrayList<>(memberMap.values());
//        /**
//         * 从rebateMap中获取已处理推荐返佣信息list集--批量更新用
//         */
//        List<ShopActivityRecommendRebate> rebateList = new ArrayList<>(rebateMap.values());
//        //插入更新
//        if(memberList.size() > 0){
//            memberService.updateBatch(memberList);
//            log.info("###########################   推荐人返佣金额更新完成  ##############################");
//        }
//        if(rebateList.size() > 0){
//            recommendRebateService.updateBatch(rebateList);
//            log.info("###########################   返佣信息更新完成  ##############################");
//        }
//        if(walletLogList.size() > 0){
//            walletLogService.insertBatch(walletLogList);
//            log.info("###########################   返佣钱包流水记录完成  ##############################");
//        }
//        if(isDealRebatedList.size() > 0){
//            orderRebateService.insertBatch(isDealRebatedList);
//            log.info("###########################   已处理返佣记录完成  ##############################");
//        }
//
//        log.info("#################################################################");
//        log.info("#####################  执行结束-识别订单返佣情况,进行自动存款,记录流水  ###################");
//        log.info("#################################################################");
//
//    }
//
//    /**
//     * 处理推荐人信息
//     * @param objects
//     * @param memberMap
//     */
//    private void dealShopMember(List<ShopMember> objects, Map<Long ,ShopMember> memberMap){
//        for(ShopMember member : objects){
//            memberMap.put(member.getId(),member);
//        }
//    }
//
//    /**
//     * 处理被推荐人信息
//     * @param objects
//     * @param rebateMap
//     */
//    private void dealRecommendRebate(List<ShopActivityRecommendRebate> objects, Map<Long ,ShopActivityRecommendRebate> rebateMap){
//        for(ShopActivityRecommendRebate rebate : objects){
//            rebateMap.put(rebate.getRecommended().getId(),rebate);
//        }
//    }
//
//    /**
//     * 处理订单
//     * @param shopOrderRebates
//     * @param memberMap
//     * @param rebateMap
//     * @param isDealRebatedList
//     * @param walletLogList
//     */
//    private void dealOrderGoods(List<ShopOrderRebateVo> shopOrderRebates,ShopActivityRebateSetting rebateSetting, Map<Long,ShopMember> memberMap,
//                                Map<Long,ShopActivityRecommendRebate> rebateMap, List<ShopOrderRebate> isDealRebatedList,
//                                List<ShopWalletLog> walletLogList){
//        log.info("#####################  本次处理返佣订单(已拆分订单) "+shopOrderRebates.size()+" 条 ###################");
//        for(ShopOrderRebateVo orderRebate : shopOrderRebates){
//            //返佣金额
//            BigDecimal price = rebateSetting.getRandomNumberInRange(rebateSetting.getReturnMoneyDown(),rebateSetting.getReturnMoneyUp());
//
//            //返佣信息--被推荐人
//            ShopActivityRecommendRebate recommendRebate = rebateMap.get(orderRebate.getRebateRecommendedid());
//            //根据被推荐人查找推荐人
//            ShopMember member = null;
//            if(recommendRebate != null){
//                member = memberMap.get(recommendRebate.getRecommend().getId());
//            }
//            //返佣订单商品记录
//            ShopOrderRebate isDealRebated = new ShopOrderRebate();
//            //返佣流水
//            ShopWalletLog walletLog = new ShopWalletLog();
//            if(member != null  && !"0".equals(price.toString())){
//                member.setAvailablePredeposit(member.getAvailablePredeposit().add(price));
//                //更新memberMap中的obj
//                memberMap.put(member.getId(),member);
//                BigDecimal rebateAmount = recommendRebate.getRebateAmount() !=null?recommendRebate.getRebateAmount():BigDecimal.ZERO;
//                recommendRebate.setRebateAmount(rebateAmount.add(price));
//                BigDecimal buyAmount = recommendRebate.getBuyAmount()!=null?recommendRebate.getBuyAmount():BigDecimal.ZERO;
//                recommendRebate.setBuyAmount(buyAmount.add(orderRebate.getOrderAmount()));
//                recommendRebate.setRebateDate(new Date());
//                //更新rebateMap中的obj
//                rebateMap.put(recommendRebate.getRecommended().getId(),recommendRebate);
//                walletLog.setId(twiterIdService.getTwiterId());
//                walletLog.setLgMemberId(member.getId());
//                walletLog.setLgMemberName(member.getMemberName());
//                walletLog.setOrderSn(orderRebate.getRebateOrderid().toString());
//                walletLog.setLgType(LgTypeEnum.RECOMMEND_REBATE.value);
//                walletLog.setLgDesc("推荐返佣");
//                walletLog.setLgRdeAmount(price);
//                walletLog.setLgAddAmount(new BigDecimal(0));
//                walletLog.setLgFreezeAmount(new BigDecimal(0));
//                walletLog.setLgAvAmount(new BigDecimal(0));
//                walletLog.setCreateTime(new Date());
//                //加入钱包流水集
//                walletLogList.add(walletLog);
//                isDealRebated.setRebateType(1);
//            }
//            isDealRebated.setRebateOrderid(orderRebate.getRebateOrderid());
//            isDealRebated.setRebateRecommendedid(orderRebate.getRebateRecommendedid());
//            isDealRebated.setRebateDate(new Date());
//            isDealRebated.setId(twiterIdService.getTwiterId());
//            //加入已处理集
//            isDealRebatedList.add(isDealRebated);
//        }
//        log.info("###########################   结束返佣处理  ##############################");
//    }
//
//}