package com.framework.loippi.entity.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 促销活动基本信息Entity
 *
 * @author longbh
 * @version 2018-09-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ACTIVITY")
public class ShopActivity implements GenericEntity, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动id
     */
    private Long id;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 主标题
     */
    @Column(name = "ACTIVITY_NAME")
    private String activityName;

    /**
     * 副标题
     */
    @Column(name = "sub_activity_name")
    private String subActivityName;

    /**
     * 副标题
     */
    @Column(name = "ACTIVITY_DESCRIPTION")
    private String activityDescription;

    /**
     * 活动小图
     */
    @Column(name = "ACTIVITY_PICTURE")
    private String activityPicture;

    /**
     * 活动大图
     */
    @Column(name = "activity_picture_pc")
    private String activityPicturePc;

    /**
     * 排序数字
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 活动开始时间
     */
    @Column(name = "START_TIME")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @Column(name = "END_TIME")
    private Date endTime;

    /**
     * 是否推荐  0否 1是
     */
    @Column(name = "is_recommend")
    private Integer isRecommend;

    /**
     * 是否包邮 0否 1是
     */
    @Column(name = "is_free_shipping")
    private Integer isFreeShipping;

    /**
     * 活动状态  10  未开始   11-报名中  12报名结束  20 活动中 30 已结束
     */
    @Column(name = "ACTIVITY_STATUS")
    private Integer activityStatus;

    //商品类型
    @Column(name = "goods_type")
    private Integer goodsType;

    /**
     * //促销活动  1    //团购 50  //限时抢 60  //优惠券 90
     * 直接对应促销规则中的活动类型
     * /满减/满免邮/满送/满折/团购/限时抢购
     * 满减10/满免邮20/满送30/满折40/团购50/限时抢购60/满免税70/满免邮免税80/满任选90/满送卷100/折扣110/一口价120
     */
    @Column(name = "ACTIVITY_TYPE")
    private Integer activityType;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 活动规则id
     */
    @Column(name = "PROMOTION_RULE_ID")
    private Long promotionRuleId;

    /**
     * 基础数量
     */
    @Column(name = "already_buy_number")
    private Integer alreadyBuyNumber;

    /**
     * 审核状态
     * 0 禁用 1开启
     */
    @Column(name = "AUDIT_STATUS")
    private Integer auditStatus;

    /**
     * 分类id-秒杀则为场次
     */
    @Column(name = "activity_class_id")
    private Long activityClassId;

    /**
     * 活动涉及的会员范围
     * 1全部会员
     * 2指定范围的会员
     */
    @Column(name = "member_type")
    private Integer memberType;

    /**
     * 10单品促销-- 20组合促销--30 整单促销-- 40全部订单促销
     */
    private Integer promotionType;

    /**
     * 其他非数据库字段，以后要移动到ShopActivityVo中去
     */
    /**
     * 团购／活动等子分类
     */
    private String activityClass;

    private int orderBy = 0;

    /**
     * 优惠规则集合
     */
    private List<ShopActivityPromotionRule> shopActivityPromotionRule;

    /**
     * 当前活动时间
     */
    private Date activityTime;

    /**
     * 当天活动开始时间
     */
    private Date todayStartTime;

    /**
     * 商品规格id
     */
    private Long goodsSpecId;
    /**
     * 商品所属分组id
     */
    private Long groupId;

    /**
     * 商品名称
     */
    private String goodsName;

    //查询时返回已经加入的活动
    private Long joinStoreId;

    private Integer getNum = 0;
    private Integer useNum = 0;
    private List<Long> ids;
    private List<Long> activityStatusList;
    //促销规则
    private Integer limitType;
    private String limitWhere;
    private String couponSource;
    private Integer ruleType;
    private Integer restrictionNum;
    private List<Long> promotionRuleIds;
    private String searchStartTime;
    private String searchEndTime;
    @Override
    public ShopActivity clone() {
        ShopActivity shopActivity = null;
        try {
            shopActivity = (ShopActivity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return shopActivity;
    }

}