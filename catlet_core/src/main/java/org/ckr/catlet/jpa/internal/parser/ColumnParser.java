package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.util.ParseUtil;
import org.ckr.catlet.jpa.internal.naming.NamingStrategyHolder;
import org.ckr.catlet.jpa.internal.vo.Column;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.util.ArrayList;
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
        List<Column> result = new ArrayList<>();

        AccessType accessType = getAccessType(classElement);
        reporter.print(NOTE, "Access type is " + accessType);
        if(accessType == null) {
            return result;
        }

        if(AccessType.FIELD.equals(accessType)) {
            reporter.print(NOTE, "parse columns from fields");

            result = parseFromFields(classElement);
        } else {
            reporter.print(NOTE, "parse columns from get methods");

            result = parseFromGetMethods(classElement);
        }

        return result;
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

            if(idAnnotation != null) {

                if (isFieldElement(element)) {
                    return AccessType.FIELD;
                } else if (isMethodElement(element)) {
                    return AccessType.PROPERTY;
                }
            }
        }

        if(classElement.getSuperclass() != null) {
            TypeElement superClass = treeUtil.getTypeElement(classElement.getSuperclass().toString());

            if(ParseUtil.getAnnotationMirrorFromElement(superClass, MappedSuperclass.class) != null) {
                return getAccessType(superClass);
            }
        }
        return null;
    }

    private List<Column> parseFromFields(TypeElement classElement) {
        List<Column> result = new ArrayList<>();

        for( Element element: classElement.getEnclosedElements()) {
            if (!(isFieldElement(element)) ||
                element.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }

            reporter.print(NOTE, "create column for field element " + element.getSimpleName());

            reporter.print(NOTE, "element type " + element.asType());

            Column column = new Column();

            updateColumnObj(column, element);
            result.add(column);
        }

        return result;
    }

    private List<Column> parseFromGetMethods(TypeElement classElement) {
        List<Column> result = new ArrayList<>();

        for( Element element: classElement.getEnclosedElements()) {
            if(!(element instanceof ExecutableElement)) {
                continue;
            }

            ExecutableElement exeElement = (ExecutableElement) element;

            if (!(isMethodElement(element)) ||
                 element.getModifiers().contains(Modifier.STATIC) ||
                 !element.getSimpleName().toString().startsWith("get") ||
                 !exeElement.getParameters().isEmpty() ) {
                continue;
            }

            reporter.print(NOTE, "create column for get method " + exeElement.getSimpleName());

            reporter.print(NOTE, "element type " + exeElement.getReturnType());

            Column column = new Column();

            updateColumnObj(column, element);
            result.add(column);
        }

        return result;
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
}
