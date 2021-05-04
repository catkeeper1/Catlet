package org.ckr.catlet.jpa;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.parser.TableParser;
import org.ckr.catlet.jpa.internal.vo.Table;
import org.ckr.catlet.jpa.internal.writer.LiquibaseWriter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
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

        reporter.print(NOTE, "LiquibaseDoclet is started");
        reporter.print(NOTE, "The generated xml files will be saved to folder:" + outputFolder);


        Set<? extends Element> includedElements = environment.getIncludedElements();

        TableParser tableParser = new TableParser(reporter, treeUtil);
        List<Table> tableList = tableParser.parseTables(includedElements);

        LiquibaseWriter writer = new LiquibaseWriter(outputFolder, tableList, reporter);

        writer.generateDdlXmlConfigDoc();
        writer.generateInsertXmlConfigDoc();
        writer.generateInsertCsvTemplate();
        writer.generateIncludeXmlConfig();

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
