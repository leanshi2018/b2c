package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import com.framework.loippi.utils.validator.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Entity - 信誉商品评价表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_goods_evaluate_like")
public class ShopGoodsEvaluate implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 评价ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 订单表自增ID
     */
    @Column(name = "geval_orderid")
    private Long gevalOrderid;

    /**
     * 订单编号
     */
    @Column(name = "geval_orderno")
    private String gevalOrderno;

    /**
     * 订单商品表编号(回复则为空)
     */
    @Column(name = "geval_ordergoodsid")
    private Long gevalOrdergoodsid;

    /**
     * 商品表编号
     */
    @Column(name = "geval_goodsid")
    private Long gevalGoodsid;

    /**
     * 商品名称
     */
    @Column(name = "geval_goodsname")
    private String gevalGoodsname;

    /**
     * 商品价格
     */
    @Column(name = "geval_goodsprice")
    private BigDecimal gevalGoodsprice;

    /**
     * 1-5分
     */
    @Column(name = "geval_scores")
    private Integer gevalScores;

    /**
     * 信誉评价内容
     */
    @Column(name = "geval_content")
    private String gevalContent;

    /**
     * 0表示不是 1表示是匿名评价
     */
    @Column(name = "geval_isanonymous")
    private Integer gevalIsanonymous;

    /**
     * 评价时间
     */
    @Column(name = "geval_addtime")
    private Date gevalAddtime;

    private String gevalAddtimeStr;

    /**
     * 评价人编号
     */
    @Column(name = "geval_frommemberid")
    private Long gevalFrommemberid;

    /**
     * 评价人名称
     */
    @Column(name = "geval_frommembername")
    private String gevalFrommembername;

    /**
     * 评价信息的状态 0为正常 1为禁止显示
     */
    @Column(name = "geval_state")
    private Integer gevalState;

    /**
     * 管理员对评价的处理备注
     */
    @Column(name = "geval_remark")
    private String gevalRemark;

    /**
     * 解释内容
     */
    @Column(name = "geval_explain")
    private String gevalExplain;

    /**
     * 晒单图片
     */
    @Column(name = "geval_image")
    private String gevalImage;

    private String[] gevalImageArr;

    /**
     * 0:未删除;1.已删除
     */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 评价获取的换购积分
     */
    @Column(name = "exchange_points")
    private Integer exchangePoints;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;
    /**
     * 管理员回复时间
     */
    @Column(name = "remark_time")
    private Date remarkTime;

    /**  */
    private String specInfo;

    /**
     * 0 没有父id
     */
    @Column(name = "parent_id")
    private Long parentId;

    //点赞数
    @Column(name = "like_num")
    private Integer likeNum;

    /*********************
     * 添加
     *********************/
    private String goodsImage;

    /**
     * 评价人头像
     */
    private String gevalFrommemberAvatar;

    private Long dispatchCity;

    /**
     * 评价该评价次数
     */
    private Integer replyNum;

    /**
     * 是否点赞过  0未点赞 1点赞过
     */
    private Integer isLiked;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 审批状态（1为审核中，2为显示，3为禁用）
     */
    private Integer checkedstatus;

    /**
     * 昵称
     */
    private String nick;
    //关键字id
    private String keyWordId;
    //关键字手机号
    private String keyWordMobile;
    //关键字订单号
    private String keyWordOrderId;
    //关键字商品id
    private String keyWordGoodsId;
    //关键字商品名称
    private String keyWordGoodsName;
    //查询条件开始时间
    private Long startTime;
    //查询条件结束时间
    private Long endTime;
    /**
     * 开始时间－页面字段
     */
    private Timestamp startTimeStr;
    /**
     * 结束时间－页面字段
     */
    private Timestamp endTimeStr;

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
        if (null != startTime) {
            this.startTimeStr = DateUtils.getTimestampByLong(startTime);
        }
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
        if (null != endTime) {
            this.endTimeStr = DateUtils.getTimestampByLong(endTime);
        }
    }

    /**
     * 评价人头像
     */
    private String memberImage;
    /**
     * 浏览数
     */
    private Long sumView;

    /**
     * 追评
     */
    private ShopGoodsEvaluate additionalEval;
    /**
     * 是否有追评
     */
    private Integer isAdditionalEval;
}
