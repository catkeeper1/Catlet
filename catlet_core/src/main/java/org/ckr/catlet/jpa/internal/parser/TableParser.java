package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.util.ParseUtil;
import org.ckr.catlet.jpa.internal.naming.NamingStrategyHolder;
import org.ckr.catlet.jpa.internal.vo.Index;
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

    private IndexParser indexParser;

    public TableParser(Reporter reporter, Elements treeUtil) {
        this.reporter = reporter;
        this.treeUtil = treeUtil;

        indexParser = new IndexParser(reporter, treeUtil);
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
        table.setExplicitName(getExplicitTableName(typeElement));
        table.setImplicitName(getImplicitTableName(typeElement));

        table.setPackageName(ParseUtil.getPackageName(typeElement));
        table.setClassName(typeElement.getSimpleName().toString());


        ColumnParser columnParser = new ColumnParser(reporter, treeUtil);

        table.setColumnList(columnParser.parseColumns(typeElement));

        List<Index> indexList = indexParser.parseIndexes(typeElement, table.getColumnList());

        table.setIndexList(indexList);

        reporter.print(NOTE, "parsed table: " + table);

        return table;

    }

    private String getExplicitTableName(TypeElement typeElement) {

        AnnotationMirror tableAnnotation =
                ParseUtil.getAnnotationMirrorFromElement(typeElement, javax.persistence.Table.class);

        if(tableAnnotation != null) {
            AnnotationValue annotationValue = ParseUtil.getAnnotationAttribute("name",
                    tableAnnotation.getElementValues());

            if(annotationValue != null) {
                return annotationValue.getValue().toString();
            }
        }

        return null;

    }

    private String getImplicitTableName(TypeElement typeElement) {

        return NamingStrategyHolder.getStrategy().getTableName(typeElement);

    }


}
