package com.framework.loippi.service.wallet;

import com.framework.loippi.entity.walet.ShopWalletRecharge;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopWalletRecharge(预存款充值表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopWalletRechargeService  extends GenericService<ShopWalletRecharge, Long> {

    /**
     * 充值订单支付后，回调处理充值的相关事宜：记录流水和更新账户余额
     * @param paySn
     */
    public void payRechargeOrderCallback(String paySn);
}
