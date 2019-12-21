package org.ckr.catlet.jpa;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.parser.TableParser;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.persistence.Entity;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.*;

public class LiquibaseDoclet implements Doclet {
    private Locale locale;
    private Reporter reporter;

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.locale = locale;
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return LiquibaseDoclet.class.getName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return new HashSet<>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        Elements treeUtil = environment.getElementUtils();
        reporter.print(NOTE, "----------------------------");
        Set<? extends Element> includedElements = environment.getIncludedElements();

        TableParser tableParser = new TableParser(reporter, treeUtil);
        tableParser.parseTables(includedElements);

        return true;
    }
}
