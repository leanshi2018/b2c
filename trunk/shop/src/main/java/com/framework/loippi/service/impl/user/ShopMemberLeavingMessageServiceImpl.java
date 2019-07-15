package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.ShopMemberLeavingMessageDao;
import com.framework.loippi.entity.user.ShopMemberLeavingMessage;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.ShopMemberLeavingMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * SERVICE - ShopMemberLeavingMessage(留言消息记录表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class ShopMemberLeavingMessageServiceImpl extends GenericServiceImpl<ShopMemberLeavingMessage, Long> implements ShopMemberLeavingMessageService {
	
	@Autowired
	private ShopMemberLeavingMessageDao shopMemberLeavingMessageDao;
	@Autowired
	private TwiterIdService twiterIdService;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopMemberLeavingMessageDao);
	}

	@Override
	public void saveLeavingMessage(String replyContent, Integer replyObjectType, Long bizId, Long memberId) {
		ShopMemberLeavingMessage shopMemberLeavingMessage=new ShopMemberLeavingMessage();
		shopMemberLeavingMessage.setBizId(bizId);
		shopMemberLeavingMessage.setCreateTime(new Date());
		shopMemberLeavingMessage.setId(twiterIdService.getTwiterId());
		shopMemberLeavingMessage.setReplyContent(replyContent);
		shopMemberLeavingMessage.setReplyObjectType(replyObjectType);
		if (replyObjectType==0){
			shopMemberLeavingMessage.setReplyObject("平台回复");
		}else{
			shopMemberLeavingMessage.setReplyObject("用户回复");
		}
		shopMemberLeavingMessage.setUid(memberId);
		shopMemberLeavingMessageDao.insert(shopMemberLeavingMessage);
	}
}
