package com.framework.loippi.controller.admin;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.loippi.core.gen.IColumn;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.framework.loippi.entity.GenTableColumn;
import com.framework.loippi.entity.User;
import com.framework.loippi.mybatis.ext.Java2MybatisTypeConvert;
import com.framework.loippi.service.GenTableColumnService;
import com.framework.loippi.service.UserService;

import com.framework.loippi.entity.GenTable;
import com.framework.loippi.service.GenTableService;
import com.framework.loippi.support.Message;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ParameterUtils;
import com.loippi.core.gen.IFactory;
import com.loippi.core.gen.ITable;
import com.loippi.core.gen.mysql.impl.MysqlFactory;
import com.framework.loippi.controller.GenericController;

/**
 * Controlelr - 业务表
 *
 * @author Loippi Team
 * @version 1.0
 */
@Controller("adminGenTableController")
@RequestMapping("/admin/gen_table")
public class GenTableController extends GenericController {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Resource
    private UserService userService;

    @Resource
    private GenTableService tableService;

    @Resource
    private GenTableColumnService columnService;

    /**
     * 添加新业务表：选择数据库表
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(ModelMap model) {
        User current = userService.getCurrent();
        try {
            IFactory factory = MysqlFactory.getInstance(url, username, password);
            List<ITable> tables = factory.getTables();
            for (ITable iTable : tables) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", iTable.getName().toUpperCase());
                Long count = tableService.count(params);
                if (count > 0) {
                    continue;
                }


                GenTable table = new GenTable();
                table.setName(iTable.getName().toUpperCase());
                String[] array = iTable.getName().toLowerCase().split("_");
                String name = "";
                for (String item : array) {
                    name += item.substring(0, 1).toUpperCase() + item.substring(1);
                }
                table.setClassName(name);
                table.setDescription(iTable.getComment());
                table.setCreateDate(new Date());
                table.setCreator(current.getId());
                tableService.save(table);

                int i = 0;
                for (IColumn column : iTable.getColumns()) {
                    i++;
                    GenTableColumn genTableColumn = new GenTableColumn();
                    genTableColumn.setName(column.getName());
                    genTableColumn.setComments(column.getComment());
                    genTableColumn.setJavaType(column.getType());
                    genTableColumn.setJavaField(column.getJavaName());
                    if (column.isPri()) {
                        genTableColumn.setPk(1);
                    } else {
                        genTableColumn.setPk(0);
                    }
                    if (column.isNull()) {
                        genTableColumn.setNullable(1);
                    } else {
                        genTableColumn.setNullable(0);
                    }
                    genTableColumn.setInsert(1);
                    genTableColumn.setEdit(1);
                    genTableColumn.setList(1);
                    genTableColumn.setQuery(1);
                    genTableColumn.setQueryType("=");
                    genTableColumn.setShowType("input");
                    genTableColumn.setSort(i * 10);
                    genTableColumn.setMybatisJdbcType(Java2MybatisTypeConvert.convert(genTableColumn.getJavaType()));
                    genTableColumn.setCreateDate(new Date());
                    genTableColumn.setCreator(current.getId());
                    genTableColumn.setGenTableId(table.getId());
                    columnService.save(genTableColumn);
                }
            }

            model.addAttribute("tables", tables);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/admin/gen_table/add";
    }

    /**
     * 添加新业务表：表单
     */
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String form(ModelMap model, String tableName) {
        try {
            IFactory factory = MysqlFactory.getInstance(url, username, password);
            ITable table = factory.getTable(tableName);
            List<ITable> tables = factory.getTables();
            model.addAttribute("tables", tables);
            model.addAttribute("table", table);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/admin/gen_table/form";
    }

    /**
     * 列表
     */
    @RequiresPermissions("admin:gen:table")
    @RequestMapping(value = "/list")
    public String list(Pageable pageable, HttpServletRequest request, ModelMap model) {
        Map<String, Object> paramter = ParameterUtils.getParametersMapStartingWith(request, "filter_");
        pageable.setParameter(paramter);
        Page<GenTable> page = tableService.findByPage(pageable);
        model.addAttribute("page", page);
        model.addAttribute("paramter", paramter);
        return "/admin/gen_table/list";
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(GenTable table, RedirectAttributes redirectAttributes) {
        User current = userService.getCurrent();
        table.setCreateDate(new Date());
        table.setCreator(current.getId());
        tableService.save(table);
        for (GenTableColumn column : table.getColumns()) {
            column.setMybatisJdbcType(Java2MybatisTypeConvert.convert(column.getJavaType()));
            column.setCreateDate(new Date());
            column.setCreator(current.getId());
            column.setGenTableId(table.getId());
            columnService.save(column);
        }
        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return "redirect:list.jhtml";
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public
    @ResponseBody
    Message delete(Long[] ids) {
        for (Long id : ids) {
            columnService.deleteByTableId(id);
        }
        tableService.deleteAll(ids);
        return SUCCESS_MESSAGE;
    }
}
