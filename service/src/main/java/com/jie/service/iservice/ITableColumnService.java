package com.jie.service.iservice;

import com.alibaba.datax.plugin.rdbms.util.DataBaseType;

import java.util.List;

public interface ITableColumnService {
    List<String> getTableColumns(DataBaseType dataBaseType, String jdbcUrl,
                                 String user, String pass, String tableName);
}
