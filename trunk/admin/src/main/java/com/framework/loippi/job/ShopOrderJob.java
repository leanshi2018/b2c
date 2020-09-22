package com.framework.loippi.job;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
import com.framework.loippi.consts.AllInPayConstant;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.common.ShopAutoShipDao;
import com.framework.loippi.dao.integration.RdMmIntegralRuleDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.dao.trade.ShopReturnOrderGoodsDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.common.ShopAutoShip;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.order.OrderFundFlow;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.entity.order.ShopSpiritOrderInfo;
import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsPresale;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.walet.RdBizPay;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.common.ShopCommonExpressService;
import com.framework.loippi.service.order.OrderFundFlowService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderLogisticsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.order.ShopSpiritOrderInfoService;
import com.framework.loippi.service.product.ShopExpressSpecialGoodsService;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import com.framework.loippi.service.product.ShopGoodsPresaleService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.travel.RdTourismComplianceService;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import com.framework.loippi.service.travel.RdTravelTicketService;
import com.framework.loippi.service.user.RdGoodsAdjustmentService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.service.wallet.RdBizPayService;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.service.ware.RdWareAdjustService;
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.TongLianUtils;

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
    private RetailProfitService retailProfitService;
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
    @Resource
    private ShopAutoShipDao shopAutoShipDao;

    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopCommonExpressService commonExpressService;
    @Resource
    private RdWarehouseService rdWarehouseService;
    @Resource
    private RdWareAdjustService rdWareAdjustService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private RdGoodsAdjustmentService rdGoodsAdjustmentService;
    @Resource
    private RdInventoryWarningService inventoryWarningService;
    @Resource
    private ShopOrderLogisticsService shopOrderLogisticsService;
    @Resource
    private ShopOrderAddressService orderAddressService;
    @Resource
    private ShopCommonAreaService areaService;
    @Resource
    private ShopGoodsGoodsService shopGoodsGoodsService;
    @Resource
    private ShopGoodsPresaleService shopGoodsPresaleService;
    @Resource
    private ShopExpressSpecialGoodsService shopExpressSpecialGoodsService;
    @Resource
    private RdBizPayService rdBizPayService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private OrderFundFlowService orderFundFlowService;
    @Resource
    private RdTravelTicketService rdTravelTicketService;
    @Resource
    private RdTourismComplianceService rdTourismComplianceService;
    @Resource
    private RdTravelTicketDetailService rdTravelTicketDetailService;
    @Resource
    private ShopSpiritOrderInfoService shopSpiritOrderInfoService;

    private static final Logger log = LoggerFactory.getLogger(ShopOrderJob.class);

    private static String secretkey = "1073f238-1971-4890-bfc4-fd903d90d7eb10294";//秘钥
    private static String customerID = "10294";//客户编号
    private static Long spirit_goods_id = 0l;//白酒商品Id
//

    /**
     * 订单12小时自动关闭取消功能
     */
    @Scheduled(cron = "0 */30 * * * ?")  //每30分钟执行一次
    public void cancelTimeOutPaymentOrder() {
        /*log.info("#################################################################");
        log.info("#####################  开始执行-订单24小时取消 ###################");
        log.info("#################################################################");*/
        /*log.info("#################################################################");
        log.info("#####################  开始执行-订单1小时取消 ###################");
        log.info("#################################################################");*/
        System.out.println("#####################  开始执行-订单1小时取消 ###################");
        int pageNo = 1;
        int pageSize = 500;
        Pageable pager=new Pageable();

        long limit = 2 * 60 * 60 * 1000;
        do {
            pager.setPageSize(pageSize);
            pager.setPageNumber(pageNo);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
            pager.setParameter(Paramap.create().put("orderState", 10).put("lockState", "0").put("servenDay", calendar.getTime()));
            List<ShopOrder> orderList = orderService.findByPage(pager).getContent();
            if (orderList != null && orderList.size() > 0) {
                orderList.forEach(order -> {
                    //log.info("订单[{}]创建时间[{}], 超过[{}]秒未支付, 自动取消");
                    System.out.println("订单[{}]创建时间[{}], 超过[{}]秒未支付, 自动取消");
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

    //@Scheduled(cron = "0 10 12 * * ?" )  //每30分钟执行一次
    public void addTL() {
        RdTravelTicket rdTravelTicket = rdTravelTicketService.find(6696310439497699328l);

        List<String> mCodeList = new ArrayList<>();
        mCodeList.add("102017199");
        mCodeList.add("103111921");
        mCodeList.add("102985369");

        for (String mCode : mCodeList) {

            RdMmBasicInfo mmBasicInfo = rdMmBasicInfoDao.findByMCode(mCode);
            Long issueNum = rdTravelTicket.getIssueNum();
            if (issueNum==null){
                issueNum = 0l;
            }

            Long total = 0l;
            int j = 0;
            if (mCode.equals("103111921")){
                j=1;
            }
            if (mCode.equals("102985369")){
                j=1;
            }
            if (mCode.equals("102017199")){
                j=4;
            }

            for (int i=0;i<j;i++){
                RdTravelTicketDetail rdTravelTicketDetail = new RdTravelTicketDetail();
                rdTravelTicketDetail.setId(twiterIdService.getTwiterId());
                rdTravelTicketDetail.setTravelId(rdTravelTicket.getId());
                rdTravelTicketDetail.setTravelName(Optional.ofNullable(rdTravelTicket.getTravelName()).orElse(""));
                rdTravelTicketDetail.setTicketPrice(Optional.ofNullable(rdTravelTicket.getTicketPrice()).orElse(BigDecimal.ZERO));
                rdTravelTicketDetail.setTicketSn("T"+twiterIdService.getTwiterId());
                rdTravelTicketDetail.setStatus(0);
                rdTravelTicketDetail.setOwnCode(mmBasicInfo.getMmCode());
                rdTravelTicketDetail.setOwnNickName(mmBasicInfo.getMmNickName());
                rdTravelTicketDetail.setOwnTime(new Date());
                rdTravelTicketDetail.setImage(Optional.ofNullable(rdTravelTicket.getImage()).orElse(""));
                rdTravelTicketDetailService.save(rdTravelTicketDetail);
                total = total+1;
            }

            rdTravelTicket.setIssueNum(issueNum+total);
            rdTravelTicketService.update(rdTravelTicket);
        }
        System.out.println("跑完");
    }


    //@Scheduled(cron = "0 32 12 * * ?" )  //每30分钟执行一次
    public void refundTL() {
        String backUrl = "";//后台通知地址 TODO

        String orderNo = "202006014456207655";//随便写
        String bizPaySn = "P20200701115005092WOMI92525";
        String paySn = "P20200701115005092";
        ShopOrder order = orderService.findByPaySn(paySn);

        BigDecimal cutAmount = new BigDecimal("0.00");
        if (order.getCutAmount()==null){
            cutAmount = new BigDecimal("0.00");
        }else {
            cutAmount = order.getCutAmount();//分账人金额
        }


        BigDecimal orderAmount = order.getOrderAmount();
        Double b = orderAmount.doubleValue()*100;
        BigDecimal bd= new BigDecimal(b);
        Double g = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Long oAmount = new Double(g).longValue();

        BigDecimal feeAmountBig = orderAmount.subtract(cutAmount);//公司的抽佣
        Double f = feeAmountBig.doubleValue()*100;
        BigDecimal fd= new BigDecimal(f);
        Double e = fd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Long feeAmount = new Double(e).longValue();

        System.out.println("*************************************");
        System.out.println("oAmount="+oAmount);
        System.out.println("feeAmountBig="+feeAmountBig);
        System.out.println("f="+f);
        System.out.println("feeAmount="+feeAmount);
        System.out.println("*************************************");

        List<Map<String, Object>> refundList = new ArrayList<Map<String, Object>>();
        Map<String, Object> refundMember = new HashMap<String, Object>();
        String s = TongLianUtils.refundOrder(orderNo,bizPaySn, order.getBuyerId().toString(), "D0", refundList,
                backUrl,oAmount,0l,feeAmount,null);
        System.out.println("跑完");
    }

    /**
     * 定时分账
     */
    //@Scheduled(cron = "0 30 08 * * ? " )  //每天上午八点三十分钟执行一次
    @Scheduled(cron = "0 18 * * * ? ")  //每隔一小时执行一次 每小时18分执行定时任务
    public void timingAccCut(){
        System.out.println("###############################执行定时分账任务#####################################");
        //查询当前系统时间向前的第二天的已完成支付且未取消的订单 （条件：1.已支付 2.未取消 3.已经发货 4.未进行过分账操作 5.支付时间区间在指定日期内）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-10);
        Date time = calendar.getTime();
        //Date time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = format.format(time);
        String[] s = timeStr.split(" ");
        String s1 = s[0];
        String startTime=s1+" 00:00:00";
        String endTime=s1+" 23:59:59";
        HashMap<String, Object> map = new HashMap<>();
        //map.put("startTime",startTime);
        map.put("endTime",endTime);
        map.put("paymentState",1);
        map.put("orderState",0);
        map.put("cutStatus",5);//查询符合时间范围内预扣积分的订单
        List<ShopOrder> list=shopOrderDao.findNoCutOrder(map);
        //List<ShopOrder> list1=shopOrderDao.findNoCutOrder1(map);
        if(list!=null&&list.size()>0){
            for (ShopOrder shopOrder : list) {
                //1.根据订单内预扣积分信息，进行分账
                RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(shopOrder.getCutGetId());
                if(accountInfo!=null){
                    try {
                        cutDispose(shopOrder,accountInfo,Optional.ofNullable(shopOrder.getCutAmount()).orElse(BigDecimal.ZERO),Optional.ofNullable(shopOrder.getCutAcc()).orElse(BigDecimal.ZERO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
/*        if(list1!=null&&list1.size()>0){
            for (ShopOrder shopOrder : list1) {
                //1.根据订单内预扣积分信息，进行分账
                RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(shopOrder.getCutGetId());
                if(accountInfo!=null){
                    try {
                        cutDispose(shopOrder,accountInfo,Optional.ofNullable(shopOrder.getCutAmount()).orElse(BigDecimal.ZERO),Optional.ofNullable(shopOrder.getCutAcc()).orElse(BigDecimal.ZERO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
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
            }else if(rdMmRelation.getSponsorCode().equals("101000158")||(rdMmRelation.getSponsorCode().equals("900000000"))){//如果推荐人为公司节点
                flag=false;
            } else {
                //获取推荐人的积分账户信息记录
                RdMmAccountInfo rdMmAccountInfo=rdMmAccountInfoDao.findAccByMCode(rdMmRelation.getSponsorCode());
                if(rdMmAccountInfo==null||rdMmAccountInfo.getBonusStatus()==null||rdMmAccountInfo.getBonusStatus()!=0||(rdMmAccountInfo.getBonusBlance().subtract(rdMmAccountInfo.getWithdrawalLine())).compareTo(acc)==-1||rdMmAccountInfo.getAutomaticWithdrawal()==0){
                    code=rdMmRelation.getSponsorCode();
                }else {
                    info=rdMmAccountInfo;
                    flag=false;
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
            List<RdBizPay> rdBizPayList = rdBizPayService.findByPaysnAndStatus(shopOrder.getPaySn(),1);
            RdBizPay rdBizPay = rdBizPayList.get(0);
            BigDecimal cutAmount = shopOrder.getCutAmount();
            if(cutAmount.compareTo(BigDecimal.ZERO)!=1){
                return;
            }
            double cutAll = cutAmount.doubleValue() * 100;
            request.put("bizOrderNo", rdBizPay.getCutPaySn());
            JSONArray collectPayList = new JSONArray();
            HashMap<String, Object> collect1 = new HashMap<>();
            collect1.put("bizOrderNo",rdBizPay.getBizPaySn());
            collect1.put("amount",cutAll);
            collectPayList.add(new JSONObject(collect1));
            request.put("collectPayList", collectPayList);
            request.put("bizUserId", accountInfo.getMmCode());
            request.put("accountSetNo", AllInPayBillCutConstant.ACCOUNT_SET_NO);//TODO
            request.put("backUrl", AllInPayConstant.CUT_BILL_BACKURL);//TODO
            request.put("amount",cutAll);
            request.put("fee",0L);
            request.put("tradeCode","4001");
            String res = YunClient.request(request);
            System.out.println("res: " + res);
            JSONObject resp = JSON.parseObject(res);
            if(resp.getString("status").equals("error")){//如果分账失败 则记录失败原因
                if(resp.getString("errorCode").equals("40017")){
                    return;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("transTypeCode","AWD");
                map.put("accType","SBB");
                map.put("trSourceType","BNK");
                map.put("trOrderOid",shopOrder.getId());
                map.put("accStatus",0);
                RdMmAccountLog rdMmAccountLog1 =rdMmAccountLogDao.findCutByOrderId(map);
                if(rdMmAccountLog1!=null){
                    rdMmAccountLog1.setAccStatus(1);
                    rdMmAccountLogDao.updateByCutOrderId(map);
                }
                String mmCode = shopOrder.getCutGetId();
                //RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(mmCode);
                RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                rdMmAccountLog.setMmCode(accountInfo.getMmCode());
                List<RdMmBasicInfo> basicInfos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode",accountInfo.getMmCode()));
                rdMmAccountLog.setMmNickName(basicInfos.get(0).getMmNickName());
                rdMmAccountLog.setTransTypeCode("CF");
                rdMmAccountLog.setAccType("SBB");
                rdMmAccountLog.setTrSourceType("BNK");
                rdMmAccountLog.setTrOrderOid(shopOrder.getId());
                rdMmAccountLog.setBlanceBefore(accountInfo.getBonusBlance());
                rdMmAccountLog.setAmount(shopOrder.getCutAcc());
                rdMmAccountLog.setBlanceAfter(accountInfo.getBonusBlance().add(shopOrder.getCutAcc()));
                rdMmAccountLog.setTransDate(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                if(period!=null){
                    rdMmAccountLog.setTransPeriod(period);
                }
                rdMmAccountLog.setTransDesc("订单分账失败退还用户奖励积分");
                rdMmAccountLog.setAutohrizeDesc("订单分账失败退还用户奖励积分");
                rdMmAccountLog.setStatus(3);
                rdMmAccountLogDao.insert(rdMmAccountLog);
                accountInfo.setBonusBlance(accountInfo.getBonusBlance().add(shopOrder.getCutAcc()));
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
                shopCommonMessage.setTitle("自动提现失败积分退还通知");
                shopCommonMessage.setContent("订单自动提现失败，退还"+shopOrder.getCutAcc()+"奖励积分到积分账户");
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
                    String mmCode = shopOrder.getCutGetId();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("transTypeCode","AWD");
                    map.put("accType","SBB");
                    map.put("trSourceType","BNK");
                    map.put("trOrderOid",shopOrder.getId());
                    map.put("accStatus",0);
                    RdMmAccountLog rdMmAccountLog =rdMmAccountLogDao.findCutByOrderId(map);
                    if(rdMmAccountLog!=null){
                        rdMmAccountLog.setAccStatus(2);
                        rdMmAccountLogDao.updateByCutOrderId(map);
                    }
                    //3.修改OrderFundFlow
                    OrderFundFlow orderFundFlow = orderFundFlowService.find("orderId",shopOrder.getId());
                    if(orderFundFlow!=null){
                        orderFundFlow.setCutFlag(1);
                        orderFundFlow.setCutPoint(amount);
                        orderFundFlow.setCutGetId(shopOrder.getCutGetId());
                        orderFundFlow.setCutTime(new Date());
                        orderFundFlowService.update(orderFundFlow);
                    }
                }
                if(payStatus.equals("pending")){//修改订单分账状态分账进行中，等待分账回调信息
                    shopOrder.setCutStatus(4);
                    shopOrder.setCutAcc(acc);
                    shopOrder.setCutAmount(amount);
                    shopOrderDao.update(shopOrder);
                }
                if(payStatus.equals("fail")){//修改订单分账状态 分账失败，记录失败原因
                    //分账失败 归还预扣积分
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("transTypeCode","AWD");
                    map.put("accType","SBB");
                    map.put("trSourceType","BNK");
                    map.put("trOrderOid",shopOrder.getId());
                    map.put("accStatus",0);
                    RdMmAccountLog rdMmAccountLog1 =rdMmAccountLogDao.findCutByOrderId(map);
                    if(rdMmAccountLog1!=null){
                        rdMmAccountLog1.setAccStatus(1);
                        rdMmAccountLogDao.updateByCutOrderId(map);
                    }
                    String mmCode = shopOrder.getCutGetId();
                    //RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(mmCode);
                    RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                    rdMmAccountLog.setMmCode(accountInfo.getMmCode());
                    List<RdMmBasicInfo> basicInfos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode",accountInfo.getMmCode()));
                    rdMmAccountLog.setMmNickName(basicInfos.get(0).getMmNickName());
                    rdMmAccountLog.setTransTypeCode("CF");
                    rdMmAccountLog.setAccType("SBB");
                    rdMmAccountLog.setTrSourceType("BNK");
                    rdMmAccountLog.setTrOrderOid(shopOrder.getId());
                    rdMmAccountLog.setBlanceBefore(accountInfo.getBonusBlance());
                    rdMmAccountLog.setAmount(shopOrder.getCutAcc());
                    rdMmAccountLog.setBlanceAfter(accountInfo.getBonusBlance().add(shopOrder.getCutAcc()));
                    rdMmAccountLog.setTransDate(new Date());
                    String period = rdSysPeriodDao.getSysPeriodService(new Date());
                    if(period!=null){
                        rdMmAccountLog.setTransPeriod(period);
                    }
                    rdMmAccountLog.setTransDesc("订单分账失败退还用户奖励积分");
                    rdMmAccountLog.setAutohrizeDesc("订单分账失败退还用户奖励积分");
                    rdMmAccountLog.setStatus(3);
                    rdMmAccountLogDao.insert(rdMmAccountLog);
                    accountInfo.setBonusBlance(accountInfo.getBonusBlance().add(shopOrder.getCutAcc()));
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
                    shopCommonMessage.setTitle("自动提现失败积分退还通知");
                    shopCommonMessage.setContent("订单自动提现失败，退还"+shopOrder.getCutAcc()+"奖励积分到积分账户");
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
                    //分账失败 修改订单
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


    @Scheduled(cron = "0 0 15,17 * * ?" )  //每天15点和17点发货
    public void timingOrder(){
        System.out.println("###############################执行定时发货#####################################");
        Integer fStatus = 1;
        List<ShopAutoShip> shipDaoAll = shopAutoShipDao.findAll();
        Integer size = shipDaoAll.size();
        if (size==1){
            ShopAutoShip shopAutoShip = shipDaoAll.get(0);
            Integer shipStatus = shopAutoShip.getShipStatus();
            if (shipStatus==null){
                fStatus =1;
            }else {
                fStatus = shipStatus;
            }
        }

        if (fStatus==1){
            List<ShopOrder> orderList = orderService.findStatu20();//所有代发货订单
            for (ShopOrder shopOrder : orderList) {
                if (shopOrder.getLogisticType()==1){

                    List<ShopOrderGoods> orderGoodsLists = shopOrderGoodsService.listByOrderId(shopOrder.getId());//订单所有商品
                    int a = 0;//是白酒1
                    int b = 0;//不是白酒1
                    if (orderGoodsLists.size()>0){
                        for (ShopOrderGoods orderGoods : orderGoodsLists) {
                            if (orderGoods.getGoodsId().longValue()!=spirit_goods_id.longValue()){
                                b=1;
                            }
                            if (orderGoods.getGoodsId().longValue()==spirit_goods_id.longValue()){
                                a=1;
                            }
                        }
                    }

                    if (a==1&&b==0){//订单中只有白酒
                        for (ShopOrderGoods orderGoods : orderGoodsLists) {
                            ShopSpiritOrderInfo haveInfo = shopSpiritOrderInfoService.findByOrderIdAndSpecId(shopOrder.getId(),orderGoods.getSpecId());
                            if (haveInfo==null){
                                ShopSpiritOrderInfo spiritOrderInfo = new ShopSpiritOrderInfo();
                                spiritOrderInfo.setId(twiterIdService.getTwiterId());
                                spiritOrderInfo.setOrderId(orderGoods.getOrderId());
                                spiritOrderInfo.setGoodsId(orderGoods.getGoodsId());
                                spiritOrderInfo.setSpecId(orderGoods.getSpecId());
                                spiritOrderInfo.setGoodsNum(orderGoods.getGoodsNum());
                                spiritOrderInfo.setSubmitState(0);
                                spiritOrderInfo.setOrderShipState(0);
                                shopSpiritOrderInfoService.save(spiritOrderInfo);
                            }
                        }
                    }else {
                        Map<String, Object> resMap = orderShip(shopOrder.getId());//发货返回信息
                        String resultS = (String)resMap.get("res");
                        if (!"".equals(resultS)){
                            if(resultS.substring(0,1).equals("{")){
                                Map maps = (Map) JSON.parse(resultS);
                                String success = (String) maps.get("success");//是否成功
                                String orderSn = (String) maps.get("CsRefNo");//订单编号
                                if (success.equals("success")) {//发货成功
                                    String trackingNo = (String) maps.get("TrackingNo");//运单号
                                    if (!Character.isDigit(trackingNo.charAt(0))) {//不是数字，就是发货失败，有可能在草稿箱
                                        String failInfo = (String) maps.get("Info");//失败信息
                                        System.out.println("failInfo");
                                        orderService.updateOrderStatus(orderSn, 20, 20, failInfo, "");
                                    } else {
                                        //TrackingNo第一个字符是数字
                                        if (!"".equals(trackingNo)) {// 订单状态：待收货 提交状态：已提交 失败原因："" +运单号
                                            System.out.println("待收货");
                                            Integer orderState = 30;
                                            Integer submitStatus = 10;
                                            String failInfo = "";
                                            orderService.updateOrderStatus(orderSn, orderState, submitStatus, failInfo, trackingNo);
                                        } else {//状订单态：仓库在备货 提交状态：已提交 失败原因：""
                                            System.out.println("仓库在备货");
                                            orderService.updateOrderStatus(orderSn, 25, 10, "", "");
                                        }
                                        List<Map<String, Object>> products = (List<Map<String, Object>>) resMap.get("Products");//发货的数据
                                            /*for (Map<String, Object> product : products) {
                                                String sku = (String) product.get("SKU");//发货商品规格编号
                                                Integer quantity = Integer.valueOf(product.get("MaterialQuantity").toString());//发货商品数量
                                                ShopGoodsSpec goodsSpec = shopGoodsSpecService.findByspecGoodsSerial(sku);//商品规格信息
                                                Long goodsSpecId = goodsSpec.getId();//商品规格id
                                                inventoryWarningService.updateInventoryByWareCodeAndSpecId("20192514", goodsSpecId, quantity);
                                            }*/

                                        List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
                                        List<ShopOrderGoods> orderGoodsList = (List<ShopOrderGoods>) resMap.get("orderGoods");
                                        List<ShopOrderGoods> shopOrderGoods = updateOrderGoods(shopOrderGoodsList, orderGoodsList, trackingNo);//需要修改订单商品信息
                                        shopOrderGoodsService.updateBatchForShipmentNum(shopOrderGoods);//修改订单商品信息
                                    }
                                }
                                if (success.equals("failure")) {//发货失败   提交状态：提交失败 失败原因：failInfo
                                    String failInfo = (String) maps.get("Info");//失败信息
                                    System.out.println("failInfo");
                                    orderService.updateOrderStatus(orderSn, 20, 20, failInfo, "");
                                }
                            }
                        }
                    }

                }else {//自提
                    /*ShopOrder shopOrder1 = new ShopOrder();
                    shopOrder1.setId(shopOrder.getId());
                    shopOrder1.setOrderState(30);
                    orderService.update(shopOrder1);*/
                }
            }
        }
    }

    /**
     * 需要修改订单商品信息
     * @param orderGoodsList
     * @return
     */
    public List<ShopOrderGoods> updateOrderGoods(List<ShopOrderGoods> shopOrderGoodsNullList,List<ShopOrderGoods> orderGoodsList,String trackingNo) {
        for (ShopOrderGoods orderGoods : orderGoodsList) {
            if (orderGoods.getGoodsId().longValue()!=spirit_goods_id.longValue()){//不是白酒的部分商品
                ShopCommonExpress express = commonExpressService.find(44l);

                ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
                shopOrderGoods.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
                shopOrderGoods.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
                shopOrderGoods.setShippingExpressName(Optional.ofNullable(express.getEName()).orElse(""));
                shopOrderGoods.setShippingCode(Optional.ofNullable(trackingNo).orElse("-1"));
                shopOrderGoods.setShippingGoodsNum(orderGoods.getGoodsNum());
                shopOrderGoods.setId(orderGoods.getId());
                shopOrderGoodsNullList.add(shopOrderGoods);

                String adminName="";
                Subject subject = SecurityUtils.getSubject();
                if (subject != null) {
                    Principal principal = (Principal) subject.getPrincipal();
                    if (principal != null && principal.getId() != null) {
                        adminName=principal.getUsername();
                    }
                }

                RdWarehouse warehouse = rdWarehouseService.findByCode("20192514");
                //新增发货单
                RdWareAdjust rdWareAdjust = new RdWareAdjust();
                rdWareAdjust.setWareCode(warehouse.getWareCode());
                rdWareAdjust.setWareName(warehouse.getWareName());
                rdWareAdjust.setAdjustType("SOT");
                rdWareAdjust.setStatus(3);
                rdWareAdjust.setAutohrizeBy(adminName);
                rdWareAdjust.setAutohrizeTime(new Date());
                rdWareAdjust.setAutohrizeDesc("订单发货");
                rdWareAdjustService.insert(rdWareAdjust);

                ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(orderGoods.getSpecId());
                ShopGoods shopGoods = shopGoodsService.find(orderGoods.getGoodsId());
                //新增的发货商品详情
                RdGoodsAdjustment rdGoodsAdjustment = new RdGoodsAdjustment();
                rdGoodsAdjustment.setWid(rdWareAdjust.getWid());
                rdGoodsAdjustment.setSpecificationId(shopGoodsSpec.getId());
                rdGoodsAdjustment.setGoodId(shopGoodsSpec.getGoodsId());
                rdGoodsAdjustment.setGoodsName(shopGoods.getGoodsName());
                rdGoodsAdjustment.setSpecName(shopGoodsSpec.getSpecName());
                rdGoodsAdjustment.setGoodsSpec(shopGoodsSpec.getSpecGoodsSpec());
                rdGoodsAdjustment.setStockNow(shopGoodsSpec.getSpecGoodsStorage().longValue());
                rdGoodsAdjustment.setStockInto(Long.valueOf(orderGoods.getGoodsNum()));
                rdGoodsAdjustment.setCreateTime(shopGoods.getCreateTime());
                rdGoodsAdjustment.setWareCode(warehouse.getWareCode());
                rdGoodsAdjustment.setSign(1);
                rdGoodsAdjustment.setAutohrizeTime(new Date());
                rdGoodsAdjustment.setStatus(1L);
                rdGoodsAdjustmentService.insert(rdGoodsAdjustment);

                inventoryWarningService.updateInventoryByWareCodeAndSpecId("20192514", orderGoods.getSpecId(), orderGoods.getGoodsNum());

                //新增订单商品物流信息表
                ShopOrderLogistics shopOrderLogistics = new ShopOrderLogistics();
                shopOrderLogistics.setGoodsId(shopGoodsSpec.getGoodsId());
                shopOrderLogistics.setGoodsImage(shopGoods.getGoodsImage());
                shopOrderLogistics.setGoodsName(shopGoods.getGoodsName());
                shopOrderLogistics.setGoodsType(shopGoods.getGoodsType());
                shopOrderLogistics.setOrderId(orderGoods.getOrderId());
                shopOrderLogistics.setSpecId(shopGoodsSpec.getId());
                shopOrderLogistics.setSpecInfo(orderGoods.getSpecInfo());
                shopOrderLogistics.setGoodsNum(orderGoods.getGoodsNum());
                shopOrderLogistics.setPpv(shopGoodsSpec.getPpv());
                shopOrderLogistics.setPrice(shopGoodsSpec.getSpecRetailPrice());
                shopOrderLogistics.setShippingExpressCode(Optional.ofNullable(express.getECode()).orElse(""));
                shopOrderLogistics.setShippingExpressId(Optional.ofNullable(express.getId()).orElse(-1L));
                shopOrderLogistics.setShippingCode(Optional.ofNullable(trackingNo).orElse("-1"));
                shopOrderLogistics.setId(twiterIdService.getTwiterId());
                shopOrderLogisticsService.insert(shopOrderLogistics);
            }

        }
        return shopOrderGoodsNullList;
    }


    /**
     * 连接第三方发货
     * @param id
     * @return
     */
    public Map<String,Object> orderShip(Long id) {
        Map<String,Object> map = new HashMap<String,Object>();//strorderinfo参数
        map.put("Style","1");
        map.put("CustomerID",customerID);
        //map.put("ChannelInfoID","CNZT-B");

        ShopOrder shopOrder = orderService.find(id);
        String orderSn = shopOrder.getOrderSn();//订单编号
        map.put("CsRefNo",orderSn);//订单号
        String buyerName = shopOrder.getBuyerName();//买家名称
        String buyerPhone = shopOrder.getBuyerPhone();//买家手机号码
        Long addressId = shopOrder.getAddressId();
        ShopOrderAddress orderAddress = orderAddressService.find(addressId);//订单地址信息
        String trueName = orderAddress.getTrueName();//收件人姓名
        map.put("ShipToName",trueName);

        String mobPhone = orderAddress.getMobPhone();//收件人电话号码
        map.put("ShipToPhoneNumber",mobPhone);

        Long provincedId = orderAddress.getProvinceId();//省级id
        Long cityId = orderAddress.getCityId();//市级ID
        Long areaId = orderAddress.getAreaId();//地区ID
        String zipCode = orderAddress.getZipCode();//邮编
        ShopCommonArea commonAreaProvinced = areaService.find(provincedId);//省
        ShopCommonArea commonAreaCity = areaService.find(cityId);//市
        ShopCommonArea commonAreaArea = areaService.find(areaId);//地区
        String provinced = commonAreaProvinced.getAreaName();
        String city = commonAreaCity.getAreaName();
        String area = commonAreaArea.getAreaName();
        String address = provinced+city+area+orderAddress.getAddress();//地址//收件人地址行1

        map.put("ShipToCountry",provinced);
        map.put("ShipToState",provinced);
        map.put("ShipToCity",city);
        map.put("ShipToAdress1",address);

        map.put("ShipToAdress2","");
        map.put("ShipToCompanyName","");
        map.put("OrderStatus","3");//订单状态--(草稿=1),(确认=3)
        map.put("TrackingNo","");
        map.put("CusRemark","");
        map.put("CODType","");
        map.put("CODMoney","");
        map.put("IDCardNo","");
        map.put("AgentNo","");
        map.put("BillQty","");
        map.put("WarehouseId","5200");

        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsService.listByOrderId(id);//订单所有商品
        List<Map<String,Object>> productLists = new ArrayList<Map<String,Object>>();//商品list
        for (ShopOrderGoods orderGoods : orderGoodsList) {

            int goodsNum = orderGoods.getGoodsNum();//商品数量
            Long specId = orderGoods.getSpecId();//商品规格索引id

            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(specId);//订单里商品的规格
            String specGoodsSpec = goodsSpec.getSpecGoodsSpec();
            //{"6544521286435999744":"24g"}
            String[] specs = specGoodsSpec.split(",");
            if (specs.length==1){
                Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                productMap.put("ProducingArea","");
                productMap.put("HSCode","");

                String[] splits = specs[0].split("\"");
                Long goodsId1 = goodsSpec.getGoodsId();//组合商品id
                Long specId1 = Long.valueOf(splits[1]);//商品规格id
                int total = 0;//商品数量
                List<ShopGoodsGoods> goodsGoodsList = shopGoodsGoodsService.findGoodsGoodsByGoodsId(goodsId1);//查看是否组合商品
                if (goodsGoodsList!=null&&goodsGoodsList.size()!=0&&goodsGoodsList.size()==1){//是组合商品

                    ShopGoodsSpec goodsSpec1 = shopGoodsSpecService.find(specId1);//参与组合商品规格数据
                    productMap.put("SKU",goodsSpec1.getSpecGoodsSerial());//物品SKU
                    productMap.put("Price",goodsSpec1.getSpecRetailPrice());//物品价格
                    productMap.put("Weight",goodsSpec1.getWeight());
                    ShopGoods shopGoods = shopGoodsService.find(goodsSpec1.getGoodsId());//参与组合商品信息
                    productMap.put("EnName",shopGoods.getGoodsName());//物品名称
                    productMap.put("CnName",shopGoods.getGoodsName());//物品名称

                    for (ShopGoodsGoods goodsGoods : goodsGoodsList) {
                        int joinNum = goodsGoods.getJoinNum();//参与组合商品数量
                        total = joinNum*goodsNum;
                    }
                    productMap.put("MaterialQuantity",total);//物品数量

                }

                if (goodsGoodsList==null||goodsGoodsList.size()==0){//不是组合商品
                    total =goodsNum;
                    productMap.put("MaterialQuantity",total);//物品数量
                    productMap.put("SKU",goodsSpec.getSpecGoodsSerial());//物品SKU
                    productMap.put("Price",goodsSpec.getSpecRetailPrice());//物品价格
                    productMap.put("Weight",goodsSpec.getWeight());
                    ShopGoods shopGoods = shopGoodsService.find(goodsSpec.getGoodsId());//参与组合商品信息
                    productMap.put("EnName",shopGoods.getGoodsName());//物品名称
                    productMap.put("CnName",shopGoods.getGoodsName());//物品名称
                }

                if (productLists.size()==0){
                    productLists.add(productMap);
                }else{
                    int h = 10000;
                    for (int i = 0; i < productLists.size(); i++) {
                        Map<String,Object> pMap = productLists.get(i);
                        if (pMap.get("SKU").equals(productMap.get("SKU"))) {
                            h = i;
                        }
                    }
                    if (h!=10000){//list里面有
                        Map<String,Object> p = productLists.get(h);//里面原来的数据
                        int quantity = (int)p.get("MaterialQuantity");//里面原来商品数量
                        total = quantity +total;//总的数量
                        p.put("MaterialQuantity",total);
                        //移除原来的
                        productLists.remove(h);
                        //添加合并数据
                        productLists.add(p);

                    }else{//list里面还没有
                        productLists.add(productMap);
                    }
                }

            }

            //{"6544521286435999744":"24g","6544522466675396608":"22g","6544414763865083904":"3#船长正红"}
            if (specs.length>1){
                Long goodsId = goodsSpec.getGoodsId();//组合商品id
                for (int j=0;j<specs.length;j++) {
                    Map<String,Object> productMap = new HashMap<String,Object>();
                    productMap.put("ProducingArea","");
                    productMap.put("HSCode","");

                    String[] splits = specs[j].split("\"");
                    Long specId2 = Long.valueOf(splits[1]);
                    ShopGoodsSpec goodsSpec1= shopGoodsSpecService.find(specId2);//参与组合商品规格数据
                    productMap.put("SKU",goodsSpec1.getSpecGoodsSerial());
                    productMap.put("Weight",goodsSpec1.getWeight());//物品SKU
                    productMap.put("Price",goodsSpec1.getSpecRetailPrice());//物品价格

                    Long goodsId1 = goodsSpec1.getGoodsId();//参与组合商品里商品id
                    ShopGoods shopGoods = shopGoodsService.find(goodsId1);//参与组合商品信息
                    productMap.put("EnName",shopGoods.getGoodsName());//物品名称
                    productMap.put("CnName",shopGoods.getGoodsName());//物品名称

                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("goodId",goodsId);
                    map1.put("combineGoodsId",goodsId1);
                    ShopGoodsGoods goodsGoods = shopGoodsGoodsService.findGoodsGoods(map1);
                    int joinNum = goodsGoods.getJoinNum();//组合商品里商品数量
                    int total = joinNum*goodsNum;//总数量
                    productMap.put("MaterialQuantity",total);

                    if (productLists.size()==0){
                        productLists.add(productMap);
                    }else{
                        int h = 10000;
                        for (int i = 0; i < productLists.size(); i++) {
                            Map<String,Object> pMap = productLists.get(i);
                            if (pMap.get("SKU").equals(productMap.get("SKU"))) {
                                h = i;
                            }
                        }
                        if (h!=10000){//list里面有
                            Map<String,Object> p = productLists.get(h);//里面原来的数据
                            int quantity = (int)p.get("MaterialQuantity");//里面原来商品数量
                            total = quantity +total;//总的数量
                            p.put("MaterialQuantity",total);
                            //移除原来的
                            productLists.remove(h);
                            //添加合并数据
                            productLists.add(p);

                        }else{//list里面还没有
                            productLists.add(productMap);
                        }
                    }

                }
            }
        }


        List<ShopGoodsPresale> presaleAll = shopGoodsPresaleService.findAll();

/*********************************************排除洗发水*************************************************/
        /*List<Map<String,Object>> productListss = new ArrayList<Map<String,Object>>();//商品list
        for (Map<String, Object> product : productLists) {
            if (!product.get("SKU").equals("6972190330042")){//不是洗发水
                productListss.add(product);
            }
        }

        map.put("Products",productListss);*/

/**************************************选择快递********************************************************/
        String eExpressCode = "CNZT-B";//第三方物流单号
        //最大级数
        /*Integer macSort = commonExpressService.macSort();
        for (int i=1;i<=macSort;i++){
            //根据级数查快递
            ShopCommonExpress express = commonExpressService.findBySort(i);
            Long expressId = express.getId();
        }*/


        //特殊快递渠道商品
        List<ShopExpressSpecialGoods> specialGoodsList = shopExpressSpecialGoodsService.findByState(0);
        Map<String,String> specialGoodsMap = new HashMap<String,String>();
        for (ShopExpressSpecialGoods specialGoods : specialGoodsList) {
            String specGoodsSerial = specialGoods.getSpecGoodsSerial();
            ShopCommonExpress express = commonExpressService.findById(specialGoods.getExpressId());
            String expressCode = express.getEExpressCode();
            specialGoodsMap.put(specGoodsSerial,expressCode);
        }
/*******************************添加清洁剂瓶盖（6972190330202-1）*************************************/
        List<Map<String,Object>> productListss = new ArrayList<Map<String,Object>>();//商品list
        int cupNum = 0;
        for (Map<String, Object> product : productLists) {


            //剔除白酒
            String sgs = (String)product.get("SKU");
            ShopGoodsSpec spec = shopGoodsSpecService.findByspecGoodsSerial(sgs);

            if (spec.getGoodsId().longValue()==spirit_goods_id.longValue()){//是白酒
                ShopSpiritOrderInfo haveInfo = shopSpiritOrderInfoService.findByOrderIdAndSpecId(shopOrder.getId(),spec.getId());
                if (haveInfo==null){
                    ShopSpiritOrderInfo spiritOrderInfo = new ShopSpiritOrderInfo();
                    spiritOrderInfo.setId(twiterIdService.getTwiterId());
                    spiritOrderInfo.setOrderId(shopOrder.getId());
                    spiritOrderInfo.setGoodsId(spec.getGoodsId());
                    spiritOrderInfo.setSpecId(spec.getId());
                    spiritOrderInfo.setGoodsNum((Integer) product.get("MaterialQuantity"));
                    spiritOrderInfo.setSubmitState(0);
                    spiritOrderInfo.setOrderShipState(1);
                    shopSpiritOrderInfoService.save(spiritOrderInfo);
                }
            }else {
                productListss.add(product);
            }


            if (product.get("SKU").equals("6972190330202")){//是OLOMI橘油多效清洁剂
                Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                productMap.put("ProducingArea","");
                productMap.put("HSCode","");
                int quantity = (int)product.get("MaterialQuantity");//数量
                productMap.put("MaterialQuantity",quantity);//物品数量
                productMap.put("SKU","6972190330202-1");//物品SKU
                productMap.put("Price",0);//物品价格
                productMap.put("Weight",0);
                productMap.put("EnName","OLOMI橘油多效清洁剂-喷头");//物品名称
                productMap.put("CnName","OLOMI橘油多效清洁剂-喷头");//物品名称

                productListss.add(productMap);
            }

            /*******************************送杯子*******************************************/
            //TODO
            /*if (product.get("SKU").equals("6942098967916") || product.get("SKU").equals("6942098967909")){//是OLOMI 益生菌固体饮料
                if (cupNum==0){
                    Map<String,Object> productMap = new HashMap<String,Object>();//单个商品
                    productMap.put("ProducingArea","");
                    productMap.put("HSCode","");
                    productMap.put("MaterialQuantity",1);//物品数量
                    productMap.put("SKU","6942098967909-1");//物品SKU
                    productMap.put("Price",0);//物品价格
                    productMap.put("Weight",0);
                    productMap.put("EnName","OLOMI摇摇杯");//物品名称
                    productMap.put("CnName","OLOMI摇摇杯");//物品名称

                    productListss.add(productMap);

                    cupNum =1;
                }
            }*/

            //查看是否是特殊快递渠道商品
            String sku = (String)product.get("SKU");
            if (specialGoodsMap.containsKey(sku)){//存在
                eExpressCode = specialGoodsMap.get(sku);
            }

            /************************************预售商品（有的话跳过该订单不发）*********************************************/
            //TODO
            if (presaleAll.size()>0){
                for (ShopGoodsPresale goodsPresale : presaleAll) {
                    if (product.get("SKU").equals(goodsPresale.getSpecGoodsSerial())){
                        Map<String,Object> resultMap = new HashMap<String,Object>();
                        resultMap.put("res","");
                        return resultMap;
                    }
                }
            }

        }
        map.put("ChannelInfoID",eExpressCode);

        map.put("Products",productListss);
/**********************************************************************************************/
        //map.put("Products",productLists);
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(map);

        //发货
        String url = "http://119.23.163.12/webservice/APIWebService.asmx";// 提供接口的地址
        String soapaction = "http://tempuri.org/";// 域名，这是在server定义的
        String res = "";
        try {
            org.apache.axis.client.Service service = new org.apache.axis.client.Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(url);
            call.setOperationName(new QName(soapaction,"AddorUpdateOrders")); // 设置要调用哪个方法
            call.addParameter(new QName(soapaction, "strorderinfo"), // 设置要传递的参数
                    org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter(new QName(soapaction,"secretkey"),
                    org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// （标准的类型）
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(soapaction + "AddorUpdateOrders");
            res = String.valueOf(call.invoke(new Object[] { jsonObject.toString(),secretkey}));// 调用方法并传递参数
            System.out.println(res);
            //{ "success":"success","Info":"订单保存并提交成功!","CsRefNo":"AP353609897201369088","OrderNo":"M102940002962190","TrackingNo":"75165047072640","Enmessage":""}
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("res",res);
        resultMap.put("Products",productLists);
        resultMap.put("orderGoods",orderGoodsList);
        return resultMap;
    }

    /**
     * 自动确认收货
     */
    //@Scheduled(cron = "0/20 * * * * ? ")  //每5秒执行一次
    @Scheduled(cron = "0 33 * * * ? ")  //每隔一小时执行一次 每小时33分执行定时任务
    public void autoReceipt(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-7);
        Date time = calendar.getTime();//当前时间减去七天前时间，该时间大于待收货订单的快递发货时间或者自提订单支付时间，则表示已达到自动收货条件
        //1.找出订单状态为待收货 发货时间和当前系统时间相差超过7天的普通快递订单
        List<ShopOrder> list1=shopOrderDao.findAutoOrder1(time);
        //2.找出订单状态为待收货 支付时间和当前系统时间相差超过7天的自提订单
        //List<ShopOrder> list2=shopOrderDao.findAutoOrder2(time);
        //3.将两个订单进行遍历，依次修改订单状态，判断是否存在零售利润，如果存在零售利润，将自提订单对应产生零售利润设置预计发放时间
        if(list1!=null&&list1.size()>0){
            orderService.autoReceipt1(list1);
        }
        /*if(list2!=null&&list1.size()>0){
            orderService.autoReceipt2(list2);
        }*/
    }

    @Scheduled(cron = "0 15 * * * ? ")  //每隔一小时执行一次 每小时15分执行定时任务
    public void grantRetail(){
        retailProfitService.grantRetail();
    }

    //@Scheduled(cron = "0 8 17 * * ?" )
    public void advance(){
        List<RdTourismCompliance> complianceList = rdTourismComplianceService.findBySql();

        RdTravelTicket rdTravelTicket = rdTravelTicketService.find(6696310439497699328L);
        Long issueNum = rdTravelTicket.getIssueNum();
        if (issueNum==null){
            issueNum = 0l;
        }

        Long total = 0l;
        if (complianceList.size()>0){
            for (RdTourismCompliance rdTourismCompliance : complianceList) {
                RdMmBasicInfo mmBasicInfo = rdMmBasicInfoDao.findByMCode(rdTourismCompliance.getMmCode());

                RdTravelTicketDetail rdTravelTicketDetail = new RdTravelTicketDetail();
                rdTravelTicketDetail.setId(twiterIdService.getTwiterId());
                rdTravelTicketDetail.setTravelId(rdTravelTicket.getId());
                rdTravelTicketDetail.setTravelName(Optional.ofNullable(rdTravelTicket.getTravelName()).orElse(""));
                rdTravelTicketDetail.setTicketPrice(Optional.ofNullable(rdTravelTicket.getTicketPrice()).orElse(BigDecimal.ZERO));
                rdTravelTicketDetail.setTicketSn("T"+twiterIdService.getTwiterId());
                rdTravelTicketDetail.setStatus(0);
                rdTravelTicketDetail.setOwnCode(rdTourismCompliance.getMmCode());
                rdTravelTicketDetail.setOwnNickName(mmBasicInfo.getMmNickName());
                rdTravelTicketDetail.setOwnTime(new Date());
                rdTravelTicketDetail.setImage(Optional.ofNullable(rdTravelTicket.getImage()).orElse(""));

                rdTravelTicketDetailService.save(rdTravelTicketDetail);
                rdTourismCompliance.setOneQualify(2);
                rdTourismCompliance.setOneGrantTime(new Date());
                //rdTourismComplianceService.update(rdTourismCompliance);

                //4.生成通知消息
                ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                shopCommonMessage.setSendUid(mmBasicInfo.getMmCode());
                shopCommonMessage.setType(1);
                shopCommonMessage.setOnLine(1);
                shopCommonMessage.setCreateTime(new Date());
                shopCommonMessage.setBizType(2);
                shopCommonMessage.setIsTop(1);
                shopCommonMessage.setCreateTime(new Date());
                shopCommonMessage.setTitle("会员达标福利，旅游券预发放");
                shopCommonMessage.setContent("为答谢上半年度各位会员朋友对公司的支持与鼓励，现对于已获得500元旅游券的会员，再预借一张500元旅游券用于9月下旬的“贵州文化之旅”。待会员今后获得新的旅游券时再扣回，即现已有500元券的会员，将再发放（预借）一张500元券。报名活动现已开启，欢迎各位会员朋友结伴而行！");
                Long msgId = twiterIdService.getTwiterId();
                shopCommonMessage.setId(msgId);
                shopCommonMessageDao.insert(shopCommonMessage);
                ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                shopMemberMessage.setBizType(2);
                shopMemberMessage.setCreateTime(new Date());
                shopMemberMessage.setId(twiterIdService.getTwiterId());
                shopMemberMessage.setIsRead(0);
                shopMemberMessage.setMsgId(msgId);
                shopMemberMessage.setUid(Long.parseLong(mmBasicInfo.getMmCode()));
                shopMemberMessageDao.insert(shopMemberMessage);

                total = total+1;
            }
            rdTravelTicket.setIssueNum(issueNum+total);
            rdTravelTicketService.update(rdTravelTicket);
        }
        System.out.println("跑完");
    }

}