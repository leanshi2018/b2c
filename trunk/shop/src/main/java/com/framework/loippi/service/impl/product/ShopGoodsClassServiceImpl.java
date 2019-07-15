package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.ShopGoodsClassDao;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsClass(商品分类表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsClassServiceImpl extends GenericServiceImpl<ShopGoodsClass, Long> implements ShopGoodsClassService {

    @Autowired
    private ShopGoodsClassDao shopGoodsClassDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsClassDao);
    }

    @Override
    public List<ShopGoodsClass> findList(Long parentid) {
        return shopGoodsClassDao.findList(parentid);
    }

    @Override
    public int findCount(ShopGoodsClass goodsClass) {
        return shopGoodsClassDao.findCount(goodsClass);
    }

    /**
     * 根据父节点查询分类
     *
     * @param map
     * @return
     */
    public List<ShopGoodsClass> findChildByParentIds(Map<String, Object> map) {
        if (map == null || map.size() == 0) return null;
        List<ShopGoodsClass> list = shopGoodsClassDao.findChildByParentIds(map);
        if (list != null && list.size() > 0) return list;
        return null;
    }

    @Override
    public List<ShopGoodsClass> findByParams2(Map<String, Object> params) {
        return shopGoodsClassDao.newFindByParams(params);
    }

    @Override
    public Map<Long, ShopGoodsClass> findToMap(List<Long> ids) {
        if (ids.size() == 0) {
            return new HashMap<>();
        }
        List<ShopGoodsClass> goodsClasses = shopGoodsClassDao.findByParams(Paramap.create().put("classIds", ids));
        Map<Long, ShopGoodsClass> mapGoodsClass = new HashMap<>();
        for (ShopGoodsClass item : goodsClasses) {
            mapGoodsClass.put(item.getId(), item);
        }
        return mapGoodsClass;
    }

    @Override
    public List<Long> findIdsByParentIds(List<Long> parentIds) {
        List<ShopGoodsClass> shopGoodsClasses = shopGoodsClassDao.findByParams(Paramap.create().put("classParentIds", parentIds));
        List<Long> ids = Lists.newArrayList();
        for (ShopGoodsClass shopGoodsClass : shopGoodsClasses) {
            ids.add(shopGoodsClass.getId());
        }
        return ids;
    }
}
