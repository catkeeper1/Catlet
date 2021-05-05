package org.ckr.catlet.jpa.internal.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Index {
    private String name = null;

    private List<IndexColumn> columnList = new ArrayList<>();

    private Boolean unique = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IndexColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<IndexColumn> columnList) {
        this.columnList = columnList;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public enum Order {
        ASC,
        DESC
    }

    public static class IndexColumn {

        private Column column;

        private Order order = null;

        public Column getColumn() {
            return column;
        }

        public void setColumn(Column column) {
            this.column = column;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        @Override
        public String toString() {

            String columnName =
                    column.getExplicitName() == null ? column.getImplicitName() : column.getExplicitName();

            return new StringJoiner(", ", IndexColumn.class.getSimpleName() + "[", "]")
                    .add("column=" + columnName)
                    .add("order='" + order + "'")
                    .toString();

        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Index.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("columnList=" + columnList)
                .add("unique=" + unique)
                .toString();
    }
}
