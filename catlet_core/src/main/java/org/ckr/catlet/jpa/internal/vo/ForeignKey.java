package org.ckr.catlet.jpa.internal.vo;

import java.util.ArrayList;
import java.util.List;

public class ForeignKey {

    private Boolean isMany = false;

    private Table targetTable;

    private List<Column> sourceColumns = new ArrayList<>();

    private List<Column> targetColumns = new ArrayList<>();

    private Boolean optional = true;

    public Boolean getMany() {
        return isMany;
    }

    public void setMany(Boolean many) {
        isMany = many;
    }

    public Table getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(Table targetTable) {
        this.targetTable = targetTable;
    }

    public List<Column> getSourceColumns() {
        return sourceColumns;
    }

    public void setSourceColumns(List<Column> sourceColumns) {
        this.sourceColumns = sourceColumns;
    }

    public List<Column> getTargetColumns() {
        return targetColumns;
    }

    public void setTargetColumns(List<Column> targetColumns) {
        this.targetColumns = targetColumns;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }
}
