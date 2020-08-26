<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <link rel="stylesheet" href="${base}/resources/css/plugins/timepicker/jquery-ui-timepicker-addon.css"/>
    <script type="text/javascript" src="${base}/res/js/jquery.edit.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
</@layout.head>
<style>
    .th_w{width:8%;}
    .ths{width:16%;}
    .ui-widget-overlay {display: none;}
</style>
<script type="text/javascript">
    $(function () {
        $('#Submit').click(function () {
            $('#formSearch').submit();
        });
    });
</script>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>商品推荐页管理</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current"><@spring.message "manage"/></a></li>
                    <li>
                        <a href="${base}/admin/shop_activity_common/add.jhtml"><@spring.message "add"/></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/shop_activity_common/findProductsRecommendationList.jhtml">
            <input type="hidden" name="pageable" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <th class="th_w">推荐页名称</th>
                    <td class="ths"><input type="text" class="text" name="recommendationName" id="recommendationName" value="${recommendationName}" ></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:void(0);" id="Submit" type="submit"  class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="" class="btns "><@spring.message "search.cancel"/></a>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>

        <form method="post" id="list">
            <table class="table tb-type2">
                <thead>
                <tr class="thead">
                    <th class="w24"><input type="checkbox" id="idsAll" class="checkitem"></th>
                    <th class="align-center">商品推荐页ID</th>
                    <th class="align-center">商品推荐页名称</th>
                    <th class="align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list page.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.id}
                        </td>
                        <td>
                            ${list.recommendationName}
                        </td>
                        <td>
                            <a class="change" href="${base}/admin/shop_activity_common/findProductsRecommendationInfo.jhtml?rId=${list.id}">修 改</a>||
                            <a id="manage" href="${base}/admin/shop_activity_common/findRecommendationGoods.jhtml?rId=${list.id}">商品管理</a>||
                            <a href="JavaScript:void(0);" onclick="del('${list.id}');">删 除</a>
                        </td>
                    </tr>
                </#list>
                </tbody>
                <div id="editdetaildiv" ></div>
                <tfoot class="tfoot">
                <tr>
                    <td colspan="16">
                        <@layout.pager pager/>
                    </td>
                </tr>
                </tfoot>
            </table>
        </form>
    </div>
    <script type="text/javascript">
        <#-- 删除推荐页-->
        function del(id) {
            $("#editdetaildiv" ).dialog({
                title: '确定删除选中推荐页？',
                height: 170,
                width: 250,
                modal: true,
                "buttons": {
                    "取消": function () {
                        $(this).dialog("close");
                    },
                    "确定": function () {
                        $("#list").attr("action", "${base}/admin/shop_activity_common/delProductsRecommendation.jhtml?rId=" + id);
                        $('#list').submit();
                        $(this).dialog("close");
                        alert("删除成功！");
                    }
                }
            })
        }
        /*修改*/
        $(".change").click(function(){
            var href = $(this).attr("href");
            $("#list").attr("action", href).submit();
            return false;
        });
        //管理页
        $("#manage").click(function(){
            var href = $(this).attr("href");
            $("#list").attr("action", href).submit();
            return false;
        });

        $(function () {
            $("#idsAll").click(function () {
                $('input[name="ids"]').attr("checked", this.checked);
            });
            var $subBox = $("input[name='ids']");
            $subBox.click(function () {
                $("#idsAll").attr("checked", $subBox.length == $("input[name='ids']:checked").length ? true : false);
            });
        });

    </script>
</@layout.body>