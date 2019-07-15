package com.framework.loippi.controller.product;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.product.ShopGoodsEvaluateSensitivity;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.product.ShopGoodsEvaluateSensitivityService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controller - 评价敏感词
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopGoodsEvaluateSensitivityController")
@RequestMapping({"/trade/evalGoodsSensitivity"})
public class ShopGoodsEvaluateSensitivitySysController extends GenericController {

    @Resource
    private ShopGoodsEvaluateSensitivityService shopGoodsEvaluateSensitivityService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RedisService redisService;

    /**
     * 跳转添加页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/form"}, method = {RequestMethod.GET})
    public String form(ModelMap model, ShopGoodsEvaluateSensitivity shopGoodsEvaluateSensitivity) {
        if (shopGoodsEvaluateSensitivity.getId() != null) {
            shopGoodsEvaluateSensitivity = shopGoodsEvaluateSensitivityService.find(shopGoodsEvaluateSensitivity.getId());
        }
        model.addAttribute("shopGoodsEvaluateSensitivity", shopGoodsEvaluateSensitivity);
        return "/views/trade/evalGoodsSensitivity/form";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(ShopGoodsEvaluateSensitivity shopGoodsEvaluateSensitivity, RedirectAttributes redirectAttributes) {
        shopGoodsEvaluateSensitivity.setUpdatetime(new Date());
        if (shopGoodsEvaluateSensitivity.getId() != null) {
            shopGoodsEvaluateSensitivityService.update(shopGoodsEvaluateSensitivity);
        } else {
            shopGoodsEvaluateSensitivity.setId(twiterIdService.getTwiterId());
            shopGoodsEvaluateSensitivityService.save(shopGoodsEvaluateSensitivity);
        }
        redisService.delete("sensitive_word_of_evaluation");
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(Long id, ModelMap model) {
        ShopGoodsEvaluateSensitivity shopGoodsEvaluateSensitivity = shopGoodsEvaluateSensitivityService.find(id);
        model.addAttribute("shopGoodsEvaluateSensitivity", shopGoodsEvaluateSensitivity);
        return "/views/trade/evalGoodsSensitivity/view";
    }

    /**
     * 列表查询
     *
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value ="/list")
    public String list(Pageable pageable, HttpServletRequest request, ModelMap model,ShopGoodsEvaluateSensitivity shopGoodsEvaluateSensitivity) {
//        processQueryConditions(pageable, request);
        pageable.setParameter(shopGoodsEvaluateSensitivity);
        model.addAttribute("shopGoodsEvaluateSensitivity",shopGoodsEvaluateSensitivity);
        model.addAttribute("page", this.shopGoodsEvaluateSensitivityService.findByPage(pageable));
        return "/views/trade/evalGoodsSensitivity/list";
    }

    /**
     * 删除操作
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = {"/delete"}, method = {RequestMethod.POST})
    public
    @ResponseBody
    Message delete(Long[] ids) {
        this.shopGoodsEvaluateSensitivityService.deleteAll(ids);
        redisService.delete("sensitive_word_of_evaluation");
        return SUCCESS_MESSAGE;
    }
}
