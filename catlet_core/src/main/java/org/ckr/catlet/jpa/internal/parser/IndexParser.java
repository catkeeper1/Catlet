package org.ckr.catlet.jpa.internal.parser;

import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.util.ParseUtil;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import java.util.List;

import static javax.tools.Diagnostic.Kind.NOTE;

public class IndexParser {

    private final Reporter reporter;
    private final Elements treeUtil;

    public IndexParser(Reporter reporter, Elements treeUtil) {
        this.reporter = reporter;
        this.treeUtil = treeUtil;
    }

    public void parseIndexes(TypeElement typeElement) {
        AnnotationMirror tableAnnotation =
                ParseUtil.getAnnotationMirrorFromElement(typeElement, javax.persistence.Table.class);

        if(tableAnnotation != null) {
            AnnotationValue indexAnnotation = ParseUtil.getAnnotationAttribute("indexes",
                    tableAnnotation.getElementValues());

            if(indexAnnotation != null) {
                List indexList = (List)indexAnnotation.getValue();

                indexList.forEach(index -> {
                    AnnotationMirror indexValues = (AnnotationMirror) index;

                    indexValues.getElementValues().values().forEach(k -> reporter.print(NOTE, "================ " + k));

                });

            }
        }
    }
}
