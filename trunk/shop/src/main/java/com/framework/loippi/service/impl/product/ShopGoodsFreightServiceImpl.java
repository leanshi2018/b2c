package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.ShopGoodsFreightDao;
import com.framework.loippi.entity.product.ShopGoodsFreight;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsFreightService;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * SERVICE - ShopGoodsFreight(地区运费表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsFreightServiceImpl extends GenericServiceImpl<ShopGoodsFreight, Long> implements ShopGoodsFreightService {
	
	@Autowired
	private ShopGoodsFreightDao shopGoodsFreightDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsFreightDao);
	}

	@Override
	public BigDecimal CalculateFreight(String areaId, Double totalWeight) {
		BigDecimal freightAmount=new BigDecimal("0");
		ShopGoodsFreight shopGoodsFreight=null;
		List<ShopGoodsFreight> shopGoodsFreightList=shopGoodsFreightDao.findByParams(Paramap.create().put("areaName",areaId));
		if (shopGoodsFreightList==null || shopGoodsFreightList.size()<=0){
			//如果该省没有特别设置,则获取默认的运费 默认运费areaId为0
			shopGoodsFreightList=shopGoodsFreightDao.findByParams(Paramap.create().put("areaId",0L));
		}
		if (shopGoodsFreightList!=null && shopGoodsFreightList.size()>0){
			shopGoodsFreight=shopGoodsFreightList.get(0);
			//运费
			if (totalWeight==0){

			}
			else if (totalWeight>1){
				freightAmount=shopGoodsFreight.getFirstWeight().add(shopGoodsFreight.getAdditionalWeight().multiply(new BigDecimal(totalWeight-1))).setScale(2, RoundingMode.HALF_UP);

			}else{
				freightAmount=shopGoodsFreight.getFirstWeight().setScale(2, RoundingMode.HALF_UP);
			}
		}
		return freightAmount;
	}
}
