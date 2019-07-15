package com.framework.loippi.dao.walet;

import com.framework.loippi.entity.walet.ShopWalletCash;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.math.BigDecimal;

/**
 * DAO - ShopWalletCash(预存款提现记录表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopWalletCashDao  extends GenericDao<ShopWalletCash, Long> {

    BigDecimal countServiceAmount();
}
