package com.framework.loippi.service.impl.user;


import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.integration.RdMmIntegralRuleDao;
import com.framework.loippi.dao.user.PlusProfitDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.*;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.*;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PlusProfitServiceImpl extends GenericServiceImpl<PlusProfit, Long> implements PlusProfitService {
    @Resource
    private PlusProfitDao plusProfitDao;
    @Resource
    private RdMmRelationService rdMmRelationService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private ShopCommonMessageDao shopCommonMessageDao;
    @Resource
    private ShopMemberMessageDao shopMemberMessageDao;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmAccountLogService rdMmAccountLogService;
    @Resource
    private RdMmIntegralRuleDao rdMmIntegralRuleDao;
    @Resource
    private RdSysPeriodDao rdSysPeriodDao;

    /**
     * 对于已经满足发放条件的plusProfit进行发放
     */
    @Override
    public void grantPlusProfit() {
        System.out.println("come in grant plusProfit");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expectTime = format.format(new Date());
        List<PlusProfit> list=plusProfitDao.findTimeMature(expectTime);
        if(list!=null&&list.size()>0){
            for (PlusProfit plusProfit : list) {
                String buyerId = plusProfit.getBuyerId();
                RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", buyerId);
                if(rdMmRelation==null||rdMmRelation.getSponsorCode()==null){
                    continue;
                }
                RdMmBasicInfo mmBasicInfo = rdMmBasicInfoService.findByMCode(rdMmRelation.getSponsorCode());
                plusProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                if(mmBasicInfo.getPlusVip()==0){
                    plusProfit.setProfits(new BigDecimal("500.00"));
                }
                if(mmBasicInfo.getPlusVip()==1){
                    plusProfit.setProfits(new BigDecimal("1000.00"));
                }
                RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", rdMmRelation.getSponsorCode());
                List<RdMmIntegralRule> all = rdMmIntegralRuleDao.findAll();
                RdMmIntegralRule rdMmIntegralRule = all.get(0);
                //1.生成消息通知
                ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                shopCommonMessage.setSendUid(rdMmRelation.getSponsorCode());
                shopCommonMessage.setType(1);
                shopCommonMessage.setOnLine(1);
                shopCommonMessage.setCreateTime(new Date());
                shopCommonMessage.setBizType(2);
                shopCommonMessage.setIsTop(1);
                shopCommonMessage.setCreateTime(new Date());
                shopCommonMessage.setTitle("积分到账通知");
                shopCommonMessage.setContent("您推荐的会员"+plusProfit.getBuyerId()+"已购买PLUS VIP订单，奖励"+plusProfit.getProfits()+"点积分，已加入奖励积分账户");
                Long msgId = twiterIdService.getTwiterId();
                shopCommonMessage.setId(msgId);
                shopCommonMessageDao.insert(shopCommonMessage);
                ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                shopMemberMessage.setBizType(2);
                shopMemberMessage.setCreateTime(new Date());
                shopMemberMessage.setId(twiterIdService.getTwiterId());
                shopMemberMessage.setIsRead(0);
                shopMemberMessage.setMsgId(msgId);
                shopMemberMessage.setUid(Long.parseLong(rdMmRelation.getSponsorCode()));
                shopMemberMessageDao.insert(shopMemberMessage);
                //2.生成日志
                RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                rdMmAccountLog.setMmCode(rdMmRelation.getSponsorCode());
                rdMmAccountLog.setMmNickName(mmBasicInfo.getMmNickName());
                rdMmAccountLog.setTransTypeCode("BA");
                rdMmAccountLog.setAccType("SBB");
                rdMmAccountLog.setTrSourceType("CMP");
                rdMmAccountLog.setTrOrderOid(plusProfit.getOrderId());
                rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
                BigDecimal amount = plusProfit.getProfits().multiply(new BigDecimal(rdMmIntegralRule.getRsCountBonusPoint())).divide(new BigDecimal(100),2);
                rdMmAccountLog.setAmount(amount);
                rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().add(amount));
                rdMmAccountLog.setTransDate(new Date());
                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                if(period!=null){
                    rdMmAccountLog.setTransPeriod(period);
                }
                rdMmAccountLog.setTransDesc("推荐会员"+plusProfit.getBuyerId()+"成为PLUS VIP会员奖励发放");
                rdMmAccountLog.setAutohrizeDesc("推荐会员"+plusProfit.getBuyerId()+"成为PLUS VIP会员奖励发放");
                rdMmAccountLog.setStatus(3);
                rdMmAccountLogService.save(rdMmAccountLog);
                rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().add(amount));
                rdMmAccountInfoService.update(rdMmAccountInfo);
                //3.修改plusProfit发放状态 发放金额 受益人
                plusProfit.setState(1);
                plusProfit.setActualTime(new Date());
                if(period!=null){
                    plusProfit.setActualPeriod(period);
                }
                plusProfitDao.update(plusProfit);
            }
        }
        System.out.println("grant plusProfit over");
    }
}
