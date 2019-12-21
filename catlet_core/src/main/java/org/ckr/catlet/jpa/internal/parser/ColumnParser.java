package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.ParseUtil;
import org.ckr.catlet.jpa.internal.vo.Column;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.util.ArrayList;
import java.util.List;

import static org.ckr.catlet.jpa.internal.ParseUtil.*;

import static javax.tools.Diagnostic.Kind.*;
import static org.ckr.catlet.jpa.internal.ParseUtil.isFieldElement;

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
            reporter.print(NOTE, "Access type is field");

            result = parseFromFields(classElement);
        } else {
            reporter.print(NOTE, "Access type is property");
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

            reporter.print(NOTE, "create column for field element" + element.getSimpleName());

            reporter.print(NOTE, "element type " + element.asType());

            Column column = new Column();

            column.setComment(treeUtil.getDocComment(element));

            AnnotationMirror columnAnnotation =
                    getAnnotationMirrorFromElement(element, javax.persistence.Column.class);

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


            result.add(column);
        }

        return result;
    }
}
