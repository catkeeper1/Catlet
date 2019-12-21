package org.ckr.catlet.plantuml;


import com.sun.source.doctree.*;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Taglet;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.preproc.Defines;
import org.ckr.catlet.util.LogUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.DocumentationTool;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.*;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This taglet is used to process plantuml diagram embeded in javadoc.
 *
 * <pre><code>
 * &#60;img alt="example image" src = "exampleImg.svg"&#62;
 * &#60;!--
 *  &#64;startuml exampleImg.svg
 *  Class01 &#60;|-- Class02
 *  Class03 *-- Class04
 *  Class05 o-- Class06
 *  Class07 .. Class08
 *  Class09 &#60;-- Class10
 *  &#64;enduml
 *  --&#62;
 * </code></pre>
 *
 * <pre><code>&#64;plantuml</code></pre>
 * <img alt="example image" src = "exampleImg.svg">
 <!--
 @startuml   exampleImg.svg
 Class01 <|-- Class02
 Class03 *-- Class04
 Class05 o-- Class06
 Class07 .. Class08
 Class09 <-- Class10
 @enduml
 -->
 @plantuml
 *
 *
 */
public class PlantumlTaglet implements Taglet {


    private JavaFileManager javaFileManager = null;
    private DocTrees docTrees = null;


    private static final Set<Location> ALLOWED_LOCATION = new HashSet<>();

    static {
        ALLOWED_LOCATION.add(Location.MODULE);
        ALLOWED_LOCATION.add(Location.PACKAGE);
        ALLOWED_LOCATION.add(Location.TYPE);
        ALLOWED_LOCATION.add(Location.CONSTRUCTOR);
        ALLOWED_LOCATION.add(Location.FIELD);
        ALLOWED_LOCATION.add(Location.METHOD);
        ALLOWED_LOCATION.add(Location.OVERVIEW);
    }

    public Set<Location> getAllowedLocations() {
        Set<Location> result = new HashSet<>();
        result.addAll(ALLOWED_LOCATION);
        return result;
    }

    public boolean isInlineTag() {
        return false;
    }

    public String getName() {
        return "plantuml";
    }

    public void init(DocletEnvironment env, Doclet doclet) {
        LogUtil.log("init taglet");

        javaFileManager = env.getJavaFileManager();
        docTrees = env.getDocTrees();
    }

    public String toString(List<? extends DocTree> tags, Element element) {
        String result = processTag(tags, element);
        return result;
    }

    private String processTag(List<? extends DocTree> tags, Element element) {
        String pkg = getPackageName(element);

        DocCommentTree docComment = docTrees.getDocCommentTree(element);

        for(DocTree bodyItem: docComment.getFullBody()) {

            if(!(bodyItem instanceof CommentTree)) {
                continue;
            }
            LogUtil.log("comment block detected.");
            CommentTree commentTree = (CommentTree) bodyItem;

            ParseResult parseResult = parseUmlDiagram(commentTree.getBody());

            if(parseResult == null) {
                continue;
            }

            LogUtil.log("parse result is:\r\n" + parseResult);

            FileObject output = null;
            try {
                output = javaFileManager.getFileForOutput(DocumentationTool.Location.DOCUMENTATION_OUTPUT,
                        pkg,
                        parseResult.getFileName(),
                        null);

            } catch (IOException e) {
                LogUtil.log("cannot locate the output file.", e);
                return "";
            }
            LogUtil.log("output file " + output.toUri());

            String umlSource = parseResult.getFileContent();

            SourceStringReader reader = new SourceStringReader(umlSource);

            try(OutputStream outputStream = output.openOutputStream()) {
                reader.outputImage(outputStream, new FileFormatOption(FileFormat.SVG));
            } catch (IOException ioe) {
                LogUtil.log("cannot write image file.", ioe);
                return "";
            }

        }

        return "";
    }

    private ParseResult parseUmlDiagram(String commentBody) {

        String fileName = null;
        StringBuilder fileContent = new StringBuilder();

        BufferedReader reader = new BufferedReader(new StringReader(commentBody));
        String status = "NOT_STARTED";
        String line = null;
        do {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                LogUtil.log("cannot readline.", e);
                return null;
            }

            if(line == null) {
                break;
            }

            String trimLine = line.trim();
            if("NOT_STARTED".equals(status) &&
               trimLine.startsWith("@startuml")) {

                fileContent.append("@startuml\r\n");
                fileName = trimLine.substring(9);
                fileName = fileName.trim();

                if("".equals(fileName)) {
                    continue;
                }
                status = "STARTED";
            } else if ("STARTED".equals(status)) {

                if(trimLine.contains("@enduml")) {
                    fileContent.append("@enduml\r\n");

                    status = "COMPLETED";
                    break;
                }

                fileContent.append(trimLine);
                fileContent.append("\r\n");

            }

        } while(true);

        if("COMPLETED".equals(status)) {
            return new ParseResult(fileName, fileContent.toString());
        }

        return null;
    }

    private String getPackageName(Element element) {
        if(element instanceof TypeElement) {
            return getClassPackageName((TypeElement) element);
        }

        if(element instanceof ExecutableElement) {
            return getMethodPackageName((ExecutableElement) element);
        }

        if(element instanceof PackageElement) {
            return getPackageName((PackageElement) element);
        }

        return null;
    }

    private String getMethodPackageName(ExecutableElement element) {
        return getClassPackageName((TypeElement) element.getEnclosingElement());
    }

    private String getClassPackageName(TypeElement element) {
        PackageElement packageElement = (PackageElement) element.getEnclosingElement();
        return getPackageName(packageElement);
    }

    private String getPackageName(PackageElement element) {
        return element.getQualifiedName().toString();
    }


    private static class ParseResult {
        private String fileName;

        private String fileContent;

        public ParseResult(String fileName, String fileContent) {
            this.fileName = fileName;
            this.fileContent = fileContent;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", ParseResult.class.getSimpleName() + "[", "]")
                    .add("fileName='" + fileName + "'")
                    .add("fileContent='" + fileContent + "'")
                    .toString();
        }
    }
}
