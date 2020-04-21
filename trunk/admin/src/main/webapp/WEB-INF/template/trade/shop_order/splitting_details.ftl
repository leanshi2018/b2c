<@layout.head>
    <!--suppress ALL -->
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/jquery.validation.min.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/resources/js/jquery.form.js"></script>
    <script src="${base}/res/js/area.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
    <script type="text/javascript" src="${base}/res/js/internation/goods/order/jquery.goods_order_zh_CN.js"></script>
    <style>
        .transinput {
            background-color: transparent;
            border: none;
            outline: none;
        }

        .select {
            width: 100px;
            height: 26px;
        }
        ul li{margin: 2%;
            font-size: 13px;}
        .goods{width: 70%}
    </style>
</@layout.head>
<@layout.body>
    <div class="page">
        <div class="fixed-bar">
            <div class="item-title">
                <h3>订单分账明细</h3>
                <ul class="tab-base">
                    <li><a href="JavaScript:void(0);" class="current" style="width: 28px;"><span>管理</span></a></li>
                </ul>
            </div>
        </div>
        <div class="fixed-empty"></div>
        <form method="post" action="${base}/admin/order/getOrderSplitDetail.jhtml" name="formSearch" id="formSearch" style="margin-top: 2%">
            <input type="hidden" name="pageNo" value="${1}">
            <tbody>
            <tr class="space">
                <th colspan="15" style="font-size;15px;font-weight: bold">订单信息</th>
            </tr>
            <tr>
                <td>
                    <ul>
                        <li><strong>订单号：</strong>${detail.orderSn}</li>
                        <li><strong>订单实付金额：</strong>${detail.orderAmount}</li>
                        <li><strong>公司分账后收入：</strong>${detail.firmSplitAmount}</li>
                    </ul>
                </td>
            </tr>
            <tr class="space">
                <th colspan="2" style="font-size: 15px;font-weight: bold">
                    会员分账信息
                </th>
            </tr>
            <tr>
                <td>
                    <table class="table tb-type2 goods ">
                        <tbody>
                        <tr>
<#--                            <th>序号</th>-->
                            <th>会员ID</th>
                            <th class="align-center">会员名称</th>
                            <th class="align-center">分账金额（元）</th>
                            <th class="align-center">积分扣减</th>
                        </tr>
<#--                        <#list detail.content as details>-->
                            <tr>
<#--                                <td class="w96 align-center"><p></p></td>-->
                                <td class="w96 align-center">${detail.cutGetId}</td>
                                <td class="w96 align-center">${detail.cutGetName}</td>
                                <td class="w96 align-center">${detail.cutAmount}</td>
                                <td class="w96 align-center">${detail.cutAcc}</td>
                            </tr>
<#--                        </#list>-->

                </tbody>
                    </table>
            <tfoot>
            <tr class="tfoot">
                <td><a href="JavaScript:void(0);" class="btn" onclick="history.go(-1)"><span>返回</span></a>
            </tr>
            </tfoot>

    </div>
    <script>


    </script>
</@layout.body>