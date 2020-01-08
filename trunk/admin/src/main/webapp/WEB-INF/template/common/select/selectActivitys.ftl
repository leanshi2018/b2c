<@layout.head>
    <link href="${base}/res/css/member.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link   rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/jquery.ajaxContent.pack.js"></script>
    <script type="text/javascript" src="${base}/res/js/ajaxfileupload/ajaxfileupload.js"></script>
    <script type="text/javascript" src="${base}/res/js/goods/jquery.poshytip.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/goods/goods_body.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/validate.expand.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/goods/jquery.charCount.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js" charset="utf-8"></script>
</@layout.head>
<@layout.body>
    <div class="layout">
        <div class="wrap" style="padding: 20px">
            <!-- 搜索栏 -->
            <form method="post" name="formSearch" id="formSearch" action="${base}/admin/jpush/findActivitys.jhtml">
                <input type="hidden" name="pageNo" value="${1}">
                <table class="tb-type1 noborder search">
                    <tbody>
                    <tr>
                        <td>
                            <input name="info" type="text" id="info" value="${info}" placeholder="请输入查询信息"/>
                            <a href="javascript:$('#formSearch').submit();" class="btn-search" title="查询">
                            </a>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>

            <table class="order">
                <thead>
                <tr>
                    <th>&nbsp;</th>
                    <th><b>活动ID</b></th>
                    <th><b>活动名称</b></th>
                    <th><b>操作</b></th>
                </tr>
                </thead>
                <tbody>
                <#list activitys.content as activity>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${activity.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${activity.id}
                        </td>
                        <td style="text-align: left">
                            ${activity.name}
                        </td>
                        <td class="w100 tc">
                            <a href="javascript:void(0);" id="selectIds" class="sc-btn sc-btn-green mt5"
                               onclick="selSpe('${activity.info}')">选择</a>
                        </td>
                    </tr>
                </#list>
                </tbody>
                <tfoot>
                <tr>
                    <td colspan="20">
                        <@layout.pager pager/>
                    </td>
                </tr>
                </tfoot>
            </table>
        </div>
    </div>
    <script>

        // $(".btn-search").click(function(){
        //     var info=$("#info").val();
        //     $("#formSearch").attr("action", "" +info);
        //     $('#formSearch').submit();
        //     // var href = $(this).attr("href");
        //     // $("#formSearch").attr("action", href).submit();
        //     return false;
        // });
        function selSpe(info) {
            //$(obj).parent().parent().remove();
            //调用父级窗口
            parent.appendWareInfo(info);
            //关闭当前窗口
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        }
    </script>
</@layout.body>