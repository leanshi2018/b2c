<@layout.head>
    <!--suppress ALL -->
    <script type="text/javascript" src="${base}/res/js/jquery-ui/jquery.ui.js"></script>
    <script type="text/javascript" src="${base}/res/js/jquery-ui/i18n/zh-CN.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/res/js/jquery.validation.min.js"></script>
    <script type="text/javascript" src="${base}/res/js/layer/layer.js"></script>
    <script type="text/javascript" src="${base}/resources/js/jquery.form.js"></script>
    <script src="${base}/res/js/area.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="${base}/res/js/jquery-ui/themes/ui-lightness/jquery.ui.css"/>
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
    </style>
</@layout.head>
<@layout.body>
    <div class="page">
        <table class="table tb-type2 order">

            <tbody>
            <tr class="space">
                <th colspan="15">订单信息</th>
            </tr>
            <tr>
                <td>
                    <ul>
                        <li><strong>订单号：</strong></li>
                        <li><strong>订单实付金额：</strong></li>
                        <li><strong>公司分账后收入：</strong></li>
                    </ul>
                </td>
            </tr>
            <tr class="space">
                <th colspan="2">
                    商城商品信息
                </th>
            </tr>
            <tr>
                <td>
                    <table class="table tb-type2 goods ">
                        <tbody>
                        <tr>
                            <th>序号</th>
                            <th>会员ID</th>
                            <th class="align-center">会员名称</th>
                            <th class="align-center">分账金额（元）</th>
                            <th class="align-center">积分扣减</th>
                        </tr>
                        <#list shippingDto.goodsInfoList as goods>
                            <tr>
                                <td class="w96 align-center"><p>${goods.goodsName}</p></td>
                                <td class="w96 align-center">${goods.specInfo}</td>
                                <td class="w96 align-center">${goods.quantity}</td>
                                <td class="w96 align-center">${goods.specInfo}</td>
                                <td class="w96 align-center">${goods.quantity}</td>
                            </tr>

                        <#else >
                        </#list>

            </tbody>
            <tfoot>
            <tr class="tfoot">
                <td><a href="JavaScript:void(0);" class="btn" onclick="history.go(-1)"><span>返回</span></a>
            </tr>
            </tfoot>
        </table>
    </div>
    <script>


    </script>
</@layout.body>