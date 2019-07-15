//package com.framework.loippi.controller.member;
//
//import com.framework.loippi.consts.Constants;
//import com.framework.loippi.controller.GenericController;
//
//import com.framework.loippi.mybatis.paginator.domain.Order;
//import com.framework.loippi.service.order.ShopOrderService;
//import com.framework.loippi.service.trade.ShopRefundReturnService;
//import com.framework.loippi.service.user.*;
//import com.framework.loippi.support.Page;
//import com.framework.loippi.support.Pageable;
//import com.framework.loippi.utils.Digests;
//import com.framework.loippi.utils.JacksonUtil;
//import com.framework.loippi.utils.Paramap;
//import com.framework.loippi.utils.excel.ExportExcelUtils;
//import com.framework.loippi.utils.validator.DateUtils;
//import com.framework.loippi.vo.user.MemberExcelVo;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 用户管理
// */
//@Controller
//@RequestMapping("/member")
//@Slf4j
//public class ShopMemberController extends GenericController {
//
//    @Autowired
//    private ShopMemberService memberService;
//    @Autowired
//    private ShopMemberGradeService membergradeservice;
//    @Autowired
//    private ShopMemberAddressService shopMemberAddressService;
//    @Resource
//    private ShopOrderService orderService;
//    @Resource
//    private ShopRefundReturnService refundReturnService;
//
//    /**
//     * 导航至主操作页面
//     */
//    @RequestMapping("/add")
//    public String add() {
//        return "/member/addMember";
//    }
//
//    /**
//     * @param @param  model
//     * @param @param  div
//     * @param @param  pageNoStr
//     * @param @param  acctName
//     * @param @param  certifyClass
//     * @param @return 设定文件
//     * @return String    返回类型
//     * @throws
//     * @Title: list
//     * @Description: TODO (查询方法)
//     */
//    @RequiresPermissions("sys:member:view")
//    @RequestMapping(value = "/list")
//    public ModelAndView list(
//            Model model, @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
//            @ModelAttribute ShopMember member, HttpServletRequest request,
//            @RequestParam(required = false, value = "memberNameflag", defaultValue = "") String memberNameflag) {
//        Pageable pager = new Pageable();
//        /** 查询条件，放入实体中， **/
//        // 会员名查询
//        member.setMemberName(memberNameflag);
//        // 手机号码查询
//        member.setMemberMobile(memberNameflag);
//        ModelAndView modelAndView = new ModelAndView("/views/member/list");
//        String starttime = request.getParameter("starttime");
//        String endtime = request.getParameter("endtime");
//        String memberState = request.getParameter("memberState");
//        if (StringUtils.isNotBlank(starttime)) {
//            member.setStarttimeLong(DateUtils.strToLong(starttime, DateUtils.DEFAULT_FORMAT_YYYY_MM_DD));
//            model.addAttribute("starttime", starttime);
//        }
//        if (StringUtils.isNotBlank(endtime)) {
//            member.setEndtimeLong(DateUtils.strToLong(endtime, DateUtils.DEFAULT_FORMAT_YYYY_MM_DD));
//            model.addAttribute("endtime", endtime);
//        }
//        if (StringUtils.isNotBlank(memberState)) {
//            member.setMemberState(Integer.parseInt(memberState));
//        }
//        if (!StringUtils.isEmpty(pageNo)) {
//            pager.setPageNumber(Integer.parseInt(pageNo));
//        }
//
//        pager.setParameter(member);
////		int total = memberService.findMemberCount(member);// 获取总条数
//        Page<ShopMember> results = memberService.findMemberListIsLike(pager);// 结果集
//        // MqLogUtils.setMqMessage(request, null, null, "查询会员列表", MqLogUtils.SYS_LOG_TYPE);
//        for (ShopMember memberstr : results.getContent()) {
//            if (memberstr.getMemberOldLoginTime() != null && !"".equals(memberstr.getMemberOldLoginTime())) {
//                memberstr.setMemberOldLoginTimestr(DateUtils.getTimestampByLong(memberstr.getMemberOldLoginTime().getTime()));
//            }
//            if (memberstr.getMemberLoginTime() != null && !"".equals(memberstr.getMemberLoginTime())) {
//                memberstr.setMemberLoginTimestr(DateUtils.getTimestampByLong(memberstr.getMemberLoginTime().getTime()));
//            }
//        }
////		pager.setTotalRows(total);
//        model.addAttribute("page", results);
//        model.addAttribute("memberName", memberNameflag);
//        model.addAttribute("member", member);
//        // 转发请求到FTL页面
//        return modelAndView;
//    }
//
//    /**
//     * 编辑
//     */
//    @RequestMapping(value = "/form", method = RequestMethod.GET)
//    public String form(Long id, ModelMap model) {
//        ShopMember shopMember = new ShopMember();
//        if (id != null) {
//            shopMember = memberService.find(id);
//        }
//        model.addAttribute("member", shopMember);
//        return "/views/member/form";
//    }
//
//    /**
//     * 编辑或修改用户
//     *
//     * @param model
//     * @return
//     */
//    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
//    public String saveOrUpdate(
//            @ModelAttribute ShopMember member,
//            HttpServletRequest request,
//            Model model) {
//        if (member.getId() == null) {
//            if (StringUtils.isNotEmpty(member.getMemberPasswd())) {
//                member.setMemberPasswd(Digests.entryptPassword(member.getMemberPasswd()));//修改密码
//            }
//            member.setId(twiterIdService.getTwiterId());
//            memberService.save(member);
//            model.addAttribute("msg", "保存成功");
//        } else {
//            if (StringUtils.isNotEmpty(member.getMemberPasswd())) {
//                member.setMemberPasswd(Digests.entryptPassword(member.getMemberPasswd()));//修改密码
//            }
//            memberService.update(member);
//            model.addAttribute("msg", "修改成功");
//        }
//        model.addAttribute("referer", request.getContextPath() + "/member/list.jhtml");
//        return Constants.MSG_URL;
//    }
//
//    /**
//     * 查询单条记录
//     *
//     * @param @param  model
//     * @param @param  id
//     * @param @param  div
//     * @param @return 设定文件
//     * @return String    返回类型
//     * @throws
//     * @Title: findById
//     * @Description: TODO(这里用一句话描述这个方法的作用)
//     */
//    @RequestMapping(value = "/findById")
//    public String findById(
//            Model model, HttpServletRequest request,
//            @RequestParam(required = false, value = "id", defaultValue = "") String id,
//            @RequestParam(required = false, value = "div", defaultValue = "") String div,
//            @RequestParam(required = false, value = "type", defaultValue = "") String type) {
//        ShopMember member = memberService.find(Long.parseLong(id));
//        if ("view".equals(type)) {        // 查看
//            if (member.getMemberLoginTime() != null && !"".equals(member.getMemberLoginTime())) {
//                member.setMemberLoginTimestr(DateUtils.getTimestampByLong(member.getMemberLoginTime().getTime()));
//            }
//            if (member.getCreateTime() != null && !"".equals(member.getCreateTime())) {
//                member.setCreateTimeStr(DateUtils.getTimestampByLong(member.getCreateTime().getTime()));
//            }
//            if (member.getMemberBirthday() != null && !"".equals(member.getMemberBirthday())) {
//                member.setMemberBirthdaystr(DateUtils.getTimestampByLong(member.getMemberBirthday().getTime()));
//            }
//            // 收货地址
//            model.addAttribute("address", shopMemberAddressService.find(member.getId()));
//            // 实名认证
//            Map<String, Object> params = Maps.newHashMap();
//            params.put("memberId", member.getId());
//        }
//        Map<String, Object> params = Maps.newHashMap();
//        List<ShopMemberGrade> membergradelist = membergradeservice.findList(params);
//        model.addAttribute("membergradelist", membergradelist);
//        model.addAttribute("member", member);
//        model.addAttribute("div", div);
//        if ("view".equals(type)) return "/views/member/viewMember";
//        return "/views/member/editMember";
//    }
//
//    /**
//     * 批量启用或禁用会员
//     *
//     * @param ids
//     * @param memberState
//     * @param request
//     * @param model
//     * @return
//     */
//    @RequestMapping("updateMemberState")
//    public String updateMemberState(@RequestParam(value = "ids", defaultValue = "", required = false) String ids,
//                                    @RequestParam(value = "memberState", defaultValue = "", required = false) String memberState,
//                                    HttpServletRequest request, Model model) {
//        String type = "启用";
//        if ("0".equals(memberState)) type = "禁用";
//        String referer = request.getHeader("Referer");
//        model.addAttribute("referer", referer);
//        if (org.apache.commons.lang3.StringUtils.isBlank(ids)) {
//            model.addAttribute("result", "ID为空");
//            model.addAttribute("msg", "操作失败，ID为空");
//        } else if (org.apache.commons.lang3.StringUtils.isBlank(memberState)) {
//            model.addAttribute("result", "memberState为空");
//            model.addAttribute("msg", "操作失败，memberState为空");
//        } else if (!("1".equals(memberState) || "0".equals(memberState))) {
//            model.addAttribute("result", "memberState为空");
//            model.addAttribute("msg", "操作失败，memberState只能为1或者0");
//        } else {
//            String[] idArray = org.apache.commons.lang3.StringUtils.split(ids, ",");
//            List<ShopMember> shopMembers = new ArrayList<>();
//            for (String idStr : idArray) {
//                ShopMember shopMember = new ShopMember();
//                shopMember.setId(Long.valueOf(idStr));
//                shopMember.setMemberState(Integer.valueOf(memberState));
//                shopMembers.add(shopMember);
//            }
//            memberService.updateBatch(shopMembers);
//            model.addAttribute("msg", type + "成功");
//        }
//        return Constants.MSG_URL;
//    }
//
//    /**
//     * 批量分组
//     *
//     * @param memberIds
//     * @param groupId
//     * @param request
//     * @param model
//     * @return
//     */
//    @RequestMapping("updateGroup")
//    public String updateGroup(@RequestParam(value = "memberIds", defaultValue = "", required = false) String memberIds,
//                              @RequestParam(value = "groupId", required = false) Long groupId,
//                              HttpServletRequest request, Model model) {
//        if (StringUtils.isBlank(memberIds)) {
//            model.addAttribute("result", "会员ID为空");
//            model.addAttribute("msg", "操作失败，会员ID为空");
//        } else if (groupId == null) {
//            model.addAttribute("result", "分组ID为空");
//            model.addAttribute("msg", "操作失败，分组ID为空");
//        } else {
//            String[] idArray = StringUtils.split(memberIds, ",");
//            List<ShopMember> shopMembers = new ArrayList<>();
//            for (String idStr : idArray) {
//                ShopMember shopMember = new ShopMember();
//                shopMember.setId(Long.valueOf(idStr));
//                shopMember.setMemberGroupid(groupId);
//                shopMembers.add(shopMember);
//            }
//            memberService.updateBatch(shopMembers);
//            model.addAttribute("msg", "分组成功");
//        }
//
//        String referer = request.getHeader("Referer");
//        model.addAttribute("referer", referer);
//        return Constants.MSG_URL;
//    }
//
//    @RequiresPermissions("sys:member:view")
//    @RequestMapping(value = "/listDialog")
//    public ModelAndView listDialog(
//            Model model,
//            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
//            @ModelAttribute ShopMember member, HttpServletRequest request,
//            @RequestParam(required = false, value = "memberNameflag", defaultValue = "") String memberNameflag) {
//        Pageable pager = new Pageable();
//        /** 查询条件，放入实体中， **/
//        // 会员名查询
//        member.setMemberName(memberNameflag);
//        //member.setMemberRole("1");
//        // 手机号码查询
//        member.setMemberMobile(memberNameflag);
////        member.setMemberRole("1");
//        ModelAndView modelAndView = new ModelAndView("/views/member/listDialog");
//        String starttime = request.getParameter("starttime");
//        String endtime = request.getParameter("endtime");
//        String memberState = request.getParameter("memberState");
//        if (StringUtils.isNotBlank(starttime)) {
//            member.setStarttimeLong(DateUtils.strToLong(starttime, DateUtils.DEFAULT_FORMAT_YYYY_MM_DD));
//            model.addAttribute("starttime", starttime);
//        }
//        if (StringUtils.isNotBlank(endtime)) {
//            member.setEndtimeLong(DateUtils.strToLong(endtime, DateUtils.DEFAULT_FORMAT_YYYY_MM_DD));
//            model.addAttribute("endtime", endtime);
//        }
//        if (StringUtils.isNotBlank(memberState)) {
//            member.setMemberState(Integer.parseInt(memberState));
//        }
//
//        if (!StringUtils.isEmpty(pageNo)) {
//            pager.setPageNumber(Integer.parseInt(pageNo));
//        }
//        pager.setParameter(member);
////		int total = memberService.findMemberCount(member);// 获取总条数
//        Page<ShopMember> results = memberService.findMemberListIsLike(pager);// 结果集
//        // MqLogUtils.setMqMessage(request, null, null, "查询会员列表", MqLogUtils.SYS_LOG_TYPE);
//        for (ShopMember memberstr : results.getContent()) {
//            if (memberstr.getMemberOldLoginTime() != null && !"".equals(memberstr.getMemberOldLoginTime())) {
//                memberstr.setMemberOldLoginTimestr(DateUtils.getTimestampByLong(memberstr.getMemberOldLoginTime().getTime()));
//            }
//            if (memberstr.getMemberLoginTime() != null && !"".equals(memberstr.getMemberLoginTime())) {
//                memberstr.setMemberLoginTimestr(DateUtils.getTimestampByLong(memberstr.getMemberLoginTime().getTime()));
//            }
//        }
////		pager.setTotalRows(total);
//        model.addAttribute("page", results);
//        model.addAttribute("memberName", memberNameflag);
//        model.addAttribute("member", member);
//        // 转发请求到FTL页面
//        return modelAndView;
//    }
//
//    /**
//     * 校验表单
//     *
//     * @return
//     */
//    @RequiresPermissions("sys:member:view")
//    @RequestMapping("/validate")
//    public
//    @ResponseBody
//    String validateForm(@RequestParam(required = false, value = "memberName", defaultValue = "") String memberName,
//                        @RequestParam(required = false, value = "memberId", defaultValue = "") String memberId) {
//        if (StringUtils.isNotBlank(memberId)) {
//            ShopMember member = memberService.find(Long.parseLong(memberId));
//            if (member != null && memberName.equals(member.getMemberName())) {
//                return "true";
//            }
//        }
//        ShopMember member = memberService.find("memberName", memberName);
//        //校验重复
//        if (member != null) {
//            return "false";
//        } else {
//            return "true";
//        }
//    }
//
//    /**
//     * 导出Excel
//     *
//     * @param model
//     * @param pageNoStr
//     * @param member
//     * @param request
//     * @param response
//     * @param memberNameflag
//     * @return
//     */
//    @RequiresPermissions("sys:member:view")
//    @RequestMapping(value = "/exportMemberrExcel")
//    public void exportMemberrExcel(
//            Model model,
//            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
//            @ModelAttribute ShopMember member, HttpServletRequest request, HttpServletResponse response,
//            @RequestParam(required = false, value = "memberNameflag", defaultValue = "") String memberNameflag) throws Exception {
//
//        Pageable pager = new Pageable();
//        /** 查询条件，放入实体中， **/
//        member.setMemberName(memberNameflag);// 会员名查询
//        member.setMemberMobile(memberNameflag);// 手机号码查询
//        member.setMemberTruename(memberNameflag); // 真实姓名查询
//
//        String starttime = request.getParameter("starttime");
//        String endtime = request.getParameter("endtime");
//        String memberState = request.getParameter("memberState");
//        if (StringUtils.isNotBlank(starttime)) {
//            member.setStarttimeLong(DateUtils.strToLong(starttime, DateUtils.DEFAULT_FORMAT_YYYY_MM_DD));
//        }
//        if (StringUtils.isNotBlank(endtime)) {
//            member.setEndtimeLong(DateUtils.strToLong(endtime, DateUtils.DEFAULT_FORMAT_YYYY_MM_DD));
//        }
//        if (StringUtils.isNotBlank(memberState)) {
//            member.setMemberState(Integer.parseInt(memberState));
//        }
//
//        if (null != pageNoStr && !pageNoStr.equals("")) {
//            pageNoStr = pageNoStr.replace(",", "");
//            pager.setPageNumber(Integer.parseInt(pageNoStr));
//        }
//        pager.setParameter(member);// 实体加载在pager中
//
//        Page<ShopMember> page = memberService.findMemberListIsLike(pager);// 结果集
//        List<MemberExcelVo> memberExcelVos = Lists.newArrayList();
//        List<ShopMember> members = page.getContent();
//        if (members != null && members.size() > 0) {
//            int no = 1;
//            for (ShopMember m : members) {
//                MemberExcelVo vo = new MemberExcelVo();
//                vo.setNo(no++);
//                vo.setMemberName(m.getMemberName());
//                vo.setMobile(m.getMemberMobile());
//                vo.setTrueName(m.getMemberTruename());
//                vo.setGroupName(m.getMemberGroupName());
//                vo.setLoginNum(m.getMemberLoginNum());
//                vo.setHbBalance("0");
//                vo.setAccountBalance(m.getAvailablePredeposit() != null ? m.getAvailablePredeposit().toString() : "");
//                vo.setLastLoginTime(m.getMemberLoginTime() == null ? "" :
//                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").
//                                format(m.getMemberLoginTime()));
//                Integer third = m.getMemberThirdParty();
//                Integer idfy = m.getMemberIdentification();
//                if (third == null || idfy == null) {
//                    vo.setSourse("");
//                } else {
//                    vo.setSourse(third == 0 ? idfy == 0 ? "pc注册" : "app注册" : third == 1 ? "微信注册" : third == 2 ? "QQ注册" : third == 3 ? "新浪注册" : "");
//                }
//                vo.setAuthc("0".equals(m.getAuthcStatus()) ? "未认证" : "已认证");
//                memberExcelVos.add(vo);
//            }
//        }
//        if (memberExcelVos != null && memberExcelVos.size() > 0) {
//            // 定义文件的标头
//            String[] headers = {"编号", "会员名", "手机号码", "姓名", "分组名称", "登录次数", "红包余额", "账户余额", "最近登录时间", "来源", "认证"};
//            Long times = System.currentTimeMillis();//定义时间戳
//            HSSFWorkbook wb = ExportExcelUtils.exportCell(memberExcelVos, null, headers, "");
//            response.setContentType("application/x-msdownload");
//            response.setHeader("Content-disposition", "attachment; filename=" + times + ".xls");
//            BufferedOutputStream bos = null;
//            try {
//                bos = new BufferedOutputStream(response.getOutputStream());
//                wb.write(bos);
//                //用户操作日志
//                //MqLogUtils.setMqMessage(request, null ,null ,"导出会员信息excel"  , MqLogUtils.SYS_LOG_TYPE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (bos != null) {
//                    bos.close();
//                }
//            }
//            return;
//
//        } else {
//            request.getRequestDispatcher("/member/list").forward(request, response);
//        }
//    }
//
//    /**
//     * 删除会员
//     *
//     * @param ids
//     * @param model
//     * @return
//     */
//    @RequiresPermissions("sys:member:edit")
//    @RequestMapping(value = "/delMember2", method = RequestMethod.POST)
//    public String delMember2(@RequestParam(value = "ids") String ids,
//                             Model model, HttpServletRequest request) {
//        String referer = request.getHeader("Referer");
//        model.addAttribute("referer", referer);
//        if (org.apache.commons.lang3.StringUtils.isBlank(ids)) {
//            model.addAttribute("result", "ID为空");
//            model.addAttribute("msg", "删除失败，ID为空");
//        } else {
//            String[] idArray = org.apache.commons.lang3.StringUtils.split(ids, ",");
//            for (String idStr : idArray) {
//                memberService.delete(Long.parseLong(idStr));
//            }
//            model.addAttribute("msg", "删除成功");
//        }
//        return Constants.MSG_URL;
//    }
//
//    /**
//     * 订单列表查询
//     */
//    @RequestMapping(value = {"/orderlist"})
//    public String list(Pageable pageable, ModelMap model,
//                       @RequestParam(required = false, value = "id", defaultValue = "") String id) {
//        try {
//            ShopMember member = memberService.find(Long.parseLong(id));
//            model.addAttribute("member", member);
//            Map<String, Object> param = Paramap.create().put("buyerId", id);
//            pageable.setParameter(param);
//            pageable.setOrderProperty("create_time");
//            pageable.setOrderDirection(Order.Direction.DESC);
//            model.addAttribute("page", this.orderService.findByPage(pageable));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "/views/member/shop_order_list";
//    }
//
//    /**
//     * 售后订单列表查询
//     */
//    @RequestMapping(value = {"/refundReturn"}, method = {RequestMethod.GET})
//    public String list(Pageable pageable, ModelMap model, HttpServletRequest request,
//                       @RequestParam(required = false, value = "id", defaultValue = "") String id) {
//        processQueryConditions(pageable, request);
//        ((Map<String, Object>) pageable.getParameter()).put("buyerId", id);
//        ShopMember member = memberService.find(Long.parseLong(id));
//        model.addAttribute("member", member);
//        model.addAttribute("page", this.refundReturnService.findByPage(pageable));
//        return "/views/member/shop_refund_return_list";
//    }
//}