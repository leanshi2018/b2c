package com.framework.loippi.service.union;

import com.framework.loippi.entity.UnionRefund;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UnionRefundService {

    public Map<String, String> toUnionRefund(UnionRefund unionRefund);

    public Map<String, Object> UnionRefundfront(String relapath, HttpServletRequest request, HttpServletResponse rep);

    public Map<String, Object> UnionRefundback(String relapath, HttpServletRequest request, HttpServletResponse resp);

}
