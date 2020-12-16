package com.framework.loippi.controller.travel;


import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.Principal;
import com.framework.loippi.entity.travel.RdTicketSendLog;
import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.entity.travel.RdTravelActivity;
import com.framework.loippi.entity.travel.RdTravelCost;
import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.travel.RdTravelActivityResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.travel.RdTicketSendLogService;
import com.framework.loippi.service.travel.RdTourismComplianceService;
import com.framework.loippi.service.travel.RdTravelActivityService;
import com.framework.loippi.service.travel.RdTravelCostService;
import com.framework.loippi.service.travel.RdTravelMemInfoService;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import com.framework.loippi.service.travel.RdTravelTicketService;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.DateConverter;
import com.framework.loippi.utils.StringUtil;

@Slf4j
@Controller
@RequestMapping("/admin/travel")
public class TravelController {

	@Resource
	private RdMmBasicInfoService rdMmBasicInfoService;
	@Resource
	private RdMmRelationService rdMmRelationService;
	@Resource
	private RdTourismComplianceService rdTourismComplianceService;
	@Resource
	private MemberQualificationService memberQualificationService;
	@Resource
	private TwiterIdService twiterIdService;
	@Resource
	private RdTravelTicketService rdTravelTicketService;
	@Resource
	private RdTravelTicketDetailService ticketDetailService;
	@Resource
	private RdTravelActivityService rdTravelActivityService;
	@Resource
	private RdTravelMemInfoService rdTravelMemInfoService;
	@Resource
	private RdTravelCostService rdTravelCostService;
	@Resource
	private RdTicketSendLogService rdTicketSendLogService;

	/**
	 * 周期计算达标送券
	 * @param request
	 * @param model
	 * @param periodCode
	 * @return
	 */
	@RequestMapping(value = "/compliance",method = RequestMethod.POST)
	public String compliance(HttpServletRequest request, ModelMap model,@RequestParam(required = true, value = "periodCode") String periodCode) {

		System.out.println("进来计算");

		if(periodCode==null || "".equals(periodCode)){
			model.addAttribute("msg", "请传入周期代码");
			return Constants.MSG_URL;
		}
		Integer sign = 0;
		String periods = "202007,202008,202009,202010,202011,202012";
		//String periods = "202004,202005,202006,202007,202011,202012";
		String[] periodList = periods.split(",");
		for (String period : periodList) {
			if (period.equals(periodCode)){
				sign = 1;
			}
		}

		if (sign==1){
			//找出当前周期最高级别大于等于4（一级代理店）的数据
			List<MemberQualification> qualificationList = memberQualificationService.findByHighRank4(periodCode);
			for (MemberQualification qualification : qualificationList) {
				Integer oneQualify = 0;//1
				Integer twoQualify = 0;//2
				Integer threeQualify = 0;//3
				Integer keepQualify = 0;//保留资格

				//计算'202006','202007','202008','202009','202010','202011','202012'内rd_dis_qualification里DD_NEW_VIP_NUMBER新推荐vip人数
				Integer vipNum = memberQualificationService.findVipNumByMCode(qualification.getMCode());

				//6月1号到12月31号从老会员转过来的会员任意一个月买满25mi算一个合格推荐
				Integer oldNum = 0;

				DateConverter dateConverter = new DateConverter();
				Date startTime = dateConverter.convert("2020-06-01 00:00:00");
				Date endTime = dateConverter.convert("2020-12-31 23:59:59");
				Calendar begin = Calendar.getInstance();
				begin.setTime(startTime);

				Calendar end = Calendar.getInstance();
				end.setTime(endTime);

				List<RdMmRelation> rdMmRelationList = rdMmRelationService.findBySponsorCode(qualification.getMCode());
				if (rdMmRelationList.size()>0){
					for (RdMmRelation rdMmRelation : rdMmRelationList) {
						if (rdMmRelation.getNOFlag()==2){
							RdMmBasicInfo byMCode = rdMmBasicInfoService.findByMCode(rdMmRelation.getMmCode());
							Calendar creationTime = Calendar.getInstance();
							creationTime.setTime(byMCode.getCreationDate());
							if (creationTime.after(begin) && creationTime.before(end)) {
								//在区间注册
								//'202006','202007','202008','202009','202010','202011','202012'中任何个周期买足25mi
								Integer countMi = memberQualificationService.countByMCode(rdMmRelation.getMmCode());
								if (countMi>0){
									oldNum = oldNum+1;
								}
							}
						}
					}
				}

				vipNum += oldNum;

				RdTourismCompliance rdTourismCompliance = rdTourismComplianceService.findByMmCode(qualification.getMCode());
				if (rdTourismCompliance==null){
					//未生成过记录
					if (qualification.getRankP0()>=4 && qualification.getRankP1()>=4){//当前周期和上一周期都是一级代理店及以上
						if (qualification.getRankP0()>=5 && qualification.getRankP1()>=5){//两个月达到二级代理店及以上
							if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6){//两个月达到三级代理店及以上
								//达到3级
								if (vipNum>=6){
									threeQualify = 1;
									twoQualify = 1;
									oneQualify = 1;
								}else if (vipNum>=4&&vipNum<6){
									twoQualify = 1;
									oneQualify = 1;
									keepQualify = 3;
								}else if (vipNum>=2&&vipNum<4){
									twoQualify = 1;
									keepQualify = 3;
								}else {
									keepQualify = 3;
								}
							}else {
								//达到2级
								if (vipNum>=4){
									twoQualify = 1;
									oneQualify = 1;
								}else if (vipNum>=2&&vipNum<4){
									oneQualify = 1;
									keepQualify = 2;
								}else {
									keepQualify = 2;
								}
							}
						}else {
							//达到1级
							if (vipNum>=2){
								oneQualify = 1;
							}else {
								keepQualify = 1;
							}
						}
					}
					RdTourismCompliance rdTourismComplianceSave = new RdTourismCompliance();
					rdTourismComplianceSave.setId(twiterIdService.getTwiterId());
					rdTourismComplianceSave.setMmCode(qualification.getMCode());
					rdTourismComplianceSave.setOneQualify(oneQualify);
					rdTourismComplianceSave.setTwoQualify(twoQualify);
					rdTourismComplianceSave.setThreeQualify(threeQualify);
					rdTourismComplianceSave.setKeepQualify(keepQualify);

					rdTourismComplianceService.save(rdTourismComplianceSave);
				}else{
					//已生成记录
					if (rdTourismCompliance.getThreeQualify()==0){//三级未达标
						if (rdTourismCompliance.getTwoQualify()==0){//未达二级
							if (rdTourismCompliance.getOneQualify()==0){//未达一级
								if (rdTourismCompliance.getKeepQualify()==1){
									//有保留1级
									if (qualification.getRankP0()>=5 && qualification.getRankP1()>=5) {//两个月达到二级代理店及以上
										if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
											if (vipNum>=6){
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = 1;
												keepQualify = 0;
											}else if (vipNum>=4&&vipNum<6){
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = 0;
												keepQualify =3;
											}else if (vipNum>=2&&vipNum<4){
												oneQualify = 1;
												twoQualify = 0;
												threeQualify = 0;
												keepQualify =3;
											}else {
												oneQualify = 0;
												twoQualify = 0;
												threeQualify = 0;
												keepQualify =3;
											}
										}else {
											threeQualify = 0;
											if (vipNum>=4){
												oneQualify = 1;
												twoQualify = 1;
												keepQualify = 0;
											}else if (vipNum>=2&&vipNum<4){
												oneQualify = 1;
												twoQualify = 0;
												keepQualify = 2;
											}else {
												oneQualify = 0;
												twoQualify = 0;
												keepQualify = 2;
											}
										}
									}else {
										if (vipNum>=2){
											oneQualify = 1;
											twoQualify = 0;
											threeQualify = 0;
											keepQualify =0;
										}else {
											oneQualify = rdTourismCompliance.getOneQualify();
											twoQualify = rdTourismCompliance.getTwoQualify();
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = rdTourismCompliance.getKeepQualify();
										}
									}
								}else {
									//未有保留1级
									if (rdTourismCompliance.getKeepQualify()==2){
										//有保留2级
										if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
											if (vipNum>=6){
												//都达成
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = 1;
												keepQualify =0;
											}else if (vipNum>=4&&vipNum<6){
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = 0;
												keepQualify =0;
											}else if (vipNum>=2&&vipNum<4){
												oneQualify = 1;
												twoQualify = rdTourismCompliance.getTwoQualify();
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify =rdTourismCompliance.getKeepQualify();
											}else {
												oneQualify = rdTourismCompliance.getOneQualify();
												twoQualify = rdTourismCompliance.getTwoQualify();
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify = rdTourismCompliance.getKeepQualify();
											}
										}else {
											if (vipNum>=4){
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = 0;
												keepQualify =0;
											}else if (vipNum>=2&&vipNum<4){
												oneQualify = 1;
												twoQualify = rdTourismCompliance.getTwoQualify();
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify =rdTourismCompliance.getKeepQualify();
											}else {
												oneQualify = rdTourismCompliance.getOneQualify();
												twoQualify = rdTourismCompliance.getTwoQualify();
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify = rdTourismCompliance.getKeepQualify();
											}
										}
									}else {
										//未有保留2级
										if (rdTourismCompliance.getKeepQualify()==3){
											//有保留3级
											if (vipNum>=6){
												//都达成
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = 1;
												keepQualify =0;
											}else if (vipNum>=4&&vipNum<6){
												oneQualify = 1;
												twoQualify = 1;
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify = rdTourismCompliance.getKeepQualify();
											}else if (vipNum>=2&&vipNum<4){
												oneQualify = 1;
												twoQualify = rdTourismCompliance.getTwoQualify();
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify =rdTourismCompliance.getKeepQualify();
											}else {
												oneQualify = rdTourismCompliance.getOneQualify();
												twoQualify = rdTourismCompliance.getTwoQualify();
												threeQualify = rdTourismCompliance.getThreeQualify();
												keepQualify = rdTourismCompliance.getKeepQualify();
											}
										}else {
											// 未有保留1级 未有保留2级 未有保留3级 未达标三级 未达标二级 未达标一级
											if (qualification.getRankP0()>=4 && qualification.getRankP1()>=4){//当前周期和上一周期都是一级代理店及以上
												if (qualification.getRankP0()>=5 && qualification.getRankP1()>=5){//两个月达到二级代理店及以上
													if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6){//两个月达到三级代理店及以上
														//达到3级
														if (vipNum>=6){
															oneQualify = 1;
															twoQualify = 1;
															threeQualify = 1;
															keepQualify = 0;
														}else if (vipNum>=4&&vipNum<6){
															oneQualify = 1;
															twoQualify = 1;
															threeQualify = 0;
															keepQualify = 3;
														}else if (vipNum>=2&&vipNum<4){
															oneQualify = 1;
															twoQualify = 0;
															threeQualify = 0;
															keepQualify = 3;
														}else {
															oneQualify = 0;
															twoQualify = 0;
															threeQualify = 0;
															keepQualify = 3;
														}
													}else {
														//达到2级
														if (vipNum>=4){
															oneQualify = 1;
															twoQualify = 1;
															threeQualify = 0;
															keepQualify = 0;
														}else if (vipNum>=2&&vipNum<4){
															oneQualify = 1;
															twoQualify = 0;
															threeQualify = 0;
															keepQualify = 2;
														}else {
															oneQualify = 0;
															twoQualify = 0;
															threeQualify = 0;
															keepQualify = 2;
														}
													}
												}else {
													//达到1级
													if (vipNum>=2){
														oneQualify = 1;
														twoQualify = 0;
														threeQualify = 0;
														keepQualify = 0;
													}else {
														oneQualify = 0;
														twoQualify = 0;
														threeQualify = 0;
														keepQualify = 1;
													}
												}
											}
										}
									}
								}
							}else {
								//已达到一级
								oneQualify = rdTourismCompliance.getOneQualify();//1
								if (rdTourismCompliance.getKeepQualify()==2){
									//有保留2级
									if (vipNum>=6){
										twoQualify = 1;
										if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
											threeQualify = 1;
											keepQualify = 0;
										}else {
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = 0;
										}
									}else if (vipNum>=4&&vipNum<6){
										twoQualify = 1;
										if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = 3;
										}else {
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = 0;
										}
									}else {
										twoQualify =0;
										if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = 3;
										}else {
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = rdTourismCompliance.getKeepQualify();
										}
									}
								}else {
									//未有保留2级
									if (rdTourismCompliance.getKeepQualify()==3){
										//未有保留2级 保留3级
										if (vipNum>=6){
											twoQualify = 1;
											threeQualify = 1;
											keepQualify = 0;
										}else if (vipNum>=4&&vipNum<6){
											twoQualify = 1;
											threeQualify = 0;
											keepQualify =rdTourismCompliance.getKeepQualify();
										}else {
											twoQualify = 0;
											threeQualify = 0;
											keepQualify =rdTourismCompliance.getKeepQualify();
										}
									}else {
										//未有保留2级 未有保留3级
										if (qualification.getRankP0()>=5 && qualification.getRankP1()>=5) {//两个月达到二级代理店及以上
											if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
												if (vipNum>=6){
													twoQualify = 1;
													threeQualify = 1;
													keepQualify = 0;
												}else if (vipNum>=4&&vipNum<6){
													twoQualify = 1;
													threeQualify = 0;
													keepQualify =3;
												}else {
													twoQualify = 0;
													threeQualify = 0;
													keepQualify =3;
												}
											}else {
												threeQualify = 0;
												if (vipNum>=4){
													twoQualify = 1;
													keepQualify = 0;
												}else {
													twoQualify = 0;
													keepQualify = 2;
												}
											}
										}else {
											twoQualify = rdTourismCompliance.getTwoQualify();
											threeQualify = rdTourismCompliance.getThreeQualify();
											keepQualify = rdTourismCompliance.getKeepQualify();
										}
									}
								}
							}
						}else {
							//已达到二级
							oneQualify = rdTourismCompliance.getOneQualify();//1
							twoQualify = rdTourismCompliance.getTwoQualify();//2
							if (rdTourismCompliance.getKeepQualify()==3){
								//有保留三级
								if (vipNum>=6){//合格
									threeQualify = 1;
									keepQualify = 0;
								}else {
									threeQualify = rdTourismCompliance.getThreeQualify();//3
									keepQualify = rdTourismCompliance.getKeepQualify();//保留资格
								}
							}else {
								//未有保留3级
								if (qualification.getRankP0()>=6 && qualification.getRankP1()>=6) {//两个月达到三级代理店及以上
									if (vipNum>=6){
										//这个月合格
										threeQualify = 1;//3
										keepQualify = 0;
									}else {
										threeQualify = 0;//3
										keepQualify = 3;
									}
								}else {
									threeQualify = rdTourismCompliance.getThreeQualify();//3
									keepQualify = rdTourismCompliance.getKeepQualify();//保留资格
								}
							}
						}

					}else {
						//全部达到过了
						oneQualify = rdTourismCompliance.getOneQualify();//1
						twoQualify = rdTourismCompliance.getTwoQualify();//2
						threeQualify = rdTourismCompliance.getThreeQualify();//3
						keepQualify = rdTourismCompliance.getKeepQualify();//保留资格
					}

					rdTourismCompliance.setOneQualify(oneQualify);
					rdTourismCompliance.setTwoQualify(twoQualify);
					rdTourismCompliance.setThreeQualify(threeQualify);
					rdTourismCompliance.setKeepQualify(keepQualify);
					rdTourismComplianceService.update(rdTourismCompliance);
				}
			}
			//model.addAttribute("referer", "");
			model.addAttribute("msg", "计算成功");
			System.out.println("跑完");
			return Constants.MSG_URL;
		}else {
			model.addAttribute("msg", "传入的周期代码不在活动范围");
			return Constants.MSG_URL;
		}
	}


	/**
	 * 发放旅游券
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/grantTicket",method = RequestMethod.POST)
	public String grantTicket(HttpServletRequest request, ModelMap model,@RequestParam(required = true, value = "ticketId") Long ticketId) {
		System.out.println("进来发放");
		if (ticketId==null){
			model.addAttribute("msg", "请选择需要发放的券");
			return Constants.MSG_URL;
		}

		RdTravelTicket rdTravelTicket = rdTravelTicketService.find(ticketId);
		if (rdTravelTicket==null){
			model.addAttribute("msg", "未找到该券");
			return Constants.MSG_URL;
		}

		rdTourismComplianceService.grantTicket(rdTravelTicket);
		System.out.println("跑完");
		model.addAttribute("msg", "发券成功");
		return Constants.MSG_URL;
	}

	/**
	 * 旅游券列表
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/findTicketAll",method = RequestMethod.GET)
	public String ticketALl(HttpServletRequest request, ModelMap model) {


		List<RdTravelTicket> ticketServiceAll = rdTravelTicketService.findAll();
		if (ticketServiceAll.size()>0){
			model.addAttribute("travelTicketList", ticketServiceAll);
			return "/common/travelTicket/index/select";
		}else {
			model.addAttribute("msg", "没有旅游券可选择");
			return "/common/travelTicket/index/select";
		}

	}

	/**
	 * 新增或者编辑按钮
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value ="/forward",method = RequestMethod.GET)
	public String forward(Model model, @RequestParam(value = "id",required = false) Long id) {
		if (id != null && id != 0) {
			RdTravelTicket rdTravelTicket = rdTravelTicketService.find(id);
			model.addAttribute("rdTravelTicket",rdTravelTicket);
			return "/common/travelTicket/index/edit";//跳往新增或编辑页面
		} else {
			model.addAttribute("rdTravelTicket", null);
			return "/common/travelTicket/index/edit";
		}
	}

	/**
	 * 保存或者编辑修改旅游券信息
	 * @param request
	 * @param model
	 * @param travelTicket 旅游券信息
	 * @return
	 */
	@RequestMapping(value = "/addOrUpdate",method = RequestMethod.POST)
	public String addOrUpdate(HttpServletRequest request, ModelMap model,@ModelAttribute RdTravelTicket travelTicket) {
		System.out.println("进入旅游券添加");
		if(StringUtil.isEmpty(travelTicket.getTravelName())){
			model.addAttribute("msg", "旅游券名称不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelTicket.getUseStartTimeStr())){
			model.addAttribute("msg", "旅游券使用开始时间为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelTicket.getUseEndTimeStr())){
			model.addAttribute("msg", "旅游券使用结束时间为空");
			return Constants.MSG_URL;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start=null;
		Date end=null;
		try {
			start = format.parse(travelTicket.getUseStartTimeStr() + " 00:00:00");
			end = format.parse(travelTicket.getUseEndTimeStr() + " 23:59:59");
			travelTicket.setUseStartTime(start);
			travelTicket.setUseEndTime(end);
		} catch (ParseException e) {
			e.printStackTrace();
			model.addAttribute("msg", "传入时间格式错误");
			return Constants.MSG_URL;
		}
		if(travelTicket.getTicketPrice()==null){
			model.addAttribute("msg", "旅游券面值为空");
			return Constants.MSG_URL;
		}
		if(travelTicket.getImage()==null){
			model.addAttribute("msg", "旅游券图片为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelTicket.getRemark())){
			model.addAttribute("msg", "旅游券使用说明为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelTicket.getRemark())){
			model.addAttribute("msg", "旅游券使用说明为空");
			return Constants.MSG_URL;
		}
		travelTicket.setIssueNum(0L);
		Subject subject = SecurityUtils.getSubject();
		if(subject!=null){
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null && principal.getId() != null) {
				Long id = principal.getId();
				String username = principal.getUsername();
				Map<String, String> map =rdTravelTicketService.saveOrEditCoupon(travelTicket,id,username);
				if (map == null || StringUtil.isEmpty(map.get("code"))) {
					model.addAttribute("msg", "保存旅游券失败！");
					return Constants.MSG_URL;
				}

				String code = map.get("code");
				if (StringUtil.isEmpty(code) || code.equals("0")) {
					String errorMsg = map.get("msg");
					model.addAttribute("msg", errorMsg);
					return Constants.MSG_URL;
				}
				//model.addAttribute("msg", "成功");
				return "redirect:travelTicket/list.jhtml";//TODO
			}
		}
		model.addAttribute("msg", "请登录后再进行旅游券相关操作");
		return Constants.MSG_URL;
	}

	/**
	 * 旅游券基本信息列表
	 *
	 */
	@RequestMapping("/travelTicket/list")
	public String list(ModelMap model,
					   @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
					   @RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize,
					   @ModelAttribute RdTravelTicket travelTicket) {
		//参数整理
		Pageable pager = new Pageable();
		pager.setPageNumber(pageNo);
		pager.setPageSize(pageSize);
		pager.setOrderProperty("create_time");
		pager.setOrderDirection(Order.Direction.DESC);
		pager.setParameter(travelTicket);
		Page<RdTravelTicket> page = rdTravelTicketService.findByPage(pager);
		model.addAttribute("page", page);
		return "/common/travelTicket/index/list";//TODO
	}

	/**
	 * 旅游券详情基本信息列表
	 *
	 */
	@RequestMapping("/travelTicketDetail/list")
	public String detailList(ModelMap model, Pageable pager,
					   @ModelAttribute RdTravelTicketDetail detail) {
		//参数整理
		/*Pageable pager = new Pageable();
		pager.setPageNumber(pageNo);
		pager.setPageSize(pageSize);*/
		pager.setOrderProperty("own_time");
		pager.setOrderDirection(Order.Direction.DESC);
		pager.setParameter(detail);
		Page<RdTravelTicketDetail> page = ticketDetailService.findByPage(pager);
		model.addAttribute("page", page);
		return "/common/travelTicket/record/list";//TODO
	}

	/**
	 * 核销或者恢复单张旅游券
	 * @param model
	 * @param ticketSn
	 * @param species 1:恢复 2：核销
	 * @return
	 */
	@RequestMapping("/travelTicketDetail/restoreOrDestroy")
	public String restoreOrDestroy(ModelMap model,@RequestParam(required = true, value = "ticketSn") String ticketSn,
								   @RequestParam(required = true, value = "status") Integer status,
								   @RequestParam(required = true, value = "species") Integer species,Long activityId) {
		if(status!=null&&status==0&&species==2&&activityId==null){
			model.addAttribute("msg", "请选择旅游活动进行旅游券核销");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(ticketSn)){
			model.addAttribute("msg", "请选择需要操作的旅游券");
			return Constants.MSG_URL;
		}
		if(species==null||(species!=1&&species!=2)){
			model.addAttribute("msg", "请选择正确的处理方式");
			return Constants.MSG_URL;
		}
		RdTravelTicketDetail ticketDetail = ticketDetailService.find("ticketSn",ticketSn);
		if(ticketDetail==null){
			model.addAttribute("msg", "需要操作的旅游券不存在");
			return Constants.MSG_URL;
		}
		if(ticketDetail.getStatus()==null){
			model.addAttribute("msg", "旅游券状态异常");
			return Constants.MSG_URL;
		}
		Subject subject = SecurityUtils.getSubject();
		if(subject!=null){
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null && principal.getId() != null) {
				Long id = principal.getId();
				String username = principal.getUsername();
				//对旅游券进行核销或者恢复操作
				try {
					ticketDetailService.restoreOrDestroy(ticketDetail,species,id,username,activityId);
					return "redirect:travelTicketDetail/list.jhtml";//TODO
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("msg", e.getMessage());
					return Constants.MSG_URL;
				}
			}
		}
		model.addAttribute("msg", "请登录后再进行旅游券相关操作");
		return Constants.MSG_URL;
	}

	/**
	 * 根据旅游活动id查询
	 * @param model
	 * @param activityId
	 * @return
	 */
	@RequestMapping("/travelTicketActivity/findById")
	public String findActivityById(ModelMap model, @RequestParam(required = true, value = "activityId") Long activityId) {
		if(activityId==null){
			model.addAttribute("msg", "请填入旅游活动id");
			return Constants.MSG_URL;
		}
		RdTravelActivity rdTravelActivity = rdTravelActivityService.find(activityId);
		if(rdTravelActivity==null){
			model.addAttribute("msg", "旅游活动信息异常");
			return Constants.MSG_URL;
		}
		model.addAttribute("rdTravelActivity", rdTravelActivity);
		return "";
	}

	/**
	 * 旅游活动列表
	 *
	 */
	@RequestMapping("/travelActivity/list")
	public String activityList(ModelMap model,
							 @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
							 @RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize,
							 @ModelAttribute RdTravelActivity activity) {
		//参数整理
		Pageable pager = new Pageable();
		pager.setPageNumber(pageNo);
		pager.setPageSize(pageSize);
		pager.setOrderProperty("create_time");
		pager.setOrderDirection(Order.Direction.DESC);
		pager.setParameter(activity);
		Page<RdTravelActivity> page = rdTravelActivityService.findByPage(pager);
		model.addAttribute("page", page);
		return "/common/travelTicket/activity/list";//TODO
	}

	/**
	 * 旅游活动列表(供弹窗核销旅游券使用)
	 *
	 */
	@RequestMapping("/travelActivity/list2")
	public String activityList2(ModelMap model,
							   @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
							   @RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize,
							   @ModelAttribute RdTravelActivity activity) {
		//参数整理
		Pageable pager = new Pageable();
		pager.setPageNumber(pageNo);
		pager.setPageSize(pageSize);
		pager.setOrderProperty("create_time");
		pager.setOrderDirection(Order.Direction.DESC);
		pager.setParameter(activity);
		Page<RdTravelActivity> page = rdTravelActivityService.findByPage(pager);
		model.addAttribute("page", page);
		return "/common/travelTicket/record/select";
	}

	/**
	 * 旅游活动新增或者编辑按钮
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value ="/activity/forward",method = RequestMethod.GET)
	public String activityForward(Model model, @RequestParam(value = "id",required = false) Long id) {
		if (id != null && id != 0) {
			RdTravelActivity travelActivity = rdTravelActivityService.find(id);
			RdTravelActivityResult result = new RdTravelActivityResult();
			BeanUtils.copyProperties(travelActivity,result);
			if(travelActivity.getImage()!=null){
				String[] strings = travelActivity.getImage().split(",");
				List<String> list = Arrays.asList(strings);
				result.setImageList(list);
			}
			model.addAttribute("travelActivity",result);
			return "/common/travelTicket/activity/edit";//跳往新增或编辑页面
		} else {
			model.addAttribute("travelActivity", null);
			return "/common/travelTicket/activity/edit";
		}
	}

	/**
	 * 旅游活动保存或者编辑修改旅游券信息
	 * @param request
	 * @param model
	 * @param travelActivity 旅游活动信息
	 * @return
	 */
	@RequestMapping(value = "/activity/addOrUpdate",method = RequestMethod.POST)
	public String addOrUpdateActivity(HttpServletRequest request, ModelMap model,@ModelAttribute RdTravelActivity travelActivity) {
		if(StringUtil.isEmpty(travelActivity.getActivityName())){
			model.addAttribute("msg", "旅游活动名称不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelActivity.getCoverImage())){
			model.addAttribute("msg", "旅游活动封面图片不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelActivity.getImage())){
			model.addAttribute("msg", "旅游活动图片不可以为空");
			return Constants.MSG_URL;
		}
		if(travelActivity.getStatus()==null){
			model.addAttribute("msg", "旅游活动状态不可以为空");
			return Constants.MSG_URL;
		}
		if(travelActivity.getActivityCost()==null){
			model.addAttribute("msg", "旅游活动价格不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelActivity.getRemark())){
			model.addAttribute("msg", "旅游活动规则不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelActivity.getStartTimeStr())){
			model.addAttribute("msg", "旅游活动报名开始时间不可以为空");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(travelActivity.getEndTimeStr())){
			model.addAttribute("msg", "旅游活动报名结束时间不可以为空");
			return Constants.MSG_URL;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start=null;
		Date end=null;
		try {
			start = format.parse(travelActivity.getStartTimeStr() + " 00:00:00");
			end = format.parse(travelActivity.getEndTimeStr() + " 23:59:59");
			travelActivity.setStartTime(start);
			travelActivity.setEndTime(end);
		} catch (ParseException e) {
			e.printStackTrace();
			model.addAttribute("msg", "传入时间格式错误");
			return Constants.MSG_URL;
		}
		if(travelActivity.getNumCeiling()==null){
			model.addAttribute("msg", "旅游活动参团人数上限不可以为空");
			return Constants.MSG_URL;
		}
		Subject subject = SecurityUtils.getSubject();
		if(subject!=null){
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null && principal.getId() != null) {
				Long id = principal.getId();
				String username = principal.getUsername();
				Map<String, String> map =rdTravelActivityService.saveOrEdit(travelActivity,id,username);
				if (map == null || StringUtil.isEmpty(map.get("code"))) {
					model.addAttribute("msg", "保存旅游券失败！");
					return Constants.MSG_URL;
				}

				String code = map.get("code");
				if (StringUtil.isEmpty(code) || code.equals("0")) {
					String errorMsg = map.get("msg");
					model.addAttribute("msg", errorMsg);
					return Constants.MSG_URL;
				}
				//model.addAttribute("msg", "成功");
				return "redirect:travelActivity/list.jhtml";//TODO
			}
		}
		model.addAttribute("msg", "请登录后再进行旅游活动相关操作");
		return Constants.MSG_URL;
	}

	/**
	 * 旅游参团信息管理列表
	 *
	 */
	@RequestMapping("/travelMemInfo/list")
	public String travelMemInfo(ModelMap model,
							 @RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
							 @RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize,
							 @ModelAttribute RdTravelMemInfo memInfo) {
		//参数整理
		Pageable pager = new Pageable();
		pager.setPageNumber(pageNo);
		pager.setPageSize(pageSize);
		pager.setOrderProperty("id");
		pager.setOrderDirection(Order.Direction.DESC);
		pager.setParameter(memInfo);
		Page<RdTravelMemInfo> page = rdTravelMemInfoService.findByPage(pager);
		model.addAttribute("page", page);
		return "/common/travelTicket/join/list";//TODO
	}

	/**
	 * 旅游团价格表
	 *
	 */
	@RequestMapping("/travelCost/list")
	public String travelCost(ModelMap model,
								@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
								@RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize,
								@ModelAttribute RdTravelCost costInfo) {
		//参数整理
		Pageable pager = new Pageable();
		pager.setPageNumber(pageNo);
		pager.setPageSize(pageSize);
		pager.setOrderProperty("id");
		pager.setOrderDirection(Order.Direction.DESC);
		pager.setParameter(costInfo);
		Page<RdTravelCost> page = rdTravelCostService.findByPage(pager);
		model.addAttribute("page", page);
		return "/common/travelTicket/price/list";//TODO
	}

	/**
	 * 旅游团价格表
	 *
	 */
	@RequestMapping("/travelCost/export")
	public String travelCostExport(ModelMap model,@ModelAttribute RdTravelCost costInfo) {
		rdTravelCostService.export(costInfo);
		model.addAttribute("msg", "导出成功");
		return Constants.MSG_URL;//TODO
	}

	/**
	 * 旅游团价格表
	 *
	 */
	/*@RequestMapping("/travel/upload")
	public String downloadTicketDetail(ModelMap model) {
		System.out.println("*************************导出未使用旅游券***************************");
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = requestAttributes.getResponse();
		HttpServletRequest request = requestAttributes.getRequest();

		// 文件名
		String filename = "旅游券列表.xls";

		try {

			// 写到服务器上
			//String path = request.getSession().getServletContext().getRealPath("") + "/" + filename;
			//‪C:\Users\LDQ\Desktop\excel
			String path = "C:\\Users\\LDQ\\Desktop" + "/" + filename;

			// 写到服务器上（这种测试过，在本地可以，放到linux服务器就不行）
			//String path =  this.getClass().getClassLoader().getResource("").getPath()+"/"+filename;

			File name = new File(path);
			// 创建写工作簿对象
			WritableWorkbook workbook = Workbook.createWorkbook(name);
			// 工作表
			WritableSheet sheet = workbook.createSheet("旅游券列表", 0);
			// 设置字体;
			//WritableFont font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

			WritableCellFormat cellFormat = new WritableCellFormat(font);
			// 设置背景颜色;
			//cellFormat.setBackground(Colour.WHITE);
			// 设置边框;
			//cellFormat.setBorder(Border.ALL, BorderLineStyle.DASH_DOT);
			// 设置文字居中对齐方式;
			cellFormat.setAlignment(Alignment.CENTRE);
			// 设置垂直居中;
			cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			// 分别给1,5,6列设置不同的宽度;
			//sheet.setColumnView(0, 15);
			//sheet.setColumnView(4, 60);
			//sheet.setColumnView(5, 35);
			// 给sheet电子版中所有的列设置默认的列的宽度;
			sheet.getSettings().setDefaultColumnWidth(20);
			// 给sheet电子版中所有的行设置默认的高度，高度的单位是1/20个像素点,但设置这个貌似就不能自动换行了
			// sheet.getSettings().setDefaultRowHeight(30 * 20);
			// 设置自动换行;
			cellFormat.setWrap(true);

			// 单元格
			Label label0 = new Label(0, 0, "id", cellFormat);
			Label label1 = new Label(1, 0, "旅游券id", cellFormat);
			Label label2 = new Label(2, 0, "旅游券面值", cellFormat);
			Label label3 = new Label(3, 0, "旅游券名称", cellFormat);
			Label label4 = new Label(4, 0, "旅游券详情编号", cellFormat);
			Label label5 = new Label(5, 0, "旅游券详情状态", cellFormat);
			Label label6 = new Label(6, 0, "持有人会员编号", cellFormat);
			Label label7 = new Label(7, 0, "持有人昵称", cellFormat);
			Label label8 = new Label(8, 0, "持有（获取）时间", cellFormat);
			Label label9 = new Label(9, 0, "使用时间", cellFormat);
			Label label10 = new Label(10, 0, "关联旅游活动id", cellFormat);
			Label label11 = new Label(11, 0, "使用于旅游活动编号", cellFormat);
			Label label12 = new Label(12, 0, "核销时间", cellFormat);
			Label label13 = new Label(13, 0, "核销人", cellFormat);

			sheet.addCell(label0);
			sheet.addCell(label1);
			sheet.addCell(label2);
			sheet.addCell(label3);
			sheet.addCell(label4);
			sheet.addCell(label5);
			sheet.addCell(label6);
			sheet.addCell(label7);
			sheet.addCell(label8);
			sheet.addCell(label9);
			sheet.addCell(label10);
			sheet.addCell(label11);
			sheet.addCell(label12);
			sheet.addCell(label13);

			// 给第二行设置背景、字体颜色、对齐方式等等;
			//WritableFont font2 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableFont font2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat cellFormat2 = new WritableCellFormat(font2);
			// 设置文字居中对齐方式;
			cellFormat2.setAlignment(Alignment.CENTRE);
			// 设置垂直居中;
			cellFormat2.setVerticalAlignment(VerticalAlignment.CENTRE);
			//cellFormat2.setBackground(Colour.WHITE);
			//cellFormat2.setBorder(Border.ALL, BorderLineStyle.THIN);
			cellFormat2.setWrap(true);

			// 记录行数
			int n = 1;

			// 查找所有未提交订单
			Map<String, List<RdTravelTicketDetail>> travelTicketMap = ticketDetailService.findNotUseTravelTicket();

			if (travelTicketMap != null && travelTicketMap.size() > 0) {

				// 遍历
				for (Map.Entry<String, List<RdTravelTicketDetail>> entry : travelTicketMap.entrySet()){

					List<RdTravelTicketDetail> orderExcelList = entry.getValue();
					for (RdTravelTicketDetail a : orderExcelList) {
						Label lt0 = new Label(0, n, a.getId()+"", cellFormat2);
						Label lt1 = new Label(1, n, a.getTravelId()+"", cellFormat2);
						Label lt2 = new Label(2, n, a.getTicketPrice()+"", cellFormat2);
						Label lt3 = new Label(3, n, a.getTravelName(), cellFormat2);
						Label lt4 = new Label(4, n, a.getTicketSn(), cellFormat2);
						Label lt5 = new Label(5, n, "", cellFormat2);
						if (a.getStatus()==0){
							lt5 = new Label(5, n, "未使用", cellFormat2);
						}else if (a.getStatus()==1){
							lt5 = new Label(5, n, "报名占用", cellFormat2);
						}else if (a.getStatus()==2){
							lt5 = new Label(5, n, "已核销", cellFormat2);
						}else {
							lt5 = new Label(5, n, "已过期", cellFormat2);
						}
						Label lt6 = new Label(6, n, a.getOwnCode(), cellFormat2);
						Label lt7 = new Label(7, n, a.getOwnNickName(), cellFormat2);
						Label lt8 = new Label(8, n, a.getOwnTime()+"", cellFormat2);
						Label lt9 = new Label(9, n, Optional.ofNullable(a.getUseTime()+"").orElse(""), cellFormat2);
						Label lt10 = new Label(10, n, Optional.ofNullable(a.getUseActivityId()+"").orElse(""), cellFormat2);
						Label lt11 = new Label(11, n, Optional.ofNullable(a.getUseActivityCode()+"").orElse(""), cellFormat2);
						Label lt12 = new Label(12, n, Optional.ofNullable(a.getConfirmTime()+"").orElse(""), cellFormat2);
						Label lt13 = new Label(13, n, Optional.ofNullable(a.getConfirmCode()+"").orElse(""), cellFormat2);

						sheet.addCell(lt0);
						sheet.addCell(lt1);
						sheet.addCell(lt2);
						sheet.addCell(lt3);
						sheet.addCell(lt4);
						sheet.addCell(lt5);
						sheet.addCell(lt6);
						sheet.addCell(lt7);
						sheet.addCell(lt8);
						sheet.addCell(lt9);
						sheet.addCell(lt10);
						sheet.addCell(lt11);
						sheet.addCell(lt12);
						sheet.addCell(lt13);

						n++;
					}
					//添加一行空白
					Label lt0 = new Label(0, n, "");
					Label lt1 = new Label(1, n, "");
					Label lt2 = new Label(2, n, "");
					Label lt3 = new Label(3, n, "");
					Label lt4 = new Label(4, n, "");
					Label lt5 = new Label(5, n, "");
					Label lt6 = new Label(6, n, "");
					Label lt7 = new Label(7, n, "");
					Label lt8 = new Label(8, n, "");
					Label lt9 = new Label(9, n, "");
					Label lt10 = new Label(10, n, "");
					Label lt11 = new Label(11, n, "");
					Label lt12 = new Label(12, n, "");
					Label lt13 = new Label(13, n, "");

					sheet.addCell(lt0);
					sheet.addCell(lt1);
					sheet.addCell(lt2);
					sheet.addCell(lt3);
					sheet.addCell(lt4);
					sheet.addCell(lt5);
					sheet.addCell(lt6);
					sheet.addCell(lt7);
					sheet.addCell(lt8);
					sheet.addCell(lt9);
					sheet.addCell(lt10);
					sheet.addCell(lt11);
					sheet.addCell(lt12);
					sheet.addCell(lt13);

					n++;
				}

				//添加一行空白
				Label lt0 = new Label(0, n, "人数=");
				Label lt1 = new Label(1, n, travelTicketMap.size()+"");
				Label lt2 = new Label(2, n, "");
				Label lt3 = new Label(3, n, "");

				sheet.addCell(lt0);
				sheet.addCell(lt1);
				sheet.addCell(lt2);
				sheet.addCell(lt3);
			}

			//开始执行写入操作
			workbook.write();
			//关闭流
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 第六步，下载excel

		OutputStream out = null;
		try {

			// 1.弹出下载框，并处理中文
			 //如果是从jsp页面传过来的话，就要进行中文处理，在这里action里面产生的直接可以用
			//String filename = request.getParameter("filename");

			 //if (request.getMethod().equalsIgnoreCase("GET")) {
			 //filename = new String(filename.getBytes("iso8859-1"), "utf-8");
			 //}


			response.addHeader("content-disposition", "attachment;filename="
					+ java.net.URLEncoder.encode(filename, "utf-8"));

			// 2.下载
			out = response.getOutputStream();
			//String path3 = request.getSession().getServletContext().getRealPath("") + "/" + filename;
			String path3 = "C:\\Users\\LDQ\\Desktop" + "/" + filename;

			// inputStream：读文件，前提是这个文件必须存在，要不就会报错
			InputStream is = new FileInputStream(path3);

			byte[] b = new byte[4096];
			int size = is.read(b);
			while (size > 0) {
				out.write(b, 0, size);
				size = is.read(b);
			}
			out.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("导出成功");
		model.addAttribute("msg", "导出成功");
		return Constants.MSG_URL;//TODO
	}*/

	/**
	 * 后台发放旅游券
	 * @param request
	 * @param model
	 * @param ticketId 旅游券id
	 * @param num 发放数量
	 * @param mmCode 会员编号
	 * @param remark 备注
	 * @return
	 */
	@RequestMapping(value = "/travelTicket/send",method = RequestMethod.POST)
	public String sendTravelTicket(HttpServletRequest request, ModelMap model,Long ticketId,Integer num,String mmCode,String remark) {
		Subject subject = SecurityUtils.getSubject();
		String username="";
		if(subject!=null){
			Principal principal = (Principal) subject.getPrincipal();
			if (principal != null && principal.getId() != null) {
				Long id = principal.getId();
				username = principal.getUsername();
			}else {
				model.addAttribute("msg", "请登录后进行操作");
				return Constants.MSG_URL;
			}
		}else {
			model.addAttribute("msg", "请登录后进行操作");
			return Constants.MSG_URL;
		}
		if(ticketId==null){
			model.addAttribute("msg", "请选择需要赠送的旅游券id");
			return Constants.MSG_URL;
		}
		if(StringUtil.isEmpty(mmCode)){
			model.addAttribute("msg", "请选择需要赠送会员编号");
			return Constants.MSG_URL;
		}
		if(num==null){
			model.addAttribute("msg", "请选择需要赠送的旅游券张数");
			return Constants.MSG_URL;
		}
		RdTravelTicket travelTicket = rdTravelTicketService.find(ticketId);
		if(travelTicket==null){
			model.addAttribute("msg", "当前选择的旅游券不存在");
			return Constants.MSG_URL;
		}
		if(num<1){
			model.addAttribute("msg", "请选择正确的旅游券赠送数量");
			return Constants.MSG_URL;
		}
		RdMmBasicInfo basicInfo = rdMmBasicInfoService.findByMCode(mmCode);
		if(basicInfo==null){
			model.addAttribute("msg", "当前选择的会员不存在");
			return Constants.MSG_URL;
		}
		ticketDetailService.sendTravelTicket(travelTicket,num,basicInfo,remark,username);
		model.addAttribute("msg", "旅游券发放成功");
		model.addAttribute("flag", 2);
		//return Constants.MSG_URL;
		return "/common/travelTicket/grantCoupons/back_message";
		/*return "redirect:list.jhtml";*/
	}

	/**
	 * 查询后台优惠券旅游券发放记录
	 * @param pageable
	 * @param model
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/ticketSendLog/list")
	public String ticketSendLogList(Pageable pageable, ModelMap model,@ModelAttribute RdTicketSendLog param) {
		pageable.setParameter(param);
		pageable.setOrderProperty("send_time");
		pageable.setOrderDirection(Order.Direction.DESC);
		model.addAttribute("page", rdTicketSendLogService.findByPage(pageable));
		return "/common/travelTicket/grantCoupons/index";
	}

	/**
	 * 获取旅游券信息跳转后台发放旅游券
	 * @param model
	 * @param ticketId
	 * @return
	 */
	@RequestMapping("/travelTicket/findById")
	public String findTicketById(ModelMap model, @RequestParam(required = true, value = "ticketId") Long ticketId) {
		if(ticketId==null){
			model.addAttribute("msg", "旅游券id不可以为空");
			return Constants.MSG_URL;
		}
		RdTravelTicket rdTravelTicket = rdTravelTicketService.find(ticketId);
		if(rdTravelTicket==null){
			model.addAttribute("msg", "旅游券信息异常");
			return Constants.MSG_URL;
		}
		model.addAttribute("data", rdTravelTicket);
		model.addAttribute("flag", 2);
		return "/common/travelTicket/grantCoupons/edit";
	}
}
