package com.framework.loippi.controller.admin;

import java.util.List;

import javax.annotation.Resource;

import com.framework.loippi.utils.Paramap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.entity.Acl;
import com.framework.loippi.entity.Role;
import com.framework.loippi.service.AclService;
import com.framework.loippi.service.RoleService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.controller.GenericController;

/**
 * Controlelr - 角色
 *
 * @author Loippi Team
 * @version 1.0
 */
@Controller("adminRoleController")
@RequestMapping("/admin/role")
public class RoleController extends GenericController {

    @Resource
    private AclService aclService;

    @Resource
    private RoleService roleService;

    /**
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(ModelMap model) {
        List<Acl> roots = aclService.findRoots(1);
        for (Acl acl : roots) {
            List<Acl> childrens = aclService.findChildrens(1, acl.getId());
            for (Acl child : childrens) {
                List<Acl> btns = aclService.findChildrens(1, child.getId());
                child.getChildren().addAll(btns);
            }
            acl.getChildren().addAll(childrens);

        }
        model.addAttribute("roots", roots);
        return "/admin/role/add";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(Role role, Long[] ids, RedirectAttributes redirectAttributes) {
        roleService.save(role, ids);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long id, ModelMap model) {
        List<Acl> roots = aclService.findRoots(1);
        for (Acl acl : roots) {
            List<Acl> childrens = aclService.findChildrens(1, acl.getId());
            for (Acl child : childrens) {
                List<Acl> btns = aclService.findChildrens(1, child.getId());
                child.getChildren().addAll(btns);
            }
            acl.getChildren().addAll(childrens);

        }
        model.addAttribute("roots", roots);
        model.addAttribute("role", roleService.findRoleAndAcls(id));
        return "/admin/role/edit";
    }

    /**
     * 更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(Role role, Long[] ids, RedirectAttributes redirectAttributes) {
        roleService.update(role, ids);
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 列表
     */
    @RequiresPermissions("admin:system:role")
    @RequestMapping("/list")
    public String list(Pageable pageable, ModelMap model,String keyWord) {
        pageable.setParameter(Paramap.create().put("keyWord",keyWord));
        model.addAttribute("page", roleService.findByPage(pageable));
        model.addAttribute("keyWord", keyWord);
        return "/admin/role/list";
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public
    @ResponseBody
    Message delete(Long[] ids) {
        for (Long id : ids) {
            roleService.delete(id);
        }

        return SUCCESS_MESSAGE;
    }
}
