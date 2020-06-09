package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopCommonArticleClassService;
import com.framework.loippi.service.common.ShopCommonArticleService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.ParameterUtils;
import com.framework.loippi.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longbh on 2017/8/19.
 */
@Controller("adminShopCommonArticleController")
@RequestMapping("/admin/article")
public class ShopCommonArticleController extends GenericController {

    @Resource
    private TwiterIdService twiterIdService;

    @Resource
    private ShopCommonArticleService shopCommonArticleService;

    @Resource
    private ShopCommonArticleClassService shopCommonArticleClassService;


    @RequestMapping(value = {"/list"})
    public String index(Model model,
                        Pageable pageable,
                        HttpServletRequest request) {
        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, null);
        Map<String, Object> paramterPage = new HashMap<String, Object>();
        try {
            String context = null;
            Integer articleShow = null;
            Integer status = null;
            if (paramterPage != null) {
                if (paramter.get("context") != null && !StringUtil.isEmpty((String) paramter.get("context"))) {
                    paramterPage.put("articleTitle", paramter.get("context"));
                    context = (String) paramter.get("context");
                }
                if (paramter.get("articleShow") != null && !StringUtil.isEmpty((String) paramter.get("articleShow"))) {
                    String articleShowStr = (String) paramter.get("articleShow");
                    if (!StringUtil.isEmpty(articleShowStr)) {
                        articleShow = Integer.valueOf(articleShowStr);
                        paramterPage.put("articleShow", articleShow);
                    }
                }
                if (paramter.get("status") != null && !StringUtil.isEmpty((String) paramter.get("status"))) {
                    String statusStr = (String) paramter.get("status");
                    if (!StringUtil.isEmpty(statusStr)) {
                        status = Integer.valueOf(statusStr);
                        paramterPage.put("status", status);
                    }
                }
            }
            model.addAttribute("context", context);
            model.addAttribute("status",status);
            model.addAttribute("articleShow",articleShow);
            if (paramterPage.size() > 0) {
                pageable.setParameter(paramterPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pageable.setOrderProperty("article_sort");
        pageable.setOrderDirection(Order.Direction.ASC);
        model.addAttribute("page", shopCommonArticleService.findByPage(pageable));//总数
        return "/content/article/list";
    }

    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute ShopCommonArticle article,ModelMap model,
                               RedirectAttributes redirectAttributes) {
        try {
            if(article.getArticleImage()==null||"".equals(article.getArticleImage().trim())){
                model.addAttribute("msg", "请上传文章封面图片");
                return Constants.MSG_URL;
            }
            if (article.getId() == null) {
                article.setCreateTime(new Date());
                article.setUpdateTime(new Date());
                article.setId(twiterIdService.getTwiterId());
                if (article.getStatus() != null) {
                    article.setStatus(1);
                } else {
                    article.setStatus(0);
                }
                article.setIsDel(0);
                shopCommonArticleService.save(article);
            } else {
                article.setUpdateTime(new Date());
                shopCommonArticleService.update(article);
            }
            addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            addFlashMessage(redirectAttributes, ERROR_MESSAGE);
        }
        return "redirect:list.jhtml";
    }

    @RequestMapping(value = {"/add"}, method = {RequestMethod.GET})
    public String add(ModelMap model, Integer articleType) {
        if (articleType != null) {
            //model.addAttribute("articleList", shopCommonArticleClassService.findAll());
            //添加学堂文章，查询出类型为学堂文章的一级分类列表 TODO
            List<ShopCommonArticleClass> list = shopCommonArticleClassService.findList(Paramap.create().put("acParentId",0).put("acStatus",0).put("acCode","school-articles"));
            model.addAttribute("articleList", list);
            if (articleType.intValue() != 1) {
                return "/article/add_guding";
            }
        }
        return "/content/article/add";
    }

    /**
     * 根据父类id查找父类id下面的所有启用状态的文章分类
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = {"/findSonById"}, method = {RequestMethod.GET})
    public String findSonById(ModelMap model, Long id) {
        if (id == null) {
            List<ShopCommonArticleClass> list = shopCommonArticleClassService.findList(Paramap.create().put("acParentId", 0).put("acStatus", 0).put("acCode","school-articles"));
            model.addAttribute("articleClassList",list);
        }else {
            List<ShopCommonArticleClass> list = shopCommonArticleClassService.findList(Paramap.create().put("acParentId", id).put("acStatus", 0).put("acCode","school-articles"));
            model.addAttribute("articleClassList",list);
        }
        return " ";//TODO
    }

    @RequestMapping(value = {"/edit"}, method = {RequestMethod.GET})
    public String edit(ModelMap model, Long id,
                       RedirectAttributes redirectAttributes) {
        ShopCommonArticle article = shopCommonArticleService.find(id);
        if (article == null) {
            addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return "redirect:list.jhtml";
        }
        model.addAttribute("articleList", shopCommonArticleClassService.findAll());
        model.addAttribute("article", article);
        return "/content/article/edit";
    }

    @RequestMapping(value = {"/view"}, method = {RequestMethod.GET})
    public String view(ModelMap model, Long id,
                       RedirectAttributes redirectAttributes) {
        ShopCommonArticle article = shopCommonArticleService.find(id);
        if (article == null) {
            addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return "redirect:list.jhtml";
        }
        model.addAttribute("article", article);
        return "/content/article/view";
    }

    /**
     * 更新发布状态
     *
     * @param
     * @return
     */
    @RequestMapping(value = {"/updateStatus"}, method = {RequestMethod.POST})
    public String updateStatus(Long[] ids, Integer status, ModelMap model, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (ids == null || ids.length == 0 || status == null) {
            return Constants.MSG_URL;
        }
        if (shopCommonArticleService.updateStatusBatch(ids, status)) {
            return Constants.MSG_URL;
        }
        return Constants.MSG_URL;
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = {"/delete"}, method = {RequestMethod.POST})
    public String delete(Long[] ids, ModelMap model, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (ids == null || ids.length == 0) {
            return Constants.MSG_URL;
        }
        try {
            shopCommonArticleService.deleteAll(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constants.MSG_URL;
    }

}
