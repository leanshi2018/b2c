//package com.framework.loippi.service.user;
//
//import com.framework.loippi.entity.PayCommon;
//import com.framework.loippi.entity.user.ShopMember;
//import com.framework.loippi.entity.user.ShopMemberRegisterNum;
//import com.framework.loippi.service.GenericService;
//import com.framework.loippi.support.Page;
//import com.framework.loippi.support.Pageable;
//import com.framework.loippi.vo.stats.StatsCountVo;
//import com.framework.loippi.vo.user.UserInfoDto;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 项目名称：leimingtech-admin
// * 类名称：ShopMemberService
// * 类描述：
// * 修改备注：
// */
//public interface ShopMemberService extends GenericService<ShopMember, Long> {
//
//    // 登录时验证用户是否存在
//    ShopMember findMemberExist(ShopMember member);
//
//    //注册时验证用户是否存在
//    int findMemberExistCount(ShopMember member);
//
//    //分页查询List
//    Page<ShopMember> findMemberListIsLike(Pageable pageable);
//
//    List<UserInfoDto> listByHids(Long uid, String[] hids);
//
//    List<ShopMemberRegisterNum> statisticsRegisterNum(ShopMemberRegisterNum shopMemberRegisterNum);
//
//    //解绑三方
//    void unbindThirdparty(Integer type, Long userId);
//
//    /**
//     * 统计订单-昨日 前日 上周 上上周, 上月, 上上月注册用户数量
//     */
//    List<StatsCountVo> listStatsCountVo();
//
//    //批量更新
//    void updateBatch(List<ShopMember> memberList);
//
//    //批量查询
//    List<ShopMember> findShopMember(List<Long> memberIds);
//
//    //批量查询转map
//    Map<Long,ShopMember> findShopMemberMap(List<Long> memberIds);
//
//    /**
//     *  更新用户表同时更新客户提供的用户表等相关信息
//     * @param member  用户信息
//     * @param type  更新类型
//     */
//    void updateMember(ShopMember member,Integer type);
//
//    /**
//     * 获取用户分享总数
//     * @return
//     */
//    int sumShare();
//
//}
