package com.framework.loippi.controller.user;


import com.framework.loippi.consts.UpdateMemberInfoStatus;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.param.user.UserAddrsAddParam;
import com.framework.loippi.param.user.UserAddrsUpdateParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.UserAddrsAddResult;
import com.framework.loippi.result.user.UserAddrsListResult;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.*;
import com.framework.loippi.vo.address.MemberAddresVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收货地址
 * Created by Administrator on 2017/11/23.
 */
@Controller
@ResponseBody
@RequestMapping("/api/address")
public class AddressAPIController extends BaseController {

    @Resource
    private RdMmAddInfoService addressService;
    @Resource
    private ShopCommonAreaService shopCommonAreaService;

    //列表
    @RequestMapping(value = "/list.json")
    public String UserAddrsList(@RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Map<String, Object> params = new HashMap<>();
        params.put("mmCode", member.getMmCode());
        Pageable pager = new Pageable(pageNumber, pageSize);
        pager.setParameter(params);
        List<RdMmAddInfo> lists = addressService.findByPage(pager).getContent();
        return ApiUtils.success(UserAddrsListResult.build(lists));
    }

    //新增
    @RequestMapping(value = "/add.json", method = RequestMethod.POST)
    public String UserAddrsAdd(@Valid UserAddrsAddParam param,
                               HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if (member != null) {
            RdMmAddInfo addr = new RdMmAddInfo();
            addr.setMobile(param.getMobile());
            addr.setConsigneeName(param.getName());
            addr.setMmCode(member.getMmCode());
            addr.setDefaultadd(param.getIsDefault());
            addr.setAddDetial(param.getAddr());
            addr.setAddProvinceCode(param.getAddProvinceCode());
            addr.setAddCityCode(param.getAddCityCode());
            addr.setAddCountryCode(param.getAddCountryCode());

//            addr.setAddTownCode(param.getAddProvinceCode()+param.getAddCityCode()+param.getAddCountryCode());
            addr.setValid(1);
            List<RdMmAddInfo> addresses = addressService.findList(Paramap.create()
                    .put("mmCode", member.getMmCode()));
            // 如果当前用户只有一条收货地址，把其它地址改为非默认
            if (addresses.size() == 0) {
                addr.setDefaultadd(1);
            }
//            Long addrId = twiterIdService.getTwiterId();
//            addr.setId(addrId);
            Long saveId = addressService.save(addr);
            System.err.println(saveId);
            System.err.println(addr.getAid());
            //如果当前新增地址为默认 则修改其他地址
            if (addr.getDefaultadd() == 1) {
                addressService.updateDef(addr.getAid(), member.getMmCode());
            }

            if (saveId != null) {
                return ApiUtils.success(UserAddrsAddResult.build(addr));
            } else {
                ApiUtils.error("新增地址失败");
            }
        }
        return ApiUtils.error("请登录");
    }

    //编辑
    @RequestMapping(value = "/update.json", method = RequestMethod.POST)
    public String UserAddrsUpdate(@Valid UserAddrsUpdateParam param,
                                  BindingResult vResult,
                                  HttpServletRequest request) {
        if (vResult.hasErrors()) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        RdMmAddInfo addr = addressService.find("aid",param.getAddrId());
        if (addr == null) {
            return ApiUtils.error(Xerror.USER_ADDRESS_NOT_EXIST, "地址不存在");
        }
        int oldIsDefault = addr.getDefaultadd();
        addr.setMobile(param.getMobile());
        addr.setConsigneeName(param.getName());
        addr.setAddDetial(param.getAddr());
        addr.setDefaultadd(param.getIsDefault());
        addr.setAddProvinceCode(param.getAddProvinceCode());
        addr.setAddCityCode(param.getAddCityCode());
        addr.setAddCountryCode(param.getAddCountryCode());
//        addr.setAddTownCode(param.getAddProvinceCode()+param.getAddCityCode()+param.getAddCountryCode());
        addr.setValid(1);
        addressService.update(addr);
        //如果当前编辑地址为默认 则修改其他地址
        if (addr.getDefaultadd() == 1 && oldIsDefault == 0) {
            addressService.updateDef(addr.getAid(), addr.getMmCode());
        }
        if (addr.getDefaultadd() == 0 && oldIsDefault == 1) {
            List<RdMmAddInfo> shopMemberAddresses = addressService.findList(Paramap.create().put("mmCode", ((AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER)).getMmCode()));
            if (shopMemberAddresses != null && shopMemberAddresses.size() != 0) {
                RdMmAddInfo address = shopMemberAddresses.get(0);
                address.setDefaultadd(1);
                addressService.update(address);
            }
        }
        return ApiUtils.success(UserAddrsAddResult.build(addr));
    }


    //设为默认地址
    @RequestMapping(value = "/setDefault.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String UserAddrsSetdefault(Long addrId,
                               HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmAddInfo addr = addressService.find(addrId);
        if (addr == null) {
            return ApiUtils.error(Xerror.USER_ADDRESS_NOT_EXIST, "地址不存在");
        }
        addressService.updateDef(addr.getAid(), member.getMmCode());
        return ApiUtils.success();
    }

    //删除
    @RequestMapping(value = "/remove.json", method = RequestMethod.POST)
    public
    @ResponseBody
    String UserAddrsRemove(Long addrId,
                           HttpServletRequest request) {
        if (addrId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        RdMmAddInfo addr = addressService.find(addrId);
        if (addr == null) {
            return ApiUtils.error(Xerror.USER_ADDRESS_NOT_EXIST, "不存在的收货地址");
        }
        addressService.delete(addrId);
        if (addr.getDefaultadd() != null && addr.getDefaultadd() == 1) { // 如果删除默认地址，需选择下一个地址作为默认地址
            List<RdMmAddInfo> shopMemberAddresses = addressService.findList(Paramap.create().put("mmCode", ((AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER)).getMmCode()));
            if (shopMemberAddresses != null && shopMemberAddresses.size() != 0) {
                RdMmAddInfo address = shopMemberAddresses.get(0);
                address.setDefaultadd(1);
                addressService.update(address);
            }
        }
        return ApiUtils.success();
    }

    //获取默认地址
    @RequestMapping(value = "/getDefault.json", method = RequestMethod.POST)
    public String UserAddrsGetDefault(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Map<String, Object> params = new HashMap<>();
        params.put("isDefault", 1);
        params.put("mmCode", member.getMmCode());
        return ApiUtils.success(UserAddrsListResult.build(addressService.findList(params)));
    }

    /**
     * 查看单个地址详情
     *
     * @param request
     * @param addrId  地址id
     * @return
     */
    @RequestMapping(value = "/getAddress.json", method = RequestMethod.POST)
    public String getAddress(HttpServletRequest request, Long addrId) {
        if (addrId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        RdMmAddInfo addr = addressService.find(addrId);
        if (addr == null) {
            return ApiUtils.error(Xerror.USER_ADDRESS_NOT_EXIST, "不存在的收货地址");
        }
        return ApiUtils.success(UserAddrsAddResult.build(addr));
    }


}

