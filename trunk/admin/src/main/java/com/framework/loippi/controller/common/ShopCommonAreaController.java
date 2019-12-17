package com.framework.loippi.controller.common;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Maps;

/**
 * Created by longbh on 2017/8/19.
 */
@Controller("adminShopCommonAreaController")
@RequestMapping("/setting/area")
public class ShopCommonAreaController extends GenericController {

    @Resource
    private ShopCommonAreaService commonAreaService;

    /**
     * 根据父类ID 获取到 下级城市
     *
     * @param @param  parentid
     * @param @throws JsonGenerationException
     * @param @throws JsonMappingException
     * @param @throws Exception    设定文件
     * @return Map<String,String>    返回类型
     * @Title: getChildArea
     */
    @RequestMapping(value = "/getChildArea", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> getChildArea(@RequestParam(value = "id") String parentid) {
        Map<String, String> map = Maps.newHashMap();
        Paramap paramap = Paramap.create().put("isDel", 0).put("areaParentId", parentid);
        List<ShopCommonArea> areas = commonAreaService.findList(paramap);
        String json = "null";
        if (areas != null && areas.size() > 0) {
            json = JacksonUtil.toJson(areas);
        }
        map.put("result", json);
        map.put("success", "true");
        return map;
    }

    /**
     * 地区管理列表
     * @param model
     * @param pageNo
     * @param pageSize
     * @param areaId
     * @param areaName
     * @return
     */
    @RequestMapping(value = "/findAreaList", method = RequestMethod.POST)
    public String findAreaList(@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
                               @RequestParam(required = false, value = "pageSize", defaultValue = "20") int pageSize,
                               @RequestParam(required = false, value = "areaId") Long areaId,
                               @RequestParam(required = false, value = "areaName") String areaName,
                               Model model) {
        ShopCommonArea shopCommonArea = new ShopCommonArea();
        shopCommonArea.setId(areaId);
        shopCommonArea.setAreaName(areaName);

        //参数整理
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        pager.setOrderProperty("express_state");
        pager.setOrderDirection(Order.Direction.DESC);
        pager.setParameter(shopCommonArea);
        Page<ShopCommonArea> page = commonAreaService.findByPage(pager);
        model.addAttribute("areaList", page);
        return "";
    }

    /**
     * 地区管理重置
     * @return
     */
    @RequestMapping(value = "/resetArea")
    public String resetArea(ModelMap model) {
        ShopCommonArea shopCommonArea = new ShopCommonArea();
        shopCommonArea.setExpressState(0);
        Long update = commonAreaService.update(shopCommonArea);
        model.addAttribute("msg", "重置成功");
        return "redirect:findAreaList.jhtml";// TODO 重定向到列表页面
    }

    /**
     * 禁用或恢复地区
     * @return
     */
    @RequestMapping(value = "/disableAndRestoreArea")
    public String disableAndRestoreArea(ModelMap model, Long areaId) {
        if (areaId==null){
            model.addAttribute("msg", "地区id为空");
            return Constants.MSG_URL;
        }

        ShopCommonArea shopCommonArea = commonAreaService.find(areaId);
        if (shopCommonArea==null){
            model.addAttribute("msg", "id没有找到对应地区");
            return Constants.MSG_URL;
        }

        Integer areaDeep = shopCommonArea.getAreaDeep();//级别
        if (shopCommonArea.getExpressState()==0){
            //禁用
            commonAreaService.disableAndRestoreArea(areaId,1);
        }else{
            //恢复
            if (shopCommonArea.getAreaParentId()==0){//级别为1的
                commonAreaService.disableAndRestoreArea(areaId,0);
            }else {
                ShopCommonArea parentArea = commonAreaService.find(shopCommonArea.getAreaParentId());
                if (parentArea.getExpressState()==1){//上级禁用
                    model.addAttribute("msg", "恢复失败，请先恢复该地区的上级地区");
                    return Constants.MSG_URL;
                }else {//上级未禁用
                    commonAreaService.disableAndRestoreArea(areaId,0);
                }
            }
        }

        return "redirect:findAreaList.jhtml";// TODO 重定向到列表页面
    }
}
