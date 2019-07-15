package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 留言消息记录表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_MEMBER_LEAVING_MESSAGE")
public class ShopMemberLeavingMessage implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 回复内容 */
	@Column(name = "reply_content" )
	private String replyContent;
	
	/** 回复对象 */
	@Column(name = "reply_object" )
	private String replyObject;
	
	/** 创建时间 */
	@Column(name = "create_time" )
	private java.util.Date createTime;
	
	/** 用户id */
	@Column(name = "uid" )
	private Long uid;
	
	/** 关联留言消息id */
	@Column(name = "biz_id" )
	private Long bizId;
	
	/** 回复对象类型 0平台 1用户 */
	@Column(name = "reply_object_type" )
	private Integer replyObjectType;
	
}
