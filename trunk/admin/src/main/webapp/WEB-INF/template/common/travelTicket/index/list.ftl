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
                        <a href="#" class="current"><span>旅游券管理</span></a>
                    </li>
                    <li>
                        <a href="${base}/admin/travel/forward.jhtml?id=${id}"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- 提示栏 -->
        <div class="fixed-empty"></div>

        <!-- 搜索栏 -->
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/travel/travelTicket/list.jhtml">
            <input type="hidden" name="pageNo" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">旅游劵ID</th>
                    <td class="w160"><input type="text" class="text w150" name="id" value="${travelTicketList.id}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">旅游券名称</th>
                    <td class="w160"><input type="text" class="text w150" name="travelLikeName" value="${travelTicketList.travelLikeName}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:document.formSearch.submit();" class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
<#--                        <#if travelLikeName != '' || id != '' >-->
                        <a href="${base}/admin/travel/travelTicket/list.jhtml" class="btns "><span><@spring.message "search.cancel"/></span></a>
<#--                        </#if>-->
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
                    <th class="align-center">劵ID</th>
                    <th class="align-center">旅游券名称</th>
                    <th class="align-center">面值</th>
                    <th class="align-center">已发放</th>
                    <th class="align-center">使用开始时间</th>
                    <th class="align-center">使用结束时间</th>
                    <th class="align-center">创建人</th>
                    <th class="align-center">创建时间</th>
                    <th class="align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list travelTicketList.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.id}
                        </td>
                        <td style="text-align: left">
                            ${list.travelName}
                        </td>
                        <td style="text-align: left">
                            ${list.ticketPrice}
                        </td>
                        <td style="text-align: left">
                            ${list.issueNum}
                        </td>
                        <td style="text-align: left">
                            <#if list.useStartTime??>
                                ${list.useStartTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.useEndTime??>
                                ${list.useEndTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            ${list.createName}
                        </td>
                        <td style="text-align: left">
                            <#if list.createTime??>
                                ${list.createTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td>
                            <a href="javascript:detail('${list.id}');">编 辑</a>
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

        $(function () {
            $("#idsAll").click(function () {
                $('input[name="ids"]').attr("checked", this.checked);
            });
            var $subBox = $("input[name='ids']");
            $subBox.click(function () {
                $("#idsAll").attr("checked", $subBox.length == $("input[name='ids']:checked").length ? true : false);
            });
        });
        //编辑页
        function detail(id) {
            window.location.href = "${base}/admin/travel/forward.jhtml?id=" + id;
        }

        function OperMenu(obj) {
            if ($(obj).find("ul").css("display") == "none") {
                $(obj).find("ul").css("display", "block");
            } else {
                $(obj).find("ul").css("display", "none");
            }
        }
        function getCheckValue() {
            var ids = "";
            $("input[name=ids]").each(function () {
                if ($(this).attr("checked")) {
                    ids += "," + $(this).val();
                }
            });
            return ids;
        }
    </script>
</@layout.body>