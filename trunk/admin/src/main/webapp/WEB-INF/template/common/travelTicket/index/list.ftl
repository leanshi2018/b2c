<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <link rel="stylesheet" href="${base}/resources/css/plugins/timepicker/jquery-ui-timepicker-addon.css"/>
    <script type="text/javascript" src="${base}/res/js/jquery.edit.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
</@layout.head>
<script type="text/javascript">
    $(function () {
        $('#shopPMansongSubmit').click(function () {
            $('#formSearch').submit();
        });
    });

    function calculation() {
        console.log($("#periodCode").val())
        var periodCode=$("#periodCode").val();
        if(periodCode==""){
            alert("请填入周期！");
        }else{
            document.write("<form action='${base}/admin/travel/compliance.jhtml' method=post name=form1 style='display:none'>"+"<input type=hidden name=periodCode value='"+$("#periodCode").val()+"'/></form>");
            document.form1.submit();
        }
    }
    /*选择旅游券*/
    function buyCouponspage() {
        layer.open({
            type: 2,
            move: false,
            shade: [0.3, '#393D49'],//开启遮罩层
            title: '选择旅游券',
            content: ['${base}/admin/travel/findTicketAll.jhtml','yes'],
            area: ['800px', '600px']
        });
    }
    function appendInfo(id,travelName) {
        $("#ticketId").val(id);
        $("#travelName").val(travelName);
    }
    function song() {
        if($("#travelName").val()==""){
            alert("请选择旅游券！");
        }else{
            document.write("<form action='${base}/admin/travel/grantTicket.jhtml' method=post name=form2 style='display:none'>" + "<input type=hidden name=ticketId value='" + $("#ticketId").val() + "'/></form>");
            document.form2.submit();
        }
    }
</script>
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
            <input type="hidden" name="pageNumber" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">旅游劵ID</th>
                    <td class="w160"><input type="text" class="text w150" name="id" value="${rdTravelTicket.id}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="w110">旅游券名称</th>
                    <td class="w160"><input type="text" class="text w150" name="travelLikeName" value="${rdTravelTicket.travelLikeName}"></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a id="shopPMansongSubmit" class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <#if rdTravelTicket.travelLikeName != '' || rdTravelTicket.id != '' >
                        <a href="${base}/admin/travel/travelTicket/list.jhtml" class="btns "><span><@spring.message "search.cancel"/></span></a>
                        </#if>
                    </td>
                    <td style="width:10px">&nbsp;</td>
                    <th class="th_w">周期</th>
                    <td class="ths">
                        <input type="text" class="text" name="periodCode" id="periodCode" value="${periodCode}">
                        <a href="javascript:void(0);" onclick="calculation()" class="btn btn-info btn-xs" />计算</a>
                    </td>
                    <#--选择优惠券-->
                    <input type="hidden" id="ticketId"  value="">
                    <th class="th_w">券</th>
                    <td class="ths">
                        <input name="travelName" id="travelName" type="text" value="${travelName}">
                        <a href="javascript:void(0);" onclick="buyCouponspage()" class="btn btn-info btn-xs" />选择</a>
                        <a href="javascript:void(0);" onclick="song()" class="btn btn-info btn-xs" />发放旅游券</a>
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
                <div id="editdetaildiv" ></div>
                <tfoot class="tfoot">
<#--                <tr>-->
                    <#-- 定义局部变量pageCount保存总页数 -->
<#--                    <#assign pageNo=list.pageNumber?int>-->
<#--                    <#if !pageNo??>-->
<#--                        <#assign pageNo=list.pageNo?int>-->
<#--                    </#if>-->
<#--                    <#assign pageSize=list.pageSize?int>-->
<#--                    <#assign recordCount=list.total?int>-->

<#--                    <#assign pageCount=list.totalPages?int>-->
<#--                    <#if list.totalPages==0><#return/></#if>-->
<#--                    &lt;#&ndash; 输出分页样式 &ndash;&gt;-->

<#--                    &lt;#&ndash; 页号越界处理 &ndash;&gt;-->
<#--                    <#if (pageNo > pageCount)>-->
<#--                        <#assign pageNo=pageCount>-->
<#--                    </#if>-->
<#--                    <#if (pageNo < 1)>-->
<#--                        <#assign pageNo=1>-->
<#--                    </#if>-->
<#--                    &lt;#&ndash; 输出分页表单 &ndash;&gt;-->
<#--                    &lt;#&ndash;共${recordCount}条数据,分${((recordCount+pageSize -1)/pageSize)?int}页,每页显示${pageSize}条&ndash;&gt;-->
<#--                    <div class="pagination">-->
<#--                        <ul><li>-->
<#--                                &lt;#&ndash; 上一页处理 &ndash;&gt;-->
<#--                                <#if (pageNo == 1)>-->
<#--                            <li><span><@spring.message "page.first"/></span></li>-->

<#--                            <#else>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(1)"><span><@spring.message "page.first"/></span></a></li>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(${pageNo - 1})"><span><@spring.message "toolbar.tooltip.prevPage" /></span></a></li>-->
<#--                            </#if>-->
<#--                            &lt;#&ndash; 如果前面页数过多,显示... &ndash;&gt;-->
<#--                            <#assign start=1>-->
<#--                            <#if (pageNo > 4)>-->
<#--                                <#assign start=(pageNo - 1)>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(1)"><span>1</span></a></li>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(2)"><span>2</span></a></li>-->
<#--                                <li><span>...</span></li>-->
<#--                            </#if>-->
<#--                            &lt;#&ndash; 显示当前页号和它附近的页号 &ndash;&gt;-->
<#--                            <#assign end=(pageNo + 1)>-->
<#--                            <#if (end > pageCount)>-->
<#--                                <#assign end=pageCount>-->
<#--                            </#if>-->
<#--                            <#list start..end as i>-->
<#--                                <#if (pageNo==i)>-->
<#--                                    <li><span class="currentpage">${i}</span></li>-->
<#--                                <#else>-->
<#--                                    <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(${i})"><span>${i}</span></a></li>-->
<#--                                </#if>-->
<#--                            </#list>-->
<#--                            &lt;#&ndash; 如果后面页数过多,显示... &ndash;&gt;-->
<#--                            <#if (end < pageCount - 2)>-->
<#--                                <li><span>...</span></li>-->
<#--                            </#if>-->
<#--                            <#if (end < pageCount - 1)>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(${pageCount - 1})"><span>${pageCount - 1}</span></a></li>-->
<#--                            </#if>-->
<#--                            <#if (end < pageCount)>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(${pageCount})"><span>${pageCount}</span></a></li>-->
<#--                            </#if>-->
<#--                            &lt;#&ndash; 下一页处理 &ndash;&gt;-->
<#--                            <#if (pageNo == pageCount)>-->
<#--                                <li><span><@spring.message "page.last" /></span></li>-->
<#--                            <#else>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(${pageNo + 1})"><span><@spring.message "toolbar.tooltip.nextPage" /></span></a></li>-->
<#--                                <li><a class="demo" href="javascript:void(0);" onclick="turnOverPage(${((recordCount+pageSize -1)/pageSize)?int})"><span><@spring.message "page.last" /></span></a></li>-->
<#--                            </#if>-->
<#--                            <script language="javascript">-->
<#--                                function turnOverPage(no) {-->
<#--                                    var queryForm = document.forms[0];-->
<#--                                    if (no >${pageCount}) {-->
<#--                                        no =${pageCount};-->
<#--                                    }-->
<#--                                    if (no < 1) {-->
<#--                                        no = 1;-->
<#--                                    }-->
<#--                                    if (queryForm.pageNo != undefined) {-->
<#--                                        queryForm.pageNo.value=no;-->
<#--                                    }-->
<#--                                    if (queryForm.pageNumber != undefined) {-->
<#--                                        queryForm.pageNumber.value=no;-->
<#--                                    }-->
<#--                                    queryForm.submit();-->
<#--                                }-->
<#--                            </script>-->
<#--                        </ul>-->
<#--                    </div>-->
<#--                </tr>-->
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