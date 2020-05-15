package com.framework.loippi.service.impl.common;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.dao.common.DailyOrderCensusDao;
import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.pojo.common.CensusVo;
import com.framework.loippi.service.common.DailyOrderCensusService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.utils.Paramap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.plaf.basic.BasicIconFactory;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 日会员订单信息统计
 */
@Service
@Slf4j
@Transactional
public class DailyOrderCensusServiceImpl extends GenericServiceImpl<DailyOrderCensus, Long> implements DailyOrderCensusService {

    @Resource
    private DailyOrderCensusDao dailyOrderCensusDao;
    @Resource
    private ShopOrderService shopOrderService;

    @Override
    public void getDailyOrderCensus() {
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
        //2.判断对应reportCode是否已经存在对应的DailyOrderCensus记录 如果存在记录，则结束方法
        List<DailyOrderCensus> list = dailyOrderCensusDao.findByParams(Paramap.create().put("reportCode",reportCode));
        if(list!=null&&list.size()>0){
            System.out.println(reportCode+"日已存在记录");
            return;
        }
        //3.生成当日会员订单记录
        DailyOrderCensus orderCensus = new DailyOrderCensus();
        orderCensus.setReportCode(reportCode);
        orderCensus.setStatisticalTime(new Date());
        Integer orderNum=0;
        Integer yesNum=shopOrderService.findOrderYesterdayNum(map);//昨日订单总数，包含已支付和未支付订单
        if(yesNum!=null){
            orderNum=yesNum;
        }
        orderCensus.setOrderNum(orderNum);
        Integer effectiveOrderNum=0;
        Integer eYesNum=shopOrderService.findEffectiveOrderYesterdayNum(map);//昨日已支付订单数
        if(eYesNum!=null){
            effectiveOrderNum=eYesNum;
        }
        orderCensus.setEffectiveOrderNum(effectiveOrderNum);
        Integer invalidOrderNum=0;
        Integer iYesNum=shopOrderService.findInvalidOrderYesterdayNum(map);//昨日未支付订单数
        if(iYesNum!=null){
            invalidOrderNum=iYesNum;
        }
        orderCensus.setInvalidOrderNum(invalidOrderNum);
        map.put("orderPlatform", OrderState.PLATFORM_APP);//app
        Integer orderNumApp=0;
        Integer appYesNum=shopOrderService.findPlatformOrderYesterdayNum(map);//有效订单中app支付订单数 即order_platform = 1
        if(appYesNum!=null){
            orderNumApp=appYesNum;
        }
        orderCensus.setOrderNumApp(orderNumApp);
        map.put("orderPlatform", OrderState.PLATFORM_WECHAT);//wechat
        Integer orderNumWechat=0;
        Integer wechatYesNum=shopOrderService.findPlatformOrderYesterdayNum(map);//有效订单中app支付订单数 即order_platform = 0
        if(wechatYesNum!=null){
            orderNumWechat=wechatYesNum;
        }
        orderCensus.setOrderNumWechat(orderNumWechat);
        BigDecimal orderIncomeTotal=shopOrderService.findYesIncomeTotal(map);
        orderCensus.setOrderIncomeTotal(orderIncomeTotal);
        if(orderIncomeTotal.compareTo(BigDecimal.ZERO)==1){
            BigDecimal orderPointTotal=shopOrderService.findYesPointTotal(map);
            BigDecimal pointProportion = orderPointTotal.divide(orderIncomeTotal).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
            orderCensus.setPointProportion(pointProportion);
        }else {
            orderCensus.setPointProportion(new BigDecimal("0.00"));
        }
        orderCensus.setUnitPrice(orderCensus.getOrderIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getEffectiveOrderNum()))).setScale(2, BigDecimal.ROUND_HALF_UP));
        map.put("orderType",1);
        CensusVo censusVoRetail=shopOrderService.findCensusData(map);
        orderCensus.setRetailIncomeTotal(censusVoRetail.getAmountTotal());
        orderCensus.setRetailOrderNum(censusVoRetail.getOrderNum());
        if(censusVoRetail.getOrderNum()==null||censusVoRetail.getOrderNum()==0){
            orderCensus.setRetailUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setRetailUnitPrice(orderCensus.getRetailIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getRetailOrderNum()))).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        map.put("orderType",2);
        CensusVo censusVoVip=shopOrderService.findCensusData(map);
        orderCensus.setVipIncomeTotal(censusVoVip.getAmountTotal());
        orderCensus.setVipOrderNum(censusVoVip.getOrderNum());
        if(censusVoVip.getOrderNum()==null||censusVoVip.getOrderNum()==0){
            orderCensus.setVipUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setVipUnitPrice(orderCensus.getVipIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getVipOrderNum()))).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        map.put("orderType",3);
        CensusVo censusVoBig=shopOrderService.findCensusData(map);
        orderCensus.setBigIncomeTotal(censusVoBig.getAmountTotal());
        orderCensus.setBigOrderNum(censusVoBig.getOrderNum());
        if(censusVoBig.getOrderNum()==null||censusVoBig.getOrderNum()==0){
            orderCensus.setBigUnitPrice(new BigDecimal("0.00"));
        }else {
            orderCensus.setBigUnitPrice(orderCensus.getBigIncomeTotal().divide(new BigDecimal(Integer.toString(orderCensus.getBigOrderNum()))).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        //4.将日订单列表统计数据插入数据库
        dailyOrderCensusDao.insert(orderCensus);
    }
}
