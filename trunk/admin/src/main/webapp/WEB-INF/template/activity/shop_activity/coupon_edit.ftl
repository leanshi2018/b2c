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
                        <a href="${base}/admin/plarformShopActivity/activity/${activityType}/list.jhtml"><span><@spring.message "activity.list"/></span></a>
                    </li>
                    <li><a href="javascript:void(0)" class="current"><span><@spring.message "Add_activity"/></span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty">
        </div>

        <form id="add_form" action="${base}/admin/plarformShopActivity/saveActivityBaseInfo/${activityType}/save.jhtml"
              method="post">
            <table class="table tb-type2">
                <input class="w300 text" id="type" name="type" type="hidden" value="${activityType}">
                <input type="hidden" id="stockNum" <#if goods ??> value="${goods.specGoodsStorage!0}" </#if>/>
                <input type="hidden" id="priceNum" <#if goods ??> value="${goods.specGoodsPrice!0}" </#if>/>
                <tbody>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>
                    </td>
                    <td>
                        <input name="auditStatus" type="radio" value="1">
                        手动领取&nbsp;&nbsp;
                        <input name="auditStatus" type="radio" value="0">

                         自动发放
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>优惠券类型
                    </td>
                    <td>
                        <select name="promotionRuleId">
                            <#if ruleList??>
                                <#list ruleList as rule >
                                    <option value="${rule.id}" <#if rule.id == shopActivity.promotionRuleId>
                                        selected="selected" </#if>>${rule.ruleTitle}</option>
                                </#list>
                            </#if>
                        </select>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>优惠券名称
                    </td>
                    <td>
                        <input name="activityName" type="text" value="${shopActivity.activityName}"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td>
                        <em class="pngFix"></em>优惠券图片
                    </td>
                    <td>
                        <p>
                        <span class="sign">
                            <input class="w300 text" name="activityPicturePc" id="activityPicturePc" type="hidden"
                                   value="${shopActivity.activityPicturePc}"/>
                            <img src="${shopActivity.activityPicturePc!''}  " id="listPictureImg"
                                 nc_type="logo1" width="88" height="44"/>
                              </span>
                        </p>
                        <p><input type="file" class="file" name="myfiles" id="listPictureImg0"
                                  onChange="ajaxFileUploads('listPictureImg0','listPictureImg','activityPicturePc');"/></p>
                        <span class="error-message">(建议上传图片尺寸750*420)</span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>优惠券说明
                    </td>
                    <td>
                        <input name="activityDescription" type="text" value="${shopActivity.activityDescription}"
                               class="w300"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr>优惠券规则</tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>发放数量
                    </td>
                    <td>
                        <input name="auditStatus" type="radio" value="1">
                       不限
                        <input name="auditStatus" type="radio" value="">
                        <input name="activityName" type="text" value=""/>张
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>可领取等级
                    </td>
                    <td>
                        <select name="promotionRuleId">
                            <#if ruleList??>
                                <#list ruleList as rule >
                                    <option value="${rule.id}" <#if rule.id == shopActivity.promotionRuleId>
                                        selected="selected" </#if>>${rule.ruleTitle}</option>
                                </#list>
                            </#if>
                        </select>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>可用范围
                    </td>
                    <td>
                        <input name="auditStatus" type="radio" value="1">
                        全品类&nbsp;&nbsp;
                        <input name="auditStatus" type="radio" value="0">
                        指定品类可用
                        <input name="auditStatus" type="radio" value="1">
                        品牌&nbsp;&nbsp;
                        <input name="auditStatus" type="radio" value="0">
                        店铺
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>可领取数量
                    </td>
                    <td>
                        <select name="promotionRuleId">
                            <#if ruleList??>
                                <#list ruleList as rule >
                                    <option value="${rule.id}" <#if rule.id == shopActivity.promotionRuleId>
                                        selected="selected" </#if>>${rule.ruleTitle}</option>
                                </#list>
                            </#if>
                        </select>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>每单可用数量
                    </td>
                    <td>
                        <select name="promotionRuleId">
                            <#if ruleList??>
                                <#list ruleList as rule >
                                    <option value="${rule.id}" <#if rule.id == shopActivity.promotionRuleId>
                                        selected="selected" </#if>>${rule.ruleTitle}</option>
                                </#list>
                            </#if>
                        </select>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>每人可用数量
                    </td>
                    <td>
                        <select name="promotionRuleId">
                            <#if ruleList??>
                                <#list ruleList as rule >
                                    <option value="${rule.id}" <#if rule.id == shopActivity.promotionRuleId>
                                        selected="selected" </#if>>${rule.ruleTitle}</option>
                                </#list>
                            </#if>
                        </select>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>能否赠送
                    </td>
                    <td>
                        <input name="auditStatus" type="radio" value="1">
                        能
                        <input name="auditStatus" type="radio" value="0">
                        否
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <span class="red">*</span> <em class="pngFix"></em>领取开始时间:
                    </td>
                    <td>
                        <input class="w300 Wdate"
                               onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                               id="startTimeStr" name="startTimeStr"
                               value="${shopActivity.startTime?string("yyyy-MM-dd HH:mm:ss")}"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <span class="red">*</span> <em class="pngFix"></em>领取结束时间:
                    </td>
                    <td>
                        <input class="w300 Wdate"
                               onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                               name="endTimeStr" id="endTimeStr"
                               value="${shopActivity.endTime?string("yyyy-MM-dd HH:mm:ss")}"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <span class="red">*</span> <em class="pngFix"></em>使用开始时间:
                    </td>
                    <td>
                        <input class="w300 Wdate"
                               onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                               id="startTimeStr" name="startTimeStr"
                               value="${shopActivity.startTime?string("yyyy-MM-dd HH:mm:ss")}"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <span class="red">*</span> <em class="pngFix"></em>使用结束时间:
                    </td>
                    <td>
                        <input class="w300 Wdate"
                               onFocus="WdatePicker({skin:'twoer',lang:'zh-cn',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                               name="endTimeStr" id="endTimeStr"
                               value="${shopActivity.endTime?string("yyyy-MM-dd HH:mm:ss")}"/>
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr>优惠设置</tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>优惠门槛
                    </td>
                    <td>

                        满<input name="activityName" type="text" value=""/>MI
                        满<input name="activityName" type="text" value=""/>元
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>优惠方式
                    </td>
                    <td>
                        <input name="auditStatus" type="radio" value="1">
                        减<input name="activityName" type="text" value=""/>元
                        <input name="auditStatus" type="radio" value="0">
                        打<input name="activityName" type="text" value=""/>折
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required" width="150px">
                        <em class="pngFix"></em>领券方式
                    </td>
                    <td>
                        <input name="auditStatus" type="radio" value="1">
                        免费领取
                        <input name="auditStatus" type="radio" value="0">
                        付费领取
                        <span class="error-message"></span>
                    </td>
                </tr>
                <tr class="noborder">
                    <td class="required">
                        <em class="pngFix"></em>领券价格
                    </td>
                    <td>
                        <input name="activityName" type="text" value="${shopActivity.activityName}"/>元
                        <span class="error-message"></span>
                    </td>
                </tr>
                </tbody>
                <tfoot>
                <tr>
                    <td></td>
                    <td>
                        <a class="btn" href="javascript:history.go(-1);"
                           style="float:left"><span><@spring.message "button.back"/></span></a>
                        <#if edit?? >
                            <a href="JavaScript:void(0);" class="btn btn-submit"><span>提交</span></a>
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

        //按钮先执行验证再提交表单
        $(function () {

            $(".btn-submit").click(function () {


                //判断日期
                var startTime = $("#startTimeStr").val();
                var endTime = $("#endTimeStr").val();
                if (startTime == null || startTime == "") {
                    alert("请填写活动开始时间!")
                    return false;
                }
                if (endTime == null || endTime == "") {
                    alert("请填写活动结束时间！");
                    return false;
                }
                var start = new Date(startTime.replace("-", "/").replace("-", "/"));
                var end = new Date(endTime.replace("-", "/").replace("-", "/"));
                if (end <= start) {
                    alert('结束日期不能小于或等于开始日期！');
                    return false;
                }

                var s = $('#screeningsId option:selected').text();
                var timeOfSite = s.replace(/[^0-9]/ig, ""); // 时间场次
                var timeOfStart = start.getHours() + "" + start.getMinutes(); // 开始
                var typeStr = $("#type").val();
                if (timeOfStart - timeOfSite <= 0 && typeStr == "xianshiqiang") {
                    layer.msg("开始时间不能小于等于场次时间");
                    return false;
                }

                //判断活动商品库存、价格是否合理
                //库存数
                var stockum = parseInt($("#stockNum").val());
                //活动数
                var activityNum = parseInt($("#stockNumber").val());
                if (activityNum > stockum) {
                    alert('参与活动的商品数量过多，库存不足！');
                    return false;
                }
                //商品原价
                var oldPrice = parseFloat($("#priceNum").val());
                var price = parseFloat($("#price").val());
                if (price > oldPrice) {
                    alert('活动价格大于商品原价，请重新设置活动价格！');
                    return false;
                }
                $("#add_form").submit();

            });

            //添加规则
            $("#btn_add_rule").click(function () {
                $('#mansong_price').val('');
                $('#mansong_discount').val('');
                $('#mansong_price_error').hide();
                $('#mansong_rule_error').hide();
                $('#div_add_rule').show();
                $('#div_confirmOrCancel').show();
                $('#btn_add_rule').hide();
                $("label[for=mansong_rule_count]").hide();
            });

            jQuery.validator.methods.greaterThanDate = function (value, element, param) {
                var date1 = new Date(Date.parse(param.replace(/-/g, "/")));
                var date2 = new Date(Date.parse(value.replace(/-/g, "/")));
                return date1 < date2;
            };
            jQuery.validator.methods.lessThanDate = function (value, element, param) {
                var date1 = new Date(Date.parse(param.replace(/-/g, "/")));
                var date2 = new Date(Date.parse(value.replace(/-/g, "/")));
                return date1 > date2;
            };
            jQuery.validator.methods.greaterThanStartDate = function (value, element) {
                var start_date = $("#startTimeStr").val();
                var date1 = new Date(Date.parse(start_date.replace(/-/g, "/")));
                var date2 = new Date(Date.parse(value.replace(/-/g, "/")));
                return date1 < date2;
            };

            jQuery.validator.methods.isNumber = function (value, element) {
                if (value == "") {
                    return true;
                }
                if (isNaN(value) || value <= 0) {
                    return false;
                }
                return true;
            };

            //页面输入内容验证
            $("#add_form").validate({
                rules: {
                    startTimeStr: {
                        required: true
                    },
                    endTimeStr: {
                        required: true
                    }
                    ,
                    stockNumber: {
                        required: true,
                        isNumber: true
                    },
                    menNumber: {
                        required: true,
                        isNumber: true
                    },
                    price: {
                        required: true,
                        isNumber: true
                    },
                    objectId: {
                        required: true,
                    },
                    sort:{
                        required: true,
                        isNumber: true
                    }
                    <#if activityType=='tuangou'>
                    ,
                    restrictionNum: {
                        required: true,
                        isNumber: true
                    }
                    </#if>
                },
                messages: {
                    startTimeStr: {
                        required: '<i class="icon-exclamation-sign"></i>开始时间不能为空'
                    },
                    endTimeStr: {
                        required: '<i class="icon-exclamation-sign"></i>结束时间不能为空'
                    },
                    menNumber: {
                        required: '请设置每个会员限购数量',
                        isNumber: '<i class="icon-exclamation-sign"></i>数量必须是大于0的数字'
                    },
                    startSignTimeStr: {
                        required: '<i class="icon-exclamation-sign"></i>报名开始时间不能为空'
                    },
                    endSignTimeStr: {
                        required: '<i class="icon-exclamation-sign"></i>报名结束时间不能为空'
                    },
                    objectId: {
                        required: '请选择活动商品！'
                    },
                    sort:{
                        required: '不能为空',
                        isNumber: '<i class="icon-exclamation-sign"></i>排序必须是数字'
                    },
                <#if activityType=='tuangou'>
                , restrictionNum: {
                    required: '请设置团购人数',
                    isNumber: '<i class="icon-exclamation-sign"></i>数量必须是大于0的数字'
                },
                stockNumber: {
                    required: '请设置参与团购商品数量',
                    isNumber: '<i class="icon-exclamation-sign"></i>数量必须是大于0的数字'
                },
                price: {
                    required: '请设置团购活动价格',
                    isNumber: '<i class="icon-exclamation-sign"></i>数量必须是大于0的数字'
                }
                </#if>
                <#if activityType=='xianshiqiang'>
                ,
                stockNumber: {
                    required: '请设置参与秒杀活动商品数量',
                    isNumber: '<i class="icon-exclamation-sign"></i>数量必须是大于0的数字'
                },
                price: {
                    required: '请设置秒杀活动价格',
                    isNumber: '<i class="icon-exclamation-sign"></i>数量必须是大于0的数字'
                }
                </#if>
            }
        });

        });

    </script>
    <div class="clear"></div>
</@layout.body>