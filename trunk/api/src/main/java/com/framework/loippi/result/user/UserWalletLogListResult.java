package com.framework.loippi.result.user;

import com.framework.loippi.entity.walet.ShopWalletLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletLogListResult {


    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 金额
     */
    private java.math.BigDecimal amount;

    /**
     * 出人账类型 (0:收入，1：支出)
     */
    private Integer opType;

    /**
     * 支付单id
     */
    private Long paymentId;

    /**
     * 余额
     */
    private java.math.BigDecimal balance;

    /**
     * 添加时间
     */
    private String createTime;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务信息
     */
    private String bizInfo;

    public static List<UserWalletLogListResult> build(
            List<ShopWalletLog> userWalletLogs) {
        if (CollectionUtils.isEmpty(userWalletLogs)) {
            return Collections.emptyList();
        }
        List<UserWalletLogListResult> results = new ArrayList<UserWalletLogListResult>();
        for (ShopWalletLog userWalletLog : userWalletLogs) {
            UserWalletLogListResult result = new UserWalletLogListResult();
            result.setId(userWalletLog.getId());
            result.setUserId(userWalletLog.getLgMemberId());
            if(userWalletLog.getLgType().equals("live_revenue")){
                if (userWalletLog.getLgAddAmount() != null
                        && userWalletLog.getLgAddAmount().compareTo(BigDecimal.ZERO)==1) {
                    result.setAmount(userWalletLog.getLgAddAmount());
                    result.setOpType(0);
                } else {
                    result.setAmount(userWalletLog.getLgRdeAmount());
                    result.setOpType(1);
                }
            }else{
                if (userWalletLog.getLgAddAmount() != null
                        && userWalletLog.getLgAddAmount().compareTo(BigDecimal.ZERO)==1) {
                    result.setAmount(userWalletLog.getLgAddAmount());
                    result.setOpType(1);
                } else {
                    result.setAmount(userWalletLog.getLgRdeAmount());
                    result.setOpType(0);
                }
            }
            //result.setPaymentId(userWalletLog.get());
            result.setBalance(userWalletLog.getLgAvAmount());
            result.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userWalletLog.getCreateTime()));
            result.setBizType(userWalletLog.getLgType());
            result.setBizInfo(userWalletLog.getLgDesc());
            results.add(result);
        }
        return results;
    }

}
