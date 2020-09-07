<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <link href="${base}/res/css/font/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>
</@layout.head>
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
                        <a href="${base}/admin/travel/travelTicket/list.jhtml"><span>旅游券管理</span></a>
                    </li>
                    <li>
                        <a href="#" class="current"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty">
        </div>
        <form id="add_form" action="${base}/admin/travel/addOrUpdate.jhtml" method="post">
            <table class="table tb-type2">
                <#--<input type="hidden" name="id" <#if shopActivityPromotionRule ??> value="${shopActivityPromotionRule.id}" </#if> />-->
                <tr>
                    <td colspan="2" class="required">旅游券名称：</td>
                </tr>
                <tr class="noborder">
                    <td class="vatop  ">
                        <input type="text" name="travelName" id="ruleTitle" value="${travelName}" class="form-control" maxlength="200"/>
                    </td>
                    <td class="vatop tips"></td>
                </tr>
                <tr class="noborder">
                    <td>
                        <em class="pngFix"></em>旅游券图片
                    </td>
                    <td>
<#--                        <#if coupon??>-->
<#--                            <p>-->
<#--                        <span class="sign">-->
<#--                            <input class="w300 text" name="image" id="image" type="hidden"-->
<#--                                   readonly   value="${coupon.image}"/>-->
<#--                            <img src="${coupon.image!''} " name="image" id="mainPictureImg" nc_type="logo1"-->
<#--                                 width="188"-->
<#--                                 height="144"/>-->
<#--                              </span>-->
<#--                            </p>-->
<#--                        <#else>-->
                            <p>
                        <span class="sign">
                            <input class="w300 text" name="image" id="image" type="hidden" value="${image}"/>
                            <img src="${image!''} " name="image" id="mainPictureImg" nc_type="logo1" width="188" height="144"/>
                              </span>
                            </p>
<#--                        </#if>-->
                        <p><input type="file" class="file" name="myfiles" id="mainPictureImg0" onChange="ajaxFileUploads('mainPictureImg0','mainPictureImg','image');"/></p>
                        <span class="error-message">建议上传图片尺寸351*184</span>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" class="required">面值：</td>
                </tr>
                <tr class="noborder">
                    <td class="vatop  ">
                        <input type="text" name="ticketPrice" id="ticketPrice" value="${ticketPrice}" class="form-control" maxlength="200"/>
                    </td>
                    <td class="vatop tips"></td>
                </tr>
                <tr>
                    <td colspan="2" class="required">优惠券规则：</td>
                </tr>
                <tr class="noborder">
                    <td class="vatop  ">
                        <input type="text" name="remark" id="remark" value="${remark}" class="form-control" maxlength="200"/>
                    </td>
                    <td class="vatop tips"></td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>使用开始时间:
                    </td>
                    <td>
<#--                        <#if coupon??>-->
<#--                            <input class="w300 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"-->
<#--                                   id="useStartTime" name="useStartTime" value="${useStartTime?string("yyyy-MM-dd")}"/>-->
<#--                        <#else>-->
                            <input class="w300 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="useStartTime" name="useStartTime" value=""/>
<#--                        </#if>-->
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>使用结束时间:
                    </td>
                    <td>
<#--                        <#if coupon??>-->
<#--                            <input class="w300 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"-->
<#--                                   id="useEndTime" name="useEndTime" value="${useEndTime?string("yyyy-MM-dd")}"/>-->
<#--                        <#else>-->
                            <input class="w300 Wdate" onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="useEndTime" name="useEndTime" value=""/>
<#--                        </#if>-->
                        <span class="error-message"></span>
                    </td>
                </tr>
            </table>
            <div id="errortest"></div>
            <div class="hr-line-dashed">
                <div class="form-group">
                    <div class="col-sm-12 col-sm-offset-10">
                        <button type="button" class="btn btn-default" onclick="window.history.back();">返回</button>
                        <button class="btn btn-success" id="subForm" type="submit">提交</button>
                    </div>
                </div>
            </div>
            </table>
        </form>
    </div>
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
        $(document).ready(function () {
            $("#add_form").validate({
                rules: {
                    travelName: {
                        required: true,
                        maxlength: 200
                    },
                    ticketPrice: {
                        digits: true,
                        min: 1,
                        max: 255
                    }
                    remark:{
                        required: true
                    },
                    useStartTime:{
                        required: true
                    }
                },
                messages: {
                    travelName: {
                        required: "请输入旅游券名称！",
                        maxlength: '标题最多100个字符'
                    },
                    ticketPrice: {
                        digits: '只能是数字'
                    },
                    remark: {
                        required: "请输入旅游券规则！"
                    },
                    useStartTime:{
                        required: "请选择使用开始时间!"
                    }
                }
            });
            //表单提交
            $("#subForm").click(function () {
                $(":radio").not(":checked").parent().find("input[type='text']").val("");
                if($("#add_form").validate()){
                    $('#add_form').submit();
                }
            });

        });

    </script>
    <div class="clear"></div>
</@layout.body>