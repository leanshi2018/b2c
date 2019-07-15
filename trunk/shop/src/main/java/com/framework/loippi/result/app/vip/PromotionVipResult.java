package com.framework.loippi.result.app.vip;



import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 晋升vip的名单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionVipResult implements Serializable {

    private static final long serialVersionUID = 1L;

    //会员编号
    private String mmCode;

    //时间
    private Date date;


}
