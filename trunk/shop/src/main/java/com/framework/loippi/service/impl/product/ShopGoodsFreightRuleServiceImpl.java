package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.ShopGoodsFreightRuleDao;
import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.entity.user.RdMmRelation;

import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsFreightRuleService;
import com.framework.loippi.service.user.RdMmRelationService;

import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


/**
 * SERVICE - ShopGoodsFreightRule(运费规则表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsFreightRuleServiceImpl extends GenericServiceImpl<ShopGoodsFreightRule, Long> implements ShopGoodsFreightRuleService {
	
	@Autowired
	private ShopGoodsFreightRuleDao shopGoodsFreightRuleDao;
	@Resource
	private RdMmRelationService rdMmRelationService;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsFreightRuleDao);
	}

	@Override
	public BigDecimal CalculateFreightDiscount(String memberId, BigDecimal goodsTotalAmount) {
		BigDecimal preferentialFreightAmount=new BigDecimal("0");

		RdMmRelation shopMember=rdMmRelationService.find("mmCode",memberId);
		List<ShopGoodsFreightRule> shopGoodsFreightRulesList=shopGoodsFreightRuleDao.findByParams(Paramap.create().put("memberGradeId",shopMember.getRank())
		.put("goodsTotalAmount",goodsTotalAmount));
		if (shopGoodsFreightRulesList!=null && shopGoodsFreightRulesList.size()>0){
			preferentialFreightAmount=shopGoodsFreightRulesList.get(0).getPreferential();
		}else{
			//有2种情况 1该类型会员没有优惠信息  2该会员优惠信息有最小价格却没有最大价格 XX以上这种
			shopGoodsFreightRulesList=shopGoodsFreightRuleDao.findByParams(Paramap.create().put("memberGradeId",shopMember.getRank())
			.put("maxIsNull",goodsTotalAmount));
			if (shopGoodsFreightRulesList!=null && shopGoodsFreightRulesList.size()>0){
				preferentialFreightAmount=shopGoodsFreightRulesList.get(0).getPreferential();
			}
		}
		return preferentialFreightAmount;
	}

	@Override
	public void insertBatch(List<ShopGoodsFreightRule> shopGoodsFreightRuleList) {
		shopGoodsFreightRuleDao.insertBatch(shopGoodsFreightRuleList);
	}

	@Override
	public void updateBatch(List<ShopGoodsFreightRule> shopGoodsFreightRuleList) {
		shopGoodsFreightRuleDao.updateBatch(shopGoodsFreightRuleList);
	}
}
