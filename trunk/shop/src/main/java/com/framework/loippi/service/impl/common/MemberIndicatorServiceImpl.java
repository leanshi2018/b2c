package com.framework.loippi.service.impl.common;

import com.framework.loippi.dao.common.MemberIndicatorDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.entity.common.MemberIndicator;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.pojo.common.MemIndicatorVo;
import com.framework.loippi.service.common.MemberIndicatorService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.utils.Paramap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员指标
 */
@Service
@Slf4j
@Transactional
public class MemberIndicatorServiceImpl extends GenericServiceImpl<MemberIndicator, Long> implements MemberIndicatorService {
    @Resource
    private RdSysPeriodService rdSysPeriodService;
    @Resource
    private MemberIndicatorDao memberIndicatorDao;
    @Resource
    private ShopOrderDao shopOrderDao;
    @Resource
    private RdMmRelationDao rdMmRelationDao;

    /**
     * 统计会员周期指标
     */
    @Override
    public void getMemberIndicator(){
        System.out.println("修改");
        //1.查询是否存在正常使用的会员指标 如果有，作废掉
        //SimpleDateFormat format5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*try {*/
            //Date parse = format5.parse("2021-02-01 00:15:00");
        /*} catch (ParseException e) {
            e.printStackTrace();
        }*/
        RdSysPeriod sysPeriod = rdSysPeriodService.getPeriodService(new Date());
        //RdSysPeriod sysPeriod = rdSysPeriodService.getPeriodService(parse);
        if(sysPeriod==null){//如果当前时间没有业务周期，结束方法
            return;
        }
        if(sysPeriod.getPeriodCode().equals("201901")){
            return;
        }
        String periodCode = sysPeriod.getPrePeriod();//查询上一个周期周期编号
        List<MemberIndicator> params = memberIndicatorDao.findByParams(Paramap.create().put("periodCode", periodCode).put("status", 1));
        if(params!=null&&params.size()>0){
            for (MemberIndicator param : params) {
                param.setUpdateTime(new Date());
                param.setStatus(2);
                memberIndicatorDao.update(param);
            }
        }
        //2.根据周期编号重新统计会员指标
        MemberIndicator indicator = new MemberIndicator();
        indicator.setPeriodCode(periodCode);
        indicator.setStatisticalTime(new Date());
        //indicator.setStatisticalTime(parse);
        Long memberNumTotal=rdMmRelationDao.findMemEffective();
        indicator.setMemberNumTotal(memberNumTotal);
        indicator.setStartNum(0L);//TODO 当前版本不涉及统计会员登录次数
        List<MemIndicatorVo> list=shopOrderDao.findMemIndicatorVo(periodCode);
        ArrayList<String> periodList = new ArrayList<>();
        if(list!=null&&list.size()>0){
            indicator.setBuyNum(Long.valueOf(list.size()));
            Long buy25=0L;
            Long buy50=0L;
            Long buy1Period=0L;
            Long buy2Period=0L;
            Long buyMore2Period=0L;
            for (MemIndicatorVo memIndicatorVo : list) {
                periodList.add(memIndicatorVo.getMCode());
                if(memIndicatorVo.getPpvTotal().compareTo(new BigDecimal("25.00"))!=-1){
                    buy25++;
                }
                if(memIndicatorVo.getPpvTotal().compareTo(new BigDecimal("50.00"))!=-1){
                    buy50++;
                }
                if(memIndicatorVo.getBuyNum()==1){
                    buy1Period++;
                }else if(memIndicatorVo.getBuyNum()==2){
                    buy2Period++;
                }else {
                    buyMore2Period++;
                }
            }
            indicator.setBuyMore25(buy25);
            indicator.setBuyMore50(buy50);
            indicator.setActiveMore25(new BigDecimal(Long.toString(buy25)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getMemberNumTotal())),2,BigDecimal.ROUND_HALF_UP));
            indicator.setActiveMore50(new BigDecimal(Long.toString(buy50)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getMemberNumTotal())),2,BigDecimal.ROUND_HALF_UP));
            indicator.setBuyOnePeriod(buy1Period);
            indicator.setBuyTwoPeriod(buy2Period);
            indicator.setBuyMoreTwoPeriod(buyMore2Period);
        }else {
            indicator.setBuyNum(0L);
            indicator.setBuyMore25(0L);
            indicator.setBuyMore50(0L);
            indicator.setActiveMore25(new BigDecimal("0.00"));
            indicator.setActiveMore50(new BigDecimal("0.00"));
            indicator.setBuyOnePeriod(0L);
            indicator.setBuyTwoPeriod(0L);
            indicator.setBuyMoreTwoPeriod(0L);
        }
        //获取当前年份
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String str = "";
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM");
        String format4 = format3.format(new Date());
        //String format4 = format3.format(parse);
        System.out.println(format4);
        if(format4.endsWith("01")){
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.YEAR,-1);
            str = format.format(instance.getTime());
            System.out.println(str+"***12");
        }else {
            str = format.format(new Date());
            //str = format.format(parse);
            System.out.println(str+"***no12");
        }
        String start=str+"-01-01 00:00:00";
        String end=str+"-12-31 23:59:59";
        HashMap<String, Object> map = new HashMap<>();
        map.put("start",start);
        map.put("end",end);
        List<MemIndicatorVo> list2=shopOrderDao.findMemIndicatorVoByYear(map);
        if(list2!=null&&list2.size()>0){
            indicator.setBuyZero(indicator.getMemberNumTotal()-list2.size());
            Long year1=0L;
            Long year2=0L;
            Long yearMore2=0L;
            for (MemIndicatorVo memIndicatorVo : list2) {
                if(memIndicatorVo.getBuyNum()==1){
                    year1++;
                }else if(memIndicatorVo.getBuyNum()==2){
                    year2++;
                }else {
                    yearMore2++;
                }
            }
            indicator.setBuyOne(year1);
            indicator.setBuyTwo(year2);
            indicator.setRePurchaseOne(new BigDecimal(Long.toString(year2)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getMemberNumTotal())),2,BigDecimal.ROUND_HALF_UP));
            indicator.setBuyMoreTwo(yearMore2);
            indicator.setRePurchaseTwo(new BigDecimal(Long.toString(yearMore2)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getMemberNumTotal())),2,BigDecimal.ROUND_HALF_UP));
        }else {
            indicator.setBuyZero(indicator.getMemberNumTotal());
            indicator.setBuyOne(0L);
            indicator.setBuyTwo(0L);
            indicator.setRePurchaseOne(new BigDecimal("0.00"));
            indicator.setBuyMoreTwo(0L);
            indicator.setRePurchaseTwo(new BigDecimal("0.00"));
        }
        //查询统计周期前一个周期的会员购买情况
        RdSysPeriod period = rdSysPeriodService.findByPeriodCode(periodCode);
        if(period!=null&&period.getPrePeriod()!=null){
            List<MemIndicatorVo> list3=shopOrderDao.findMemIndicatorVo(period.getPrePeriod());
            if(list3!=null&&list3.size()>0){
                ArrayList<String> intersect = new ArrayList<>();
                for (MemIndicatorVo memIndicatorVo : list3) {
                    for (String s : periodList) {
                        if(memIndicatorVo.getMCode().equals(s)){
                            intersect.add(s);
                            periodList.remove(s);
                            break;
                        }
                    }
                }
                indicator.setPeriodBeforeNum(Long.valueOf(list3.size()));
                int size = intersect.size();
                indicator.setBuyBack(Long.valueOf(size));
                indicator.setRepurchaseRate(new BigDecimal(Long.toString(indicator.getBuyBack())).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getPeriodBeforeNum())),2,BigDecimal.ROUND_HALF_UP));
            }else {
                indicator.setPeriodBeforeNum(0L);
                indicator.setBuyBack(0L);
                indicator.setRepurchaseRate(new BigDecimal("0.00"));
            }
        }else {//没有统计周期的上一个周期
            indicator.setPeriodBeforeNum(0L);
            indicator.setBuyBack(0L);
            indicator.setRepurchaseRate(new BigDecimal("0.00"));
        }
        indicator.setStatus(1);
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        //calendar.setTime(parse);//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -3);  //设置为前3月
        Date time3month = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = dateFormat.format(time3month);
        Long month3NobuyNum=rdMmRelationDao.findNoBuyNum(format1);
        indicator.setMonth3NobuyNum(month3NobuyNum);
        indicator.setMonth3NobuyRate(new BigDecimal(Long.toString(month3NobuyNum)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getMemberNumTotal())),2,BigDecimal.ROUND_HALF_UP));
        calendar.add(Calendar.MONTH,-6);
        Date time6month = calendar.getTime();
        String format2 = dateFormat.format(time6month);
        Long month6NobuyNum=rdMmRelationDao.findNoBuyNum(format2);
        indicator.setMonth6NobuyNum(month6NobuyNum);
        indicator.setMonth6NobuyRate(new BigDecimal(Long.toString(month6NobuyNum)).multiply(new BigDecimal("100.00")).divide(new BigDecimal(Long.toString(indicator.getMemberNumTotal())),2,BigDecimal.ROUND_HALF_UP));
        //写入数据库
        memberIndicatorDao.insert(indicator);
        System.out.println("结束");
    }
}
