package com.framework.loippi.controller.member;

import cn.jiguang.common.utils.StringUtils;
import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Page;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.validator.DateUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Pageable;

/**
 * Controller - 会员基础信息
 * 
 * @author zijing
 * @version 2.0
 */
@Controller("adminRdMmBasicInfoController")
@RequestMapping({ "/admin/rd_mm_basic_info" })
public class RdMmBasicInfoController extends GenericController {

	@Resource
	private RdMmBasicInfoService rdMmBasicInfoService;
	@Resource
	private RdRanksService rdRanksService;

	/**
	 * 跳转添加页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/add" }, method = { RequestMethod.GET })
	public String add(ModelMap model) {
		return "/admin/rd_mm_basic_info/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(RdMmBasicInfo rdMmBasicInfo, RedirectAttributes redirectAttributes) {
		rdMmBasicInfoService.save(rdMmBasicInfo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find(id);
		model.addAttribute("rdMmBasicInfo", rdMmBasicInfo);
		return "/admin/rd_mm_basic_info/edit";
	}
	
	
	/**
	 * 详情
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find(id);
		model.addAttribute("rdMmBasicInfo", rdMmBasicInfo);
		return "/admin/rd_mm_basic_info/view";
	}
	

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(RdMmBasicInfo rdMmBasicInfo, RedirectAttributes redirectAttributes) {
		rdMmBasicInfoService.update(rdMmBasicInfo);
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
	@RequestMapping(value = { "/list" }, method = { RequestMethod.GET })
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.rdMmBasicInfoService.findByPage(pageable));
		return "/admin/rd_mm_basic_info/list";
	}

	/**
	 * 删除操作
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = { "/delete" }, method = { RequestMethod.POST })
	public @ResponseBody Message delete(Long[] ids) {
		this.rdMmBasicInfoService.deleteAll(ids);
		return SUCCESS_MESSAGE;
	}

	    @RequestMapping(value = "/listDialog")
    public ModelAndView listDialog(
            Model model,
            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
            @RequestParam(required = false, value = "memberNameflag", defaultValue = "") String memberNameflag) {
        Pageable pager = new Pageable();
        ModelAndView modelAndView = new ModelAndView("/views/member/listDialog");
        if (!StringUtils.isEmpty(pageNo)) {
            pager.setPageNumber(Integer.parseInt(pageNo));
        }
        pager.setParameter(Paramap.create().put("info",memberNameflag));

        Page<RdMmBasicInfo> results = rdMmBasicInfoService.findByPage(pager);// 结果集

         //MqLogUtils.setMqMessage(request, null, null, "查询会员列表", MqLogUtils.SYS_LOG_TYPE);
        //for (RdMmBasicInfo memberstr : results.getContent()) {
        //    if (memberstr.getMemberOldLoginTime() != null && !"".equals(memberstr.getMemberOldLoginTime())) {
        //        memberstr.setMemberOldLoginTimestr(DateUtils.getTimestampByLong(memberstr.getMemberOldLoginTime().getTime()));
        //    }
        //    if (memberstr.getMemberLoginTime() != null && !"".equals(memberstr.getMemberLoginTime())) {
        //        memberstr.setMemberLoginTimestr(DateUtils.getTimestampByLong(memberstr.getMemberLoginTime().getTime()));
        //    }
        //}
//		pager.setTotalRows(total);
        model.addAttribute("page", results);
        model.addAttribute("memberName", memberNameflag);
        // 转发请求到FTL页面
        return modelAndView;
    }
}
