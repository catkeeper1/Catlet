package org.ckr.catlet.plantuml;

import com.sun.source.doctree.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringJoiner;

public class PlantumlTagVisitor implements DocTreeVisitor<PlantumlTagVisitor.ParseResult, String> {


    @Override
    public ParseResult visitAttribute(AttributeTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitAuthor(AuthorTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitComment(CommentTree node, String s) {
        System.out.println("visitComment");
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitDeprecated(DeprecatedTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitDocComment(DocCommentTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitDocRoot(DocRootTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitEndElement(EndElementTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitEntity(EntityTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitErroneous(ErroneousTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitIdentifier(IdentifierTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitInheritDoc(InheritDocTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitLink(LinkTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitLiteral(LiteralTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitParam(ParamTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitReference(ReferenceTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitReturn(ReturnTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitSee(SeeTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitSerial(SerialTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitSerialData(SerialDataTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitSerialField(SerialFieldTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitSince(SinceTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitStartElement(StartElementTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitText(TextTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitThrows(ThrowsTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitUnknownBlockTag(UnknownBlockTagTree node, String s) {
        System.out.println("visitUnknownBlockTag");

        System.out.println(node.getContent().size());
        if(node == null ||
           node.getContent() == null ||
           node.getContent().isEmpty()) {
            return null;
        }

        String fileName = null;
        StringBuilder fileContent = new StringBuilder();
        String contentValue = node.getContent().get(0).toString();
        System.out.println(contentValue);
        BufferedReader reader = new BufferedReader(new StringReader(contentValue));

        String line = null;
        do {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                return null;
            }

            if(line == null) {
                break;
            }

            if(fileName == null) {
                fileName = line;
            } else {
                fileContent.append(line);
                fileContent.append("\r\n");
            }
        } while(true);

        ParseResult result = new ParseResult(fileName, fileContent.toString());
        return result;
    }

    @Override
    public ParseResult visitUnknownInlineTag(UnknownInlineTagTree node, String s) {
        System.out.println("visitUnknownInlineTag");

        System.out.println(node.getContent().size());
        if(node == null ||
                node.getContent() == null ||
                node.getContent().isEmpty()) {
            return null;
        }

        String fileName = null;
        StringBuilder fileContent = new StringBuilder();
        String contentValue = node.getContent().get(0).toString();
        System.out.println(contentValue);
        BufferedReader reader = new BufferedReader(new StringReader(contentValue));

        String line = null;
        do {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                return null;
            }

            if(line == null) {
                break;
            }

            if(fileName == null) {
                fileName = line;
            } else {
                fileContent.append(line);
                fileContent.append("\r\n");
            }
        } while(true);

        ParseResult result = new ParseResult(fileName, fileContent.toString());
        return result;
    }

    @Override
    public ParseResult visitValue(ValueTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitVersion(VersionTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public ParseResult visitOther(DocTree node, String s) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    public static class ParseResult {
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
