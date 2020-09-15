package com.framework.loippi.entity.system;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_accesskey")
public class RdAccessKey implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 秘钥key
     */
    @Column(name = "access_key")
    private String accessKey;

    /**
     * 秘钥value
     */
    @Column(name = "secret")
    private String secret;
}
