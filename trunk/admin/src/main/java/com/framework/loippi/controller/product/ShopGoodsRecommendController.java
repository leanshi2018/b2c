package com.framework.loippi.controller.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContext;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsRecommend;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.result.sys.SelectGoodsSpec;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsRecommendService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Lists;

/**
 * Controller - 商品推荐
 *
 * @author longbh
 * @version 2.0
 */
@Controller("adminShopGoodsRecommendController")
@RequestMapping({"/admin/shop_goods_recommend"})
public class ShopGoodsRecommendController extends GenericController {

    @Resource
    private ShopGoodsRecommendService shopGoodsRecommendService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopGoodsClassService shopGoodsClassService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;

    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest request, String goodsKeywords, Long gcId, String brandNameStr,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo) {
        Pageable pager = new Pageable(pageNo, 10);
        pager.setParameter(Paramap.create().put("goodsKeywords", goodsKeywords).put("gcId", gcId).put("brandNameStr", brandNameStr));
        pager.setOrderProperty("sort");
        pager.setOrderDirection(Direction.ASC);
        Page<ShopGoodsRecommend> results = shopGoodsRecommendService.findByPage(pager);//结果集
        List<Long> idsArray = new ArrayList<>();
        for (ShopGoodsRecommend item : results.getContent()) {
            idsArray.add(item.getGoodsId());
        }
        Map<Long, ShopGoods> shopGoodsMap = shopGoodsService.findGoodsMap(idsArray);
        for (ShopGoodsRecommend item : results.getContent()) {
            item.setShopGoods(shopGoodsMap.get(item.getGoodsId()));
        }
        //商品分类查询及回显
        List<ShopGoodsClass> goodsClasses = shopGoodsClassService.findByParams2(Paramap.create().put("deep", 2));
//        List<ShopGoodsBrand> goodsBrand = shopGoodsBrandService.findAll();
        model.addAttribute("brandNameStr", brandNameStr);
        model.addAttribute("classList", goodsClasses);
        model.addAttribute("page", results);

        model.addAttribute("goodsKeywords", goodsKeywords);
        model.addAttribute("gcId", gcId);
//        model.addAttribute("brandId", brandId);

        return "goods/recommend/list";
    }

    @RequestMapping("/forward")
    public String forward(Model model, @RequestParam(required = false) Long id) {
        if (id != null && id != 0) {
            ShopGoodsRecommend shopGoodsRecommend = shopGoodsRecommendService.find(id);
            if (shopGoodsRecommend.getRecommendImage() != null) {
                shopGoodsRecommend.setImageList(Lists.newArrayList(shopGoodsRecommend.getRecommendImage().split(",")));
            } else {
                shopGoodsRecommend.setImageList(Lists.newArrayList());
            }
            model.addAttribute("shopGoodsRecommend", shopGoodsRecommend);
            ShopGoods shopGoods = shopGoodsService.find(shopGoodsRecommend.getGoodsId());
            model.addAttribute("shopGoods", shopGoods);
        } else {
            model.addAttribute("shopGoodsRecommend", new ShopGoodsRecommend());
        }
        return "goods/recommend/edit";
    }

    @RequestMapping("/edit")
    public String saveOrUpdate(@ModelAttribute ShopGoodsRecommend document, HttpServletRequest request, Model model) {
        RequestContext requestContext = new RequestContext(request);
        model.addAttribute("referer", "list.jhtml");

        if (document.getId() == null || document.getId() == 0) {
            document.setCreateTime(new Date());
            document.setUpdateTime(new Date());
            document.setStatus(1);
            document.setId(twiterIdService.getTwiterId());
            shopGoodsRecommendService.save(document);
            model.addAttribute("msg", requestContext.getMessage("Delivery_settings_msg17"));
        } else {
            document.setUpdateTime(new Date());
            shopGoodsRecommendService.update(document);
            model.addAttribute("msg", requestContext.getMessage("successful_modification"));
        }
        return Constants.MSG_URL;
    }

    /**
     * 删除推荐
     *
     * @param
     * @return
     */
    @RequestMapping("/delete")
    public String delete(Model model, @RequestParam Long ids[], HttpServletRequest request) {
        this.shopGoodsRecommendService.deleteAll(ids);
        model.addAttribute("msg", "物理删除成功");
        model.addAttribute("referer", request.getHeader("Referer"));
        return Constants.MSG_URL;
    }

    /**
     * 修改状态
     *
     * @param model
     * @param ids
     * @param request
     * @return
     */
    @RequestMapping("/updateStatus")
    public String updateStatus(Model model, @RequestParam Long ids, Integer status, HttpServletRequest request) {
        ShopGoodsRecommend shopGoodsRecommend = new ShopGoodsRecommend();
        shopGoodsRecommend.setId(ids);
        shopGoodsRecommend.setStatus(status);
        shopGoodsRecommend.setUpdateTime(new Date());
        shopGoodsRecommendService.update(shopGoodsRecommend);
        String info="";
        if (status==1){
            info="启用成功";
        } else{
            info="禁用成功";
        }
        model.addAttribute("msg", info);
        model.addAttribute("referer", request.getHeader("Referer"));
        return Constants.MSG_URL;
    }

    /**
     * 获取店铺商品列表
     *
     * @param pageNo
     * @return
     */

    @RequestMapping("/select")
    public String listGoods(Model model,
                            @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                            String goodsId,String type, String keyWord) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        ShopGoods goods = new ShopGoods();
        //设置状态开启
        //  goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
        goods.setState(GoodsState.GOODS_OPEN_STATE);
        //设置上架状态
        goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);
        //设置删除状态
        goods.setIsDel(2);
        //时间降序
        goods.setSortField("goodsAddTime");
        goods.setOrderBy("desc");
        goods.setGoodsListKeywords(keyWord);
        pager.setParameter(goods);
        pager.setPageSize(20);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        List<ShopGoods> list = byPage.getContent();
        List<Long> classIds = new ArrayList<>(), brandIds = new ArrayList<>();
        for (ShopGoods shopGoods : list) {
            classIds.add(shopGoods.getGcId());
            brandIds.add(shopGoods.getBrandId());
        }
        Map<Long, ShopGoodsClass> shopGoodsClassMap = shopGoodsClassService.findToMap(classIds);
        Map<Long, ShopGoodsBrand> shopGoodsBrandMap = shopGoodsBrandService.findToMap(brandIds);
        for (ShopGoods shopGoods : list) {
            Long gcId=Optional.ofNullable(shopGoods.getGcId()).orElse(-1L);
            if (gcId==-1L){
                shopGoods.setGcName("");
            }else{
                shopGoods.setGcName(shopGoodsClassMap.get(gcId).getGcName());
            }

            shopGoods.setBrandName(shopGoodsBrandMap.get(shopGoods.getBrandId()).getBrandName());
        }
        model.addAttribute("pager", byPage);
        model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
        model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
        model.addAttribute("goodsId", goodsId);
        model.addAttribute("keyWord", keyWord);
        if (type!=null){
            return "/goods/recommend/goods_select";
        }else{
            return "/shop_goods/goods_select";
        }

    }

    @RequestMapping("/selectSpec")
    public String listGoodsSpec(Model model,
                            @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(required = false, value = "goodsId", defaultValue = "") String goodsId,
                            @RequestParam(required = false, value = "goodsName", defaultValue = "")String goodsName) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        SelectGoodsSpec selectGoodsSpec = new SelectGoodsSpec();
        /*if (goodsId==null||!"".equals(goodsId)){
            System.out.println("");
        }else {
            selectGoodsSpec.setGoodsId(new Long(goodsId));
        }*/
        if (goodsId!=null){
            selectGoodsSpec.setGoodsId(new Long(goodsId));
        }
        selectGoodsSpec.setGoodsName(goodsName);
        pager.setParameter(selectGoodsSpec);
        pager.setPageSize(20);
        Page<SelectGoodsSpec> byPage = shopGoodsSpecService.listGoodsView(pager);
        List<SelectGoodsSpec> content = byPage.getContent();
        List<SelectGoodsSpec> list = new ArrayList<SelectGoodsSpec>();
        for (SelectGoodsSpec goodsSpec : content) {
            ShopGoodsSpec goodsSpec1 = shopGoodsSpecService.find(goodsSpec.getSpecId());
            ShopGoods shopGoods = shopGoodsService.find(goodsSpec.getGoodsId());
            GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, goodsSpec1);
            if (shopGoods.getGoodsType()==3){
                goodsSpec.setSpecInfo(goodsSpec1.getSpecGoodsSerial());
            }else{
                String specInfo = "";
                Map<String, String> map = goodsSpec1.getSepcMap();
                //遍历规格map,取出键值对,拼接specInfo
                if (map != null) {
                    Set<String> set = map.keySet();
                    for (String str : set) {
                        specInfo += str + ":" + map.get(str) + "、";
                    }
                    if(specInfo.length()==0){
                        specInfo = goodsSpec1.getSpecGoodsSerial();
                    }else{
                        specInfo = specInfo.substring(0, specInfo.length() - 1);
                    }
                }
                goodsSpec.setSpecInfo(specInfo);
            }
            list.add(goodsSpec);
        }
        model.addAttribute("pager", byPage);
        model.addAttribute("list", list);
        model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
        model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
        model.addAttribute("goodsId", goodsId);
        return "/common/buyFree/selsetgoods";


    }

}
