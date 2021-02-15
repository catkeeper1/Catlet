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

        List<Index> result = new ArrayList<>();
        if(tableAnnotation != null) {
            AnnotationValue indexAnnotation = ParseUtil.getAnnotationAttribute("indexes",
                    tableAnnotation.getElementValues());

            if(indexAnnotation != null) {
                List indexList = (List)indexAnnotation.getValue();

                indexList.forEach(id -> {

                    Index index = new Index();
                    AnnotationMirror indexValues = (AnnotationMirror) id;
                    index.setName(ParseUtil.getAnnotationAttributeStringValue("name", indexValues));
                    index.setUnique(ParseUtil.getAnnotationAttributeBooleanValue("unique", indexValues));

                    String columnList =
                            ParseUtil.getAnnotationAttributeStringValue("columnList", indexValues);

                    index.setColumnList(parseIndexColumn(columnList, tableColumns));
                    result.add(index);
                });

            }
        }

        return result;
    }

    private List<Index.IndexColumn> parseIndexColumn(String columnList, Collection<Column> tableColumns) {

        List<Index.IndexColumn> reuults = new ArrayList<>();

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
                    reuults.add(indexColumn);
                }

                if (columnTokenizer.hasMoreTokens()) {
                    indexColumn.setOrder(columnTokenizer.nextToken());
                }
            }

        }

        return reuults;
    }


}
