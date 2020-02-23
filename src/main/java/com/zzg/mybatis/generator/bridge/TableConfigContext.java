package com.zzg.mybatis.generator.bridge;

import org.mybatis.generator.config.TableConfiguration;

public class TableConfigContext {

    static TableConfiguration tableConfiguration;

    private static String primaryKeyName;

    public static void setTableConfiguration(TableConfiguration tableConfiguration) {
        TableConfigContext.tableConfiguration = tableConfiguration;
    }

    public static void setPrimaryKeyName(String pkn) {
        primaryKeyName = pkn;
    }

    public static String getPrimaryKeyName() {
        return primaryKeyName;
    }
}
