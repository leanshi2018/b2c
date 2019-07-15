package com.framework.loippi.controller.common;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.DocumentConsts;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.common.ShopCommonDocumentService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.Param;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 系统文章管理
 */
@Slf4j
@Controller
@RequestMapping("/admin/document")
public class ShopDocumentController extends GenericController {

    @Resource
    private ShopCommonDocumentService shopCommonDocumentService;
    @Resource
    private TwiterIdService twiterIdService;

    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest request,ShopCommonDocument shopCommonDocument,
                       @RequestParam(required = false, value = "div", defaultValue = "") String div,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo, Long[] ids) {
        Pageable pager = new Pageable(pageNo, 20);
        Map<String, Object> qyMap = new HashMap<>();
        shopCommonDocument.setNotIndocType(DocumentConsts.DOCUMENT_TYPE_HELP);
        pager.setParameter(shopCommonDocument);
        Page<ShopCommonDocument> results = shopCommonDocumentService.findByPage(pager);//结果集
        model.addAttribute("page", results);
        //model.addAttribute("docType",2);
        model.addAttribute("shopCommonDocument",shopCommonDocument);
        return "common/document/list";
    }

    @RequestMapping("/listHelp")
    public String listHelp(Model model, HttpServletRequest request,ShopCommonDocument shopCommonDocument,
                           @RequestParam(required = false, value = "div", defaultValue = "") String div,
                           @RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo, Long[] ids) {
        Pageable pager = new Pageable(pageNo, 10);
        Map<String, Object> qyMap = new HashMap<>();
        shopCommonDocument.setDocType(DocumentConsts.DOCUMENT_TYPE_HELP);
        pager.setParameter(shopCommonDocument);
        Page<ShopCommonDocument> results = shopCommonDocumentService.findByPage(pager);//结果集
        model.addAttribute("page", results);
        //model.addAttribute("docType",1);
        model.addAttribute("shopCommonDocument",shopCommonDocument);
        return "common/document/listHelp";
    }

    @RequestMapping("/forward")
    public String forward(Model model, @RequestParam Long id) {
        if (id != 0 && id != null) {
            model.addAttribute("document", shopCommonDocumentService.find(id));
            return "common/document/edit";
        } else {
            return "common/document/save";
        }
    }

    @RequestMapping("/edit")
    public String saveOrUpdate(@ModelAttribute ShopCommonDocument document, HttpServletRequest request, Model model) {
        RequestContext requestContext = new RequestContext(request);
        if ("help".equals(Optional.ofNullable(document.getDocType()).orElse(""))){
            model.addAttribute("referer", "listHelp.jhtml");
        }else{
            model.addAttribute("referer", "list.jhtml");
        }


        if (document.getId() == null || document.getId() == 0) {
            document.setCreateTime(new Date());
            document.setUpdateTime(new Date());
            String contents = StringEscapeUtils.unescapeHtml4(document.getDocContent());
            document.setDocContent(contents);
            document.setId(twiterIdService.getTwiterId());
            shopCommonDocumentService.save(document);
            model.addAttribute("msg", requestContext.getMessage("Delivery_settings_msg17"));
        } else {
            document.setUpdateTime(new Date());
            String contents = StringEscapeUtils.unescapeHtml4(document.getDocContent());
            document.setDocContent(contents);
            shopCommonDocumentService.update(document);
            model.addAttribute("msg", requestContext.getMessage("successful_modification"));
        }
        return Constants.MSG_URL;
    }

    ///**
    // * 删除文章
    // *
    // * @param
    // * @return
    // */
    //@RequestMapping("/delete")
    //public
    //@ResponseBody
    //Map<String, String> delete(@RequestParam Long ids[], HttpServletRequest request) {
    //    RequestContext requestContext = new RequestContext(request);
    //    Map<String, String> map = Maps.newHashMap();
    //    for (Long id : ids) {
    //        if (id == 0) {
    //            map.put("result", requestContext.getMessage("delete_error"));
    //            map.put("success", "false");
    //        } else {
    //            shopCommonDocumentService.delete(id);
    //            map.put("result", requestContext.getMessage("delete.success"));
    //            map.put("success", "true");
    //        }
    //    }
    //    return map;
    //}
    /**
     * 删除文章
     *
     * @param
     * @return
     */
    @RequestMapping("/delete")
    public
    String delete(@RequestParam Long ids[], HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        Map<String, String> map = Maps.newHashMap();
        for (Long id : ids) {
            if (id == 0) {
                map.put("result", requestContext.getMessage("delete_error"));
                map.put("success", "false");
            } else {
                shopCommonDocumentService.delete(id);
                map.put("result", requestContext.getMessage("delete.success"));
                map.put("success", "true");
            }
        }
        return "redirect:listHelp.jhtml";
    }

}
