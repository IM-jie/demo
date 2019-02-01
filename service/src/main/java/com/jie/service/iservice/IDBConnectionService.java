package com.jie.service.iservice;

import com.alibaba.datax.plugin.rdbms.util.DataBaseType;

public interface IDBConnectionService {
    boolean connectionUserful(final DataBaseType dataBaseType,
                              final String jdbcUrl, final String username, final String password);
}
