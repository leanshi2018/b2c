package com.framework.loippi.controller.common;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.ShopCommonMessageService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;

/**
 * 后台消息创建
 */
@Controller("ShopCommonMessageNewController")
@RequestMapping("/admin/shop_common_message_new")
public class ShopCommonMessageNewController extends GenericController {
    @Resource
    private ShopCommonMessageService shopCommonMessageService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 查询消息列表
     * @param model
     * @param shopCommonMessage 查询条件
     * @param pageSize
     * @param pageNo
     * @return
     */
    @RequestMapping(value ="/list")
    public String list(ModelMap model, @ModelAttribute ShopCommonMessage shopCommonMessage,
                       @RequestParam(required = false, value = "pageSize", defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo) {
        Pageable pager = new Pageable(pageNo, pageSize);
        shopCommonMessage.setBizType(1);
        shopCommonMessage.setType(1);
        pager.setParameter(shopCommonMessage);
        pager.setOrderProperty("create_time");
        pager.setOrderDirection(Order.Direction.DESC);
        Page<ShopCommonMessage> results = shopCommonMessageService.findByPage(pager);//结果集
        model.addAttribute("page", results);
        return "/common/notification_message/index";
    }

    /**
     * 新增或者编辑按钮
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value ="/forward",method = RequestMethod.GET)
    public String forward(Model model, @RequestParam(value = "id",required = false) Long id) {
        if (id != null && id != 0) {
            ShopCommonMessage message = shopCommonMessageService.find(id);
            if(message!=null){
                model.addAttribute("message", shopCommonMessageService.find(id));
                if(message.getJumpPath()!=null&&!"".equals(message.getJumpPath())){
                    Map<String, String> map = JacksonUtil.readJsonToMap(message.getJumpPath());
                    model.addAttribute("jumpPathData",map);
                }
            }else {
                model.addAttribute("message", null);
            }
            return "/common/notification_message/edit";//跳往新增或编辑页面
        } else {
            model.addAttribute("message", null);
            return "/common/notification_message/edit";
        }
    }

    /**
     * 新增
     * @param model
     * @return
     */
    @RequestMapping(value ="/add",method = RequestMethod.GET)
    public String add(Model model) {
        return "/common/notification_message/edit";
    }

    /**
     * 创建或者修改消息
     * @param message
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate",method = RequestMethod.POST)
    public String saveOrUpdate(@ModelAttribute ShopCommonMessage message, HttpServletRequest request, Model model) {
        if(StringUtil.isEmpty(message.getTitle())){
            model.addAttribute("msg","通知消息标题不可以为空");
            return Constants.MSG_URL;
        }
        if(StringUtil.isEmpty(message.getContent())){
            model.addAttribute("msg","通知消息内容不可以为空");
            return Constants.MSG_URL;
        }
        if(message.getTitle().length()>20){
            model.addAttribute("msg","通知消息标题过长");
            return Constants.MSG_URL;
        }
        if(message.getContent().length()>150){
            model.addAttribute("msg","通知消息内容过长");
            return Constants.MSG_URL;
        }
        if(message.getId()!=null){
            shopCommonMessageService.update(message);
        }else {
            message.setId(twiterIdService.getTwiterId());
            message.setBizType(1);//设置类型为消息通知
            message.setCreateTime(new Date());
            message.setType(1);
            message.setOnLine(1);
            message.setIsTop(1);
            shopCommonMessageService.save(message);
        }
        return "redirect:list.jhtml";
    }

    /**
     * 停用消息
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value ="/delete",method = RequestMethod.GET)
    public String delete(Model model, @RequestParam Long id) {
       if(id==null){
           model.addAttribute("msg","请指定需要停用的系统消息");
           return Constants.MSG_URL;
       }
        ShopCommonMessage shopCommonMessage = shopCommonMessageService.find(id);
       if(shopCommonMessage==null){
           model.addAttribute("msg","当前指定消息不存在");
           return Constants.MSG_URL;
       }
       shopCommonMessageService.delete(id);
       return "redirect:list.jhtml";
    }
}
