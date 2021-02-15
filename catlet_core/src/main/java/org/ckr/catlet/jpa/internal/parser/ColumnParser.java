package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import net.sourceforge.plantuml.bpm.Col;
import org.ckr.catlet.jpa.internal.util.ParseUtil;
import org.ckr.catlet.jpa.internal.naming.NamingStrategyHolder;
import org.ckr.catlet.jpa.internal.vo.Column;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

            if(element.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }

            if (AccessType.FIELD.equals(accessType)  &&
                !(isFieldElement(element))
                ) {
                continue;
            }

            if (AccessType.PROPERTY.equals(accessType)) {
                if(!(element instanceof ExecutableElement)) {
                    continue;
                }

                ExecutableElement exeElement = (ExecutableElement) element;
                if (!(isMethodElement(element)) ||
                    !element.getSimpleName().toString().startsWith("get") ||
                    !exeElement.getParameters().isEmpty() ) {
                    continue;
                }
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
        column.setJavaPropertyName(ParseUtil.getJavaPropertyName(element));
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
}
