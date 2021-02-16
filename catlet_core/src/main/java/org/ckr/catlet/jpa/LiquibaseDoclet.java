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
import javax.persistence.Entity;
import javax.tools.Diagnostic;
import java.util.*;

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

        Option[] options = {
                new Option() {
                    private final List<String> someOption = Arrays.asList(
                            "-output"
                    );

                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "the path of output folder";
                    }

                    @Override
                    public Option.Kind getKind() {
                        return Option.Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return someOption;
                    }

                    @Override
                    public String getParameters() {
                        return "";
                    }

                    @Override
                    public boolean process(String opt, List<String> arguments) {
                        outputFolder = arguments.get(0);
                        return true;
                    }
                }
        };

        return new HashSet<>(Arrays.asList(options));
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
}
