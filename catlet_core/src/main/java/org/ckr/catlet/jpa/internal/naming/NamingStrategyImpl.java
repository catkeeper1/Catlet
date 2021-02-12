package org.ckr.catlet.jpa.internal.naming;

import org.ckr.catlet.jpa.NamingStrategy;
import org.ckr.catlet.jpa.internal.util.ParseUtil;
import org.ckr.catlet.jpa.internal.util.NamingUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class NamingStrategyImpl implements NamingStrategy {
    @Override
    public String getTableName(TypeElement typeElement) {

        String className = typeElement.getSimpleName().toString();

        return NamingUtil.convertCamelCaseToUnderScoreCase(className);

    }

    @Override
    public String getColumnName(Element element) {
        String colName = ParseUtil.getJavaPropertyName(element);

        return NamingUtil.convertCamelCaseToUnderScoreCase(colName);

    }
}
