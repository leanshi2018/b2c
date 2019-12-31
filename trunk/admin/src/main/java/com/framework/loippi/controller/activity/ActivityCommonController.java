package com.framework.loippi.controller.activity;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopHomePictureService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
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
        pageable.setParameter(Paramap.create().put("pictureName", param.getPictureName()).put("auditStatus",1).put("pictureType",0));
        pageable.setOrderProperty("p_sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        model.addAttribute("page", shopHomePictureService.findByPage(pageable));
        return "/common/rotationChart/index";
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

        Map<String,Object> map = new HashMap<String,Object>();
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

        JSONObject activityUrlJson = JSONObject.fromObject(map);

        shopHomePicture.setActivityUrl(activityUrlJson.toString());
        shopHomePicture.setJumpName(openName);

        if (shopHomePicture.getId()==null){//添加
            shopHomePicture.setId(twiterIdService.getTwiterId());

            if (shopHomePicture.getPictureType()==0){//轮播图
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    model.addAttribute("msg", "轮播图排序"+shopHomePicture.getPSort()+"号已存在");
                    return Constants.MSG_URL;
                }
                shopHomePictureService.save(shopHomePicture);
            }else {//广告位图
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
            }
            return "redirect:findHomePictureList.jhtml";
        }else {
            if (shopHomePicture.getPictureType()==0){//轮播图
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    for (ShopHomePicture homePicture : pictureList) {
                        if (homePicture.getId()!=shopHomePicture.getId()){
                            if (homePicture.getPSort()==shopHomePicture.getPSort()){
                                model.addAttribute("msg", "轮播图排序"+shopHomePicture.getPSort()+"号已存在");
                                return Constants.MSG_URL;
                            }
                        }
                    }
                }
                shopHomePictureService.update(shopHomePicture);
            }else {//广告位图
                if (shopHomePicture.getPSort()<1 || shopHomePicture.getPSort()>3){
                    model.addAttribute("msg", "广告位图排序只能是1-3");
                    return Constants.MSG_URL;
                }
                List<ShopHomePicture> pictureList = shopHomePictureService.findByTypeAndSort(shopHomePicture.getPictureType(),shopHomePicture.getPSort());
                if (pictureList.size()!=0){
                    for (ShopHomePicture homePicture : pictureList) {
                        if (homePicture.getId()!=shopHomePicture.getId()){
                            if (homePicture.getPSort()==shopHomePicture.getPSort()){
                                model.addAttribute("msg", "广告位图排序"+shopHomePicture.getPSort()+"号已存在");
                                return Constants.MSG_URL;
                            }
                        }
                    }
                }
                shopHomePictureService.update(shopHomePicture);
            }
            return "redirect:findHomePictureList.jhtml";
        }

    }



}
