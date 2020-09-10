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
                        <a href="#" class="current"><span>旅游券领取使用明细</span></a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- 提示栏 -->
        <div class="fixed-empty"></div>

        <!-- 搜索栏 -->
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/travel/travelTicketDetail/list.jhtml">
            <input type="hidden" name="pageNo" value="${1}">
            <input type="hidden" id="status" value="">
            <input type="hidden" id="ticketSn" value="">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">劵序号</th>
                    <td class="w160"><input type="text" class="text w150" name="id" value="${id}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">旅游券名称</th>
                    <td class="w160"><input type="text" class="text w150" name="travelLikeName" value="${travelLikeName}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th>状态</th>
                    <td>
                        <select name="status" class="w100">
                            <option value=""  <#if status == null>selected="selected"</#if>>不限</option>
                            <option value="0" <#if status == '0'>selected="selected"</#if>>未使用</option>
                            <option value="1" <#if status == '1'>selected="selected"</#if>>报名占用</option>
                            <option value="2" <#if status == '2'>selected="selected"</#if>>已核销</option>
                            <option value="3" <#if status == '3'>selected="selected"</#if>>已过期</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">领取时间</th>
                    <td class="w160"><input class="txt Wdate" type="text" id="query_start_time" name="ownTimeLeft" readonly value="${ownTimeLeft}"
                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'query_end_time\')}'});"/>
                        <label for="query_start_time">~</label>
                        <input class="txt Wdate" type="text" id="query_end_time" name="ownTimeRight" readonly value="${ownTimeRight}"
                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'query_start_time\')}'});"/>
                    </td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">领取人id</th>
                    <td class="w160"><input type="text" class="text w150" name="ownCode" value="${ownCode}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">领取人昵称</th>
                    <td class="w160"><input type="text" class="text w150" name="ownNickName" value="${ownNickName}"></td>
                </tr>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">报名活动id</th>
                    <td class="w160"><input type="text" class="text w150" name="useActivityId" value="${useActivityId}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:document.formSearch.submit();" class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="${base}/admin/travel/travelTicketDetail/list.jhtml" class="btns "><span><@spring.message "search.cancel"/></span></a>
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
                    <th class="align-center">旅游券序号</th>
                    <th class="align-center">旅游券名称</th>
                    <th class="align-center">持有人ID</th>
                    <th class="align-center">持有人昵称</th>
                    <th class="align-center">面值</th>
                    <th class="align-center">获得时间</th>
                    <th class="align-center">使用时间</th>
                    <th class="align-center">核销时间</th>
                    <th class="align-center">状态</th>
                    <th class="align-center">报名的旅游ID</th>
                    <th class="align-center">报名的旅游名称</th>
                    <th class="align-center">更新人</th>
                    <th class="align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list travelTicketDetailList.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.travelId}
                        </td>
                        <td style="text-align: left">
                            ${list.travelName}
                        </td>
                        <td style="text-align: left">
                            ${list.ownCode}
                        </td>
                        <td style="text-align: left">
                            ${list.ownNickName}
                        </td>
                        <td style="text-align: left">
                            ${list.ticketPrice}
                        </td>
                        <td style="text-align: left">
                            <#if list.ownTime??>
                                ${list.ownTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.useTime??>
                                ${list.useTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.confirmTime??>
                                ${list.confirmTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.status == 0>未使用</#if>
                            <#if list.status == 1>报名占用</#if>
                            <#if list.status == 2>已核销</#if>
                            <#if list.status == 3>已过期</#if>
                        </td>
                        <td style="text-align: left">
                            ${list.id}
                        </td>
                        <td style="text-align: left">

                        </td>
                        <td style="text-align: left">

                        </td>
                        <td>
                            <#if list.status==0||list.status==3>
                                <a href="javascript:updateStatus('${list.ticketSn}','${list.status}')">核 销</a>
                            </#if>
                            <#if list.status==1||list.status==2>
                                <a href="javascript:update('${list.ticketSn}','${list.status}')">恢 复</a>
                            </#if>
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
        //核销弹窗
        function updateStatus(ticketSn,status) {
            $("#ticketSn").val(ticketSn);
            $("#status").val(status);
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择',
                content: ['${base}/admin/travel/travelActivity/list2.jhtml', 'yes'],
                area: ['800px', '600px']
            });

        }

        function appendInfo(id) {
            console.log("旅游券编号"+$("#ticketSn").val())
            console.log(id);
            <#--$.ajax({-->
            <#--    type: "post",-->
            <#--    url: "${base}/admin/travel/travelTicketDetail/restoreOrDestroy.jhtml",-->
            <#--    data: {-->
            <#--        "ticketSn":$("#ticketSn").val(),-->
            <#--        "species":2,-->
            <#--        "status":$("#status").val(),-->
            <#--        "activityId":id-->
            <#--    },-->
            <#--    dataType: "json",-->
            <#--    async: false,-->
            <#--    success: function (data) {-->
            <#--        console.log(data);-->
            <#--        $('#formSearch').submit();-->
            <#--    }-->
            <#--});-->
            var url= "${base}/admin/travel/travelTicketDetail/restoreOrDestroy.jhtml";
            location.href = url + "?ticketSn=" + $("#ticketSn").val()+"&species=2"+"&status="+$("#status").val()+"&activityId="+id;
        }
        //恢复单张旅游券
        function update(ticketSn,status) {
            if (confirm('要恢复该券，并清除报名信息吗？')) {
                var url = "${base}/admin/travel/travelTicketDetail/restoreOrDestroy.jhtml";
                location.href = url + "?ticketSn=" + ticketSn+"&species=1"+"&status="+status;
            }
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