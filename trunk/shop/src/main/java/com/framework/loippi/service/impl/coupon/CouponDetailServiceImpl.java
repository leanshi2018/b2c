package com.framework.loippi.service.impl.coupon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.framework.loippi.dao.coupon.CouponDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.travel.RdTicketSendLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.travel.RdTicketSendLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.common.coupon.CouponUserLogResult;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

/**
 * 优惠券详情service层
 */
@Service
@Transactional
public class CouponDetailServiceImpl extends GenericServiceImpl<CouponDetail, Long> implements CouponDetailService {
    @Resource
    private  CouponDetailDao couponDetailDao;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private CouponUserService couponUserService;
    @Resource
    private CouponDao couponDao;
    @Resource
    private RdTicketSendLogService rdTicketSendLogService;

    @Override
    public void updateList(ArrayList<CouponDetail> details) {
        couponDetailDao.updateList(details);
    }

    @Override
    public Object findLogResultByPage(Pageable pageable) {

        PageList<CouponUserLogResult> result = couponDetailDao.findLogResultByPage(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    /**
     * 非金额支付优惠券过期下架回收
     * @param paramap
     */
    @Override
    public void recycleNoMoney(Paramap paramap) {
        couponDetailDao.recycleNoMoney(paramap);
    }

    @Override
    public List<CouponDetail> findListByBuyOrderId(Long buyOrderId) {
        return couponDetailDao.findListByBuyOrderId(buyOrderId);
    }

    /**
     * 后台管理人员发放优惠券
     * @param coupon 优惠券
     * @param num 张数
     * @param basicInfo 会员基础信息
     * @param remark 备注
     * @param username 操作人
     */
    @Override
    public void sendCouponTicket(Coupon coupon, Integer num, RdMmBasicInfo basicInfo, String remark, String username) {
        List<CouponUser> list = couponUserService.findList(Paramap.create().put("mCode",basicInfo.getMmCode()).put("couponId",coupon.getId()));
        //1.修改会员优惠券拥有记录表
        CouponUser couponUser=null;
        Long twiterId = twiterIdService.getTwiterId();
        if(list==null||list.size()==0){
            couponUser = new CouponUser();
            couponUser.setId(twiterId);
            couponUser.setMCode(basicInfo.getMmCode());
            couponUser.setCouponId(coupon.getId());
            couponUser.setHaveCouponNum(0);
            couponUser.setOwnNum(num);
            couponUser.setUseAbleNum(coupon.getUseNumLimit());
            couponUser.setUseNum(0);
            couponUserService.save(couponUser);
        }else {
            couponUser = list.get(0);
            couponUser.setOwnNum(couponUser.getOwnNum()+num);
            couponUserService.update(couponUser);
        }
        //2.发放优惠券
        for (int i = 0; i < num; i++) {
            CouponDetail couponDetail = new CouponDetail();
            couponDetail.setId(twiterIdService.getTwiterId());
            couponDetail.setRdCouponUserId(twiterId);
            couponDetail.setCouponId(coupon.getId());
            couponDetail.setCouponSn("YH"+twiterIdService.getTwiterId());
            couponDetail.setCouponName(coupon.getCouponName());
            couponDetail.setReceiveId(basicInfo.getMmCode());
            couponDetail.setReceiveNickName(basicInfo.getMmNickName());
            couponDetail.setReceiveTime(new Date());
            couponDetail.setHoldId(basicInfo.getMmCode());
            couponDetail.setHoldNickName(basicInfo.getMmNickName());
            couponDetail.setHoldTime(new Date());
            couponDetail.setUseStartTime(coupon.getUseStartTime());
            couponDetail.setUseEndTime(coupon.getUseEndTime());
            couponDetail.setUseState(2);
            couponDetailDao.insert(couponDetail);
        }
        //3.如果优惠券有发放数量限制，修改优惠券已发放数量
        if(coupon.getTotalLimitNum()!=null&&coupon.getTotalLimitNum()!=-1){
            coupon.setReceivedNum(coupon.getReceivedNum()+num);
            couponDao.update(coupon);
        }
        //4.生成发放日志
        RdTicketSendLog log = new RdTicketSendLog();
        log.setId(twiterIdService.getTwiterId());
        log.setTicketType(1);
        log.setTicketId(coupon.getId());
        log.setTicketName(coupon.getCouponName());
        log.setMmCode(basicInfo.getMmCode());
        log.setMmNickName(basicInfo.getMmNickName());
        log.setNum(num);
        log.setOperationCode(username);
        log.setSendTime(new Date());
        if(remark!=null&&!"".equals(remark)){
            log.setRemark(remark);
        }
        rdTicketSendLogService.save(log);
    }
}
