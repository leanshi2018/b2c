package com.framework.loippi.controller.trade;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.dto.ShopWalletIncomeExcel;
import com.framework.loippi.dto.ShopWalletLogzhifuExcel;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.wallet.ShopWalletLogService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ParameterUtils;
import com.framework.loippi.utils.excel.ExportExcelUtils;
import com.framework.loippi.vo.user.ShopMemberPaymentTallyVo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 财务管理
 * Created by Administrator on 2017/9/29.
 */
@Controller("adminShopFinancialController")
@RequestMapping({"/admin/shop_financial"})
public class ShopFinancialSysController {

    @Resource
    private ShopMemberPaymentTallyService paymentTallyService;
    @Resource
    private ShopWalletLogService shopWalletLogService;


    /**
     * 收入
     */
    @RequestMapping(value = "/income")
    public String income(@RequestParam(defaultValue = "1") Integer pageNo, HttpServletRequest request,
        ShopMemberPaymentTallyVo param, ModelMap model) {
        String searchStartStr = request.getParameter("searchStartStr");
        String searchEndStr = request.getParameter("searchEndStr");
        try {
            if (StringUtils.isNotBlank(searchStartStr)) {
                param.setSearchStartTime(new SimpleDateFormat("yyyy-MM-dd").parse(searchStartStr));
                model.addAttribute("searchStartStr", searchStartStr);
            }
            if (StringUtils.isNotBlank(searchEndStr)) {
                param.setSearchEndTime(new SimpleDateFormat("yyyy-MM-dd").parse(searchEndStr));
                model.addAttribute("searchEndStr", searchEndStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page page = paymentTallyService.findByPageStoreIncome(pageable);
        model.addAttribute("page", page);
        model.addAttribute("param", param);
        //微信与支付宝
        BigDecimal wxAndZfb = paymentTallyService.countAmount();
//        BigDecimal bigDecimal = shopWalletCashService.countServiceAmount();
//        model.addAttribute("countAmount", wxAndZfb.add(bigDecimal));
        return "/trade/shop_financial/income";
    }

    /**
     * 支出
     */
    @RequestMapping(value = "/expenditure")
    public String expenditure(@RequestParam(defaultValue = "1") Integer pageNo, HttpServletRequest request,
        ModelMap model, ShopWalletLog param) {
        String searchStartStr = request.getParameter("searchStartStr");
        String searchEndStr = request.getParameter("searchEndStr");
        try {
            if (StringUtils.isNotBlank(searchStartStr)) {
                param.setSearchStartTime(new SimpleDateFormat("yyyy-MM-dd").parse(searchStartStr));
                model.addAttribute("searchStartStr", searchStartStr);
            }
            if (StringUtils.isNotBlank(searchEndStr)) {
                param.setSearchEndTime(new SimpleDateFormat("yyyy-MM-dd").parse(searchEndStr));
                model.addAttribute("searchEndStr", searchEndStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> paramList = new ArrayList<>();

        if (param.getLgType() != null && !"".equals(param.getLgType())) {
            paramList.add(param.getLgType());
        } else {
            paramList.add("order_return_refund");
            paramList.add("cash_pay");
            paramList.add("recommend_rebate");
            paramList.add("order_cancel");
        }
        param.setLgTypesParam(paramList);
        param.setIs_lg_add_rde(1);
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<ShopWalletLog> page = shopWalletLogService.findByPage(pageable);
        model.addAttribute("page", page);
        model.addAttribute("param", param);
        model.addAttribute("countAmount", shopWalletLogService.countAmount(param));
        return "/trade/shop_financial/expenditure";
    }

    /**
     * 备注--收入
     */
    @RequestMapping("/updateRemark")
    public String updateRemark(HttpServletRequest request, ShopMemberPaymentTally param, Model model) {
        model.addAttribute("referer", request.getHeader("referer"));
        if (param.getId() == null) {
            model.addAttribute("msg", "操作失败,ID为空");
            return Constants.MSG_URL;
        }
        if (param.getRemark().length() >= 20) {
            model.addAttribute("msg", "操作失败,不能超过20个字符");
            return Constants.MSG_URL;
        }
        paymentTallyService.update(param);
        model.addAttribute("msg", "操作成功");
        return Constants.MSG_URL;
    }

    /**
     * 备注--支出
     */
    @RequestMapping("/updateLgDesc")
    public String updateLgDesc(HttpServletRequest request, ShopWalletLog param, Model model) {
        model.addAttribute("referer", request.getHeader("referer"));
        if (param.getId() == null) {
            model.addAttribute("msg", "操作失败,ID为空");
            return Constants.MSG_URL;
        }
        if (param.getLgDesc().length() >= 20) {
            model.addAttribute("msg", "操作失败,不能超过20个字符");
            return Constants.MSG_URL;
        }
        shopWalletLogService.update(param);
        model.addAttribute("msg", "操作成功");
        return Constants.MSG_URL;
    }

    /**
     * 导出订单信息excel  收入
     */
    @RequestMapping("/incomeExcel")
    @RequiresPermissions("admin:export:Excel")
    public void incomeExcel(HttpServletRequest request, HttpServletResponse response, ShopMemberPaymentTallyVo param,
        ModelMap model, @RequestParam(defaultValue = "1") Integer pageNo) throws Exception {
        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
        // 订单状态
        // 订单类型
        List<ShopWalletIncomeExcel> orderExcelVoList = new ArrayList<>();
        String searchStartStr = request.getParameter("searchStartStr");
        String searchEndStr = request.getParameter("searchEndStr");
        try {
            if (StringUtils.isNotBlank(searchStartStr)) {
                param.setSearchStartTime(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(searchStartStr));
                model.addAttribute("searchStartStr", searchStartStr);
            }
            if (StringUtils.isNotBlank(searchEndStr)) {
                param.setSearchEndTime(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(searchEndStr));
                model.addAttribute("searchEndStr", searchEndStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page page = paymentTallyService.findByPageStore(pageable);
        List<ShopMemberPaymentTally> content = page.getContent();
        if (content != null && content.size() != 0) {
            for (ShopMemberPaymentTally vo : content) {
                ShopWalletIncomeExcel shopWalletLogzhifuExcel = new ShopWalletIncomeExcel();
                shopWalletLogzhifuExcel.setId(vo.getId().toString());
                shopWalletLogzhifuExcel.setPaymentSn(vo.getPaymentSn());
                shopWalletLogzhifuExcel.setBuyerName(vo.getBuyerName());
                shopWalletLogzhifuExcel.setPaymentAmount(vo.getPaymentAmount());
                shopWalletLogzhifuExcel.setPaymentCode(vo.getPaymentCode());
                shopWalletLogzhifuExcel.setRemark(vo.getRemark());
                shopWalletLogzhifuExcel.setCreateTime(vo.getCreateTime().toString());
                orderExcelVoList.add(shopWalletLogzhifuExcel);
            }
        }

        if (orderExcelVoList != null && orderExcelVoList.size() != 0) {
            // 定义文件的标头
            String[] headers = {"流水号", "订单号", "会员号", "实付金额", "支付方式", "支付账号", "支付时间", "备注"};
            Long times = System.currentTimeMillis();//定义时间戳
            HSSFWorkbook wb = ExportExcelUtils.exportCell(orderExcelVoList, null, headers, "");
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + times + ".xls");
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(response.getOutputStream());
                wb.write(bos);
                //用户操作日志
                //MqLogUtils.setMqMessage(request, null ,null ,"导出会员信息excel"  , MqLogUtils.SYS_LOG_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }
            return;
        }
    }

    /**
     * 导出订单信息excel  支出
     */
    @RequestMapping("/exportOrderExcel")
    @RequiresPermissions("admin:export:Excel")
    public void exportOrderExcelzhifu(HttpServletRequest request, HttpServletResponse response, ShopWalletLog param,
        @RequestParam(defaultValue = "1") Integer pageNo) throws Exception {
        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
        // 订单状态
        // 订单类型
        List<ShopWalletLogzhifuExcel> orderExcelVoList = new ArrayList<>();
        List<String> paramList = new ArrayList<>();
        paramList.add("order_return_refund");
        paramList.add("cash_pay");
        paramList.add("recommend_rebate");
        paramList.add("order_cancel");
        param.setLgTypesParam(paramList);
        param.setIs_lg_add_rde(1);
        Pageable pageable = new Pageable(pageNo, 20);
        pageable.setParameter(param);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<ShopWalletLog> page = shopWalletLogService.findByPage(pageable);
        List<ShopWalletLog> shopCreditRechargePage = page.getContent();
        if (shopCreditRechargePage != null && shopCreditRechargePage.size() != 0) {
            for (ShopWalletLog vo : shopCreditRechargePage) {
                ShopWalletLogzhifuExcel shopWalletLogzhifuExcel = new ShopWalletLogzhifuExcel();
                shopWalletLogzhifuExcel.setId(vo.getId().toString());
                shopWalletLogzhifuExcel.setLgMemberName(vo.getLgMemberName());
                if (vo.getLgType().equals("cash_pay")) {
                    shopWalletLogzhifuExcel.setLgType("提现");
                } else if (vo.getLgType().equals("order_return_refund")) {
                    shopWalletLogzhifuExcel.setLgType("退货退款");
                } else if (vo.getLgType().equals("recommend_rebate")) {
                    shopWalletLogzhifuExcel.setLgType("返佣");
                } else {
                    shopWalletLogzhifuExcel.setLgType("取消订单");
                }
                shopWalletLogzhifuExcel.setLgDesc(vo.getLgDesc());
                shopWalletLogzhifuExcel.setLgRdeAmount(vo.getLgRdeAmount());
                shopWalletLogzhifuExcel.setOrderSn(vo.getOrderSn() == null ? "" : vo.getOrderSn().toString());
                shopWalletLogzhifuExcel.setOutComeType("银行卡");
                shopWalletLogzhifuExcel.setLgMemberId(vo.getLgMemberId().toString());
                Date createTime = vo.getCreateTime();
                String createTimeStr = createTime.toString();
                shopWalletLogzhifuExcel.setCreateTime(createTimeStr);
                orderExcelVoList.add(shopWalletLogzhifuExcel);
            }
        }

        if (orderExcelVoList != null && orderExcelVoList.size() != 0) {
            // 定义文件的标头
            String[] headers = {"流水号", "会员号", "类型", "金额", "关联订单号", "到账方式", "到账账号", "支付时间", "备注"};
            Long times = System.currentTimeMillis();//定义时间戳
            HSSFWorkbook wb = ExportExcelUtils.exportCell(orderExcelVoList, null, headers, "");
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=" + times + ".xls");
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(response.getOutputStream());
                wb.write(bos);
                //用户操作日志
                //MqLogUtils.setMqMessage(request, null ,null ,"导出会员信息excel"  , MqLogUtils.SYS_LOG_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }
            return;
        }
    }
}
