package com.zzg.mybatis.generator.plugins;

import com.zzg.mybatis.generator.interfaces.NewInterfacePlugin;
import com.zzg.mybatis.generator.model.SqlTag;
import com.zzg.mybatis.generator.model.Statement;
import com.zzg.mybatis.generator.util.MethodHelper;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

public class BatchUpdateByPrimaryKeySelectivePlugin extends NewInterfacePlugin {

    private static String ID = "batchUpdateByPrimaryKeySelective";

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        
        String itemPrefix = "item";

        XmlElement parentElement = document.getRootElement();

        XmlElement updateElement = new XmlElement(SqlTag.UPDATE.getTagName());

        updateElement.addAttribute(new Attribute("id", ID));

        XmlElement forEachElement = new XmlElement("foreach");
        forEachElement.addAttribute(new Attribute("collection", "list"));
        forEachElement.addAttribute(new Attribute("index", "index"));
        forEachElement.addAttribute(new Attribute("item", itemPrefix));
        forEachElement.addAttribute(new Attribute("separator", ";"));

        updateElement.addElement(forEachElement);

        StringBuilder columns = new StringBuilder();

        columns.append(Statement.UPDATE);
        columns.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        forEachElement.addElement(new TextElement(columns.toString()));
        XmlElement setElement = new XmlElement("set");
        forEachElement.addElement(setElement);
        IntrospectedColumn primaryColumn = null;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                columns.setLength(0);
                columns.append(itemPrefix + "." + introspectedColumn.getJavaProperty());
                columns.append(" != null");
                XmlElement ifElement = new XmlElement("if");
                ifElement.addAttribute(new Attribute(
                        "test", columns.toString()));
                columns.setLength(0);
                columns.append(
                        MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn) + " ");
                columns.append(Statement.EQUAL);
                columns.append(
                        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, itemPrefix + "."));
                columns.append(Statement.COMMA);
                ifElement.addElement(new TextElement(columns.toString()));
                setElement.addElement(ifElement);
        }
        // 追加主键查询
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        boolean and = false;
        StringBuilder where = new StringBuilder();
        for (IntrospectedColumn pk : primaryKeyColumns) {
            where.setLength(0);
            if (!and) {
                where.append(Statement.WHERE);
                and = true;
            } else {
                where.append(Statement.AND);
            }
            where.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(pk) + " ");
            where.append(Statement.EQUAL);
            where.append(MyBatis3FormattingUtilities.getParameterClause(pk));
            forEachElement.addElement(new TextElement(where.toString()));
        }
        if (this.context.getPlugins().sqlMapInsertSelectiveElementGenerated(updateElement, introspectedTable)) {
            parentElement.addElement(updateElement);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public static Method getInterface () {
        return MethodHelper.createMethod(ID, new FullyQualifiedJavaType("int"), new Parameter(
                new FullyQualifiedJavaType("@Param(" + '"' + "list" + '"' +") List<Model>"), "records"
        ));
    }

}
