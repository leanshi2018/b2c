package com.framework.loippi.controller.travel;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.travel.RdTourismComplianceService;
import com.framework.loippi.service.travel.RdTravelTicketService;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.utils.DateConverter;

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

	/**
	 * 周期计算达标送券
	 * @param request
	 * @param model
	 * @param periodCode
	 * @return
	 */
	@RequestMapping(value = "/compliance",method = RequestMethod.POST)
	public String compliance(HttpServletRequest request, ModelMap model,@RequestParam(required = true, value = "periodCode") String periodCode) {

		if(periodCode==null || "".equals(periodCode)){
			model.addAttribute("msg", "请传入周期代码");
			return Constants.MSG_URL;
		}
		Integer sign = 0;
		String periods = "202007,202008,202009,202010,202011,202012";
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

				//计算'202006','202007','202008','202010','202011','202012'内rd_dis_qualification里DD_NEW_VIP_NUMBER新推荐vip人数
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
								//'202006','202007','202008','202010','202011','202012'中任何个周期买足25mi
								Integer countMi = memberQualificationService.countByMCode(rdMmRelation.getMmCode());
								if (countMi>0){
									oldNum++;
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
			return "";
		}else {
			model.addAttribute("msg", "没有旅游券可选择");
			return "";
		}

	}

}
