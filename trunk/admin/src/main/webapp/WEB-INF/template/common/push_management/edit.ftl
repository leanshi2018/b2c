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
                        <a href=""><span>管理</span></a>
                    </li>
                    <li><a href="javascript:void(0)" class="current"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty">
        </div>
        <form id="add_form" action="" method="post">
            <table class="table tb-type2">
                <tbody>
                <tr>
                    <td colspan="3" class="required" style="background: #eee">基本信息</td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>推送方式
                    </td>
                    <td>
                            <input name="couponName" id="couponName" type="text" value="${couponName}"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>通知内容
                    </td>
                    <td>
                            <input name="scopeRemark" id="scopeRemark" type="text" value="${scopeRemark}" class="w300"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>推送时间
                    </td>
                    <td>
                            <input class="w300 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="sendStartTime" name="sendStartTime" value=""/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>目标平台
                    </td>
                    <td>
                        <label><input name="rankLimit" class="" type="checkbox" value="" />安卓</label>
                        <label><input name="rankLimit" class="" type="checkbox" value="" />IOS正式</label>
                        <label><input name="rankLimit" class="" type="checkbox" value="" />IOS测试</label>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>推送对象
                    </td>
                    <td>
                        <input name="" type="radio" value="1" readonly <#if coupon.whetherPresent==1>checked</#if>>
                        全部用户
                        <input name="" type="radio" value="0" readonly <#if coupon.whetherPresent==0 >checked</#if>>
                        指定用户
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>
                    </td>
                    <td colspan="2" class="vatop ">
                        <div class="col-sm-9">
                            <div class="col-lg-1" STYLE="width: 90%;display: none;">
                                <input type="radio" name="" value="1"  id="" <#if reduceType ==1> checked </#if> >
                                <span >跳转路径</span>
                                <input name="" type="text" id=""   style="width:95px;"
                                        <#if reduceType ==1> value="${couponValue}"</#if> />
                                <i class="fa fa-search"></i>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>
                    </td>
                    <td colspan="2" class="vatop ">
                        <div class="col-sm-9">
                            <#--满折扣-->
                            <div class="col-lg-1" STYLE="width: 90%;display: none;" >
                                <input type="radio" name="" value="3" id=""  <#if reduceType ==3> checked </#if> >
                                <span >跳转链接</span>
                                <input name=""  type="text" id="" style="width:95px;"   <#if reduceType ==3> value="${couponValue}"</#if> />
                            </div>
                        </div>
                    </td>
                </tr>
                </tbody>
                <tfoot>
                <tr>
                    <td></td>
                    <td>
                        <#if coupon??>
                            <a class="btn" href="javascript:history.go(-1);"
                               style="float:left"><span><@spring.message "button.back"/></span></a>
                        <#else>
                            <a class="btn" href="javascript:history.go(-1);"
                               style="float:left"><span><@spring.message "button.back"/></span></a>
                            <a class="btn btn-success" id="subForm" type="submit">提交</a>
                        </#if>
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

        $(function () {
            //表单提交
            $("#subForm").click(function () {
                var Title = $("#couponName").val();
                if (Title == "") {
                    alert('!');
                    return false;
                }
                var image=$("#image").val();
                if (image == "") {
                    alert('!');
                    return false;
                }
                var scopeRemark=$("#scopeRemark").val();
                if (scopeRemark == "") {
                    alert('!');
                    return false;
                }

                $('#add_form').submit();
            })

        });

        /*选择商品*/
        $("#single").click(function () {
            var goodsId = $("[name='goodsId']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择商品',
                content: ['${base}/admin/shop_goods_recommend/select.jhtml?type=1&goodsId=' + goodsId, 'yes'],
                area: ['800px', '600px']
            });
        });
        function appendGoods(goodsId, goodsName, className, brandName,goodsType) {
            $("#goodsId").val(goodsId);
            $("#goodsName").val(goodsName);
            $("#className").val(className);

        }
        /*选择优惠券*/
        $("#check").click(function () {
            var couponName = $("[name='couponName']").val();
            layer.open({
                type: 2,
                move: false,
                shade: [0.3, '#393D49'],//开启遮罩层
                title: '选择优惠券',
                content: ['${base}/admin/plarformShopCoupon/coupon/select.jhtml?status=2&couponName=' + couponName, 'yes'],
                area: ['800px', '600px']
            });
        });
        function appendGoods(id,couponName) {
            $("#couponId").val(id);
            $("#couponName").val(couponName);
        }


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
    </script>
    <div class="clear"></div>
</@layout.body>