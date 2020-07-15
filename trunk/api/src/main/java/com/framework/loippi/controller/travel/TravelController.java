package com.framework.loippi.controller.travel;

import com.allinpay.yunst.sdk.YunClient;
import com.allinpay.yunst.sdk.bean.YunRequest;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.travel.RdTravelActivity;
import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.param.order.OrderSubmitParam;
import com.framework.loippi.param.travel.TravelMemSubmitParam;
import com.framework.loippi.result.app.coupon.CouponDetailListResult;
import com.framework.loippi.result.app.travel.TravelActivityResult;
import com.framework.loippi.result.app.travel.TravelTicketDetailListResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.travel.RdTravelActivityService;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import com.framework.loippi.service.travel.RdTravelTicketService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * 旅游活动控制层
 *
 * @author zc
 * @version 1.0
 */
@Controller("travelController")
@Slf4j
@ResponseBody
@RequestMapping("/api/travel")
public class TravelController extends BaseController {
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private RdTravelTicketDetailService rdTravelTicketDetailService;
    @Resource
    private RdTravelActivityService rdTravelActivityService;
    @Resource
    private RdTravelTicketService rdTravelTicketService;
    @Resource
    private TwiterIdService twiterIdService;

    /**
     *
     * @param param 旅游活动参团报名人信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/tuxedo.json", method = RequestMethod.POST)
    public String tuxedo(@Valid List< TravelMemSubmitParam> param, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error(Xerror.USER_UNLOGIN_JSON_CODE);
        }
        if(param==null||param.size()==0){
            return ApiUtils.error("请填入参团游客身份信息");
        }
        if(param.size()>2){
            return ApiUtils.error("一次只能填入最多2个参团人信息");
        }
        TravelMemSubmitParam submitParam = param.get(0);
        Long travelId = submitParam.getTravelId();
        Integer useTicketNum = submitParam.getUseTicketNum();
        //1.查询旅游活动状态，判断是否还可以报名
        RdTravelActivity rdTravelActivity = rdTravelActivityService.find(submitParam.getActivityId());
        if(rdTravelActivity==null){
            return ApiUtils.error("旅游活动不存在");
        }
        if(rdTravelActivity.getStartTime().getTime()>new Date().getTime()){//不在报名时间区域内
            return ApiUtils.error("旅游活动尚未达到开始报名时间");
        }
        if(rdTravelActivity.getEndTime().getTime()<new Date().getTime()){//不在报名时间区域内
            return ApiUtils.error("报名已截止，请关注下次旅游活动");
        }
        if(rdTravelActivity.getStatus()==null||rdTravelActivity.getStatus()!=1){
            return ApiUtils.error("当前旅游活动无法进行报名");
        }
        if(rdTravelActivity.getNumCeiling()!=null&&rdTravelActivity.getNumTuxedo()!=null&&(rdTravelActivity.getNumTuxedo()+param.size())>rdTravelActivity.getNumCeiling()){
            return ApiUtils.error("此团报名已满，请关注下次旅游活动");
        }
        //2.校验会员是否已经报名过该旅游活动
        List<RdTravelTicketDetail> useList = rdTravelTicketDetailService.findList(Paramap.create().put("travelId",travelId).put("status",1)
                .put("ownCode",member.getMmCode()).put("useActivityId",rdTravelActivity.getId()));
        if(useList!=null&&useList.size()>0){
            return ApiUtils.error("您已报名，无需重复报名");
        }
        //3.查找会员拥有的可以使用的旅游券
        List<RdTravelTicketDetail> list = rdTravelTicketDetailService.findList(Paramap.create().put("travelId",travelId).put("status",0)
        .put("ownCode",member.getMmCode()));
        if((list==null||list.size()==0)&&useTicketNum>0){
            return ApiUtils.error("您的旅游券信息有变化，请退出后重新提交");
        }
        if((list!=null&&list.size()>0)&&list.size()<useTicketNum){
            return ApiUtils.error("您的旅游券信息有变化，请退出后重新提交");
        }
        //3.扣除用户旅游券，修改旅游活动相关参数，存储报名参团人个人信息
        try {
            ArrayList<RdTravelMemInfo> memInfos = new ArrayList<>();
            for (TravelMemSubmitParam travelMemSubmitParam : param) {
                RdTravelMemInfo memInfo = new RdTravelMemInfo();
                memInfo.setId(twiterIdService.getTwiterId());
                memInfo.setMmCode(member.getMmCode());
                memInfo.setActivityId(rdTravelActivity.getId());
                memInfo.setName(travelMemSubmitParam.getName());
                memInfo.setIdCard(travelMemSubmitParam.getIdCard());
                memInfo.setMobile(travelMemSubmitParam.getMobile());
                memInfos.add(memInfo);
            }
            rdTravelActivityService.tuxedo(memInfos,useTicketNum,list,rdTravelActivity);
            return ApiUtils.success("旅游参团报名成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiUtils.error("旅游参团报名失败");
        }
    }

    /**
     * 个人旅游券列表
     * @param request
     * @param stateType 旅游券状态 0未使用 1：报名占用 2：已核销 3：已过期
     * @return
     */
    @RequestMapping(value = "/ticketList", method = RequestMethod.POST)
    public String ticketList(HttpServletRequest request, @RequestParam(required = true,value = "stateType") Integer stateType,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "20")Integer pageSize,
                             @RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum
    ) {
        if(stateType==null){
            return ApiUtils.error("请传入需要查询的旅游券状态类型");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录后进行旅游券查询操作");
        }
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNum);
        pageable.setOrderDirection(Order.Direction.DESC);
        pageable.setOrderProperty("own_time");
        if(stateType==9){
            pageable.setParameter(Paramap.create().put("ownCode",member.getMmCode()).put("elseState",9));
        }else {
            pageable.setParameter(Paramap.create().put("ownCode",member.getMmCode()).put("status",stateType));
        }
        Page<RdTravelTicketDetail> page = rdTravelTicketDetailService.findByPage(pageable);
        List<RdTravelTicketDetail> tickets = page.getContent();
        if(tickets!=null&&tickets.size()>0){
            HashMap<Long, RdTravelTicket> map = new HashMap<>();
            for (RdTravelTicketDetail ticketDetail : tickets) {
                RdTravelTicket rdTravelTicket = rdTravelTicketService.find(ticketDetail.getTravelId());
                map.put(ticketDetail.getTravelId(),rdTravelTicket);
            }
            return ApiUtils.success(TravelTicketDetailListResult.build(tickets,map));
        }else {
            return ApiUtils.success(new ArrayList<TravelTicketDetailListResult>());
        }
    }

    /**
     * 查看具体某一张旅游券的详情
     * @param request
     * @param ticketSn
     * @return
     */
    @RequestMapping(value = "/ticketDetailInfo", method = RequestMethod.POST)
    public String ticketDetailInfo(HttpServletRequest request,String ticketSn) {
        if(StringUtil.isEmpty(ticketSn)){
            return ApiUtils.error("请选择需要查看的旅游券编号");
        }
        RdTravelTicketDetail ticketDetail = rdTravelTicketDetailService.find("ticketSn",ticketSn);
        if(ticketDetail==null){
            return ApiUtils.error("旅游券信息异常");
        }
        Long travelId = ticketDetail.getTravelId();
        if(travelId==null){
            return ApiUtils.error("旅游券信息异常");
        }
        RdTravelTicket rdTravelTicket = rdTravelTicketService.find(travelId);
        if(rdTravelTicket==null){
            return ApiUtils.error("旅游券信息异常");
        }
        return ApiUtils.success(TravelTicketDetailListResult.build2(ticketDetail,rdTravelTicket));
    }

    /**
     * 查询所有处于报名时间内的旅游活动
     * @param request
     * @return
     */
    @RequestMapping(value = "/travelActivity/list", method = RequestMethod.POST)
    public String getTravelActivity(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录后进行操作");
        }
        List<RdTravelActivity> list = rdTravelActivityService.findList(Paramap.create().put("searchTime",new Date()));
        if(list!=null&&list.size()>0){
            HashMap<Long, Integer> map = new HashMap<>();
            for (RdTravelActivity rdTravelActivity : list) {
                List<RdTravelTicketDetail> details = rdTravelTicketDetailService.findList(Paramap.create().put("useActivityId", rdTravelActivity.getId())
                        .put("ownCode", member.getMmCode()));
                if(details!=null&&details.size()>0){
                    map.put(rdTravelActivity.getId(),1);
                }else {
                    map.put(rdTravelActivity.getId(),0);
                }
            }
            return ApiUtils.success(TravelActivityResult.build(list,map));
        }else {
            return ApiUtils.success(new ArrayList<TravelActivityResult>());
        }
    }

    /**
     * 点击报名，获取当前会员旅游券张数
     * @param request
     * @return
     */
    @RequestMapping(value = "/ticketNum", method = RequestMethod.POST)
    public String ticketNum(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录后进行操作");
        }
        List<RdTravelTicketDetail> list = rdTravelTicketDetailService.findList(Paramap.create().put("ownCode", member.getMmCode()).put("status", 0));
        HashMap<String, Object> map = new HashMap<>();
        if(list!=null&&list.size()>0){
            map.put("num",list.size());
        }else {
            map.put("num",0);
        }
        map.put("price",500);
        return ApiUtils.success(map);
    }
}
