package com.framework.loippi.controller.common;

import com.framework.loippi.utils.JacksonUtil;
import com.google.common.collect.Maps;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.utils.Paramap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
}
