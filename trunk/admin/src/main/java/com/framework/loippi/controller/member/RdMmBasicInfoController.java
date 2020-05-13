package com.framework.loippi.controller.member;

import cn.jiguang.common.utils.StringUtils;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.framework.loippi.controller.GenericController;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

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


	/**
	 * 后台个人业绩
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/admin/selfPerformance")
	public String selfPerformance(Model model,@RequestParam(required = false, value = "periodCode") String periodCode,
								  @RequestParam(required = false, value = "mCode") String mCode) {
		
		return "";
	}

    /*******************************************************通联接口*********************************************************/
	/**TODO
	 * 会员钱包管理页面
	 * @param model
	 * @return
	 */
	/*@RequestMapping(value = "/getMemberWalletInfo", method = RequestMethod.GET)
	public String getMemberWalletInfo(String mCode, ModelMap model, HttpServletRequest request) throws Exception {
		if (mCode==null){
			model.addAttribute("msg", "mCode未空");
			model.addAttribute("referer", request.getHeader("Referer"));
			return Constants.MSG_URL;
		}
		RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.findByMCode(mCode);
		if (rdMmBasicInfo==null){
			model.addAttribute("msg", "找不到该会员基础信息");
			model.addAttribute("referer", request.getHeader("Referer"));
			return Constants.MSG_URL;
		}

		Long userState = 0l;
		String name = "";
		String phone = "";
		String cardNoDecrypt = "";
		String s = TongLianUtils.getMemberInfo(mCode);
		if(!"".equals(s)) {
			Map maps = (Map) JSON.parse(s);
			String status = maps.get("status").toString();
			if (status.equals("OK")) {
				String signedValue = maps.get("signedValue").toString();
				Map okMap = (Map) JSON.parse(signedValue);
				//Long memberType = Optional.ofNullable(Long.valueOf(okMap.get("memberType").toString())).orElse(0l);// 会员类型 2企业 3个人
				Map<String, Object> freezeAmount =  (Map<String, Object>)okMap.get("freezeAmount");//会员信息 否
				if (freezeAmount!=null){
					userState = Long.valueOf(freezeAmount.get("userState").toString());//用户状态。  1有效  3审核失败 5已锁 7待审核

					name = freezeAmount.get("name").toString();//姓名 否
					//String userId = freezeAmount.get("userId").toString();//通商云用户 id
					phone = freezeAmount.get("phone").toString();//手机号码 否
					String identityCardNo = freezeAmount.get("identityCardNo").toString();//身份证号码，RSA 加密。否
					//byte[] bytes = TongLianUtils.hexStringToByteArray(identityCardNo);
					cardNoDecrypt = YunClient.decrypt(identityCardNo);
					//Boolean isPhoneChecked = (Boolean)freezeAmount.get("isPhoneChecked");//是否绑定手机  否
					//String registerTime = freezeAmount.get("registerTime").toString();//创建时间 yyyy-MM-dd HH:mm:ss 否
					//Boolean isIdentityChecked = (Boolean)freezeAmount.get("isIdentityChecked");//是否进行实名认证 否
					//String realNameTime = freezeAmount.get("realNameTime").toString();//实名认证时间 yyyy-MM-dd HH:mm:ss 否
					//String remark = freezeAmount.get("remark").toString();//备注  否
					//Boolean isSetPayPwd = (Boolean)freezeAmount.get("isSetPayPwd");//是否已签电子协议  f
					//Boolean isSignContract = (Boolean)freezeAmount.get("isSignContract");//是否已签电子协议 f
				}

			}else {
				String message = maps.get("message").toString();
				model.addAttribute("msg", "通联接口发生错误:"+Optional.ofNullable(message).orElse(""));
				model.addAttribute("referer", request.getHeader("Referer"));
				return Constants.MSG_URL;
			}
		}else {
			model.addAttribute("msg", "通联接口调取失败");
			model.addAttribute("referer", request.getHeader("Referer"));
			return Constants.MSG_URL;
		}

		List<BindCardDto> bindCardDtoList = new ArrayList<BindCardDto>();
		String res = TongLianUtils.queryBankCard(mCode,"");
		if(!"".equals(res)) {
			Map maps = (Map) JSON.parse(res);
			String status = maps.get("status").toString();
			if (status.equals("OK")) {
				String signedValue = maps.get("signedValue").toString();
				Map okMap = (Map) JSON.parse(signedValue);
				List<Map<String, Object>> bindCardList =  (List<Map<String, Object>>)okMap.get("bindCardList");//已绑定银行卡信息列表
				if (bindCardList.size()>0){
					for (Map<String, Object> bindCard : bindCardList) {
						BindCardDto bindCardDto = new BindCardDto();
						bindCardDto.setMmCode(mCode);
						bindCardDto.setBankName(Optional.ofNullable(bindCard.get("bankName").toString()).orElse(""));
						bindCardDto.setBankCardNo(Optional.ofNullable(bindCard.get("bankCardNo").toString()).orElse(""));
						bindCardDto.setAccName(Optional.ofNullable(rdMmBasicInfo.getMmName()).orElse(""));
						bindCardDto.setCardType(Optional.ofNullable(Long.valueOf(bindCard.get("cardType").toString())).orElse(1l));
						bindCardDto.setIsSafeCard((Boolean) bindCard.get("isSafeCard"));
						bindCardDto.setBindState(Optional.ofNullable(Long.valueOf(bindCard.get("bindState").toString())).orElse(1l));
						bindCardDtoList.add(bindCardDto);
					}
				}

			}else {
				String message = maps.get("message").toString();
				model.addAttribute("msg", "通联接口发生错误:"+Optional.ofNullable(message).orElse(""));
				model.addAttribute("referer", request.getHeader("Referer"));
				return Constants.MSG_URL;
			}
		}else {
			model.addAttribute("msg", "通联接口调取失败");
			model.addAttribute("referer", request.getHeader("Referer"));
			return Constants.MSG_URL;
		}

		Long allAmount = 0l;//总额
		Long freezeAmount = 0l;//冻结额
		String resBalance = TongLianUtils.queryBalance(mCode, TongLianUtils.ACCOUNT_SET_NO);
		if(!"".equals(resBalance)) {
			Map maps = (Map) JSON.parse(resBalance);
			String status = maps.get("status").toString();
			if (status.equals("OK")) {
				String signedValue = maps.get("signedValue").toString();
				Map okMap = (Map) JSON.parse(signedValue);
				allAmount = Optional.ofNullable(Long.valueOf(okMap.get("allAmount").toString())).orElse(0l);//总额
				freezeAmount =  Optional.ofNullable(Long.valueOf(okMap.get("freezenAmount").toString())).orElse(0l);//冻结额

			}else {
				String message = maps.get("message").toString();
				model.addAttribute("msg", "通联接口发生错误:"+Optional.ofNullable(message).orElse(""));
				model.addAttribute("referer", request.getHeader("Referer"));
				return Constants.MSG_URL;
			}
		}else {
			model.addAttribute("msg", "通联接口调取失败");
			model.addAttribute("referer", request.getHeader("Referer"));
			return Constants.MSG_URL;
		}

		model.addAttribute("MemberWalletInfo", MemberWalletInfo.build(rdMmBasicInfo,bindCardDtoList,allAmount,freezeAmount,cardNoDecrypt,name,phone,userState));
		return "";
	}*/
}
