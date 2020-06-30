package com.framework.loippi.service.impl.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsGoodsDao;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.service.common.ProductService;
import com.framework.loippi.service.product.ShopGoodsSpecService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ShopGoodsSpecService shopGoodsSpecService;

    @Autowired
    private ShopGoodsDao shopGoodsDao;
    @Autowired
    private ShopGoodsGoodsDao shopGoodsGoodsDao;

    /**
     * 修改库存
     *
     * @parm GoodsSpec 需要2个参数 specId 以及出售数量 specSalenum(这个出售数量是本次的出售数量) 返回 0 则保存失败 返回 1 则保存成功
     */
    @Override
    public void updateStorage(ShopGoodsSpec goodsSpec, ShopGoods goods) {
        if (goodsSpec.getGoodsId() != null && goodsSpec.getSpecSalenum() != null && goods.getId() != null) {
            shopGoodsSpecService.updateGoodsSpecStorage(goodsSpec);
            ShopGoods newGoods = new ShopGoods();
            newGoods.setId(goods.getId());
            newGoods.setGoodsImageMore("");
            newGoods.setStock(goods.getStock() - goodsSpec.getSpecSalenum());
            int salenum = goods.getSalenum() + goodsSpec.getSpecSalenum();
            newGoods.setSalenum(salenum < 0 ? 0 : salenum);
            shopGoodsDao.update(newGoods);
            //组合商品不但要扣在组合商品上面 还要扣组合的具体商品
            /*if (goods.getGoodsType()==3){
                ShopGoodsSpec spec=shopGoodsSpecService.find(goodsSpec.getId());
                Map<String, String> specMap = JacksonUtil.readJsonToMap(spec.getSpecGoodsSpec());
                Map<String, String> goodsMap = JacksonUtil.readJsonToMap(spec.getSpecName());
                Set<String> keySpec = specMap.keySet();
                Set<String> keyGoods = goodsMap.keySet();
                Iterator<String> itSpec = keySpec.iterator();
                Iterator<String> itGoods = keyGoods.iterator();
                while (itSpec.hasNext() && itGoods.hasNext())
                {
                    String specId=itSpec.next();
                    String goodsId=itGoods.next();
                    //拿到组合的具体数量情况
                    int joinNum=1;
                    List<ShopGoodsGoods> shopGoodsGoodsList=shopGoodsGoodsDao.findByParams(Paramap.create().put("goodId",goods.getId()).put("combineGoodsId",goodsId));
                    if (!shopGoodsGoodsList.isEmpty()){
                        joinNum=Optional.ofNullable(shopGoodsGoodsList.get(shopGoodsGoodsList.size()-1).getJoinNum()).orElse(1);
                    }
                    ShopGoodsSpec shopGoodsSpec=new ShopGoodsSpec();
                    shopGoodsSpec.setId(Long.parseLong(specId));
                    shopGoodsSpec.setSpecSalenum(goodsSpec.getSpecSalenum()*joinNum);
                    shopGoodsSpecService.updateGoodsSpecStorage(shopGoodsSpec);
                    ShopGoods shopGoods = shopGoodsDao.find(Long.parseLong(goodsId));
                    shopGoods.setStock(shopGoods.getStock() - goodsSpec.getSpecSalenum()*joinNum);
                     salenum = shopGoods.getSalenum() + goodsSpec.getSpecSalenum()*joinNum;
                    shopGoods.setSalenum(salenum < 0 ? 0 : salenum);
                    shopGoodsDao.update(shopGoods);
                }
            }*/
        }
    }

    @Override
    public void updateStorageNew(ShopGoodsSpec goodsSpec, ShopGoods goods, Integer logisticType) {
        if (goodsSpec.getGoodsId() != null && goodsSpec.getSpecSalenum() != null && goods.getId() != null) {

            if (logisticType==1){
                shopGoodsSpecService.updateGoodsSpecStorage(goodsSpec);
            }else {//自提
                //shopGoodsSpecService.updateSpecSaleNum(goodsSpec);//只改售出数量
            }



            ShopGoods newGoods = new ShopGoods();
            newGoods.setId(goods.getId());
            newGoods.setGoodsImageMore("");
            newGoods.setStock(goods.getStock() - goodsSpec.getSpecSalenum());
            int salenum = goods.getSalenum() + goodsSpec.getSpecSalenum();
            newGoods.setSalenum(salenum < 0 ? 0 : salenum);
            shopGoodsDao.update(newGoods);
        }
    }
}