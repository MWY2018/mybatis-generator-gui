package com.zzg.mybatis.generator.plugins;

import com.zzg.mybatis.generator.util.MethodHelper;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

public class SelectBySelectiveAndReturnOneRecordPlugin extends SelectBySelectivePlugin {

    private static String ID = "selectOne";


    public static Method getInterface () {
        return MethodHelper.createMethod(ID, new FullyQualifiedJavaType("Model"), new Parameter(
                new FullyQualifiedJavaType("Model"), "record"
        ));
    }

}
