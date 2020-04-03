//package com.framework.loippi.entity.user;
//
//import com.framework.loippi.mybatis.eitity.GenericEntity;
//import com.framework.loippi.mybatis.ext.annotation.Column;
//import com.framework.loippi.mybatis.ext.annotation.Table;
//import com.framework.loippi.util.validator.Words;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.List;
//
///**
// * Entity - 会员表
// *
// * @author zijing
// * @version 2.0
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "SHOP_MEMBER")
//public class ShopMember implements GenericEntity {
//
//    private static final long serialVersionUID = 5081846432919091193L;
//
//    /**
//     * 会员id
//     */
//    @Column(id = true, name = "id", updatable = false)
//    private Long id;
//
//    /**
//     * 会员名称
//     */
//    @Column(name = "member_name")
//    private String memberName;
//
//    /**
//     * 真实姓名
//     */
//    @Column(name = "member_truename")
//    private String memberTruename;
//
//    //身份证id
//    @Column(name = "member_trueid")
//    private String memberTrueid;
//
//    /**
//     * 会员头像
//     */
//    @Column(name = "member_avatar")
//    private String memberAvatar;
//
//    /**
//     * 会员性别
//     */
//    @Column(name = "member_sex")
//    private Integer memberSex;
//
//    /**
//     * 生日
//     */
//    @Column(name = "member_birthday")
//    private Date memberBirthday;
//
//    /**
//     * 会员密码
//     */
//    @Column(name = "member_passwd")
//    private String memberPasswd;
//
//    /**
//     * 支付密码
//     */
//    @Column(name = "payment_passwd")
//    private String paymentPasswd;
//
//    /**
//     * 会员邮箱
//     */
//    @Column(name = "member_email")
//    private String memberEmail;
//
//    /**
//     * qq
//     */
//    @Column(name = "member_qq")
//    private String memberQq;
//
//    /**
//     * 阿里旺旺
//     */
//    @Column(name = "member_ww")
//    private String memberWw;
//
//    /**
//     * 登录次数
//     */
//    @Column(name = "member_login_num")
//    private Integer memberLoginNum;
//
//    /**
//     * 会员注册时间
//     */
//    @Column(name = "create_time")
//    private Date createTime;
//
//    /**
//     * 当前登录时间
//     */
//    @Column(name = "member_login_time")
//    private Date memberLoginTime;
//
//    /**
//     * 上次登录时间
//     */
//    @Column(name = "member_old_login_time")
//    private Date memberOldLoginTime;
//
//    /**
//     * 当前登录ip
//     */
//    @Column(name = "member_login_ip")
//    private String memberLoginIp;
//
//    /**
//     * 上次登录ip
//     */
//    @Column(name = "member_old_login_ip")
//    private String memberOldLoginIp;
//
//    /**  */
//    @Column(name = "member_openid")
//    private String memberOpenid;
//
//    /**  */
//    @Column(name = "member_info")
//    private String memberInfo;
//
//    /**
//     * 会员购物积分
//     */
//    @Column(name = "member_consume_points")
//    private BigDecimal memberConsumePoints;
//
//    /**
//     * 会员等级积分
//     */
//    @Column(name = "member_rank_points")
//    private Integer memberRankPoints;
//
//    /**
//     * 预存款可用金额
//     */
//    @Column(name = "available_predeposit")
//    private BigDecimal availablePredeposit;
//
//    /**
//     * 预存款冻结金额
//     */
//    @Column(name = "freeze_predeposit")
//    private BigDecimal freezePredeposit;
//
//    /**
//     * 红包余额冻结金额
//     */
//    @Column(name = "freeze_hb_balance")
//    private BigDecimal freezeHbBalance;
//
//    /**
//     * 是否允许举报(1可以/2不可以)
//     */
//    @Column(name = "inform_allow")
//    private Integer informAllow;
//
//    /**
//     * 会员是否有购买权限 1为开启 0为关闭
//     */
//    @Column(name = "is_buy")
//    private Integer isBuy;
//
//    /**
//     * 会员是否有咨询和发送站内信的权限 1为开启 0为关闭
//     */
//    @Column(name = "is_allowtalk")
//    private Integer isAllowtalk;
//
//    /**
//     * 会员的开启状态 1为开启 0为关闭
//     */
//    @Column(name = "member_state")
//    private Integer memberState;
//
//    /**
//     * 会员信用
//     */
//    @Column(name = "member_credit")
//    private Integer memberCredit;
//
//    /**
//     * sns空间访问次数
//     */
//    @Column(name = "member_share_num")
//    private Integer memberShareNum;
//
//    /**
//     * 地区ID
//     */
//    @Column(name = "member_areaid")
//    private String memberAreaid;
//
//    /**
//     * 城市ID
//     */
//    @Column(name = "member_cityid")
//    private String memberCityid;
//
//    /**
//     * 省份ID
//     */
//    @Column(name = "member_provinceid")
//    private String memberProvinceid;
//
//    /**
//     * 地区内容
//     */
//    @Column(name = "member_areainfo")
//    private String memberAreainfo;
//    /**
//     * 地址内容
//     */
//    @Column(name = "member_address")
//    private String memberAddress;
//
//    /**
//     * 隐私设定
//     */
//    @Column(name = "member_privacy")
//    private String memberPrivacy;
//
//    /**
//     * 删除标志
//     */
//    @Column(name = "is_del")
//    private Integer isDel;
//
//    /**  */
//    @Column(name = "sign_code")
//    private String signCode;
//
//    /**  */
//    @Column(name = "sign_code_state")
//    private String signCodeState;
//
//    /**
//     * 手机号
//     */
//    @Column(name = "member_mobile")
//    private String memberMobile;
//
//    /**
//     * 会员等级id
//     */
//    @Column(name = "member_gradeid")
//    private Long memberGradeid;
//
//    /**
//     * 用户类型 1-身份证 2-护照 3-军官证 4-回乡证
//     */
//    @Column(name = "user_type")
//    private Integer userType;
//    /**
//     * 用户类型
//     */
//    @Column(name = "member_type")
//    private Integer memberType;
//
//    /**
//     * 会员升级日期
//     */
//    @Column(name = "gradeDate")
//    private Date gradedate;
//
//    /**
//     * 来自什么平台注册默认0 pc, 1 安卓 , 2 ios
//     */
//    @Column(name = "member_identification")
//    private Integer memberIdentification;
//
//    /**
//     * 第三方默认0 本站，1微信，2QQ，3新浪
//     */
//    @Column(name = "member_third_party")
//    private Integer memberThirdParty;
//
//    /**
//     * 会员邀请码
//     */
//    @Column(name = "member_invitation_code")
//    private String memberInvitationCode;
//
//    /**
//     * 邀请人的邀请码
//     */
//    @Column(name = "member_invitation_code_from")
//    private String memberInvitationCodeFrom;
//
//    /**
//     * 邀请人的Id
//     */
//    @Column(name = "member_from")
//    private Long memberFrom;
//
//    /**
//     * 会员角色：1：会员，2：店铺和会员, 3-企业用户
//     */
//    @Column(name = "member_role")
//    private String memberRole;
//
//    /**
//     * 分组ID
//     */
//    @Column(name = "member_groupid")
//    private Long memberGroupid;
//    /** 等级
//	 */
//    @Column(name = "member_level")
//    private Integer memberLevel;
//    /**
//     * 接收消息通知（1 表示接收 0或者空表示不接收通知）
//     */
//    @Column(name = "is_receive")
//    private Integer isReceive;
//
//    @Column(name = "hid")
//    private String hid;
//
//    // 用于查询的字段,补全字段
//
//    /**
//     * 临时用来搜索最后登录时间
//     */
//    private Long starttimeLong;
//    private Long endtimeLong;
//    /**
//     * 上次登录时间
//     */
//    private Timestamp memberOldLoginTimestr;
//
//    /**
//     * 上次登录时间
//     */
//    private String orderString;
//
//    /**
//     * 会员分组名称
//     */
//    private String memberGroupName;
//
//    /**
//     * 会员生日－页面字段
//     */
//    private Timestamp memberBirthdaystr;
//    /**
//     * 创建时间－页面字段
//     */
//    private Timestamp createTimeStr;
//    /**
//     * 当前登录时间－页面字段
//     */
//    private Timestamp memberLoginTimestr;
//    /**
//     * 状态查询
//     */
//    @Words(field = "状态查询", message = "状态查询包含敏感词")
//    private String status;
//    /**
//     * 临时用来前台判断认证状态
//     */
//    private String authcStatus;
//    /**
//     * 红包余额
//     */
//    @Column(name = "red_packet_balance")
//    private BigDecimal redPacketBalance;
//
//    private String openId;
//
//    /**
//     * 第三方登录微信openid
//     */
//    private String weixinOpenid;
//    /**
//     * 第三方登录QQ openid
//     */
//    private String qqOpenid;
//    /**
//     * 第三方登录微博openid
//     */
//    private String weiboOpenid;
//
//    /**
//     * 1专场新人 2专场新人已下单 3专场老人
//     */
//    private Integer isSpecialNewPeople;
//
//    /**
//     * sessionId
//     */
//    @Column(name = "session_id")
//    private String sessionId;
//
//    private List<Long> memberIds;
//}
