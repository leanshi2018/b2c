<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>

</@layout.head>
<script>
    <#--function delBrand() {-->
    <#--    var items = $("input[name='ids']:checked").length;-->
    <#--    if (items == 0) {-->
    <#--        alert("<@spring.message "deletes"/>");-->
    <#--    } else {-->
    <#--        if (confirm('<@spring.message "delete"/>')) {-->
    <#--            $('#form_list').submit();-->
    <#--        }-->
    <#--    }-->
    <#--}-->

    <#--function delRow(recommendid) {-->
    <#--    if (confirm('<@spring.message "delete"/>')) {-->
    <#--        var url = "${base}/admin/shop_common_message/delete.jhtml";-->
    <#--        location.href = url + "?ids=" + recommendid;-->
    <#--    }-->
    <#--}-->


</script>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>优惠券购买数据</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current"><span><@spring.message "manage"/></span></a></li>
                    <li>
                        <a href="${base}/admin/shop_common_message/index.jhtml?id=0"><span><@spring.message "add"/></span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <#--        <form method="post" name="formSearch" action="${base}/admin/shop_common_message/list.jhtml">-->
        <#--            <input type="hidden" name="pageNo" value="${1}">-->
        <#--            <table class="tb-type1 noborder search">-->

        <#--                <tbody>-->
        <#--                <tr>-->
        <#--                    <th><label for="navTitle"><@spring.message "the_title"/></label></th>-->
        <#--                    <td><input type="text" value="${paramter.title}" name="filter_title" id="filter_title" class="txt">-->
        <#--                    </td>-->
        <#--                    <th class="th_width"><label for="query_start_time">推送时间</label></th>-->
        <#--                    <td><input class="txt Wdate" type="text" id="query_start_time" name="searchStartTime" readonly-->
        <#--                               value="${shopCommonDocument.searchStartTime}"-->
        <#--                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'query_end_time\')}'});"/>-->
        <#--                    </td>-->
        <#--                    <td>-->
        <#--                        <label for="query_start_time">~</label>-->
        <#--                    </td>-->
        <#--                    <td>-->
        <#--                        <input class="txt Wdate" type="text" id="query_end_time" name="searchEndTime" readonly-->
        <#--                               value="${shopCommonDocument.searchEndTime}"-->
        <#--                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'query_start_time\')}'});"/>-->
        <#--                    </td>-->
        <#--                    <td><a href="javascript:document.formSearch.submit();" class="btn-search "-->
        <#--                           title="<@spring.message "search"/>">&nbsp;</a>-->
        <#--                        <#if (paramter.title != null && paramter.title != '')>-->
        <#--                            <a class="btns"-->
        <#--                               href="${base}/admin/shop_common_message/list.jhtml"><span><@spring.message "search.cancel"/></span></a>-->
        <#--                        </#if>-->
        <#--                    </td>-->
        <#--                </tr>-->
        <#--                </tbody>-->
        <#--            </table>-->
        <#--        </form>-->
        <#--        <form method="post" id='form_list' action="${base}/admin/shop_common_message/delete.jhtml">-->
        <#--            <input type="hidden" name="status" id="statusObj">-->
        <#--            <table class="table table-striped table-bordered table-hover dataTables-example dataTable">-->
        <#--                <thead>-->
        <#--                <tr class="thead">-->
        <#--                    <th class="w24"></th>-->
        <#--                    <th>消息ID</th>-->
        <#--                    <th>消息标题</th>-->
        <#--                    <th>消息内容</th>-->
        <#--                    <th>跳转</th>-->
        <#--                    <th>发送时间</th>-->
        <#--                    <th>操作</th>-->
        <#--                </tr>-->
        <#--                </thead>-->
        <#--                <tbody>-->
        <#--                <#list page.content as shopCommonMessage>-->
        <#--                    <tr>-->
        <#--                        <td><input type="checkbox" name="ids" value="${shopCommonMessage.id}" class="checkitem"></td>-->
        <#--                        <td>${shopCommonMessage.id!''}</td>-->
        <#--                        <td>${shopCommonMessage.title!''}</td>-->
        <#--                        <td>${shopCommonMessage.content!''}</td>-->
        <#--                        <td>${shopCommonMessage.link!''}</td>-->
        <#--&lt;#&ndash;                        <td><#if shopCommonMessage.UType==1>部分用户<#elseif shopCommonMessage.UType==2>全部用户</#if></td>&ndash;&gt;-->
        <#--&lt;#&ndash;                        <td><#if shopCommonMessage.type==1>APP内消息中心推送&ndash;&gt;-->
        <#--&lt;#&ndash;                            <#elseif shopCommonMessage.type==2>&ndash;&gt;-->
        <#--&lt;#&ndash;                                短信推送&ndash;&gt;-->
        <#--&lt;#&ndash;                            <#else>通知栏推送</#if>&ndash;&gt;-->
        <#--&lt;#&ndash;                        </td>&ndash;&gt;-->
        <#--                        <td><#if shopCommonMessage.createTime??>${shopCommonMessage.createTime?string('yyyy-MM-dd HH:mm')}</#if></td>-->
        <#--                        <td>-->
        <#--                            <a href="${base}/admin/shop_common_message/view.jhtml?id=${shopCommonMessage.id}"> ${message("admin.common.view")} </a>|-->
        <#--                            <#if shopCommonMessage.onLine!=1>-->
        <#--                                <a href="${base}/admin/shop_common_message/updateOnline.jhtml?id=${shopCommonMessage.id}&status=1">发布</a>|</#if>-->
        <#--                            <a data-id="${shopCommonMessage.id}"-->
        <#--                               href="javascript:delRow('${shopCommonMessage.id}')"> ${message("admin.common.delete")} </a>-->
        <#--                        </td>-->
        <#--                    </tr>-->
        <#--                </#list>-->
        <#--                </tbody>-->
        <#--                <tfoot>-->
        <#--                <tr class="tfoot">-->
        <#--                    <td><input type="checkbox" class="checkall" id="checkallBottom"><label-->
        <#--                                for="checkallBottom"><@spring.message "all"/></label></td>-->
        <#--                    <td colspan="16">-->
        <#--                        &nbsp;&nbsp;<a href="JavaScript:void(0);" class="btn"-->
        <#--                                       onclick="delarticle();"><span><@spring.message "del"/></span></a>-->
        <#--                        <@layout.pager page/>-->
        <#--                    </td>-->
        <#--                </tr>-->
        <#--                </tfoot>-->
        <#--            </table>-->
        <#--        </form>-->
    </div>
    <script type="text/javascript">

    </script>

</@layout.body>