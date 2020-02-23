package com.zzg.mybatis.generator.model;

public enum SqlTag {

    SELECT("select"),
    UPDATE("update"),
    INSERT("insert"),
    DELETE("delete"),
    INCLUDE("include");

    private String tagName;

    SqlTag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

}
