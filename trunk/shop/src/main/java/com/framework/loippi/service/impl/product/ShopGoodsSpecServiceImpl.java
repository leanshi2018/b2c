package com.framework.loippi.service.impl.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsSpecDao;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.sys.GoodsStocksListView;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Lists;

/**
 * SERVICE - ShopGoodsSpec(商品规格表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsSpecServiceImpl extends GenericServiceImpl<ShopGoodsSpec, Long> implements ShopGoodsSpecService {

    @Resource
    private ShopGoodsSpecDao shopGoodsSpecDao;

    @Resource
    private ShopGoodsDao shopGoodsDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsSpecDao);
    }


    public void updateGoodsSpecStorage(ShopGoodsSpec goodsSpec) {
        shopGoodsSpecDao.updateGoodsSpecStorage(goodsSpec);
    }

//    @Override
//    public Map<String, Object> getGoodsSpecBySpecId(Long goodsId, Long goodsSpecId) {
//        ShopGoods goods = shopGoodsDao.find(goodsId);
//        String goodsSpec = goods.getGoodsSpec();
//        String specName = goods.getSpecName();
//        if (specName == null || specName.equals("")) {
//            return null;
//        }
//        Map<String, String> specNameMap = JacksonUtil.readJsonToMap(specName);
//        Map<String, List<GoodsSpecVo>> goodsSpecMap = GoodsUtils.goodsSpecStrToMapList(goodsSpec);
//        ShopGoodsSpec shopGoodsSpec = shopGoodsSpecDao.find(goodsSpecId);
//        List<ShopGoodsSpec> goodsSpecs = new ArrayList<>();
//        goodsSpecs.add(shopGoodsSpec);
//        //规格颜色对应的图片
//        Map<String, String> goodsColImg = GoodsUtils.goodsColImgStrToMap(goods.getGoodsColImg());
//        //得到该商品的所有goodsvalueId的String,以逗号分割
//        for (int i = 0; i < goodsSpecs.size(); i++) {
//            goodsSpecs.get(i).setSpecValueIDS(
//                    GoodsUtils.getThisGoodsAllSpecValueId(
//                            goodsSpecs.get(i).getSpecGoodsSpec()
//                    )
//            );
//        }
//        Map<String, Object> specmap = new HashMap<String, Object>();
//        specmap.put("goodsColImg", goodsColImg);
//        specmap.put("specname", specNameMap);
//        specmap.put("specvalue", goodsSpecMap);
//        specmap.put("goodsSpecs", goodsSpecs);
//        return specmap;
//    }

    public List<ShopGoodsSpec> findListByGoodsId(Long goodsId) {
        return shopGoodsSpecDao.findListByGoodsId(goodsId);
    }

    @Override
    public ShopGoodsSpec findByCondition(ShopGoodsSpec condition) {
        return shopGoodsSpecDao.findByCondition(condition);
    }

    @Override
    public void deleteGoodsSpecByGoodsId(Long... goodsId) {
        shopGoodsSpecDao.deleteGoodsSpecByGoodsId(Paramap.create().put("goodIds", Lists.newArrayList(goodsId)));
    }

    //批量查询转map
    public Map<Long, ShopGoodsSpec> findMapSpec(List<Long> idList) {
        if(idList.size() == 0)return new HashMap<>();
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecDao.findByParams(Paramap.create().put("ids", idList));
        Map<Long, ShopGoodsSpec> shopGoodsSpecMap = new HashMap<>();
        for (ShopGoodsSpec itemSpec : shopGoodsSpecs) {
            shopGoodsSpecMap.put(itemSpec.getId(), itemSpec);
        }
        return shopGoodsSpecMap;
    }

    @Override
    public Page<GoodsStocksListView> findGoodsStocksInfo(Pageable pageable) {
        PageList<GoodsStocksListView> result=shopGoodsSpecDao.findGoodsStocksInfo(pageable.getParameter(), pageable.getPageBounds());
        return new Page<GoodsStocksListView>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public ShopGoodsSpec findByspecGoodsSerial(String specGoodsSerial) {
        return shopGoodsSpecDao.findByspecGoodsSerial(specGoodsSerial);
    }

    @Override
    public void updateSpecSaleNum(ShopGoodsSpec goodsSpec) {

    }

}
