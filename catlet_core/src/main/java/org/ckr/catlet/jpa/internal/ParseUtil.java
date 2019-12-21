package org.ckr.catlet.jpa.internal;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Map;

public class ParseUtil {

    public static AnnotationMirror getAnnotationMirrorFromElement(Element element,
                                                                  Class<? extends Annotation> clazz) {
        for(AnnotationMirror annotation : element.getAnnotationMirrors()) {
            QualifiedNameable quaElement =
                    (QualifiedNameable)annotation.getAnnotationType().asElement();

            if(quaElement.getQualifiedName().toString().equals(clazz.getName())) {
                return annotation;
            }
        }

        return null;
    }

    public static AnnotationValue getAnnotationAttribute(String attributeName,
                                                         Map<? extends ExecutableElement,
                                                    ? extends AnnotationValue> annotationValues) {
        for(ExecutableElement key: annotationValues.keySet()) {
            if(key.getSimpleName().toString().equals(attributeName)) {
                return annotationValues.get(key);
            }
        }

        return null;

    }

    public static Object getAnnotationAttributeValue(String attributeName,
                                                              AnnotationMirror annotation) {
        AnnotationValue result = getAnnotationAttribute(attributeName, annotation.getElementValues());
        if(result == null) {
            return null;
        }

        return result.getValue();

    }

    public static String getAnnotationAttributeStringValue(String attributeName,
                                                     AnnotationMirror annotation) {
        Object result = getAnnotationAttributeValue(attributeName, annotation);
        if(result == null) {
            return null;
        }

        return result.toString();

    }

    public static Boolean getAnnotationAttributeBooleanValue(String attributeName,
                                                           AnnotationMirror annotation) {
        String result = getAnnotationAttributeStringValue(attributeName, annotation);
        if(result == null) {
            return null;
        }

        if("true".equalsIgnoreCase(result)) {
            return Boolean.TRUE;
        }

        if("false".equalsIgnoreCase(result)) {
            return Boolean.FALSE;
        }
        return null;

    }

    public static Integer getAnnotationAttributeIntegerValue(String attributeName,
                                                             AnnotationMirror annotation) {
        String result = getAnnotationAttributeStringValue(attributeName, annotation);
        if(result == null) {
            return null;
        }
        try {
            return Integer.valueOf(result);
        } catch (NumberFormatException e) {
            return null;
        }

    }

    public static boolean isFieldElement(Element element) {
        return element instanceof VariableElement && ElementKind.FIELD.equals(element.getKind());
    }

    public static boolean isMethodElement(Element element) {
        return element instanceof ExecutableElement && ElementKind.METHOD.equals(element.getKind());
    }

    public static boolean isGetSetMethodsExist(Element fieldElement, TypeElement classElement) {

        String fieldName = fieldElement.getSimpleName().toString();

        boolean getMethodExists = false;
        boolean setMethodExists = false;

        for(Element element: classElement.getEnclosedElements()) {

            if(element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                String methodName = executableElement.getSimpleName().toString();

                //get method
                if(executableElement.getParameters().isEmpty() &&
                   methodName.startsWith("get") &&
                   executableElement.getReturnType().equals(fieldElement.asType())) {

                    String fileNameForGetMethod = getFieldNameFromGetSetMethod(methodName);

                    if(fileNameForGetMethod.equals(fieldElement.getSimpleName().toString())) {
                        getMethodExists = true;
                        System.out.println("get method exist. " + executableElement.getSimpleName());
                    }
                }

                //set method
                if(executableElement.getParameters().size() == 1 &&
                   methodName.startsWith("set") &&
                   executableElement.getParameters().get(0).asType().equals(fieldElement.asType())) {

                    String fileNameForGetMethod = getFieldNameFromGetSetMethod(methodName);

                    if(fileNameForGetMethod.equals(fieldElement.getSimpleName().toString())) {
                        setMethodExists = true;
                        System.out.println("set method exist. " + executableElement.getSimpleName());
                    }
                }

                if(getMethodExists && setMethodExists) {
                    return true;
                }
            }
        }

        return false;
    }

    private static String getFieldNameFromGetSetMethod(String methodName) {
        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4, methodName.length());
    }

    public static boolean supportedDataType(TypeMirror typeMirror) {
        return true;
    }

    public static String getPackageName(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        if (!qualifiedName.contains(".")) {
            return "";
        }

        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));

    }
}
