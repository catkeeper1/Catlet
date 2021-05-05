package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.util.ParseUtil;

import org.ckr.catlet.jpa.internal.vo.Column;

import org.ckr.catlet.jpa.internal.vo.Index;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import java.util.ArrayList;

import java.util.List;

import static javax.tools.Diagnostic.Kind.NOTE;
import static org.ckr.catlet.jpa.internal.util.ParseUtil.getAnnotationAttributeBooleanValue;
import static org.ckr.catlet.jpa.internal.util.ParseUtil.getAnnotationAttributeStringValue;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;


public class IndexParser {

    private final Reporter reporter;
    private final Elements treeUtil;

    public IndexParser(Reporter reporter, Elements treeUtil) {
        this.reporter = reporter;
        this.treeUtil = treeUtil;
    }


    public List<Index> parseIndexes(TypeElement typeElement, Collection<Column> tableColumns) {

        AnnotationMirror tableAnnotation =
                ParseUtil.getAnnotationMirrorFromElement(typeElement, javax.persistence.Table.class);

        List<Index> resultList = new ArrayList<>();
        if(tableAnnotation != null) {
            AnnotationValue indexesAnnotationValue = ParseUtil.getAnnotationAttribute("indexes",
                    tableAnnotation.getElementValues());

            if(indexesAnnotationValue != null) {
                List indexAnnotationValues = (List)indexesAnnotationValue.getValue();

                indexAnnotationValues.forEach(indexAnnotation -> {
                    AnnotationMirror indexMirror = (AnnotationMirror) indexAnnotation;

                    Index index = parseIndex(indexMirror, tableColumns);

                    reporter.print(NOTE,"parsed index:" + index.toString());
                    resultList.add(index);

                });

            }
        }
        return resultList;
    }

    private Index parseIndex(AnnotationMirror indexMirror, Collection<Column> tableColumns) {
        Index index = new Index();
        index.setName(getAnnotationAttributeStringValue("name", indexMirror));
        index.setUnique(getAnnotationAttributeBooleanValue("unique", indexMirror));

        String columnList = getAnnotationAttributeStringValue("columnList", indexMirror);

        index.setColumnList(parseIndexColumn(columnList, tableColumns));

        return index;
    }

    private List<Index.IndexColumn> parseIndexColumn(String columnList, Collection<Column> tableColumns) {

        List<Index.IndexColumn> results = new ArrayList<>();

        StringTokenizer columnListTokenizer = new StringTokenizer(columnList, ",");

        while (columnListTokenizer.hasMoreTokens()) {
            String columnValue = columnListTokenizer.nextToken();

            Index.IndexColumn indexColumn = new Index.IndexColumn();

            StringTokenizer columnTokenizer = new StringTokenizer(columnValue, " ");

            if(columnTokenizer.hasMoreTokens()) {
                String columnName = columnTokenizer.nextToken();

                Column column = ColumnParser.findByName(columnName, tableColumns);

                if(column != null) {
                    indexColumn.setColumn(column);
                    results.add(indexColumn);
                }

                if (columnTokenizer.hasMoreTokens()) {
                    String orderStr = columnTokenizer.nextToken();

                    if("ASC".equals(orderStr.toUpperCase())) {
                        indexColumn.setOrder(Index.Order.ASC);
                    } else if("DESC".equals(orderStr.toUpperCase())) {
                        indexColumn.setOrder(Index.Order.DESC);
                    }

                }
            }

        }

        return results;
    }


}
