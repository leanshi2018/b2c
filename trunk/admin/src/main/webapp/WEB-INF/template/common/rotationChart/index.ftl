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
    .th_w{width:8%;}
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
            $("#pictureName").val($("#pictureName").val());
        });
    });
</script>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>轮播图管理</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current"><@spring.message "manage"/></a></li>
                    <li>
                        <a href="${base}/admin/shop_activity_common/findPicture.jhtml?pictureId=${id}"><@spring.message "add"/></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/shop_activity_common/findHomePictureList.jhtml">
            <input type="hidden" name="pageNo" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <th class="th_w">轮播图名称</th>
                    <td class="ths"><input type="text" class="text" name="pictureName" id="pictureName" value="${pictureName}" ></td>
                    <td style="width:10px">&nbsp;</td>
                    <td class="w70 tc">
                        <a href="javascript:void(0);" id="shopPMansongSubmit" type="submit"  class="btn-search " title="<@spring.message "search"/>">&nbsp;</a>
                        <a href="" class="btns "><@spring.message "search.cancel"/></a>
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
                    <th class="align-center">轮播题名称</th>
                    <th class="align-center">跳转页面</th>
                    <th class="align-center">排序</th>
                    <th class="align-center">是否显示</th>
                    <th class="align-center"><@spring.message "operation"/></th>
                </tr>
                </thead>
                <tbody>
                <#list page.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.pictureName}
                        </td>
                        <td style="text-align: left">
                             <#if list.activityUrl??>
                                <#if list.activityUrl == 'homepage'>辑</#if>
                                <#if list.activityUrl == "messagepage">消息中心</#if>
                                <#if list.activityUrl == "goodsdetailspage">商品详情</#if>
                                <#if list.activityUrl == "mypage">我</#if>
                                <#if list.activityUrl == "myresultspage">个人业绩</#if>
                                <#if list.activityUrl == "orderpage">我的订单</#if>
                                <#if list.activityUrl == "myintegralpage">我的积分</#if>
                                <#if list.activityUrl == "rewardintegralpage">奖励积分</#if>
                                <#if list.activityUrl == "shoppingintegralpage">购物积分</#if>
                                <#if list.activityUrl == "buyintegralpage">换购积分</#if>
                                <#if list.activityUrl == "bankcardpage">我的银行卡</#if>
                                <#if list.activityUrl == "learnpage">学堂</#if>
                                <#if list.activityUrl == "learnarticlepage">学堂文章详情</#if>
                                <#if list.activityUrl == "invitationpage">我的邀请</#if>
                                <#if list.activityUrl == "activityGoodsListpage">活动页面</#if>
                                <#if list.activityUrl == 'buyCouponspage'>优惠券购买详情</#if>
                                <#if list.activityUrl == 'recommendGoodspage'>推荐页面</#if>
                                <#if list.activityUrl == 'gatherGoodspage'>凑单页面</#if>
                                <#if list.activityUrl == 'plusCenterpage'>PLUS会员中心</#if>
                             </#if>
                             <#if list.jumpInterface??>${list.jumpInterface}</#if>
                        </td>
                        <td>
                            ${list.PSort}
                        </td>
                        <td>
                            <#if list.auditStatus == 0>不显示</#if>
                            <#if list.auditStatus == 1>显示</#if>
                        </td>
                        <td>
                            <a class="look" href="${base}/admin/shop_activity_common/findPicture.jhtml?pictureId=${list.id}">编 辑</a>||
                            <a href="JavaScript:void(0);" onclick="del('${list.id}');">删 除</a>
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
        function del(id) {
            $("#editdetaildiv" ).dialog({
                title: '确定删除选中轮播图？',
                height: 170,
                width: 250,
                modal: true,
                "buttons": {
                    "取消": function () {
                        $(this).dialog("close");
                    },
                    "确定": function () {
                        $("#couponlist").attr("action", "${base}/admin/shop_activity_common/delPicture.jhtml?pictureId=" + id);
                        $('#couponlist').submit();
                        $(this).dialog("close");
                        alert("删除成功！");
                    }
                }
            })

        }

        /*post提交*/
        $(".look").click(function(){
            var href = $(this).attr("href");
            $("#couponlist").attr("action", href).submit();
            return false;
        });
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