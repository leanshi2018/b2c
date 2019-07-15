//package com.framework.loippi.job;
//
//import com.framework.loippi.consts.Constants;
//import com.framework.loippi.consts.MemberLevelLogType;
//import com.framework.loippi.consts.PaymentTallyState;
//import com.framework.loippi.entity.ShopLevelWay;
//import com.framework.loippi.entity.common.ShopTask;
//import com.framework.loippi.entity.common.ShopTaskFinish;
//import com.framework.loippi.entity.order.ShopOrder;
//import com.framework.loippi.service.LevelLogService;
//import com.framework.loippi.service.ShopLevelWayService;
//import com.framework.loippi.service.TwiterIdService;
//import com.framework.loippi.service.common.ShopTaskFinishService;
//import com.framework.loippi.service.common.ShopTaskService;
//import com.framework.loippi.support.Page;
//import com.framework.loippi.support.Pageable;
//import com.framework.loippi.utils.DateUtils;
//import com.framework.loippi.utils.StringUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// *
// * 任务记录表
// *
// */
//@Service
//@EnableScheduling
//@Lazy(false)
//public class ShopTaskJob {
//
//    private static final Logger log = LoggerFactory.getLogger(ShopTaskJob.class);
//
//    @Resource
//    private ShopTaskService  taskService;
//
//    @Resource
//    private ShopTaskFinishService  taskFinishService;
//
//    @Resource
//    private ShopLevelWayService shopLevelWayService;
//
//    @Resource
//    private LevelLogService levelLogService;
//
//    @Resource
//    private TwiterIdService twiterIdService;
//
//
//    /**
//     * 购买商品，获取相关的经验值
//     */
//    @Scheduled(cron = "0 */3 * * * ?")
//    public void shopOrderExperience() {
//
//        log.info("#################################################################");
//        log.info("#####################  开始执行-核算订单结算获取经验值 ###################");
//        log.info("#################################################################");
//
//        //获取消费的订单经验值计算规则
//        ShopLevelWay levelWay = shopLevelWayService.find(5l);
//        if(levelWay == null || levelWay.getExperience()==null||levelWay.getExperience()==0
//                ||levelWay.getMostExperience()==null||levelWay.getMostExperience()==0){
//            log.info("#################################################################");
//            log.info("##################### 购买1件商品的计算规则缺失！ 结束-核算订单结算获取经验值 ###################");
//            log.info("#################################################################");
//            return;
//        }
//        //当天封顶经验值
//        Integer goodsTopExper = levelWay.getMostExperience();
//        //每个商品对应获取的经验值
//        Integer goodsExper = levelWay.getExperience();
//
//        int pageNo = 1;
//        int pageSize = 50;
//
//        Pageable page  = new Pageable();
//        page.setPageSize(pageSize);
//        page.setPageNumber(pageNo);
//        ShopTask shopTask = new ShopTask();
//        shopTask.setTaskType(MemberLevelLogType.LOG_TYPE_LIVE_PURCHASE);
//        page.setParameter(shopTask);
//
//        do {
//
//            page.setPageSize(pageSize);
//            page.setPageNumber(pageNo);
//            Page<ShopTask> tasksList = taskService.findByPage(page);
//
//            if (tasksList != null && tasksList.getContent().size() > 0) {
//
//                List<Long> memberIds = new ArrayList<>();
//                tasksList.getContent().forEach(taks -> {
//                    if(!StringUtil.isEmpty(taks.getData3())){
//                        memberIds.add(Long.parseLong(taks.getData3()));
//                    }
//                });
//
//                //当天经验值
//                Map<Long,Integer> experMap = levelLogService.getMemberExperince(memberIds, MemberLevelLogType.LOG_TYPE_LIVE_PURCHASE);
//
//                for(ShopTask taks : tasksList.getContent()){
//
//                    if(StringUtil.isEmpty(taks.getData3())||taks.getData1()== null){
//                        log.info("无法获取购买者id信息，或购买商品数为空！orderInfo:"+taks.getData2());
//                        continue;
//                    }
//
//                    Integer experNum = experMap.get(Long.parseLong(taks.getData3()));
//                    if(experNum!=null&&experNum>=goodsTopExper){//已超过封顶经验值
//                        log.info("已超过封顶经验值！orderInfo:"+taks.getData2());
//                        taskService.delete(taks.getId());
//                        continue;
//                    }
//
//                    //获得的购买商品经验值
//                    Integer goodsExperNum = taks.getData1().intValue()*goodsExper;
//                    if(goodsExperNum.intValue()>goodsTopExper.intValue()){
//                        goodsExperNum = goodsTopExper;
//                    }
//                    try{
//                        levelLogService.saveExperience(null,Long.parseLong(taks.getData3()),MemberLevelLogType.LOG_TYPE_LIVE_PURCHASE,goodsExperNum);
//                        ShopTaskFinish taskFinish = new ShopTaskFinish();
//                        taskFinish.setId(twiterIdService.getTwiterId());
//                        taskFinish.setCreateTime(new Date());
//                        taskFinish.setData1(taks.getData1());
//                        taskFinish.setData2(taks.getData2());
//                        taskFinish.setData3(taks.getData3());
//                        taskFinish.setTaskType(taks.getTaskType());
//                        taskFinish.setFinishTime(new Date());
//                        taskFinishService.save(taskFinish);
//                        taskService.delete(taks.getId());
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//
//            }
//            pageNo++;
//            pageSize = tasksList.getContent().size();
//        } while (pageSize == 50);
//
//        log.info("#################################################################");
//        log.info("#####################  结束-核算订单结算获取经验值 ###################");
//        log.info("#################################################################");
//
//    }
//
//
//    /**
//     * 主播推荐商品购买，获取经验值情况
//     */
//    @Scheduled(cron = "0 */4 * * * ?")
//    public void shopAnchorExperience() {
//
//        log.info("#################################################################");
//        log.info("#####################  开始执行-核算主播推荐商品购买，获取经验值情况 ###################");
//        log.info("#################################################################");
//
//        //获取消费的订单经验值计算规则
//        ShopLevelWay levelWay = shopLevelWayService.find(5l);
//        if(levelWay == null || levelWay.getExperience()==null||levelWay.getExperience()==0
//                ||levelWay.getMostExperience()==null||levelWay.getMostExperience()==0){
//            log.info("#################################################################");
//            log.info("##################### 直播中购买推荐商品1件的计算规则缺失！ 结束-主播推荐商品购买，获取经验值情况 ###################");
//            log.info("#################################################################");
//            return;
//        }
//        //当天封顶经验值
//        Integer goodsTopExper = levelWay.getMostExperience();
//        //每个商品对应获取的经验值
//        Integer goodsExper = levelWay.getExperience();
//
//        int pageNo = 1;
//        int pageSize = 50;
//
//        Pageable page  = new Pageable();
//        page.setPageSize(pageSize);
//        page.setPageNumber(pageNo);
//        ShopTask shopTask = new ShopTask();
//        shopTask.setTaskType(MemberLevelLogType.LOG_TYPE_LIVE_BROADCAST_PURCHASE);
//        page.setParameter(shopTask);
//
//        do {
//
//            page.setPageSize(pageSize);
//            page.setPageNumber(pageNo);
//            Page<ShopTask> tasksList = taskService.findByPage(page);
//
//            if (tasksList != null && tasksList.getContent().size() > 0) {
//
//                List<Long> anchorIds = new ArrayList<>();
//                tasksList.getContent().forEach(taks -> {
//                    if(!StringUtil.isEmpty(taks.getData2())){
//                        anchorIds.add(Long.parseLong(taks.getData2()));
//                    }
//                });
//
//                //当天经验值
//                Map<Long,Integer> experMap = levelLogService.getMemberExperince(anchorIds, MemberLevelLogType.LOG_TYPE_LIVE_BROADCAST_PURCHASE);
//
//                for(ShopTask taks : tasksList.getContent()){
//
//                    if(StringUtil.isEmpty(taks.getData2())||taks.getData1()== null){
//                        log.info("无法获取购买者id信息，或购买商品数为空！orderInfo:"+taks.getData2());
//                        continue;
//                    }
//
//                    Integer experNum = experMap.get(taks.getData2());
//                    if(experNum!=null&&experNum>=goodsTopExper){//已超过封顶经验值
//                        log.info("已超过封顶经验值！");
//                        taskService.delete(taks.getId());
//                        continue;
//                    }
//
//                    //获得的购买商品经验值
//                    Integer goodsExperNum = taks.getData1().intValue()*goodsExper;
//                    if(goodsExperNum.intValue()>goodsTopExper.intValue()){
//                        goodsExperNum = goodsTopExper;
//                    }
//                    try{
//                        levelLogService.saveExperience(null,Long.parseLong(taks.getData2()),MemberLevelLogType.LOG_TYPE_LIVE_BROADCAST_PURCHASE,goodsExperNum);
//                        ShopTaskFinish taskFinish = new ShopTaskFinish();
//                        taskFinish.setId(twiterIdService.getTwiterId());
//                        taskFinish.setCreateTime(new Date());
//                        taskFinish.setData1(taks.getData1());
//                        taskFinish.setData2(taks.getData2());
//                        taskFinish.setData3(taks.getData3());
//                        taskFinish.setTaskType(taks.getTaskType());
//                        taskFinish.setFinishTime(new Date());
//                        taskFinishService.save(taskFinish);
//                        taskService.delete(taks.getId());
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//
//            }
//            pageNo++;
//            pageSize = tasksList.getContent().size();
//        } while (pageSize == 50);
//
//        log.info("#################################################################");
//        log.info("#####################  结束-核算主播推荐商品购买，获取经验值情况 ###################");
//        log.info("#################################################################");
//
//    }
//
//
//}
