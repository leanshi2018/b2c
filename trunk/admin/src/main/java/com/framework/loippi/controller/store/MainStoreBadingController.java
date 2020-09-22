package com.framework.loippi.controller.store;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/storeBinding")
public class MainStoreBadingController {
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdMmRelationService rdMmRelationService;

    /**
     * 会员申请绑定主次店关系
     * @param request
     * @param mainCode 主店会员编号
     * @param secondCode 需绑定次店会员编号
     * @return
     */
    @RequestMapping(value = "/binding",method = RequestMethod.POST)
    public String binding(HttpServletRequest request,ModelMap model, String mainCode,String secondCode) {
        if(StringUtil.isEmpty(mainCode)){
            model.addAttribute("msg", "请选择需进行绑定的主店会员编号");
            return Constants.MSG_URL;
        }
        if(StringUtil.isEmpty(secondCode)){
            model.addAttribute("msg", "请选择需进行绑定的次店会员编号");
            return Constants.MSG_URL;
        }
        //1.分别判断主店会员和次店会员关系表中的会员状态，是否为正常状态 判断俩会员是否都是主店
        RdMmBasicInfo mainBasic = rdMmBasicInfoService.findByMCode(mainCode);
        RdMmBasicInfo secondBasic = rdMmBasicInfoService.findByMCode(secondCode);
        RdMmRelation mainRelation = rdMmRelationService.find("mmCode",mainCode);
        RdMmRelation secondRelation = rdMmRelationService.find("mmCode",secondCode);
        if(mainBasic==null||secondBasic==null||mainRelation==null||secondRelation==null){
            model.addAttribute("msg", "会员信息异常，不予绑定");
            return Constants.MSG_URL;
        }
        if(mainBasic.getMainFlag()==null||secondBasic.getMainFlag()==null||!(mainBasic.getMainFlag()==1&&secondBasic.getMainFlag()==1)){
            model.addAttribute("msg", "绑定会员双方必须都是主店会员");
            return Constants.MSG_URL;
        }
        if(mainRelation.getMmStatus()==null||secondRelation.getMmStatus()==null||!(mainRelation.getMmStatus()==0&&secondRelation.getMmStatus()==0)){
            model.addAttribute("msg", "绑定会员双方必须都是正常状态");
            return Constants.MSG_URL;
        }
        //2.判断两会员之间是否为直接推荐关系
        if(secondRelation.getSponsorCode()==null||!secondRelation.getSponsorCode().equals(mainCode)){
            model.addAttribute("msg", "绑定会员双方必须是直接推荐关系");
            return Constants.MSG_URL;
        }
        //3.判断主店会员附属是否已经有3个次店
        Long num=rdMmBasicInfoService.countSecondShop(mainCode);
        if(num>=3){
            model.addAttribute("msg", "该主店会员已经拥有次店个数已达到上限");
            return Constants.MSG_URL;
        }
        //4.判断需绑定会员是否绑定了其他次店，如果绑定，不予通过绑定
        Long secondNum=rdMmBasicInfoService.countSecondShop(secondCode);
        if(secondNum>0){
            model.addAttribute("msg", "需绑定为次店的会员，已关联其他次店，不可进行绑定");
            return Constants.MSG_URL;
        }
        //5.修改次店会员手机号以及主次店标识，判断主店会员积分账户是否激活，如果未激活，不做处理，如果激活且次店会员为激活，使用主店会员信息激活次店会员积分信息
        rdMmBasicInfoService.storeBinding(mainBasic,secondBasic);
        return "";
    }

    /**
     * 主次店会员绑定页面跳转
     * @param model
     * @return
     */
    @RequestMapping(value ="/forward",method = RequestMethod.GET)
    public String forward(Model model) {
        return "";//跳往前端操作页面
    }
}
