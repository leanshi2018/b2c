<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/ajaxfileupload/ajaxfileupload.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/dialog/dialog.js" id="dialog_js"></script>
</@layout.head>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>快递公司详情</h3>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form id="inputForm" method="post" action="${base}/trade/evalGoodsSensitivity/save.jhtml">
            <table class="table tb-type2">
                <tbody>

                </tbody>
                <tfoot>
                <tr class="tfoot">
                    <td colspan="15">
                        <a class="btn" href="javascript:history.go(-1);"><span><@spring.message "button.back"/></span></a>
                        <a href="JavaScript:void(0);" class="btn" id="submitBtn" onclick="$('#inputForm').submit();"><span><@spring.message "submit"/></span></a></td>
                </tr>
                </tfoot>
            </table>
        </form>
    </div>

    <script type="text/javascript">
        $(document).ready(function(){

        });

    </script>
</@layout.body>