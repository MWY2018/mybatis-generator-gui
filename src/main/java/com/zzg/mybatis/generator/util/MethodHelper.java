package com.zzg.mybatis.generator.util;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.ArrayList;
import java.util.List;

public class MethodHelper {

    public static Method createMethod (String methodName, FullyQualifiedJavaType returnType,
                                       List<Parameter> parameters) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(returnType);
        method.setNative(false);
        method.setFinal(false);
        method.setStatic(false);
        method.setSynchronized(false);
        for (Parameter parameter : parameters) {
            method.addParameter(parameter);
        }
        return method;
    }

    public static Method createMethod (String methodName, FullyQualifiedJavaType returnType,
                                       Parameter parameter) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter);
        return createMethod(methodName, returnType, parameters);
    }

}
