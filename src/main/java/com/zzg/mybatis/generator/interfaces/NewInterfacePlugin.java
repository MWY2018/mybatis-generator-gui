package com.zzg.mybatis.generator.interfaces;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;

import java.util.List;

public abstract class NewInterfacePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    public static Method getInterface () {
        return null;
    }
}
