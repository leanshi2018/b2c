package com.framework.loippi.service.impl.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.activity.ShopActivityGoodsSpecDao;
import com.framework.loippi.dao.cart.ShopCartDao;
import com.framework.loippi.dao.order.ShopOrderGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsGoodsDao;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.result.common.goods.IdNameDto;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.goods.BrandListVo;
import com.framework.loippi.vo.stats.StatsCountVo;

/**
 * SERVICE - ShopGoods(商品表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsServiceImpl extends GenericServiceImpl<ShopGoods, Long> implements ShopGoodsService {

    @Autowired
    private ShopGoodsDao shopGoodsDao;
    @Autowired
    private ShopGoodsSpecService shopGoodsSpecService;
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopGoodsGoodsDao shopGoodsGoodsDao;
    @Resource
    private ShopOrderGoodsDao shopOrderGoodsDao;
    @Resource
    private ShopActivityGoodsSpecDao shopActivityGoodsSpecDao;
    @Resource
    private ShopCartDao shopCartDao;



    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopGoodsDao);
    }

    //保存商品信息
    @Override
    public Long saveGoods(ShopGoods shopGoods, String goodsSpecJson, String goodsJson) {
        shopGoods = GoodsUtils.enGoods(shopGoods);
        shopGoods.setCreateTime(new Date());
        if(!StringUtil.isEmpty(shopGoods.getSalePopulationIds())){
            shopGoods.setSalePopulationIds(","+shopGoods.getSalePopulationIds());
        }
        Long id = shopGoods.getId();
        shopGoodsDao.insert(shopGoods);
        //保存至goodsspec
        shopGoods.setId(id);
        saveToGoodsSpec(shopGoods, goodsSpecJson);
        saveToGoods(shopGoods, goodsJson);
        return id;
    }

    private void saveToGoods(ShopGoods goods, String goodsJson) {
        if (goodsJson == null || goodsJson.trim().equals("")) {
            return;
        }
        List<ShopGoodsGoods> shopGoodsGoodses = JacksonUtil.convertList(goodsJson, ShopGoodsGoods.class);
        List<Long> notDelId = new ArrayList<>();
        for (ShopGoodsGoods shopGoodsGoods : shopGoodsGoodses) {
            if (shopGoodsGoods.getId() != null && shopGoodsGoods.getId() != 0) {
                notDelId.add(shopGoodsGoods.getId());
            }
        }
        //删除被删除的记录
        if (notDelId.size() > 0) {
            shopGoodsGoodsDao.deleteNotIds(notDelId);
        }
        int index=0;
        for (ShopGoodsGoods shopGoodsGoods : shopGoodsGoodses) {
            int joinNum=1;
            if (goods.getJoinNum()!=null){
                String[] JoinNumSort=StringUtils.split(goods.getJoinNum(),",");
                joinNum=Integer.parseInt(JoinNumSort[index]);
            }
            //if (shopGoodsGoods.getId() != null && shopGoodsGoods.getId() != 0) {
            //    shopGoodsGoods.setJoinNum(joinNum);
            //    shopGoodsGoodsDao.update(shopGoodsGoods);
            //} else {
                shopGoodsGoods.setId(twiterIdService.getTwiterId());
                shopGoodsGoods.setGoodId(goods.getId());
                shopGoodsGoods.setJoinNum(joinNum);
                shopGoodsGoodsDao.insert(shopGoodsGoods);
            //}
            index++;
        }
    }

    /**
     * 保存规格
     *
     * @param goods
     * @param goodsSpecJson
     */
    private void saveToGoodsSpec(ShopGoods goods, String goodsSpecJson) {
        //在保存之前首先删除goodsSpec表中关于这个goodsId的数据
        shopGoodsSpecService.deleteGoodsSpecByGoodsId(goods.getId());// TODO 这里只是修改了状态
        if (goodsSpecJson != null && !goodsSpecJson.trim().equals("")) {
            //准备创建表shop_goods_sepc的实体类对象
            List<ShopGoodsSpec> goodsSpecs = JacksonUtil.convertList(goodsSpecJson, ShopGoodsSpec.class);
            //循环获得goodsspec
            for (int i = 0; i < goodsSpecs.size(); i++) {
                ShopGoodsSpec goodsSpec = (ShopGoodsSpec) goodsSpecs.get(i);
                //保存goodsspecs
                if (Optional.ofNullable(goodsSpec.getId()).orElse(0L)!=0L){
                    ShopGoodsSpec spec = shopGoodsSpecService.find(goodsSpec.getId());
                    goodsSpec.setSpecGoodsStorage(spec.getSpecGoodsStorage());
                    shopGoodsSpecService.update(goodsSpec);
                }else {
                    //设置商品id
                    goodsSpec.setGoodsId(goods.getId());
                    //设置销售量为0
                    goodsSpec.setSpecSalenum(0);
                    Long specId = twiterIdService.getTwiterId();
                    goodsSpec.setId(specId);
                    shopGoodsSpecService.save(goodsSpec);
                    ////避免库存出现紊乱 待改进
                    //List<ShopGoodsSpec> shopGoodsSpecList=shopGoodsSpecService.findList(Paramap.create().put("goodsId",goods.getId()).put("specGoodsSpec",goodsSpec.getSpecGoodsSpec()));
                    //shopGoodsSpecService.save(goodsSpec);
                    //if (shopGoodsSpecList!=null && shopGoodsSpecList.size()>0){
                    //    ShopOrderGoods shopOrderGoods=new ShopOrderGoods();
                    //    List<Long> specIds=new ArrayList<>();
                    //    for (ShopGoodsSpec item:shopGoodsSpecList) {
                    //        specIds.add(item.getId());
                    //    }
                    //    shopOrderGoods.setSpecIds(specIds);
                    //    shopOrderGoods.setSpecId(specId);
                    //    shopOrderGoodsDao.updateBatch(shopOrderGoods);
                    //    //活动
                    //    ShopActivityGoodsSpec shopActivityGoodsSpec=new ShopActivityGoodsSpec();
                    //    shopActivityGoodsSpec.setSpecIds(specIds);
                    //    shopActivityGoodsSpec.setSpecId(specId);
                    //    shopActivityGoodsSpecDao.updateBatchSpec(shopActivityGoodsSpec);
                    //    //购物车
                    //    ShopCart shopCart=new ShopCart();
                    //    shopCart.setSpecIds(specIds);
                    //    shopCart.setSpecId(specId);
                    //    shopCartDao.updateBatchSpec(shopCart);
                    //    //客户那边的库存表,使二者规格保持一致
                    //    //RdInventoryWarning
                }
                if (i == 0) {
                    //设置商品specid
                    goods.setSpecId(goodsSpec.getId());
                    goods.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
                    goods.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
                    goods.setGoodsRetailProfit(goodsSpec.getSpecRetailProfit());//TODO
                    goods.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
                    goods.setPpv(goodsSpec.getPpv());
                    goods.setBigPpv(goodsSpec.getBigPpv());
                    goods.setWeight(goodsSpec.getWeight());
                    goods.setShelfLife(goodsSpec.getShelfLife());

                }
            }
        } else {
            //如果未设置规格,默认设置一条规格
            ShopGoodsSpec goodsSpec = new ShopGoodsSpec();
            goodsSpec.setId(twiterIdService.getTwiterId());
            //设置商品id
            goodsSpec.setGoodsId(goods.getId());
            //价格
            goodsSpec.setSpecBigPrice(goods.getGoodsBigPrice());
            goodsSpec.setSpecMemberPrice(goods.getGoodsMemberPrice());
            goodsSpec.setSpecRetailPrice(goods.getGoodsRetailPrice());
            goodsSpec.setSpecRetailProfit(goods.getGoodsRetailProfit());//TODO
            goodsSpec.setPpv(goods.getPpv());
            goodsSpec.setBigPpv(goods.getBigPpv());

            //库存
            goodsSpec.setSpecGoodsStorage(goods.getGoodsTotalStorage());
            //货号
            goodsSpec.setSpecGoodsSerial(goods.getGoodsSerial());
            //设置销售量为0
            goodsSpec.setSpecSalenum(0);
            //规格商品库存
            goodsSpec.setSpecGoodsStorage(0);
            //保存goodsspecs
            shopGoodsSpecService.save(goodsSpec);
            //设置商品specid
            goods.setSpecId(goodsSpec.getId());
            goods.setGoodsMemberPrice(goodsSpec.getSpecMemberPrice());
            goods.setGoodsRetailPrice(goodsSpec.getSpecRetailPrice());
            goods.setGoodsRetailProfit(goods.getGoodsRetailProfit());
            goods.setGoodsBigPrice(goodsSpec.getSpecBigPrice());
            goods.setPpv(goodsSpec.getPpv());
            goods.setBigPpv(goodsSpec.getBigPpv());
            goods.setWeight(goodsSpec.getWeight());
            goods.setShelfLife(goodsSpec.getShelfLife());
        }
        //再次修改goods表
        //判断是否存有goodsspec
        if (goods.getSpecId() != null) {
            ShopGoods tagGoods = new ShopGoods();
            tagGoods.setId(goods.getId());
            tagGoods.setSpecId(goods.getSpecId());
            tagGoods.setGoodsMemberPrice(goods.getGoodsMemberPrice());
            tagGoods.setGoodsRetailPrice(goods.getGoodsRetailPrice());
            tagGoods.setGoodsRetailProfit(goods.getGoodsRetailProfit());
            tagGoods.setGoodsBigPrice(goods.getGoodsBigPrice());
            tagGoods.setPpv(goods.getPpv());
            tagGoods.setBigPpv(goods.getBigPpv());
            tagGoods.setWeight(goods.getWeight());
            tagGoods.setShelfLife(goods.getShelfLife());
            tagGoods.setGoodsImageMore(goods.getGoodsImageMore());
            shopGoodsDao.update(tagGoods);
        }
    }


    /**
     * 编辑商品在库房信息
     *
     * @param goods
     * @param goodsSpecJson
     * @return
     */
    @Override
    public Integer updateGoods(ShopGoods goods, String goodsSpecJson, String specNameJson) {
        goods = GoodsUtils.enGoods(goods);
        //===========更新商品信息=========
        goods.setGoodsClick(null);
        //商品状态值 30:审核通过,40:违规下架,50:审核未通过,60:待审核
        goods.setSalenum(null);
        goods.setGoodsTotalStorage(null);//设置默认值为0
        //保存goods
        goods.setUpdateTime(new Date());
        if (!StringUtil.isEmpty(goods.getSalePopulationIds())){
            if (!goods.getSalePopulationIds().startsWith(",")){
                goods.setSalePopulationIds(","+goods.getSalePopulationIds());
            }
        }
        shopGoodsDao.update(goods);
        //==========更新规格=============
        Long goodsId = goods.getId();
        String goodsSp = goods.getGoodsSpec();
        //获取以前的规格信息
        ShopGoods oldGoods = shopGoodsDao.find(goodsId);
        String oldGoodsSp = oldGoods.getGoodsSpec();
        // 获取该商品库对应的商品列表
        if (StringUtil.isEmpty(oldGoodsSp)) {
            // 保存至goodsspec
            saveToGoodsSpec(goods, goodsSpecJson);
            return 1;
        } else {
            if (oldGoodsSp.equals(goodsSp) && !StringUtil.isEmpty(goodsSpecJson)) {
                if (!StringUtil.isEmpty(goodsSpecJson)) {
                    //准备创建表shop_goods_sepc的实体类对象
                    List<ShopGoodsSpec> goodsSpecs = JacksonUtil.convertList(goodsSpecJson, ShopGoodsSpec.class);
                    //循环获得goodsspec
                    for (int i = 0; i < goodsSpecs.size(); i++) {
                        ShopGoodsSpec goodsSpec = (ShopGoodsSpec) goodsSpecs.get(i);
                        String specGoodsSpec = goodsSpec.getSpecGoodsSpec();
                        ShopGoodsSpec condition = new ShopGoodsSpec();
                        condition.setGoodsId(goodsId);
                        condition.setSpecGoodsSpec(specGoodsSpec);
                        ShopGoodsSpec gs = shopGoodsSpecService.findByCondition(condition);
                        if (gs==null){
                            goodsSpec.setId(twiterIdService.getTwiterId());
                            goodsSpec.setGoodsId(goodsId);
                            if (goodsSpec.getShelfLife()==null){
                                goodsSpec.setShelfLife(0);
                            }
                            if (goodsSpec.getSpecSalenum()==null){
                                goodsSpec.setSpecSalenum(0);
                            }

                            shopGoodsSpecService.save(goodsSpec);
                        }
                        if (gs != null) {
                            gs.setSpecGoodsSerial(goodsSpec.getSpecGoodsSerial());
                            gs.setSpecIsopen(goodsSpec.getSpecIsopen());
                            //价格
                            gs.setSpecRetailPrice(goodsSpec.getSpecRetailPrice());
                            gs.setSpecRetailProfit(goodsSpec.getSpecRetailProfit());
                            gs.setSpecMemberPrice(goodsSpec.getSpecMemberPrice());
                            gs.setSpecBigPrice(goodsSpec.getSpecBigPrice());
                            gs.setPpv(goodsSpec.getPpv());
                            gs.setBigPpv(goodsSpec.getBigPpv());

                            //gs.setSpecGoodsStorage(goodsSpec.getSpecGoodsStorage());
                            gs.setSpecBarCode(goodsSpec.getSpecBarCode());
                            gs.setSpecPic(goodsSpec.getSpecPic());
                            gs.setWeight(goodsSpec.getWeight());
                            gs.setCostPrice(goodsSpec.getCostPrice());
                            //更新goodsspecs
                            shopGoodsSpecService.update(gs);
                        }
                    }
                }
            } else {
                // 保存至goodsspec
                saveToGoodsSpec(goods, goodsSpecJson);
            }
        }
        saveToGoods(goods, specNameJson);
        return 1;
    }

    @Override
    public List<ShopGoods> findByPageCombination(Long GooodsId) {
        return shopGoodsDao.findByPageCombination(GooodsId);
    }

    @Override
    public Long countPageCombination(Long GooodsId) {
        return shopGoodsDao.countPageCombination(GooodsId);
    }

    @Override
    public void updateAll(Map<String, Object> var1) {
        shopGoodsDao.updateAll(var1);

    }

    /**
     * 修改库存
     *
     * @param GooodsId
     * @param saleSum
     * @return
     */
    @Override
    public boolean updateSaleNum(Long GooodsId, Integer saleSum) {
        ShopGoods shopGoods = shopGoodsDao.find(GooodsId);
        Integer salenum = shopGoods.getSalenum();
        Long stock = shopGoods.getStock();
        stock = stock - saleSum;
        salenum = salenum + saleSum;
        shopGoods.setSalenum(salenum);
        shopGoods.setStock(stock);
        Long update = shopGoodsDao.update(shopGoods);
        //TODO修改规格库存

        if (update != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<BrandListVo> countOnSaleByBrand(Map<String, Object> map) {
        return shopGoodsDao.countOnSaleByBrand(map);
    }

    //商品统计
    @Override
    public List<StatsCountVo> listStatsCountVo() {
        return shopGoodsDao.listStatsCountVo();
    }

    //获取多条商品信息,并转map
    public Map<Long, ShopGoods> findGoodsMap(List<Long> goodList) {
        if (goodList.size() == 0) return new HashMap<>();
        List<ShopGoods> shopOrderGoodses = shopGoodsDao.findByParams(Paramap.create().put("ids", goodList).put("showCommentNum",1));
        Map<Long, ShopGoods> orderMap = new HashMap<>();
        for (ShopGoods shopOrderGoods : shopOrderGoodses) {
            orderMap.put(shopOrderGoods.getId(), shopOrderGoods);
        }
        return orderMap;
    }

    //查询活动热门商品列表
    @Override
    public Map<Long, List<IdNameDto>> findGoodsBySpecMap(List<Long> goodIds,Integer type) {
        if (goodIds.size() == 0) {
            return new HashMap<>();
        }
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findList(Paramap.create().put("shopGoodsIds", goodIds).put("specIsopen",1));
        Map<Long, List<IdNameDto>> specMaps = new HashMap<>();
        for (ShopGoodsSpec shopGoodsSpec : shopGoodsSpecs) {
            List<IdNameDto> shopGoodsSpecList = specMaps.get(shopGoodsSpec.getGoodsId());
            if (shopGoodsSpecList == null) {
                shopGoodsSpecList = new ArrayList<>();
            }
            //普通商品
            if (type==1){
                shopGoodsSpecList.add(IdNameDto.of(shopGoodsSpec));
            }else{
                //组合商品 解决单件商品的规格问题
                IdNameDto idNameDto=IdNameDto.of(shopGoodsSpec);
                idNameDto.setName(idNameDto.getName().replace(",",";"));
                shopGoodsSpecList.add(idNameDto);
            }

            specMaps.put(shopGoodsSpec.getGoodsId(), shopGoodsSpecList);
        }
        return specMaps;
    }

    @Override
    public List<ShopGoods> findOweGoods() {
        List<ShopGoods> goodsShow1 = shopGoodsDao.findByShow1AndType();
        List<ShopGoods> goodsShow2 = shopGoodsDao.findByShow2AndType();
        if (goodsShow2.size()>0){
            goodsShow1.addAll(goodsShow2);
        }
        return goodsShow1;
    }
}










