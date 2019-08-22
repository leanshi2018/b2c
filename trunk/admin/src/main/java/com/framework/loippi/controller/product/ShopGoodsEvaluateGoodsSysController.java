package com.framework.loippi.controller.product;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.consts.IntegrationNameConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsEvaluateSetting;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.validator.DateUtils;

/**
 * 评论管理
 */
@Slf4j
@Controller
@RequestMapping("/trade/evalGoods")
public class ShopGoodsEvaluateGoodsSysController extends BaseController {

    @Resource
    private ShopGoodsEvaluateService evaluateGoodsService;
    @Resource
    private ShopGoodsService goodsService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private ShopOrderService shopOrderService;
    @Autowired
    private TUserSettingService tUserSettingService;
    @Resource
    protected Validator validator;
    protected String json;
    @Resource
    private com.framework.loippi.service.user.RdMmAccountLogService RdMmAccountLogService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;

    @RequiresPermissions("sys:evaluation:view")
    @RequestMapping(value = "/list")
    public ModelAndView list(Model model, @ModelAttribute ShopGoodsEvaluate evaluateGoods, HttpServletRequest request,
        @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
        @RequestParam(required = false, value = "sTimeStr", defaultValue = "") String sTimeStr,
        @RequestParam(required = false, value = "eTimeStr", defaultValue = "") String eTimeStr) {

        Pageable pager = new Pageable();
        ModelAndView modelAndView = new ModelAndView("/views/trade/evalGoods/list");
        if (StringUtils.isNotEmpty(sTimeStr)) {
            Long sTimeLong = DateUtils.strToLong(sTimeStr + " 00:00:00");
            evaluateGoods.setStartTime(sTimeLong);
            model.addAttribute("sTimeStr", sTimeStr);
        }
        if (StringUtils.isNotEmpty(eTimeStr)) {
            Long eTimeLong = DateUtils.strToLong(eTimeStr + " 23:59:59");
            evaluateGoods.setEndTime(eTimeLong);
            model.addAttribute("eTimeStr", eTimeStr);
        }
        if (!StringUtils.isEmpty(pageNoStr)) {
            pager.setPageNumber(Integer.parseInt(pageNoStr));
        }
        pager.setParameter(evaluateGoods);
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        Page<ShopGoodsEvaluate> page = this.evaluateGoodsService.findWithGoodsByPage(pager, request.getContextPath());
        model.addAttribute("pager", pager);
        model.addAttribute("geval", evaluateGoods);
        model.addAttribute("page", page);
        return modelAndView;
    }

    /**
     * 评价编辑
     */
    @RequestMapping("form")
    public String form(Model model, ShopGoodsEvaluate evaluate) {
        if (evaluate.getId() != null) {
            evaluate = evaluateGoodsService.find(evaluate.getId());
            Long goodsId = evaluate.getGevalGoodsid();
            if (goodsId != null) {
                ShopOrderGoods goods = shopOrderGoodsService.find(evaluate.getGevalOrdergoodsid());
                evaluate.setGevalGoodsname(goods.getGoodsName());
            }
            if (StringUtils.isNotBlank(evaluate.getGevalImage())) {
                evaluate.setGevalImageArr(evaluate.getGevalImage().split("##"));
            }
        }
        model.addAttribute("evaluate", evaluate);
        model.addAttribute("goodses", goodsService.findList(new HashMap<String, Object>()));
        return "/views/trade/evalGoods/form";
    }

    /**
     * 评论查看
     */
    @RequestMapping("view")
    public String view(Model model, ShopGoodsEvaluate evaluate) {
        evaluate = evaluateGoodsService.find(evaluate.getId());
        if (StringUtils.isNotBlank(evaluate.getGevalImage())) {
            evaluate.setGevalImageArr(evaluate.getGevalImage().split(","));
        }
        if (evaluate.getGevalAddtime() != null) {
            evaluate.setGevalAddtimeStr(new SimpleDateFormat("yyyy-MM-dd hh:mm")
                .format(evaluate.getGevalAddtime()));
        }
        Long goodsId = evaluate.getGevalGoodsid();
        if (goodsId != null) {
            ShopGoods goods = shopGoodsService.find(goodsId);
            if (goods != null) {
                evaluate.setGevalGoodsname(goods.getGoodsName());
            }
        }
//        if (StringUtils.isNotBlank(evaluate.getGevalImage())) {
//            evaluate.setGevalImageArr(evaluate.getGevalImage().split("##"));
//        }
        model.addAttribute("evaluate", evaluate);
        return "/views/trade/evalGoods/view";
    }

    /**
     * 保存
     */
    @RequestMapping("save")
    public String save(HttpServletRequest request, ShopGoodsEvaluate evaluate) {
        evaluate.setIp(request.getRemoteAddr());
        if (evaluate.getId() == null) {
            evaluate.setCheckedstatus(1);
            evaluate.setGevalAddtime(new Date());
            evaluate.setId(twiterIdService.getTwiterId());
            evaluate.setParentId(0l);
            evaluate.setCreateTime(new Date());
            evaluateGoodsService.save(evaluate);
        } else {
            evaluate.setUpdateTime(new Date());
            evaluateGoodsService.update(evaluate);
        }
        return "redirect:list.jhtml";
    }

    /**
     * 管理员回复评价,分配积分
     */
    @RequestMapping("saveReplyAndIntegration")
    public String saveReplyAndIntegration(HttpServletRequest request, Long id, Integer type, String message,
        ModelMap model) {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        ShopGoodsEvaluate shopGoodsEvaluate = evaluateGoodsService.find(id);
        if (shopGoodsEvaluate == null) {
            model.addAttribute("msg", "评论不存在");
            return Constants.MSG_URL;
        }
        ShopGoodsEvaluate evaluate = new ShopGoodsEvaluate();
        evaluate.setId(id);
        //type=2 表示管理员是进行评价回复
        if (type == 2) {
            evaluate.setGevalRemark(message);
            evaluate.setRemarkTime(new Date());
            evaluateGoodsService.update(evaluate);
            model.addAttribute("msg", "回复成功");
        }
        //type=1 表示管理员是进行积分分配
        if (type == 1) {
            Double points = 0.00;
            try {
                points = Double.valueOf(message);
                if (points!=1&&points!=0.5){
                    model.addAttribute("msg", "分配积分系数不正确");
                    return Constants.MSG_URL;
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("msg", "积分必须为数字");
                return Constants.MSG_URL;
            }

            String adminName="";//操作人
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                Principal principal = (Principal) subject.getPrincipal();
                if (principal != null && principal.getId() != null) {
                    adminName=principal.getUsername();
                }
            }

            System.out.println("adminName="+adminName);

            ShopOrderGoods shopOrderGoods = shopOrderGoodsService.find(shopGoodsEvaluate.getGevalOrdergoodsid());//订单商品
            BigDecimal price = shopOrderGoods.getVipPrice();//vip价格
            Integer goodsNum = shopOrderGoods.getGoodsNum();

            BigDecimal multiply = price.multiply(new BigDecimal(points)).multiply(new BigDecimal(goodsNum));

            evaluate.setExchangePoints(multiply.intValue());
            evaluateGoodsService.update(evaluate);
            RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", shopGoodsEvaluate.getGevalFrommemberid());
            RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());
            RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
            rdMmAccountLog.setTransTypeCode("PC");
            rdMmAccountLog.setAccType("SRB");
            rdMmAccountLog.setTrSourceType("SBB");
            rdMmAccountLog.setMmCode(shopMember.getMmCode());
            rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
            rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
            rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getRedemptionBlance());
            rdMmAccountLog.setAmount(BigDecimal.valueOf(points));
            rdMmAccountLog.setTransDate(new Date());
            //转出无需审核直接成功
            rdMmAccountLog.setStatus(3);
            rdMmAccountLog.setCreationBy(adminName);
            rdMmAccountLog.setCreationTime(new Date());
            rdMmAccountLog.setAutohrizeBy(adminName);
            rdMmAccountLog.setAutohrizeTime(new Date());
            rdMmAccountInfo.setRedemptionBlance(rdMmAccountInfo.getRedemptionBlance().add(BigDecimal.valueOf(points)));
            rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getRedemptionBlance());
            List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
            rdMmAccountLogList.add(rdMmAccountLog);
            Integer transNumber = rdMmAccountInfoService
                .saveAccountInfo(rdMmAccountInfo, multiply.intValue(), IntegrationNameConsts.BOP, rdMmAccountLogList, null);
            model.addAttribute("msg", "积分分配成功");
        }
        return Constants.MSG_URL;
    }

    /**
     * 修改状态
     */
    @RequestMapping(value = "updateStatus", method = RequestMethod.POST)
    public
    @ResponseBody
    void updateStatus(@RequestParam("ids") String ids,
        @RequestParam("checkedstatus") String checkedstatus) {
        for (String id : ids.split(",")) {
            ShopGoodsEvaluate evaluate = new ShopGoodsEvaluate();
            evaluate.setId(Long.parseLong(id));
            evaluate.setGevalState(Integer.parseInt(checkedstatus));
            evaluateGoodsService.update(evaluate);
        }
    }

    /**
     * 删除
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public String del(@RequestParam("ids") String ids) {
        String[] idArray = ids.split(",");
        Long[] idLongs = new Long[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            idLongs[i] = Long.valueOf(idArray[i]);
        }
        evaluateGoodsService.deleteAll(idLongs);
        return "redirect:list.jhtml";
    }

    /**
     * 单条删除
     */
    @RequiresPermissions("sys:evaluation:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam Long id, Model model, HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (id.equals("0")) {
            model.addAttribute("msg", requestContext.getMessage("delete_error"));
        } else {
            evaluateGoodsService.delete(id);
            model.addAttribute("msg", requestContext.getMessage("delete.success"));
        }
        return "redirect:list.jhtml";
    }

    /**
     * 评价设置
     */
    @RequestMapping(value = "setting", method = RequestMethod.GET)
    public String setting(Model model) {
        String set = tUserSettingService.read("evaluate_setting") + "";
        if (set == null || "null".equals(set) || "".equals(set)) {
            model.addAttribute("evalSetting", new ShopGoodsEvaluateSetting());
        } else {
            model.addAttribute("evalSetting", JacksonUtil.fromJson(set, ShopGoodsEvaluateSetting.class));
        }
        return "/views/trade/evalGoods/setting";
    }

    @RequestMapping(value = "saveSetting", method = RequestMethod.POST)
    public String saveSetting(ShopGoodsEvaluateSetting shopGoodsEvaluateSetting) {
        tUserSettingService.save("evaluate_setting", JacksonUtil.toJson(shopGoodsEvaluateSetting));
        return "redirect:setting.jhtml";
    }

    @RequestMapping("/listGoods")
    public String listGoods(Model model,
        @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
        String goodsIds, String checkIds, String giveIds) {
        Pageable pager = new Pageable();
        pager.setPageNumber(pageNo);
        ShopGoods goods = new ShopGoods();
        goods.setState(GoodsState.GOODS_OPEN_STATE);
        //设置上架状态
        goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);
        //设置删除状态
        goods.setIsDel(2);
        List<Long> ids = new ArrayList<>();
        Map<String, Object> qyMap = new HashMap<>();
        //时间降序
        goods.setSortField("goodsAddTime");
        goods.setOrderBy("desc");
        pager.setParameter(goods);
        pager.setPageSize(5);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);
        model.addAttribute("goodsIds", goodsIds);
        model.addAttribute("checkIds", checkIds);
        model.addAttribute("giveIds", giveIds);
        model.addAttribute("pager", byPage);
        model.addAttribute("pageNo", pager.getPageNumber());    // 当前页
        model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
        model.addAttribute("toUrl", "/trade/evalGoods/listGoods");
        return "/views/trade/evalGoods/goods_select";
    }

}
