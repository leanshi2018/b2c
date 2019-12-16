<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <link href="${base}/res/css/font/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>


    <script type="text/javascript" charset="utf-8" src="${base}/res/js/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="${base}/res/js/ueditor/ueditor.all.js"></script>

    <!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
    <!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
    <script type="text/javascript" charset="utf-8" src="${base}/res/js/ueditor/lang/zh-cn/zh-cn.js"></script>
</@layout.head>
<style>
    .pins{border:none!important;width:12%;}
    .checkname td,.css{
        border-top: 1px solid #ccc;
        border-bottom: 1px solid #ccc;
        width: 100px;
        text-align: center;
    }
</style>
<@layout.body>
    <script type="text/javascript" src="${base}/res/js/jquery.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery.validation.min.js"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css">
    <link rel="stylesheet" type="text/css" href="${base}/resources/css/plugins/colpick/css/colpick.css"/>
    <link rel="stylesheet" href="${base}/resources/css/plugins/timepicker/jquery-ui-timepicker-addon.css"/>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <ul class="tab-base">
                    <li>
                        <a href=""><span>管理</span></a>
                    </li>
                    <li><a href="javascript:void(0)" class="current"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty">
        </div>
        <form id="add_form" action="${base}/admin/jpush/sendJpush.jhtml" method="post">
            <table class="table tb-type2">
                <tbody>
                <tr>
                    <td colspan="3" class="required" style="background: #eee">基本信息</td>
                </tr>
                <#if jpush=="">
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>推送方式
                    </td>
                    <td>

                        <input name="pushMethod" type="radio" value="1"  <#if jpush.pushMethod==1||jpush.pushMethod==null>checked</#if>>
                        通知栏推送
                        <input name="pushMethod" type="radio" value="2"  <#if jpush.pushMethod==2>checked</#if>>
                        其他

                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>通知内容
                    </td>
                    <td>
                            <input name="message" id="message" type="text" value="${jpush.message}" class="w200"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>推送时间
                    </td>
                    <td>
                            <input class="w200 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd '})"
                                   id="pushTime" name="pushTime" value=""/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>目标平台
                    </td>
                    <td>
                        <label><input name="platform"  type="checkbox" value="0" checked/>所有</label>
                        <label><input name="platform"  type="checkbox" value="1" />安卓</label>
                        <label><input name="platform"  type="checkbox" value="2" />IOS正式</label>
                        <label><input name="platform"  type="checkbox" value="3" />IOS测试</label>
                        <label><input name="platform"  type="checkbox" value="4" />安卓和IOS测试</label>
                        <label><input name="platform"  type="checkbox" value="5" />安卓和IOS正式</label>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>推送对象
                    </td>
                    <td>
                        <input name="audience" type="radio" value="all" id="alluser" <#if jpush.audience==all>checked</#if>>
                        全部用户
                        <input name="" type="radio" value=""  id="checkid">
                        指定用户<br>
                        <span class="error-message"></span>
                    </td>
                </tr>

<#--                        <input class="pins" type="text" id="goodsId" name="goodsId" value="<#if shopGoods??>${shopGoods.id}</#if>">-->
<#--                        <input class="pins" type="text" value="<#if shopGoods??>${shopGoods.goodsName}</#if>" name="goodsName"-->
<#--                               id="goodsName">-->
<#--                        <input class="pins" type="text" value="<#if shopGoods??>${shopGoods.className}</#if>" name="className"-->
<#--                               id="className">-->

                <tr class="noborder" >
                    <td class="required">
                        <em class="pngFix"></em>
                    </td>
                    <td>
                      <a class="btn btn-success showuers" onclick="clickcheckmember();" style="display: none;float: left;border:none;">添加</a>
                        <a class="btn btn-default showuers" style="display: none;background: #ccc;border:none;" >删除</a>
                    </td>
                </tr>
                <tr class="noborder" >
<#--                    <tr>-->
<#--                        <td class="css">会员编号</td><td class="css">手机号</td><td class="css">昵称</td><td class="css">操作</td>-->
<#--                    </tr>-->
                    <td class="required">
                        <em class="pngFix"></em>
                    </td>
                    <td class="checkname">

                    </td>

                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>
                    </td>
                    <td>
                        <div class="col-sm-9">
                            <div class="col-lg-1" STYLE="width: 90%;">
                                <input type="radio" name="jump" value=""  checked>
                                <span >跳转路径</span>
                                <select name="jumpPath" class="w150" id="jumpPath">
                                    <option value="homepage" <#if jpush.jumpPath == 'homepage'>selected="selected"</#if>>辑</option>
                                    <option value="messagepage"<#if jpush.jumpPath == "messagepage">selected="selected" </#if>>消息中心</option>
                                    <option value="goodsdetailspage" id="goodsdetailspage" <#if jpush.jumpPath == "goodsdetailspage">selected="selected" </#if>>商品详情</option>
                                    <option value="mypage"  <#if jpush.jumpPath == "mypage">selected="selected" </#if>>我</option>
                                    <option value="myresultspage" <#if jpush.jumpPath == "myresultspage">selected="selected" </#if>>个人业绩</option>
                                    <option value="orderpage"    <#if jpush.jumpPath == "orderpage">selected="selected" </#if>>我的订单</option>
                                    <option value="myintegralpage" <#if jpush.jumpPath == "myintegralpage">selected="selected" </#if>>我的积分</option>
                                    <option value="rewardintegralpage" <#if jpush.jumpPath == "rewardintegralpage">selected="selected" </#if>>奖励积分</option>
                                    <option value="shoppingintegralpage" <#if jpush.jumpPath == "shoppingintegralpage">selected="selected" </#if>>购物积分</option>
                                    <option value="buyintegralpage" <#if jpush.jumpPath == "buyintegralpage">selected="selected" </#if>>换购积分</option>
                                    <option value="bankcardpage" <#if jpush.jumpPath == "bankcardpage">selected="selected" </#if>>我的银行卡</option>
                                    <option value="learnpage" <#if jpush.jumpPath == "learnpage">selected="selected" </#if>>学堂</option>
                                    <option value="learnarticlepage" <#if jpush.jumpPath == "learnarticlepage">selected="selected" </#if>>学堂文章详情</option>
                                    <option value="invitationpage" <#if jpush.jumpPath == "invitationpage">selected="selected" </#if>>我的邀请</option>
                                    <option value="activityGoodsListpage" <#if jpush.jumpPath == "activityGoodsListpage">selected="selected" </#if>>活动页面</option>
                                    <option value="buyCouponspage" id="buyCouponspage" <#if jpush.jumpPath == 'buyCouponspage'>selected="selected" </#if>>优惠券购买详情</option>
                                    <option value=""  <#if jpush.jumpPath == "">selected="selected"</#if>></option>
                                </select>
                                <input name="jumpName" id="jumpName"class="w150" type="hidden" value=""/>
                                <input name="jumpJson" class="w150" value="${jumpJson}"style="height:23px;"/>
                                <a class="btn-search" id="searchs" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;"></a>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>
                    </td>
                    <td>
                        <div class="col-sm-9">

                            <div class="col-lg-1" STYLE="width: 90%;" >
                                <input type="radio" name="jump" value="" id="">
                                <span >跳转链接</span>
                                <input name="jumpLink"  type="text" id="" class="w200"  value="${jpush.jumpLink}" />
                            </div>
                        </div>
                    </td>
                </tr>
                </#if>
                <#if jpush!=null>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>推送方式
                        </td>
                        <td>

                            <input name="pushMethod" type="radio" value="1"  <#if jpush.pushMethod==1||jpush.pushMethod==null>checked</#if>>
                            通知栏推送
                            <input name="pushMethod" type="radio" value="2"  <#if jpush.pushMethod==2>checked</#if>>
                            其他

                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>通知内容
                        </td>
                        <td>
                            <input name="message" id="message" type="text" value="${jpush.message}" class="w200"/>
                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>推送时间
                        </td>
                        <td>
                            <input class="w200 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="pushTime" name="pushTime" value=""/>
                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>目标平台
                        </td>
                        <td>
                            <label><input name="platform"  type="checkbox" value="0" <#if jpush.platform==0>checked</#if>/>所有</label>
                            <label><input name="platform"  type="checkbox" value="1" <#if jpush.platform==1>checked</#if> />安卓</label>
                            <label><input name="platform"  type="checkbox" value="2" <#if jpush.platform==2>checked</#if>/>IOS正式</label>
                            <label><input name="platform"  type="checkbox" value="3" <#if jpush.platform==3>checked</#if>/>IOS测试</label>
                            <label><input name="platform"  type="checkbox" value="4" <#if jpush.platform==4>checked</#if>/>安卓和IOS测试</label>
                            <label><input name="platform"  type="checkbox" value="5" <#if jpush.platform==5>checked</#if>/>安卓和IOS正式</label>
                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>推送对象
                        </td>
                        <td>
                            <input name="audience" type="radio" value="all"  <#if jpush.audience=="all">checked</#if>>
                            全部用户
                            <input name="" type="radio" value=""  id="checkid">
                            指定用户<br>
                            <a></a>
                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required" width="150px">
                            <em class="pngFix"></em>
                        </td>
                        <td>
                            <div class="col-sm-9">
                                <div class="col-lg-1" STYLE="width: 90%;">
                                    <input type="radio" name="jump" value="" checked>
                                    <span >跳转路径</span>
                                    <select name="jumpPath" class="w150" id="jumpPath">
                                        <option value="${page.homepage}"  <#if jpush.jumpPath == page.homepage>selected="selected"</#if>>辑</option>
                                        <option value="messagepage" <#if jpush.jumpPath == messagepage>selected="selected" </#if>>消息中心</option>
                                        <option value="goodsdetailspage"   <#if jpush.jumpPath == goodsdetailspage>selected="selected" </#if>>商品详情</option>
                                        <option value="mypage" <#if jpush.jumpPath == mypage>selected="selected" </#if>>我</option>
                                        <option value="myresultspage" <#if jpush.jumpPath == myresultspage>selected="selected" </#if>>个人业绩</option>
                                        <option value="orderpage"    <#if jpush.jumpPath == orderpage>selected="selected" </#if>>我的订单</option>
                                        <option value="myintegralpage" <#if jpush.jumpPath == myintegralpage>selected="selected" </#if>>我的积分</option>
                                        <option value="rewardintegralpage" <#if jpush.jumpPath == rewardintegralpage>selected="selected" </#if>>奖励积分</option>
                                        <option value="shoppingintegralpage" <#if jpush.jumpPath == shoppingintegralpage>selected="selected" </#if>>购物积分</option>
                                        <option value="buyintegralpage" <#if jpush.jumpPath == buyintegralpage>selected="selected" </#if>>换购积分</option>
                                        <option value="bankcardpage" <#if jpush.jumpPath == bankcardpage>selected="selected" </#if>>我的银行卡</option>
                                        <option value="learnpage" <#if jpush.jumpPath == learnpage>selected="selected" </#if>>学堂</option>
                                        <option value="learnarticlepage" <#if jpush.jumpPath == learnarticlepage>selected="selected" </#if>>学堂文章详情</option>
                                        <option value="invitationpage" <#if jpush.jumpPath == invitationpage>selected="selected" </#if>>我的邀请</option>
                                        <option value="activityGoodsListpage" <#if jpush.jumpPath == activityGoodsListpage>selected="selected" </#if>>活动页面</option>
                                        <option value="buyCouponspage"   <#if jpush.jumpPath == buyCouponspage>selected="selected" </#if>>优惠券购买详情</option>
                                        <option value="" <#if jpush.jumpPath == "">selected="selected"</#if>>请选择</option>
                                    </select>
                                    <input name="jumpJson" class="w200" value="${jumpJson}"style="height:23px;border:1px solid #ccc;"/>
                                    <a class="btn-search"  style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;"></a>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required" width="150px">
                            <em class="pngFix"></em>
                        </td>
                        <td>
                            <div class="col-sm-9">
                                <div class="col-lg-1" STYLE="width: 90%;" >
                                    <input type="radio" name="jump" id="jumps">
                                    <span >跳转链接</span>
                                    <input name="jumpLink"  type="text" id="link" class="w400"  value="${jpush.jumpLink}" />
                                </div>
                            </div>
                        </td>
                    </tr>
                </#if>
                </tbody>
                <tfoot>
                <tr>
                    <td></td>
                    <td>
                        <#if jpush=="">
                            <a class="btn" href="javascript:history.go(-1);"
                               style="float:left"><span><@spring.message "button.back"/></span></a>
                            <a class="btn btn-success" id="subForm" type="submit">提交</a>
                        </#if>
                        <#if jpush!=null>
                        <a class="btn" href="javascript:history.go(-1);"
                           style="float:left"><span><@spring.message "button.back"/></span></a>
                        </#if>
                    </td>
                </tr>
                </tfoot>
            </table>
        </form>
    </div>
    <script type="text/javascript" src="${base}/resources/js/plugins/colpick/colpick.js"></script>
    <script>
        $(function () {
            console.log($("#jumpPath option:selected").val());
            /*判断跳转链接选中状态*/
            var link=$("#link").val();
            if(link!=""){
                $("#jumps").attr("checked","checked");
            }

            $("#subForm").click(function () {
                var Title = $("#message").val();
                if (Title == "") {
                    alert('通知内容不能为空!');
                    return false;
                }
                var pushTime=$("#pushTime").val();
                if (pushTime == "") {
                    alert('推送时间不能为空!');
                    return false;
                }

                $('#add_form').submit();
            })

        });
        $("#checkid").click(function () {
            $("#checkid").attr("checked","checked");
            $("#alluser").attr("checked","");
            $(".showuers").css("display","");
        });
        /*添加用户*/
        var clicknum = 1;
        function clickcheckmember() {
            var num = 0;
            var ifrm = $("#layui-layer-iframe" + clicknum);
            layer.open({
                type: 2,
                title: '<@spring.message "select"/>',
                shadeClose: true,
                maxmin: true,
                area: ['730px', '450px'],
                content: '${base}/admin/rd_mm_basic_info/listDialog.jhtml', //iframe的url
                btns: 2,
                btn: ['<@spring.message "confirm"/>', '<@spring.message "statu.cancel"/>'],
                yes: function (index) {
                    var domframe = window.frames["layui-layer-iframe" + clicknum];
                    var list = domframe.document.getElementsByName("ids");
                    var memberids = "";
                    var membername = "";
                    var names = [];
                    $("[name='memberNmae']").each(function () {
                        names.push($(this).val())
                    });
                    for (var i = 0; i < list.length; i++) {
                        var mname = $(list[i]).attr("membername");
                        if ($.inArray(mname, names) != -1) {
                            continue;
                        }
                        if (list[i].checked) {
                            if (i == list.length - 1) {
                                $(".checkname").append("<tr><td><span name='memberId' >"+ list[i].value + "</span></td><td><span name='mobile' >"+ $(list[i]).attr('mobile')+ "</span></td><td><span name='memberNmae' >"+ $(list[i]).attr('membername') +"<span></td><td><a href='javascript:void(0)' onclick='delRow(this);' class='current' title='<@spring.message "click_delete"/>'>删除</td></tr><br/>");         memberids += list[i].value;
                                membername += $(list[i]).attr("membername");
                            } else {
                                $(".checkname").append("<tr><td><span name='memberId' >"+ list[i].value + "</span></td><td><span name='mobile' >"+ $(list[i]).attr('mobile') + "</span></td><td><span name='memberNmae' >"+ $(list[i]).attr('membername') +"<span></td><td><a href='javascript:void(0)' onclick='delRow(this);' class='current' title='<@spring.message "click_delete"/>'>删除</td></tr><br/>");
                                memberids += list[i].value + ",";
                                membername += $(list[i]).attr("membername") + ",";
                            }
                            num++;
                        }
                    }
                    ;
                    layer.close(index);
                },
                end: function (index) {
                    clicknum++;
                    layer.close(index);
                }
            });
        }
        function delRow(obj){
            $(obj).parent().parent().remove();
        }

        /*判断是否选择优惠券购买详情*/
        $('#jumpPath').change(function(){
            var value=$(this).children('option:selected').val();
            if (value=="buyCouponspage"){
                $("#searchs").attr("onclick","buyCouponspage()");
                appendGoods(id,couponName);
            }else{
                $("#searchs").attr("onclick","");
            }
            if (value=="goodsdetailspage"){
                $("#jumpName").val("商品详情");
                $("#searchs").attr("onclick","goodsdetailspage()");
                appendGoods(goodsId, goodsName, className, brandName,goodsType);

            }else {
                $("#searchs").attr("onclick","");
            }
        });

        /*选择商品*/
       function goodsdetailspage() {
            var goodsId = $("[name='goodsId']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择商品',
                content: ['${base}/admin/shop_goods_recommend/select.jhtml?type=1&goodsId=' + goodsId, 'yes'],
                area: ['800px', '600px']
            })
        }
        function appendGoods(goodsId, goodsName, className, brandName,goodsType) {
            $("#goodsId").val(goodsId);
            $("#goodsName").val(goodsName);
            $("#className").val(className);

        }
        /*选择优惠券*/
        function buyCouponspage() {
            var couponName = $("[name='couponName']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择优惠券',
                content: ['${base}/admin/plarformShopCoupon/coupon/select.jhtml?status=2&couponName=' + couponName, 'yes'],
                area: ['800px', '600px']
            })
        }
        function appendGoods(id,couponName) {
            $("#couponId").val(id);
            $("#couponName").val(couponName);
        }


    </script>
    <div class="clear"></div>
</@layout.body>