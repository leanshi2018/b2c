package com.framework.loippi.controller.trade;

import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import javax.annotation.Resource;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 * Controller - 优惠订单类型
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopOrderDiscountTypeController")
@RequestMapping({"/admin/shop_order_discount_type"})
public class ShopOrderDiscountTypeController extends GenericController {

    @Resource
    private ShopOrderDiscountTypeService shopOrderDiscountTypeService;

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, ModelMap model) {
        ShopOrderDiscountType shopOrderDiscountType;
        if (id != null && id != 0) {
            shopOrderDiscountType = shopOrderDiscountTypeService.find(id);
        } else {
            shopOrderDiscountType = new ShopOrderDiscountType();
        }
        model.addAttribute("shopOrderDiscountType", shopOrderDiscountType);
        return "/trade/ordertype/update";
    }

    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(ShopOrderDiscountType shopOrderDiscountType, RedirectAttributes redirectAttributes) {
       if (shopOrderDiscountType.getPreferentialType()!=4){
           shopOrderDiscountType.setPreferential(BigDecimal.ZERO);
       }
        if (Optional.ofNullable(shopOrderDiscountType.getId()).orElse(0L) == 0L && Optional.ofNullable(shopOrderDiscountType.getId()).orElse(1L) != 0) {
            shopOrderDiscountType.setUpdateTime(new Date());
            shopOrderDiscountType.setCreateTime(new Date());
            shopOrderDiscountTypeService.save(shopOrderDiscountType);
        } else {
            shopOrderDiscountType.setUpdateTime(new Date());
            shopOrderDiscountTypeService.update(shopOrderDiscountType);
        }
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 列表查询
     *
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value = {"/list"}, method = {RequestMethod.GET})
    public String list(Pageable pageable, ModelMap model) {
        pageable.setOrderProperty("sort");
        pageable.setOrderDirection(Direction.ASC);
        model.addAttribute("page", this.shopOrderDiscountTypeService.findByPage(pageable));
        return "/trade/ordertype/list";
    }

    /**
     * 删除操作
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = {"/delete"}, method = {RequestMethod.POST})
    public

    String delete(Long[] ids) {
        this.shopOrderDiscountTypeService.deleteAll(ids);
        return "redirect:list.jhtml";
    }
}
