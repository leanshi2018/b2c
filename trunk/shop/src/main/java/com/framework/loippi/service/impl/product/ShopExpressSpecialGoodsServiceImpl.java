package com.framework.loippi.service.impl.product;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopExpressSpecialGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.common.goods.ExpressSpecialGoodsResult;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopExpressSpecialGoodsService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

/**
 * @author :ldq
 * @date:2019/10/25
 * @description:dubbo com.framework.loippi.service.impl.product
 */
@Service
public class ShopExpressSpecialGoodsServiceImpl extends GenericServiceImpl<ShopExpressSpecialGoods, Long> implements ShopExpressSpecialGoodsService {

	@Autowired
	private ShopExpressSpecialGoodsDao shopExpressSpecialGoodsDao;
	@Autowired
	private ShopGoodsSpecDao shopGoodsSpecDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopExpressSpecialGoodsDao);
	}

	@Override
	public List<ShopExpressSpecialGoods> findByState(Integer eState) {
		return shopExpressSpecialGoodsDao.findByState(eState);
	}

	@Override
	public List<ShopExpressSpecialGoods> findByExpressId(Long expressId) {
		return shopExpressSpecialGoodsDao.findByExpressId(expressId);
	}

	@Override
	public void addExpressSpecialGoods(ShopCommonExpress express, Long[] specIds,String adminName) {
		for (Long specId : specIds) {
			ShopGoodsSpec goodsSpec = shopGoodsSpecDao.find(specId);

			ShopExpressSpecialGoods shopExpressSpecialGoods = new ShopExpressSpecialGoods();
			shopExpressSpecialGoods.setGoodsSpecId(goodsSpec.getId());
			shopExpressSpecialGoods.setGoodsId(goodsSpec.getGoodsId());
			shopExpressSpecialGoods.setSpecGoodsSerial(goodsSpec.getSpecGoodsSerial());
			shopExpressSpecialGoods.setExpressId(express.getId());
			shopExpressSpecialGoods.setExpLevel(1);
			shopExpressSpecialGoods.setEState(0);
			shopExpressSpecialGoods.setCreationBy(adminName);
			shopExpressSpecialGoods.setCreationTime(new Date());
			shopExpressSpecialGoods.setUpdateBy(adminName);
			shopExpressSpecialGoods.setUpdateTime(new Date());

			shopExpressSpecialGoodsDao.insert(shopExpressSpecialGoods);
		}
	}

	@Override
	public Object findListResultByPage(Pageable pageable) {
		PageList<ExpressSpecialGoodsResult> result = shopExpressSpecialGoodsDao.findListResultByPage(pageable.getParameter(), pageable.getPageBounds());
		return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
	}
}
