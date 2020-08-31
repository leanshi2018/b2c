<@layout.head>
    <link href="${base}/res/css/member.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/jquery.ajaxContent.pack.js"></script>
    <script type="text/javascript" src="${base}/res/js/ajaxfileupload/ajaxfileupload.js"></script>
    <script type="text/javascript" src="${base}/res/js/goods/jquery.poshytip.min.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/goods/goods_body.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/validate.expand.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/goods/jquery.charCount.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js" charset="utf-8"></script>
</@layout.head>
<@layout.body>
    <div class="layout">
        <div class="wrap" style="padding: 20px">
            <!-- 搜索栏 -->
            <form method="POST" name="formSearch" id="formSearch"
                  action="${base}/admin/shop_activity_common/findShopGoodList.jhtml">
                <input type="hidden" name="pageable" value="${1}">
                <table class="tb-type1 noborder search">
                    <tbody>
                    <tr>
                       <td>
                            <select class="select">
                                <#list goodsClass as list>
                                    <option name="Id" value="${list.id}">${list.gcName}</option>
                                </#list>
                            </select>
                            <input name="goodsName" type="text" value="${goodsName}" placeholder="请输入商品名称"/>
                            <a href="javascript:$('#formSearch').submit();" class="btn-search " title="查询">
                            </a>
                            <a href="javascript:selectgoodsbtn()"  class="btns ">确定</a>
                       </td>

                    </tr>
                    </tbody>
                </table>
            </form>
            <form id="form_list" method="get" action="${base}/admin/shop_activity_common/findShopGoodList.jhtml">
            <table class="order">
                <thead>
                <tr>
                    <th>&nbsp;</th>
                    <th><b>分类Id</b></th>
                    <th><b>商品名称</b></th>
<#--                    <th><b>操作</b></th>-->
                </tr>
                </thead>
                <tbody>
                <#list page.content as list>
                    <tr>
                        <td><input type="checkbox" name="ids" value="${list.id}" class="checkitem"></td>
                        <td style="text-align: left">
                            ${list.gcId}
                        </td>
                        <td style="text-align: left">
                            ${list.goodsName}
                        </td>
<#--                        <td class="w100 tc">-->
<#--                            <a href="javascript:void(0);" id="selectIds" class="sc-btn sc-btn-green mt5" onclick="selSpecGoods('${list.id}')">选择</a>-->
<#--                        </td>-->
                    </tr>
                </#list>
                </tbody>
                <tfoot>

                <tr>
                    <td colspan="20">
                        <@layout.pager pager/>
                    </td>
                </tr>
                </tfoot>
            </table>
            </form>
        </div>
    </div>
    <script>
        function selectgoodsbtn() {
            var items = $("input[name='ids']:checked");
            if (items.length == 0) {
                alert("请至少选择一个商品!");
            } else {
                var jsonMap=[];
                for(var i=0;i<items.length;i++){
                    var id=items[i].value;
                    var map={id};
                    jsonMap.push(map);
                }
                console.log(jsonMap);
                parent.appendInfo(jsonMap);
            }
        }
        function selSpecGoods(id) {
            //调用父级窗口
            parent.appendInfo(id);
            //关闭当前窗口
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        }
    </script>
</@layout.body>