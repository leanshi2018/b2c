<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>

</@layout.head>
<script>

</script>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>优惠券转让记录</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current"><span><@spring.message "manage"/></span></a></li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <#--        <form method="post" name="formSearch" id="formSearch"-->
        <#--              action="${base}/admin/plarformShopCoupon/Coupon/findCouponUserLogList.jhtml">-->
        <#--            <input type="hidden" name="pageNo" value="${1}">-->
        <#--            <table class="tb-type1 noborder search">-->
        <#--                <tbody>-->
        <#--                <tr>-->
        <#--                    <td style="width:10px">&nbsp;</td>-->
        <#--                    <th class="w110">劵序号</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.cuoponId}"></td>-->
        <#--                    <th class="w160">优惠劵名称</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.couponName}"></td>-->
        <#--                    <th>使用状态</th>-->
        <#--                    <td>-->
        <#--                        <select name="activityStatus" class="w100">-->
        <#--                            <option value="" <#if shopActivity.useState == null>selected="selected"</#if>>不限</option>-->
        <#--                            <option value="10" <#if shopActivity.useState == '1'>selected="selected"</#if>>已使用</option>-->
        <#--                            <option value="11" <#if shopActivity.useState == '2'>selected="selected"</#if>>未使用</option>-->
        <#--                            <option value="20" <#if shopActivity.useState == '3'>selected="selected"</#if>>已过期</option>-->
        <#--                            <option value="30" <#if shopActivity.useState == '4'>selected="selected"</#if>>已禁用</option>-->
        <#--                        </select>-->
        <#--                    </td>-->
        <#--                </tr>-->
        <#--                <tr>-->
        <#--                    <th><label for="query_start_time">领取时间</th>-->
        <#--                    <td><input class="txt Wdate" type="text" id="query_start_times" name="payStartTime" readonly-->
        <#--                               value="${param.payStartTime}" readonly-->
        <#--                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'query_end_time\')}'});"/>-->
        <#--                    </td>-->
        <#--                    <td style="width: 58px;">-->
        <#--                        <label for="query_start_time">~</label>-->
        <#--                    </td>-->
        <#--                    <td>-->
        <#--                        <input class="txt Wdate" type="text" style="margin-left: -40px" id="query_end_times"-->
        <#--                               name="payEndTime" readonly-->
        <#--                               value="${param.payEndTime}" readonly-->
        <#--                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'query_start_time\')}'});"/>-->
        <#--                    </td>-->
        <#--                    <th class="w110">领取人ID</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.receiveId}"></td>-->
        <#--                    <th class="w160">领取人昵称</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.receiveNickName}"></td>-->
        <#--                </tr>-->
        <#--                <tr>-->
        <#--                    <th class="w160">持有人ID</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.holdId}"></td>-->
        <#--                    <th class="w160">持有人昵称</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.holdNickName}"></td>-->
        <#--                    <th class="w160">使用订单号</th>-->
        <#--                    <td class="w160"><input type="text" class="text w150" name="name"-->
        <#--                                            value="${shopActivity.useOrderId}"></td>-->
        <#--                    <td style="width:10px">&nbsp;</td>-->
        <#--                    <td class="w70 tc">-->
        <#--                        <a href="javascript:void(0);" id="shopPMansongSubmit" class="btn-search "-->
        <#--                           title="<@spring.message "search"/>">&nbsp;</a>-->
        <#--                        <a href="${base}/admin/plarformShopActivity/activity/${activityType}/list.jhtml"-->
        <#--                           class="btns "><span><@spring.message "search.cancel"/></span></a>-->
        <#--                        <a href="javascript:updateActitivtyStatus(1);" class="btns "><span>开启</span></a>-->
        <#--                        <a href="javascript:updateActitivtyStatus(0);" class="btns "><span>禁用</span></a>-->
        <#--                        <a href="javascript:deleteActivityAll();" class="btns "><span>删除</span></a>-->
        <#--                    </td>-->
        <#--                </tr>-->
        <#--                </tbody>-->
        <#--            </table>-->
        <#--        </form>-->

        <form method="post"  action="${base}/admin/plarformShopCoupon/coupon/translog.jhtml">
            <table class="table tb-type2">
                <thead>
                <tr class="thead">
                    <th class="w24"><input type="checkbox" id="idsAll" class="checkitem"></th>
                    <th class="align-center">优惠券序号</th>
                    <th class="align-center">优惠券名称</th>
                    <th class="align-center">领取人ID</th>
                    <th class="align-center">领取人昵称</th>
                    <th class="align-center">转赠人ID</th>
                    <th class="align-center">转赠人昵称</th>
                    <th class="align-center">持有人ID</th>
                    <th class="align-center">持有人昵称</th>
                    <th class="align-center">领取时间</th>
                    <th class="align-center">转赠时间</th>
                    <th class="align-center">使用时间</th>
                    <th class="align-center">状态</th>
                    <th class=" align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
<#--                <#list page.content as coupons>-->
<#--                    <tr>-->
<#--                        <td><input type="checkbox" name="ids" value="${coupons.id}" class="checkitem"></td>-->
<#--                        <td style="text-align: left">-->
<#--                            ${coupons.couponOrderSn}-->
<#--                        </td>-->
<#--                        <td style="text-align: left">-->
<#--                            ${coupons.receiveId}-->
<#--                        </td>-->
<#--                        <td style="text-align: left">-->
<#--                            ${coupons.receiveNickName}-->
<#--                        </td>-->
<#--                        <td style="text-align: left">-->
<#--                            ${coupons.couponNumber}-->
<#--                        </td>-->
<#--                        <td style="text-align: left">-->
<#--                            ${coupons.couponAmount}-->
<#--                        </td>-->
<#--                        <td>${coupons.pointAmount}</td>-->
<#--                        <td>${coupons.orderAmount}</td>-->
<#--                        <td>-->
<#--                            <#if coupons.couponOrderState == 0>已取消</#if>-->
<#--                            <#if coupons.couponOrderState == 10>待付款</#if>-->
<#--                            <#if coupons.couponOrderState == 40>交易完成</#if>-->
<#--                        </td>-->
<#--                        <td>-->
<#--                            <#if coupons.refundState == 0>无退款</#if>-->
<#--                            <#if coupons.refundState == 1>部分退款</#if>-->
<#--                            <#if coupons.refundState == 2>全部退款</#if>-->
<#--                        </td>-->
<#--                        <td>-->
<#--                            <#if coupons.createTime??>-->
<#--                                ${coupons.createTime?string("yyyy-MM-dd HH:mm:ss")}-->
<#--                            </#if>-->
<#--                        </td>-->
<#--                        <td>-->
<#--                            <#if coupons.paymentTime??>-->
<#--                                ${coupons.paymentTime?string("yyyy-MM-dd HH:mm:ss")}-->
<#--                            </#if>-->
<#--                        </td>-->
<#--                        <td>-->
<#--                            <#if coupons.updateTime??>-->
<#--                                ${coupons.updateTime?string("yyyy-MM-dd HH:mm:ss")}-->
<#--                            </#if>-->
<#--                        </td>-->
<#--                        <td>-->
<#--                            ${coupons.paymentId}-->
<#--                        </td>-->
<#--                    </tr>-->
<#--                </#list>-->
<#--                </tbody>-->
<#--                <tfoot class="tfoot">-->
<#--                <tr>-->
<#--                    <td colspan="20">-->
<#--                        <@layout.pager page/>-->
<#--                    </td>-->
<#--                </tr>-->
<#--                </tfoot>-->
            </table>
        </form>
    </div>
    <script type="text/javascript">

    </script>

</@layout.body>