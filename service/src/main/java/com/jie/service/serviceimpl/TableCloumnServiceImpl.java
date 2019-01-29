package com.jie.service.serviceimpl;

import com.alibaba.datax.plugin.rdbms.util.DBUtil;
import com.alibaba.datax.plugin.rdbms.util.DataBaseType;
import com.jie.service.iservice.ITableColumnService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableCloumnServiceImpl implements ITableColumnService {
    @Override
    public List<String> getTableColumns(DataBaseType dataBaseType, String jdbcUrl, String user, String pass, String tableName) {
        return DBUtil.getTableColumns(dataBaseType, jdbcUrl, user, pass, tableName);
    }
}
