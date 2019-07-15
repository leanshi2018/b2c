package com.framework.loippi.controller.common;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.service.common.ShopCommonDocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 静态页面
 * Created by longbh on 2017/8/6.
 */
@Controller
@RequestMapping("/views")
public class ViewController extends GenericController {

    @Resource
    private ShopCommonDocumentService shopCommonDocumentService;

    @RequestMapping("/forward/{id}")
    public String forward(Model model, @PathVariable Long id) {
        ShopCommonDocument shopCommonDocument = shopCommonDocumentService.find(id);
        model.addAttribute("article", shopCommonDocument);
        return "/wap/view";
    }

}
