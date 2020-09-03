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
    <!-- 提交关键字查询 -->
    <script type="text/javascript">
        $(function () {
            $('#mansongSubmit').click(function () {
                $('#formSearch').submit();
            });
        });
    </script>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <ul class="tab-base">
                    <li>
                        <a href="#" class="current"><span>旅游活动管理</span></a>
                    </li>
                    <li>
                        <a href="${base}/admin/travel/activity/forward.jhtml?id=${id}"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <!-- 提示栏 -->
        <div class="fixed-empty"></div>

        <!-- 搜索栏 -->
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/travel/travelActivity/list.jhtml">
            <input type="hidden" name="pageNo" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">活动id</th>
                    <td class="w160"><input type="text" class="text w150" name="id" value="${id}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">活动名称</th>
                    <td class="w160"><input type="text" class="text w150" name="activityName" value="${activityName}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th>活动状态</th>
                    <td>
                        <select name="status" class="w100">
                            <option value="" <#if status == null>selected="selected"</#if>>不限</option>
                            <option value="0" <#if status == '0'>selected="selected"</#if>>未开始</option>
                            <option value="1" <#if status == '1'>selected="selected"</#if>>报名中</option>
                            <option value="2" <#if status == '2'>selected="selected"</#if>>已成团等待开团</option>
                            <option value="3" <#if status == '3'>selected="selected"</#if>>已完成</option>
                            <option value="-1"<#if status == '-1'>selected="selected"</#if>>已作废</option>
                        </select>
                    </td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:void(0);" id="mansongSubmit" class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="${base}/admin/travel/travelActivity/list.jhtml" class="btns "><span><@spring.message "search.cancel"/></span></a>
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
                    <th class="align-center">活动ID</th>
                    <th class="align-center">活动名称</th>
                    <th class="align-center">价格</th>
                    <th class="align-center">最大可参团人数</th>
                    <th class="align-center">已参团人数</th>
                    <th class="align-center">报名开始时间</th>
                    <th class="align-center">报名截止时间</th>
                    <th class="align-center">状态</th>
                    <th class="align-center">创建人</th>
                    <th class="align-center">创建时间</th>
                    <th class=" align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list rdTravelActivityList.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.id}
                        </td>
                        <td style="text-align: left">
                            ${list.activityName}
                        </td>
                        <td style="text-align: left">
                            ${list.activityCost}
                        </td>
                        <td style="text-align: left">
                            ${list.numCeiling}
                        </td>
                        <td style="text-align: left">
                            ${list.numTuxedo}
                        </td>
                        <td style="text-align: left">
                            <#if list.startTime??>
                                ${list.startTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.endTime??>
                                ${list.endTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.status == 0>未开始</#if>
                            <#if list.status == 1>报名中</#if>
                            <#if list.status == 2>已成团等待开团</#if>
                            <#if list.status == 3>已完成</#if>
                            <#if list.status ==-1>已作废</#if>
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
                    <td colspan="16">
                        <@layout.pager page/>
                    </td>
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
            window.location.href = "${base}/admin/travel/activity/forward.jhtml?id=" + id;
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