package com.framework.loippi.service.impl.user;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.integration.RdMmIntegralRuleDao;
import com.framework.loippi.dao.user.*;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.user.*;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.utils.Paramap;
@Service
@Transactional
public class RetailProfitServiceImpl extends GenericServiceImpl<RetailProfit, Long> implements RetailProfitService {
    @Autowired
    private RetailProfitDao retailProfitDao;
    @Autowired
    private RdMmRelationDao rdMmRelationDao;
    @Autowired
    private RdMmAccountInfoDao rdMmAccountInfoDao;
    @Autowired
    private RdMmIntegralRuleDao rdMmIntegralRuleDao;
    @Autowired
    private RdMmBasicInfoDao rdMmBasicInfoDao;
    @Autowired
    private RdSysPeriodDao rdSysPeriodDao;
    @Autowired
    private RdMmAccountLogDao rdMmAccountLogDao;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopCommonMessageDao shopCommonMessageDao;
    @Autowired
    private ShopMemberMessageDao shopMemberMessageDao;

    @Override
    public List<RetailProfit> findNoGrantByCode(String mmCode) {
        return retailProfitDao.findNoGrantByCode(mmCode);
    }

    @Override
    public BigDecimal findTotalProfit(HashMap<String, Object> map) {
        return retailProfitDao.findTotalProfit(map);
    }

    @Override
    public BigDecimal countProfit(Paramap put) {
        return retailProfitDao.countProfit(put);
    }
    @Override
    public BigDecimal findPeriodPay(Paramap put) {
        return retailProfitDao.findPeriodPay(put);
    }

    @Override
    public BigDecimal findPeriodNoPay(Paramap put) {
        return retailProfitDao.findPeriodNoPay(put);
    }

    /**
     * 零售利润发放
     */
    @Override
    public void grantRetail() {
        System.out.println("come in grant retail");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expectTime = format.format(new Date());
        List<RetailProfit> list=retailProfitDao.findTimeMature(expectTime);
        System.out.println(list.size());
        if(list!=null&&list.size()>0){
            int a=0;
            int b=0;
            ArrayList<RdMmAccountLog> rdMmAccountLogs = new ArrayList<>();
            for (RetailProfit retailProfit : list) {
                if(retailProfit.getProfits()!=null&&retailProfit.getProfits().compareTo(BigDecimal.ZERO)!=1){
                    continue;
                }
                a++;
                //根据retailProfit对应记录进行零售利润发放
                List<RdMmRelation> rdMmRelations = rdMmRelationDao.findByParams(Paramap.create().put("mmCode",retailProfit.getBuyerId()));
                if(rdMmRelations!=null&&rdMmRelations.size()>0){
                    RdMmRelation rdMmRelation = rdMmRelations.get(0);
                    List<RdMmRelation> rdMmRelations1 = rdMmRelationDao.findByParams(Paramap.create().put("mmCode", rdMmRelation.getSponsorCode()));
                    if(rdMmRelations1!=null&&rdMmRelations1.size()>0){
                        RdMmRelation rdMmRelation1 = rdMmRelations1.get(0);
                        if(rdMmRelation1.getRank()==0){//推荐人是普通会员的话，不发奖励，将奖励记录状态修改为延迟发放
                            retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                            retailProfit.setState(2);
                            retailProfitDao.update(retailProfit);
                        }else {//推荐人是vip及以上级别，发放
                            b++;
                            List<RdMmAccountInfo> accountInfos = rdMmAccountInfoDao.findByParams(Paramap.create().put("mmCode",rdMmRelation1.getMmCode()));
                            if(accountInfos!=null&&accountInfos.size()>0){
                                RdMmAccountInfo rdMmAccountInfo = accountInfos.get(0);
                                List<RdMmIntegralRule> all = rdMmIntegralRuleDao.findAll();
                                RdMmIntegralRule rdMmIntegralRule = all.get(0);
                                //生成积分账户修改日志
                                RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
                                rdMmAccountLog.setMmCode(rdMmRelation1.getMmCode());
                                List<RdMmBasicInfo> basicInfos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode",rdMmRelation1.getMmCode()));
                                rdMmAccountLog.setMmNickName(basicInfos.get(0).getMmNickName());
                                rdMmAccountLog.setTransTypeCode("BA");
                                rdMmAccountLog.setAccType("SBB");
                                rdMmAccountLog.setTrSourceType("CMP");
                                rdMmAccountLog.setTrOrderOid(retailProfit.getOrderId());
                                rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
                                BigDecimal amount = retailProfit.getProfits().multiply(new BigDecimal(rdMmIntegralRule.getRsCountBonusPoint())).divide(new BigDecimal(100),2);
                                rdMmAccountLog.setAmount(amount);
                                rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().add(amount));
                                rdMmAccountLog.setTransDate(new Date());
                                String period = rdSysPeriodDao.getSysPeriodService(new Date());
                                if(period!=null){
                                    rdMmAccountLog.setTransPeriod(period);
                                }
                                rdMmAccountLog.setTransDesc("零售利润奖励发放");
                                rdMmAccountLog.setAutohrizeDesc("零售利润奖励发放");
                                rdMmAccountLog.setStatus(3);
                                rdMmAccountLogDao.insert(rdMmAccountLog);
                                rdMmAccountLogs.add(rdMmAccountLog);
                                //修改积分账户
                                rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().add(amount));
                                rdMmAccountInfoDao.update(rdMmAccountInfo);
                                //修改零售利润
                                retailProfit.setReceiptorId(rdMmRelation.getSponsorCode());
                                retailProfit.setActualTime(new Date());
                                if(period!=null){
                                    retailProfit.setActualPeriod(period);
                                }
                                retailProfit.setState(1);
                                retailProfitDao.update(retailProfit);
                                //设置零售利润积分发放通知
                                ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
                                shopCommonMessage.setSendUid(rdMmRelation1.getMmCode());
                                shopCommonMessage.setType(1);
                                shopCommonMessage.setOnLine(1);
                                shopCommonMessage.setCreateTime(new Date());
                                shopCommonMessage.setBizType(2);
                                shopCommonMessage.setIsTop(1);
                                shopCommonMessage.setCreateTime(new Date());
                                shopCommonMessage.setTitle("积分到账通知");
                                shopCommonMessage.setContent("您从零售订单："+retailProfit.getOrderSn()+"获得"+amount+"点积分，已加入奖励积分账户");
                                Long msgId = twiterIdService.getTwiterId();
                                shopCommonMessage.setId(msgId);
                                shopCommonMessageDao.insert(shopCommonMessage);
                                ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
                                shopMemberMessage.setBizType(2);
                                shopMemberMessage.setCreateTime(new Date());
                                shopMemberMessage.setId(twiterIdService.getTwiterId());
                                shopMemberMessage.setIsRead(0);
                                shopMemberMessage.setMsgId(msgId);
                                shopMemberMessage.setUid(Long.parseLong(rdMmRelation1.getMmCode()));
                                shopMemberMessageDao.insert(shopMemberMessage);
                            }
                        }
                    }
                }
            }
            System.out.println(a+"***零售利润数量");
            System.out.println(b+"***发放零售利润数量");
            System.out.println(rdMmAccountLogs);
            System.out.println(rdMmAccountLogs.size()+"***日志表个数");
            System.out.println("grant retail over");
        }
    }


}
