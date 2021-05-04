package org.ckr.catlet.jpa.internal.util;

import org.ckr.catlet.jpa.internal.exception.DocletException;

import java.io.File;
import java.util.StringTokenizer;

public class FileUtil {
    /**
     * Create Directories at baseDir followed by the hierarchy of path separated by period.
     *
     * @param baseDir baseDir
     * @param path    path
     * @return baseDir
     */
    public static File createDirectory(File baseDir, String path) {

        File result = baseDir;

        StringTokenizer tokenizer = new StringTokenizer(path, ".");

        while (tokenizer.hasMoreTokens()) {
            result = new File(result, tokenizer.nextToken());
            if (!result.exists() && !result.mkdirs()) {

                throw new DocletException("Cannot create directory " + result.getAbsolutePath());

            }
        }

        return result;

    }
}
