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
    .th_w{width:6%;}
    .ths{width:16%;}
    .ui-widget-overlay {display: none;}
    /*.ui-widget-header{*/
    /*    background: none;border:none;color: #666}*/
    /*.ui-widget-header .ui-icon {*/
    /*    background-image: url(images/ui-icons_222222_256x240.png);*/
    /*}*/
</style>
<script type="text/javascript">
    $(function () {
        $('#shopPMansongSubmit').click(function () {
            $('#formSearch').submit();
        });
    });
</script>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>通知栏推送管理</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current"><span><@spring.message "manage"/></span></a></li>
                    <li>
                        <a href="${base}/admin/jpush/add/forword.jhtml"><span><@spring.message "add"/></span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form method="post" name="formSearch" id="formSearch" action="">
            <input type="hidden" name="pageNo" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <th class="th_w">标题</th>
                    <td class="ths"><input type="text" class="text" name="" value=""></td>
                    <th class="th_w">推送时间</th>
                    <td class="ths"><input type="text" class="text" name="" value=""></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:void(0);" id="shopPMansongSubmit" type="submit"  class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="" class="btns "><span><@spring.message "search.cancel"/></span></a>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>

        <form method="post" id="couponlist">
            <table class="table tb-type2">
                <thead>
                <tr class="thead">
                    <th class="w24"><input type="checkbox" id="idsAll" class="checkitem"></th>
                    <th class="align-center">消息ID</th>
                    <th class="align-center">通知内容</th>
                    <th class="align-center">推送对象</th>
                    <th class="align-center">推送方式</th>
                    <th class="align-center">跳转页面</th>
                    <th class="align-center">推送时间</th>
                    <th class="align-center">状态</th>
                    <th class="align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list couponList.content as coupons>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${coupons.id}" class="checkitem"></td>
                        <td style="text-align: left">

                        </td>
                        <td style="text-align: left">

                        </td>
                        <td style="text-align: left">

                        </td>
                        <td style="text-align: left">

                        </td>
                        <td style="text-align: left">

                        </td>
                        <td>
                            <#--                            <#if coupons.updateTime??>-->
                            <#--                                ${coupons.updateTime?string("yyyy-MM-dd HH:mm:ss")}-->
                            <#--                            </#if>-->
                        </td>
                        <td>
                            <#--                            <#if coupons.status == 1>待审核</#if>-->
                            <#--                            <#if coupons.status == 2>审核通过</#if>-->
                            <#--                            <#if coupons.status == 3>审核失败</#if>-->
                            <#--                            <#if coupons.status == 4>下架</#if>-->
                        </td>
                        <td>
                            <#--                            <a class="look" href="${base}/admin/plarformShopCoupon/viewCouponContent.jhtml?couponId=${coupons.id}">查&nbsp;看</a>||-->
                            <#--                            <a href="JavaScript:void(0);" onclick="lowershelf('${coupons.id}');">删除</a>-->
                        </td>
                    </tr>
                </#list>
                </tbody>
                <div id="editdetaildiv" ></div>
                <tfoot>
                <tr>

                </tr>
                </tfoot>
            </table>
        </form>
    </div>
    <script type="text/javascript">
        $(".look").click(function(){
            var href = $(this).attr("href");
            $("#couponlist").attr("action", href).submit();
            return false;
        });

        <#--function prohibit(id) {-->
        <#--    $("#editdetaildiv" ).dialog({-->
        <#--        title: '请对该优惠券进行审核',-->
        <#--        height: 170,-->
        <#--        width: 250,-->
        <#--        modal: true,-->
        <#--        "buttons": {-->
        <#--            "审核失败": function () {-->
        <#--                $("#couponlist").attr("action", "${base}/admin/plarformShopCoupon/auditCoupon.jhtml?couponId=" + id + "&targetStatus=3" );-->
        <#--                $('#couponlist').submit();-->
        <#--                $(this).dialog("close");-->
        <#--            },-->
        <#--            "审核通过": function () {-->
        <#--                $("#couponlist").attr("action", "${base}/admin/plarformShopCoupon/auditCoupon.jhtml?couponId=" + id + "&targetStatus=2" );-->
        <#--                $('#couponlist').submit();-->
        <#--                $(this).dialog("close");-->
        <#--                alert("审核通过！");-->
        <#--            }-->
        <#--        }-->
        <#--    })-->

        <#--}-->

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