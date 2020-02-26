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
    #articleTitle,#activityname,#goodsName,#couponName{display: none;width: 209px;}
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
        <form id="add_form" action="${base}/admin/shop_common_message_new/saveOrUpdate.jhtml" method="post">

            <table class="table tb-type2">

                <tbody>
                <tr>
                    <td colspan="3" class="required" style="background: #eee">基本信息</td>
                </tr>

                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>展示方式
                        </td>
                        <td>

                            <input name="" type="text" readonly value="通知中心">
                            <span class="error-message"></span>
                        </td>
                    </tr>

                    <#if message=="">
                        <tr class="noborder">
                            <td class="required">
                                <em class="pngFix"></em>消息标题
                            </td>
                            <td>
                                <input name="title" id="title" type="text" value="${message.title}" />

                                <span class="error-message"></span>
                            </td>
                        </tr>
                        <tr class="noborder">
                            <td class="required">
                                <em class="pngFix"></em>图文详情
                            </td>

                        </tr>
                        <tr class="noborder">
                            <td class="required">
                                <em class="pngFix"></em>
                            </td>
                            <td colspan="4" class="vatop rowform">
                                <div class="span10">
                                    <script id="ueditor" name="content"  type="text"style="height:250px;width:100%;max-width:540px;">${message.content}</script>
                                    <script type="text/javascript">
                                        //实例化编辑器
                                        //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
                                        var ueditor = UE.getEditor('ueditor');
                                    </script>
                                </div>
                            </td>

                            <span class="error-message"></span>

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
                                            <option value="" selected="selected">请选择</option>
                                            <option value="homepage" <#if message.jumpPath == 'homepage'>selected="selected"</#if>>辑</option>
                                            <option value="messagepage"<#if message.jumpPath == "messagepage">selected="selected" </#if>>消息中心</option>
                                            <option value="goodsdetailspage" id="goodsdetailspage" <#if message.jumpPath == "goodsdetailspage">selected="selected" </#if>>商品详情</option>
                                            <option value="mypage"  <#if message.jumpPath == "mypage">selected="selected" </#if>>我</option>
                                            <option value="myresultspage" <#if message.jumpPath == "myresultspage">selected="selected" </#if>>个人业绩</option>
                                            <option value="orderpage"    <#if message.jumpPath == "orderpage">selected="selected" </#if>>我的订单</option>
                                            <option value="myintegralpage" <#if message.jumpPath == "myintegralpage">selected="selected" </#if>>我的积分</option>
                                            <option value="rewardintegralpage" <#if message.jumpPath == "rewardintegralpage">selected="selected" </#if>>奖励积分</option>
                                            <option value="shoppingintegralpage" <#if message.jumpPath == "shoppingintegralpage">selected="selected" </#if>>购物积分</option>
                                            <option value="buyintegralpage" <#if message.jumpPath == "buyintegralpage">selected="selected" </#if>>换购积分</option>
                                            <option value="bankcardpage" <#if message.jumpPath == "bankcardpage">selected="selected" </#if>>我的银行卡</option>
                                            <option value="learnpage" <#if message.jumpPath == "learnpage">selected="selected" </#if>>学堂</option>
                                            <option value="learnarticlepage" <#if message.jumpPath == "learnarticlepage">selected="selected" </#if>>学堂文章详情</option>
                                            <option value="invitationpage" <#if message.jumpPath == "invitationpage">selected="selected" </#if>>我的邀请</option>
                                            <option value="activityGoodsListpage" <#if message.jumpPath == "activityGoodsListpage">selected="selected" </#if>>活动页面</option>
                                            <option value="buyCouponspage" id="buyCouponspage" <#if message.jumpPath == 'buyCouponspage'>selected="selected" </#if>>优惠券购买详情</option>
                                        </select>
                                        <#--选择文章-->
                                        <input type="text" class="text w500" value="${article.articleTitle}" name="articleTitle" id="articleTitle">
                                        <input type="hidden" class="text w500" value="${article.id}" name="id" >
                                        <input type="hidden" class="text w500" value="${article.articleContent}" name="articleContent" >
                                        <#--选择活动-->
                                        <input name="name" type="text" id="activityname" value="${shopActivity.name}"/>
                                        <input name="activityId" id="activityId" type="hidden" value="${shopActivity.id}"/>
                                        <input name="info" id="info" type="hidden" value="${shopActivity.info}"/>
                                        <#--选择商品-->
                                        <form id="recommend_form" method="post" name="recommendForm" action="${base}/admin/shop_goods_recommend/edit.jhtml">
                                            <input class="pins" type="hidden" id="goodsId" name="goodsId" value="<#if shopGoods??>${shopGoods.id}</#if>">
                                            <input class="pins" type="text" value="<#if shopGoods??>${shopGoods.goodsName}</#if>" name="goodsName" id="goodsName">
                                            <input class="pins" type="hidden" value="<#if shopGoods??>${shopGoods.className}</#if>" name="className" id="className">
                                        </form>
                                        <#--选择优惠券-->
                                        <input type="hidden" id="couponId" name="couponId" value="${id}">
                                        <#--                                <input type="hidden" id="couponLikeName" name="couponLikeName" value="${couponLikeName}">-->
                                        <input name="couponName" id="couponName" type="text" value="${couponName}">
                                        <#--映射的名字-->
                                        <input name="openName" id="openName"class="w150" type="hidden" value=""/>
                                        <#--拼接的json-->
                                        <input name="jumpJson" class="w150" id="jsons" value="${jumpJson}"style="height:23px;display:none;"/>
                                        <#--搜索商品的按鈕-->
                                        <a class="btn-search" id="searchgoods" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="goodsdetailspage()"></a>
                                        <#--搜索优惠券-->
                                        <a class="btn-search" id="searchbuys" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="buyCouponspage()"></a>
                                        <a class="btn-search" id="searchactivity" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="activityGoodsListpage()"></a>
                                        <a class="btn-search" id="searchlearnarticle" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="learnarticlepage()"></a>
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
                                        <input type="radio" name="jump" value="" id="jumps">
                                        <span >跳转链接</span>

                                        <input name="jumpUrl"  type="text" id="link" class="w200"  value="${message.jumpUrl}" style="width: 346px"/>

                                    </div>
                                </div>
                            </td>
                        </tr>
                    </#if>

                <#if message!=null>
                    <input type="hidden" name="id" value="${message.id}" />
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>消息标题
                        </td>
                        <td>
                            <input name="title" id="title" type="text" value="${message.title}" />

                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>图文详情
                        </td>

                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>
                        </td>
                        <td colspan="4" class="vatop rowform">
                            <div class="span10">

                                <script id="ueditor" name="content"  type="text"style="height:250px;width:100%;max-width:540px;">${message.content}</script>



                                <script type="text/javascript">
                                    //实例化编辑器
                                    //建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
                                    var ueditor = UE.getEditor('ueditor');
                                </script>
                            </div>
                        </td>

                        <span class="error-message"></span>

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
                                        <option value="" selected="selected">请选择</option>
                                        <option value="homepage" <#if message.jumpPath == 'homepage'>selected="selected"</#if>>辑</option>
                                        <option value="messagepage"<#if message.jumpPath == "messagepage">selected="selected" </#if>>消息中心</option>
                                        <option value="goodsdetailspage" id="goodsdetailspage" <#if message.jumpPath == "goodsdetailspage">selected="selected" </#if>>商品详情</option>
                                        <option value="mypage"  <#if message.jumpPath == "mypage">selected="selected" </#if>>我</option>
                                        <option value="myresultspage" <#if message.jumpPath == "myresultspage">selected="selected" </#if>>个人业绩</option>
                                        <option value="orderpage"    <#if message.jumpPath == "orderpage">selected="selected" </#if>>我的订单</option>
                                        <option value="myintegralpage" <#if message.jumpPath == "myintegralpage">selected="selected" </#if>>我的积分</option>
                                        <option value="rewardintegralpage" <#if message.jumpPath == "rewardintegralpage">selected="selected" </#if>>奖励积分</option>
                                        <option value="shoppingintegralpage" <#if message.jumpPath == "shoppingintegralpage">selected="selected" </#if>>购物积分</option>
                                        <option value="buyintegralpage" <#if message.jumpPath == "buyintegralpage">selected="selected" </#if>>换购积分</option>
                                        <option value="bankcardpage" <#if message.jumpPath == "bankcardpage">selected="selected" </#if>>我的银行卡</option>
                                        <option value="learnpage" <#if message.jumpPath == "learnpage">selected="selected" </#if>>学堂</option>
                                        <option value="learnarticlepage" <#if message.jumpPath == "learnarticlepage">selected="selected" </#if>>学堂文章详情</option>
                                        <option value="invitationpage" <#if message.jumpPath == "invitationpage">selected="selected" </#if>>我的邀请</option>
                                        <option value="activityGoodsListpage" <#if message.jumpPath == "activityGoodsListpage">selected="selected" </#if>>活动页面</option>
                                        <option value="buyCouponspage" id="buyCouponspage" <#if message.jumpPath == 'buyCouponspage'>selected="selected" </#if>>优惠券购买详情</option>
                                    </select>
                                    <#--选择文章-->
                                    <input type="text" class="text w500" value="${article.articleTitle}" name="articleTitle" id="articleTitle">
                                    <input type="hidden" class="text w500" value="${article.id}" name="id" >
                                    <input type="hidden" class="text w500" value="${article.articleContent}" name="articleContent" >
                                    <#--选择活动-->
                                    <input name="name" type="text" id="activityname" value="${shopActivity.name}"/>
                                    <input name="activityId" id="activityId" type="hidden" value="${shopActivity.id}"/>
                                    <input name="info" id="info" type="hidden" value="${shopActivity.info}"/>
                                    <#--选择商品-->
                                    <form id="recommend_form" method="post" name="recommendForm" action="${base}/admin/shop_goods_recommend/edit.jhtml">
                                        <input class="pins" type="hidden" id="goodsId" name="goodsId" value="<#if shopGoods??>${shopGoods.id}</#if>">
                                        <input class="pins" type="text" value="<#if shopGoods??>${shopGoods.goodsName}</#if>" name="goodsName" id="goodsName">
                                        <input class="pins" type="hidden" value="<#if shopGoods??>${shopGoods.className}</#if>" name="className" id="className">
                                    </form>
                                    <#--选择优惠券-->
                                    <input type="hidden" id="couponId" name="couponId" value="${id}">
                                    <#--                                <input type="hidden" id="couponLikeName" name="couponLikeName" value="${couponLikeName}">-->
                                    <input name="couponName" id="couponName" type="text" value="${couponName}">
                                    <#--映射的名字-->
                                    <input name="openName" id="openName"class="w150" type="hidden" value=""/>
                                    <#--拼接的json-->
                                    <input name="jumpJson" class="w150" id="jsons" value="${jumpJson}"style="height:23px;display:none;"/>
                                    <#--搜索商品的按鈕-->
                                    <a class="btn-search" id="searchgoods" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="goodsdetailspage()"></a>
                                    <#--搜索优惠券-->
                                    <a class="btn-search" id="searchbuys" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="buyCouponspage()"></a>
                                    <a class="btn-search" id="searchactivity" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="activityGoodsListpage()"></a>
                                    <a class="btn-search" id="searchlearnarticle" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="learnarticlepage()"></a>
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
                                    <input type="radio" name="jump" value="" id="jumps">
                                    <span >跳转链接</span>

                                    <input name="jumpUrl"  type="text" id="link" class="w200"  value="${message.jumpUrl}" style="width: 346px"/>


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
                        <#if message!=null>
                        <a class="btn" href="javascript:history.go(-1);" style="float:left"><span><@spring.message "button.back"/></span></a>
                            <a class="btn btn-success" id="subForm" type="submit">提交</a>
                        </#if>
<#--                        <#if message==null>-->
<#--                        <a class="btn" href="javascript:history.go(-1);" style="float:left"><span><@spring.message "button.back"/></span></a>-->
<#--                        <a class="btn btn-success" id="subForm" type="submit">提交</a>-->
<#--                        </#if>-->
                    </td>
                </tr>
                </tfoot>
            </table>
        </form>
    </div>
    <script type="text/javascript" src="${base}/resources/js/plugins/colpick/colpick.js"></script>
    <script>
        //摘要提取
        UE.getEditor('ueditor').addListener('blur', function (editor) {
            extractContent();
        });
        function extractContent() {
            var content = UE.getEditor('ueditor').getContent();
            if (content != "") {

                var con = UE.getEditor('ueditor').getContentTxt();
                var contentSub = con.substring(0, 200);
                $('#digest').attr('value', '');
                $('#digest').attr('value', contentSub);
            }
        }
        /*判断是否选择打开方式*/
        $('#openType').change(function() {
            var value = $(this).children('option:selected').val();
            if (value == "跳转商品推荐页") {
                $("#activityname").show();
                $("#searchactivity").css("display","");
                $("#openName").val("活动页面");
            }else{
                $("#activityname").css("display","none");
                $("#searchactivity").css("display","none");
            }
            if (value == "跳转路径") {
                $("#openPage").css("display","");
            }else{
                $("#openPage").css("display","none");
            }
            if (value == "跳转链接") {
                $("#jumpInterface").css("display","");
            }else{
                $("#jumpInterface").css("display","none");
            }
        })
        //上传图片
        function ajaxFileUploads(myBlogImage, imgId, img) {
            $.ajaxFileUpload({
                //处理文件上传操作的服务器端地址(可以传参数,已亲测可用)
                url: '${base}/admin/fileupload/uploadImage.jhtml',
                secureuri: false,                       //是否启用安全提交,默认为false
                fileElementId: myBlogImage,           //文件选择框的id属性
                dataType: 'json',                       //服务器返回的格式,可以是json或xml等
                fileSize: 5120000,
                allowType: 'jpg,jpeg,png,JPG,JPEG,PNG',
                success: function (data, status) {        //服务器响应成功时的处理函数

                    if (true == data.success) {     //0表示上传成功(后跟上传后的文件路径),1表示失败(后跟失败描述)
                        $("img[id='" + imgId + "']").attr("src", "" + data.result);
                        $("#" + img).val(data.result);
                    }
                },
                error: function (data, status, e) { //服务器响应失败时的处理函数
                    <#--layer.msg('<@spring.message "goods_msg_requrid10"/>！！', 1, 8);-->
                    $('#result').html('图片上传失败，请重试！！');
                }
            });
        }
        $(function () {
            /*判断跳转链接选中状态*/
            var link=$("#link").val();
            if(link!=""){
                $("#jumps").attr("checked","checked");
            }
            /*提交按钮*/
            $("#subForm").click(function () {
                var Title = $("#title").val();
                if (Title == "") {
                    alert("请输入消息标题！");
                    return false;
                }

                $('#add_form').submit();
            })

        });

        /*判断是否选择跳转路径*/
        $('#jumpPath').change(function(){
            var value=$(this).children('option:selected').val();
            if(value=="homepage"){
                $("#openName").val("辑");
            }
            if(value=="messagepage"){

                $("#openName").val("消息中心");
            }
            if (value=="goodsdetailspage"){
                $("#goodsName").show();
                $("#openName").val("商品详情");
                $("#searchgoods").css("display","");
            }else {
                $("#searchgoods").css("display","none");
                $("#goodsName").css("display","none");
            }
            if(value=="mypage"){
                $("#openName").val("我");
            }
            if(value=="myresultspage"){
                $("#openName").val("个人业绩");
            }
            if(value=="orderpage"){
                $("#openName").val("我的订单");
            }
            if(value=="myintegralpage"){
                $("#openName").val("我的积分");
            }
            if(value=="rewardintegralpage"){
                $("#openName").val("奖励积分");
            }
            if(value=="shoppingintegralpage"){
                $("#openName").val("购物积分");
            }
            if(value=="bankcardpage"){
                $("#openName").val("我的银行卡");
            }
            if(value=="learnpage"){
                $("#openName").val("学堂");
            }
            if(value=="learnarticlepage"){
                $("#articleTitle").show();
                $("#searchlearnarticle").css("display","");
                $("#openName").val("学堂文章详情");
            }else{
                $("#articleTitle").css("display","none");
                $("#searchlearnarticle").css("display","none");
            }
            if(value=="invitationpage"){
                $("#openName").val("我的邀请");
            }
            if(value=="activityGoodsListpage"){
                $("#activityname").show();
                $("#searchactivity").css("display","");
                $("#openName").val("活动页面");
            }else{
                $("#activityname").css("display","none");
                $("#searchactivity").css("display","none");
            }
            if (value=="buyCouponspage"){
                $("#couponName").show();
                $("#searchbuys").css("display","");
                $("#openName").val("优惠券购买详情");

            }else{
                $("#searchbuys").css("display","none");
                $("#couponName").css("display","");

            }

        });

        /*选择学堂文章*/
        function learnarticlepage() {
            var infos = $("[name='info']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择文章',
                content: ['${base}/admin/jpush/findArticles.jhtml?info=' + infos, 'yes'],
                area: ['800px', '600px']
            })
        }
        function appendInfo(name,content,info) {
            $("#articleTitle").val(name);
            $("#contents").val(content);
            var articleContent=$("#contents").val();
            var title=$("#articleTitle").val();
            // console.log(articleContent);
            $("#jsons").val("{\"url\":\"" + articleContent + "\",\"title\":\"" + title + "\"}");

        }
        /*选择活动*/
        function activityGoodsListpage() {
            var info = $("[name='info']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择活动',
                content: ['${base}/admin/jpush/findActivitys.jhtml?info='+info, 'yes'],
                area: ['800px', '600px']
            })
        }
        function appendWareInfo(name,info) {
            $("#activityname").val(name);
            var activityId=$("#activityId").val();
            $("#jsons").val("{\"activityId\":\"" + activityId + "\"}");

        }
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
        function appendGoods(goodsId, goodsName, className) {
            $("#goodsId").val(goodsId);
            $("#goodsName").val(goodsName);
            $("#className").val(className);
            var goodsId=$("#goodsId").val();
            console.log("商品"+goodsId);
            $("#jsons").val("{\"goodsId\":\"" + goodsId + "\"}");

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
            });
        }
        function selSource(id,couponName) {
            $("#couponId").val(id);
            $("#couponName").val(couponName);
            var id= $("#couponId").val();
            console.log("优惠券"+id);
            $("#jsons").val("{\"couponId\":\"" + id + "\"}");
        }

    </script>
    <div class="clear"></div>
</@layout.body>