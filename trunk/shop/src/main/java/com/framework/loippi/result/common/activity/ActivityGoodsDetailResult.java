package com.framework.loippi.result.common.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.StringUtil;


/*
 * Result - 商品详情   活动使用的商品详情信息
 *
 * @author Loippi team
 * @version 2.0
 * @description 商品详情
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGoodsDetailResult {


    /**
     * 商品id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long goodsIs;

    /**
     * 默认选中的规则id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long specId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品轮播图
     */
    private String listImage;

    /**
     * 零售价
     */
    private BigDecimal goodsRetailPrice;
    /**
     * 零售价 高
     */
    private BigDecimal goodsRetailPriceHigh;

    /**
     * 会员价格
     */
    private BigDecimal goodsMemberPrice;
    /**
     * 会员价格 高
     */
    private BigDecimal goodsMemberPriceHigh;

    /**
     * ppv
     */
    private BigDecimal ppv;
    /**
     * ppv 高
     */
    private BigDecimal ppvHigh;
    /**
     * 商品描述
     */
    private String goodsSubtitle;
    /**
     * 商品促销语
     */
    private String goodsKeywords;
    /**
     * 售出数量
     */

    private Integer saleNum;

    /**
     * 商品收藏数量
     */
    private Integer goodsCollect = 0;

    //当前会员是否收藏 0适合收藏,1是未收藏
    private Integer favGoods = 0;


    //商品详情,手机版
    private String mobileBody;

    //活动商品总数量
    private Long stockNumber;

    private List<ActivityGoodsSpesDescItem> specItemList;

    private List<ActivityGoodsSkuInfo> products;

    //评价
    private List<ActivityEvaluateGoodsResult> evaluateList;

    private Long evaluateCount;

    private String shareUrl;

    //默认的商品展示图片
    private String defaultImage;

    //聊天帐号id
    private String chatAccount;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long brandId;
    /**
     * 图片
     */
    private String brandPic;
    /**
     * 星级
     */
    private Integer stars;

    //精品图片
    private Boolean isRecomend = false;
    private String recommendImages;

    private Integer goodsType;
    /**
     * 好评率
     */
    private Double evaluateRate;

    /**
     * 品牌描述
     */
    private String description;

    /**
     * 满减运费金额
     */
    private BigDecimal shippingCouponAmount;

    /**
     * 关键字集合
     */
    List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList=new ArrayList<ShopGoodsEvaluateKeywords>();

    public static ActivityGoodsDetailResult build(ShopGoods item, List<ShopGoodsSpec> shopGoodsSpecs, String prefix, String wapServer) {
        ActivityGoodsDetailResult result = new ActivityGoodsDetailResult();
        Optional<ActivityGoodsDetailResult> opt = Optional.ofNullable(result);
        result.setGoodsIs(Optional.ofNullable(item.getId()).orElse(0L));//商品id
        result.setClassId(Optional.ofNullable(item.getGcId()).orElse(-1L));//分类id
        result.setGoodsName(Optional.ofNullable(item.getGoodsName()).orElse(""));//商品名称
        result.setGoodsKeywords(Optional.ofNullable(item.getGoodsKeywords()).orElse(""));//商品促销语
        result.setGoodsSubtitle(Optional.ofNullable(item.getGoodsSubtitle()).orElse(""));//商品副标题,描述
        if (shopGoodsSpecs.size()==0 || shopGoodsSpecs.size()==1){
            result.setGoodsRetailPrice(Optional.ofNullable(item.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));//销售价
            result.setGoodsRetailPriceHigh(Optional.ofNullable(item.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
            result.setGoodsMemberPrice(Optional.ofNullable(item.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));//销售价
            result.setGoodsMemberPriceHigh(Optional.ofNullable(item.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
            result.setPpv(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));//ppv
            result.setPpvHigh(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));
        }else {
            BigDecimal retailPrice = Optional.ofNullable(item.getGoodsRetailPrice()).orElse(BigDecimal.ZERO);
            BigDecimal retailPriceHigh = Optional.ofNullable(item.getGoodsRetailPrice()).orElse(BigDecimal.ZERO);
            BigDecimal memberPrice = Optional.ofNullable(item.getGoodsMemberPrice()).orElse(BigDecimal.ZERO);
            BigDecimal memberPriceHigh = Optional.ofNullable(item.getGoodsMemberPrice()).orElse(BigDecimal.ZERO);
            BigDecimal ppv = Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO);
            BigDecimal ppvHigh = Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO);
            for (ShopGoodsSpec shopGoodsSpec : shopGoodsSpecs) {
                if (shopGoodsSpec.getSpecRetailPrice().compareTo(item.getGoodsRetailPrice())==1){//大于
                    retailPriceHigh = shopGoodsSpec.getSpecRetailPrice();
                }else if (shopGoodsSpec.getSpecRetailPrice().compareTo(item.getGoodsRetailPrice())==-1){//小于
                    retailPrice = shopGoodsSpec.getSpecRetailPrice();
                }
                if (shopGoodsSpec.getSpecMemberPrice().compareTo(item.getGoodsMemberPrice())==1){//大于
                    memberPriceHigh = shopGoodsSpec.getSpecMemberPrice();
                }else if (shopGoodsSpec.getSpecMemberPrice().compareTo(item.getGoodsMemberPrice())==-1){//小于
                    memberPrice = shopGoodsSpec.getSpecMemberPrice();
                }
                if (shopGoodsSpec.getPpv().compareTo(item.getPpv())==1){//大于
                    ppvHigh = shopGoodsSpec.getPpv();
                }else if (shopGoodsSpec.getPpv().compareTo(item.getPpv())==-1){//小于
                    ppv = shopGoodsSpec.getPpv();
                }
            }
            result.setGoodsRetailPrice(retailPrice);//销售价
            result.setGoodsRetailPriceHigh(retailPriceHigh);
            result.setGoodsMemberPrice(memberPrice);//销售价
            result.setGoodsMemberPriceHigh(memberPriceHigh);
            result.setPpv(ppv);//ppv
            result.setPpvHigh(ppvHigh);


        }

        result.setGoodsType(Optional.ofNullable(item.getGoodsType()).orElse(1));
        result.setSpecId(Optional.ofNullable(item.getSpecId()).orElse(-1L));//规格id
        result.setListImage(Optional.ofNullable(prefix + item.getGoodsImageMore()).orElse(""));//轮播图
        result.setDefaultImage(Optional.ofNullable(prefix + item.getGoodsImage()).orElse(""));
        result.setEvaluateRate(Optional.ofNullable(item.getEvaluaterate()).orElse(1D));//好评率
        //手机端详情拼接
        String mobileBody = StringEscapeUtils.unescapeHtml4(item.getMobileBody());
        if (!StringUtil.isEmpty(mobileBody)) {
            List<Map> mobileBodyMap = JacksonUtil.convertList(mobileBody, Map.class);
            StringBuilder strBuilder = new StringBuilder("");
            for (Map<String, String> mobileBodyItem : mobileBodyMap) {
                if (mobileBodyItem.get("type").equals("image")) {
                    strBuilder.append("<img src='" + prefix + mobileBodyItem.get("value") + "' width='100%'>");
                }
                if (mobileBodyItem.get("type").equals("text")) {
                    strBuilder.append("<p>" + mobileBodyItem.get("value") + "</p>");
                }
            }
            result.setMobileBody(Optional.ofNullable(prefix + strBuilder.toString()).orElse(""));
        }
        result.setGoodsCollect(Optional.ofNullable(item.getGoodsCollect()).orElse(0));
        result.setSaleNum(Optional.ofNullable(item.getSalenum()).orElse(0));
        result.setStockNumber(Optional.ofNullable(item.getStock()).orElse(0l));
        // 市场价不受活动影响
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append(wapServer);
        shareUrl.append("/wap/goods/detail/");
        shareUrl.append(item.getId());
        shareUrl.append(".html");
        result.setShareUrl(shareUrl.toString());
        return result;
    }


}
