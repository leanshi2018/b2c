package com.framework.loippi.controller.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.entity.common.ShopCommonArea;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.user.RdMmAddInfo;
import com.framework.loippi.param.user.UserAddrsAddParam;
import com.framework.loippi.param.user.UserAddrsUpdateParam;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.UserAddrsAddResult;
import com.framework.loippi.result.user.UserAddrsListResult;
import com.framework.loippi.service.common.ShopCommonAreaService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;

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
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopCommonAreaService areaService;
    @Resource
    private ShopOrderAddressService shopOrderAddressService;

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

    //自提地址列表
    @RequestMapping(value = "/mentionAddrList.json")
    public String mentionAddrList(HttpServletRequest request) {

        List<RdMmAddInfo> lists = addressService.findMentionAddrList();
        if (lists.size()==0){
            return ApiUtils.error("自提地址为空");
        }
        return ApiUtils.success(UserAddrsListResult.build(lists));
    }

    /**
     *
     * @param addId 地址表id
     * @param orderSn 订单编号
     * @param request
     * @return
     */
    @RequestMapping(value = "/replaceOrderAdd.json", method = RequestMethod.POST)
    public String replaceOrderAdd(String orderSn,Long addId,
                                  HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录");
        }
        if (addId==null){
            return ApiUtils.error("请选择地址");
        }
        if(orderSn==null){
            return ApiUtils.error("订单信息未知");
        }
        ShopOrder shopOrder = shopOrderService.find("orderSn",orderSn);
        if(shopOrder==null){
            return ApiUtils.error("订单信息异常");
        }
        if(shopOrder.getOrderState()!=10&&shopOrder.getOrderState()!=20){
            return ApiUtils.error("只有待发货及待付款订单可以修改订单地址");
        }
        RdMmAddInfo address = addressService.find(addId);
        if(address==null){
            return ApiUtils.error("地址信息异常");
        }
        //2.修改订单关联的地址信息
        Long twiterId = twiterIdService.getTwiterId();
        ShopOrderAddress orderAddress = new ShopOrderAddress();
        orderAddress.setId(twiterId);
        if (addId.longValue()>0){
            orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
            if (address.getMmCode()==null || "".equals(address.getMmCode())){
                orderAddress.setMemberId(0l);
            }else {
                orderAddress.setMemberId(Long.parseLong(address.getMmCode()));
            }
            orderAddress.setTrueName(address.getConsigneeName());
            orderAddress.setAddress(address.getAddDetial());
            orderAddress.setMobPhone(address.getMobile());
            orderAddress.setAreaInfo(address.getAddProvinceCode() + address.getAddCityCode() + address.getAddCountryCode());
            if ("".equals(address.getAddCountryCode())){
                ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCityCode());
                if (shopCommonArea==null) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonArea.getExpressState()==1){//不配送
                    throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址暂不配送");
                }
                orderAddress.setAreaId(shopCommonArea.getId());
                orderAddress.setCityId(shopCommonArea.getId());
                orderAddress.setProvinceId(shopCommonArea.getAreaParentId());
                shopOrderAddressService.save(orderAddress);
            }else{
                List<ShopCommonArea> shopCommonAreas = areaService.findByAreaName(address.getAddCountryCode());//区
                if (CollectionUtils.isEmpty(shopCommonAreas)) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonAreas.size()>1){
                    List<ShopCommonArea> shopCommonCitys = areaService.findByAreaName(address.getAddCityCode());//市
                    if (shopCommonCitys==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    if (shopCommonCitys.size()==1){
                        ShopCommonArea shopCommonCity = shopCommonCitys.get(0);
                        orderAddress.setCityId(shopCommonCity.getId());
                        orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                        for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                            if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                                orderAddress.setAreaId(shopCommonArea.getId());
                                if (shopCommonArea.getExpressState()==1){//不配送
                                    throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                                }
                            }
                        }
                    }else {
                        ShopCommonArea shopCommonProvice = areaService.find("areaName", address.getAddProvinceCode());//省
                        for (ShopCommonArea shopCommonCity : shopCommonCitys) {
                            if (shopCommonCity.getAreaParentId().longValue()==shopCommonProvice.getId().longValue()){
                                orderAddress.setCityId(shopCommonCity.getId());
                                orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                                for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                                    if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                                        orderAddress.setAreaId(shopCommonArea.getId());
                                        if (shopCommonArea.getExpressState()==1){//不配送
                                            throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    shopOrderAddressService.save(orderAddress);
                }else{
                    ShopCommonArea shopCommonArea = shopCommonAreas.get(0);
                    if (shopCommonArea.getExpressState()==1){//不配送
                        throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                    }
                    orderAddress.setAreaId(shopCommonArea.getId());
                    //if ()
                    orderAddress.setCityId(shopCommonArea.getAreaParentId());
                    ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                    if (shopCommonArea2==null) {
                        throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                    }
                    orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
                    shopOrderAddressService.save(orderAddress);
                }
            }
        }else {
            orderAddress.setMemberId(shopOrder.getBuyerId());
            orderAddress.setTrueName(shopOrder.getBuyerName());
            orderAddress.setAreaId(-1l);
            orderAddress.setCityId(-1l);
            orderAddress.setAreaInfo("自提没有保存收货地址");
            orderAddress.setMobPhone(address.getMobile());
            orderAddress.setIsDefault("0");
            orderAddress.setProvinceId(-1l);
            orderAddress.setMentionId(addId);

        }

        //3.修改订单表关联的订单地址id
        shopOrder.setAddressId(twiterId);
        shopOrder.setIsModify(1);
        shopOrderService.update(shopOrder);
        return ApiUtils.success("修改收货地址信息成功");
    }

    /**
     *
     * @param param 地址详细信息
     * @param orderSn 订单编号
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateOrderAdd.json", method = RequestMethod.POST)
    public String updateOrderAdd(@Valid UserAddrsAddParam param,String orderSn,
                                 HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录");
        }
        ShopOrder shopOrder = shopOrderService.find("orderSn",orderSn);
        if(shopOrder==null){
            return ApiUtils.error("订单信息异常");
        }
        if(shopOrder.getOrderState()!=10&&shopOrder.getOrderState()!=20){
            return ApiUtils.error("只有待发货及待付款订单可以修改订单地址");
        }
        //1.根据传入信息 新增一个地址信息
        RdMmAddInfo address = new RdMmAddInfo();
        address.setMobile(param.getMobile());
        address.setConsigneeName(param.getName());
        address.setMmCode(member.getMmCode());
        address.setDefaultadd(param.getIsDefault());
        address.setAddDetial(param.getAddr());
        address.setAddProvinceCode(param.getAddProvinceCode());
        address.setAddCityCode(param.getAddCityCode());
        address.setAddCountryCode(param.getAddCountryCode());
        address.setValid(1);
        List<RdMmAddInfo> addresses = addressService.findList(Paramap.create()
                .put("mmCode", member.getMmCode()));
        // 如果当前用户只有一条收货地址，把其它地址改为非默认
        if (addresses.size() == 0) {
            address.setDefaultadd(1);
        }
        Long saveId = addressService.save(address);
        if (address.getDefaultadd() == 1) {
            addressService.updateDef(address.getAid(), member.getMmCode());
        }
        //2.修改订单关联的地址信息
        Long twiterId = twiterIdService.getTwiterId();
        ShopOrderAddress orderAddress = new ShopOrderAddress();
        if (address == null) {
            throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXIT, "收货地址不能为空");
        }
        orderAddress.setIsDefault(Optional.ofNullable(address.getDefaultadd()).orElse(0).toString());
        orderAddress.setId(twiterId);
        orderAddress.setMemberId(Long.parseLong(address.getMmCode()));
        orderAddress.setTrueName(address.getConsigneeName());
        orderAddress.setAddress(address.getAddDetial());
        orderAddress.setMobPhone(address.getMobile());
        orderAddress
                .setAreaInfo(address.getAddProvinceCode() + address.getAddCityCode() + address.getAddCountryCode());
        if ("".equals(address.getAddCountryCode())){
            ShopCommonArea shopCommonArea = areaService.find("areaName", address.getAddCityCode());
            if (shopCommonArea==null) {
                throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
            }
            if (shopCommonArea.getExpressState()==1){//不配送
                throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址暂不配送");
            }
            orderAddress.setAreaId(shopCommonArea.getId());
            orderAddress.setCityId(shopCommonArea.getId());
            orderAddress.setProvinceId(shopCommonArea.getAreaParentId());
            shopOrderAddressService.save(orderAddress);
        }else{
            List<ShopCommonArea> shopCommonAreas = areaService.findByAreaName(address.getAddCountryCode());//区
            if (CollectionUtils.isEmpty(shopCommonAreas)) {
                throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
            }
            if (shopCommonAreas.size()>1){
                List<ShopCommonArea> shopCommonCitys = areaService.findByAreaName(address.getAddCityCode());//市
                if (shopCommonCitys==null) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                if (shopCommonCitys.size()==1){
                    ShopCommonArea shopCommonCity = shopCommonCitys.get(0);
                    orderAddress.setCityId(shopCommonCity.getId());
                    orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                    for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                        if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                            orderAddress.setAreaId(shopCommonArea.getId());
                            if (shopCommonArea.getExpressState()==1){//不配送
                                throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                            }
                        }
                    }
                }else {
                    ShopCommonArea shopCommonProvice = areaService.find("areaName", address.getAddProvinceCode());//省
                    for (ShopCommonArea shopCommonCity : shopCommonCitys) {
                        if (shopCommonCity.getAreaParentId().longValue()==shopCommonProvice.getId().longValue()){
                            orderAddress.setCityId(shopCommonCity.getId());
                            orderAddress.setProvinceId(shopCommonCity.getAreaParentId());
                            for (ShopCommonArea shopCommonArea : shopCommonAreas) {
                                if (shopCommonArea.getAreaParentId().longValue()==shopCommonCity.getId().longValue()){
                                    orderAddress.setAreaId(shopCommonArea.getId());
                                    if (shopCommonArea.getExpressState()==1){//不配送
                                        throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                                    }
                                }
                            }
                        }
                    }
                }
                shopOrderAddressService.save(orderAddress);
            }else{
                ShopCommonArea shopCommonArea = shopCommonAreas.get(0);
                if (shopCommonArea.getExpressState()==1){//不配送
                    throw new StateResult(AppConstants.RECEIVED_ADDRESS_NOT_EXPRESS, "该收货地址不配送");
                }
                orderAddress.setAreaId(shopCommonArea.getId());
                //if ()
                orderAddress.setCityId(shopCommonArea.getAreaParentId());
                ShopCommonArea shopCommonArea2 = areaService.find(shopCommonArea.getAreaParentId());
                if (shopCommonArea2==null) {
                    throw new RuntimeException("请检查APP是否最新版本，并重新添加地址");
                }
                orderAddress.setProvinceId(shopCommonArea2.getAreaParentId());
                shopOrderAddressService.save(orderAddress);
            }
        }
        //3.修改订单表关联的订单地址id
        shopOrder.setAddressId(twiterId);
        shopOrder.setIsModify(1);
        shopOrderService.update(shopOrder);
        return ApiUtils.success("修改收货地址信息成功");
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

