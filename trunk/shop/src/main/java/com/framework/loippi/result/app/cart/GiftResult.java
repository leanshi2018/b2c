package com.framework.loippi.result.app.cart;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;

import com.framework.loippi.entity.product.ShopGoods;

/**
 * @author :ldq
 * @date:2020/12/2
 * @description:dubbo com.framework.loippi.result.app.cart
 */
@Data
@Accessors(chain = true)
public class GiftResult {


	private Integer flag;
	private Integer giftsNum;
	public ArrayList<ShopGoods> shopGoods;
}
