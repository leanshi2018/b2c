package com.framework.loippi.service.impl.coupon;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

import com.framework.loippi.dao.coupon.CouponPayDetailDao;
import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.entity.coupon.*;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.coupon.*;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.service.wechat.WechatRefundService;
import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.validator.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.coupon.CouponDao;
import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.result.common.coupon.CouponTransferResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.utils.Paramap;

/**
 * 优惠券业务层
 */
@Service
@Transactional
public class CouponServiceImpl extends GenericServiceImpl<Coupon, Long> implements CouponService {
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private WechatRefundService wechatRefundService;
    @Resource
    private WechatMobileRefundService wechatMobileRefundService;
    @Resource
    private AlipayRefundService alipayRefundService;
    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;
    @Resource
    private CouponPayDetailDao couponPayDetailDao;
    @Resource
    private CouponDao couponDao;
    @Resource
    private CouponDetailDao couponDetailDao;
    @Resource
    private CouponUserDao couponUserDao;
    @Resource
    private CouponUserService couponUserService;
    @Resource
    private CouponTransLogService couponTransLogService;
    @Resource
    private CouponDetailService couponDetailService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RdSysPeriodService rdSysPeriodService;

    /**
     *  添加/编辑优惠券
     * @param coupon 优惠券实体类
     * @param username 当前登录用户
     * @return
     */
    @Override
    public Map<String, String> saveOrEditCoupon(Coupon coupon,Long id, String username) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "0");
        String errorMsg = "";
        resultMap.put("msg", errorMsg);
        if(coupon.getReduceType()==3||coupon.getReduceType()==4){//折扣券存储的时候存储小数
            coupon.setCouponValue(coupon.getCouponValue().divide(BigDecimal.TEN,2,BigDecimal.ROUND_HALF_UP));
        }
        if(coupon.getId()!=null){//编辑
            coupon.setUpdateId(id);
            coupon.setUpdateName(username);
            coupon.setUpdateTime(new Date());
            Long falg = couponDao.update(coupon);
            if(falg==1){
                resultMap.put("code", "1");
                return resultMap;
            }
        }else {//新建
            coupon.setCreateId(id);
            coupon.setCreateName(username);
            coupon.setCreateTime(new Date());
            coupon.setReceivedNum(0L);//设置已发放优惠券数量为0
            coupon.setStatus(1);//设置带审核状态
            Long flag = couponDao.insert(coupon);
            if(flag==1){
                resultMap.put("code", "1");
                return resultMap;
            }
        }
        return resultMap;
    }

    /**
     * 优惠券转让
     * @param mmCode 转出人
     * @param mmCode 转出人昵称
     * @param recipientCode 接收人
     * @param coupon 优惠券实体类
     * @param couponUser 转出人拥有优惠券情况
     * @param transNum 转出数量
     * @return
     */
    @Override
    public HashMap<String, Object> transactionCoupon(String mmCode, String mNickName,String recipientCode, Coupon coupon, CouponUser couponUser, Integer transNum) throws Exception{
        HashMap<String, Object> map = new HashMap<>();
        map.put("flag",true);
        map.put("msg","转赠成功");
        RdMmBasicInfo recipienter = rdMmBasicInfoService.find("mmCode",recipientCode);
        if(recipienter==null){
            map.put("flag",false);
            map.put("msg","赠送对象不存在");
            return map;
        }
        List<CouponUser> list = couponUserService.findList(Paramap.create().put("mCode", recipientCode).put("couponId", coupon.getId()));
        CouponUser recipientCouponUser=null;
        Boolean mark = false;
        if(list==null||list.size()==0){
            recipientCouponUser = new CouponUser();
            recipientCouponUser.setMCode(recipientCode);
            recipientCouponUser.setCouponId(coupon.getId());
            recipientCouponUser.setOwnNum(0);
            recipientCouponUser.setHaveCouponNum(0);
            recipientCouponUser.setUseAbleNum(coupon.getUseNumLimit());
            recipientCouponUser.setUseNum(0);
            Long id = twiterIdService.getTwiterId();
            recipientCouponUser.setId(id);
        }else {
            mark=true;
            recipientCouponUser = list.get(0);
        }
        List<CouponDetail> couponDetails = couponDetailService.findList(Paramap.create().put("couponId",coupon.getId()).put("holdId",mmCode)
        .put("useState",2));//属于当前用户，当前种类优惠券，状态处于未使用，且并未退款的优惠券详情集合
        if(couponDetails==null||couponDetails.size()<transNum||couponDetails.size()==0){
            map.put("flag",false);
            map.put("msg","您当前拥有的可转让优惠券不足");
            return map;
        }
        RdSysPeriod sysPeriod = rdSysPeriodService.getPeriodService(new Date());
        String periodCode = "";
        if(sysPeriod!=null){
            periodCode=sysPeriod.getPeriodCode();
        }
        ArrayList<CouponDetail> details = new ArrayList<>();
        ArrayList<CouponTransLog> logs = new ArrayList<>();
        int count=0;
        String serialNum="CT"+twiterIdService.getTwiterId();
        for (CouponDetail couponDetail : couponDetails) {
            if(count<transNum){
                count++;
                //1.修改优惠券详情表
                couponDetail.setHoldId(recipientCode);
                couponDetail.setHoldNickName(recipienter.getMmNickName());
                couponDetail.setHoldTime(new Date());
                details.add(couponDetail);
                //2.生成优惠券转让记录表
                CouponTransLog log = new CouponTransLog();
                log.setId(twiterIdService.getTwiterId());
                log.setTurnId(mmCode);
                log.setTurnNickName(mNickName);
                log.setAcceptId(recipientCode);
                log.setAcceptNickName(recipienter.getMmNickName());
                log.setTransTime(new Date());
                log.setCouponId(coupon.getId());
                log.setCouponName(coupon.getCouponName());
                log.setCouponSn(couponDetail.getCouponSn());
                log.setTransPeriod(periodCode);
                log.setSerialNum(serialNum);
                log.setReceiveId(couponDetail.getReceiveId());
                log.setReceiveNickName(couponDetail.getReceiveNickName());
                logs.add(log);
            }
        }
        //3.修改交易双方CouponUser拥有数量记录
        recipientCouponUser.setOwnNum(recipientCouponUser.getOwnNum()+transNum);
        if(mark){//接收人update操作
            couponUserService.update(recipientCouponUser);
        }else {//接收人insert操作
            couponUserService.save(recipientCouponUser);
        }
        couponUser.setOwnNum(couponUser.getOwnNum()-transNum);
        couponUserService.update(couponUser);
        //4.批量修改修改优惠券详情表
        couponDetailService.updateList(details);
        //5.批量插入优惠券交易日志
        couponTransLogService.insertList(logs);
        CouponTransferResult result = new CouponTransferResult();
        result.setSerialNum(serialNum);
        result.setCouponName(coupon.getCouponName());
        result.setTransNum(transNum);
        result.setResidueNum(couponUser.getOwnNum());
        result.setReceiveCode(recipienter.getMmCode());
        result.setReceiveName(recipienter.getMmName());
        result.setReceiveNickName(recipienter.getMmNickName());
        result.setCouponId(coupon.getId());
        map.put("object",result);
        return map;
    }

    /**
     * 优惠券审核
     * @param coupon 优惠券
     * @param targetStatus 审核状态
     * @param id 审核人id
     * @param username 审核人姓名
     * @throws Exception
     */
    @Override
    public void updateCouponState(Coupon coupon, Integer targetStatus, Long id,String username) throws Exception {
        coupon.setStatus(targetStatus);
        coupon.setUpdateId(id);//设置修改人id
        coupon.setUpdateName(username);//设置修改人姓名
        coupon.setUpdateTime(new Date());
        couponDao.update(coupon);
    }

    /**
     * 查询过期为进行回收的优惠券
     * @param put
     * @return
     */
    @Override
    public List<Coupon> findOverdueCoupon(Paramap put) {
        return couponDao.findOverdueCoupon(put);
    }

    /**
     * 对指定优惠券进行过期处理
     * @param coupon
     * @return
     */
    @Override
    public HashMap<String, Object> shelvesOrOverdueCoupon(Coupon coupon) throws Exception{
        HashMap<String, Object> result = new HashMap<>();
        result.put("flag",true);
        //1.判断优惠券是否使用积分或者金钱购买，如果为购买的优惠券，则需要进行退款操作
        if(coupon.getUseMoneyFlag()==0){//免费
            //1.1免费领取优惠券只需对所有优惠券进行回收，无需退款
            //1.1.1修改coupon
            coupon.setStatus(4);
            coupon.setUpdateName("系统定时任务过期");
            coupon.setUpdateTime(new Date());
            couponDao.update(coupon);
            //1.1.2清空couponUser表
            couponUserDao.overdueCouponUserByCouponId(coupon.getId());
            //1.1.3回收couponDetail
            couponDetailDao.recycleNoMoney(Paramap.create().put("refundState",0).put("refundSum", BigDecimal.ZERO).put("couponId",coupon.getId()));
        }else if(coupon.getUseMoneyFlag()==1){//付费
            CouponDetail couponDetail1 = new CouponDetail();
            couponDetail1.setCouponId(coupon.getId());
            couponDetail1.setUseState(2);
            Long num=couponDetailDao.getNoUseNum(couponDetail1);
            coupon.setRefundNum((int) (Optional.ofNullable(coupon.getRefundNum()).orElse(0)+num));
            //1.2对未使用的优惠券进行退款 退款路径原路返回
            //1.2.1修改coupon
            coupon.setStatus(4);
            couponDao.update(coupon);
            //1.2.2查询出当前优惠券对应的优惠券订单
            List<CouponPayDetail> details = couponPayDetailDao.findByParams(Paramap.create().put("couponId",coupon.getId()).put("paymentState",1)
                    .put("couponOrderState",40));
            if(details!=null&&details.size()>0){//当前过期优惠券有已完成订单
                for (CouponPayDetail couponPayDetail : details) {
                    //根据购买编号，查询尚未使用的优惠券明细，统计张数，购买人退款，持有人couponUser拥有数量修改
                    List<CouponDetail> couponDetails = couponDetailDao.findByParams(Paramap.create().put("couponId",coupon.getId())
                            .put("useState",2).put("buyOrderId",couponPayDetail.getId()));
                    if(couponDetails!=null&&couponDetails.size()>0){//当前订单有未使用的优惠券
                        //1.2.2.1 统计数量，将金额退还给领取人
                        int couponNum = couponDetails.size();
                        String paymentCode = "";
                        if (couponPayDetail.getPaymentId()==6){
                            paymentCode = "pointsPaymentPlugin";
                        }else{
                            //不是积分支付
                            TSystemPluginConfig pluginConfig = tSystemPluginConfigService.find(couponPayDetail.getPaymentId());
                            if (pluginConfig!=null){
                                paymentCode = pluginConfig.getPluginId();
                            }
                        }
                        if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
                            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                            AliPayRefund aliPayRefund = new AliPayRefund();
                            //支付宝交易号 ，退款金额，退款理由
                            aliPayRefund.setRefundAmountNum(couponNum);//退款数量，目前是单笔退款
                            aliPayRefund.setBatchNo(bathno);
                            aliPayRefund.setTradeNo(couponPayDetail.getPaySn());
                            aliPayRefund.setRefundAmount(coupon.getCouponPrice().multiply(new BigDecimal(couponNum)));
                            //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                            aliPayRefund.setRRefundReason("优惠券订单退款");
                            aliPayRefund.setDetaildata(couponPayDetail.getTradeSn(),coupon.getCouponPrice(),"优惠券订单退款");
                            //跳到支付宝退款接口
                            String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
                            if ("true".equals(sHtmlText)) {
                                //保存批次号和修改订单数据
                                updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                            }
                        }
                        else if (paymentCode.equals("weixinMobilePaymentPlugin")) {//微信开放平台支付

                            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                            WeiRefund weiRefund = new WeiRefund();
                            weiRefund.setOutrefundno(bathno);//微信交易号
                            weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                            weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                            weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* couponNum * 100));
                            //weiRefund.setRefundfee(1);
                            //weiRefund.setTotalfee(1);
                            //跳到微信退款接口
                            //toweichatrefund();
                            Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
                            String msg = "";
                            if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                                //保存批次号和修改订单数据
                                updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                            }
                        }
                        else if (paymentCode.equals("weixinH5PaymentPlugin")) {//微信公共平台支付
                            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                            WeiRefund weiRefund = new WeiRefund();
                            weiRefund.setOutrefundno(bathno);//微信交易号
                            weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                            weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                            weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* couponNum * 100));
                            //              weiRefund.setRefundfee((int) (0.01 * 100));
                            //跳到微信退款接口
                            //backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);

                            Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
                            String msg = "";
                            if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                                //保存批次号和修改订单数据
                                updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                            }
                        }
                        else if (paymentCode.equals("pointsPaymentPlugin")) {

                            String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                            //把积分退还给用户
                            String mCode = couponPayDetail.getReceiveId();
                            RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponPayDetail.getReceiveId());
                            if (rdMmAccountInfo!=null){
                                //更新用户购物积分
                                RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                rdMmAccountLog.setTransTypeCode("OT");
                                rdMmAccountLog.setAccType("SWB");
                                rdMmAccountLog.setTrSourceType("SWB");
                                rdMmAccountLog.setMmCode(couponPayDetail.getReceiveId());
                                rdMmAccountLog.setMmNickName(couponPayDetail.getReceiveNickName());
                                rdMmAccountLog.setTrMmCode(couponPayDetail.getReceiveId());
                                rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
                                //所需积分
                                BigDecimal pricePoint = (couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(couponNum));
                                rdMmAccountLog.setAmount(pricePoint);
                                rdMmAccountLog.setTransDate(new Date());
                                String period = rdSysPeriodService.getSysPeriodService(new Date());
                                rdMmAccountLog.setTransPeriod(period);
                                rdMmAccountLog.setTrOrderOid(couponPayDetail.getId());
                                //无需审核直接成功
                                rdMmAccountLog.setStatus(3);
                                rdMmAccountLog.setCreationBy(coupon.getUpdateName());
                                rdMmAccountLog.setCreationTime(new Date());
                                rdMmAccountLog.setAutohrizeBy(coupon.getUpdateName());
                                rdMmAccountLog.setAutohrizeTime(new Date());
                                rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
                                rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
                                rdMmAccountInfoService.update(rdMmAccountInfo);
                                rdMmAccountLogService.save(rdMmAccountLog);

                                //保存批次号和修改订单数据
                                //updateCouponDetailList(couponDetailId,bathno,coupon,couponPayDetail);
                                for (CouponDetail couponDetail : couponDetails) {
                                    if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                                        //未使用且未退款
                                        couponDetail.setUseState(3);
                                        couponDetail.setRefundState(2);
                                        couponDetail.setRefundSum(couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP));
                                        couponDetail.setBatchNo(bathno); //退款批次号
                                        couponDetail.setRefundTime(new Date());
                                        couponDetailService.update(couponDetail);//将批次号存入优惠券表
                                        List<CouponUser> couponUsers = couponUserDao.findByMMCodeAndCouponId(Paramap.create().put("mCode", couponDetail.getHoldId()).put("couponId", couponDetail.getCouponId()));
                                        if(couponUsers!=null&&couponUsers.size()>0){
                                            CouponUser couponUser = couponUsers.get(0);
                                            couponUser.setOwnNum(couponUser.getOwnNum()-1);
                                            couponUserDao.update(couponUser);
                                        }
                                    }
                                }

                                if ((couponPayDetail.getRefundCouponNum()+couponNum)==couponPayDetail.getCouponNumber()){
                                    couponPayDetail.setRefundState(2);
                                }else{
                                    couponPayDetail.setRefundState(1);
                                }
                                couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+couponNum);
                                couponPayDetail.setBatchNo(bathno);
                                couponPayDetail.setRefundTime(new Date());
                                couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
                                couponPayDetailDao.update(couponPayDetail);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void addCoupon(Coupon coupon, String mmCode) {
        RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode",mmCode);

        Map<String, Object> couponUserMap = new HashMap<>();
        couponUserMap.put("mCode",mmCode);
        couponUserMap.put("couponId",coupon.getId());
        List<CouponUser> couponUserList = couponUserDao.findByMMCodeAndCouponId(couponUserMap);
        if (couponUserList.size()==0){
            CouponUser couponUser = new CouponUser();
            couponUser.setId(twiterIdService.getTwiterId());
            couponUser.setMCode(mmCode);
            couponUser.setCouponId(coupon.getId());
            couponUser.setHaveCouponNum(1);
            couponUser.setOwnNum(1);
            couponUser.setUseAbleNum(coupon.getUseNumLimit());
            couponUser.setUseNum(0);
            couponUserDao.insert(couponUser);
            List<CouponUser> couponUsers = couponUserDao.findByMMCodeAndCouponId(couponUserMap);

            //生成优惠券详情表
            CouponDetail couponDetail = new CouponDetail();
            couponDetail.setId(twiterIdService.getTwiterId());
            couponDetail.setRdCouponUserId(couponUsers.get(0).getId());
            couponDetail.setCouponId(coupon.getId());
            couponDetail.setCouponSn("YH"+twiterIdService.getTwiterId());
            couponDetail.setCouponName(coupon.getCouponName());
            couponDetail.setReceiveId(mmCode);
            couponDetail.setReceiveNickName(member.getMmNickName());
            couponDetail.setReceiveTime(new Date());
            couponDetail.setHoldId(mmCode);
            couponDetail.setHoldNickName(member.getMmNickName());
            couponDetail.setHoldTime(new Date());
            couponDetail.setUseStartTime(coupon.getUseStartTime());
            couponDetail.setUseEndTime(coupon.getUseEndTime());
            couponDetail.setUseState(2);
            couponDetail.setRefundState(0);
            couponDetailDao.insert(couponDetail);

        }else {
            for (CouponUser couponUser : couponUserList) {
                couponUser.setHaveCouponNum(couponUser.getHaveCouponNum()+1);
                couponUser.setOwnNum(couponUser.getOwnNum()+1);
                couponUserDao.update(couponUser);
                //生成优惠券详情表
                CouponDetail couponDetail = new CouponDetail();
                couponDetail.setId(twiterIdService.getTwiterId());
                couponDetail.setRdCouponUserId(couponUser.getId());
                couponDetail.setCouponId(coupon.getId());
                couponDetail.setCouponSn("YH"+twiterIdService.getTwiterId());
                couponDetail.setCouponName(coupon.getCouponName());
                couponDetail.setReceiveId(mmCode);
                couponDetail.setReceiveNickName(member.getMmNickName());
                couponDetail.setReceiveTime(new Date());
                couponDetail.setHoldId(mmCode);
                couponDetail.setHoldNickName(member.getMmNickName());
                couponDetail.setHoldTime(new Date());
                couponDetail.setUseStartTime(coupon.getUseStartTime());
                couponDetail.setUseEndTime(coupon.getUseEndTime());
                couponDetail.setUseState(2);
                couponDetail.setRefundState(0);
                couponDetailDao.insert(couponDetail);
            }
        }
        //修改优惠券剩余数量
        coupon.setReceivedNum(coupon.getReceivedNum()+1);
        couponDao.update(coupon);

    }

    @Override
    public Coupon judgeNoStart(Paramap put) {
        return couponDao.judgeNoStart(put);
    }

    @Override
    public Coupon judgeUseEnd(Paramap put) {
        return couponDao.judgeUseEnd(put);
    }

    /**
     * 定时任务查出已过期优惠券进行过期
     *
     */
    @Override
    public void overCoupon() {
        List<Coupon> coupons = couponDao.findOverdueCoupon(Paramap.create().put("status",2).put("searchUseTime",new Date()));
        if(coupons!=null&&coupons.size()>0){
            for (Coupon coupon : coupons) {//获得过期优惠券集合，进行遍历回收处理
                //1.判断优惠券是否使用积分或者金钱购买，如果为购买的优惠券，则需要进行退款操作
                if(coupon.getUseMoneyFlag()==0){//免费
                    //1.1免费领取优惠券只需对所有优惠券进行回收，无需退款
                    //1.1.1修改coupon
                    coupon.setStatus(4);
                    coupon.setUpdateName("系统定时任务过期");
                    coupon.setUpdateTime(new Date());
                    couponDao.update(coupon);
                    //1.1.2清空couponUser表
                    couponUserDao.overdueCouponUserByCouponId(coupon.getId());
                    //1.1.3回收couponDetail
                    couponDetailDao.recycleNoMoney(Paramap.create().put("refundState",0).put("refundSum", BigDecimal.ZERO).put("couponId",coupon.getId()));
                }else if(coupon.getUseMoneyFlag()==1){//付费
                    CouponDetail couponDetail1 = new CouponDetail();
                    couponDetail1.setCouponId(coupon.getId());
                    couponDetail1.setUseState(2);
                    Long num=couponDetailDao.getNoUseNum(couponDetail1);
                    coupon.setRefundNum((int) (Optional.ofNullable(coupon.getRefundNum()).orElse(0)+num));
                    //1.2对未使用的优惠券进行退款 退款路径原路返回
                    //1.2.1修改coupon
                    coupon.setStatus(4);
                    coupon.setUpdateName("系统定时任务过期");
                    coupon.setUpdateTime(new Date());
                    couponDao.update(coupon);
                    //1.2.2查询出当前优惠券对应的优惠券订单
                    List<CouponPayDetail> details = couponPayDetailDao.findByParams(Paramap.create().put("couponId",coupon.getId()).put("paymentState",1)
                            .put("couponOrderState",40));
                    if(details!=null&&details.size()>0){//当前过期优惠券有已完成订单
                        for (CouponPayDetail couponPayDetail : details) {
                            //根据购买编号，查询尚未使用的优惠券明细，统计张数，购买人退款，持有人couponUser拥有数量修改
                            List<CouponDetail> couponDetails = couponDetailDao.findByParams(Paramap.create().put("couponId",coupon.getId())
                                    .put("useState",2).put("buyOrderId",couponPayDetail.getId()));
                            if(couponDetails!=null&&couponDetails.size()>0){//当前订单有未使用的优惠券
                                //1.2.2.1 统计数量，将金额退还给领取人
                                int couponNum = couponDetails.size();
                                String paymentCode = "";
                                if (couponPayDetail.getPaymentId()==6){
                                    paymentCode = "pointsPaymentPlugin";
                                }else{
                                    //不是积分支付
                                    TSystemPluginConfig pluginConfig = tSystemPluginConfigService.find(couponPayDetail.getPaymentId());
                                    if (pluginConfig!=null){
                                        paymentCode = pluginConfig.getPluginId();
                                    }
                                }
                                if (paymentCode.equals("alipayMobilePaymentPlugin")) {//支付宝退款
                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                                    AliPayRefund aliPayRefund = new AliPayRefund();
                                    //支付宝交易号 ，退款金额，退款理由
                                    aliPayRefund.setRefundAmountNum(couponNum);//退款数量，目前是单笔退款
                                    aliPayRefund.setBatchNo(bathno);
                                    aliPayRefund.setTradeNo(couponPayDetail.getPaySn());
                                    aliPayRefund.setRefundAmount(coupon.getCouponPrice().multiply(new BigDecimal(couponNum)));
                                    //aliPayRefund.setRefundAmount(new BigDecimal(0.01));
                                    aliPayRefund.setRRefundReason("优惠券订单退款");
                                    aliPayRefund.setDetaildata(couponPayDetail.getTradeSn(),coupon.getCouponPrice(),"优惠券订单退款");
                                    //跳到支付宝退款接口
                                    String sHtmlText = alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
                                    if ("true".equals(sHtmlText)) {
                                        //保存批次号和修改订单数据
                                        updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                                    }
                                }
                                else if (paymentCode.equals("weixinMobilePaymentPlugin")) {//微信开放平台支付

                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                                    WeiRefund weiRefund = new WeiRefund();
                                    weiRefund.setOutrefundno(bathno);//微信交易号
                                    weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                                    weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                                    weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* couponNum * 100));
                                    //weiRefund.setRefundfee(1);
                                    //weiRefund.setTotalfee(1);
                                    //跳到微信退款接口
                                    //toweichatrefund();
                                    Map<String, Object> map = wechatMobileRefundService.toRefund(weiRefund);
                                    String msg = "";
                                    if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                                        //保存批次号和修改订单数据
                                        updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                                    }
                                }
                                else if (paymentCode.equals("weixinH5PaymentPlugin")) {//微信公共平台支付
                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();

                                    WeiRefund weiRefund = new WeiRefund();
                                    weiRefund.setOutrefundno(bathno);//微信交易号
                                    weiRefund.setOuttradeno(couponPayDetail.getPaySn());//订单号
                                    weiRefund.setTotalfee((int) ((couponPayDetail.getOrderAmount().doubleValue()) * 100));//单位，整数微信里以分为单位
                                    weiRefund.setRefundfee((int) ((coupon.getCouponPrice().doubleValue())* couponNum * 100));
                                    //              weiRefund.setRefundfee((int) (0.01 * 100));
                                    //跳到微信退款接口
                                    //backurl = toweichatrefund(weiRefund, id, adminMessage, "mp_weichatpay", model, request);

                                    Map<String, Object> map = wechatRefundService.toRefund(weiRefund);
                                    String msg = "";
                                    if (map.size() != 0 && map.get("result_code").equals("SUCCESS")) {
                                        //保存批次号和修改订单数据
                                        updateCouponDetailList(couponPayDetail,bathno,coupon,couponDetails,couponNum);
                                    }
                                }
                                else if (paymentCode.equals("pointsPaymentPlugin")) {

                                    String bathno = DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber();
                                    //把积分退还给用户
                                    String mCode = couponPayDetail.getReceiveId();
                                    RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", couponPayDetail.getReceiveId());
                                    if (rdMmAccountInfo!=null){
                                        //更新用户购物积分
                                        RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                        rdMmAccountLog.setTransTypeCode("OT");
                                        rdMmAccountLog.setAccType("SWB");
                                        rdMmAccountLog.setTrSourceType("SWB");
                                        rdMmAccountLog.setMmCode(couponPayDetail.getReceiveId());
                                        rdMmAccountLog.setMmNickName(couponPayDetail.getReceiveNickName());
                                        rdMmAccountLog.setTrMmCode(couponPayDetail.getReceiveId());
                                        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
                                        //所需积分
                                        BigDecimal pricePoint = (couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP)).multiply(new BigDecimal(couponNum));
                                        rdMmAccountLog.setAmount(pricePoint);
                                        rdMmAccountLog.setTransDate(new Date());
                                        String period = rdSysPeriodService.getSysPeriodService(new Date());
                                        rdMmAccountLog.setTransPeriod(period);
                                        rdMmAccountLog.setTrOrderOid(couponPayDetail.getId());
                                        //无需审核直接成功
                                        rdMmAccountLog.setStatus(3);
                                        //rdMmAccountLog.setCreationBy(adminName);
                                        rdMmAccountLog.setCreationTime(new Date());
                                        //rdMmAccountLog.setAutohrizeBy(adminName);
                                        rdMmAccountLog.setAutohrizeTime(new Date());
                                        rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().add(pricePoint));
                                        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
                                        rdMmAccountInfoService.update(rdMmAccountInfo);
                                        rdMmAccountLogService.save(rdMmAccountLog);

                                        //保存批次号和修改订单数据
                                        //updateCouponDetailList(couponDetailId,bathno,coupon,couponPayDetail);
                                        for (CouponDetail couponDetail : couponDetails) {
                                            if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                                                //未使用且未退款
                                                couponDetail.setUseState(3);
                                                couponDetail.setRefundState(2);
                                                couponDetail.setRefundSum(couponPayDetail.getUsePointNum().divide(new BigDecimal(couponPayDetail.getCouponNumber()),0,BigDecimal.ROUND_HALF_UP));
                                                couponDetail.setBatchNo(bathno); //退款批次号
                                                couponDetail.setRefundTime(new Date());
                                                couponDetailService.update(couponDetail);//将批次号存入优惠券表
                                                List<CouponUser> couponUsers = couponUserDao.findByMMCodeAndCouponId(Paramap.create().put("mCode", couponDetail.getHoldId()).put("couponId", couponDetail.getCouponId()));
                                                if(couponUsers!=null&&couponUsers.size()>0){
                                                    CouponUser couponUser = couponUsers.get(0);
                                                    couponUser.setOwnNum(couponUser.getOwnNum()-1);
                                                    couponUserDao.update(couponUser);
                                                }
                                            }
                                        }

                                        if ((couponPayDetail.getRefundCouponNum()+couponNum)==couponPayDetail.getCouponNumber()){
                                            couponPayDetail.setRefundState(2);
                                        }else{
                                            couponPayDetail.setRefundState(1);
                                        }
                                        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+couponNum);
                                        couponPayDetail.setBatchNo(bathno);
                                        couponPayDetail.setRefundTime(new Date());
                                        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(pricePoint));
                                        couponPayDetailDao.update(couponPayDetail);
                                    }
                                }
                            }
                        }
                    }
                }else {
                    continue;
                }
            }
        }
    }
    public void updateCouponDetailList(CouponPayDetail couponPayDetail, String bathno, Coupon coupon, List<CouponDetail> couponDetailList,Integer refundNum) {

        if ((couponPayDetail.getRefundCouponNum()+refundNum)==couponPayDetail.getCouponNumber()){
            couponPayDetail.setRefundState(2);
        }else{
            couponPayDetail.setRefundState(1);
        }
        couponPayDetail.setRefundCouponNum(couponPayDetail.getRefundCouponNum()+refundNum);
        couponPayDetail.setBatchNo(bathno);
        couponPayDetail.setRefundTime(new Date());
        couponPayDetail.setRefundAmount(couponPayDetail.getRefundAmount().add(coupon.getCouponPrice().multiply(new BigDecimal(refundNum))));
        couponPayDetailDao.update(couponPayDetail);

        for (CouponDetail couponDetail : couponDetailList) {
            if (couponDetail.getUseState()==2 && couponDetail.getRefundState()==1){
                //未使用且未退款
                couponDetail.setUseState(3);
                couponDetail.setRefundState(2);
                couponDetail.setRefundSum(coupon.getCouponPrice());
                couponDetail.setBatchNo(bathno); //退款批次号
                couponDetail.setRefundTime(new Date());
                couponDetailService.update(couponDetail);//将批次号存入优惠券表
                List<CouponUser> couponUsers = couponUserDao.findByMMCodeAndCouponId(Paramap.create().put("mCode", couponDetail.getHoldId()).put("couponId", couponDetail.getCouponId()));
                if(couponUsers!=null&&couponUsers.size()>0){
                    CouponUser couponUser = couponUsers.get(0);
                    couponUser.setOwnNum(couponUser.getOwnNum()-1);
                    couponUserDao.update(couponUser);
                }
            }
        }
    }
}
