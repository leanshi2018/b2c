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
    .pins{border:1px solid #ccc!important;width:12%;}
    .checkname td,.css{
        border-top: 1px solid #ccc;
        border-bottom: 1px solid #ccc;
        width: 100px;
        text-align: center;
    }
    #articleTitle,#activityname,#goodsName,#couponName,#recommendationName{display: none;width: 209px;}
</style>
<@layout.body>
    <script type="text/javascript" src="${base}/res/js/jquery.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery.validation.min.js"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/ajaxfileupload/ajaxfileupload.js"></script>
    <script type="text/javascript" src="${base}/res/js/ajaxfileupload/ajaxfileupload.js"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css">
    <link rel="stylesheet" type="text/css" href="${base}/resources/css/plugins/colpick/css/colpick.css"/>
    <link rel="stylesheet" href="${base}/resources/css/plugins/timepicker/jquery-ui-timepicker-addon.css"/>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <ul class="tab-base">
                    <li>
                        <a href="${base}/admin/shop_activity_common/findHomePictureList.jhtml"><span>管理</span></a>
                    </li>
                    <li><a href="javascript:void(0)" class="current"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty">
        </div>
        <form id="add_form" action="${base}/admin/shop_activity_common/saveOrUpdateHomePicture.jhtml" method="post">
            <table class="table tb-type2">
                <tbody>
                <tr>
                    <td colspan="3" class="required" style="background: #eee">基本信息</td>
                </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>是否显示
                        </td>
                        <td>
                             <#if picture=="">
                                 <input name="auditStatus" type="radio" value="1"  <#if auditStatus==1||auditStatus==null>checked</#if>>
                            是
                            <input name="auditStatus" type="radio" value="0"  <#if auditStatus==0>checked</#if>>
                            否
                            <#else>
                                  <input name="auditStatus" type="radio" value="1"  <#if picture.auditStatus==1||picture.auditStatus==null>checked</#if>>
                            是
                            <input name="auditStatus" type="radio" value="0"  <#if picture.auditStatus==0>checked</#if>>
                            否
                            </#if>


                            <span class="error-message"></span>
                        </td>
                    </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>商品详情活动入口
                    </td>
                    <td>
                        <#if picture=="">
                            <input name="" type="checkbox" id="checkboxID"><br>
                            <input name="content" type="text" id="contentss"  class="w300">
                            <p style="color: #BBB;line-height: 20px;">*商品详情活动入口标题</p>
                        <#else>
                            <input name="" type="checkbox" id="checkboxID" value="${picture.pictureType}"<#if picture.pictureType==4>checked</#if>><br>
                            <input name="content" type="text" id="contentss" value="${picture.content}"  class="w300">
                            <p style="color: #BBB;line-height: 20px;">*商品详情活动入口标题</p>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>名称
                        </td>
                        <td>
                            <#if picture=="">
                                <input name="pictureName" id="pictureName" type="text" value="${pictureName}" class="w200"/>
                            <#else>
                                <input name="pictureName" id="pictureName" type="text" value="${picture.pictureName}" class="w200"/>
                            </#if>

                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>主图
                        </td>
                        <td>
                            <#if picture=="">
                                <span class="sign">
                            <input class="w300 text" name="pictureUrl" id="pictureUrl" type="hidden"value="${pictureUrl}"/>
                                 <input class="w300 text" name="pictureType" id="pictureType" type="hidden" value="2"/>
                            <img src="${pictureUrl!''}" name="pictureUrl" id="mainPictureImg" nc_type="logo1" width="188" height="144"/>
                              </span>
                                </p>
                                <p><input type="file" class="file" name="myfiles" id="mainPictureImg0"
                                          onChange="ajaxFileUploads('mainPictureImg0','mainPictureImg','pictureUrl');"/></p>
                                <span class="error-message">建议上传图片尺寸222*296px</span>
                            <#else>
                                <span class="sign">
                            <input class="w300 text" name="pictureUrl" id="pictureUrl" type="hidden"value="${picture.pictureUrl}"/>
                                 <input class="w300 text" name="pictureType" id="pictureType" type="hidden" value="2"/>
                            <img src="${picture.pictureUrl!''}" name="pictureUrl" id="mainPictureImg" nc_type="logo1" width="188" height="144"/>
                              </span>
                                </p>
                                <p><input type="file" class="file" name="myfiles" id="mainPictureImg0"
                                          onChange="ajaxFileUploads('mainPictureImg0','mainPictureImg','pictureUrl');"/></p>
                                <span class="error-message">建议上传图片尺寸222*296px</span>
                            </#if>
                            <p>

                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>排序数字
                        </td>
                        <td>
                            <#if picture=="">
                                <input name="pSort" id="pSort" type="text" value="${pSort}" class="w200"/>
                            <#else>
                                <input name="pSort" id="pSort" type="text" value="${picture.PSort}" class="w200"/>
                            </#if>

                            <span class="error-message"></span>
                        </td>
                    </tr>
                    <tr class="noborder">
                        <td class="required">
                            <em class="pngFix"></em>打开方式
                        </td>
                        <td>
                            <select name="openType" class="w200" id="openType">
                                <#if picture=="">
                                    <option value="" selected="selected">请选择</option>
                                    <option value="跳转路径" id=""<#if picture.openType == "跳转路径">selected="selected" </#if>>跳转路径</option>
                                    <option value="跳转链接" id=""<#if picture.openType == "跳转链接">selected="selected" </#if>>跳转链接</option>
                                </#if>
                                <#if picture!=null>
                                    <option value="跳转路径" <#if picture.jumpName == "凑单页面"||picture.jumpName == "推荐页面"||picture.jumpName == '活动页面'||picture.jumpName == "辑"
                                    ||picture.jumpName == "消息中心"||picture.jumpName == "商品详情"||picture.jumpName == "我"||picture.jumpName == "个人业绩"
                                    ||picture.jumpName == "我的订单"||picture.jumpName == "我的积分"||picture.jumpName == "奖励积分"||picture.jumpName == "换购积分"
                                    ||picture.jumpName == "购物积分"||picture.jumpName == "我的银行卡"||picture.jumpName == "学堂"||picture.jumpName == "学堂文章详情"
                                    ||picture.jumpName == "我的邀请"||picture.jumpName == "优惠券购买详情"||picture.jumpName == "PLUS会员中心">selected="selected" </#if>>跳转路径</option>
                                    <option value="跳转链接" <#if picture.jumpInterface!= "">selected="selected" </#if>>跳转链接</option>
                                </#if>
                            </select>
                            <span class="error-message"></span>
                        </td>
                    </tr>

                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>选择（填入）跳转
                    </td>
                    <td>
                        <#if picture=="">
                            <div class="col-sm-9">
                                <div class="col-lg-1" STYLE="width: 90%;">
                                    <#--选择跳转商品推荐页-->
                                    <select name="" class="w200" id="toactitvty" style="display: none;">
                                        <option value="" selected="selected">请选择</option>
                                    </select>
                                    <#--选择跳转链接-->
                                    <input name="jumpInterface" id="jumpInterface" type="text" value="${picture.jumpInterface}" class="w200"style="display: none;"/>
                                    <#--选择跳转路径-->
                                    <select name="openPage" class="w200" id="openPage"style="display: none;">
                                        <option value="" selected="selected">请选择</option>
                                        <option value="gatherGoodspage" <#if picture.openPage == 'gatherGoodspage'>selected="selected"</#if>>凑单页面</option>
                                        <option value="recommendGoodspage" <#if picture.openPage == 'recommendGoodspage'>selected="selected"</#if>>推荐页面</option>
                                        <option value="activityGoodsListpage" <#if picture.openPage == 'activityGoodsListpage'>selected="selected"</#if>>商品活动页面</option>
                                        <option value="plusCenterpage" <#if picture.openPage == 'plusCenterpage'>selected="selected"</#if>>PLUS会员中心</option>
                                        <option value="homepage" <#if picture.openPage == 'homepage'>selected="selected"</#if>>辑</option>
                                        <option value="messagepage"<#if picture.openPage == "messagepage">selected="selected" </#if>>消息中心</option>
                                        <option value="goodsdetailspage" id="goodsdetailspage" <#if picture.openPage == "goodsdetailspage">selected="selected" </#if>>商品详情</option>
                                        <option value="mypage"  <#if picture.openPage == "mypage">selected="selected" </#if>>我</option>
                                        <option value="myresultspage" <#if picture.openPage == "myresultspage">selected="selected" </#if>>个人业绩</option>
                                        <option value="orderpage"    <#if picture.openPage == "orderpage">selected="selected" </#if>>我的订单</option>
                                        <option value="myintegralpage" <#if picture.openPage == "myintegralpage">selected="selected" </#if>>我的积分</option>
                                        <option value="rewardintegralpage" <#if picture.openPage == "rewardintegralpage">selected="selected" </#if>>奖励积分</option>
                                        <option value="shoppingintegralpage" <#if picture.openPage == "shoppingintegralpage">selected="selected" </#if>>购物积分</option>
                                        <option value="buyintegralpage" <#if picture.openPage == "buyintegralpage">selected="selected" </#if>>换购积分</option>
                                        <option value="bankcardpage" <#if picture.openPage == "bankcardpage">selected="selected" </#if>>我的银行卡</option>
                                        <option value="learnpage" <#if picture.openPage == "learnpage">selected="selected" </#if>>学堂</option>
                                        <option value="learnarticlepage" <#if picture.openPage == "learnarticlepage">selected="selected" </#if>>学堂文章详情</option>
                                        <option value="invitationpage" <#if picture.openPage == "invitationpage">selected="selected" </#if>>我的邀请</option>
                                        <option value="activityGoodsListpage" <#if picture.openPage =="activityGoodsListpage">selected="selected" </#if>>活动页面</option>
                                        <option value="buyCouponspage" id="buyCouponspage" <#if picture.openPage == 'buyCouponspage'>selected="selected" </#if>>优惠券购买详情</option>
                                    </select>
                                    <#--选择文章-->
                                    <input type="hidden" class="text w500" value="${article.articleContent}" name="articleContent" id="contents">
                                    <input type="text" class="text w500" value="${article.articleTitle}" name="articleTitle" id="articleTitle">
                                    <input type="hidden" class="text w500" value="${article.id}" name="id" >
                                    <#--选择活动-->
                                    <#--选择活动的名称显示-->
                                    <input name="activityname" type="text" id="activityname" value="${picture.pictureName}"/>
                                    <input name="activityId" id="activityId" type="hidden" value="${shopActivity.id}"/>
                                    <input name="info" id="info" type="hidden" value="${shopActivity.info}"/>
                                    <#-- 选择推荐页 -->
                                    <input name="recommendationName" type="text" id="recommendationName" value="${recommendationName}"/>
                                    <input  id="rId" type="hidden" value="${id}"/>
                                    <#--选择商品-->
                                    <form id="recommend_form" method="post" name="recommendForm" action="${base}/admin/shop_goods_recommend/edit.jhtml">
                                        <input class="pins" type="hidden" id="goodsId" name="goodsId" value="<#if shopGoods??>${shopGoods.id}</#if>">
                                        <input class="pins" type="text" value="<#if shopGoods??>${shopGoods.goodsName}</#if>" name="goodsName" id="goodsName">
                                        <input class="pins" type="hidden" value="<#if shopGoods??>${shopGoods.className}</#if>" name="className" id="className">
                                    </form>
                                    <#--选择的名称-->
                                    <input type="hidden" id="descName" name="descName" value="">
                                    <#--选择优惠券-->
                                    <input type="hidden" id="couponId" name="couponId" value="${id}">
                                    <#--<input type="hidden" id="couponLikeName" name="couponLikeName" value="${couponLikeName}">-->
                                    <input name="couponName" id="couponName" type="text" value="${couponName}">
                                    <#--映射的名字-->
                                    <input name="openName" id="openName"class="w150" type="hidden" value=""/>
                                    <#--拼接的json-->
                                    <input name="jumpJson" class="w150" id="jsons" value="${jumpJson}" type="hidden" />
                                    <p style="display:none;" id="pictureJson"></p>
                                    <#--搜索商品的按鈕-->
                                    <a class="btn-search" id="searchgoods" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="goodsdetailspage()"></a>
                                    <#--搜索优惠券-->
                                    <a class="btn-search" id="searchbuys" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="buyCouponspage()"></a>
                                    <a class="btn-search" id="searchactivity" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="activityGoodsListpage()"></a>
                                    <a class="btn-search" id="searchrecommend" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="recommendpage()"></a>
                                    <a class="btn-search" id="searchlearnarticle" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="learnarticlepage()"></a>
                                </div>
                            </div>
                        </#if>
                        <#if picture!=null>
                            <input type="hidden" name="id" value="${picture.id}" />
                            <div class="col-sm-9">
                                <div class="col-lg-1" STYLE="width: 90%;">
                                    <#--选择跳转链接-->
                                    <input name="jumpInterface" id="jumpInterface" type="text" value="${picture.jumpInterface}" style="display: none;width:353px;"/>
                                    <#--选择跳转路径-->
                                    <select name="openPage" class="w200" id="openPage"style="display: none;">
                                        <option value="" selected="selected">请选择</option>
                                        <option value="gatherGoodspage" <#if picture.activityUrl == 'gatherGoodspage'>selected="selected"</#if>>凑单页面</option>
                                        <option value="recommendGoodspage" <#if picture.activityUrl == 'recommendGoodspage'>selected="selected"</#if>>推荐页面</option>
                                        <option value="activityGoodsListpage" <#if picture.activityUrl == 'activityGoodsListpage'>selected="selected"</#if>>商品活动页面</option>
                                        <option value="plusCenterpage" <#if picture.activityUrl == 'plusCenterpage'>selected="selected"</#if>>PLUS会员中心</option>
                                        <option value="homepage" <#if picture.activityUrl == 'homepage'>selected="selected"</#if>>辑</option>
                                        <option value="messagepage"<#if picture.activityUrl == "messagepage">selected="selected" </#if>>消息中心</option>
                                        <option value="goodsdetailspage" id="goodsdetailspage" <#if picture.activityUrl == "goodsdetailspage">selected="selected" </#if>>商品详情</option>
                                        <option value="mypage"  <#if picture.activityUrl == "mypage">selected="selected" </#if>>我</option>
                                        <option value="myresultspage" <#if picture.activityUrl == "myresultspage">selected="selected" </#if>>个人业绩</option>
                                        <option value="orderpage"    <#if picture.activityUrl == "orderpage">selected="selected" </#if>>我的订单</option>
                                        <option value="myintegralpage" <#if picture.activityUrl == "myintegralpage">selected="selected" </#if>>我的积分</option>
                                        <option value="rewardintegralpage" <#if picture.activityUrl == "rewardintegralpage">selected="selected" </#if>>奖励积分</option>
                                        <option value="shoppingintegralpage" <#if picture.activityUrl == "shoppingintegralpage">selected="selected" </#if>>购物积分</option>
                                        <option value="buyintegralpage" <#if picture.activityUrl == "buyintegralpage">selected="selected" </#if>>换购积分</option>
                                        <option value="bankcardpage" <#if picture.activityUrl == "bankcardpage">selected="selected" </#if>>我的银行卡</option>
                                        <option value="learnpage" <#if picture.activityUrl == "learnpage">selected="selected" </#if>>学堂</option>
                                        <option value="learnarticlepage" <#if picture.activityUrl == "learnarticlepage">selected="selected" </#if>>学堂文章详情</option>
                                        <option value="invitationpage" <#if picture.activityUrl == "invitationpage">selected="selected" </#if>>我的邀请</option>
                                        <option value="buyCouponspage"  <#if picture.activityUrl == 'buyCouponspage'>selected="selected" </#if>>优惠券购买详情</option>
                                    </select>
                                    <#--选择的名称-->
                                    <input type="hidden" id="descName" name="descName" value="${picture.descName}">
                                    <#--选择文章-->
                                    <input type="text" class="text w500" value="${article.articleTitle}" name="articleTitle" id="articleTitle">
                                    <input type="hidden" class="text w500" value="${article.id}" name="id" >
                                    <input type="hidden" class="text w500" value="${article.articleContent}" name="articleContent" >
                                    <#--选择活动-->
                                    <input name="name" type="text" id="activityname" value=""/>
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
                                    <#--<input type="hidden" id="couponLikeName" name="couponLikeName" value="${couponLikeName}">-->
                                    <input name="couponName" id="couponName" type="text" value="${couponName}">
                                    <#--映射的名字-->
                                    <input name="openName" id="openName"class="w150" type="hidden" value="${picture.pictureName}"/>
                                    <#--拼接的json-->
                                    <input name="jumpJson" class="w150" id="jsons" value="" style="height:23px;display:none;"/>
                                    <p style="display:none;" id="pictureJson">${picture.pictureJson}</p>
                                    <#--搜索商品的按鈕-->
                                    <a class="btn-search" id="searchgoods" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="goodsdetailspage()"></a>
                                    <#-- 选择推荐页 -->
                                    <input name="recommendationName" type="text" id="recommendationName" value="${recommendationName}"/>
                                    <input  id="rId" type="hidden" value="${id}"/>
                                    <#--搜索优惠券-->
                                    <a class="btn-search" id="searchbuys" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="buyCouponspage()"></a>
                                    <a class="btn-search" id="searchactivity" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="activityGoodsListpage()"></a>
                                    <a class="btn-search" id="searchrecommend" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="recommendpage()"></a>
                                    <a class="btn-search" id="searchlearnarticle" style="background-color:#ccc;margin-left:-49px;margin-top:-3px;border: none;display:none;" onclick="learnarticlepage()"></a>
                                </div>
                            </div>
                        </#if>
                    </td>
                    </tr>
                </tbody>
                <tfoot>
                <tr>
                    <td></td>
                    <td>
                        <a class="btn" href="javascript:history.go(-1);"
                           style="float:left"><span><@spring.message "button.back"/></span></a>
                        <a class="btn btn-success" id="subForm" type="submit">提交</a>
                    </td>
                </tr>
                </tfoot>
            </table>
        </form>
    </div>
    <script type="text/javascript" src="${base}/resources/js/plugins/colpick/colpick.js"></script>
    <script>
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
        /*判断是否选择打开方式*/
        $('#openType').change(function() {
            var value = $(this).children('option:selected').val();
            if (value == "跳转路径") {
                $("#openPage").css("display","");
                $("#openPage").attr("name","openPage");
                $("#openName").attr("name","openName");
                $("#jumpInterface").attr("name","");
                $("#jumpInterface").css("display", "none");
                $("#jsons").attr("name","jumpJson");
            }
            if (value == "跳转链接") {
                console.log(value)
                $("#jumpInterface").attr("name","jumpInterface");
                $("#jumpInterface").css("display","");
                $("#openPage").css("display","none");
                $("#openPage").attr("name","");
                $("#activityname").css("display","none");
                $("#searchactivity").css("display","none");
                $("#openName").attr("name","");
                $("#recommendationName").css("display","none");
                $("#jsons").attr("name","");

            }
        })
        /*判断是否选择跳转路径*/
        $('#openPage').change(function(){
            $("#openpages").attr("name","");
            var value=$(this).children('option:selected').val();
            if(value=="gatherGoodspage"){
                $("#openName").val("凑单页面");
            }
            if(value=="recommendGoodspage"){
                $("#openName").val("推荐页面");
                $("#recommendationName").show();
                $("#searchrecommend").css("display","");
            }else{
                $("#recommendationName").css("display","none");
                $("#searchrecommend").css("display","none");
            }
            if(value=="recommendGoodspage"){
                $("#openName").val("活动页面");
                $("#activityname").show();
                $("#searchactivity").css("display","");
            }else{
                $("#activityname").css("display","none");
                $("#searchactivity").css("display","none");
            }
            if(value=="plusCenterpage"){
                $("#openName").val("PLUS会员中心");
            }
            if(value=="homepage"){
                $("#openName").val("辑");
            }
            if(value=="messagepage"){
                $("#openName").val("消息中心");
            }
            if (value=="goodsdetailspage"){
                $("#goodsName").show();
                $("#searchgoods").css("display","");
                $("#openName").val("商品详情");
                $("#recommendationName").css("display","none");
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
                $("#recommendationName").css("display","none");
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
                $("#recommendationName").css("display","none");
            }else{
                $("#activityname").css("display","");
                $("#searchs").attr("onclick","");
            }
            if (value=="buyCouponspage"){
                $("#couponName").show();
                $("#searchbuys").css("display","");
                $("#openName").val("优惠券购买详情");
                $("#recommendationName").css("display","none");
            }else{
                $("#searchbuys").css("display","none");
                $("#couponName").css("display","");
            }
        });
        //提交参数和判断选择方式及传参
        $(function () {

            var value =  $('#openType option:selected').val();
            console.log(value);
            if (value == "跳转路径") {
                $("#openPage").css("display","");
                $("#openPage").attr("name","openPage");
                $("#openName").attr("name","openName");
                $("#jumpInterface").attr("name","");
                $("#jumpInterface").css("display", "none");
                $("#jsons").attr("name","jumpJson");
            }
            if (value == "跳转链接") {
                $("#jumpInterface").attr("name","jumpInterface");
                $("#jumpInterface").css("display","");
                $("#openPage").css("display","none");
                $("#openPage").attr("name","");
                $("#activityname").css("display","none");
                $("#searchactivity").css("display","none");
                $("#openName").attr("name","");
                $("#recommendationName").css("display","none");
                $("#jsons").attr("name","");
            }


            //取jsons编辑页面不修改状态默认传过来的参数
            var jsonstr=$("#pictureJson").html();
            console.log(jsonstr);
            if(jsonstr!=""){
                var vals = $('#openPage option:selected').val();
                console.log(vals);
                var descName=$('#descName').val();
                console.log(descName);
                if(vals=="recommendGoodspage"){
                    $("#recommendationName").val(descName);
                    $("#openName").val("推荐页面");
                    $("#recommendationName").show();
                    $("#searchrecommend").css("display","");
                    var rId = jsonstr.replace(/[^0-9]/ig,"");
                    $("#jsons").val("{\"rId\":\"" + rId + "\"}");
                }else{
                    $("#recommendationName").css("display","none");
                }
                if(vals=="gatherGoodspage"){
                    $("#openName").val("凑单页面");
                }
                if(value=="plusCenterpage"){
                    $("#openName").val("PLUS会员中心");
                }
                if(vals=="homepage"){
                    $("#openName").val("辑");
                }
                if(vals=="messagepage"){
                    $("#openName").val("消息中心");
                }
                if (vals=="goodsdetailspage"){
                    $("#goodsName").val(descName);
                    $("#goodsName").show();
                    $("#searchgoods").css("display","");
                    $("#openName").val("商品详情");
                    var goodsId = jsonstr.replace(/[^0-9]/ig,"");
                    $("#jsons").val("{\"goodsId\":\"" + goodsId + "\"}");
                }
                if(vals=="mypage"){
                    $("#openName").val("我");
                }
                if(vals=="myresultspage"){
                    $("#openName").val("个人业绩");
                }
                if(vals=="orderpage"){
                    $("#openName").val("我的订单");
                }
                if(vals=="myintegralpage"){
                    $("#openName").val("我的积分");
                }
                if(vals=="rewardintegralpage"){
                    $("#openName").val("奖励积分");
                }
                if(vals=="shoppingintegralpage"){
                    $("#openName").val("购物积分");
                }
                if(vals=="bankcardpage"){
                    $("#openName").val("我的银行卡");
                }
                if(vals=="learnpage"){
                    $("#openName").val("学堂");
                }
                if(vals=="learnarticlepage"){
                    $("#articleTitle").val(descName);
                    $("#articleTitle").show();
                    $("#searchlearnarticle").css("display","");
                    $("#openName").val("学堂文章详情");
                    // $("#jsons").val("{\"url\":\"" + articleContent + "\",\"title\":\"" + title + "\"}");
                }
                if(vals=="invitationpage"){
                    $("#openName").val("我的邀请");
                }
                if(vals=="activityGoodsListpage"){
                    $("#activityname").val(descName);
                    $("#activityname").show();
                    $("#searchactivity").css("display","");
                    $("#openName").val("活动页面");
                    var activityId = jsonstr.replace(/[^0-9]/ig,"");
                    $("#jsons").val("{\"activityId\":\"" + activityId + "\"}");
                }
                if (vals=="buyCouponspage"){
                    $("#couponName").val(descName);
                    $("#couponName").show();
                    $("#searchbuys").css("display","");
                    $("#openName").val("优惠券购买详情");
                    var couponId = jsonstr.replace(/[^0-9]/ig,"");
                    $("#jsons").val("{\"couponId\":\"" + couponId + "\"}");
                }
            }

            /*提交按钮*/
            $("#subForm").click(function () {
                var Title = $("#pictureName").val();
                if (Title == "") {
                    alert("请输入名称！");
                    return false;
                }
                var num = $("#pSort").val();
                if (num == "") {
                    alert("请输入排序数字！");
                    return false;
                }
                var num = $("#pSort").val();
                if (num == "") {
                    alert("请输入排序数字！");
                    return false;
                }
                var tf=$("input[type='checkbox']").is(':checked');
                if(tf==true){
                    $("#pictureType").val("4");
                    console.log("checkbox is checked");
                    var contents = $("#contentss").val();
                    if (contents == "") {
                        alert("请输入商品详情活动入口标题！");
                        return false;
                    }
                }
                $('#add_form').submit();
            })

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
            console.log(articleContent);
            $("#jsons").val("{\"url\":\"" + articleContent + "\",\"title\":\"" + title + "\"}");
            $("#descName").val(name);

        }
        /*选择推荐页*/
        function recommendpage() {
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择推荐页',
                content: ['${base}/admin/shop_activity_common/selectProductsRecommendationList.jhtml', 'yes'],
                area: ['800px', '600px']
            })
        }
        function appendGoodsAll(id,name) {
            $("#recommendationName").val(name);
            $("#rId").val(id);
            var rId=$("#rId").val();
            $("#jsons").val("{\"rId\":\"" + rId + "\"}");
            $("#descName").val(name);

        }
        /*选择活动*/
        function activityGoodsListpage() {
            var info = $("[name='info']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择活动',
                content: ['${base}/admin/jpush/findActivityings.jhtml?info='+info, 'yes'],
                area: ['800px', '600px']
            })

        }
        function appendWareInfo(id,name,info) {

            $("#activityname").val(name);
            $("#activityId").val(id);
            var activityId=$("#activityId").val();

            $("#jsons").val("{\"activityId\":\"" + activityId + "\"}");
            $("#descName").val(name);

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
            $("#descName").val(goodsName);

        }
        /*选择优惠券*/
        function buyCouponspage() {
            var couponName = $("[name='couponName']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择优惠券',
                content: ['${base}/admin/plarformShopCoupon/coupon/select.jhtml?status=2', 'yes'],
                area: ['800px', '600px']
            });
        }
        function selSource(id,couponName) {
            $("#couponId").val(id);
            $("#couponName").val(couponName);
            console.log($("#couponName").val());
            var id= $("#couponId").val();
            console.log("优惠券"+id);
            $("#jsons").val("{\"couponId\":\"" + id + "\"}");
            $("#descName").val(couponName);
        }

    </script>
    <div class="clear"></div>
</@layout.body>