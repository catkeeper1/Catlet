package org.ckr.catlet.jpa;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public interface NamingStrategy {

    /**
     * Return implicit column name base on the entity class name
     */
    public String getTableName(TypeElement typeElement);

    /**
     * Return implicit column name base on the method or field name
     *
     */
    public String getColumnName(Element element);
}
