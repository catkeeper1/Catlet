package org.ckr.catlet.jpa.internal.util;


import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.exception.DocletException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class FileWriterTemplate {

    private File file = null;


    public FileWriterTemplate(File file) {
        this.file = file;
    }

    protected abstract void doWrite(OutputStreamWriter writer) throws IOException;

    /**
     * Template steps for Liquibase file generation.
     */
    public void execute() {

        try (FileWriter docWriter = new FileWriter(file)) {

            this.doWrite(docWriter);
            docWriter.flush();
        } catch (IOException ioExp) {
            throw new DocletException("cannot write file.", ioExp);
        }

    }


}
