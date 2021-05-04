package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.util.ParseUtil;
import org.ckr.catlet.jpa.internal.naming.NamingStrategyHolder;
import org.ckr.catlet.jpa.internal.util.StringUtil;
import org.ckr.catlet.jpa.internal.vo.Column;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static org.ckr.catlet.jpa.internal.util.ParseUtil.*;

import static javax.tools.Diagnostic.Kind.*;
import static org.ckr.catlet.jpa.internal.util.ParseUtil.isFieldElement;

public class ColumnParser {
    private Reporter reporter;
    private Elements treeUtil;

    public ColumnParser(Reporter reporter, Elements treeUtil) {
        this.reporter = reporter;
        this.treeUtil = treeUtil;
    }

    public List<Column> parseColumns(TypeElement classElement) {


        AccessType accessType = getAccessType(classElement);
        reporter.print(NOTE, "Access type is " + accessType);
        if(accessType == null) {
            return new ArrayList<>();
        }

        return doParseColumns(classElement, accessType);
    }


    private AccessType getAccessType(TypeElement classElement) {

        AnnotationMirror accessAnnotation = getAnnotationMirrorFromElement(classElement, Access.class);

        if(accessAnnotation != null) {
            AnnotationValue accType = getAnnotationAttribute("value", accessAnnotation.getElementValues());
            VariableElement accessType = (VariableElement) accType.getValue();

            if("FIELD".equals(accessType.getSimpleName().toString())) {
                return AccessType.FIELD;
            } else {
                return AccessType.PROPERTY;
            }
        }

        for( Element element: classElement.getEnclosedElements()) {

            AnnotationMirror idAnnotation = ParseUtil.getAnnotationMirrorFromElement(element, Id.class);
            AnnotationMirror embeddedIdAnnotation =
                    ParseUtil.getAnnotationMirrorFromElement(element, EmbeddedId.class);

            if(idAnnotation != null || embeddedIdAnnotation!=null) {

                if (isFieldElement(element)) {
                    return AccessType.FIELD;
                } else if (isMethodElement(element)) {
                    return AccessType.PROPERTY;
                }
            }
        }

        TypeElement superClass = getSuperClassElement(classElement);
        if(superClass != null) {
            return getAccessType(superClass);
        }

        return AccessType.PROPERTY;
    }

    private TypeElement getSuperClassElement(TypeElement classElement) {
        if(classElement.getSuperclass() != null) {
            TypeElement superClass = treeUtil.getTypeElement(classElement.getSuperclass().toString());

            if(ParseUtil.getAnnotationMirrorFromElement(superClass, MappedSuperclass.class) != null) {
                return superClass;
            }
        }
        return null;
    }

    private List<Column> doParseColumns(TypeElement classElement, AccessType accessType) {
        List<Column> result = new ArrayList<>();

        for( Element element: classElement.getEnclosedElements()) {

            if(!isValidElementForColumn(element, accessType)) {
                continue;
            }

            reporter.print(NOTE, "create column for element " + element.getSimpleName());
            reporter.print(NOTE, "element type " + getJavaPropertyType(element));

            List<Column> embeddedPkColumns = doParseEmbeddedPk(element, accessType);

            if(!embeddedPkColumns.isEmpty()) {
                result.addAll(embeddedPkColumns);
                continue;
            }


            Column column = new Column();

            updateColumnObj(column, element);
            result.add(column);
        }

        TypeElement superClass = getSuperClassElement(classElement);
        if(superClass!=null) {
            result.addAll(doParseColumns(superClass, accessType));
        }

        return result;
    }

    private boolean isValidElementForColumn(Element element, AccessType accessType) {
        if(element.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }

        if (AccessType.FIELD.equals(accessType)  &&
                !(isFieldElement(element))
        ) {
            return false;
        }

        if (AccessType.PROPERTY.equals(accessType)) {
            if(!(element instanceof ExecutableElement)) {
                return false;
            }

            ExecutableElement exeElement = (ExecutableElement) element;
            if (!(isMethodElement(element)) ||
                    !element.getSimpleName().toString().startsWith("get") ||
                    !exeElement.getParameters().isEmpty() ) {
                return false;
            }
        }

        if(getAnnotationMirrorFromElement(element, ManyToMany.class) !=null ||
           getAnnotationMirrorFromElement(element, ManyToOne.class) !=null ||
           getAnnotationMirrorFromElement(element, OneToMany.class) !=null ||
           getAnnotationMirrorFromElement(element, OneToOne.class) !=null ||
           getAnnotationMirrorFromElement(element, JoinTable.class) !=null ||
           getAnnotationMirrorFromElement(element, JoinColumn.class) !=null ||
           getAnnotationMirrorFromElement(element, JoinColumns.class) !=null) {
            return false;
        }

        return true;
    }

    private List<Column> doParseEmbeddedPk(Element element, AccessType accessType) {
        AnnotationMirror embeddedIdAnnotation =
                getAnnotationMirrorFromElement(element, javax.persistence.EmbeddedId.class);

        if(embeddedIdAnnotation != null) {

            TypeElement embedType = treeUtil.getTypeElement(getJavaPropertyType(element));

            AnnotationMirror embeddableAnnotation =
                    getAnnotationMirrorFromElement(embedType, Embeddable.class);

            if (embeddableAnnotation != null) {
                List<Column> embeddedColumns = doParseColumns(embedType, accessType);
                embeddedColumns.forEach(c -> c.setPrimaryKey(true));
                return embeddedColumns;
            }
        }

        return Collections.emptyList();
    }



    private void updateColumnObj(Column column, Element element) {
        column.setComment(treeUtil.getDocComment(element));

        AnnotationMirror columnAnnotation =
                getAnnotationMirrorFromElement(element, javax.persistence.Column.class);

        AnnotationMirror idAnnotation =
                getAnnotationMirrorFromElement(element, javax.persistence.Id.class);

        if(idAnnotation != null) {
            column.setPrimaryKey(true);
        }

        if(columnAnnotation != null) {
            String name =
                    getAnnotationAttributeStringValue("name",
                            columnAnnotation);

            if(name != null && name.trim().length() > 0) {
                column.setExplicitName(name);
            }
            column.setColumnDefinition(getAnnotationAttributeStringValue("columnDefinition",
                    columnAnnotation));

            column.setNullable(getAnnotationAttributeBooleanValue("nullable",
                    columnAnnotation));

            column.setLength(getAnnotationAttributeIntegerValue("length",
                    columnAnnotation));

        }

        column.setImplicitName(NamingStrategyHolder.getStrategy().getColumnName(element));
        column.setJavaFieldType(ParseUtil.getJavaPropertyType(element));
        column.setJavaElementName(element.getSimpleName().toString());

        column.setEnclosingClassName(((TypeElement)element.getEnclosingElement()).getQualifiedName().toString());
    }

    public static Column findByName(String columnName, Collection<Column> columnList) {

        for (Column column: columnList) {
            if(columnName.equals(column.getExplicitName()) ||
               columnName.equals(column.getImplicitName())) {
                return column;
            }
        }

        return null;
    }

    public static String getColumnPyhsicalName(Column column) {
        if(!StringUtil.isEmpty(column.getExplicitName())) {
            return column.getExplicitName();
        }

        return column.getImplicitName();
    }

    /**
     * Get fully qualified name of the Column type.
     *
     * @param column Column
     * @return ColumnType
     */
    public static String getColumnType(Column column) {
        String result = "";

        ColumnInfo colInfo = getDefaultColumnInfoByJavaType(column.getJavaFieldType());

        result = colInfo.columnType;

        if (column.getColumnDefinition() != null && column.getColumnDefinition().trim().length() > 0) {
            result = column.getColumnDefinition();
        }

        if (column.getLength() != null) {
            colInfo.length = column.getLength();
        }

        if (column.getScale() != null) {
            colInfo.scale = column.getScale();
        }

        if (column.getPrecision() != null) {
            colInfo.precision = column.getPrecision();
        }

        if (colInfo.length != null) {
            result = colInfo.columnType + "(" + colInfo.length + ")";
        } else if (colInfo.scale != null) {
            result = colInfo.columnType + "(" + colInfo.scale + ", " + colInfo.precision + ")";
        }

        return result;
    }

    private static class ColumnInfo {
        Integer length = null;
        Integer scale = null;
        Integer precision = null;
        String columnType = null;
    }

    private static ColumnInfo getDefaultColumnInfoByJavaType(String javaFieldType) {
        ColumnInfo result = new ColumnInfo();

        if (String.class.getName().equals(javaFieldType)) {
            result.length = 100;
            result.columnType = "java.sql.Types.VARCHAR";
        } else if (Boolean.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.BOOLEAN";
        } else if (Date.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.DATE";
        } else if (java.sql.Date.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.DATE";
        } else if (Timestamp.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.TIMESTAMP";
        } else if (Long.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.BIGINT";
        } else if (Integer.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.INTEGER";
        } else if (Short.class.getName().equals(javaFieldType)) {
            result.columnType = "java.sql.Types.SMALLINT";
        } else if (BigDecimal.class.getName().equals(javaFieldType)) {
            result.scale = 19;
            result.precision = 4;
            result.columnType = "java.sql.Types.DECIMAL";
        }

        return result;
    }
}
