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

    </div>
    <script type="text/javascript">

    </script>

</@layout.body>