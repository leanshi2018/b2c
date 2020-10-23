package com.framework.loippi.controller.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonSpec;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.product.ShopGoodsTypeSpec;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.common.goods.IdNameDto;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.common.ShopCommonSpecService;
import com.framework.loippi.service.common.ShopCommonSpecValueService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import com.framework.loippi.service.product.ShopGoodsRecommendService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.product.ShopGoodsTypeService;
import com.framework.loippi.service.product.ShopGoodsTypeSpecService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import com.framework.loippi.vo.goods.SpecVo;

/**
 * Controller - 商品表
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopGoodsController")
@RequestMapping({"/admin/shop_goods"})
public class ShopGoodsSysController extends GenericController {

    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsClassService shopGoodsClassService;
    @Resource
    private ShopGoodsTypeService shopGoodsTypeService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopGoodsGoodsService shopGoodsGoodsService;
    @Resource
    private RdRanksService rdRanksService;
    @Resource
    private ShopGoodsRecommendService shopGoodsRecommendService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopGoodsTypeSpecService shopGoodsTypeSpecService;
    @Resource
    private ShopCommonSpecService shopCommonSpecService;
    @Resource
    private ShopCommonSpecValueService shopCommonSpecValueService;


    /**
     * 删除操作
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(Long[] ids, int type, Model model, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        if (ids == null || ids.length == 0) {
            model.addAttribute("msg", "删除失败，ID为空");
            return "redirect:list.jhtml";
        }
        try {
            Map<String, Object> mapids = new HashMap<>();
            mapids.clear();
            mapids.put("shopGoodsIds", ids);

            Long shopOrderGoodses = shopOrderGoodsService.count(
                Paramap.create().put("goodIds", ids));
            if (shopOrderGoodses > 0) {
                model.addAttribute("msg", "删除失败,存在已下单的商品");
                model.addAttribute("referer", request.getHeader("Referer"));
                return Constants.MSG_URL;
            }

            if (type == 2) {
                map.put("ids", ids);
                map.put("isDel", GoodsState.GOODS_HAS_DELETE);
                map.put("goodsShow", GoodsState.GOODS_OFF_SHOW);
                map.put("goodsImageMore","");//防止图片消失不能为空
                this.shopGoodsService.updateAll(map);
                model.addAttribute("msg", "删除成功");
                model.addAttribute("referer", request.getHeader("Referer"));
            } else {
                map.put("ids", ids);
                map.put("isDel", GoodsState.GOODS_NOT_DELETE);
                map.put("goodsShow", GoodsState.GOODS_ON_SHOW);
                map.put("goodsImageMore","");//防止图片消失不能为空
                this.shopGoodsService.updateAll(map);
                model.addAttribute("msg", "恢复成功");
                model.addAttribute("referer", request.getHeader("Referer"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:list.jhtml";
        }
        return Constants.MSG_URL;
    }


    /**
     * 物理删除操作
     */
    @RequestMapping(value = "/cut", method = RequestMethod.POST)
    public String cut(Long[] ids, Model model, HttpServletRequest request) {
        if (ids == null || ids.length == 0) {
            model.addAttribute("msg", "物理删除失败，ID为空");
            return "redirect:list.jhtml";
        }
        try {
            this.shopGoodsService.deleteAll(ids);
            shopGoodsSpecService.deleteGoodsSpecByGoodsId(ids);
            model.addAttribute("msg", "物理删除成功");
            model.addAttribute("referer", request.getHeader("Referer"));
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:list.jhtml";
        }
        return Constants.MSG_URL;
    }

    /**
     * 套装商品列表
     */
    @RequestMapping(value = "/listGroupGoods/{isShow}")
    public ModelAndView listGroupGoods(@ModelAttribute ShopGoods goodsPlatform,
        @PathVariable Integer isShow,
        @RequestParam(required = false, value = "goodsName", defaultValue = "") String goodsName,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNumber) {
        ModelAndView model = new ModelAndView("/shop_goods/list_group");
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNumber);
        goodsPlatform.setGoodsType(3);
        goodsPlatform.setGoodsShow(isShow);
        goodsPlatform.setIsDel(GoodsState.GOODS_NOT_DELETE);
        goodsPlatform.setOrderAll(GoodsState.DEFAULT_ORDER);
        pager.setParameter(goodsPlatform);
        pager.setOrderProperty("sort");
        pager.setOrderDirection(Order.Direction.ASC);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        Map<String, String> treeMap = new HashMap<>();
        Map<String, String> classNameMap = new HashMap<>();
        List<ShopGoodsClass> firstGoodsClasses = new ArrayList<>();
        List<ShopGoods> list = byPage.getContent();
        //商品分类查询及回显
        List<ShopGoodsClass> goodsClasses = new ArrayList<>();
        List<ShopGoodsClass> shopGoodsClassList = shopGoodsClassService.findAll();
        for (ShopGoodsClass item : shopGoodsClassList) {
            if (Optional.ofNullable(item.getDeep()).orElse(0) == 2) {
                goodsClasses.add(item);
                treeMap.put(item.getId() + "", item.getGcParentId() + "");
            } else {
                if (Optional.ofNullable(item.getGcParentId()).orElse(1L) == 0L) {
                    firstGoodsClasses.add(item);
                }
                classNameMap.put(item.getId() + "", item.getGcName());
            }
        }
        for (ShopGoods item : list) {
            String tree = treeMap.get(item.getGcId() + "");
            if (tree != null && !"".equals(tree)) {
                item.setGcParentName(Optional.ofNullable(classNameMap.get(tree)).orElse(""));
            }
        }
        List<ShopGoodsBrand> goodsBrand = shopGoodsBrandService.findAll();
        //获取动态会员等级 效率不高待优化
        List<RdRanks> shopMemberGradeList = (List) getData(list).get("shopMemberGradeList");
        list = (List) getData(list).get("shopGoodsList");
        model.addObject("shopMemberGradeList", shopMemberGradeList);
        model.addObject("brands", goodsBrand);
        model.addObject("firstGoodsClasses", firstGoodsClasses);
        model.addObject("gcommon", goodsPlatform);
        model.addObject("classList", goodsClasses);
        model.addObject("goodsGcId", goodsPlatform.getGcId());
        model.addObject("gcParentId", goodsPlatform.getGcParentId());
        model.addObject("goodsGcName", goodsPlatform.getGcName());
        model.addObject("goodsListKeywords", goodsPlatform.getGoodsListKeywords());
        model.addObject("goodsName", goodsName);
        model.addObject("goodsList", list);// 结果集
        model.addObject("page", byPage);
        return model;
    }

    /**
     * 积分商品列表
     */
    @RequestMapping(value = "/listPointGoods/{isShow}")
    public ModelAndView listPointGoods(@ModelAttribute ShopGoods goodsPlatform,
        @PathVariable Integer isShow,
        @RequestParam(required = false, value = "goodsName", defaultValue = "") String goodsName,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNumber) {
        ModelAndView model = new ModelAndView("/shop_goods/list_point");
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNumber);
        goodsPlatform.setGoodsType(2);
        goodsPlatform.setGoodsShow(isShow);
        goodsPlatform.setIsDel(GoodsState.GOODS_NOT_DELETE);
        pager.setParameter(goodsPlatform);
        pager.setOrderProperty("sort");
        pager.setOrderDirection(Order.Direction.ASC);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        List<ShopGoods> list = byPage.getContent();
//        List<ShopGoodsClass> goodsClasses = shopGoodsClassService.findByParams2(Paramap.create().put("deep", 2));
        //获取动态会员等级 效率不高待优化
        List<RdRanks> shopMemberGradeList = (List) getData(list).get("shopMemberGradeList");
        list = (List) getData(list).get("shopGoodsList");
        model.addObject("shopMemberGradeList", shopMemberGradeList);
        model.addObject("gcommon", goodsPlatform);
//        model.addObject("classList", goodsClasses);
        model.addObject("goodsGcId", goodsPlatform.getGcId());
        model.addObject("gcParentId", goodsPlatform.getGcParentId());
        model.addObject("goodsName", goodsName);
        model.addObject("goodsListKeywords", goodsPlatform.getGoodsListKeywords());
        model.addObject("goodsList", list);// 结果集
        model.addObject("page", byPage);
        return model;
    }

    /**
     * 普通商品库商品列表
     */
    @RequestMapping(value = "/list/{isShow}")
    public ModelAndView list(
        @PathVariable Integer isShow,
        @ModelAttribute ShopGoods goodsPlatform,
        @RequestParam(required = false, value = "goodsName", defaultValue = "") String goodsName,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNumber) {
        ModelAndView model = new ModelAndView("/shop_goods/list");
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNumber);
        goodsPlatform.setGoodsType(1);
        goodsPlatform.setGoodsShow(isShow);
        goodsPlatform.setIsDel(GoodsState.GOODS_NOT_DELETE);
        goodsPlatform.setOrderAll(GoodsState.DEFAULT_ORDER);
        pager.setParameter(goodsPlatform);
        pager.setOrderProperty("sort");
        pager.setOrderDirection(Order.Direction.ASC);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        Map<String, String> treeMap = new HashMap<>();
        Map<String, String> classNameMap = new HashMap<>();
        List<ShopGoods> list = byPage.getContent();

        //商品分类查询及回显
        List<ShopGoodsClass> goodsClasses = new ArrayList<>();//二级分类
        List<ShopGoodsClass> firstGoodsClasses = new ArrayList<>();
        List<ShopGoodsClass> shopGoodsClassList = shopGoodsClassService.findAll();
        for (ShopGoodsClass item : shopGoodsClassList) {
            if (Optional.ofNullable(item.getDeep()).orElse(0) == 2) {
                goodsClasses.add(item);
                treeMap.put(item.getId() + "", item.getGcParentId() + "");
            } else {
                if (Optional.ofNullable(item.getGcParentId()).orElse(1L) == 0L) {
                    firstGoodsClasses.add(item);
                }
                classNameMap.put(item.getId() + "", item.getGcName());
            }
        }
        for (ShopGoods item : list) {
            String tree = treeMap.get(item.getGcId() + "");
            if (tree != null && !"".equals(tree)) {
                item.setGcParentName(Optional.ofNullable(classNameMap.get(tree)).orElse(""));
            }
        }
        List<ShopGoodsBrand> goodsBrand = shopGoodsBrandService.findAll();
        //获取动态会员等级 效率不高待优化
        List<RdRanks> shopMemberGradeList = (List) getData(list).get("shopMemberGradeList");
        list = (List) getData(list).get("shopGoodsList");
        model.addObject("shopMemberGradeList", shopMemberGradeList);
        model.addObject("brands", goodsBrand);
        model.addObject("gcommon", goodsPlatform);
        model.addObject("classList", goodsClasses);
        model.addObject("firstGoodsClasses", firstGoodsClasses);
        model.addObject("goodsGcId", goodsPlatform.getGcId());
        model.addObject("gcParentId", goodsPlatform.getGcParentId());
        model.addObject("goodsGcName", goodsPlatform.getGcName());
        model.addObject("goodsKeywords", goodsPlatform.getGoodsKeywords());
        model.addObject("goodsListKeywords", goodsPlatform.getGoodsListKeywords());
        model.addObject("goodsName", goodsName);
        model.addObject("goodsList", list);// 结果集
        model.addObject("page", byPage);
        model.addObject("isShow", isShow);
        model.addObject("searchStartTime", goodsPlatform.getSearchStartTime());
        model.addObject("searchEndTime", goodsPlatform.getSearchEndTime());
        model.addObject("salePopulation", goodsPlatform.getSalePopulation());
        return model;
    }

    /**
     * 发布商品前选择分类
     */
    @RequestMapping(value = "/sellIndex")
    public ModelAndView sellIndex(@RequestParam(value = "goodsId", required = false, defaultValue = "") Long goodsId,
        @RequestParam(defaultValue = "1") Integer goodsType) {
        ModelAndView model = new ModelAndView("/shop_goods/pro_sell_index");
        List<ShopGoodsClass> list = shopGoodsClassService
            .findByParams2(Paramap.create().put("gcParentId", 0L).put("gcShow", 1));
        model.addObject("datas", list);
        ShopGoods shopGoods = shopGoodsService.find(goodsId);
        model.addObject("goodsInfo", shopGoods);
        model.addObject("goodsType", goodsType);
        model.addObject("goodsId", goodsId);
        return model;
    }

    @RequestMapping(value = "/findChildClass")
    public
    @ResponseBody
    Map<String, String> findChildClass(@RequestParam(value = "id") Long id) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        List<ShopGoodsClass> classList = shopGoodsClassService
            .findByParams2(Paramap.create().put("gcParentId", id).put("gcShow", 1));
        String json = "null";
        for (ShopGoodsClass shopGoodsClass : classList) {
            shopGoodsClass.setStingid(shopGoodsClass.getId().toString());
            shopGoodsClass.setStringParentId(shopGoodsClass.getGcParentId().toString());
        }
        if (classList != null && classList.size() > 0) {
            json = JacksonUtil.toJson(classList);
        }
        map.put("result", json);
        map.put("success", "true");
        return map;
    }

    /**
     * 校验分类下是否还有商品
     */
    @RequestMapping("/validateGoodsByGcId")
    public
    @ResponseBody
    Boolean validateGoodsByGcId(@RequestParam Long id) {
        Map<String, Object> params = Paramap.create().put("gcId", id);
        Long results = shopGoodsService.count(params);
        if (results > 0) {
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping(value = "/savePro")
    public
    @ResponseBody
    Message savePro(HttpServletRequest request, ShopGoods goods, String specNameJson) {
        try {
            String goodsName = goods.getGoodsName();
            boolean b = validateGoodsName(goodsName, goods.getId());
            if (!b) {
                return Message.error("商品名字重复");
            }
            //设置utf-8
            request.setCharacterEncoding("utf-8");
            String goodsSpecJson = request.getParameter("goodsSpecJson");
            String goodsJson = request.getParameter("goodsJson");
            if (goodsSpecJson == null) {
                return Message.error("请添加正确的规格信息");
            }
            //调用保存的service的方法,返回状态0为失败1为成功
            goods.setId(twiterIdService.getTwiterId());
            goods.setSalenum(GoodsState.DEFAULT_SALENUM);
            goods.setState(GoodsState.GOODS_OPEN_STATE);
            goods.setIsDel(GoodsState.GOODS_NOT_DELETE);
            goods.setUpdateTime(new Date());
            goods.setEvaluaterate(1D);
            Long goodsId = shopGoodsService.saveGoods(goods, goodsSpecJson, specNameJson);
            //判断是否成功
            if (goodsId.equals("0")) {
                //将失败的信号传入前台
                return Message.error("商品数据保存失败");
            }
            //将成功的信号传导前台
            return Message.success("商品数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            //将失败的信号传到前台
            return Message.error("商品数据保存异常");
        }
    }


    /**
     * @param @return 设定文件
     * @return ModelAndView 返回类型
     * @throws Exception 修改商品 填写基本信息
     * @Title: selldetail
     */
    @RequestMapping(value = "/editgoods")
    public String editgoods(HttpServletRequest request,
        @RequestParam(value = "id") Long goodsId,
        @RequestParam(value = "gcId", required = false, defaultValue = "") Long gcId,
        @RequestParam(value = "goodsType", required = false, defaultValue = "") Integer goodsType,
        ModelMap model) throws Exception {
        //根据goodsid获得goods
        ShopGoods goods = new ShopGoods();
        goods.setId(goodsId);
        goods.setGcId(gcId);
        goods.setGoodsType(goodsType);
        goods.setGoodsShow(1);
        if (goodsId != null) {
            goods = shopGoodsService.find(goodsId);
        }
        List<ShopGoods> shopGoodsList=new ArrayList<>();
        shopGoodsList.add(goods);
        List<RdRanks> shopMemberGradeList = (List) getData(shopGoodsList).get("shopMemberGradeList");
        shopGoodsList = (List) getData(shopGoodsList).get("shopGoodsList");
        //放入model
        model.addAttribute("goods", shopGoodsList.get(0));
        /* * 通过类型id获得类型下的品牌,规格,属性
         * 首先通过类型id获得goodsTypeVo
         * 在这个超类中,有3个list,是品牌,规格,属性*/
        List<ShopGoodsBrand> brands = new ArrayList<ShopGoodsBrand>();
        List<SpecVo> specs = new ArrayList<SpecVo>();
        Long typeId = null;
        //分类名称
        String catename;
        if (gcId != null) {
            ShopGoodsClass goodsClass = shopGoodsClassService.find(gcId);
            //MqLogUtils.setMqMessage(request,null ,null ,"查询单个商品分类"+goodsClass.getGcId(),MqLogUtils.SYS_LOG_TYPE);
            catename = goodsClass.getGcName();
            typeId = goodsClass.getTypeId();
        } else {
            gcId = goods.getGcId();
            catename = goods.getGcName();
            typeId = goods.getTypeId();
            //规格
            //通过goodsid在数据库中查出goods_spec
            //得到goodsSpec的list
            List<ShopGoodsSpec> goodsSpecs = shopGoodsSpecService.findListByGoodsId(goods.getId());
            Integer stock = 0;
            //MqLogUtils.setMqMessage(request,null ,null ,"查询商品规格列表",MqLogUtils.SYS_LOG_TYPE);
            for (int i = 0; i < goodsSpecs.size(); i++) {
                if (goodsSpecs.get(i).getSpecGoodsSpec() != null && !goodsSpecs.get(i).getSpecGoodsSpec().trim()
                    .equals("")) {
                    goodsSpecs.get(i)
                        .setSpecValueIDS(GoodsUtils.getThisGoodsAllSpecValueId(goodsSpecs.get(i).getSpecGoodsSpec()));
                    stock += goodsSpecs.get(i).getSpecGoodsStorage();
                }
            }
            //因为进货在客户那里进行进货 只改变规格商品数量 没改吧商品数量所以进行验证 如果数量不同就进行更新
            validationInventory(stock, goods);
            //放入model
            model.addAttribute("goodsSpecs", goodsSpecs);
            if (goodsSpecs.size() == 1) {
                model.addAttribute("goodsstorenum", goodsSpecs.get(0).getSpecGoodsStorage());
            }
            String goodsSpec = goods.getGoodsSpec();
            System.out.println(goodsSpec);
            //**判断是否有规格属性，如果没有返回的list是null*//*
            if (goodsSpec != null && !goodsSpec.trim().equals("")) {
                Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(goodsSpec);
                System.out.println(specMap);
                model.addAttribute("specMap", specMap);
            }
        }
        List<ShopGoodsTypeSpec> shopGoodsTypeSpecList=shopGoodsTypeSpecService.findByTypes(Optional.ofNullable(typeId).orElse(0L));
        List<Long> longList=new ArrayList<>();
        for (ShopGoodsTypeSpec item:shopGoodsTypeSpecList) {
            longList.add(item.getSpId());
        }
        List<ShopCommonSpec> shopCommonSpecList=new ArrayList<>();
        if(longList!=null && longList.size()>0){
            shopCommonSpecList=shopCommonSpecService.findList(Paramap.create().put("ids",longList));
            for (ShopCommonSpec item:shopCommonSpecList) {
                SpecVo specVo= new SpecVo();
                specVo.setId(item.getId());
                specVo.setSpName(item.getSpName());
                specVo.setSpValue(item.getSpValue());
                specVo.setSpecValueList(shopCommonSpecValueService.findList(Paramap.create().put("spId",item.getId())));
                specs.add(specVo);
            }
        }

        //ShopGoodsType goodsTypeVO = shopGoodsTypeService.selectTypeFetchOther(typeId);
        ////MqLogUtils.setMqMessage(request,null ,null ,"查询商品类型",MqLogUtils.SYS_LOG_TYPE);
        //if (null != goodsTypeVO) {
        //    //获得该类型下的品牌
        //    List<ShopGoodsTypeBrand> brandList = goodsTypeVO.getBrandList();
        //    List<ShopGoodsTypeBrand> brandstype = goodsTypeVO.getBrandList();
        //    for (ShopGoodsTypeBrand shopGoodsTypeBrand : brandstype) {
        //        ShopGoodsBrand shopGoodsBrand = shopGoodsBrandService.find(shopGoodsTypeBrand.getBrandId());
        //        if (shopGoodsBrand != null) {
        //            brands.add(shopGoodsBrand);
        //        }
        //    }
        //    //获得该类型下的规格
        //    specs = goodsTypeVO.getSpecVos();
        //    //获得该类型下的属性
        //}
        //List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        //放入model
        model.addAttribute("brands", brands);
        model.addAttribute("shopMemberGradeList", shopMemberGradeList);
        model.addAttribute("typeId", typeId);
        //放入model
        model.addAttribute("specs", specs);
        //放入model
        model.addAttribute("gcId", gcId);
        //放入model
        model.addAttribute("catename", catename);
        //商品图片
        String goodsImageMore = goods.getGoodsImageMore();
        if (StringUtil.isEmpty(goodsImageMore)) {
            model.addAttribute("imageList", null);
        } else {
            String[] imageMore = goods.getGoodsImageMore().split(",");
            if (imageMore != null && imageMore.length > 0) {
                List<String> imageList = Arrays.asList(imageMore);
                model.addAttribute("imageList", imageList);
            } else {
            }
        }
        //图片目录
        String imgSrc = Constants.SPECIMAGE_PATH;
        model.addAttribute("imgSrc", imgSrc);
        model.addAttribute("goodsId", goodsId);
        return "/shop_goods/pro_edit_goodspingtai";
    }

    /**
     * 编辑商品库商品信息
     */
    @RequestMapping(value = "/updatePro", method = RequestMethod.POST)
    public
    @ResponseBody
    Message updatePro(HttpServletRequest request, ShopGoods goods, String specNameJson, ModelMap model) {
        try {
            //设置utf-8
            request.setCharacterEncoding("utf-8");
            String goodsSpecJson = request.getParameter("goodsSpecJson");
            if (goodsSpecJson == null) {
                return Message.error("请添加正确的规格信息");
            }
            Long gcId = goods.getGcId();
            ShopGoodsClass gc = shopGoodsClassService.find(gcId);
            goods.setTypeId(gc.getTypeId());
            goods.setUpdateTime(new Date());
            Integer state = shopGoodsService.updateGoods(goods, goodsSpecJson, specNameJson);
            //判断是否成功
            if (state == 0) {
                //将失败的信号传入前台
                //log.error("商品库数据更新失败");
                return Message.error("商品库数据更新失败");
            }
            //将成功的信号传导前台
            //log.info("商品库数据更新成功");
            return Message.success("商品库数据更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            //将失败的信号传到前台
            return Message.error("商品库数据更新异常");
        }
    }

    /**
     * 编辑商品库商品信息
     */
    @RequestMapping(value = "/addVideo", method = RequestMethod.POST)
    public String addVideo(HttpServletRequest request,@RequestParam(value = "goodsId", required = false) Long goodsId,
                     @RequestParam(value = "goodsVideo", required = false, defaultValue = "")String goodsVideo, ModelMap model) {
        if (goodsId==null){
            model.addAttribute("msg", "商品Id为空");
            model.addAttribute("referer", request.getHeader("Referer"));
            return Constants.MSG_URL;
        }
        ShopGoods goods = new ShopGoods();
        goods.setId(goodsId);
        goods.setGoodsVideo(goodsVideo);
        shopGoodsService.update(goods);
        return "";
    }


    /**
     * @param @return 编辑组合商品
     * @return ModelAndView 返回类型
     * @throws Exception 修改商品 填写基本信息
     * @Title: selldetail
     */
    @RequestMapping(value = "/editcombine")
    public String editcombine(HttpServletRequest request,
        @RequestParam(value = "id") Long goodsId,
        @RequestParam(value = "gcId", required = false, defaultValue = "") Long gcId,
        @RequestParam(value = "goodsType", required = false, defaultValue = "") Integer goodsType,
        ModelMap model) throws Exception {
        //根据goodsid获得goods
        ShopGoods goods = new ShopGoods();
        goods.setId(goodsId);
        goods.setGoodsType(goodsType);
        goods.setGoodsShow(1);
        if (goodsId != null) {
            goods = shopGoodsService.find(goodsId);
        }
        List<ShopGoods> shopGoodsList=new ArrayList<>();
        shopGoodsList.add(goods);
        List<RdRanks> shopMemberGradeList = (List) getData(shopGoodsList).get("shopMemberGradeList");
        shopGoodsList = (List) getData(shopGoodsList).get("shopGoodsList");
        //放入model
        model.addAttribute("goods", goods);
        /* * 通过类型id获得类型下的品牌,规格,属性
         * 首先通过类型id获得goodsTypeVo
         * 在这个超类中,有3个list,是品牌,规格,属性*/
        List<ShopGoodsBrand> brands = new ArrayList<ShopGoodsBrand>();
        //分类名称
        String catename;
        Long typeId = null;
        if (gcId != null) {
            ShopGoodsClass goodsClass = shopGoodsClassService.find(gcId);
            //MqLogUtils.setMqMessage(request,null ,null ,"查询单个商品分类"+goodsClass.getGcId(),MqLogUtils.SYS_LOG_TYPE);
            catename = goodsClass.getGcName();
            typeId = goodsClass.getTypeId();
        } else {
            gcId = goods.getGcId();
            typeId = goods.getTypeId();
            catename = goods.getGcName();
            //规格
            //通过goodsid在数据库中查出goods_spec
            //得到goodsSpec的list
            List<ShopGoodsSpec> goodsSpecs = shopGoodsSpecService.findListByGoodsId(goods.getId());
            int stock = 0;
            //MqLogUtils.setMqMessage(request,null ,null ,"查询商品规格列表",MqLogUtils.SYS_LOG_TYPE);
            for (int i = 0; i < goodsSpecs.size(); i++) {
                if (goodsSpecs.get(i).getSpecGoodsSpec() != null && !goodsSpecs.get(i).getSpecGoodsSpec().trim()
                    .equals("")) {
                    goodsSpecs.get(i)
                        .setSpecValueIDS(GoodsUtils.getThisGoodsAllSpecValueId(goodsSpecs.get(i).getSpecGoodsSpec()));
                    stock += goodsSpecs.get(i).getSpecGoodsStorage();
                }
            }
            //因为进货在客户那里进行进货 只改变规格商品数量 没改吧商品数量所以进行验证 如果数量不同就进行更新
            validationInventory(stock, goods);
            //放入model
            model.addAttribute("goodsSpecs", goodsSpecs);

            //通过goodsid在数据库中查出goods_spec
            List<Long> goodsIdList = new ArrayList<>();
            List<ShopGoodsGoods> shopGoodsGoodses = shopGoodsGoodsService
                .findList(Paramap.create().put("goodId", goodsId));
            for (ShopGoodsGoods shopGoods : shopGoodsGoodses) {
                goodsIdList.add(shopGoods.getCombineGoodsId());
            }
            //处理组合商品的单个商品个规格问题
            Map<Long, List<IdNameDto>> shopGoodsSpecMap = shopGoodsService.findGoodsBySpecMap(goodsIdList,3);
            for (ShopGoodsGoods shopGoods : shopGoodsGoodses) {
                List<IdNameDto> shopGoodsSpecs = shopGoodsSpecMap.get(shopGoods.getCombineGoodsId());
                shopGoods.setShopGoodsSpecList(shopGoodsSpecs);
            }
            //放入model
            model.addAttribute("specs", shopGoodsGoodses);
            String goodsSpec = goods.getGoodsSpec();
            System.out.println(goodsSpec);
            //**判断是否有规格属性，如果没有返回的list是null*//*
            if (goodsSpec != null && !goodsSpec.trim().equals("")) {
                Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(goodsSpec);
                System.out.println(specMap);
                model.addAttribute("specMap", specMap);
            }
        }
        //List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
        //放入model
        model.addAttribute("brands", brands);
        model.addAttribute("shopMemberGradeList", shopMemberGradeList);
        //放入model
        model.addAttribute("gcId", gcId);
        //放入model
        model.addAttribute("typeId", typeId);
        //放入model
        model.addAttribute("catename", catename);
        //商品图片
        String goodsImageMore = goods.getGoodsImageMore();
        if (StringUtil.isEmpty(goodsImageMore)) {
            model.addAttribute("imageList", null);
        } else {
            String[] imageMore = goods.getGoodsImageMore().split(",");
            if (imageMore != null && imageMore.length > 0) {
                List<String> imageList = Arrays.asList(imageMore);
                model.addAttribute("imageList", imageList);
            } else {
            }
        }
        //图片目录
        String imgSrc = Constants.SPECIMAGE_PATH;
        model.addAttribute("imgSrc", imgSrc);
        model.addAttribute("goodsId", goodsId);
        return "/shop_goods/pro_edit_goodscombine";
    }

    /**
     * 上下架
     */
    @RequestMapping(value = "/aduit", method = RequestMethod.POST)
    public String aduit(Long[] ids, Integer type, ModelMap model, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        if (ids != null && ids.length > 0) {
            //1为上架
            if (type == 1) {
                map.put("ids", ids);
                map.put("goodsShow", type);
                map.put("goodsImageMore","");//防止图片消失不能为空
                shopGoodsService.updateAll(map);
                model.addAttribute("msg", "上架成功");
                model.addAttribute("referer", request.getHeader("Referer"));
            } else {
                //下架前判断是否是首页推荐商品
                map.put("goodsIds", ids);
                Long count = shopGoodsRecommendService.count(map);
                if (count > 0) {
                    model.addAttribute("msg", "下架商品中包含首页的推广位,请先换掉该推荐位置的该商品在进行下架");
                    model.addAttribute("referer", request.getHeader("Referer"));
                    return Constants.MSG_URL;
                }
                map.clear();
                //下架前判断是否是某件商品的组合商品
                map.put("combineGoodsIds", ids);
                List<Long> resultList = new ArrayList<>();
                for (int i = 0, length = ids.length; i < length; i++) {
                    resultList.add(ids[i]);
                }
                List<ShopGoodsGoods> shopGoodsGoodsList = shopGoodsGoodsService.findList(map);
                if (shopGoodsGoodsList != null && shopGoodsGoodsList.size() > 0) {
                    for (ShopGoodsGoods item : shopGoodsGoodsList) {
                        resultList.add(item.getGoodId());
                    }
                }
                map.clear();
                Map<String, Object> goodsMap = new HashMap<>();
                goodsMap.put("ids", resultList);
                goodsMap.put("goodsShow", type);
                goodsMap.put("goodsImageMore","");//防止图片消失不能为空
                shopGoodsService.updateAll(goodsMap);
                model.addAttribute("msg", "下架成功");
                model.addAttribute("referer", request.getHeader("Referer"));
            }
        } else {
            model.addAttribute("msg", "操作失败`，ID为空");
            model.addAttribute("referer", request.getHeader("Referer"));
        }
        return Constants.MSG_URL;
    }

    /**
     * 验证同店铺下商品名称是否重复
     */
    @RequestMapping("/validateGoodsName")
    public
    @ResponseBody
    boolean validateGoodsName(@RequestParam(value = "goodsName", defaultValue = "") String goodsName, Long id) {
        Map<String, Object> goods1 = new HashMap<>();
        goods1.put("goodsName", goodsName);
        goods1.put("combinationIndex", id);
        Long goodsCount = shopGoodsService.count(goods1);
        if (goodsCount > 0) {
            return false;
        }
        return true;
    }


    /**
     * 检验商品编码唯一性
     */
    @RequestMapping("/validate")
    public
    @ResponseBody
    boolean validateGoodsName1(String huohao, Long id) {
        Map<String, Object> goods1 = new HashMap<>();
        goods1.put("specGoodsSerial", huohao);
        goods1.put("combinationIndex", id);
        Long goodsCount = shopGoodsSpecService.count(goods1);
        if (goodsCount > 0) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/updateStorePrice")
    @ResponseBody
    public Map updateStorePrice(ShopGoods shopGoods) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (shopGoods != null) {
                ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(shopGoods.getGoodsSpecId());
                Long update = shopGoodsSpecService.update(shopGoodsSpec);
                if (update != null) {
                    map.put("msg", "更新成功");
                    map.put("result", true);
                    return map;
                }
            }
            map.put("msg", "id为空,更新失败");
            map.put("result", false);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "更新失败");
            map.put("result", false);
            return map;
        }
    }


    @RequestMapping(value = "/updateMarketPrice")
    @ResponseBody
    public Map updateMarketPrice(ShopGoods shopGoods) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (shopGoods != null) {
                ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(shopGoods.getGoodsSpecId());
                Long update = shopGoodsSpecService.update(shopGoodsSpec);
                if (update != null) {
                    map.put("msg", "更新成功");
                    map.put("result", true);
                    return map;
                }
            }
            map.put("msg", "id为空,更新失败");
            map.put("result", false);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "更新失败");
            map.put("result", false);
            return map;
        }
    }

    /**
     * 返回品牌选择器
     */
    @RequestMapping("/selectBrand")
    public String selectBrand(Model model,
        String brandName,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo) {
        try {
            Pageable pager = new Pageable();
            pager.setPageNumber(pageNo);
            ShopGoodsBrand shopGoodsBrand = new ShopGoodsBrand();
            shopGoodsBrand.setBrandRecommend(1);
            shopGoodsBrand.setBrandNameLike(brandName);
            pager.setParameter(shopGoodsBrand);
            Page<ShopGoodsBrand> goodsBrandByPage = shopGoodsBrandService.findByPage(pager);
            model.addAttribute("pager", goodsBrandByPage);
            model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
            model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
            return "/shop_goods/selectBrand";
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.MSG_URL;
        }
    }

    /**
     * 商品选择器
     */
    @RequestMapping("/select")
    public String listGoods(Model model,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
        String goodsId,
        @ModelAttribute ShopGoods goodsPlatform) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);

        //设置状态开启
        //  goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
        goodsPlatform.setState(GoodsState.GOODS_OPEN_STATE);
        //设置上架状态
        goodsPlatform.setGoodsShow(GoodsState.GOODS_ON_SHOW);
        goodsPlatform.setGoodsType(1);
        //设置删除状态
        goodsPlatform.setIsDel(2);
        pager.setParameter(goodsPlatform);
        pager.setPageSize(20);
        Page<ShopGoods> shopGoodsPage = shopGoodsService.findByPage(pager);
        List<Long> goodsIdList = new ArrayList<>();
        for (ShopGoods shopGoods : shopGoodsPage.getContent()) {
            goodsIdList.add(shopGoods.getId());
        }
        Map<Long, List<IdNameDto>> shopGoodsSpecMap = shopGoodsService.findGoodsBySpecMap(goodsIdList,1);
        for (ShopGoods shopGoods : shopGoodsPage.getContent()) {
            List<IdNameDto> shopGoodsSpecs = shopGoodsSpecMap.get(shopGoods.getId());
            if (shopGoodsSpecs == null) {
                shopGoods.setShopGoodsSpecs("[]");
            } else {
                shopGoods.setShopGoodsSpecs(JacksonUtil.toJson(shopGoodsSpecs));
            }
        }
        //商品分类查询及回显
        Map<String, String> treeMap = new HashMap<>();
        Map<String, String> classNameMap = new HashMap<>();
        List<ShopGoodsClass> goodsClasses = new ArrayList<>();
        List<ShopGoodsClass> firstGoodsClasses = new ArrayList<>();
        List<ShopGoodsClass> shopGoodsClassList = shopGoodsClassService.findAll();
        for (ShopGoodsClass item : shopGoodsClassList) {
            if (Optional.ofNullable(item.getDeep()).orElse(0) == 2) {
                goodsClasses.add(item);
                treeMap.put(item.getId() + "", item.getGcParentId() + "");
            } else {
                if (Optional.ofNullable(item.getGcParentId()).orElse(1L) == 0L) {
                    firstGoodsClasses.add(item);
                }
                classNameMap.put(item.getId() + "", item.getGcName());
            }
        }
        for (ShopGoods item : shopGoodsPage.getContent()) {
            String tree = treeMap.get(item.getGcId() + "");
            if (tree != null && !"".equals(tree)) {
                item.setGcParentName(Optional.ofNullable(classNameMap.get(tree)).orElse(""));
            }
        }
        model.addAttribute("classList", goodsClasses);
        model.addAttribute("firstGoodsClasses", firstGoodsClasses);
        model.addAttribute("goodsGcId", goodsPlatform.getGcId());
        model.addAttribute("gcParentId", goodsPlatform.getGcParentId());
        model.addAttribute("goodsListKeywords", goodsPlatform.getGoodsListKeywords());
        model.addAttribute("pager", shopGoodsPage);
        model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
        model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
        model.addAttribute("goodsId", goodsId);
        return "/shop_goods/goods_select";
    }

    @RequestMapping(value = "/updateOrderSort")
    @ResponseBody
    public Map updateOrderSort(ShopGoods shopGoods) {
        Map<String, Object> map = new HashMap<>();
        if (shopGoods != null) {
            shopGoods.setUpdateTime(new Date());
            shopGoods.setGoodsImageMore("");
            Long update = shopGoodsService.update(shopGoods);
            if (update != null) {
                map.put("msg", "更新成功");
                map.put("result", true);
                return map;
            }
        }
        map.put("msg", "id为空,更新失败");
        map.put("result", false);
        return map;
    }

    public Map getData(List<ShopGoods> shopGoodsList) {
        Map<String, Object> map = new HashMap<>();
        List<RdRanks> shopMemberGradeList = null;
        if (redisService.get("shopMemberGradeList") != null) {
            shopMemberGradeList = JacksonUtil.convertList(redisService.get("shopMemberGradeList"), RdRanks.class);
        } else {
            shopMemberGradeList = rdRanksService.findAll();
            redisService.save("shopMemberGradeList", shopMemberGradeList);
        }

        for (ShopGoods item : shopGoodsList) {
            String gradeName = "";

            for (RdRanks shopMemberGrade : shopMemberGradeList) {
                if (Optional.ofNullable(item.getSalePopulationIds()).orElse("")
                    .contains(","+shopMemberGrade.getRankId() + ",")) {
                    gradeName += shopMemberGrade.getRankName() + ",";
                }
            }
            if (gradeName.endsWith(",")){
                gradeName=gradeName.substring(0,gradeName.length()-1);
            }
            item.setSalePopulationName(gradeName);
        }
        map.put("shopMemberGradeList", shopMemberGradeList);
        map.put("shopGoodsList", shopGoodsList);
        return map;
    }

    /**
     * 加载二级分类下拉选项模板
     */
    @RequestMapping(value = "/loadSelectClassTwoTpl")
    public String loadSelectClassTwoTpl(ModelMap model, Long gcParentId) {
        if (gcParentId != null) {
            List<ShopGoodsClass> classTwoList = shopGoodsClassService
                .findList(Paramap.create().put("gcParentId", gcParentId).put("order", "gc_sort ASC"));
            model.addAttribute("classTwo", classTwoList);
        }
        return "admin/template/load_select_class_two";
    }


    /**
     * 验证商品表,商品库存  因为进货在客户那里进行进货 只改变规格商品数量 没改吧商品数量所以进行验证 如果数量不同就进行更新
     */
    public void validationInventory(Integer stock, ShopGoods shopGoods) {
        //因为进货在客户那里进行进货 只改变规格商品数量 没改吧商品数量所以进行验证 如果数量不同就进行更新
        if (shopGoods.getStock() != null && !shopGoods.getStock().toString().equals(stock + "")) {
            ShopGoods newGoods = new ShopGoods();
            newGoods.setId(shopGoods.getId());
            newGoods.setGoodsImageMore("");
            newGoods.setStock(Long.parseLong(stock + ""));
            shopGoodsService.update(newGoods);
        }
    }

}
