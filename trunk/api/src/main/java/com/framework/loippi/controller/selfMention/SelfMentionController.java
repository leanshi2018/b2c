package com.framework.loippi.controller.selfMention;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.NotifyConsts;
import com.framework.loippi.consts.OrderState;
import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.consts.WareHouseConsts;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.pojo.selfMention.GoodsType;
import com.framework.loippi.pojo.selfMention.OrderInfo;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.selfMention.SelfMentionOrderResult;
import com.framework.loippi.result.selfMention.SelfMentionOrderStatistics;
import com.framework.loippi.result.selfMention.SelfMentionShopResult;
import com.framework.loippi.result.selfMention.SelfOrderSubmitResult;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderAddressService;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.service.order.ShopOrderPayService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.user.RdGoodsAdjustmentService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAddInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdSysPeriodService;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import com.framework.loippi.service.ware.RdWareAdjustService;
import com.framework.loippi.service.ware.RdWareAllocationService;
import com.framework.loippi.service.ware.RdWareOrderService;
import com.framework.loippi.service.ware.RdWarehouseService;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.Xerror;
import com.framework.loippi.vo.store.MentionProductVo;
import com.framework.loippi.vo.store.MentionWareGoodsVo;
import com.framework.loippi.vo.store.OrderGoodsVo;

@Controller("selfMentionController")
@Slf4j
public class SelfMentionController extends BaseController {

    @Resource
    private RdInventoryWarningService rdInventoryWarningService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopGoodsGoodsService shopGoodsGoodsService;
    @Resource
    private RdWarehouseService rdWarehouseService;
    @Resource
    private RdWareOrderService rdWareOrderService;
    @Resource
    private RdWareAllocationService rdWareAllocationService;
    @Resource
    private RdWareAdjustService rdWareAdjustService;
    @Resource
    private RdSysPeriodService rdSysPeriodService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopOrderGoodsService shopOrderGoodsService;
    @Resource
    private RdGoodsAdjustmentService rdGoodsAdjustmentService;
    @Resource
    private ShopOrderAddressService shopOrderAddressService;
    @Resource
    private RdMmAddInfoService rdMmAddInfoService;
    @Resource
    private RdMmAccountInfoService rdMmAccountInfoService;
    @Resource
    private RdMmIntegralRuleService rdMmIntegralRuleService;
    @Resource
    private RdMmBasicInfoService rdMmBasicInfoService;
    @Resource
    private ShopOrderPayService orderPayService;
    @Resource
    private ShopMemberPaymentTallyService paymentTallyService;
    @Resource
    private AlipayMobileService alipayMobileService;
    @Resource
    private WechatMobileService wechatMobileService;
    /**
     * 点击进入我的小店
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/mention/center.json")
    @ResponseBody
    public String inCenter(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        String wareCode = rdWarehouse.getWareCode();//获取仓库号
        List<GoodsType> list=rdInventoryWarningService.findGoodsTypeByWareCode(wareCode);
        Integer goodsTypeNum=0;//商品种类数量
        if(list!=null&&list.size()>0){
            goodsTypeNum=list.size();
        }
        Integer mentionId = rdWarehouse.getMentionId();
        if(mentionId==null){
            return ApiUtils.error("自提店信息异常");
        }
        Integer dailyNum=shopOrderService.findDailyCountByMentionId(mentionId);
        Integer monthNum=shopOrderService.findMonthCountByMentionId(mentionId);
        List<OrderInfo> orderInfos=shopOrderService.findMonthOrderInfo(mentionId);
        BigDecimal total=BigDecimal.ZERO;
        if(orderInfos!=null&&orderInfos.size()>0){
            for (OrderInfo orderInfo : orderInfos) {
                total=total.add(orderInfo.getOrderAmount()).add(orderInfo.getUsePointNum()).subtract(orderInfo.getRefundAmount()).subtract(orderInfo.getRefundPoint());
            }
        }

        SelfMentionShopResult result=SelfMentionShopResult.build(goodsTypeNum,dailyNum,monthNum,total);
        result.setPortrait(member.getAvatar());
        result.setShopName(rdWarehouse.getWareName());
        return ApiUtils.success(result);
    }

    /**
     * 商品列表
     */
    @RequestMapping(value = "/api/mention/goodsList", method = RequestMethod.GET)
    @ResponseBody
    public String goodsList(HttpServletRequest request, Pageable pager) throws Exception {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        String mmCode = member.getMmCode();
        RdWarehouse warehouse = rdWarehouseService.findByMmCode(mmCode);
        if (warehouse == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouse.getWareCode();

        Paramap paramap = Paramap.create();
        //paramap.put("mmCode", member.getMmCode());
        paramap.put("wareCode", wareCode);
        paramap.put("wareStatus", 0);//0正常  1 停用
        if (wareCode == null ) {
            return ApiUtils.error("仓库代码为空");
        }
        pager.setOrderDirection(Order.Direction.DESC);
        //pager.setOrderProperty("create_time");
        pager.setParameter(paramap);
        List<MentionWareGoodsVo> list = new ArrayList<MentionWareGoodsVo>();
        Page<RdInventoryWarning> goodsPage = rdInventoryWarningService.findByPage(pager);
        if (goodsPage.getContent().size()==0){
            return ApiUtils.success(list);//空的对象
        }
        /*RdWarehouse warehouse = rdWarehouseService.findByCode(wareCode);
        if (warehouse == null ) {
            return ApiUtils.error("未找到该仓库");
        }*/
        for (RdInventoryWarning inventoryWarning : goodsPage.getContent()) {
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(inventoryWarning.getGoodsCode()));
            if (shopGoods.getGoodsShow()==1){
                ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(inventoryWarning.getSpecificationId());
                GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, goodsSpec);
                MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
                wareGoodsVo.setWareCode(Optional.ofNullable(inventoryWarning.getWareCode()).orElse(""));
                wareGoodsVo.setGoodsName(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
                wareGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
                wareGoodsVo.setSpecId(Optional.ofNullable(inventoryWarning.getSpecificationId()).orElse(0l));

                if (shopGoods.getGoodsType()==3){
                    wareGoodsVo.setSpecGoodsSpec(goodsSpec.getSpecGoodsSerial());
                }else{
                    String specInfo = "";
                    Map<String, String> map = goodsSpec.getSepcMap();
                    //遍历规格map,取出键值对,拼接specInfo
                    if (map != null) {
                        Set<String> set = map.keySet();
                        for (String str : set) {
                            specInfo += str + ":" + map.get(str) + "、";
                        }
                        specInfo = specInfo.substring(0, specInfo.length() - 1);
                    }
                    wareGoodsVo.setSpecGoodsSpec(specInfo);
                }

                wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
                wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
                wareGoodsVo.setPpv(Optional.ofNullable(shopGoods.getPpv()).orElse(BigDecimal.ZERO));
                wareGoodsVo.setInventory(Optional.ofNullable(inventoryWarning.getInventory()).orElse(0));
                Integer salesNum = shopOrderService.countMentionSales(warehouse.getMentionId(),inventoryWarning.getSpecificationId());//销量
                if (salesNum==null){
                    wareGoodsVo.setSales(0);
                }else {
                    wareGoodsVo.setSales(salesNum);
                }
                String ware = inventoryWarning.getWareCode();
                Long specId = inventoryWarning.getSpecificationId();
                Integer pNum = rdInventoryWarningService.findProductInventory(ware,specId);
                wareGoodsVo.setProductInventory(pNum);//单品库存
                list.add(wareGoodsVo);
            }
        }
        return ApiUtils.success(list);
    }

    /**
     * 单品商品列表
     */
    @RequestMapping(value = "/api/mention/productGoodsList", method = RequestMethod.GET)
    @ResponseBody
    public String productGoodsList(HttpServletRequest request, @RequestParam(required = false,defaultValue = "1",value = "currentPage")Integer currentPage) throws Exception {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        String mmCode = member.getMmCode();
        RdWarehouse warehouse = rdWarehouseService.findByMmCode(mmCode);
        if (warehouse == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouse.getWareCode();


        //该仓库所有单品的库存数
        List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCode(wareCode);
        Map<Long,Integer> inventoryMap = new HashMap<>();
        for (RdInventoryWarning inventoryWarning : inventoryWarningList) {
            Long goodsCode = Long.valueOf(inventoryWarning.getGoodsCode());//商品id
            ShopGoods goods = shopGoodsService.find(goodsCode);
            if (goods.getGoodsType()==3){//组合商品
                ShopGoodsSpec spec = shopGoodsSpecService.find(inventoryWarning.getSpecificationId());
                Map<String, String> specMap = JacksonUtil.readJsonToMap(spec.getSpecGoodsSpec());
                Map<String, String> goodsMap = JacksonUtil.readJsonToMap(spec.getSpecName());
                Set<String> keySpec = specMap.keySet();
                Set<String> keyGoods = goodsMap.keySet();
                Iterator<String> itSpec = keySpec.iterator();
                Iterator<String> itGoods = keyGoods.iterator();
                while (itSpec.hasNext() && itGoods.hasNext()) {
                    String specId = itSpec.next();//单品的规格id
                    String goodsId = itGoods.next();//单品的商品id
                    //拿到组合的具体数量情况
                    int joinNum=1;
                    Map<String,Object> mapGGs= new HashMap<>();
                    mapGGs.put("goodId",inventoryWarning.getGoodsCode());
                    mapGGs.put("combineGoodsId",goodsId);
                    List<ShopGoodsGoods> goodsGoodsList = shopGoodsGoodsService.findGoodsGoodsList(mapGGs);
                    ShopGoodsGoods goodsGoods = new ShopGoodsGoods();
                    if (goodsGoodsList.size()>0){
                        if (goodsGoodsList.size()==1){
                            goodsGoods = goodsGoodsList.get(0);
                        }else {
                            for (ShopGoodsGoods shopGoodsGoods : goodsGoodsList) {
                                if (shopGoodsGoods.getGoodsSpec().equals(specId)){
                                    goodsGoods = shopGoodsGoods;
                                }
                            }
                        }
                        if (goodsGoods==null){
                            throw new Exception("组合数据不全");
                        }
                    }else {
                        throw new Exception("组合数据不全");
                    }
                    if (goodsGoods!=null){
                        joinNum= Optional.ofNullable(goodsGoods.getJoinNum()).orElse(1);
                    }

                    Integer inventory = inventoryWarning.getInventory()*joinNum;

                    if (inventoryMap.containsKey(new Long(specId))){//存在
                        Integer num = inventoryMap.get(new Long(specId));
                        Integer total = num + inventory;
                        inventoryMap.put(new Long(specId),total);
                    }else {//不存在
                        inventoryMap.put(new Long(specId),inventory);
                    }

                }
            }else {//普通商品和换购商品(单品)
                if (inventoryMap.containsKey(inventoryWarning.getSpecificationId())){//存在
                    Integer num = inventoryMap.get(inventoryWarning.getSpecificationId());
                    inventoryMap.put(inventoryWarning.getSpecificationId(),num+inventoryWarning.getInventory());
                }else {//不存在
                    inventoryMap.put(inventoryWarning.getSpecificationId(),inventoryWarning.getInventory());
                }
            }

        }

        List<MentionWareGoodsVo> productResults = new ArrayList<MentionWareGoodsVo>();
            
        for(Long specId:inventoryMap.keySet()){//遍历map的键
            Integer num = inventoryMap.get(specId);
            RdInventoryWarning warning = rdInventoryWarningService.findInventoryWarningByWareAndSpecId(wareCode,specId);
            ShopGoodsSpec spec = shopGoodsSpecService.find(specId);
            ShopGoods shopGoods = shopGoodsService.find(spec.getGoodsId());

            GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, spec);
            MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
            wareGoodsVo.setWareCode(Optional.ofNullable(wareCode).orElse(""));
            wareGoodsVo.setGoodsName(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
            wareGoodsVo.setSpecId(Optional.ofNullable(specId).orElse(0l));

            if (shopGoods.getGoodsType()==3){
                wareGoodsVo.setSpecGoodsSpec(spec.getSpecGoodsSerial());
            }else{
                String specInfo = "";
                Map<String, String> map = spec.getSepcMap();
                //遍历规格map,取出键值对,拼接specInfo
                if (map != null) {
                    Set<String> set = map.keySet();
                    for (String str : set) {
                        specInfo += str + ":" + map.get(str) + "、";
                    }
                    specInfo = specInfo.substring(0, specInfo.length() - 1);
                }
                wareGoodsVo.setSpecGoodsSpec(specInfo);
            }
            wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setPpv(Optional.ofNullable(shopGoods.getPpv()).orElse(BigDecimal.ZERO));
            if (warning==null){
                wareGoodsVo.setInventory(0);
            }else {
                wareGoodsVo.setInventory(Optional.ofNullable(warning.getInventory()).orElse(0));
            }

            wareGoodsVo.setProductInventory(num);//单品库存
            productResults.add(wareGoodsVo);
        }

        List<MentionWareGoodsVo> productResultList = new ArrayList<MentionWareGoodsVo>();

        int size = productResults.size();
        if (size==0){
            MentionProductVo resultPage = new MentionProductVo();
            resultPage.setPageNum(0);
            resultPage.setPageSize(10);
            resultPage.setPages(1);
            resultPage.setTotal(size);
            resultPage.setProductResults(productResultList);
            return ApiUtils.success(resultPage);
        }else if (size<=10){
            currentPage = 1;
            productResultList = productResults;
        }else {
            int totalPage = (int) Math.ceil((double) size/10);
            if (currentPage>totalPage){
                currentPage = totalPage;
            }
            int startNum = (currentPage - 1) * 10;
            int endNum = ((currentPage - 1) * 10)+10;
            if (endNum>=size){
                endNum = size;
            }
            productResultList = productResults.subList(startNum, endNum);
        }

        MentionProductVo resultPage = new MentionProductVo();
        resultPage.setPageNum(currentPage);
        resultPage.setPageSize(10);
        resultPage.setPages((int) Math.ceil((double) size/10));
        resultPage.setTotal(size);
        resultPage.setProductResults(productResultList);

        return ApiUtils.success(resultPage);
    }

    /**
     * 欠货商品列表
     */
    @RequestMapping(value = "/api/mention/oweGoodsList", method = RequestMethod.GET)
    @ResponseBody
    public String oweGoodsList(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        String mmCode = member.getMmCode();
        RdWarehouse warehouse = rdWarehouseService.findByMmCode(mmCode);
        if (warehouse == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouse.getWareCode();

        List<MentionWareGoodsVo> list = new ArrayList<MentionWareGoodsVo>();
        List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareCode);
        if (inventoryWarningList.size()==0){
            return ApiUtils.success(list);
        }
        for (RdInventoryWarning inventoryWarning : inventoryWarningList) {
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(inventoryWarning.getGoodsCode()));
            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(inventoryWarning.getSpecificationId());
            GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, goodsSpec);
            MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
            wareGoodsVo.setWareCode(Optional.ofNullable(inventoryWarning.getWareCode()).orElse(""));
            wareGoodsVo.setGoodsName(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
            wareGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
            wareGoodsVo.setSpecId(Optional.ofNullable(inventoryWarning.getSpecificationId()).orElse(0l));
            if (shopGoods.getGoodsType()==3){
                wareGoodsVo.setSpecGoodsSpec(goodsSpec.getSpecGoodsSerial());
            }else{
                String specInfo = "";
                Map<String, String> map = goodsSpec.getSepcMap();
                //遍历规格map,取出键值对,拼接specInfo
                if (map != null) {
                    Set<String> set = map.keySet();
                    for (String str : set) {
                        specInfo += str + ":" + map.get(str) + "、";
                    }
                    specInfo = specInfo.substring(0, specInfo.length() - 1);
                }
                wareGoodsVo.setSpecGoodsSpec(specInfo);
            }
            wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setPpv(Optional.ofNullable(shopGoods.getPpv()).orElse(BigDecimal.ZERO));
            wareGoodsVo.setInventory(Optional.ofNullable(inventoryWarning.getInventory()).orElse(0));
            String ware = inventoryWarning.getWareCode();
            Long specId = inventoryWarning.getSpecificationId();

            Map<String, Object> haveMap = new HashMap<String, Object>();
            haveMap.put("wareCode", WareHouseConsts.COMPANY_WARE_CODE);
            haveMap.put("specificationId",specId);
            RdInventoryWarning warning = rdInventoryWarningService.haveInventoryByWareCodeAndSpecId(haveMap);
            Integer wareNum = 0;
            if (warning!=null){
                wareNum = warning.getInventory();
            }

            Integer num = Math.abs(wareGoodsVo.getInventory());

            if (wareNum>=num){
                list.add(wareGoodsVo);
            }
        }
        return ApiUtils.success(list);
    }

    /**
     * 欠货商品列表
     */
    @RequestMapping(value = "/api/mention/oweGoodsList1", method = RequestMethod.GET)
    @ResponseBody
    public String oweGoodsList1(HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        String mmCode = member.getMmCode();
        RdWarehouse warehouse = rdWarehouseService.findByMmCode(mmCode);
        if (warehouse == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouse.getWareCode();


        List<MentionWareGoodsVo> oweList = new ArrayList<MentionWareGoodsVo>();

        List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareCode);
        List<Long> specIdList = new ArrayList<Long>();
        for (RdInventoryWarning inventoryWarning : inventoryWarningList) {
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(inventoryWarning.getGoodsCode()));
            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(inventoryWarning.getSpecificationId());
            RdInventoryWarning companyInven = rdInventoryWarningService.findInventoryWarningByWareAndSpecId(WareHouseConsts.COMPANY_WARE_CODE, inventoryWarning.getSpecificationId());
            Integer companyInvenInventory = 0;
            if (companyInven==null){
                companyInvenInventory = 0;
            }else {
                companyInvenInventory = companyInven.getInventory();
            }
            //Integer num = Math.abs(wareGoodsVo.getInventory());
            oweList.add(getWareGoodsVo(inventoryWarning,shopGoods,goodsSpec,companyInvenInventory));
            specIdList.add(goodsSpec.getId());
        }

        List<MentionWareGoodsVo> goodsVoList = new ArrayList<MentionWareGoodsVo>();
        //所有展示商品
        List<ShopGoods> goodsList = shopGoodsService.findOweGoods();
        for (ShopGoods goods : goodsList) {
            List<ShopGoodsSpec> goodsSpecList = shopGoodsSpecService.findListByGoodsId(goods.getId());
            if (goodsSpecList.size()>0){
                for (ShopGoodsSpec goodsSpec : goodsSpecList) {

                    if (specIdList.size()>0){
                        boolean flag = specIdList.contains(goodsSpec.getId());//判断是否在欠货列表中
                        if (flag==false){
                            RdInventoryWarning wareInventory = rdInventoryWarningService.findInventoryWarningByWareAndSpecId(wareCode, goodsSpec.getId());
                            RdInventoryWarning companyInven = rdInventoryWarningService.findInventoryWarningByWareAndSpecId(WareHouseConsts.COMPANY_WARE_CODE, goodsSpec.getId());
                            Integer companyInvenInventory = 0;
                            if (companyInven==null){
                                companyInvenInventory = 0;
                            }else {
                                 companyInvenInventory = companyInven.getInventory();
                            }

                            if (wareInventory==null){
                                //添加数据
                                RdInventoryWarning inventoryWarning = new RdInventoryWarning();
                                inventoryWarning.setWareCode(warehouse.getWareCode());
                                inventoryWarning.setWareName(warehouse.getWareName());
                                inventoryWarning.setGoodsCode(goodsSpec.getGoodsId().toString());
                                inventoryWarning.setGoodsName(goods.getGoodsName());
                                inventoryWarning.setSpecificationId(goodsSpec.getId());
                                inventoryWarning.setSpecifications(goodsSpec.getSpecGoodsSpec());
                                inventoryWarning.setInventory(0);
                                inventoryWarning.setPrecautiousLine(0);
                                rdInventoryWarningService.save(inventoryWarning);

                                goodsVoList.add(getWareGoodsVo(inventoryWarning, goods, goodsSpec,companyInvenInventory));
                            }else {
                                goodsVoList.add(getWareGoodsVo(wareInventory, goods, goodsSpec,companyInvenInventory));
                            }
                        }

                    }else {
                        RdInventoryWarning wareInventory = rdInventoryWarningService.findInventoryWarningByWareAndSpecId(wareCode, goodsSpec.getId());
                        RdInventoryWarning companyInven = rdInventoryWarningService.findInventoryWarningByWareAndSpecId(WareHouseConsts.COMPANY_WARE_CODE, goodsSpec.getId());
                        Integer companyInvenInventory = 0;
                        if (companyInven==null){
                            companyInvenInventory = 0;
                        }else {
                            companyInvenInventory = companyInven.getInventory();
                        }
                        if (wareInventory==null){
                            //添加数据
                            RdInventoryWarning inventoryWarning = new RdInventoryWarning();
                            inventoryWarning.setWareCode(warehouse.getWareCode());
                            inventoryWarning.setWareName(warehouse.getWareName());
                            inventoryWarning.setGoodsCode(goodsSpec.getGoodsId().toString());
                            inventoryWarning.setGoodsName(goods.getGoodsName());
                            inventoryWarning.setSpecificationId(goodsSpec.getId());
                            inventoryWarning.setSpecifications(goodsSpec.getSpecGoodsSpec());
                            inventoryWarning.setInventory(0);
                            inventoryWarning.setPrecautiousLine(0);
                            rdInventoryWarningService.save(inventoryWarning);

                            goodsVoList.add(getWareGoodsVo(inventoryWarning, goods, goodsSpec,companyInvenInventory));
                        }else {
                            goodsVoList.add(getWareGoodsVo(wareInventory, goods, goodsSpec,companyInvenInventory));
                        }
                    }
                }
            }
        }

        if (oweList.size()==0 &&goodsVoList.size()==0){
            return ApiUtils.success(new ArrayList<MentionWareGoodsVo>());
        }

        if (specIdList.size()>0){
            oweList.addAll(goodsVoList);
            return ApiUtils.success(oweList);
        }else {
            return ApiUtils.success(goodsVoList);
        }



    }

    public MentionWareGoodsVo getWareGoodsVo(RdInventoryWarning wareInventory,ShopGoods goods,ShopGoodsSpec goodsSpec,Integer companyInvenInventory) {
        GoodsUtils.getSepcMapAndColImgToGoodsSpec(goods, goodsSpec);
        MentionWareGoodsVo wareGoodsVo = new MentionWareGoodsVo();
        wareGoodsVo.setWareCode(Optional.ofNullable(wareInventory.getWareCode()).orElse(""));
        wareGoodsVo.setGoodsName(Optional.ofNullable(goods.getGoodsName()).orElse(""));
        wareGoodsVo.setGoodsImage(Optional.ofNullable(goods.getGoodsImage()).orElse(""));
        wareGoodsVo.setSpecId(Optional.ofNullable(wareInventory.getSpecificationId()).orElse(0l));
        if (goods.getGoodsType()==3){
            wareGoodsVo.setSpecGoodsSpec(goodsSpec.getSpecGoodsSerial());
        }else{
            String specInfo = "";
            Map<String, String> map = goodsSpec.getSepcMap();
            //遍历规格map,取出键值对,拼接specInfo
            if (map != null) {
                Set<String> set = map.keySet();
                for (String str : set) {
                    specInfo += str + ":" + map.get(str) + "、";
                }
                specInfo = specInfo.substring(0, specInfo.length() - 1);
            }
            wareGoodsVo.setSpecGoodsSpec(specInfo);
        }
        wareGoodsVo.setGoodsRetailPrice(Optional.ofNullable(goods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
        wareGoodsVo.setGoodsMemberPrice(Optional.ofNullable(goods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
        wareGoodsVo.setPpv(Optional.ofNullable(goods.getPpv()).orElse(BigDecimal.ZERO));
        wareGoodsVo.setCostPrice(Optional.ofNullable(goodsSpec.getCostPrice()).orElse(new BigDecimal(100.00)));
        wareGoodsVo.setInventory(Optional.ofNullable(wareInventory.getInventory()).orElse(0));
        wareGoodsVo.setInventoryGc(Optional.ofNullable(companyInvenInventory).orElse(0));
        return wareGoodsVo;
    }


    /**
     * 进货订单详情
     */
    @RequestMapping(value = "/api/mention/allocationOrderInfo", method = RequestMethod.POST)
    @ResponseBody
    public String allocationOrderInfo(@RequestParam String orderSn,HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        if(orderSn==null || "".equals(orderSn)){
            return ApiUtils.error("订单号为空");
        }

        RdWareOrder wareOrder = rdWareOrderService.findBySn(orderSn);
        if (wareOrder==null){
            return ApiUtils.error("未找到该订单");
        }

        if (wareOrder.getOrderType()==8){//8调拨订单
            RdWareAllocation allocation = rdWareAllocationService.findBySn(orderSn);

            RdWarehouse rdWarehouse = rdWarehouseService.findByCode(allocation.getWareCodeIn());
            wareOrder.setProvinceCode(rdWarehouse.getProvinceCode());
            wareOrder.setCityCode(rdWarehouse.getCityCode());
            wareOrder.setCountryCode(rdWarehouse.getCountryCode());
            wareOrder.setWareDetial(rdWarehouse.getWareDetial());

            List<RdGoodsAdjustment> rdGoodsAdjustmentList = rdGoodsAdjustmentService.findByWidAndSign(allocation.getWId(),2);
            List<OrderGoodsVo> orderGoodsVos = new ArrayList<OrderGoodsVo>();
            for (RdGoodsAdjustment rdGoodsAdjustment : rdGoodsAdjustmentList) {

                ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(rdGoodsAdjustment.getSpecificationId());
                ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(rdGoodsAdjustment.getGoodId()));
                GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, goodsSpec);
                OrderGoodsVo orderGoodsVo = new OrderGoodsVo();
                orderGoodsVo.setGoodId(Optional.ofNullable(rdGoodsAdjustment.getGoodId()).orElse(0l));
                orderGoodsVo.setGoodsName(Optional.ofNullable(rdGoodsAdjustment.getGoodsName()).orElse(""));
                orderGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
                if (shopGoods.getGoodsType()==3){
                    orderGoodsVo.setGoodsSpec(goodsSpec.getSpecGoodsSerial());
                }else{
                    String specInfo = "";
                    Map<String, String> map = goodsSpec.getSepcMap();
                    //遍历规格map,取出键值对,拼接specInfo
                    if (map != null) {
                        Set<String> set = map.keySet();
                        for (String str : set) {
                            specInfo += str + ":" + map.get(str) + "、";
                        }
                        specInfo = specInfo.substring(0, specInfo.length() - 1);
                    }
                    orderGoodsVo.setGoodsSpec(specInfo);
                }
                orderGoodsVo.setStockInto(Optional.ofNullable(rdGoodsAdjustment.getStockInto()).orElse(0l));
                if (goodsSpec==null){
                    orderGoodsVo.setPpv(BigDecimal.ZERO);
                    orderGoodsVo.setGoodsRetailPrice(BigDecimal.ZERO);
                    orderGoodsVo.setGoodsMemberPrice(BigDecimal.ZERO);
                }else {
                    orderGoodsVo.setPpv(Optional.ofNullable(goodsSpec.getPpv()).orElse(BigDecimal.ZERO));
                    orderGoodsVo.setGoodsRetailPrice(Optional.ofNullable(goodsSpec.getSpecRetailPrice()).orElse(BigDecimal.ZERO));
                    orderGoodsVo.setGoodsMemberPrice(Optional.ofNullable(goodsSpec.getSpecMemberPrice()).orElse(BigDecimal.ZERO));
                }
                orderGoodsVo.setCostPrice(Optional.ofNullable(goodsSpec.getCostPrice()).orElse(new BigDecimal(100.00)));
                orderGoodsVo.setOweInventory(Optional.ofNullable(rdGoodsAdjustment.getStockOwe().intValue()).orElse(0));//欠货数量
                orderGoodsVo.setComeInventory(Optional.ofNullable(rdGoodsAdjustment.getStockInto().intValue()).orElse(0));//补货数量
                orderGoodsVos.add(orderGoodsVo);
            }
            if (orderGoodsVos.size()==0){
                return ApiUtils.error("该订单无调拨商品");
            }
            wareOrder.setOrderGoodsVoList(orderGoodsVos);
        }

        if (wareOrder.getOrderType()==9){//9后台发货
            return ApiUtils.error("该订单是调整单，请联系客服");
        }

        return ApiUtils.success(wareOrder);
    }

    /**
     * 调整单详情
     */
    @RequestMapping(value = "/api/mention/adjustInfo", method = RequestMethod.POST)
    @ResponseBody
    public String adjustInfo(@RequestParam Integer wid,HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        if(wid==null){
            return ApiUtils.error("调整编号为空");
        }

        RdWareAdjust rdWareAdjust = rdWareAdjustService.find(wid);
        if (rdWareAdjust==null){
            return ApiUtils.error("未找到该调整单");
        }

        List<RdGoodsAdjustment> rdGoodsAdjustmentList = rdGoodsAdjustmentService.findByWidAndSign(wid,1);
        List<OrderGoodsVo> orderGoodsVos = new ArrayList<OrderGoodsVo>();
        for (RdGoodsAdjustment rdGoodsAdjustment : rdGoodsAdjustmentList) {

            ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(rdGoodsAdjustment.getSpecificationId());
            ShopGoods shopGoods = shopGoodsService.find(Long.valueOf(rdGoodsAdjustment.getGoodId()));
            GoodsUtils.getSepcMapAndColImgToGoodsSpec(shopGoods, goodsSpec);

            OrderGoodsVo orderGoodsVo = new OrderGoodsVo();
            orderGoodsVo.setGoodId(Optional.ofNullable(rdGoodsAdjustment.getGoodId()).orElse(0l));
            orderGoodsVo.setGoodsName(Optional.ofNullable(rdGoodsAdjustment.getGoodsName()).orElse(""));
            orderGoodsVo.setGoodsImage(Optional.ofNullable(shopGoods.getGoodsImage()).orElse(""));
            if (shopGoods.getGoodsType()==3){
                orderGoodsVo.setGoodsSpec(goodsSpec.getSpecGoodsSerial());
            }else{
                String specInfo = "";
                Map<String, String> map = goodsSpec.getSepcMap();
                //遍历规格map,取出键值对,拼接specInfo
                if (map != null) {
                    Set<String> set = map.keySet();
                    for (String str : set) {
                        specInfo += str + ":" + map.get(str) + "、";
                    }
                    specInfo = specInfo.substring(0, specInfo.length() - 1);
                }
                orderGoodsVo.setGoodsSpec(specInfo);
            }
            orderGoodsVo.setStockInto(Optional.ofNullable(rdGoodsAdjustment.getStockInto()).orElse(0l));
            if (goodsSpec==null){
                orderGoodsVo.setPpv(BigDecimal.ZERO);
                orderGoodsVo.setGoodsRetailPrice(BigDecimal.ZERO);
                orderGoodsVo.setGoodsMemberPrice(BigDecimal.ZERO);
            }else {
                orderGoodsVo.setPpv(Optional.ofNullable(goodsSpec.getPpv()).orElse(BigDecimal.ZERO));
                orderGoodsVo.setGoodsRetailPrice(Optional.ofNullable(goodsSpec.getSpecRetailPrice()).orElse(BigDecimal.ZERO));
                orderGoodsVo.setGoodsMemberPrice(Optional.ofNullable(goodsSpec.getSpecMemberPrice()).orElse(BigDecimal.ZERO));
            }
            orderGoodsVos.add(orderGoodsVo);
        }
        if (orderGoodsVos.size()==0){
            return ApiUtils.error("该订单无调拨商品");
        }
        rdWareAdjust.setOrderGoodsVoList(orderGoodsVos);


        return ApiUtils.success(rdWareAdjust);
    }


    /**
     * 按自由创建发货单
     */
    @RequestMapping(value = "/api/mention/createOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createOrder(@RequestParam Map<Long, Integer> specIdNumMap, HttpServletRequest request) throws Exception {
        // 检查不为空
        for (Map.Entry<Long, Integer> entry : specIdNumMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                return ApiUtils.error(Xerror.PARAM_INVALID);
            }
        }

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        String mmCode = member.getMmCode();
        RdWarehouse warehouseIn = rdWarehouseService.findByMmCode(mmCode);
        if (warehouseIn == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouseIn.getWareCode();

        RdWareOrder rdWareOrder = new RdWareOrder();
        rdWareOrder.setId(twiterIdService.getTwiterId());
        String orderSn = "DH"+twiterIdService.getTwiterId();
        rdWareOrder.setOrderSn(orderSn);
        rdWareOrder.setStoreId(warehouseIn.getWareCode());
        rdWareOrder.setStoreName(warehouseIn.getWareName());
        rdWareOrder.setMCode(Optional.ofNullable(warehouseIn.getMmCode()).orElse(""));
        rdWareOrder.setConsigneeName(Optional.ofNullable(warehouseIn.getConsigneeName()).orElse(""));
        rdWareOrder.setWarePhone(Optional.ofNullable(warehouseIn.getWarePhone()).orElse(""));
        rdWareOrder.setOrderType(8);
        rdWareOrder.setOrderState(5);//待审
        RdSysPeriod nowPeriod = rdSysPeriodService.getPeriodService(new Date());
        if (nowPeriod==null){
            rdWareOrder.setCreationPeriod("");
        }else {
            rdWareOrder.setCreationPeriod(nowPeriod.getPeriodCode());
        }
        rdWareOrder.setCreateTime(new Date());
        rdWareOrder.setOrderDesc("欠货创建");
        rdWareOrder.setProvinceCode(Optional.ofNullable(warehouseIn.getProvinceCode()).orElse(""));
        rdWareOrder.setCityCode(Optional.ofNullable(warehouseIn.getCityCode()).orElse(""));
        rdWareOrder.setCountryCode(Optional.ofNullable(warehouseIn.getCountryCode()).orElse(""));
        rdWareOrder.setWareDetial(Optional.ofNullable(warehouseIn.getWareDetial()).orElse(""));


        RdWarehouse warehouseOut = rdWarehouseService.findByCode(WareHouseConsts.COMPANY_WARE_CODE);//仓库
        //调拨单
        RdWareAllocation wareAllocation = new RdWareAllocation();
        wareAllocation.setWareCodeIn(warehouseIn.getWareCode());
        wareAllocation.setWareNameIn(warehouseIn.getWareName());
        wareAllocation.setWareCodeOut(warehouseOut.getWareCode());
        wareAllocation.setWareNameOut(warehouseOut.getWareName());
        wareAllocation.setAttachAdd("");
        wareAllocation.setStatus(2);
        wareAllocation.setAutohrizeBy("");
        wareAllocation.setAutohrizeDesc("");

        //查找入库仓库为负数的商品
        /*List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareAllocation.getWareCodeIn());
        if (inventoryWarningList.size()==0){
            return ApiUtils.error("无欠货商品");
        }*/

        //创建调拨单和订单
        rdWareAllocationService.addAllocation(rdWareOrder,wareAllocation,specIdNumMap);

        return ApiUtils.success();
    }

    /**
     * 按欠货创建发货单
     */
    @RequestMapping(value = "/api/mention/createOweOrder", method = RequestMethod.POST)
    @ResponseBody
    public String createOweOrder( HttpServletRequest request) throws Exception {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        String mmCode = member.getMmCode();
        RdWarehouse warehouseIn = rdWarehouseService.findByMmCode(mmCode);
        if (warehouseIn == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouseIn.getWareCode();

        List<RdWareAllocation> list = rdWareAllocationService.haveAllocation(wareCode,2);
        if (list.size()>0){
            return ApiUtils.error("该会员自提仓库还有待审调拨单，请等待审核后再进行创建新的调拨单");
        }

        RdWareOrder rdWareOrder = new RdWareOrder();
        rdWareOrder.setId(twiterIdService.getTwiterId());
        String orderSn = "DH"+twiterIdService.getTwiterId();
        rdWareOrder.setOrderSn(orderSn);
        rdWareOrder.setStoreId(warehouseIn.getWareCode());
        rdWareOrder.setStoreName(warehouseIn.getWareName());
        rdWareOrder.setMCode(Optional.ofNullable(warehouseIn.getMmCode()).orElse(""));
        rdWareOrder.setConsigneeName(Optional.ofNullable(warehouseIn.getConsigneeName()).orElse(""));
        rdWareOrder.setWarePhone(Optional.ofNullable(warehouseIn.getWarePhone()).orElse(""));
        rdWareOrder.setOrderType(8);
        rdWareOrder.setOrderState(5);//待审
        RdSysPeriod nowPeriod = rdSysPeriodService.getPeriodService(new Date());
        if (nowPeriod==null){
            rdWareOrder.setCreationPeriod("");
        }else {
            rdWareOrder.setCreationPeriod(nowPeriod.getPeriodCode());
        }
        rdWareOrder.setCreateTime(new Date());
        rdWareOrder.setOrderDesc("欠货创建");
        rdWareOrder.setProvinceCode(Optional.ofNullable(warehouseIn.getProvinceCode()).orElse(""));
        rdWareOrder.setCityCode(Optional.ofNullable(warehouseIn.getCityCode()).orElse(""));
        rdWareOrder.setCountryCode(Optional.ofNullable(warehouseIn.getCountryCode()).orElse(""));
        rdWareOrder.setWareDetial(Optional.ofNullable(warehouseIn.getWareDetial()).orElse(""));


        RdWarehouse warehouseOut = rdWarehouseService.findByCode(WareHouseConsts.COMPANY_WARE_CODE);//仓库
        //调拨单
        RdWareAllocation wareAllocation = new RdWareAllocation();
        wareAllocation.setWareCodeIn(warehouseIn.getWareCode());
        wareAllocation.setWareNameIn(warehouseIn.getWareName());
        wareAllocation.setWareCodeOut(warehouseOut.getWareCode());
        wareAllocation.setWareNameOut(warehouseOut.getWareName());
        wareAllocation.setAttachAdd("");
        wareAllocation.setStatus(2);
        wareAllocation.setWareOrderSn(orderSn);
        wareAllocation.setAutohrizeBy("");
        wareAllocation.setAutohrizeDesc("");
        wareAllocation.setCreateTime(new Date());

        //查找入库仓库为负数的商品
        List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareAllocation.getWareCodeIn());
        if (inventoryWarningList.size()==0){
            return ApiUtils.error("无欠货商品");
        }

        //创建调拨单和订单
        rdWareAllocationService.addAllocationOwe(rdWareOrder,wareAllocation,inventoryWarningList);

        return ApiUtils.success();
    }

    /**
     * 按欠货创建发货单
     */
    @RequestMapping(value = "/api/mention/createOweOrderNew", method = RequestMethod.POST)
    @ResponseBody
    public String createOweOrderNew( HttpServletRequest request,String mapS) throws Exception {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        String mmCode = member.getMmCode();
        RdWarehouse warehouseIn = rdWarehouseService.findByMmCode(mmCode);
        if (warehouseIn == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouseIn.getWareCode();

        List<RdWareAllocation> list = rdWareAllocationService.haveAllocation(wareCode,2);
        if (list.size()>0){
            return ApiUtils.error("该会员自提仓库还有待审调拨单，请等待审核后再进行创建新的调拨单");
        }

        RdWareOrder rdWareOrder = new RdWareOrder();
        rdWareOrder.setId(twiterIdService.getTwiterId());
        String orderSn = "DH"+twiterIdService.getTwiterId();
        rdWareOrder.setOrderSn(orderSn);
        rdWareOrder.setStoreId(warehouseIn.getWareCode());
        rdWareOrder.setStoreName(warehouseIn.getWareName());
        rdWareOrder.setMCode(Optional.ofNullable(warehouseIn.getMmCode()).orElse(""));
        rdWareOrder.setConsigneeName(Optional.ofNullable(warehouseIn.getConsigneeName()).orElse(""));
        rdWareOrder.setWarePhone(Optional.ofNullable(warehouseIn.getWarePhone()).orElse(""));
        rdWareOrder.setOrderType(8);
        rdWareOrder.setOrderState(5);//待审
        RdSysPeriod nowPeriod = rdSysPeriodService.getPeriodService(new Date());
        if (nowPeriod==null){
            rdWareOrder.setCreationPeriod("");
        }else {
            rdWareOrder.setCreationPeriod(nowPeriod.getPeriodCode());
        }
        rdWareOrder.setCreateTime(new Date());
        rdWareOrder.setOrderDesc("欠货创建");
        rdWareOrder.setProvinceCode(Optional.ofNullable(warehouseIn.getProvinceCode()).orElse(""));
        rdWareOrder.setCityCode(Optional.ofNullable(warehouseIn.getCityCode()).orElse(""));
        rdWareOrder.setCountryCode(Optional.ofNullable(warehouseIn.getCountryCode()).orElse(""));
        rdWareOrder.setWareDetial(Optional.ofNullable(warehouseIn.getWareDetial()).orElse(""));


        RdWarehouse warehouseOut = rdWarehouseService.findByCode(WareHouseConsts.COMPANY_WARE_CODE);//仓库
        //调拨单
        RdWareAllocation wareAllocation = new RdWareAllocation();
        wareAllocation.setWareCodeIn(warehouseIn.getWareCode());
        wareAllocation.setWareNameIn(warehouseIn.getWareName());
        wareAllocation.setWareCodeOut(warehouseOut.getWareCode());
        wareAllocation.setWareNameOut(warehouseOut.getWareName());
        wareAllocation.setAttachAdd("");
        wareAllocation.setStatus(2);
        wareAllocation.setWareOrderSn(orderSn);
        wareAllocation.setAutohrizeBy("");
        wareAllocation.setAutohrizeDesc("");
        wareAllocation.setCreateTime(new Date());

        //查找入库仓库为负数的商品
        //List<RdInventoryWarning> inventoryWarningList = rdInventoryWarningService.findByWareCodeAndOweInven(wareAllocation.getWareCodeIn());
        JSONArray array = JSON.parseArray(mapS);
        if (array.size()==0){
            return ApiUtils.error("请选择欠货商品");
        }
        /*System.out.println("mapS="+mapS);
        System.out.println("array="+array);
        for (Object o : array) {
        System.out.println("o="+o);
            Long specId = 0l;
            Integer stockInto = 0;
            JSONObject jsonObject = JSON.parseObject(o.toString());
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                if (entry.getKey().equals("id")) {
                    specId = Long.valueOf(entry.getValue().toString());
                    System.out.println("id="+ specId);
                }
                if (entry.getKey().equals("inventory")) {
                    stockInto = Integer.valueOf(entry.getValue().toString());
                    System.out.println("inventory="+ stockInto);
                }
            }
        }*/

        //创建调拨单和订单
        rdWareAllocationService.addAllocationOweNew(rdWareOrder,wareAllocation,array);

        return ApiUtils.success();
    }

    /**
     * 创建发货单(提交订单)
     */
    @RequestMapping(value = "/api/mention/createOweOrder1", method = RequestMethod.POST)
    @ResponseBody
    public String createOweOrder1( HttpServletRequest request,String mapS) throws Exception {

        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);

        String mmCode = member.getMmCode();
        RdWarehouse warehouseIn = rdWarehouseService.findByMmCode(mmCode);
        if (warehouseIn == null ) {
            return ApiUtils.error("该会员没有自提仓库");
        }
        String wareCode = warehouseIn.getWareCode();

        List<RdWareAllocation> list = rdWareAllocationService.haveAllocation(wareCode,2);
        if (list.size()>0){
            return ApiUtils.error("该会员自提仓库还有待审调拨单，请等待审核后再进行创建新的调拨单");
        }

        RdWarehouse warehouseOut = rdWarehouseService.findByCode(WareHouseConsts.COMPANY_WARE_CODE);//仓库

        JSONArray array = JSON.parseArray(mapS);
        if (array.size()==0){
            return ApiUtils.error("请选择欠货商品");
        }

        //创建调拨单和订单
        SelfOrderSubmitResult result = rdWareAllocationService.addAllocationOwe1(warehouseIn,warehouseOut,array);

        RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        result.setIntegration(accountInfo.getWalletBlance().setScale(2));

        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        if (rdMmIntegralRule==null || rdMmIntegralRule.getShoppingPointSr()==null){
            result.setProportion(0d);
        }else{
            result.setProportion(rdMmIntegralRule.getShoppingPointSr().doubleValue()*0.01);
        }

        return ApiUtils.success(result);
    }

    /**
     * 
     */
    @RequestMapping(value = "/api/mention/wareOrderInfo", method = RequestMethod.POST)
    @ResponseBody
    public String wareOrderInfo(@RequestParam Long id,HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }

        if(id==null){
            return ApiUtils.error("订单id为空");
        }

        SelfOrderSubmitResult result = new SelfOrderSubmitResult();
        RdWareOrder wareOrder = rdWareOrderService.find(id);
        if (wareOrder==null){
            return ApiUtils.error("未找到该订单");
        }
        if (wareOrder.getUsePointNum()==null||wareOrder.getUsePointNum()==0){
            wareOrder.setUsePointFlag(0);
        }else {
            wareOrder.setUsePointFlag(1);
        }
        result.setFlagState(wareOrder.getFlagState());
        result.setWareOrder(wareOrder);
        RdMmAccountInfo accountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        result.setIntegration(accountInfo.getWalletBlance().setScale(2));

        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        if (rdMmIntegralRule==null || rdMmIntegralRule.getShoppingPointSr()==null){
            result.setProportion(0d);
        }else{
            result.setProportion(rdMmIntegralRule.getShoppingPointSr().doubleValue()*0.01);
        }

        return ApiUtils.success(result);
    }


    /**
     * 去付款
     *
     * @param paysn 支付订单编码
     * @param paymentCode 支付代码名称: ZFB YL weiscan
     * @param paymentId 支付方式索引id
     * @param integration 积分
     * @param paypassword 支付密码
     * @param paymentType 1在线支付 2货到付款
     */
    @RequestMapping("/api/mention/pay")
    @ResponseBody
    public String payOrder(@RequestParam(value = "paysn") String paysn,
                           @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
                           @RequestParam(defaultValue = "0") String paymentId,
                           @RequestParam(defaultValue = "0") String integration,
                           @RequestParam(defaultValue = "0") String paypassword,
                           @RequestParam(defaultValue = "1") Integer paymentType,
                           HttpServletRequest request) {

        /*if (paymentCode.equals("alipayMobilePaymentPlugin")){
            return ApiUtils.error("支付宝功能升级中，请先选用其他支付方式");
        }*/

        if(integration==null&&"".equals(integration)){
            return ApiUtils.error("请输入支付的积分金额");
        }
        int i = 0;
        try {
            String[] strings = integration.split("\\.");
            String string = strings[0];
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ApiUtils.error("输入积分数额有误");
        }
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
        RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
        //处理购物积分
        //获取购物积分购物比例
        List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
                .findList(Paramap.create().put("order", "RID desc"));
        RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
        if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
            rdMmIntegralRule = rdMmIntegralRuleList.get(0);
        }
        int shoppingPointSr = Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0);

        List<RdWareOrder> wareOrderList = rdWareOrderService.findByPaySn(paysn);
        if (CollectionUtils.isEmpty(wareOrderList)) {
            return ApiUtils.error("调拨订单不存在");
        }
        RdWareOrder wareOrder = wareOrderList.get(0);
        if (wareOrder.getFlagState()==0){
            return ApiUtils.error("该调拨订单不需要付款");
        }

        //if (integration != 0) {
        if (i != 0) {
            if (rdMmAccountInfo.getPaymentPwd() == null) {
                return ApiUtils.error("你还未设置支付密码");
            }
            if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
                return ApiUtils.error("支付密码错误");
            }
            if (rdMmAccountInfo.getWalletStatus() != 0) {
                return ApiUtils.error("购物积分账户状态未激活或者已被冻结");
            }
            ShopOrderPay pay = orderPayService.findBySn(paysn);
            System.out.println("p1="+pay);
            //处理积分支付
            rdWareOrderService.ProcessingIntegrals(paysn, i, shopMember, pay, shoppingPointSr);
        }

        System.out.println("##########################################");
        System.out
                .println("###  订单支付编号：" + paysn + "  |  支付方式名称：" + paymentCode + " |  支付方式索引id：" + paymentId + "#########");
        System.out.println("##########################################");
        ShopOrderPay pay = orderPayService.findBySn(paysn);
        System.out.println("p2="+pay);

        //货到付款判断
        if (paymentType == 2) {
            if (pay.getPaymentType() != 2) {
                return ApiUtils.error("该订单不是货到付款,请选择支付方式");
            }
        }
        PayCommon payCommon = new PayCommon();
        payCommon.setOutTradeNo(pay.getPaySn());
        if ("balancePaymentPlugin".equals(paymentCode)) {
            payCommon.setPayAmount(pay.getPayAmount());
        } else {
            //payCommon.setPayAmount(new BigDecimal(0.01));
            payCommon.setPayAmount(pay.getPayAmount());
        }
        payCommon.setTitle("订单支付");
        payCommon.setBody(pay.getPaySn() + "订单支付");
        if (paymentCode.equals("alipayMobilePaymentPlugin")){
            payCommon.setNotifyUrl(NotifyConsts.ADMIN_NOTIFY_FILE+ "/admin/paynotify/alipayNotify/"+paysn+".jhtml");
        }else {
            payCommon.setNotifyUrl(server + "/api/paynotify/notifyMobile/" + paymentCode + "/" + paysn + ".json");
        }
        payCommon.setReturnUrl(server + "/payment/payfront");
        String sHtmlText = "";
        Map<String, Object> model = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("alipayMobilePaymentPlugin")) {
            rdWareOrderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
            paymentTallyService.savePaymentTally(paymentCode, "支付宝", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            //修改订单付款信息
            sHtmlText = alipayMobileService.toPay(payCommon);//TODO
            model.put("tocodeurl", sHtmlText);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinMobilePaymentPlugin")) {
            //修改订单付款信息
            rdWareOrderService.updateByPaySn(paysn, Long.valueOf(paymentId));
            //保存支付流水记录
            paymentTallyService.savePaymentTally(paymentCode, "微信支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
            String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
            model.put("tocodeurl", tocodeurl);
            model.put("orderSn", pay.getOrderSn());
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("pointsPaymentPlugin")) {//积分全额支付
            //积分全额支付判断
            if (paymentCode.equals("pointsPaymentPlugin")) {
                if (pay.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
                    return ApiUtils.error("该订单不符合购物积分全抵现,请选择支付方式");
                }
            }
            paymentTallyService.savePaymentTally(paymentCode, "积分全抵扣", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = rdWareOrderService
                    .updateOrderpay(payCommon, member.getMmCode(), "在线支付-购物积分", paymentCode, paymentId);
            model.putAll(data);
        } else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("cashOnDeliveryPlugin")) {//货到付款
            // TODO: 2018/12/18
            /*paymentTallyService.savePaymentTally(paymentCode, "货到付款", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
            Map<String, Object> data = orderService
                    .updateOrderpay(payCommon, member.getMmCode(), "货到付款", paymentCode, paymentId);
            model.putAll(data);*/
        }

        return ApiUtils.success(model);
    }

    /**
     * 取消订单
     */
    @RequestMapping(value = "/api/mention/cancelWareOrder", method = RequestMethod.POST)
    @ResponseBody
    public String cancelWareOrder(@RequestParam long orderId, HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        // 更新订单状态与库存
        rdWareOrderService.updateCancelOrder(orderId, Constants.OPERATOR_MEMBER, member.getMmCode(),
                PaymentTallyState.PAYMENTTALLY_TREM_MB, "用户自己取消调拨订单","");
        return ApiUtils.success();
    }

    /**
     * 获取当前登录用户自提订单信息
     * @param request
     * @param orderState 20:待发货 30:待收货 40：已完成  100：查询其他状态订单
     * @param pager
     * @return
     */
    @RequestMapping(value = "/api/mention/orders.json")
    @ResponseBody
    public String getOrders(HttpServletRequest request,Integer orderState,@RequestParam(required = false,value = "selectName")String selectName,Pageable pager) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        int pageSize = pager.getPageSize();
        int pageNumber = (pager.getPageNumber()-1)*pageSize;
        List<ShopOrder> list=shopOrderService.findSelfOrderByPage(rdWarehouse,pageNumber,pageSize,orderState,selectName);
        HashMap<Long, List<ShopOrderGoods>> hashMap = new HashMap<>();
        Map<Long, String> addressMap = new HashMap<>();
        if(list!=null&&list.size()>0){
            for (ShopOrder shopOrder : list) {
                List<ShopOrderGoods> orderGoods = shopOrderGoodsService.findList(Paramap.create().put("orderId",shopOrder.getId()));
                ShopOrderAddress address = shopOrderAddressService.find(shopOrder.getAddressId());
                if (address==null){
                    addressMap.put(shopOrder.getId(),"用户未填写#WOMI#用户未填写");
                }else {
                    addressMap.put(shopOrder.getId(),address.getTrueName()+"#WOMI#"+address.getMobPhone());
                }
                hashMap.put(shopOrder.getId(),orderGoods);
            }
            return ApiUtils.success(SelfMentionOrderResult.buildList(list,hashMap,addressMap));
        }
        return ApiUtils.success(new ArrayList<SelfMentionOrderResult>());
    }


    /**
     * 确认提货
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/api/mention/confirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public String transactionCoupon(HttpServletRequest request, Long orderId) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("请登录后进行此操作");
        }

        ShopOrder order = shopOrderService.find(orderId);
        if(order==null){
            return ApiUtils.error("该订单不存在，请联系客服");
        }

        if (order.getOrderState()!= OrderState.ORDER_STATE_UNFILLED){
            if(order==null){
                return ApiUtils.error("该订单不是待提货状态，不可点击操作");
            }
        }

        order.setOrderState(OrderState.ORDER_STATE_NOT_RECEIVING);
        order.setShippingTime(new Date());

        RdWarehouse warehouse = rdWarehouseService.findByMmCode(member.getMmCode());
        /*RdWarehouse warehouse = null;
        //自提地址
        ShopOrderAddress orderAddress = shopOrderAddressService.find(order.getAddressId());
        if (orderAddress==null){
            return ApiUtils.error("该订单自提地址不存在");
        }else {
            Long addId = orderAddress.getMemberId();
            if (addId!=null){
                RdMmAddInfo rdMmAddInfo = rdMmAddInfoService.find(addId);
                List<RdWarehouse> rdWarehouseList = rdWarehouseService.findByMemberId(addId);
                if (rdWarehouseList.size()>0){
                    warehouse = rdWarehouseList.get(0);
                }
            }

        }*/

        if (warehouse==null){
            return ApiUtils.error("自提仓库不存在");
        }else {
            // 改自提点库存以及商品规格售出数量
            List<ShopOrderGoods> orderGoodsList = shopOrderGoodsService.listByOrderId(order.getId());//订单所有商品
            for (ShopOrderGoods orderGoods : orderGoodsList) {

                int goodsNum = orderGoods.getGoodsNum();//商品数量
                Long specId = orderGoods.getSpecId();//商品规格索引id

                ShopGoodsSpec shopGoodsSpec = shopGoodsSpecService.find(specId);
                ShopGoods shopGoods = shopGoodsService.find(orderGoods.getGoodsId());

                //新增发货单
                RdWareAdjust rdWareAdjust = new RdWareAdjust();
                rdWareAdjust.setWareCode(warehouse.getWareCode());
                rdWareAdjust.setWareName(warehouse.getWareName());
                rdWareAdjust.setAdjustType("SOT");
                rdWareAdjust.setStatus(3);
                rdWareAdjust.setAutohrizeBy("自提店"+member.getMmCode());
                rdWareAdjust.setAutohrizeTime(new Date());
                rdWareAdjust.setAutohrizeDesc("订单发货");
                rdWareAdjustService.insert(rdWareAdjust);

                //新增的发货商品详情
                RdGoodsAdjustment rdGoodsAdjustment = new RdGoodsAdjustment();
                rdGoodsAdjustment.setWid(rdWareAdjust.getWid());
                rdGoodsAdjustment.setSpecificationId(shopGoodsSpec.getId());
                rdGoodsAdjustment.setGoodId(shopGoodsSpec.getGoodsId());
                rdGoodsAdjustment.setGoodsName(shopGoods.getGoodsName());
                rdGoodsAdjustment.setSpecName(shopGoodsSpec.getSpecName());
                rdGoodsAdjustment.setGoodsSpec(shopGoodsSpec.getSpecGoodsSpec());
                rdGoodsAdjustment.setStockNow(shopGoodsSpec.getSpecGoodsStorage().longValue());
                rdGoodsAdjustment.setStockInto(Long.valueOf(orderGoods.getGoodsNum()));
                rdGoodsAdjustment.setCreateTime(shopGoods.getCreateTime());
                rdGoodsAdjustment.setWareCode(warehouse.getWareCode());
                rdGoodsAdjustment.setSign(1);
                rdGoodsAdjustment.setAutohrizeTime(new Date());
                rdGoodsAdjustment.setStatus(1L);
                rdGoodsAdjustmentService.insert(rdGoodsAdjustment);

                //查看该自提店是否有该规格商品数据
                Map<String,Object> haveMap = new HashMap<String,Object>();
                haveMap.put("wareCode",warehouse.getWareCode());
                haveMap.put("specificationId",specId);
                RdInventoryWarning rdInventoryWarning = rdInventoryWarningService.haveInventoryByWareCodeAndSpecId(haveMap);
                ShopGoodsSpec goodsSpec = shopGoodsSpecService.find(orderGoods.getSpecId());
                if (rdInventoryWarning==null){//不存在 生成条新的数据
                    RdInventoryWarning inventoryWarning = new RdInventoryWarning();
                    inventoryWarning.setWareCode(warehouse.getWareCode());
                    inventoryWarning.setWareName(warehouse.getWareName());
                    inventoryWarning.setGoodsCode(orderGoods.getGoodsId().toString());
                    inventoryWarning.setGoodsName(orderGoods.getGoodsName());
                    inventoryWarning.setSpecificationId(orderGoods.getSpecId());
                    inventoryWarning.setSpecifications(goodsSpec.getSpecGoodsSpec());
                    inventoryWarning.setInventory(-goodsNum);
                    inventoryWarning.setPrecautiousLine(0);
                    rdInventoryWarningService.saveIn(inventoryWarning);
                }else {//存在
                    rdInventoryWarningService.updateInventoryByWareCodeAndSpecId(warehouse.getWareCode(),specId,goodsNum);
                }
                //修改规格表中的售出数量
                /*goodsSpec.setSpecSalenum(goodsNum);
                shopGoodsSpecService.updateSpecSaleNum(goodsSpec);*/
            }
        }

        shopOrderService.update(order);

        return ApiUtils.success();
    }


    /**
     * 按照月份统计自提店订单数及销售额
     * @param request
     * @param month
     * @return
     */
    @RequestMapping(value = "/api/mention/orderStatistics.json")
    @ResponseBody
    public String orderStatistics(HttpServletRequest request,String month) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        if(StringUtil.isEmpty(month)){
            return ApiUtils.error("请选择需要查询的月份");
        }
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM");
            Date monthDate = format.parse(month);
            Calendar instance = Calendar.getInstance();
            instance.setTime(monthDate);
            instance.set(Calendar.DAY_OF_MONTH,1);
            Date firstDay = instance.getTime();
            instance.set(Calendar.DATE, instance.getActualMaximum(instance.DATE));
            Date lastDay = instance.getTime();
            java.text.SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String str1 = format1.format(firstDay);
            String str2 = format1.format(lastDay);
            String timeLeft=str1+" 00:00:00";
            String timeRight=str2+" 23:59:59";
            HashMap<String, Object> map = new HashMap<>();
            map.put("timeLeft",timeLeft);
            map.put("timeRight",timeRight);
            map.put("mentionId",rdWarehouse.getMentionId());
            SelfMentionOrderStatistics orderStatistics=shopOrderService.statisticsSelfOrderByTime(map);
            if(orderStatistics!=null&&orderStatistics.getOrderIncome()==null){
                orderStatistics.setOrderIncome(BigDecimal.ZERO);
            }
            return ApiUtils.success(orderStatistics);
        } catch (ParseException e) {
            e.printStackTrace();
            return ApiUtils.error("请传入正确的月份");
        }
    }

    /**
     * 按照月份查询自提店调拨单
     * @param request
     * @param month
     * @return
     */
    @RequestMapping(value = "/api/mention/month/wareAllocation.json")
    @ResponseBody
    public String getWareAllocationMonth(HttpServletRequest request,String month) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        if(StringUtil.isEmpty(month)){
            return ApiUtils.error("请选择需要查询的月份");
        }
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM");
            Date monthDate = format.parse(month);
            Calendar instance = Calendar.getInstance();
            instance.setTime(monthDate);
            instance.set(Calendar.DAY_OF_MONTH,1);
            Date firstDay = instance.getTime();
            instance.set(Calendar.DATE, instance.getActualMaximum(instance.DATE));
            Date lastDay = instance.getTime();
            java.text.SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String str1 = format1.format(firstDay);
            String str2 = format1.format(lastDay);
            String timeLeft=str1+" 00:00:00";
            String timeRight=str2+" 23:59:59";
            List<RdWareAllocation> list = rdWareAllocationService.findList(Paramap.create().put("searchTimeLeft",timeLeft).put("searchTimeRight",timeRight).put("wareCodeIn",rdWarehouse.getWareCode()));
            return ApiUtils.success(list);
        } catch (ParseException e) {
            e.printStackTrace();
            return ApiUtils.error("请传入正确的月份");
        }
    }
    /**
     * 按照月份查询自提店调整单
     * @param request
     * @param month
     * @return
     */
    @RequestMapping(value = "/api/mention/month/wareAdjust.json")
    @ResponseBody
    public String getRdWareAdjustMonth(HttpServletRequest request,String month) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        if(member==null){
            return ApiUtils.error("当前用户尚未登录");
        }
        String mmCode = member.getMmCode();//店主会员编号
        RdWarehouse rdWarehouse = rdWarehouseService.find("mmCode",mmCode);
        if(rdWarehouse==null){
            return ApiUtils.error("当前用户尚未开店");
        }
        if(StringUtil.isEmpty(month)){
            return ApiUtils.error("请选择需要查询的月份");
        }
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM");
            Date monthDate = format.parse(month);
            Calendar instance = Calendar.getInstance();
            instance.setTime(monthDate);
            instance.set(Calendar.DAY_OF_MONTH,1);
            Date firstDay = instance.getTime();
            instance.set(Calendar.DATE, instance.getActualMaximum(instance.DATE));
            Date lastDay = instance.getTime();
            java.text.SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String str1 = format1.format(firstDay);
            String str2 = format1.format(lastDay);
            String timeLeft=str1+" 00:00:00";
            String timeRight=str2+" 23:59:59";
            List<RdWareAdjust> list = rdWareAdjustService.findList(Paramap.create().put("searchTimeLeft",timeLeft).put("searchTimeRight",timeRight).put("wareCode",rdWarehouse.getWareCode()).put("adjustTypeS","其他入库出库"));
            return ApiUtils.success(list);
        } catch (ParseException e) {
            e.printStackTrace();
            return ApiUtils.error("请传入正确的月份");
        }
    }
}
