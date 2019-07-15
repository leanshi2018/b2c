package com.framework.loippi.controller.admin.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.framework.loippi.entity.Acl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AclDTO {
	
	
	private String id ;
	
	private String text;
	
	private String icon;
	
	private boolean children;

	
	public List<AclDTO> build(List<Acl> acls){
		List<AclDTO> dtos = new ArrayList<AclDTO>();
		for (Acl acl : acls) {
			dtos.add(new AclDTO(acl.getId().toString(),acl.getName(), StringUtils.isEmpty(acl.getIcon()) ? "none" : acl.getIcon(),acl.getParentId() == null ? true :CollectionUtils.isNotEmpty(acl.getChildren()) ));
		}
		return dtos;
	}
}
