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
    function toSelectGoodsDialog() {
        layer.open({
            type: 2,
            move: false,
            shade: [0.3, '#393D49'],//开启遮罩层
            title: '选择规格',
            content: ['${base}//admin/shop_activity_common/findShopGoodList.jhtml', 'yes'],
            area: ['800px', '600px']
        });
    }

    function appendInfo(id, name) {
        var url = "${base}/admin/shop_activity_common/saveRecommendationGoods.jhtml";
        var para = {
            "rId": id,
            "jsonMap": $("#typeId").val()
        };
        var va = $.ajax({
            type: "post",
            url: url,
            data: para,
            dataType: "json",
            async: false,
            success: function (data) {
                $('#formSearch').submit();

            }
        });
    }
</script>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>商品管理</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current"><@spring.message "manage"/></a></li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/shop_activity_common/findRecommendationGoods.jhtml">
            <input type="hidden" name="pageable" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <th class="th_w">商品名称</th>
                    <td class="ths"><input type="text" class="text" name="goodsName" id="goodsName" value="${recommendationName}" ></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:void(0);" id="Submit" type="submit"  class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="" class="btns "><@spring.message "search.cancel"/></a>
                        <a href="javascript:toSelectGoodsDialog()"  class="btns ">添加</a>
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
                    <th class="align-center">商品ID</th>
                    <th class="align-center">商品名称</th>
                    <th class="align-center">商品分类</th>
                    <th class="align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list page.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.goodsId}
                        </td>
                        <td>
                            ${list.goodsName}
                        </td>
                        <td>

                        </td>
                        <td>
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
                        $("#list").attr("action", "${base}/admin/shop_activity_common/delRecommendationGoods.jhtml?id=" + id);
                        $('#list').submit();
                        $(this).dialog("close");
                        alert("删除成功！");
                    }
                }
            })
        }

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