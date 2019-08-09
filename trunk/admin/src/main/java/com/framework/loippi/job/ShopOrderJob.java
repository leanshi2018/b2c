package com.framework.loippi.job;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.dao.integration.RdMmIntegralRuleDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.validator.DateUtils;

/**   暂时屏蔽
 * 功能：根据设置定时更新订单
 * 类名：ShopOrderJob
 * 日期：2019/01/17  18:59
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Service
@EnableScheduling
@Lazy(false)
public class ShopOrderJob {

    @Resource
    private ShopOrderService orderService;
    @Resource
    private RetailProfitDao retailProfitDao;
    @Resource
    private RdMmRelationDao rdMmRelationDao;
    @Resource
    private RdMmAccountInfoDao rdMmAccountInfoDao;
    @Resource
    private RdMmAccountLogDao rdMmAccountLogDao;
    @Resource
    private RdMmIntegralRuleDao rdMmIntegralRuleDao;
    @Resource
    private RdMmBasicInfoDao rdMmBasicInfoDao;
    @Resource
    private RdSysPeriodDao rdSysPeriodDao;

    private static final Logger log = LoggerFactory.getLogger(ShopOrderJob.class);
//

    /**
     * 订单12小时自动关闭取消功能
     */
    @Scheduled(cron = "0 */30 * * * ?")  //每30分钟执行一次
    public void cancelTimeOutPaymentOrder() {
        log.info("#################################################################");
        log.info("#####################  开始执行-订单24小时取消 ###################");
        log.info("#################################################################");
        int pageNo = 1;
        int pageSize = 500;
        Pageable pager=new Pageable();

        long limit = 2 * 60 * 60 * 1000;
        do {
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNo);
            pager.setParameter(Paramap.create().put("orderState", 10).put("lockState", "0").put("servenDay", DateUtils.addDays(new Date(), -1)));
            List<ShopOrder> orderList = orderService.findByPage(pager).getContent();
            if (orderList != null && orderList.size() > 0) {
                orderList.forEach(order -> {
                    log.info("订单[{}]创建时间[{}], 超过[{}]秒未支付, 自动取消");
                    try {
                        orderService.updateCancelOrder(order.getId(), Constants.OPERATOR_TIME_TASK, order.getBuyerId(), PaymentTallyState.PAYMENTTALLY_TREM_MB,"系统定时取消订单","");
                    }catch (Exception e){
                        System.err.println(e);
                        e.printStackTrace();
                    }

                });
            }
            pageNo++;
            pageSize = orderList.size();
        } while (pageSize == 500);
    }

    //@Scheduled(cron = "0/5 * * * * ? ")  //每5秒执行一次
    @Scheduled(cron = "0 15 * * * ? ")  //每隔一小时执行一次 每小时15分执行定时任务
    public void grant(){
        System.out.println("###############################执行定时任务#####################################");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expectTime = format.format(new Date());
        List<RetailProfit> list=retailProfitDao.findTimeMature(expectTime);
        System.out.println(list.size());
        if(list!=null&&list.size()>0){
            for (RetailProfit retailProfit : list) {
                //根据retailProfit对应记录进行零售利润发放
                List<RdMmRelation> rdMmRelations = rdMmRelationDao.findByParams(Paramap.create().put("mmCode",retailProfit.getBuyerId()));
                if(rdMmRelations!=null&&rdMmRelations.size()>0){
                    RdMmRelation rdMmRelation = rdMmRelations.get(0);
                    List<RdMmRelation> rdMmRelations1 = rdMmRelationDao.findByParams(Paramap.create().put("mmCode", rdMmRelation.getSponsorCode()));
                    if(rdMmRelations1!=null&&rdMmRelations1.size()>0){
                        RdMmRelation rdMmRelation1 = rdMmRelations1.get(0);
                        if(rdMmRelation1.getRank()==0){//推荐人是普通会员的话，不发奖励，将奖励记录状态修改为延迟发放
                            retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                            retailProfit.setState(2);
                            retailProfitDao.update(retailProfit);
                        }else {//推荐人是vip及以上级别，发放
                            List<RdMmAccountInfo> accountInfos = rdMmAccountInfoDao.findByParams(Paramap.create().put("mmCode",rdMmRelation1.getMmCode()));
                            if(accountInfos!=null&&accountInfos.size()>0){
                                RdMmAccountInfo rdMmAccountInfo = accountInfos.get(0);
                                List<RdMmIntegralRule> all = rdMmIntegralRuleDao.findAll();
                                RdMmIntegralRule rdMmIntegralRule = all.get(0);
                                //生成积分账户修改日志
                                RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                rdMmAccountLog.setMmCode(rdMmRelation1.getMmCode());
                                List<RdMmBasicInfo> basicInfos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode",rdMmRelation1.getMmCode()));
                                rdMmAccountLog.setMmNickName(basicInfos.get(0).getMmNickName());
                                rdMmAccountLog.setTransTypeCode("BA");
                                rdMmAccountLog.setAccType("SBB");
                                rdMmAccountLog.setTrSourceType("CMP");
                                rdMmAccountLog.setTrOrderOid(retailProfit.getOrderId());
                                rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
                                BigDecimal amount = retailProfit.getProfits().multiply(new BigDecimal(rdMmIntegralRule.getRsCountBonusPoint())).divide(new BigDecimal(100),2);
                                rdMmAccountLog.setAmount(amount);
                                rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().add(amount));
                                rdMmAccountLog.setTransDate(new Date());
                                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                                if(period!=null){
                                    rdMmAccountLog.setTransPeriod(period);
                                }
                                rdMmAccountLog.setTransDesc("零售利润奖励发放");
                                rdMmAccountLog.setStatus(3);
                                rdMmAccountLogDao.insert(rdMmAccountLog);
                                //修改积分账户
                                rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().add(amount));
                                rdMmAccountInfoDao.update(rdMmAccountInfo);
                                //修改零售利润
                                retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                                retailProfit.setActualTime(new Date());
                                if(period!=null){
                                    retailProfit.setActualPeriod(period);
                                }
                                retailProfit.setState(1);
                                retailProfitDao.update(retailProfit);
                            }
                        }
                    }
                }
            }
        }
    }

}