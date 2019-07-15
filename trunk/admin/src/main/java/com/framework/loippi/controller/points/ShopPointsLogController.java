package com.framework.loippi.controller.points;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.point.ShopPointsLog;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.point.ShopPointsLogService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller - 会员积分日志表
 *
 * @author zijing
 * @version 2.0
 */
@Controller("adminShopPointsLogController")
@RequestMapping({"/admin/shop_points_log"})
public class ShopPointsLogController extends GenericController {

    @Resource
    private ShopPointsLogService shopPointsLogService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 会员积分日志表列表
     *
     * @param pageNo
     * @return
     */
    @RequestMapping("/list")
    public String list(Model model, @RequestParam(required = false, value = "pageNo", defaultValue = "1")
            int pageNo, HttpServletRequest request) {
        Pageable pager = new Pageable(pageNo, 20);
        processQueryConditions(pager, request);
        Page<ShopPointsLog> result = shopPointsLogService.findByPage(pager);
        model.addAttribute("paramter", request);
        model.addAttribute("pager", result);
        return "/points/pointslog/shopPointsLogList";
    }

    /**
     * 跳转至会员积分日志表新增或修改页面
     * @param id
     * @return
     */
    @RequestMapping("/forward")
    public String add(Model model, @RequestParam(required = false, value = "id") Long id) {
        ShopPointsLog shopPointsLog = shopPointsLogService.find(id);
        if (shopPointsLog == null) {
            shopPointsLog = new ShopPointsLog();
        }
        model.addAttribute("shopPointsLog", shopPointsLog);
        return "/points/pointslog/shopPointsLog";
    }

    /**
     * 会员积分日志表修改或保存
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(Model model, @ModelAttribute ShopPointsLog shopPointsLog,
                               @RequestParam(required = false, value = "id", defaultValue = "0") Long id) {
        if (id != 0) {
            shopPointsLogService.update(shopPointsLog);
            model.addAttribute("msg", "修改成功");
        } else {
            shopPointsLog.setId(twiterIdService.getTwiterId());
            shopPointsLogService.save(shopPointsLog);
            model.addAttribute("msg", "保存成功");
        }
        model.addAttribute("referer", "/admin/shop_points_log/list.jhtml");
        return Constants.MSG_URL;
    }

    /**
     * 会员积分日志表删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public String delete(Model model, @RequestParam Long[] ids,
                         HttpServletRequest request) {
        for (Long id : ids) {
            shopPointsLogService.delete(id);
        }
        model.addAttribute("referer", "/admin/shop_points_log/list.jhtml");
        return Constants.MSG_URL;
    }

}
