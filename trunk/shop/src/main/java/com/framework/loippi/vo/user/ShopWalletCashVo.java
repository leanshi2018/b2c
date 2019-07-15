package com.framework.loippi.vo.user;

import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.ShopWalletCash;
import lombok.Data;
import lombok.ToString;

/**
 * Created by lys on 2017/10/17.
 */
@Data
@ToString
public class ShopWalletCashVo extends ShopWalletCash {

    public Long storeId;

}
