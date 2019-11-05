package com.framework.loippi.service.impl.coupon;

import com.framework.loippi.dao.coupon.CouponDao;
import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponTransLogService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.utils.Paramap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 优惠券业务层
 */
@Service
@Transactional
public class CouponServiceImpl extends GenericServiceImpl<Coupon, Long> implements CouponService {

    @Resource
    private CouponDao couponDao;
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
        }
        List<CouponUser> list = couponUserService.findList(Paramap.create().put("mCode", recipientCode).put("couponId", coupon.getId()));
        CouponUser recipientCouponUser=null;
        Boolean mark = false;
        if(list==null||list.size()==0){
            recipientCouponUser = new CouponUser();
            recipientCouponUser.setMCode(recipientCode);
            recipientCouponUser.setCouponId(coupon.getId());
            recipientCouponUser.setOwnNum(0);
            recipientCouponUser.setUseAbleNum(coupon.getUseNumLimit());
            recipientCouponUser.setUseNum(0);
            Long id = twiterIdService.getTwiterId();
            recipientCouponUser.setId(id);
        }else {
            mark=true;
            recipientCouponUser = list.get(0);
        }
        List<CouponDetail> couponDetails = couponDetailService.findList(Paramap.create().put("couponId",coupon.getId()).put("holdId",mmCode)
        .put("useState",2).put("refundState",1));//属于当前用户，当前种类优惠券，状态处于未使用，且并未退款的优惠券详情集合
        if(couponDetails==null||couponDetails.size()<transNum){
            map.put("flag",false);
            map.put("msg","您当前拥有的可转让优惠券不足");
        }
        RdSysPeriod sysPeriod = rdSysPeriodService.getPeriodService(new Date());
        String periodCode = "";
        if(sysPeriod!=null){
            periodCode=sysPeriod.getPeriodCode();
        }
        ArrayList<CouponDetail> details = new ArrayList<>();
        ArrayList<CouponTransLog> logs = new ArrayList<>();
        int count=0;
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
}
