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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.*;

public class LiquibaseDoclet implements Doclet {
    private Locale locale;
    private Reporter reporter;

    private String outputFolder;

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
        final Set<Option> options = Set.of(
                // An option that takes no arguments.
                new Option("--output", true, "the output folder of liquibase xml files", "<string>") {
                    @Override
                    public boolean process(String option,
                                           List<String> arguments) {
                        outputFolder = arguments.get(0);
                        return true;
                    }
                }
        );

        return options;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        Elements treeUtil = environment.getElementUtils();
        reporter.print(NOTE, "----------------------------");
        reporter.print(NOTE, "liquibase doclet started");
        reporter.print(NOTE, "output folder is: " + outputFolder);

        Set<? extends Element> includedElements = environment.getIncludedElements();

        TableParser tableParser = new TableParser(reporter, treeUtil);
        tableParser.parseTables(includedElements);

        return true;
    }

    abstract class Option implements Doclet.Option {
        private final String name;
        private final boolean hasArg;
        private final String description;
        private final String parameters;

        Option(String name, boolean hasArg,
               String description, String parameters) {
            this.name = name;
            this.hasArg = hasArg;
            this.description = description;
            this.parameters = parameters;
        }

        @Override
        public int getArgumentCount() {
            return hasArg ? 1 : 0;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Kind getKind() {
            return Kind.STANDARD;
        }

        @Override
        public List<String> getNames() {
            return List.of(name);
        }

        @Override
        public String getParameters() {
            return hasArg ? parameters : "";
        }
    }
}
