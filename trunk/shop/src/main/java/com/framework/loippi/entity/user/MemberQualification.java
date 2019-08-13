package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员资格表信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_dis_qualification")
public class MemberQualification implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    @Column(name = "id_rd_dis_qlf" )
    private int idRdDisQlf;//
    @Column(name = "PERIOD_CODE" )
    private String periodCode;//业务月份（YYYYMM），每一个业务月保留完整的会员关系、级别、业绩等数据
    @Column(name = "MM_CODE" )
    private String mCode;//会员编号
    @Column(name = "MM_NAME" )
    private String mName;//会员姓名
    @Column(name = "SPONSOR_CODE" )
    private String sponsorCode;//推荐人编号
    @Column(name = "SPONSOR_NAME" )
    private String sponsorName;//推荐人姓名
    @Column(name = "MM_STATUS" )
    private int mStatus;//当期会员的状态
    @Column(name = "RA_STATUS" )
    private int raStatus;//关联公司绑定状态 0:未绑定 1：已绑定
    @Column(name = "RA_SHOP_YN" )
    private int raShopYn;//老系统会员开店状态 0：未开店 1：已开店
    @Column(name = "PPV" )
    private BigDecimal ppv;//当期个人购买的PV
    @Column(name = "PPV_RETAIL" )
    private BigDecimal ppvRetail;//当期个人零售的PV，包括分享给普通顾客购买的PV
    @Column(name = "APPV_INIT" )
    private BigDecimal appvInit;//期初个人累计PV
    @Column(name = "APPV_FINAL" )
    private BigDecimal appvFinal;//期末个人累计PV
    @Column(name = "A_RETAIL_INIT" )
    private BigDecimal retailInit;//个人零售购买的期初值
    @Column(name = "A_RETAIL" )
    private BigDecimal retail;//个人零售购买额
    @Column(name = "A_RETAIL_FINAL" )
    private BigDecimal retailFinal;//个人零售购买的期末值
    @Column(name = "RANK_P2" )
    private int rankP2;//
    @Column(name = "RANK_P1" )
    private int rankP1;//
    @Column(name = "RANK_P0" )
    private int rankP0;//
    @Column(name = "RANK_AC" )
    private int rankAc;//当期计算后个人级别
    @Column(name = "RANK_RECORD_HIGH" )
    private int rankRecordHigh;//历史最高级别
    @Column(name = "LEAF_YN" )
    private int leafYn;//是否叶子节点\r\n0：不是\r\n1：是叶子节点
    @Column(name = "ORPHAN" )
    private Integer orphan;//是否孤儿 0：是  1：不是
    @Column(name = "LAYER" )
    private Integer layer;//所处层次，公司节点为0层，以下每层+1
    @Column(name = "G7PV" )
    private BigDecimal g7pv;//当期团队7层的PV
    @Column(name = "NPV" )
    private BigDecimal npv;//当期全网络的PV
    @Column(name = "GPV_FLAGSHIP" )
    private BigDecimal gpvFlagship;//达到旗舰店(7级)以上级别的人员，其同级以下团队的总业绩
    @Column(name = "GROUP_RANK_MAX" )
    private int groupRankMax;//团队最高级别（包括本人）
    @Column(name = "DD_RANK2_NUMBER" )
    private int ddRank2Number;//直接推荐代理的人数，代理级别为2
    @Column(name = "DL_RANK6_NUMBER" )
    private int dlRank6Number;//下属3级代理店网络数
    @Column(name = "DL_RANK7_NUMBER" )
    private int dlRank7Number;//下属旗舰店网络数
    @Column(name = "DL_RANK8_NUMBER" )
    private int dlRank8Number;//下属高级旗舰店网络数
    @Column(name = "PPV_QUALIFIED" )
    private int ppvqualified;//当月个人消费是否合格\r\n0：不合格\r\n1：合格
    @Column(name = "TOURISM_QUALIFIED" )
    private int tourismQualified;//当月旅游奖是否合格\r\n0：不合格\r\n1：合格
    @Column(name = "CAR_QUALIFIED" )
    private int carQualified;//当月车奖是否合格\r\n0：不合格\r\n1：合格
    @Column(name = "DIVIDEND_QUALIFIED" )
    private int dividendQualified;//当月分红奖是否合格\r\n0：不合格\r\n1：合格
}
