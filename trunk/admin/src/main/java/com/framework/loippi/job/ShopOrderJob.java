package com.framework.loippi.job;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.framework.loippi.consts.AllInPayConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.framework.loippi.consts.AllInPayBillCutConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.integration.RdMmIntegralRuleDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.trade.ShopReturnOrderGoodsDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
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
    private ShopRefundReturnService shopRefundReturnService;
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
    @Resource
    private ShopReturnOrderGoodsDao shopReturnOrderGoodsDao;
    @Resource
    private ShopGoodsSpecDao shopGoodsSpecDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Resource
    private ShopCommonMessageDao shopCommonMessageDao;
    @Resource
    private ShopMemberMessageDao shopMemberMessageDao;
    @Resource
    private ShopOrderDao shopOrderDao;
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
    @Scheduled(cron = "0 15 * * * ? ")  //每隔一小时执行一次 每小时25分执行定时任务
    public void grant(){
        System.out.println("###############################执行定时任务#####################################");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expectTime = format.format(new Date());
        List<RetailProfit> list=retailProfitDao.findTimeMature(expectTime);
        System.out.println(list.size());
        if(list!=null&&list.size()>0){
            for (RetailProfit retailProfit : list) {
                if(retailProfit.getProfits()!=null&&retailProfit.getProfits().compareTo(BigDecimal.ZERO)!=1){
                    continue;
                }
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
                                rdMmAccountLog.setAutohrizeDesc("零售利润奖励发放");
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
                                //设置零售利润积分发放通知
                                ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                                shopCommonMessage.setSendUid(rdMmRelation1.getMmCode());
                                shopCommonMessage.setType(1);
                                shopCommonMessage.setOnLine(1);
                                shopCommonMessage.setCreateTime(new Date());
                                shopCommonMessage.setBizType(2);
                                shopCommonMessage.setIsTop(1);
                                shopCommonMessage.setCreateTime(new Date());
                                shopCommonMessage.setTitle("积分到账通知");
                                shopCommonMessage.setContent("您从零售订单："+retailProfit.getOrderSn()+"获得"+amount+"点积分，已加入奖励积分账户");
                                Long msgId = twiterIdService.getTwiterId();
                                shopCommonMessage.setId(msgId);
                                shopCommonMessageDao.insert(shopCommonMessage);
                                ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                                shopMemberMessage.setBizType(2);
                                shopMemberMessage.setCreateTime(new Date());
                                shopMemberMessage.setId(twiterIdService.getTwiterId());
                                shopMemberMessage.setIsRead(0);
                                shopMemberMessage.setMsgId(msgId);
                                shopMemberMessage.setUid(Long.parseLong(rdMmRelation1.getMmCode()));
                                shopMemberMessageDao.insert(shopMemberMessage);
                            }
                        }
                    }
                }
            }
        }
    }
    //@Scheduled(cron = "0 53 18 * * ? " )  //每天上午十点执行一次
 /*   @Scheduled(cron = "0 35 15 * * ? " )  //每天上午十点执行一次
=======
/*
    @Scheduled(cron = "0 53 18 * * ? " )  //每天上午十点执行一次
>>>>>>> 1635638c2fa1292e31c1f8c3ff987b085cc9c756
    public void test(){
        List<RetailProfit> retailProfits = retailProfitDao.findAll();
        System.out.println(retailProfits.size());
        int num=0;
        if(retailProfits!=null&&retailProfits.size()>0){
            for (RetailProfit retailProfit : retailProfits) {
                long orderId = retailProfit.getOrderId();
                ShopOrder shopOrder = orderService.find(orderId);
                if(shopOrder.getShippingTime()!=null){
                    Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                    ca.setTime(shopOrder.getShippingTime()); //设置时间为当前时间
                    ca.add(Calendar.DATE, 10);//
                    retailProfit.setExpectTime(ca.getTime());
                    String period = rdSysPeriodDao.getSysPeriodService(ca.getTime());
                    if(period!=null){
                        retailProfit.setExpectPeriod(period);
                    }
                    retailProfitDao.update(retailProfit);
                    num++;
                }
            }
        }
        System.out.println(num);*/

/*        System.out.println(retailProfits.size());
        int a =0;
        //ArrayList<Long> longs = new ArrayList<>();
        HashMap<Long, BigDecimal> hashMap = new HashMap<>();
        for (RetailProfit retailProfit : retailProfits) {
            BigDecimal profits = retailProfit.getProfits();
            BigDecimal cut=BigDecimal.ZERO;
            List<ShopRefundReturn> list=shopRefundReturnService.findByOrderId(retailProfit.getOrderId());
            if(list!=null&&list.size()>0){
                for (ShopRefundReturn shopRefundReturn : list) {
                    List<ShopReturnOrderGoods> shopReturnOrderGoods = shopReturnOrderGoodsDao.findByParams(Paramap.create().put("returnOrderId",shopRefundReturn.getId()));
                    if(shopReturnOrderGoods!=null&&shopReturnOrderGoods.size()>0){
                        for (ShopReturnOrderGoods shopReturnOrderGood : shopReturnOrderGoods) {
                            ShopGoodsSpec shopGoodsSpec = shopGoodsSpecDao.find(shopReturnOrderGood.getSpecId());
                            cut=cut.add(shopGoodsSpec.getSpecRetailProfit().multiply(new BigDecimal(shopReturnOrderGood.getGoodsNum())));
                        }
                    }
                }
                profits=profits.subtract(cut);
                retailProfit.setProfits(profits);
                //longs.add(retailProfit.getId());
                hashMap.put(retailProfit.getId(),profits);
                retailProfitDao.update(retailProfit);
            }else {
                a++;
            }
        }
        System.out.println(a);
        System.out.println(hashMap.size());
        System.out.println("#######################################");
        System.out.println(hashMap);
        System.out.println("#######################################");*/
    //}
    /**
     * 定时分账
     */
    //@Scheduled(cron = "0 30 08 * * ? " )  //每天上午八点三十分钟执行一次
    //@Scheduled(cron = "0 15 * * * ? ")  //每隔一小时执行一次 每小时25分执行定时任务
    public void timingAccCut(){
        System.out.println("###############################执行定时分账任务#####################################");
        //查询当前系统时间向前的第二天的已完成支付且未取消的订单 （条件：1.已支付 2.未取消 3.已经发货 4.未进行过分账操作 5.支付时间区间在指定日期内）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-15);
        Date time = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = format.format(time);
        String[] s = timeStr.split(" ");
        String s1 = s[0];
        String startTime=s1+" 00:00:00";
        String endTime=s1+" 23:59:59";
        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        map.put("paymentState",1);
        map.put("orderState",0);
        map.put("cutStatus",0);
        List<ShopOrder> list=shopOrderDao.findNoCutOrder(map);
        List<ShopOrder> list1=shopOrderDao.findNoCutOrder1(map);
        //对筛选出的订单做进一步的判断，判断是否满足分账条件 1.判断订单现金支付部分是否满足分账标准（暂时定义为100） 2.查询一个积分账户满足分账分佣条件的用户，进行分账
        if(list!=null&&list.size()>0){
            for (ShopOrder shopOrder : list) {
                //逐个订单进行处理 判断是否满足分账的条件
                //1.判断支付金额是否满足
                BigDecimal orderAmount = shopOrder.getOrderAmount();
                if(orderAmount.compareTo(new BigDecimal(Integer.toString(AllInPayBillCutConstant.CUT_MINIMUM)))==-1){
                    shopOrder.setCutStatus(1);
                    shopOrder.setCutFailInfo("金额不满足分账条件");
                    shopOrderDao.update(shopOrder);
                    continue;
                }
                Boolean flag=false;
                //2.为当前订单找到一个合适的分账对象
                //2.1 获取当前订单需要分账出去的金额
                BigDecimal amount = orderAmount.multiply(new BigDecimal(Integer.toString(AllInPayBillCutConstant.PERCENTAGE))).multiply(new BigDecimal("0.01")).setScale(2,BigDecimal.ROUND_DOWN);//当前订单需要分出去多少钱，单位为圆
                BigDecimal acc = amount.divide(new BigDecimal("0.95"),0,BigDecimal.ROUND_UP);//奖励积分需要的积分数量 积分取整
                RdMmAccountInfo rdMmAccountInfo = cutGetPeople(shopOrder, acc);
                if(rdMmAccountInfo==null||rdMmAccountInfo.getMmCode()==null){//说明通过推荐人思路未找到合适的分账对象
                    //从积分账户提现记录中筛选出合适条件的进行匹配
                    //按照上次提现时间进行排序，时间越靠前的，放在前面，分页查询第一页，前一百条记录
                    List<RdMmAccountInfo> accountInfos=rdMmAccountInfoDao.findLastWithdrawalOneHundred();
                    if(accountInfos!=null&&accountInfos.size()>0){
                        for (RdMmAccountInfo accountInfo : accountInfos) {
                            if(accountInfo.getBonusBlance().compareTo(acc)!=-1){
                                //调用分账处理方法
                                try {
                                    cutDispose(shopOrder,accountInfo,amount,acc);
                                    flag=true;
                                    break;//分账结束，跳出查询适用分账用户信息循环
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }else {//根据rdMmAccountInfo 进行分账处理
                    try {
                        cutDispose(shopOrder,rdMmAccountInfo,amount,acc);
                        flag=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //如果根据推荐人已经提现记录都未找到合适的推荐人，则flag的值依旧为定义时的false，则不进行分账，只修改订单中关联分账信息的记录
                if(flag==false){
                    shopOrder.setCutStatus(1);
                    shopOrder.setCutFailInfo("未查询到满足条件的用户进行分账");
                    shopOrderDao.update(shopOrder);
                    continue;
                }
            }
        }
        if(list1!=null&&list1.size()>0){
            for (ShopOrder shopOrder : list1) {
                //逐个订单进行处理 判断是否满足分账的条件
                //1.判断支付金额是否满足
                BigDecimal orderAmount = shopOrder.getOrderAmount();
                if(orderAmount.compareTo(new BigDecimal(Integer.toString(AllInPayBillCutConstant.CUT_MINIMUM)))==-1){
                    shopOrder.setCutStatus(1);
                    shopOrder.setCutFailInfo("金额不满足分账条件");
                    shopOrderDao.update(shopOrder);
                    continue;
                }
                Boolean flag=false;
                //2.为当前订单找到一个合适的分账对象
                //2.1 获取当前订单需要分账出去的金额
                BigDecimal amount = orderAmount.multiply(new BigDecimal(Integer.toString(AllInPayBillCutConstant.PERCENTAGE))).multiply(new BigDecimal("0.01")).setScale(2,BigDecimal.ROUND_DOWN);//当前订单需要分出去多少钱，单位为圆
                BigDecimal acc = amount.divide(new BigDecimal("0.95"),0,BigDecimal.ROUND_UP);//奖励积分需要的积分数量 积分取整
                RdMmAccountInfo rdMmAccountInfo = cutGetPeople(shopOrder, acc);
                if(rdMmAccountInfo==null||rdMmAccountInfo.getMmCode()==null){//说明通过推荐人思路未找到合适的分账对象
                    //从积分账户提现记录中筛选出合适条件的进行匹配
                    //按照上次提现时间进行排序，时间越靠前的，放在前面，分页查询第一页，前一百条记录
                    List<RdMmAccountInfo> accountInfos=rdMmAccountInfoDao.findLastWithdrawalOneHundred();
                    if(accountInfos!=null&&accountInfos.size()>0){
                        for (RdMmAccountInfo accountInfo : accountInfos) {
                            if(accountInfo.getBonusBlance().compareTo(acc)!=-1){
                                //调用分账处理方法
                                try {
                                    cutDispose(shopOrder,accountInfo,amount,acc);
                                    flag=true;
                                    break;//分账结束，跳出查询适用分账用户信息循环
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }else {//根据rdMmAccountInfo 进行分账处理
                    try {
                        cutDispose(shopOrder,rdMmAccountInfo,amount,acc);
                        flag=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //如果根据推荐人已经提现记录都未找到合适的推荐人，则flag的值依旧为定义时的false，则不进行分账，只修改订单中关联分账信息的记录
                if(flag==false){
                    shopOrder.setCutStatus(1);
                    shopOrder.setCutFailInfo("未查询到满足条件的用户进行分账");
                    shopOrderDao.update(shopOrder);
                    continue;
                }
            }
        }
    }

    /**
     * 根据扣减积分金额，找到一个合适的获取分账信息的会员
     * @param shopOrder
     * @param acc
     */
    public RdMmAccountInfo cutGetPeople(ShopOrder shopOrder,BigDecimal acc){
        Boolean flag=true;
        String code=Long.toString(shopOrder.getBuyerId());
        RdMmAccountInfo info = new RdMmAccountInfo();
        while (flag){//根据订单购买人查询其推荐人信息，看推荐人是否满足分账条件，如果不满足继续往上查找，直至找到或者查询到公司节点为止 找到返回该会员积分账户信息 如果未找到，返回null
            System.out.println(code);
            RdMmRelation rdMmRelation=rdMmRelationDao.findBySpoCode(code);
            if(rdMmRelation==null||rdMmRelation.getSponsorCode()==null){//如果关系表为null或者该会员推荐人信息异常 结束该方法
                flag=false;
            }else if(rdMmRelation.getSponsorCode().equals("101000158")){//如果推荐人为公司节点
                flag=false;
            } else {
                //获取推荐人的积分账户信息记录
                RdMmAccountInfo rdMmAccountInfo=rdMmAccountInfoDao.findAccByMCode(rdMmRelation.getSponsorCode());
                if(rdMmAccountInfo==null||rdMmAccountInfo.getBonusStatus()==null||rdMmAccountInfo.getBonusStatus()!=0||rdMmAccountInfo.getBonusBlance().compareTo(acc)==-1||rdMmAccountInfo.getAutomaticWithdrawal()==0){
                    code=rdMmRelation.getSponsorCode();
                }else {
                    info=rdMmAccountInfo;
                }
            }
        }
        return info;//返回后判断info中是否存在会员编号即可判断是否通过推荐人方式查询找到分账对象
    }



    /**
     * 根据查询到的合适的分账受益人积分账户信息进行分账，处理用户积分(减扣积分，修改用户最新提现时间)，生成积分交易日志，修改订单内订单分账状态
     * @param shopOrder
     * @param accountInfo
     */
    public void cutDispose(ShopOrder shopOrder,RdMmAccountInfo accountInfo,BigDecimal amount,BigDecimal acc) throws Exception{
        //1.请求通商云服务器，进行分账
        final YunRequest request = new YunRequest("OrderService", "signalAgentPay");
        try {
            request.put("bizOrderNo", shopOrder.getPaySn());
            JSONArray collectPayList = new JSONArray();
            HashMap<String, Object> collect1 = new HashMap<>();
            collect1.put("bizOrderNo",shopOrder.getPaySn());
            collect1.put("amount", shopOrder.getOrderAmount().multiply(new BigDecimal("100")));
            collect1.put("fee", (shopOrder.getOrderAmount().subtract(amount)).multiply(new BigDecimal("100")));
            collectPayList.add(new JSONObject(collect1));
            request.put("collectPayList", collectPayList);
            request.put("bizUserId", accountInfo.getMmCode());
            request.put("accountSetNo","100001");//TODO
            request.put("backUrl", AllInPayConstant.CUT_BILL_BACKURL);//TODO
            request.put("amount",shopOrder.getOrderAmount().multiply(new BigDecimal("100")));
            request.put("fee",(shopOrder.getOrderAmount().subtract(amount)).multiply(new BigDecimal("100")));
            request.put("tradeCode","4001");
            String res = YunClient.request(request);
            System.out.println("res: " + res);
            JSONObject resp = JSON.parseObject(res);
            if(resp.getString("status").equals("error")){//如果分账失败 则记录失败原因
                shopOrder.setCutStatus(3);
                shopOrder.setCutAmount(BigDecimal.ZERO);
                shopOrder.setCutAcc(BigDecimal.ZERO);
                shopOrder.setCutFailInfo(resp.getString("message")+",错误代码："+resp.getString("errorCode"));
                shopOrderDao.update(shopOrder);
            }
            if(resp.getString("status").equals("OK")){
                String signedValue = resp.getString("signedValue");
                JSONObject signedValueMap = JSON.parseObject(signedValue);
                String payStatus = signedValueMap.getString("payStatus");
                if(payStatus.equals("success")){//代付（分账）成功 修改订单内分账状态 扣减积分 生成积分日志
                    //1.修改订单
                    shopOrder.setCutStatus(2);
                    shopOrder.setCutAcc(acc);
                    shopOrder.setCutAmount(amount);
                    shopOrder.setCutTime(new Date());
                    shopOrderDao.update(shopOrder);
                    //2.生成积分变更记录
                    RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                    rdMmAccountLog.setMmCode(accountInfo.getMmCode());
                    List<RdMmBasicInfo> basicInfos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode",accountInfo.getMmCode()));
                    rdMmAccountLog.setMmNickName(basicInfos.get(0).getMmNickName());
                    rdMmAccountLog.setTransTypeCode("WD");
                    rdMmAccountLog.setAccType("SBB");
                    rdMmAccountLog.setTrSourceType("BNK");
                    rdMmAccountLog.setTrOrderOid(shopOrder.getId());
                    rdMmAccountLog.setBlanceBefore(accountInfo.getBonusBlance());
                    rdMmAccountLog.setAmount(acc);
                    rdMmAccountLog.setBlanceAfter(accountInfo.getBonusBlance().add(acc));
                    rdMmAccountLog.setTransDate(new Date());
                    String period = rdSysPeriodDao.getSysPeriodService(new Date());
                    if(period!=null){
                        rdMmAccountLog.setTransPeriod(period);
                    }
                    rdMmAccountLog.setPresentationFeeNow(acc.subtract(amount));
                    rdMmAccountLog.setActualWithdrawals(amount);
                    rdMmAccountLog.setTransDesc("平台订单支付自动分账提现");
                    rdMmAccountLog.setAutohrizeDesc("平台订单支付自动分账提现");
                    rdMmAccountLog.setStatus(3);
                    rdMmAccountLog.setAccStatus(2);
                    rdMmAccountLogDao.insert(rdMmAccountLog);
                    //3.扣减用户积分
                    accountInfo.setBonusBlance(accountInfo.getBonusBlance().subtract(acc));
                    accountInfo.setLastWithdrawalTime(new Date());
                    rdMmAccountInfoDao.update(accountInfo);
                    //4.生成通知消息
                    ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                    shopCommonMessage.setSendUid(accountInfo.getMmCode());
                    shopCommonMessage.setType(1);
                    shopCommonMessage.setOnLine(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setBizType(2);
                    shopCommonMessage.setIsTop(1);
                    shopCommonMessage.setCreateTime(new Date());
                    shopCommonMessage.setTitle("自动提现积分扣减通知");
                    shopCommonMessage.setContent("已帮您自动提现"+acc+"奖励积分到钱包，请在奖励积分明细内查询");
                    Long msgId = twiterIdService.getTwiterId();
                    shopCommonMessage.setId(msgId);
                    shopCommonMessageDao.insert(shopCommonMessage);
                    ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                    shopMemberMessage.setBizType(2);
                    shopMemberMessage.setCreateTime(new Date());
                    shopMemberMessage.setId(twiterIdService.getTwiterId());
                    shopMemberMessage.setIsRead(0);
                    shopMemberMessage.setMsgId(msgId);
                    shopMemberMessage.setUid(Long.parseLong(accountInfo.getMmCode()));
                    shopMemberMessageDao.insert(shopMemberMessage);
                }
                if(payStatus.equals("pending")){//修改订单分账状态分账进行中，等待分账回调信息
                    shopOrder.setCutStatus(4);
                    shopOrder.setCutAcc(acc);
                    shopOrder.setCutAmount(amount);
                    shopOrderDao.update(shopOrder);
                }
                if(payStatus.equals("fail")){//修改订单分账状态 分账失败，记录失败原因
                    shopOrder.setCutStatus(3);
                    shopOrder.setCutAmount(BigDecimal.ZERO);
                    shopOrder.setCutAcc(BigDecimal.ZERO);
                    shopOrder.setCutFailInfo(signedValueMap.getString("payFailMessage"));
                    shopOrderDao.update(shopOrder);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}