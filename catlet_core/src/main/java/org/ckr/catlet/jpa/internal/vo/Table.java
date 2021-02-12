package org.ckr.catlet.jpa.internal.vo;


import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Table {
    private String implicitName;

    private String explicitName;

    private String packageName = null;

    private String className = null;

    private List<Index> indexList = new ArrayList<>();

    private List<Column> columnList = new ArrayList<>();

    private List<ForeignKey> foreignKeyList = new ArrayList<>();

    private String comment;

    public String getImplicitName() {
        return implicitName;
    }

    public void setImplicitName(String implicitName) {
        this.implicitName = implicitName;
    }

    public String getExplicitName() {
        return explicitName;
    }

    public void setExplicitName(String explicitName) {
        this.explicitName = explicitName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Index> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Index> indexList) {
        this.indexList = indexList;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    public List<ForeignKey> getForeignKeyList() {
        return foreignKeyList;
    }

    public void setForeignKeyList(List<ForeignKey> foreignKeyList) {
        this.foreignKeyList = foreignKeyList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Table.class.getSimpleName() + "[", "]")
                .add("implicitName='" + implicitName + "'")
                .add("explicitName='" + explicitName + "'")
                .add("packageName='" + packageName + "'")
                .add("className='" + className + "'")
                .add("indexList=" + indexList)
                .add("columnList=" + columnList)
                .add("foreignKeyList=" + foreignKeyList)
                .add("comment='" + comment + "'")
                .toString();
    }
}
