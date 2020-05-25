package com.framework.loippi.service.impl.common;

import com.framework.loippi.dao.common.MemberShippingBehaviorDao;
import com.framework.loippi.entity.common.MemberShippingBehavior;
import com.framework.loippi.pojo.common.MemberShippingBehaviorVo;
import com.framework.loippi.service.common.MemberShippingBehaviorService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.utils.Paramap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@Transactional
public class MemberShippingBehaviorServiceImpl extends GenericServiceImpl<MemberShippingBehavior, Long> implements MemberShippingBehaviorService {
    @Resource
    private MemberShippingBehaviorDao memberShippingBehaviorDao;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private ShopOrderService shopOrderService;

    /**
     * 定时任务执行生成每日新老会员购买行为统计
     */
    @Override
    public void getMemberShippingBehavior() {
        //1.获取昨日时间确定reportCode
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Date time = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String reportCode = format.format(time);
        HashMap<String, Object> map = new HashMap<>();
        String start=reportCode+" 00:00:00";
        String end=reportCode+" 23:59:59";
        map.put("start",start);
        map.put("end",end);
        //2.查询当日是否已经存在记录
        List<MemberShippingBehavior> list = memberShippingBehaviorDao.findByParams(Paramap.create().put("reportCode", reportCode));
        if(list!=null&&list.size()>0){
            System.out.println(reportCode+"日已存在记录");
            return;
        }
        //3.生成记录
        MemberShippingBehavior behavior = new MemberShippingBehavior();
        behavior.setReportCode(reportCode);
        behavior.setStatisticalTime(new Date());
        //分别查询出老会员总数和已经升级为vip会员的新会员
        Long newVip=rdMmRelationService.findNewVipRankMoreOne();
        int intValue = newVip.intValue();
        List<MemberShippingBehaviorVo> voList=shopOrderService.findNewShippingBehavior();
        if(voList!=null&&voList.size()>0){
            Integer zero=0;
            Integer one=0;
            Integer twoMore=0;
            for (MemberShippingBehaviorVo memberShippingBehaviorVo : voList) {
                if(memberShippingBehaviorVo.getOrderNum()==1){
                    one++;
                }else {
                    twoMore++;
                }
            }
            zero=intValue-one-twoMore;
            behavior.setNewVipBuyZero(zero);
            behavior.setNewVipBuyOne(one);
            behavior.setNewVipBuyTwomore(twoMore);
            behavior.setBuyZeroRate(new BigDecimal(Integer.toString(zero)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(intValue)),2,BigDecimal.ROUND_HALF_UP));
            behavior.setBuyOneRate(new BigDecimal(Integer.toString(one)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(intValue)),2,BigDecimal.ROUND_HALF_UP));
            behavior.setBuyTwomoreRate(new BigDecimal(Integer.toString(twoMore)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(intValue)),2,BigDecimal.ROUND_HALF_UP));
        }else {
            behavior.setNewVipBuyZero(intValue);
            behavior.setNewVipBuyOne(0);
            behavior.setNewVipBuyTwomore(0);
            behavior.setBuyZeroRate(new BigDecimal("100.00"));
            behavior.setBuyOneRate(new BigDecimal("0.00"));
            behavior.setBuyTwomoreRate(new BigDecimal("0.00"));
        }
        Long oldMember=rdMmRelationService.count(Paramap.create().put("nOFlag",2));
        List<MemberShippingBehaviorVo> voList2=shopOrderService.findOldShippingBehavior();
        if(voList2!=null&&voList2.size()>0){
            Integer oldZero=0;
            Integer oldOne=0;
            Integer oldTwoMore=0;
            for (MemberShippingBehaviorVo memberShippingBehaviorVo : voList2) {
                if(memberShippingBehaviorVo.getOrderNum()==1){
                    oldOne++;
                }else {
                    oldTwoMore++;
                }
            }
            oldZero=oldMember.intValue()-oldOne-oldTwoMore;
            behavior.setOldMemBuyZero(oldZero);
            behavior.setOldMemBuyOne(oldOne);
            behavior.setOldMemBuyTwomore(oldTwoMore);
            behavior.setOldBuyZeroRate(new BigDecimal(Integer.toString(oldZero)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(oldMember.intValue())),2,BigDecimal.ROUND_HALF_UP));
            behavior.setOldBuyOneRate(new BigDecimal(Integer.toString(oldOne)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(oldMember.intValue())),2,BigDecimal.ROUND_HALF_UP));
            behavior.setOldBuyTwomoreRate(new BigDecimal(Integer.toString(oldTwoMore)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Integer.toString(oldMember.intValue())),2,BigDecimal.ROUND_HALF_UP));
        }else {
            behavior.setOldMemBuyZero(oldMember.intValue());
            behavior.setOldMemBuyOne(0);
            behavior.setOldMemBuyTwomore(0);
            behavior.setOldBuyZeroRate(new BigDecimal("100.00"));
            behavior.setOldBuyOneRate(new BigDecimal("0.00"));
            behavior.setOldBuyTwomoreRate(new BigDecimal("0.00"));
        }
        //4.写入数据
        memberShippingBehaviorDao.insert(behavior);
    }
}
