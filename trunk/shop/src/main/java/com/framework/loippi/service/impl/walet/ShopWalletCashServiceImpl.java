//package com.framework.loippi.service.impl.walet;
//
//
//import com.framework.loippi.dao.walet.ShopWalletCashDao;
//import com.framework.loippi.dao.walet.ShopWalletLogDao;
//import com.framework.loippi.entity.TUserSetting;
//
//import com.framework.loippi.entity.walet.LgTypeEnum;
//import com.framework.loippi.entity.walet.ShopWalletCash;
//import com.framework.loippi.entity.walet.ShopWalletLog;
//import com.framework.loippi.service.TUserSettingService;
//import com.framework.loippi.service.TwiterIdService;
//import com.framework.loippi.service.impl.GenericServiceImpl;
//
//import com.framework.loippi.util.Dateutil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
///**
// * SERVICE - ShopWalletCash(预存款提现记录表)
// *
// * @author zijing
// * @version 2.0
// */
//@Service
//public class ShopWalletCashServiceImpl extends GenericServiceImpl<ShopWalletCash, Long> implements
//        ShopWalletCashService {
//    @Autowired
//    private ShopWalletCashDao shopWalletCashDao;
//    @Autowired
//    private ShopMemberDao shopMemberDao;
//    @Autowired
//    private TwiterIdService twiterIdService;
//    @Autowired
//    private ShopWalletLogDao shopWalletLogDao;
//    @Autowired
//    private TUserSettingService tUserSettingService;
//
//    @Autowired
//    public void setGenericDao() {
//        super.setGenericDao(shopWalletCashDao);
//    }
//
//    @Override
//    public void saveWallet(ShopWalletCash walletCash, ShopMember shopMember) {
//
//        TUserSetting tUserSetting = tUserSettingService.find("cKey", "withdraw_commission");
//        String cValue = tUserSetting.getCValue();
//        //提现手续费：比率
//        BigDecimal rate = new BigDecimal(cValue).divide(new BigDecimal(100));
//        //计算手续费
//        BigDecimal poundage = walletCash.getPdcAmount().multiply(rate);
//        walletCash.setServiceAmount(poundage);
//        walletCash.setPdcAmount(walletCash.getPdcAmount());
//        shopWalletCashDao.insert(walletCash);
//
//        //冻结余额
//        ShopMember member = new ShopMember();
//        member.setId(shopMember.getId());
//        member.setAvailablePredeposit(shopMember.getAvailablePredeposit().subtract(walletCash.getPdcAmount()).subtract(poundage));
//        member.setFreezePredeposit(shopMember.getFreezePredeposit().add(walletCash.getPdcAmount()).add(poundage));
//
//        //产生冻结流水记录
//        ShopWalletLog shopWalletLog = new ShopWalletLog();
//        shopWalletLog.setId(twiterIdService.getTwiterId());
//        if (walletCash.getStoreId() != null) {
//            shopWalletLog.setStoreId(walletCash.getStoreId());
//        }
//        shopWalletLog.setBizId(walletCash.getId());
//        shopWalletLog.setOrderSn(walletCash.getPdcSn());
//        shopWalletLog.setLgMemberId(shopMember.getId());
//        shopWalletLog.setLgMemberName(shopMember.getMemberName());
//        shopWalletLog.setCreateTime(new Date());
//        shopWalletLog.setLgRdeAmount(walletCash.getPdcAmount());
//        shopWalletLog.setLgType(LgTypeEnum.CASH_APPLY.value);
//        shopWalletLog.setLgSn("W" + Dateutil.getDateString());
//        shopWalletLog.setLgAvAmount(shopMember.getAvailablePredeposit().subtract(walletCash.getPdcAmount()));
//        shopWalletLog.setLgFreezeAmount(walletCash.getPdcAmount());
//        shopWalletLog.setLgRdeAmount(walletCash.getPdcAmount());
//        shopWalletLog.setLgDesc("用户申请提现冻结金额" + walletCash.getPdcAmount() + "元");
//        shopWalletLogDao.insert(shopWalletLog);
//
//        shopMemberDao.update(member);
//    }
//
//    @Override
//    public void releaseWallet(ShopWalletCash walletCash, ShopMember shopMember, String status) {
//        if ("1".equals(walletCash.getPdcPaymentState())) {
//            return;
//        }
//        walletCash.setPdcPaymentState(status);
//        if ("1".equals(status)) {
//            //产生流水记录
//            ShopWalletLog shopWalletLog = new ShopWalletLog();
//            shopWalletLog.setId(twiterIdService.getTwiterId());
//            if (walletCash.getStoreId() != null && walletCash.getStoreId() != 0) {
//                shopWalletLog.setStoreId(walletCash.getStoreId());
//            }
//            shopWalletLog.setBizId(walletCash.getId());
//            shopWalletLog.setOrderSn(walletCash.getPdcSn());
//            shopWalletLog.setLgMemberId(shopMember.getId());
//            shopWalletLog.setLgMemberName(shopMember.getMemberName());
//            shopWalletLog.setCreateTime(new Date());
//            shopWalletLog.setLgType(LgTypeEnum.CASH_PAY.value);
//            shopWalletLog.setLgSn("W" + Dateutil.getDateString());
//            //变化支出,存入
//            shopWalletLog.setLgRdeAmount(walletCash.getPdcAmount());
//            //变化金额
//            shopWalletLog.setLgAvAmount(shopMember.getAvailablePredeposit());
//            shopWalletLog.setLgFreezeAmount(new BigDecimal(0));
//            shopWalletLog.setLgRdeAmount(walletCash.getPdcAmount());
//            shopWalletLog.setLgDesc(
//                    "用户提现金额" + walletCash.getPdcAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "元");
//            shopWalletLogDao.insert(shopWalletLog);
//            //提现手续费流水
//            ShopWalletLog shopWalletLogService = new ShopWalletLog();
//            shopWalletLogService.setId(twiterIdService.getTwiterId());
//            if (walletCash.getStoreId() != null && walletCash.getStoreId() != 0) {
//                shopWalletLogService.setStoreId(walletCash.getStoreId());
//            }
//            shopWalletLogService.setBizId(walletCash.getId());
//            shopWalletLogService.setOrderSn(walletCash.getPdcSn());
//            shopWalletLogService.setLgMemberId(shopMember.getId());
//            shopWalletLogService.setLgMemberName(shopMember.getMemberName());
//            shopWalletLogService.setCreateTime(new Date());
//            shopWalletLogService.setLgRdeAmount(walletCash.getServiceAmount());
//            shopWalletLogService.setLgType(LgTypeEnum.CASH_APPLY_SERVICEAMOUNT.value);
//            shopWalletLogService.setLgSn("W" + Dateutil.getDateString());
//            shopWalletLogService.setLgAvAmount(shopMember.getAvailablePredeposit().subtract(walletCash.getServiceAmount()));
//            shopWalletLogService.setLgFreezeAmount(walletCash.getPdcAmount());
//            shopWalletLogService.setLgDesc("用户申请提现手续费金额" + walletCash.getServiceAmount() + "元");
//            shopWalletLogDao.insert(shopWalletLogService);
//            //清空冻结金额
//            ShopMember member = new ShopMember();
//            member.setId(shopMember.getId());
//            member.setFreezePredeposit(shopMember.getFreezePredeposit().subtract(walletCash.getPdcAmount().subtract(walletCash.getServiceAmount())));
//            shopMemberDao.update(member);
//        } else {
//            //产生流水记录,拒绝提现
//            ShopWalletLog shopWalletLog = new ShopWalletLog();
//            shopWalletLog.setId(twiterIdService.getTwiterId());
//            if (walletCash.getStoreId() != null && walletCash.getStoreId() != 0) {
//                shopWalletLog.setStoreId(walletCash.getStoreId());
//            }
//            shopWalletLog.setBizId(walletCash.getId());
//            shopWalletLog.setOrderSn(walletCash.getPdcSn());
//            shopWalletLog.setLgMemberId(shopMember.getId());
//            shopWalletLog.setLgMemberName(shopMember.getMemberName());
//            shopWalletLog.setCreateTime(new Date());
//            shopWalletLog.setLgType(LgTypeEnum.CASH_DEL.value);
//            shopWalletLog.setLgSn("W" + Dateutil.getDateString());
//            shopWalletLog.setLgAddAmount(walletCash.getPdcAmount());
//            shopWalletLog.setLgAvAmount(shopMember.getAvailablePredeposit().add(walletCash.getPdcAmount()));
//            shopWalletLog.setLgFreezeAmount(new BigDecimal(0));
//            shopWalletLog.setLgAddAmount(walletCash.getPdcAmount());
//            shopWalletLog.setLgDesc(
//                    "用户申请提现金额" + walletCash.getPdcAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "元被拒绝");
//            shopWalletLogDao.insert(shopWalletLog);
//
//            //清空冻结金额
//            ShopMember member = new ShopMember();
//            member.setId(shopMember.getId());
//            member.setFreezePredeposit(shopMember.getFreezePredeposit().subtract(walletCash.getPdcAmount().subtract(walletCash.getServiceAmount())));
//            member.setAvailablePredeposit(shopMember.getAvailablePredeposit().add(walletCash.getPdcAmount().add(walletCash.getServiceAmount())));
//            shopMemberDao.update(member);
//        }
//        shopWalletCashDao.update(walletCash);
//    }
//
//    @Override
//    public BigDecimal countServiceAmount() {
//        return shopWalletCashDao.countServiceAmount();
//    }
//}
