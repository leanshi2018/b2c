package com.framework.loippi.service.impl.ware;

import com.framework.loippi.consts.MentionSubsidyConstants;
import com.framework.loippi.dao.ware.RdMentionCalculateDao;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.ware.RdMentionCalculate;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.pojo.selfMention.OrderInfo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.service.ware.RdMentionCalculateService;
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.utils.Paramap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RdMentionCalculateServiceImpl extends GenericServiceImpl<RdMentionCalculate, Long> implements RdMentionCalculateService {
    @Resource
    private RdWarehouseService rdWarehouseService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private RdMentionCalculateDao rdMentionCalculateDao;
    /**
     * 查询所有自提店，计算自提店上一个月的销售额，并计算补贴金额
     */
    @Override
    public void statistical() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String time = format.format(calendar.getTime());
        //1.查询出当前系统拥有自提店店铺集合
        List<RdWarehouse> wares=rdWarehouseService.findMentionWare();
        if(wares==null||wares.size()==0){
            return;
        }
        //2.初始化RdMentionCalculate记录集合
        ArrayList<RdMentionCalculate> calculates = new ArrayList<>();
        //3.遍历自提店，根据自提店id进行上一个月订单的统计
        for (RdWarehouse ware : wares) {
            Integer mentionId = ware.getMentionId();
            List<RdMentionCalculate> params = rdMentionCalculateDao.findByParams(Paramap.create().put("reportCode",time).put("mentionId",mentionId).put("status",1));
            if(params!=null&&params.size()>0){
                continue;
            }
            List<RdMentionCalculate> params2 = rdMentionCalculateDao.findByParams(Paramap.create().put("reportCode",time).put("mentionId",mentionId).put("status",2));
            if(params2!=null&&params2.size()>0){
                continue;
            }
            RdMentionCalculate calculate = new RdMentionCalculate();
            calculate.setId(twiterIdService.getTwiterId());
            calculate.setReportCode(time);
            calculate.setStatisticalTime(new Date());
            calculate.setMentionId(mentionId);
            calculate.setMentionName(ware.getWareName());
            calculate.setMCode(ware.getMmCode());
            RdMmBasicInfo basicInfo = rdMmBasicInfoService.find("mmCode",ware.getMmCode());
            if(basicInfo!=null&&basicInfo.getMmNickName()!=null){
                calculate.setMNickName(basicInfo.getMmNickName());
            }else {
                calculate.setMNickName("");
            }
            Integer monthNum=shopOrderService.findLastMonthCountByMentionId(mentionId);
            calculate.setOrderNum(monthNum);
            List<OrderInfo> orderInfos=shopOrderService.findLastMonthOrderInfo(mentionId);
            BigDecimal total=BigDecimal.ZERO;
            if(orderInfos!=null&&orderInfos.size()>0){
                for (OrderInfo orderInfo : orderInfos) {
                    total=total.add(orderInfo.getOrderAmount()).add(orderInfo.getUsePointNum()).subtract(orderInfo.getRefundAmount()).subtract(orderInfo.getRefundPoint());
                }
            }
            calculate.setIncome(total);
            if(total.compareTo(new BigDecimal("100000"))!=-1){
                calculate.setSubsidiesCoefficient(MentionSubsidyConstants.SUBSIDY_MORE_TEN);
            }else if(total.compareTo(new BigDecimal("100000"))==-1&&total.compareTo(new BigDecimal("50000"))!=-1){
                calculate.setSubsidiesCoefficient(MentionSubsidyConstants.SUBSIDY_BETWEEN_FIVE_TEN);
            }else if(total.compareTo(new BigDecimal("50000"))==-1&&total.compareTo(new BigDecimal("10000"))!=-1){
                calculate.setSubsidiesCoefficient(MentionSubsidyConstants.SUBSIDY_BETWEEN_ONE_FIVE);
            }else {
                calculate.setSubsidiesCoefficient(BigDecimal.ZERO);
            }
            BigDecimal subsidiesAcc = calculate.getIncome().multiply(calculate.getSubsidiesCoefficient()).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
            calculate.setSubsidiesAcc(subsidiesAcc);
            calculate.setStatus(1);
            calculates.add(calculate);
        }
        //4.如果RdMentionCalculate集合大于0 写入数据库
        if(calculates.size()>0){
            rdMentionCalculateDao.insertList(calculates);
        }
    }
}
