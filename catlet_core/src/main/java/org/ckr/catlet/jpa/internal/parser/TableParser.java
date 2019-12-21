package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.ParseUtil;
import org.ckr.catlet.jpa.internal.naming.NamingStrategyHolder;
import org.ckr.catlet.jpa.internal.vo.Table;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.*;

public class TableParser {

    private Reporter reporter;
    private Elements treeUtil;

    public TableParser(Reporter reporter, Elements treeUtil) {
        this.reporter = reporter;
        this.treeUtil = treeUtil;
    }

    public List<Table> parseTables(Set<? extends Element> set) {
        List<Table> result = new ArrayList<>();
        for(Element element: set) {

            Table table = parseTable(element);

            if(table != null) {
                result.add(table);
            }

        }

        return result;
    }

    private Table parseTable(Element element) {
        if (!(element instanceof TypeElement)) {
            return null;
        }

        TypeElement typeElement = (TypeElement) element;
        AnnotationMirror entityAnnotation = ParseUtil.getAnnotationMirrorFromElement(typeElement, Entity.class);

        if(entityAnnotation == null) {
            return null;
        }

        reporter.print(NOTE, "parse class: " + typeElement.getQualifiedName().toString());

        Table table = new Table();
        table.setTableName(getTableName(typeElement));
        table.setPackageName(ParseUtil.getPackageName(typeElement));
        table.setClassName(typeElement.getSimpleName().toString());


        ColumnParser columnParser = new ColumnParser(reporter, treeUtil);

        table.setColumnList(columnParser.parseColumns(typeElement));

        reporter.print(NOTE, "parsed table: " + table);

        return table;

    }

    private String getTableName(TypeElement typeElement) {

        AnnotationMirror tableAnnotation =
                ParseUtil.getAnnotationMirrorFromElement(typeElement, javax.persistence.Table.class);

        if(tableAnnotation != null) {
            AnnotationValue annotationValue = ParseUtil.getAnnotationAttribute("name",
                    tableAnnotation.getElementValues());

            if(annotationValue != null) {
                return annotationValue.getValue().toString();
            }
        }

        return getTableNameFromClassName(typeElement);

    }

    private String getTableNameFromClassName(TypeElement typeElement) {

        return NamingStrategyHolder.getStrategy().getTableName(typeElement);

    }


}
