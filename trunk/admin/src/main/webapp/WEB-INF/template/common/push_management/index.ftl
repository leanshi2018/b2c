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
                    <li><a href="JavaScript:void(0);" class="current"><@spring.message "manage"/></a></li>
                    <li>
                        <a href="${base}/admin/jpush/add/forword.jhtml"><@spring.message "add"/></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form method="post" name="formSearch" id="formSearch" action="${base}/admin/jpush/list.jhtml">
            <input type="hidden" name="pageNo" value="${1}">
            <table class="tb-type1 noborder search">
                <tbody>
                <tr>
                    <th class="th_w">内容</th>
                    <td class="ths"><input type="text" class="text" name="likelist" value="${likelist}" ></td>
                    <th class="th_w">推送时间</th>
                    <td style="width: 36%;">
                        <input class="txt Wdate" type="text" id="query_start_time" name="searchLeftTime"
                               value="${searchLeftTime}" readonly
                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'query_end_time\')}'});"/>
                        <label for="query_start_time">~</label>
                        <input class="txt Wdate" type="text" id="query_end_time" name="searchRightTime"
                               value="${searchRightTime}" readonly
                               onClick="WdatePicker({lang:'${locale}',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'query_start_time\')}'});"/>
                    </td>
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
                <#list jpushList.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.id}
                        </td>
                        <td style="text-align: left">
                            ${list.message}
                        </td>
                        <td style="text-align: left">
                            <#if list.audience=="all">全部</#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.pushMethod== 1>通知栏推送</#if>
                            <#if list.pushMethod== 2>其他</#if>
                        </td>
                        <td style="text-align: left">
                            <#if list.jumpPath == 'homepage'>辑</#if>
                            <#if list.jumpPath == "listpage">消息中心</#if>
                            <#if list.jumpPath == "goodsdetailspage">商品详情</#if>
                            <#if list.jumpPath == "mypage">我</#if>
                            <#if list.jumpPath == "myresultspage">个人业绩</#if>
                            <#if list.jumpPath == "orderpage">我的订单</#if>
                            <#if list.jumpPath == "myintegralpage">我的积分</#if>
                            <#if list.jumpPath == "rewardintegralpage">奖励积分</#if>
                            <#if list.jumpPath == "shoppingintegralpage">购物积分</#if>
                            <#if list.jumpPath == "buyintegralpage">换购积分</#if>
                            <#if list.jumpPath == "bankcardpage">我的银行卡</#if>
                            <#if list.jumpPath == "learnpage">学堂</#if>
                            <#if list.jumpPath == "learnarticlepage">学堂文章详情</#if>
                            <#if list.jumpPath == "invitationpage">我的邀请</#if>
                            <#if list.jumpPath == "activityGoodsListpage">活动页面</#if>
                            <#if list.jumpPath == 'buyCouponspage'>优惠券购买详情</#if>
                            <#if list.jumpLink??>${list.jumpLink}</#if>
                        </td>
                        <td>
                            <#if list.pushTime??>
                                ${list.pushTime?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                        </td>
                        <td>
                            <#--                            <#if coupons.status == 1>待审核</#if>-->
                            <#--                            <#if coupons.status == 2>审核通过</#if>-->
                            <#--                            <#if coupons.status == 3>审核失败</#if>-->
                            <#--                            <#if coupons.status == 4>下架</#if>-->
                        </td>
                        <td>
                            <a class="look" href="${base}/admin/jpush/show.jhtml?id=${list.id}">查&nbsp;看</a>||
                            <a href="JavaScript:void(0);" >删除</a>
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