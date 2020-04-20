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
            <form method="post" name="formSearch" id="formSearch"
                  action="${base}/admin/order/mentionAddressAll.jhtml">
                <input type="hidden" name="pageNo" value="${1}">
                <input type="hidden" name="id" value="${id}"/>
                <table class="tb-type1 noborder search">
                    <tbody>
                    <tr>
                        <td>
                            <input name="provinceCode" type="text" value="${provinceCode}" placeholder="自提点"/>
                            <input name="phone" type="text" value="${phone}" placeholder="手机号"/>
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
                    <th><b>自提点</b></th>
                    <th><b>手机号</b></th>
                    <th><b>详细地址</b></th>
                    <th><b>操作</b></th>
                </tr>
                </thead>
                <tbody>
                <#list page.content as address>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${address.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${address.phone}
                        </td>
                        <td style="text-align: left">
                            ${address.accountSetName}
                        </td>

                        <td class="w100 tc">
                            <a href="javascript:void(0);" id="selectIds" class="sc-btn sc-btn-green mt5"
                               onclick="Specaddress('${address.phone}','${address.provinceCode}')">选择</a>
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
        function Specaddress(phone, provinceCode) {
            //$(obj).parent().parent().remove();
            //调用父级窗口
            parent.selSource(phone, provinceCode);
            //关闭当前窗口
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        }
    </script>
</@layout.body>