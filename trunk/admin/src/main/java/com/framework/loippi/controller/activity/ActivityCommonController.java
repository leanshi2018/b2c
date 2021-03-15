package com.framework.loippi.controller.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.entity.common.ShopProductRecommendation;
import com.framework.loippi.entity.common.ShopRecommendationGoods;
import com.framework.loippi.entity.product.ShopGoodsClass;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.common.recommendation.RecommendationGoodsResult;
import com.framework.loippi.result.common.recommendation.RecommendationGoodsResultPage;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopHomePictureService;
import com.framework.loippi.service.common.ShopProductRecommendationService;
import com.framework.loippi.service.common.ShopRecommendationGoodsService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;

/**
 * Created by longbh on 2017/7/27.
 */
@Controller("adminActivityCommonController")
@RequestMapping({"/admin/shop_activity_common"})
public class ActivityCommonController extends GenericController {

    @Resource
    private TUserSettingService tUserSettingService;
    @Resource
    private ShopHomePictureService shopHomePictureService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RetailProfitService retailProfitService;
    @Resource
    private ShopProductRecommendationService shopProductRecommendationService;
    @Resource
    private ShopRecommendationGoodsService shopRecommendationGoodsService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsClassService shopGoodsClassService;

    @RequestMapping(value = "/grant", method = RequestMethod.GET)
    public void grant() {
        retailProfitService.grantRetail();
    }

    /**
     * 活动反馈
     */
    @RequestMapping(value = "/setReback", method = RequestMethod.GET)
    public String setReback(Long id, ModelMap model) {
        Map<String, Object> params = new HashMap<>();
        params.put("reback_state", tUserSettingService.read("reback_state"));
        params.put("reback_max", tUserSettingService.read("reback_max"));
        params.put("reback_min", tUserSettingService.read("reback_min"));
        params.put("reback_yongjin", tUserSettingService.read("reback_yongjin"));
        params.put("reback_yongjin_rule", tUserSettingService.read("reback_yongjin_rule"));
        model.addAttribute("setting", params);
        return "/activity/common/setReback";
    }

    /**
     * 轮播图列表
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/findHomePictureList")
    public String findHomePictureList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopHomePicture param) {
        pageable.setParameter(Paramap.create().put("pictureName", param.getPictureName()).put("pictureType",0));
        pageable.setOrderProperty("p_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopHomePictureService.findByPage(pageable));
        return "/common/rotationChart/index";
    }

    /**
     * 广告位列表
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/findADPictureList")
    public String findADPictureList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopHomePicture param) {
        pageable.setParameter(Paramap.create().put("pictureName", param.getPictureName()).put("pictureType",1));
        pageable.setOrderProperty("p_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopHomePictureService.findByPage(pageable));
        return "/common/ad_management/index";
    }

    /**
     * 广告位2列表
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/findAD2PictureList")
    public String findAD2PictureList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopHomePicture param) {
        pageable.setParameter(Paramap.create().put("pictureName", param.getPictureName()).put("pictureTypeS",2));
        pageable.setOrderProperty("p_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopHomePictureService.findByPage(pageable));
        return "/common/ad_management/index";
    }

    /**
     * 系统弹窗列表
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/findSysPictureList")
    public String findSysPictureList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopHomePicture param) {
        pageable.setParameter(Paramap.create().put("pictureName", param.getPictureName()).put("pictureTypeS",5));
        pageable.setOrderProperty("p_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopHomePictureService.findByPage(pageable));
        return "/common/system_pop_up/index";
    }

    /**
     * 轮播图
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/findPicture")
    public String findPicture(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "pictureId") Long pictureId) {
        /*if (pictureId==null){
            model.addAttribute("msg", "id为空");
            return Constants.MSG_URL;
        }*/
        model.addAttribute("picture", shopHomePictureService.find(pictureId));
        return "common/rotationChart/edit";
    }

    /**
     * 广告位图
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/findADPicture")
    public String findADPicture(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "pictureId") Long pictureId) {
        model.addAttribute("picture", shopHomePictureService.find(pictureId));
        return "/common/ad_management/edit";
    }

    /**
     * 系统弹窗
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/findSysPicture")
    public String findSysPicture(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "pictureId") Long pictureId) {
        model.addAttribute("picture", shopHomePictureService.find(pictureId));
        return "/common/system_pop_up/edit";
    }

    /**
     * 广告位2图
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/findAD2Picture")
    public String findAD2Picture(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "pictureId") Long pictureId) {
        /*if (pictureId==null){
            model.addAttribute("msg", "id为空");
            return Constants.MSG_URL;
        }*/
        model.addAttribute("picture", shopHomePictureService.find(pictureId));
        return "/common/ad_management/edit";
    }

    /**
     * 删除轮播图
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/delPicture")
    public String delPicture(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "pictureId") Long pictureId) {
        if (pictureId==null){
            model.addAttribute("msg", "id为空");
            return Constants.MSG_URL;
        }
        shopHomePictureService.delete(pictureId);
        return "redirect:findHomePictureList.jhtml";
    }


    /**
         * 轮播图添加和修改
     * @param request
     * @param shopHomePicture
     * @param model
     * @param attr
     * @param openPage 跳转参数
     * @param openName 中文
     * @param openType 打开方式
     * @param jumpInterface 跳转接口
     * @param jumpJson 参数json
     * @return
     */
    @RequestMapping(value = "/saveOrUpdateHomePicture",method = RequestMethod.POST)
    public String saveOrUpdateHomePicture(HttpServletRequest request, @ModelAttribute ShopHomePicture shopHomePicture, ModelMap model, RedirectAttributes attr,
                                  @RequestParam(required = false, value = "openPage") String openPage,
                                  @RequestParam(required = false, value = "openName") String openName,
                                  @RequestParam(required = false, value = "openType") String openType,
                                  @RequestParam(required = false, value = "jumpInterface") String jumpInterface,
                                  @RequestParam(required = false, value = "jumpJson") String jumpJson) {
        if(StringUtil.isEmpty(shopHomePicture.getPictureName())){
            model.addAttribute("msg", "名称不可以为空");
            return Constants.MSG_URL;
        }
        if(StringUtil.isEmpty(shopHomePicture.getPictureUrl())){
            model.addAttribute("msg", "请上传图片");
            return Constants.MSG_URL;
        }
        if(shopHomePicture.getPSort()==null){
            model.addAttribute("msg", "排序数字不可以为空");
            return Constants.MSG_URL;
        }
        if(shopHomePicture.getAuditStatus()==null){
            model.addAttribute("msg", "请选择是否显示");
            return Constants.MSG_URL;
        }
        if(shopHomePicture.getPictureType()==null){
            model.addAttribute("msg", "图片类型为空");
            return Constants.MSG_URL;
        }

        /*Map<String,Object> map = new HashMap<String,Object>();
        map.put("page",openPage);

        if (jumpJson!=null){
            Map<String, String> jsonMap = JacksonUtil.readJsonToMap(jumpJson);
            Set<String> strings = jsonMap.keySet();
            Iterator<String> iterator = strings.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = jsonMap.get(key);
                map.put(key,value);
            }
        }

        JSONObject activityUrlJson = JSONObject.fromObject(map);*/

        shopHomePicture.setActivityUrl(openPage);
        shopHomePicture.setPictureJson(jumpJson);
        shopHomePicture.setJumpName(openName);
        if (jumpInterface==null){
            shopHomePicture.setJumpInterface("");
        }else {
            shopHomePicture.setJumpInterface(jumpInterface);
        }

        if (shopHomePicture.getId()==null){//添加
            shopHomePicture.setId(twiterIdService.getTwiterId());

            if (shopHomePicture.getPictureType()==0){//轮播图
                System.out.println("轮播图添加");
                /*List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    model.addAttribute("msg", "轮播图排序"+shopHomePicture.getPSort()+"号已存在");
                    return Constants.MSG_URL;
                }*/
                shopHomePictureService.save(shopHomePicture);

                return "redirect:findHomePictureList.jhtml";
            }else if (shopHomePicture.getPictureType()==1){//广告位图
                System.out.println("广告位图添加");
                if (shopHomePicture.getPSort()<1 || shopHomePicture.getPSort()>3){
                    model.addAttribute("msg", "广告位图排序只能是1-3");
                    return Constants.MSG_URL;
                }
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    model.addAttribute("msg", "广告位图排序"+shopHomePicture.getPSort()+"号已存在");
                    return Constants.MSG_URL;
                }
                shopHomePictureService.save(shopHomePicture);
                return "redirect:findAD2PictureList.jhtml";
            }else if (shopHomePicture.getPictureType()==2){//首页广告图2
                System.out.println("广告位图2添加");
                /*if (shopHomePicture.getPSort()<1 || shopHomePicture.getPSort()>3){
                    model.addAttribute("msg", "广告位图2排序只能是1-3");
                    return Constants.MSG_URL;
                }
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    model.addAttribute("msg", "广告位图2排序"+shopHomePicture.getPSort()+"号已存在");
                    return Constants.MSG_URL;
                }*/
                shopHomePictureService.save(shopHomePicture);
                return "redirect:findAD2PictureList.jhtml";
            }else if (shopHomePicture.getPictureType()==5){
                System.out.println("系统弹窗添加");

                Integer num = shopHomePictureService.countNumByType(5);
                if (num==0){
                    shopHomePictureService.save(shopHomePicture);
                    return "redirect:findSysPictureList.jhtml";
                }else {
                    model.addAttribute("msg", "系统弹窗已存在一个");
                    return Constants.MSG_URL;
                }
            }else {
                model.addAttribute("msg", "请选择正确的类型");
                return Constants.MSG_URL;
            }
        }else {
            if (shopHomePicture.getPictureType()==0){//轮播图
                System.out.println("轮播图修改");
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                /*if (pictureList.size()!=0){
                    for (ShopHomePicture homePicture : pictureList) {
                        if (homePicture.getPictureType()==0){
                            if (homePicture.getId().longValue() != shopHomePicture.getId().longValue()){
                                if (homePicture.getPSort()==shopHomePicture.getPSort()){
                                    model.addAttribute("msg", "轮播图排序"+shopHomePicture.getPSort()+"号已存在");
                                    return Constants.MSG_URL;
                                }
                            }
                        }
                    }
                }*/
                if (shopHomePicture.getJumpInterface()==null){
                    shopHomePicture.setJumpInterface("");
                }
                if (shopHomePicture.getPictureJson()==null){
                    shopHomePicture.setPictureJson("");
                }
                if (shopHomePicture.getDescName()==null) {
                    shopHomePicture.setDescName("");
                }
                if (shopHomePicture.getActivityUrl()==null){
                    shopHomePicture.setActivityUrl("");
                }
                shopHomePictureService.update(shopHomePicture);
                return "redirect:findHomePictureList.jhtml";
            }else if (shopHomePicture.getPictureType()==1){//广告位图
                System.out.println("广告位图修改");
                if (shopHomePicture.getPSort()<1 || shopHomePicture.getPSort()>3){
                    model.addAttribute("msg", "广告位图排序只能是1-3");
                    return Constants.MSG_URL;
                }
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    for (ShopHomePicture homePicture : pictureList) {
                        if (homePicture.getPictureType()==1){
                            if (homePicture.getId().longValue() != shopHomePicture.getId().longValue()){
                                if (homePicture.getPSort()==shopHomePicture.getPSort()){
                                    model.addAttribute("msg", "广告位图排序"+shopHomePicture.getPSort()+"号已存在");
                                    return Constants.MSG_URL;
                                }
                            }
                        }
                    }
                }
                if (shopHomePicture.getJumpInterface()==null){
                    shopHomePicture.setJumpInterface("");
                }
                if (shopHomePicture.getPictureJson()==null){
                    shopHomePicture.setPictureJson("");
                }
                if (shopHomePicture.getDescName()==null){
                    shopHomePicture.setDescName("");
                }
                if (shopHomePicture.getActivityUrl()==null){
                    shopHomePicture.setActivityUrl("");
                }
                System.out.println("t=="+shopHomePicture);
                shopHomePictureService.update(shopHomePicture);
                return "redirect:findAD2PictureList.jhtml";
            }else if (shopHomePicture.getPictureType()==2){
                System.out.println("广告位图2修改");
                if (shopHomePicture.getPSort()<1 || shopHomePicture.getPSort()>3){
                    model.addAttribute("msg", "广告位图2排序只能是1-3");
                    return Constants.MSG_URL;
                }
                //List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (shopHomePicture.getJumpInterface()==null){
                    shopHomePicture.setJumpInterface("");
                }
                if (shopHomePicture.getPictureJson()==null){
                    shopHomePicture.setPictureJson("");
                }
                if (shopHomePicture.getActivityUrl()==null){
                    shopHomePicture.setActivityUrl("");
                }
                if (shopHomePicture.getDescName()==null){
                    shopHomePicture.setDescName("");
                }
                System.out.println("t=="+shopHomePicture);
                shopHomePictureService.update(shopHomePicture);
                return "redirect:findAD2PictureList.jhtml";
            }else {
                System.out.println("系统弹窗修改");
                if (shopHomePicture.getJumpInterface()==null){
                    shopHomePicture.setJumpInterface("");
                }
                if (shopHomePicture.getPictureJson()==null){
                    shopHomePicture.setPictureJson("");
                }
                if (shopHomePicture.getActivityUrl()==null){
                    shopHomePicture.setActivityUrl("");
                }
                if (shopHomePicture.getDescName()==null){
                    shopHomePicture.setDescName("");
                }
                System.out.println("t=="+shopHomePicture);
                shopHomePictureService.update(shopHomePicture);
                return "redirect:findSysPictureList.jhtml";
            }
        }

    }


    /**
     * 添加推荐页
     * @param request
     * @param shopProductsRecommendation
     * @param model
     * @param attr
     * @return
     */
    @RequestMapping(value = "/saveProductsRecommendation",method = RequestMethod.POST)
    public String saveProductsRecommendation(HttpServletRequest request, @ModelAttribute ShopProductRecommendation shopProductsRecommendation, ModelMap model, RedirectAttributes attr ){

        if(StringUtil.isEmpty(shopProductsRecommendation.getRecommendationName())){
            model.addAttribute("msg", "名称不可以为空");
            return Constants.MSG_URL;
        }
        if(StringUtil.isEmpty(shopProductsRecommendation.getPictureUrl())){
            model.addAttribute("msg", "请上传图片");
            return Constants.MSG_URL;
        }
        shopProductsRecommendation.setId(twiterIdService.getTwiterId());
        shopProductsRecommendation.setAuditStatus(1);
        shopProductRecommendationService.save(shopProductsRecommendation);

        return "redirect:findProductsRecommendationList.jhtml";
    }

    /**
     * 新增
     * @param model
     * @return
     */
    @RequestMapping(value ="/add",method = RequestMethod.GET)
    public String add(Model model) {
        return "/common/recommended/add";
    }

    /**
     * 推荐页列表
     * @param request
     * @param pageable
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/findProductsRecommendationList")
    public String findProductsRecommendationList(HttpServletRequest request, Pageable pageable, ModelMap model, @ModelAttribute ShopProductRecommendation param) {
        pageable.setParameter(Paramap.create().put("recommendationName", param.getRecommendationName()));
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopProductRecommendationService.findByPage(pageable));
        return "/common/recommended/index";
    }

    /**
     * 选择推荐页列表
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/selectProductsRecommendationList")
    public String selectProductsRecommendationList(HttpServletRequest request, ModelMap model) {
        model.addAttribute("page", shopProductRecommendationService.findAll());
        return "/common/select/selectRecomend";
    }

    /**
     * 商品推荐页管理详情
     * @param request
     * @param model
     * @param rId
     * @return
     */
    @RequestMapping(value = "/findProductsRecommendationInfo")
    public String findProductsRecommendationInfo(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "rId") Long rId) {
        model.addAttribute("page", shopProductRecommendationService.find(rId));

        return "/common/recommended/edit";
    }

    /**
     * 删除推荐页
     * @param request
     * @param model
     * @param rId
     * @return
     */
    @RequestMapping(value = "/delProductsRecommendation")
    public String delProductsRecommendation(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "rId") Long rId) {
        if (rId==null){
            model.addAttribute("msg", "未找到该推荐页");
            return Constants.MSG_URL;
        }
        shopProductRecommendationService.delete(rId);
        shopRecommendationGoodsService.delByRId(rId);

        return "redirect:findProductsRecommendationList.jhtml";
    }

    /**
     * 修改推荐页详情
     * @param request
     * @param model
     * @param param
     * @return
     */
    @RequestMapping(value = "/updateProductsRecommendation")
    public String updateProductsRecommendation(HttpServletRequest request, ModelMap model, @ModelAttribute ShopProductRecommendation param) {
        shopProductRecommendationService.update(param);

        return "redirect:findProductsRecommendationList.jhtml";
    }

    /**
     * 推荐页商品管理
     * @param request
     * @param pageable
     * @param model
     * @param rId
     * @return
     */
    @RequestMapping(value = "/findRecommendationGoods")
    public String findRecommendationGoods(HttpServletRequest request, Pageable pageable, ModelMap model,
                                          @RequestParam(required = false, value = "rId") Long rId,
                                          @RequestParam(required = false, value = "goodsId") Long goodsId,
                                          @RequestParam(required = false, value = "goodsName") String goodsName) {
        RecommendationGoodsResult goodsResult = new RecommendationGoodsResult();
        goodsResult.setRId(rId);
        goodsResult.setGoodsId(goodsId);
        goodsResult.setGoodsName(goodsName);
        pageable.setParameter(goodsResult);
        pageable.setOrderDirection(Order.Direction.DESC);
        Page serviceGoodsResult = shopRecommendationGoodsService.findGoodsResult(pageable);
        model.addAttribute("page", RecommendationGoodsResultPage.build(serviceGoodsResult,shopGoodsClassService.findAll()));
        //model.addAttribute("page",serviceGoodsResult);
        return "/common/recommended/manage";
    }

    /**
     *  商品列表
     * @param request
     * @param pageable
     * @param model
     * @param gcId
     * @param goodsName
     * @return
     */
    @RequestMapping(value = "/findShopGoodList")
    public String findShopGoodList(HttpServletRequest request, Pageable pageable, ModelMap model,
                                   @RequestParam(required = false, value = "gcId") Long gcId,
                                   @RequestParam(required = false, value = "goodsName") String goodsName) {
        pageable.setParameter(Paramap.create().put("gcId", gcId).put("goodsName",goodsName).put("goodsShow",1));
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopGoodsService.findByPage(pageable));
		List<ShopGoodsClass> all = shopGoodsClassService.findAll();
		Map<String,Object> gcNameMap = new HashMap<String,Object>();
		if (all.size()>0){
			for (ShopGoodsClass goodsClass : all) {
				gcNameMap.put("id",goodsClass.getId());
				gcNameMap.put("gcName",goodsClass.getGcName());
			}
		}
		model.addAttribute("goodsClass", gcNameMap);
        return "/common/recommended/selectGoods";
    }

    /**
     *  商品分类列表
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/findShopGoodClassList")
    public String findShopGoodClassList(HttpServletRequest request,  ModelMap model){
        System.out.println("进来了");
        List<ShopGoodsClass> all = shopGoodsClassService.findAll();
        Map<String,String> gcNameMap = new HashMap<String,String>();
        if (all.size()>0){
            for (ShopGoodsClass goodsClass : all) {
                gcNameMap.put(goodsClass.getId().toString(),goodsClass.getGcName());
            }
        }
        System.out.println("***************************");
        System.out.println("gcn="+gcNameMap);
        System.out.println("***************************");
        model.addAttribute("goodsClass", gcNameMap);
        return "/common/recommended/selectGoods";
    }

    /**
     * 推荐页商品添加
     * @param request
     * @param model
     * @param rId
     * @param jsonMap
     * @return
     */
    @RequestMapping(value = "/saveRecommendationGoods")
    public String saveRecommendationGoods(HttpServletRequest request, ModelMap model,
                                          @RequestParam(required = false, value = "rId") Long rId,
                                          @RequestParam(required = false, value = "jsonMap") String jsonMap) {

        if (rId==null){
            model.addAttribute("msg", "找不到该推荐页");
            return Constants.MSG_URL;
        }
        List<ShopRecommendationGoods> goodsList = shopRecommendationGoodsService.findByRId(rId);


		System.out.println("json="+jsonMap);
        JSONArray array = JSON.parseArray(jsonMap);
        if (array.size()==0){
            model.addAttribute("msg", "请选择商品添加");
            return Constants.MSG_URL;
        }

        for (Object o : array) {
            System.out.println("o="+o);
            ShopRecommendationGoods recommendationGoods = new ShopRecommendationGoods();
            recommendationGoods.setId(twiterIdService.getTwiterId());
            recommendationGoods.setRId(rId);
            Long goodsId = 0l;
            JSONObject jsonObject = JSON.parseObject(o.toString());
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                goodsId = Long.valueOf(entry.getValue().toString());
                System.out.println("id="+ goodsId);
                recommendationGoods.setGoodsId(goodsId);
            }
            int flag = 0;
            for (ShopRecommendationGoods goods : goodsList) {
                if (goods.getGoodsId().toString().equals(goodsId.toString())){
                    flag=1;
                }
            }
            if (flag==0){
                shopRecommendationGoodsService.save(recommendationGoods);
            }
        }
        model.addAttribute("msg", "添加商品成功");
        //return Constants.MSG_URL;
        //return "";
        return "redirect:findRecommendationGoods.jhtml?rId="+rId+"&goodsId"+"&goodsName";
    }

    /**
     * 删除推荐页商品
     * @param request
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/delRecommendationGoods")
    public String delRecommendationGoods(HttpServletRequest request, ModelMap model, @RequestParam(required = false, value = "id") Long id) {
        if (id==null){
            model.addAttribute("msg", "未找到该商品");
            return Constants.MSG_URL;
        }
        ShopRecommendationGoods goods = shopRecommendationGoodsService.find(id);
        Long rId = goods.getRId();
        shopRecommendationGoodsService.delete(id);
        model.addAttribute("msg", "删除商品成功");
        //return Constants.MSG_URL;
        return "redirect:findRecommendationGoods.jhtml?rId="+rId+"&goodsId"+"&goodsName";
    }

}
