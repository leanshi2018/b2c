//package com.framework.loippi.dao.user;
//
//import com.framework.loippi.entity.user.ShopMember;
//import com.framework.loippi.entity.user.ShopMemberRegisterNum;
//import com.framework.loippi.mybatis.dao.GenericDao;
//import com.framework.loippi.mybatis.paginator.domain.PageBounds;
//import com.framework.loippi.mybatis.paginator.domain.PageList;
//import com.framework.loippi.vo.stats.StatsCountVo;
//import com.framework.loippi.vo.user.UserInfoDto;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * DAO - ShopMember(会员表)
// *
// * @author zijing
// * @version 2.0
// */
//public interface ShopMemberDao extends GenericDao<ShopMember, Long> {
//
//    //登录时验证用户是否存在
//    ShopMember findMemberExist(ShopMember member);
//
//    //注册时验证用户是否存在
//    int findMemberExistCount(ShopMember member);
//
//    //分页查询List
//    PageList<ShopMember> findMemberListIsLike(Object var1, PageBounds var2);
//
//    //根据第三方聊天id获取用户信息
//    List<UserInfoDto> listByHids(@Param("uid") Long uid, @Param("hids") String[] hids);
//
//    //统计
//    List<ShopMemberRegisterNum> statisticsRegisterNum(ShopMemberRegisterNum shopMemberRegisterNum);
//
//    //解绑第三方登录
//    void unbindThirdparty(@Param("type") Integer type, @Param("id") Long userId);
//
//    // 统计订单-昨日 前日 上周 上上周, 上月, 上上月注册用户数量
//    List<StatsCountVo> listStatsCountVo();
//
//    //批量更新用户数据
//    void updateBatch(List<ShopMember> memberList);
//
//    List<ShopMember> selectMemberByParam(Map<String, Object> map);
//
//    //获取用户分享总数
//    int sumShare();
//}
