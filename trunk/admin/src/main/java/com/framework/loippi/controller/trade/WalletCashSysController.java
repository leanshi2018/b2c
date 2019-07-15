//package com.framework.loippi.controller.trade;
//
//import com.framework.loippi.consts.Constants;
//import com.framework.loippi.controller.GenericController;
//
//import com.framework.loippi.dto.ShopWalletCashExcel;
//import com.framework.loippi.dto.ShopWalletLogExcel;
//import com.framework.loippi.dto.ShopWalletRechargeExcel;
//import com.framework.loippi.entity.User;
//
//import com.framework.loippi.entity.user.ShopMemberPaymentTally;
//import com.framework.loippi.entity.walet.ShopWalletCash;
//import com.framework.loippi.entity.walet.ShopWalletLog;
//import com.framework.loippi.entity.walet.ShopWalletRecharge;
//import com.framework.loippi.service.UserService;
//import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
//
//import com.framework.loippi.service.wallet.ShopWalletLogService;
//import com.framework.loippi.service.wallet.ShopWalletRechargeService;
//import com.framework.loippi.support.Page;
//import com.framework.loippi.support.Pageable;
//import com.framework.loippi.utils.ParameterUtils;
//import com.framework.loippi.utils.excel.ExportExcelUtils;
//import com.google.common.primitives.Ints;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedOutputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// * 现金流水相关
// * Created by longbh on 2017/9/8.
// */
//@Controller
//@Slf4j
//@RequestMapping({"/admin/shop_cash"})
//public class WalletCashSysController extends GenericController {
//
//    @Autowired
//    private ShopWalletCashService shopWalletCashService;
//    @Autowired
//    private ShopWalletLogService shopWalletLogService;
//    @Autowired
//    private ShopWalletRechargeService shopWalletRechargeService;
//    @Autowired
//    private ShopMemberPaymentTallyService shopMemberPaymentTallyService;
//    @Autowired
//    private ShopMemberService shopMemberService;
//    @Resource
//    private UserService userService;
//
//    /**
//     * 现金流水
//     */
//    @RequestMapping(value = "/listCashLog")
//    public String listCashLog(@RequestParam(defaultValue = "1") Integer pageNo, ModelMap model, HttpServletRequest request) {
//        Pageable pageable = new Pageable(pageNo, 20);
//        processQueryConditions(pageable, request);
//        Page<ShopWalletLog> shopCreditRechargePage = shopWalletLogService.findByPage(pageable);
//        model.addAttribute("page", shopCreditRechargePage);
//        model.addAttribute("paramter", pageable.getParameter());
//        return "/statisc/listCash";
//    }
//
//    /**
//     * x提现流水
//     *
//     * @param pageNo
//     * @param model
//     * @param request
//     * @param shopWalletCashParam
//     * @return
//     */
//    @RequestMapping(value = "/listWithDraw")
//    public String listWithDraw(@RequestParam(defaultValue = "1") Integer pageNo, ModelMap model, HttpServletRequest request, ShopWalletCash shopWalletCashParam) {
//        Pageable pageable = new Pageable(pageNo, 20);
//        String searchStartStr = request.getParameter("searchStartStr");
//        String searchEndStr = request.getParameter("searchEndStr");
//        try {
//            if (StringUtils.isNotBlank(searchStartStr)) {
//                shopWalletCashParam.setSearchStartTime(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(searchStartStr));
//                model.addAttribute("searchStartStr", searchStartStr);
//            }
//            if (StringUtils.isNotBlank(searchEndStr)) {
//                shopWalletCashParam.setSearchEndTime(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(searchEndStr));
//                model.addAttribute("searchEndStr", searchEndStr);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        pageable.setParameter(shopWalletCashParam);
//        Page<ShopWalletCash> shopWalletCashlist = shopWalletCashService.findByPage(pageable);
//        model.addAttribute("page", shopWalletCashlist);
//        model.addAttribute("paramter", pageable.getParameter());
//        return "/statisc/listWithDraw";
//    }
//
//    /**
//     * 标记提现申请处理状态----未完待续（冻结解冻没搞）
//     *
//     * @param id
//     * @param pdcPaymentState
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/updateWithDraw")
//    public String updateWithDraw(Long id, String pdcPaymentState, ModelMap model, HttpServletRequest request) {
//        User user = userService.getCurrent();
//        ShopWalletCash shopWalletCash = shopWalletCashService.find(id);
//        shopWalletCash.setPdcPaymentAdmin(user.getId());
//        shopWalletCash.setPdcPaymentTime(new Date());
//        ShopMember shopMember = shopMemberService.find(shopWalletCash.getPdcMemberId());
//        shopWalletCashService.releaseWallet(shopWalletCash, shopMember, pdcPaymentState);
//        return "redirect:listWithDraw.jhtml";
//    }
//
//    /**
//     * 备注--支出
//     */
//    @RequestMapping("/updateLgDesc")
//    public String updateLgDesc(HttpServletRequest request, ShopWalletCash param, Model model) {
//        model.addAttribute("referer", request.getHeader("referer"));
//        if (param.getId() == null) {
//            model.addAttribute("msg", "操作失败,ID为空");
//            return Constants.MSG_URL;
//        }
//        if (param.getLgDesc().length() >= 20) {
//            model.addAttribute("msg", "操作失败,不能超过20个字符");
//            return Constants.MSG_URL;
//        }
//        shopWalletCashService.update(param);
//        model.addAttribute("msg", "操作成功");
//        return Constants.MSG_URL;
//    }
//
//
//    /**
//     * 充值流水
//     *
//     * @param pageNo
//     * @param model
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/listRecharge")
//    public String listRecharge(@RequestParam(defaultValue = "1") Integer pageNo, ModelMap model, HttpServletRequest request) {
//        Pageable pageable = new Pageable(pageNo, 20);
//        processQueryConditions(pageable, request);
//        Map<String, Object> params = (Map<String, Object>) pageable.getParameter();
//        params.put("pdrPaymentState", "1");
//        Page<ShopWalletRecharge> shopWalletCash = shopWalletRechargeService.findByPage(pageable);
//        model.addAttribute("page", shopWalletCash);
//        model.addAttribute("paramter", pageable.getParameter());
//        return "/statisc/listRecharge";
//    }
//
//    /**
//     * 导出订单信息excel
//     */
//    @RequestMapping("/exportExcel")
//    @RequiresPermissions("admin:export:Excel")
//    public void exportOrderExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
//        List<ShopWalletCashExcel> orderExcelVoList = new ArrayList<>();
//        List<ShopWalletCash> shopWalletCash = shopWalletCashService.findList(paramter);
//        if (shopWalletCash != null && shopWalletCash.size() != 0) {
//            for (ShopWalletCash vo : shopWalletCash) {
//                ShopWalletCashExcel excel = new ShopWalletCashExcel(vo);
//                orderExcelVoList.add(excel);
//            }
//        }
//
//        if (orderExcelVoList != null && orderExcelVoList.size() != 0) {
//            // 定义文件的标头
//            String[] headers = {"流水号", "提现人用户名", "提现金额（元)", "账户姓名", "开户行支行", "银行卡号", "申请时间", "状态", "处理时间"};
//            Long times = System.currentTimeMillis();//定义时间戳
//            HSSFWorkbook wb = ExportExcelUtils.exportCell(orderExcelVoList, null, headers, "");
//            response.setContentType("application/x-msdownload");
//            response.setHeader("Content-disposition", "attachment; filename=" + times + ".xls");
//            BufferedOutputStream bos = null;
//            try {
//                bos = new BufferedOutputStream(response.getOutputStream());
//                wb.write(bos);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (bos != null) {
//                    bos.close();
//                }
//            }
//            return;
//        }
//    }
//
//    /**
//     * 导出订单信息excel
//     */
//    @RequestMapping("/exportRecharge")
//    @RequiresPermissions("admin:export:Excel")
//    public void exportRecharge(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
//        // 订单状态
//        // 订单类型
//        List<ShopWalletRechargeExcel> orderExcelVoList = new ArrayList<>();
//        List<ShopWalletRecharge> shopWalletCash = shopWalletRechargeService.findList(paramter);
//        if (shopWalletCash != null && shopWalletCash.size() != 0) {
//            for (ShopWalletRecharge vo : shopWalletCash) {
//                ShopWalletRechargeExcel excel = new ShopWalletRechargeExcel(vo);
//                orderExcelVoList.add(excel);
//            }
//        }
//
//        if (orderExcelVoList != null && orderExcelVoList.size() != 0) {
//            // 定义文件的标头
//            String[] headers = {"流水号", "充值账号", "充值金额（元）", "充值方式", "充值时间"};
//            Long times = System.currentTimeMillis();//定义时间戳
//            HSSFWorkbook wb = ExportExcelUtils.exportCell(orderExcelVoList, null, headers, "");
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
//        }
//    }
//
//    /**
//     * 导出订单信息excel
//     */
//    @RequestMapping("/exportCash")
//    @RequiresPermissions("admin:export:Excel")
//    public void exportCash(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
//        // 订单状态
//        // 订单类型
//        List<ShopWalletLogExcel> orderExcelVoList = new ArrayList<>();
//        paramter.put("paymentState", "1");
//        paramter.put("tradeTypes", Ints.asList(10, 20));
//        List<ShopMemberPaymentTally> shopCreditRechargePage = shopMemberPaymentTallyService.findList(paramter);
//        if (shopCreditRechargePage != null && shopCreditRechargePage.size() != 0) {
//            for (ShopMemberPaymentTally vo : shopCreditRechargePage) {
//                ShopWalletLogExcel excel = new ShopWalletLogExcel(vo);
//                orderExcelVoList.add(excel);
//            }
//        }
//
//        if (orderExcelVoList != null && orderExcelVoList.size() != 0) {
//            // 定义文件的标头
//            String[] headers = {"流水号", "订单号", "金额（元）", "类型", "支付类型", "支付名称", "发生时间"};
//            Long times = System.currentTimeMillis();//定义时间戳
//            HSSFWorkbook wb = ExportExcelUtils.exportCell(orderExcelVoList, null, headers, "");
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
//        }
//    }
//
//}
