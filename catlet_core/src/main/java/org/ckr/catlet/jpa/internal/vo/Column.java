package org.ckr.catlet.jpa.internal.vo;

import java.util.StringJoiner;

public class Column {

    private Boolean nullable = true;

    private Boolean isPrimaryKey = false;

    private String columnDefinition;

    private Integer length;

    private Integer precision;

    private Integer scale;

    private String javaElementName;

    private String ImplicitName;

    private String explicitName;

    private String javaFieldType;

    private String comment;

    private String enclosingClassName;



    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getJavaElementName() {
        return javaElementName;
    }

    public void setJavaElementName(String javaElementName) {
        this.javaElementName = javaElementName;
    }

    public String getImplicitName() {
        return ImplicitName;
    }

    public void setImplicitName(String implicitName) {
        ImplicitName = implicitName;
    }

    public String getExplicitName() {
        return explicitName;
    }

    public void setExplicitName(String explicitName) {
        this.explicitName = explicitName;
    }

    public String getJavaFieldType() {
        return javaFieldType;
    }

    public void setJavaFieldType(String javaFieldType) {
        this.javaFieldType = javaFieldType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEnclosingClassName() {
        return enclosingClassName;
    }

    public void setEnclosingClassName(String enclosingClassName) {
        this.enclosingClassName = enclosingClassName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Column.class.getSimpleName() + "[", "]")
                .add("nullable=" + nullable)
                .add("isPrimaryKey=" + isPrimaryKey)
                .add("columnDefinition='" + columnDefinition + "'")
                .add("length=" + length)
                .add("precision=" + precision)
                .add("scale=" + scale)
                .add("javaElementName='" + javaElementName + "'")
                .add("ImplicitName='" + ImplicitName + "'")
                .add("explicitName='" + explicitName + "'")
                .add("javaFieldType='" + javaFieldType + "'")
                .add("comment='" + comment + "'")
                .add("enclosingClassName='" + enclosingClassName + "'")
                .toString();
    }
}
