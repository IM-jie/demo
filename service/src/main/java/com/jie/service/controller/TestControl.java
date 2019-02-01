package com.jie.service.controller;

import com.alibaba.datax.plugin.rdbms.util.DataBaseType;
import com.jie.service.iservice.IDBConnectionService;
import com.jie.service.iservice.ITableColumnService;
import com.jie.service.iservice.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestControl {
    @Autowired
    private ITestService iTestService;

    @Autowired
    private ITableColumnService iTableColumnService;

    @Autowired
    private IDBConnectionService idbConnectionService;

    @GetMapping("/test")
    public String test(){
        return iTestService.test();
    }

    @GetMapping("/getcolumn")
    public String getTableColumns(){
        String jdbcUrl = "jdbc:mysql://134.175.21.91:3306/datasource?useUnicode=true&characterEncoding=utf8";
        String userName = "root";
        String password = "123456";
        DataBaseType DATABASE_TYPE = DataBaseType.MySql;
        List<String> columns = iTableColumnService.getTableColumns(DATABASE_TYPE, jdbcUrl, userName, password, "t_test");
        return String.join(",", columns);
    }

    @GetMapping("/testconnection")
    public boolean getConnection(){
        String jdbcUrl = "jdbc:mysql://134.175.21.91:3306/datasource?useUnicode=true&characterEncoding=utf8";
        String userName = "root";
        String password = "12346";
        DataBaseType DATABASE_TYPE = DataBaseType.MySql;
        return idbConnectionService.connectionUserful(DATABASE_TYPE, jdbcUrl, userName, password);
    }
}
