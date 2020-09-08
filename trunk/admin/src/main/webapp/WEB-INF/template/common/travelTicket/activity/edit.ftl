<@layout.head>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <link href="${base}/res/css/font/font-awesome/css/font-awesome.min.css" rel="stylesheet"/>
</@layout.head>
<style>
    .required{width:130px;}
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
                        <a href="${base}/admin/travel/travelTicket/list.jhtml"><span>旅游活动管理</span></a>
                    </li>
                    <li>
                        <a href="#" class="current"><span>新增</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty">
        </div>
        <form id="add_form" action="${base}/admin/travel/activity/addOrUpdate.jhtml" method="post">
            <table class="table tb-type2">
                <input type="hidden" name="id" <#if travelActivity ??> value="${travelActivity.id}" </#if> />
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>旅游活动名称
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input type="text" name="activityName" id="activityName" value="${travelActivity.activityName}" class="form-control" maxlength="200"/>
                        <#else>
                            <input type="text" name="activityName" id="activityName" value="${activityName}" class="form-control" maxlength="200"/>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>活动封面
                    </td>
                    <td>
                        <#if travelActivity??>
                            <p>
                        <span class="sign">
                            <input class="w300 text" name="coverImage" id="coverImage" type="hidden"
                                   readonly   value="${travelActivity.coverImage}"/>
                            <img src="${travelActivity.coverImage!''} " name="coverImage" id="mainPictureImg" nc_type="logo1"
                                 width="188"
                                 height="144"/>
                              </span>
                            </p>
                        <#else>
                            <p>
                        <span class="sign">
                            <input class="w300 text" name="coverImage" id="coverImage" type="hidden"
                                   value="${coverImage}"/>
                            <img src="${coverImage!''} " name="coverImage" id="mainPictureImg" nc_type="logo1"
                                 width="188"
                                 height="144"/>
                              </span>
                            </p>
                        </#if>
                        <p><input type="file" class="file" name="myfiles" id="mainPictureImg0"
                                  onChange="ajaxFileUploads('mainPictureImg0','mainPictureImg','coverImage');"/></p>
                        <span class="error-message">建议上传图片尺寸351*184</span>
                    </td>
                </tr>
                <tr class="noborder displays" >
                    <td class="required">
                        <em class="pngFix">活动图片</em>
                    </td>
                    <td>
                        <div class="pic_list">
                            <input type="hidden" id="goods_images_isupload" value="true"/>
                            <ul id="menu" class="menu" >
                                <li class="active" id="li_1">
                                    <a href="javascript:void(0);"
                                       style="background:#3366CC; color:#fff; line-height:22px; height:22px; padding:0 11px; position:relative; margin-right:20px;">
                                        <@spring.message "Local.upload"/>
                                        <input type="file" onChange="moreAjaxFileUploads('gradeImage','photo')"
                                               style="opacity:0; top:0; left:0; width:100%; height:100%; margin:0; position:absolute;"
                                               id="gradeImage" name="myfiles" class="file" multiple="multiple"/>
                                    </a>
                                    <span><@spring.message "message.goods.image"/>(建议上传图片尺寸:750*562,最多9张)</span>
                                </li>
                            </ul>
                            <div class="content">
                                <div id="demo"></div>
                                <div class="standard">
                                    <div>
                                        <ul style="min-height: 130px;overflow:auto;overflow-x: hidden;border: 1px solid #ccc;"
                                            id="photoView01" class="gbin1-list">
                                            <#if imageList??>
                                                <#list imageList as imgSrc>
                                                    <li style='height:120px;display:inline'><img class='img' style='width:100px;height:100px' src='${imgSrc}'/><a href='javascript:void(0)' imageSrc='${imgSrc}' name='deletePhoto'><@spring.message "del"/></a></li>
                                                </#list>
                                            </#if>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>活动状态
                    </td>
                    <td>
                        <#if travelActivity??>
                            <select name="status" id="status">
                                <option value="0" <#if travelActivity.status == 0>selected="selected" </#if>>未开始</option>
                                <option value="1" <#if travelActivity.status == 1>selected="selected" </#if>>报名中</option>
                                <option value="2" <#if travelActivity.status == 2>selected="selected" </#if>>已成团等待开团</option>
                                <option value="3" <#if travelActivity.status == 3>selected="selected" </#if>>已完成</option>
                                <option value="-1"<#if travelActivity.status ==-1>selected="selected" </#if>>已作废</option>
                            </select>
                        <#else>
                            <select name="status" id="status">
                                <option value="0" <#if status == 0>selected="selected" </#if>>未开始</option>
                                <option value="1" <#if status == 1>selected="selected" </#if>>报名中</option>
                                <option value="2" <#if status == 2>selected="selected" </#if>>已成团等待开团</option>
                                <option value="3" <#if status == 3>selected="selected" </#if>>已完成</option>
                                <option value="-1"<#if status ==-1>selected="selected" </#if>>已作废</option>
                            </select>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>活动价格
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input type="text" name="activityCost" id="activityCost" value="${travelActivity.activityCost}" class="form-control" maxlength="200"/>
                        <#else>
                            <input type="text" name="activityCost" id="activityCost" value="${activityCost}" class="form-control" maxlength="200"/>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>参团人数上限(0不限制)
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input type="text" name="numCeiling" id="numCeiling" value="${travelActivity.numCeiling}" class="form-control" maxlength="200"/>
                        <#else>
                            <input type="text" name="numCeiling" id="numCeiling" value="${numCeiling}" class="form-control" maxlength="200"/>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
<#--                <tr class="noborder">-->
<#--                    <td colspan="2" class="required">参团人数上限()：</td>-->
<#--                    <td class="vatop ">-->
<#--                        <input name="notlimit"  type="radio" id="buxian" checked>不限-->
<#--                        <input name="numCeiling" id="buxians"  type="hidden" value="0" style="width:95px;"/>-->
<#--                        <input name="notlimit"   type="radio" id="totalnums" >-->
<#--                        <input name="" type="text" id="ts" value="${numCeiling}"  style="width:95px;"/>-->
<#--                    </td>-->
<#--                    <td class="vatop tips"></td>-->
<#--                </tr>-->
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>活动链接
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input type="text" name="detailLink" id="detailLink" value="${travelActivity.detailLink}" class="form-control" maxlength="200"/>
                        <#else>
                            <input type="text" name="detailLink" id="detailLink" value="${detailLink}" class="form-control" maxlength="200"/>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>活动规则
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input type="text" name="remark" id="remark" value="${travelActivity.remark}" class="form-control" maxlength="200"/>
                        <#else>
                            <input type="text" name="remark" id="remark" value="${remark}" class="form-control" maxlength="200"/>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>报名开始时间:
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input class="w300 Wdate"
                                   onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="startTime" name="startTime" readonly
                                   value="${travelActivity.startTime?string("yyyy-MM-dd")}"/>
                        <#else>
                            <input class="w300 Wdate"
                                   onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="startTime" name="startTime"
                                   value=""/>
                        </#if>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>报名结束时间:
                    </td>
                    <td>
                        <#if travelActivity??>
                            <input class="w300 Wdate"
                                   onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="endTime" name="endTime" readonly
                                   value="${travelActivity.endTime?string("yyyy-MM-dd")}"/>
                        <#else>
                            <input class="w300 Wdate"
                                   onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd'})"
                                   id="endTime" name="endTime"
                                   value=""/>
                        </#if>
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
        //参团人数上限
        $("#totalnums").click(function() {
            $("#ts").attr("name","numCeiling");
            $("#buxians").attr("name","");
        })
        $("#buxian").click(function() {
            $("#ts").attr("name","");
            $("#buxians").attr("name","numCeiling");
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
        $(document).ready(function () {
            $("#add_form").validate({
                rules: {
                    activityName: {
                        required: true,
                        maxlength: 200
                    },
                    activityCost: {
                        digits: true
                    },
                    remark:{
                        required: true
                    },
                    startTime:{
                        required: true
                    }
                },
                messages: {
                    activityName: {
                        required: "请输入旅游活动名称！",
                        maxlength: '标题最多100个字符'
                    },
                    activityCost: {
                        digits: '只能是数字'
                    },
                    remark: {
                        required: "请输入活动规则！"
                    },
                    startTime:{
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