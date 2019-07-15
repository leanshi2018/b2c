package com.framework.loippi.result.user;

import com.framework.loippi.consts.DocumentConsts;
import com.framework.loippi.consts.IntegrationNameConsts;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 *
 * @author Loippi team
 * @version 2.0
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIntegrationListResult {
    /**
     * 积分名称
     */
   private Integer integrationType;
    /**
     * 积分数量
     */
    private BigDecimal integration;
    /**
     * 积分状态
     * 0.正常
     * 1.冻结
     * 2.未激活
     */
    private Integer integrationStatus;
    /**
     *文章标识
     */
    private String docType;

    public static List<UserIntegrationListResult> build(RdMmAccountInfo rdMmAccountInfo) {
        List<UserIntegrationListResult> userIntegrationListResultList=new ArrayList<>();
        if (rdMmAccountInfo!=null){
        for (int i=0;i<3;i++){
            BigDecimal num=new BigDecimal(0);
            UserIntegrationListResult userIntegrationListResult=new UserIntegrationListResult();
            if (i==0){
                userIntegrationListResult.setIntegrationType(IntegrationNameConsts.BOP);
                userIntegrationListResult.setDocType(DocumentConsts.REWARD_POINTS_RULE);
                userIntegrationListResult.setIntegrationStatus(Optional.ofNullable(rdMmAccountInfo.getBonusStatus()).orElse(0));
                if (rdMmAccountInfo!=null){
                    num=Optional.ofNullable(rdMmAccountInfo.getBonusBlance()).orElse(BigDecimal.valueOf(0));
                }
            }else if (i==1){
                userIntegrationListResult.setIntegrationType(IntegrationNameConsts.SHP);
                userIntegrationListResult.setDocType(DocumentConsts.SHOPPING_POINTS_RULES);
                userIntegrationListResult.setIntegrationStatus(Optional.ofNullable(rdMmAccountInfo.getWalletStatus()).orElse(0));
                if (rdMmAccountInfo!=null){
                    num=Optional.ofNullable(rdMmAccountInfo.getWalletBlance()).orElse(BigDecimal.valueOf(0));
                }
            }else if (i==2){
                userIntegrationListResult.setIntegrationType(IntegrationNameConsts.PUI);
                userIntegrationListResult.setDocType(DocumentConsts.EXCHANGE_POINTS_RULES);
                userIntegrationListResult.setIntegrationStatus(Optional.ofNullable(rdMmAccountInfo.getRedemptionStatus()).orElse(0));
                if (rdMmAccountInfo!=null){
                    num=Optional.ofNullable(rdMmAccountInfo.getRedemptionBlance()).orElse(BigDecimal.valueOf(0));
                }
            }
            userIntegrationListResult.setIntegration(num);
            userIntegrationListResultList.add(userIntegrationListResult);
        }
        }
        return userIntegrationListResultList;
    }
}
