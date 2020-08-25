package com.framework.loippi.controller.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.entity.common.ShopProductRecommendation;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopHomePictureService;
import com.framework.loippi.service.common.ShopProductRecommendationService;
import com.framework.loippi.service.common.ShopRecommendationGoodsService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RetailProfitService;
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
        pageable.setParameter(Paramap.create().put("pictureName", param.getPictureName()).put("pictureType",2));
        pageable.setOrderProperty("p_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopHomePictureService.findByPage(pageable));
        return "/common/ad_management/index";
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
        /*if (pictureId==null){
            model.addAttribute("msg", "id为空");
            return Constants.MSG_URL;
        }*/
        model.addAttribute("picture", shopHomePictureService.find(pictureId));
        return "/common/ad_management/edit";
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
            }else {//首页广告图2
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
                shopHomePictureService.update(shopHomePicture);
                return "redirect:findAD2PictureList.jhtml";
            }else {
                System.out.println("广告位图2修改");
                if (shopHomePicture.getPSort()<1 || shopHomePicture.getPSort()>3){
                    model.addAttribute("msg", "广告位图2排序只能是1-3");
                    return Constants.MSG_URL;
                }
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                /*if (pictureList.size()!=0){
                    for (ShopHomePicture homePicture : pictureList) {
                        if (homePicture.getPictureType()==2){
                            if (homePicture.getId().longValue() != shopHomePicture.getId().longValue()){
                                if (homePicture.getPSort()==shopHomePicture.getPSort()){
                                    model.addAttribute("msg", "广告位图2排序"+shopHomePicture.getPSort()+"号已存在");
                                    return Constants.MSG_URL;
                                }
                            }
                        }
                    }
                }*/
                shopHomePictureService.update(shopHomePicture);
                return "redirect:findAD2PictureList.jhtml";
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
        if(shopProductsRecommendation.getAuditStatus()==null){
            model.addAttribute("msg", "请选择是否显示");
            return Constants.MSG_URL;
        }
        shopProductsRecommendation.setId(twiterIdService.getTwiterId());
        shopProductRecommendationService.save(shopProductsRecommendation);

        return "redirect:findProductsRecommendationList.jhtml";
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
        return "";
    }

    /**
     * 商品推荐页管理详情
     * @param request
     * @param model
     * @param rId
     * @return
     */
    @RequestMapping(value = "/findProductsRecommendation")
    public String findProductsRecommendation(HttpServletRequest request, Pageable pageable, ModelMap model, @RequestParam(required = false, value = "rId") Long rId) {
        pageable.setParameter(Paramap.create().put("rId", rId));
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopRecommendationGoodsService.findByPage(pageable));
        return "";
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
        return "";
    }

}
