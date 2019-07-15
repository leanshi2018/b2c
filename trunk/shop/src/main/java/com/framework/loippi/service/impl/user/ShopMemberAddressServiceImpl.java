//package com.framework.loippi.service.impl.user;
//
//import com.framework.loippi.service.impl.GenericServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.framework.loippi.dao.user.ShopMemberAddressDao;
//import com.framework.loippi.entity.user.ShopMemberAddress;
//import com.framework.loippi.service.user.ShopMemberAddressService;
//
///**
// * SERVICE - ShopMemberAddress(买家地址信息表)
// *
// * @author zijing
// * @version 2.0
// */
//@Service
//public class ShopMemberAddressServiceImpl extends GenericServiceImpl<ShopMemberAddress, Long> implements ShopMemberAddressService {
//
//    @Autowired
//    private ShopMemberAddressDao shopMemberAddressDao;
//
//    @Autowired
//    public void setGenericDao() {
//        super.setGenericDao(shopMemberAddressDao);
//    }
//
//    public int updateDef(Long addressId, Long memberId) {
//        int result = 0;
//        if (addressId != null) {
//            ShopMemberAddress address = new ShopMemberAddress();
//            address.setMemberId(memberId);
//            address.setIsDefault(0);
//            shopMemberAddressDao.updateMember(address);
//            address.setMemberId(memberId);
//            address.setId(addressId);
//            address.setIsDefault(1);
//            shopMemberAddressDao.update(address);
//            result = 1;
//        }
//        return result;
//    }
//
//}
