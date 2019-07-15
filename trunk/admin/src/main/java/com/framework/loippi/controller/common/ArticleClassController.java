package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.mybatis.paginator.domain.Order.Direction;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopCommonArticleClassService;
import com.framework.loippi.service.common.ShopCommonArticleService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.file.FileUtils;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by rabook on 2014/11/4.
 */
@Slf4j
@Controller
@RequestMapping("/admin/class")
public class ArticleClassController extends GenericController {

    @Resource
    private ShopCommonArticleClassService shopCommonArticleClassService;
    @Resource
    private ShopCommonArticleService shopCommonArticleService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     * 列表
     *
     * @param model
     * @return
     */
    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest request,ShopCommonArticleClass shopCommonArticleClass,
    @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNumber) {
        Pageable pager = new Pageable();
        shopCommonArticleClass.setAcParentId(0L);
        shopCommonArticleClass.setAcCode("school-articles");
        pager.setParameter(shopCommonArticleClass);
        pager.setPageNumber(pageNumber);
        pager.setOrderDirection(Direction.ASC);
        pager.setOrderProperty("ac_sort");
        Page<ShopCommonArticleClass> results = shopCommonArticleClassService.findByPage(pager);//结果集
        model.addAttribute("page", results);//结果集
        model.addAttribute("shopCommonArticleClass", shopCommonArticleClass);
        return "content/class/list2";
    }

    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam Long[] ids, Model model, HttpServletRequest request) throws IOException {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        RequestContext requestContext = new RequestContext(request);
        ShopCommonArticle article = new ShopCommonArticle();
        StringBuilder stringBuilder = new StringBuilder();
        for (Long id : ids) {
            article.setAcId(id);
            List<ShopCommonArticle> articleAll = shopCommonArticleService.findList("acId", id);
            if (articleAll.size() > 0) {
                for (ShopCommonArticle articles : articleAll) {
                    shopCommonArticleService.delete(articles.getId());
                }
            }
            //ToStaticSendToFront.saveArticleDetailToStaticBatch();
            ShopCommonArticleClass articleClass = shopCommonArticleClassService.find(id);
//            String staticPath = ActivityTypeEnus.STATIC_PAGE_BASEPATH + articleClass.getStaticUrl();
//            FileUtils.delete(staticPath);
            stringBuilder.append(articleClass.getAcName()).append(",");
            shopCommonArticleClassService.delete(id);
        }
        model.addAttribute("msg", requestContext.getMessage("delete.success"));
        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/findOne")
    public String findOne(@RequestParam String id, Model model,String gcParentId) {
        model.addAttribute("list", shopCommonArticleClassService.findList(Paramap.create().put("acCode",	"school-articles").put("acParentId",0)));
        model.addAttribute("parentId", gcParentId);
        return "content/class/save";
    }

    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute ShopCommonArticleClass articleClass,
                               ModelMap model, HttpServletRequest request) throws IOException {
        RequestContext requestContext = new RequestContext(request);

        //验证数据有效性
        if (!beanValidatorForModel(model, articleClass)) {
            model.addAttribute("referer", "list.jhtml");
            return Constants.MSG_URL;
        }
        if (articleClass.getId() == null || articleClass.getId() == 0) {
            articleClass.setCreateTime(new Date());
            articleClass.setId(twiterIdService.getTwiterId());
            if (articleClass.getAcStatus()==null){
                articleClass.setAcStatus(0);
            }
            shopCommonArticleClassService.save(articleClass);
            //ToStaticSendToFront.saveArticleDetailToStaticBatch();
            model.addAttribute("msg", requestContext.getMessage("Delivery_settings_msg17"));
        } else {
            ShopCommonArticleClass ac = shopCommonArticleClassService.find(articleClass.getId());
            ac.setAcName(articleClass.getAcName());
            ac.setAcSort(articleClass.getAcSort());
            ac.setAcCode(articleClass.getAcCode());
            ac.setRemarks(articleClass.getRemarks());
            ac.setImageUrl(articleClass.getImageUrl());
            ac.setAcStatus(articleClass.getAcStatus());
            ac.setUpdateTime(new Date());
            shopCommonArticleClassService.update(ac);
            //ToStaticSendToFront.saveArticleDetailToStaticBatch();
            model.addAttribute("msg", requestContext.getMessage("successful_modification"));
        }
        model.addAttribute("referer", "list.jhtml");
        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/editFind")
    public String findForUpdate(@RequestParam Long id, Model model) {
        model.addAttribute("list", shopCommonArticleClassService.findList(Paramap.create().put("acCode","school-articles")));
        model.addAttribute("class", shopCommonArticleClassService.find(id));
        return "content/class/edit";
    }

    /**
     * 查询子列表
     *
     * @param id 父id
     * @return json
     */
    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("child")
    public
    @ResponseBody
    List<ShopCommonArticleClass> child(@RequestParam Long id, @RequestParam int level) {
        //存入deep，配合ajax
        List<ShopCommonArticleClass> classList = shopCommonArticleClassService.findList("acParentId", id);
        for (ShopCommonArticleClass vo : classList) {
            vo.setDeep(level);
            vo.setAcNameLike(vo.getId()+"");
            vo.setAcParentIdStr(vo.getAcParentId()+"");
        }
        return classList;
    }

    /**
     * 修改排序
     *
     * @return
     */
    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/modifySort")
    public
    @ResponseBody
    Boolean modifySort(@RequestParam Long id, @RequestParam Integer value, HttpServletRequest request) {
        ShopCommonArticleClass articleClass = new ShopCommonArticleClass();
        articleClass.setId(id);
        articleClass.setAcSort(value);
        articleClass.setUpdateTime(new Date());
        shopCommonArticleClassService.update(articleClass);
        return true;
    }

    /**
     * 修改分类名称
     *
     * @param id
     * @param value
     * @return
     */
    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/modifyName")
    public
    @ResponseBody
    Boolean modifyname(@RequestParam Long id, @RequestParam String value, HttpServletRequest request) {
        ShopCommonArticleClass articleClass = new ShopCommonArticleClass();
        articleClass.setId(id);
        articleClass.setAcName(value);

        //验证数据有效性
        if (!beanValidatorForJson(articleClass)) {
            return false;
        }
        //判断是否有重复名称
        Map<String, Object> params = Paramap.create().put("acName", value).put("noId", id);
        Long count = shopCommonArticleClassService.count(params);
        if (count > 0) {
            return false;
        } else {
            //执行修改操作
            articleClass.setUpdateTime(new Date());
            shopCommonArticleClassService.update(articleClass);
            return true;
        }
    }

    /**
     * 校验表单
     *
     * @return
     */
    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/validate")
    public
    @ResponseBody
    String validateForm(Long id, String acName) {
        //校验重复
        Map<String, Object> params = Paramap.create().put("acName", acName).put("noId", id);
        Long articleClass = shopCommonArticleClassService.count(params);
        if (articleClass > 0) {
            return "false";
        } else {
            return "true";
        }
    }

    /**
     * 校验菜单下是否有子菜单
     *
     * @return
     */
    @RequestMapping("/validateparentid")
    public
    @ResponseBody
    Boolean validateparentid(@RequestParam Long id) {
        //校验重复
        Map<String, Object> params = new HashMap<>();
        params.put("acParentId", id);
        Long goodsclaslist = shopCommonArticleClassService.count(params);
        if (goodsclaslist > 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * 校验菜单下是否有文章
     *
     * @return
     */
    @RequestMapping("/validateArticleByGcId")
    public
    @ResponseBody
    Boolean validateArticleByGcId(@RequestParam Long id) {
        //校验重复
        Long goodsclaslist = shopCommonArticleService.count(Paramap.create().put("id",id));
        if (goodsclaslist > 0) {
            return false;
        } else {
            return true;
        }
    }

}
