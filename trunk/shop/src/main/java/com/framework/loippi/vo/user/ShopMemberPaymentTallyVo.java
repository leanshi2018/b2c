package com.framework.loippi.vo.user;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lys on 2017/10/17.
 */
@Data
@ToString
public class ShopMemberPaymentTallyVo extends ShopMemberPaymentTally {

    public Long storeId;
    public Long phoneNumber;


}
