<@layout.head>
    <link href="${base}/res/css/member.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
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
            <form method="get" name="formSearch" id="formSearch" action="${base}/admin/jpush/findMember.jhtml">
                <input type="hidden" name="pageNo" value="${1}">
                <input type="hidden" name="id" value="${id}"/>
                <table class="tb-type1 noborder search">
                    <tbody>
                    <tr>
                        <td>
                            <input name="info" id="info" type="text" value="${info}" placeholder="请输入活动名称"/>
                            <a href="javascript:$('#formSearch').submit();" class="btn-search " title="查询">
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
                    <th><b>用户ID</b></th>
                    <th><b>手机号</b></th>
                    <th><b>操作</b></th>
                </tr>
                </thead>
                <tbody>
                <#list RdMmBasicInfo.content as users>
                    <tr>
                        <td><input type="checkbox" name="id" value="${users.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${users.mmId}
                        </td>
                        <td style="text-align: left">
                            ${users.verificationMobile}
                        </td>
                        <td class="w100 tc">
                            <a href="javascript:void(0);" id="selectIds" class="sc-btn sc-btn-green mt5"
                               onclick="selSpeccoupons('${users.info}')">选择</a>
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
        function selSpeccoupons(info,id, verificationMobile,verificationNickName) {
            //$(obj).parent().parent().remove();
            //调用父级窗
            parent.appendGoods(info,id,verificationMobile,verificationNickName);
            //关闭当前窗口
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        }
        <#--var infos=$("#info").val();-->
        <#--var urls="${base}/admin/jpush/findMember.jhtml?info="+infos;-->
        <#--$("#formSearch").attr("action", urls);-->
    </script>
</@layout.body>