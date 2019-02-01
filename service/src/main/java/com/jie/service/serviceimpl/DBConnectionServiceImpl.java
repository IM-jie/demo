package com.jie.service.serviceimpl;

import com.alibaba.datax.plugin.rdbms.util.DBUtil;
import com.alibaba.datax.plugin.rdbms.util.DataBaseType;
import com.jie.service.iservice.IDBConnectionService;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class DBConnectionServiceImpl implements IDBConnectionService {
    @Override
    public boolean connectionUserful(DataBaseType dataBaseType, String jdbcUrl, String username, String password) {
        Connection connection = DBUtil.testConnection(dataBaseType, jdbcUrl,username, password);
        return connection == null ? false : true;
    }
}
