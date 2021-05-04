package org.ckr.catlet.jpa.internal.vo;


import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static org.ckr.catlet.jpa.internal.util.StringUtil.printCollection;

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
        final StringBuilder sb = new StringBuilder("Table{").append("\n");
        sb.append("  implicitName='").append(implicitName).append('\'').append("\n");
        sb.append(", explicitName='").append(explicitName).append('\'').append("\n");
        sb.append(", packageName='").append(packageName).append('\'').append("\n");
        sb.append(", className='").append(className).append('\'').append("\n");
        sb.append(", indexList=").append(printCollection(indexList, 4));
        sb.append(", columnList=").append(printCollection(columnList, 4 ));
        sb.append(", foreignKeyList=").append(printCollection(foreignKeyList, 4));
        sb.append(", comment='").append(comment).append('\'').append("\n");
        sb.append('}').append("\n");
        return sb.toString();
    }
}
