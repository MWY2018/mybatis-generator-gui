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

import java.util.List;

public class SelectBySelectivePlugin extends NewInterfacePlugin {

    private static String ID = "selectBySelective";

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        XmlElement answer = new XmlElement(SqlTag.SELECT.getTagName());

        answer.addAttribute(new Attribute("id", ID));
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap",
                    introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap",
                    introspectedTable.getBaseResultMapId()));
        }
        String parameterType;
        if (introspectedTable.getRules().generatePrimaryKeyClass())
        {
            parameterType = introspectedTable.getPrimaryKeyType();
        }
        else
        {
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterType = "map";
            } else {
                parameterType = introspectedTable.getBaseRecordType();
            }
        }
        answer.addAttribute(new Attribute("parameterType",
                parameterType));

        this.context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append(Statement.SELECT);
//        if (StringUtility.stringHasValue(introspectedTable.getSelectByPrimaryKeyQueryId()))
//        {
//            sb.append('\'');
//            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
//            sb.append("' as " + introspectedTable.getSelectByPrimaryKeyQueryId() + ",");
//        }
        answer.addElement(new TextElement(sb.toString()));
        {
            XmlElement include = new XmlElement(SqlTag.INCLUDE.getTagName());
            include.addAttribute(new Attribute("refid",
                    introspectedTable.getBaseColumnListId()));
            answer.addElement(include);
        }

        if (introspectedTable.hasBLOBColumns())
        {
            answer.addElement(new TextElement(","));
            XmlElement include = new XmlElement(SqlTag.INCLUDE.getTagName());
            include.addAttribute(new Attribute("refid",
                    introspectedTable.getBlobColumnListId()));
            answer.addElement(include);
        }
        sb.setLength(0);
        sb.append(Statement.FROM);
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        // TODO is_delete应该为配置项
        sb.append(Statement.WHERE  + "is_deleted = 0 ");
        answer.addElement(new TextElement(sb.toString()));
        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getNonPrimaryKeyColumns()))
        {
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");

            XmlElement isNotNullElement = new XmlElement("if");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            answer.addElement(isNotNullElement);
            sb.setLength(0);
            sb.append(Statement.AND);
            sb.append(
                    MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(
                    MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }
        if (this.context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public static Method getInterface () {
        return MethodHelper.createMethod(ID, new FullyQualifiedJavaType("List<Model>"), new Parameter(
                new FullyQualifiedJavaType("Model"), "record"
        ));
    }

}
