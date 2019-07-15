package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author Loippi team
 * @version 2.0
 * @description
 */
@Data
@NoArgsConstructor

public class IntegrationBuildResult extends RdMmAccountLog {

    public static RdMmAccountLog bonusSP(RdMmBasicInfo shopMember, RdMmAccountInfo rdMmAccountInfo, Integer integration, BigDecimal walletBlance) {
        RdMmAccountLog rdMmAccountLog=new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("SP");
        rdMmAccountLog.setAccType("SWB");
        rdMmAccountLog.setTrSourceType("SWB");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        //转出无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setTransDate(new Date());
        return rdMmAccountLog;
    }
    public static RdMmAccountLog bonusWD(RdMmBasicInfo shopMember,RdMmAccountInfo rdMmAccountInfo,Integer integration,BigDecimal bonusPointWd,Integer bankCardId) {
        RdMmAccountLog rdMmAccountLog=new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("WD");
        rdMmAccountLog.setAccType("");
        rdMmAccountLog.setTrSourceType("BNK");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        rdMmAccountLog.setPresentationFeeNow(bonusPointWd);
        rdMmAccountLog.setActualWithdrawals(BigDecimal.valueOf(integration).subtract(bonusPointWd.multiply(BigDecimal.valueOf(integration))));
        rdMmAccountLog.setTrBankOid(bankCardId);
        //提现需审核初始为申请状态
        rdMmAccountLog.setStatus(2);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getBonusBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setTransDate(new Date());
        return rdMmAccountLog;
    }
    public static RdMmAccountLog WalletBT(RdMmBasicInfo shopMember,RdMmAccountInfo rdMmAccountInfo,BigDecimal walletBlance) {
        RdMmAccountLog rdMmAccountLog=new RdMmAccountLog();

        rdMmAccountLog.setTransTypeCode("BT");
        rdMmAccountLog.setAccType("SWB");
        rdMmAccountLog.setTrSourceType("SWB");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
        rdMmAccountLog.setAmount(walletBlance);
        //转出无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance().add(walletBlance));
        rdMmAccountLog.setTransDate(new Date());
        return rdMmAccountLog;
    }
    public static RdMmAccountLog WalletTT(RdMmBasicInfo shopMember,RdMmAccountInfo rdMmAccountInfo,Integer integration,RdMmBasicInfo accentMember) {
        RdMmAccountLog rdMmAccountLog=new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("TT");
        rdMmAccountLog.setAccType("");
        rdMmAccountLog.setTrSourceType("OWB");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(accentMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        //转出无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setTransDate(new Date());
        return rdMmAccountLog;
    }
    public static RdMmAccountLog WalletTF(RdMmBasicInfo shopMember,RdMmAccountInfo rdMmAccountInfo,Integer integration,RdMmBasicInfo accentMember) {
        RdMmAccountLog rdMmAccountLog=new RdMmAccountLog();
        rdMmAccountLog.setTransTypeCode("TF");
        rdMmAccountLog.setAccType("SWB");
        rdMmAccountLog.setTrSourceType("");
        rdMmAccountLog.setMmCode(shopMember.getMmCode());
        rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
        rdMmAccountLog.setTrMmCode(accentMember.getMmCode());
        rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
        rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
        //转出无需审核直接成功
        rdMmAccountLog.setStatus(3);
        rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
        rdMmAccountLog.setCreationTime(new Date());
        rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
        rdMmAccountLog.setAutohrizeTime(new Date());
        rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance().add(BigDecimal.valueOf(integration)));
        rdMmAccountLog.setTransDate(new Date());
        return rdMmAccountLog;
    }
}
