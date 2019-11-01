package com.framework.loippi.service.impl.coupon;

import com.framework.loippi.dao.coupon.CouponDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 优惠券业务层
 */
@Service
public class CouponServiceImpl extends GenericServiceImpl<Coupon, Long> implements CouponService {

    @Resource
    private CouponDao couponDao;

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
     * 优惠券审核
     * @param coupon
     * @param targetStatus
     * @param user
     * @throws Exception
     */
    /*@Override
    public void updateCouponState(Coupon coupon, Integer targetStatus, Principal user) throws Exception {
        coupon.setStatus(targetStatus);
        coupon.setUpdateId(Optional.ofNullable(user.getId()).orElse(0L));//设置修改人id
        coupon.setUpdateName(Optional.ofNullable(user.getUsername()).orElse(""));//设置修改人姓名
        coupon.setUpdateTime(new Date());
        couponDao.update(coupon);
    }*/
}
