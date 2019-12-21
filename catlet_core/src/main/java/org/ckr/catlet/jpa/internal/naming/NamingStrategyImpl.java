package org.ckr.catlet.jpa.internal.naming;

import org.ckr.catlet.jpa.NamingStrategy;

import javax.lang.model.element.TypeElement;

public class NamingStrategyImpl implements NamingStrategy {
    @Override
    public String getTableName(TypeElement typeElement) {

        String className = typeElement.getSimpleName().toString();

        StringBuilder result = new StringBuilder();

        for(int i = 0; i < className.length(); i++ ) {
            char ch = className.charAt(i);

            result.append(Character.toUpperCase(ch));

            if(Character.isLowerCase(ch) &&
                    (i + 1) < className.length() &&
                    Character.isUpperCase(className.charAt(i + 1))) {

                result.append('_');
            }
        }

        return result.toString();
    }
}
