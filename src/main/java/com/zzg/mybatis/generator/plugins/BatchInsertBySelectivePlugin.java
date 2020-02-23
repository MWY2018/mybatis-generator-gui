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
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;

public class BatchInsertBySelectivePlugin extends NewInterfacePlugin {

    private static String ID = "batchInsertBySelective";

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String itemPrefix = "item";

        XmlElement parentElement = document.getRootElement();

        XmlElement insertElement = new XmlElement(SqlTag.INSERT.getTagName());

        insertElement.addAttribute(new Attribute("id", ID));

        this.context.getCommentGenerator().addComment(insertElement);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null)
        {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(gk.getColumn());
            if ((introspectedColumn != null) &&
                    (gk.isJdbcStandard()))
            {
                insertElement.addAttribute(new Attribute("useGeneratedKeys", "true"));
                insertElement.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
                insertElement.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName()));
            }
        }

        String insertStatement = Statement.INSERT + Statement.INTO + introspectedTable.getFullyQualifiedTableNameAtRuntime();
//        insertElement.addElement(new TextElement(insertStatement));
        StringBuilder columns = new StringBuilder();
        XmlElement columnsElement = new XmlElement("trim");
        columnsElement.addAttribute(new Attribute("prefix", "("));
        columnsElement.addAttribute(new Attribute("suffix", ")"));
        columnsElement.addAttribute(new Attribute("suffixOverrides", ","));

        XmlElement valuesElements = new XmlElement("trim");
        valuesElements.addAttribute(new Attribute("prefix", " values("));
        valuesElements.addAttribute(new Attribute("suffix", ")"));
        valuesElements.addAttribute(new Attribute("suffixOverrides", ","));

        XmlElement forEachElement = new XmlElement("foreach");
        forEachElement.addAttribute(new Attribute("collection", "list"));
        forEachElement.addAttribute(new Attribute("index", "index"));
        forEachElement.addAttribute(new Attribute("item", itemPrefix));
        forEachElement.addAttribute(new Attribute("separator", ";"));

        insertElement.addElement(forEachElement);
        forEachElement.addElement(new TextElement(insertStatement));
        forEachElement.addElement(columnsElement);
        forEachElement.addElement(valuesElements);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable
                .getAllColumns())) {
            if ((introspectedColumn.isSequenceColumn()) ||
                    (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()))
            {
                columns.setLength(0);
                columns.append(
                        MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                columns.append(',');
                columnsElement.addElement(new TextElement(columns.toString()));

                columns.setLength(0);
                columns.append(
                        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, itemPrefix + "."));
                columns.append(',');
                valuesElements.addElement(new TextElement(columns.toString()));
            }
            else
            {
                columns.setLength(0);
                columns.append(itemPrefix + "." + introspectedColumn.getJavaProperty());
                columns.append(" != null");
                XmlElement insertNotNullElement = new XmlElement("if");
                insertNotNullElement.addAttribute(new Attribute(
                        "test", columns.toString()));

                columns.setLength(0);
                columns.append(
                        MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                columns.append(',');
                insertNotNullElement.addElement(new TextElement(columns.toString()));
                columnsElement.addElement(insertNotNullElement);

                columns.setLength(0);
                columns.append(itemPrefix + "." + introspectedColumn.getJavaProperty());
                columns.append(" != null");
                XmlElement valuesNotNullElement = new XmlElement("if");
                valuesNotNullElement.addAttribute(new Attribute(
                        "test", columns.toString()));

                columns.setLength(0);
                columns.append(
                        MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, itemPrefix + "."));
                columns.append(',');
                valuesNotNullElement.addElement(new TextElement(columns.toString()));
                valuesElements.addElement(valuesNotNullElement);
            }
        }

        if (this.context.getPlugins().sqlMapInsertSelectiveElementGenerated(insertElement, introspectedTable)) {
            parentElement.addElement(insertElement);
        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public static Method getInterface () {
        return MethodHelper.createMethod(ID, new FullyQualifiedJavaType("int"), new Parameter(
                new FullyQualifiedJavaType("@Param(" + '"' + "list" + '"' +") List<Model>"), "records"
        ));
    }

}
