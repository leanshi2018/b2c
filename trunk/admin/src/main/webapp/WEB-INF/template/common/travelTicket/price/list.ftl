<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <link rel="stylesheet" href="${base}/resources/css/plugins/timepicker/jquery-ui-timepicker-addon.css"/>
    <script type="text/javascript" src="${base}/res/js/jquery.edit.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>

</@layout.head>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <ul class="tab-base">
                    <li>
                        <a href="#" class="current"><span>旅游团价格表</span></a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- 提示栏 -->
        <div class="fixed-empty"></div>

        <!-- 搜索栏 -->
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/travel/travelCost/list.jhtml">
            <input type="hidden" name="pageNo" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">会员号</th>
                    <td class="w160"><input type="text" class="text w150" name="activityId" value="${activityId}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">活动ID</th>
                    <td class="w160"><input type="text" class="text w150" name="mmCode" value="${mmCode}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:document.formSearch.submit();" class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="${base}/admin/travel/travelCost/list.jhtml" class="btns "><span><@spring.message "search.cancel"/></span></a>
                        <a class="btn btn-outline btn-info btn-xs" id="btn-add-loippi exportExcell" href="javascript:void(0)"
                           onclick="exportExcell('${page.pageNo}')"><i class="fa fa-plus"></i>导出Excel</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
        <form method="post" id='form_list'>
            <table class="table tb-type2">
                <thead>
                <tr class="thead">
                    <th class="w24"><input type="checkbox" id="idsAll" class="checkitem"></th>
                    <th class="align-center">会员号</th>
                    <th class="align-center">会员昵称</th>
                    <th class="align-center">活动ID</th>
                    <th class="align-center">活动名称</th>
                    <th class="align-center">报名人数</th>
                    <th class="align-center">用券数量</th>
                    <th class="align-center">需补差价</th>
                </tr>
                </thead>
                <tbody>
                <#list rdTravelCostList.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.mmCode}
                        </td>
                        <td style="text-align: left">
                            ${list.mnickName}
                        </td>
                        <td style="text-align: left">
                            ${list.activityId}
                        </td>
                        <td style="text-align: left">
                            ${list.activityName}
                        </td>
                        <td style="text-align: left">
                            ${list.joinNum}
                        </td>
                        <td style="text-align: left">
                            ${list.useNum}
                        </td>
                        <td style="text-align: left">
                            ${list.moenyFill}
                        </td>
                    </tr>
                </#list>
                </tbody>
                <tfoot class="tfoot">
                <tr>
<#--                    <td colspan="16">-->
<#--                        <@layout.pager page/>-->
<#--                    </td>-->
                </tr>
                </tfoot>
            </table>
        </form>
    </div>

    <script>
        function exportExcell(pageNumber) {
            var fromVal=$("#formSearch").serialize();
            var pageNumber = pageNumber;
            window.location.href = "${base}/admin/order/list/exportExcel.jhtml?pages=" + pageNumber+"&param="+ fromVal;
            //layer.msg("暂未实现",{icon:5});
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