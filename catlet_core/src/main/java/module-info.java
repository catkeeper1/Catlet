/**
 * Catlet module.
 */
module org.ckr.catlet {
    exports org.ckr.catlet.plantuml;
    exports org.ckr.catlet.jpa;
    requires jdk.javadoc;
    requires net.sourceforge.plantuml;
    requires java.logging;
    requires hibernate.jpa;
    requires java.sql;
}