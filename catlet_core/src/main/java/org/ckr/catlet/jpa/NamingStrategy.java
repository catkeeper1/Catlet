package org.ckr.catlet.jpa;

import javax.lang.model.element.TypeElement;

public interface NamingStrategy {
    public String getTableName(TypeElement typeElement);
}
