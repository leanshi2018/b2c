package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.*;
import com.framework.loippi.entity.product.*;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.service.product.ShopGoodsTypeService;

import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsType(商品类型表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsTypeServiceImpl extends GenericServiceImpl<ShopGoodsType, Long> implements ShopGoodsTypeService {

    @Autowired
    private ShopGoodsTypeDao shopGoodsTypeDao;
    @Autowired
    private ShopGoodsTypeSpecDao shopGoodsTypeSpecDao;
    @Autowired
    private ShopGoodsTypeBrandDao shopGoodsTypeBrandDao;
    @Autowired
    private TwiterIdService twiterIdService;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsTypeDao);
    }

    @Override
    public void saveGoodsType(ShopGoodsType vo) {
        Long id = twiterIdService.getTwiterId();
        vo.setId(id);
        shopGoodsTypeDao.insert(vo);
        this.saveOther(vo, id);

    }

    @Override
    public void updateGoodsType(ShopGoodsType vo) {
        //先删除
        shopGoodsTypeBrandDao.deleteByTypeId(vo.getId());
        shopGoodsTypeSpecDao.deleteByTypeId(vo.getId());
        //修改good_type
        shopGoodsTypeDao.update(vo);
        //重新批量插入
        this.saveOther(vo, vo.getId());
    }

    @Override
    public ShopGoodsType selectTypeFetchOther(Long typeId) {
        ShopGoodsType shopGoodsType = shopGoodsTypeDao.selectTypeFetchOther(typeId);
        return shopGoodsType;
    }

    @Override
    public List<ShopGoodsBrand> findBrandList(Map<String, Object> params) {
        if (params == null || params.size() == 0) return null;
        return shopGoodsTypeDao.findBrandList(params);
    }

    @Override
    public List<ShopGoodsType> findAllSimple() {
        return shopGoodsTypeDao.findAllSimple();
    }

    @Override
    public Page<ShopGoodsType> findSimpleByPage(Pageable pageable) {
        PageList<ShopGoodsType> result = shopGoodsTypeDao.findSimpleByPage(pageable.getParameter(), pageable.getPageBounds());
        return new Page<ShopGoodsType>(result, result.getPaginator().getTotalCount(), pageable);
    }

    //保存其他
    private void saveOther(ShopGoodsType vo, Long typeId) {
        //重新构造插入good_attribute,shop_attribute_value
        //重新构造插入good_type_brand
        if (CollectionUtils.isNotEmpty(vo.getBrandList())) {
            List<ShopGoodsTypeBrand> tbList = Lists.newArrayList();
            for (ShopGoodsTypeBrand tb : vo.getBrandList()) {
                if (tb.getBrandId() != null) {
                    tb.setTypeId(typeId);
                    tb.setId(twiterIdService.getTwiterId());
                    tbList.add(tb);
                }
            }
            shopGoodsTypeBrandDao.batchSave(tbList);
        }
        //重新构造插入good_type_spec
        if (CollectionUtils.isNotEmpty(vo.getSpecList())) {
            List<ShopGoodsTypeSpec> tsList = Lists.newArrayList();
            for (ShopGoodsTypeSpec ts : vo.getSpecList()) {
                if (ts.getSpId() != null) {
                    ts.setTypeId(typeId);
                    Long id = twiterIdService.getTwiterId();
                    ts.setId(id);
                    tsList.add(ts);
                }
            }
            shopGoodsTypeSpecDao.batchSave(tsList);
        }
    }

}
