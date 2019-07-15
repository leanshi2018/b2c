package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 信息设置
 * Created by zj on 2018/5/8.
 */
@Controller
@RequestMapping("/admin/setting")
public class ShopCommonSettingController {

    @Autowired
    private TUserSettingService tUserSettingService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private ShopCommonAreaService areaService;


    @RequestMapping("/baseParamter")
    public ModelAndView find(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("/common/setting/baseParamter");
        modelAndView.addObject("platformPhone", tUserSettingService.read("platformPhone") + "");
        modelAndView.addObject("platfomContact", tUserSettingService.read("platfomContact") + "");
        modelAndView.addObject("platfomContactPhone", tUserSettingService.read("platfomContactPhone") + "");
        modelAndView.addObject("platfomContactAddress", tUserSettingService.read("platfomContactAddress") + "");
        modelAndView.addObject("platfomContactAddressDetail", tUserSettingService.read("platfomContactAddressDetail") + "");
        modelAndView.addObject("addProvinceCode", tUserSettingService.read("addProvinceCode") + "");
        modelAndView.addObject("addCityCode", tUserSettingService.read("addCityCode") + "");
        modelAndView.addObject("addCountryCode", tUserSettingService.read("addCountryCode") + "");
        Paramap paramap = Paramap.create().put("isDel", 0).put("areaParentId", 0);
        List<ShopCommonArea> areas = areaService.findList(paramap);
        modelAndView.addObject("areas", areas);
        return modelAndView;
    }

    @RequestMapping("/updatePlatForm")
    public String update(@RequestParam("platformPhone") String platformPhone,
                         @RequestParam("platfomContact") String platfomContact,
                         @RequestParam("platfomContactPhone") String platfomContactPhone,
                         @RequestParam("platfomContactAddress") String platfomContactAddress,
                         @RequestParam("platfomContactAddressDetail") String platfomContactAddressDetail,
                         @RequestParam("addProvinceCode") String addProvinceCode,
                         @RequestParam("addCityCode") String addCityCode,
                         @RequestParam("addCountryCode") String addCountryCode,
                         Model model, HttpServletRequest request) {
        model.addAttribute("referer", request.getHeader("Referer"));
        tUserSettingService.save("addProvinceCode", addProvinceCode);
        tUserSettingService.save("addCityCode", addCityCode);
        tUserSettingService.save("addCountryCode", addCountryCode);
        tUserSettingService.save("platformPhone", platformPhone);
        tUserSettingService.save("platfomContact", platfomContact);
        tUserSettingService.save("platfomContactPhone", platfomContactPhone);
        tUserSettingService.save("platfomContactAddress", platfomContactAddress);
        tUserSettingService.save("platfomContactAddressDetail", platfomContactAddressDetail);
        RdMmAddInfo shopMemberAddress=rdMmAddInfoService.find("aid",-1);
        if (shopMemberAddress==null){
            RdMmAddInfo memberAddress=new RdMmAddInfo();
            memberAddress.setAid(-1);
            memberAddress.setConsigneeName(platfomContact);
            memberAddress.setMobile(platfomContactPhone);
            memberAddress.setAddProvinceCode(addProvinceCode);
            memberAddress.setAddCityCode(addCityCode);
            memberAddress.setAddCountryCode(addCountryCode);
//            memberAddress.setAddTownCode(platfomContactAddress);
            memberAddress.setAddDetial(platfomContactAddressDetail);
            rdMmAddInfoService.save(memberAddress);
        }else{
            shopMemberAddress.setConsigneeName(platfomContact);
            shopMemberAddress.setMobile(platfomContactPhone);
            shopMemberAddress.setAddProvinceCode(addProvinceCode);
            shopMemberAddress.setAddCityCode(addCityCode);
            shopMemberAddress.setAddCountryCode(addCountryCode);
            shopMemberAddress.setAddDetial(platfomContactAddressDetail);
            rdMmAddInfoService.update(shopMemberAddress);
        }
        model.addAttribute("msg", "基本参数设置成功");
        return Constants.MSG_URL;
    }

}
