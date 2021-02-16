package org.ckr.catlet.jpa.internal.writer;


import jdk.javadoc.doclet.Reporter;
import org.ckr.catlet.jpa.internal.exception.DocletException;
import org.ckr.catlet.jpa.internal.util.FileUtil;
import org.ckr.catlet.jpa.internal.util.FileWriterTemplate;
import org.ckr.catlet.jpa.internal.vo.Column;
import org.ckr.catlet.jpa.internal.vo.Index;
import org.ckr.catlet.jpa.internal.vo.Table;

import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import static org.ckr.catlet.jpa.internal.parser.ColumnParser.getColumnPyhsicalName;
import static org.ckr.catlet.jpa.internal.parser.ColumnParser.getColumnType;
import static org.ckr.catlet.jpa.internal.parser.TableParser.getTablePyhsicalName;
import static org.ckr.catlet.jpa.internal.util.StringUtil.genIndent;

/**
 * Created by Administrator on 2017/6/20.
 */
public class LiquibaseWriter {

    private static final String COLUMN_TAG_START = "<column name=\"";

    private static final String COLUMN_TAG_TYPE = "\" type=\"";

    private static final String ENTER = "\r\n";

    private static final String DOC_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ENTER
                    + "<databaseChangeLog" + ENTER
                    + "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"" + ENTER
                    + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + ENTER
                    + "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog" + ENTER
                    + "         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd\">" + ENTER;

    private static final String DOC_END = "</databaseChangeLog>";

    private static final String CHANGE_SET_END = genIndent(1) + "</changeSet>" + ENTER;


    private File baseDir;

    private List<Table> tableList;

    private Reporter reporter;

    public LiquibaseWriter(String baseDirPath, List<Table> tableList, Reporter reporter) {
        createBaseDir(baseDirPath);
        this.tableList = tableList;
        this.reporter = reporter;
    }

    protected void createBaseDir(String baseDirPath) {
        File dir = new File(baseDirPath);

        if (!dir.isDirectory()) {
            throw new DocletException(dir.getAbsolutePath() + " is not a valid dir.");
        }

        dir = new File(dir, "liquibaseXml");

        if (!dir.exists() && !dir.mkdir()) {
            throw new DocletException("cannot create directory:" + dir.getAbsolutePath());

        }

        this.baseDir = dir;
    }

    private File createDocFile(Table table, String fileName) {


        File result = FileUtil.createDirectory(this.baseDir,
                table.getPackageName().replace(".", "/"));


        result = new File(result, fileName);


        try {
            boolean created = result.createNewFile();

            if(!created) {
                reporter.print(Diagnostic.Kind.NOTE,
                        fileName + " already exist. The file content will be replaced");
            }

        } catch (IOException ex) {

            throw new DocletException("create not create new file " + result.getAbsolutePath(), ex);
        }

        return result;
    }

    /**
     * Generate Ddl statement of Liquibase for tables.
     * <pre>
     *     <code>
     * &#60;?xml version="1.0" encoding="UTF-8"?&#62;
     * &#60;databaseChangeLog
     *         xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
     *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *         xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
     *          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd"&#62;
     *     &#60;changeSet author="liquibase-docs" id="createTable-org.ckr.msdemo.adminservice.entity.MENU"&#62;
     *
     *         &#60;createTable tableName="MENU"&#62;
     *             &#60;column name="CODE" type="java.sql.Types.VARCHAR(100)"&#62;
     *                 &#60;constraints nullable="false"/&#62;
     *             &#60;/column&#62;
     *             &#60;column name="PARENT_CODE" type="java.sql.Types.VARCHAR(100)"/&#62;
     *             &#60;column name="DESCRIPTION" type="java.sql.Types.VARCHAR(200)"/&#62;
     *             &#60;column name="FUNCTION_POINT" type="java.sql.Types.VARCHAR(100)"/&#62;
     *             &#60;column name="MODULE" type="java.sql.Types.VARCHAR(100)"/&#62;
     *         &#60;/createTable&#62;
     *     &#60;/changeSet&#62;
     *     &#60;changeSet author="liquibase-docs" id="createTablePk-org.ckr.msdemo.adminservice.entity.MENU"&#62;
     *
     *         &#60;addPrimaryKey constraintName="PK_MENU" columnNames="CODE" tableName="MENU" /&#62;
     *     &#60;/changeSet&#62;
     * &#60;/databaseChangeLog&#62;
     *     </code>
     * </pre>
     */
    public void generateDdlXmlConfigDoc() {
        for (Table table : tableList) {

            File docFile = createDocFile(table, "db.changelog.create_" + getTablePyhsicalName(table) + ".xml");

            final LiquibaseWriter t = this;

            new FileWriterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeDdlDoc(table, writer);
                }
            }.execute();

        }

    }

    private void writeDdlDoc(Table table, OutputStreamWriter writter) throws IOException {
        //write header
        writter.write(DOC_HEADER);

        writeTableContent(table, writter);

        writePrimaryContent(table, writter);

        writeIndexContent(table, writter);
        //the end
        writter.write(DOC_END);
    }

    private void writeTableContent(Table table, OutputStreamWriter writter) throws IOException {
        writeChangeSet(writter, "createTable-" + table.getPackageName() + "." + getTablePyhsicalName(table));
        writter.write(ENTER);

        writter.write(genIndent(2) + "<createTable tableName=\""
                + getTablePyhsicalName(table) + "\">" + ENTER);

        for (Column column : table.getColumnList()) {
            this.writeColumnContent(column, writter);
        }

        writter.write(genIndent(2) + "</createTable>" + ENTER);
        writter.write(CHANGE_SET_END);
    }

    private void writePrimaryContent(Table table, OutputStreamWriter writter) throws IOException {
        StringBuilder fieldNames = new StringBuilder("");
        for (Column column : table.getColumnList()) {
            if (Boolean.TRUE.equals(column.getPrimaryKey())) {

                if (fieldNames.length() > 0) {
                    fieldNames.append(",");
                }

                fieldNames.append(getColumnPyhsicalName(column));

            }
        }

        if (fieldNames.length() == 0) {
            return;
        }

        String tablePhysicalName = getTablePyhsicalName(table);

        writeChangeSet(writter, "createTablePk-" + table.getPackageName() + "." + tablePhysicalName);
        writter.write(ENTER);

        writter.write(genIndent(2) + "<addPrimaryKey "
                + "constraintName=\"" + "PK_" + tablePhysicalName + "\" "
                + "columnNames=\"" + fieldNames.toString() + "\" "
                + "tableName=\"" + tablePhysicalName + "\" />" + ENTER);

        writter.write(CHANGE_SET_END);
    }

    private void writeIndexContent(Table table, OutputStreamWriter writter) throws IOException {

        if (table.getIndexList().isEmpty()) {
            return;
        }

        String tablePhysicalName = getTablePyhsicalName(table);

        writeChangeSet(writter, "createTableIndex-" + table.getPackageName() + "." + tablePhysicalName);

        int noOfIndex = 0;

        for (Index index : table.getIndexList()) {

            boolean unique = false;
            if (Boolean.TRUE.equals(index.getUnique())) {
                unique = true;
            }

            String indexName = "IND_" + tablePhysicalName + "_" + noOfIndex;

            if (index.getName() != null) {
                indexName = index.getName();
            }

            writter.write(genIndent(2) + "<createIndex "
                    + "indexName=\"" + indexName + "\" "
                    + "tableName=\"" + tablePhysicalName + "\" "
                    + "unique=\"" + unique + "\">" + ENTER);

            for (Index.IndexColumn indexColumn : index.getColumnList()) {

                String physicalColumnName = getColumnPyhsicalName(indexColumn.getColumn());
                writter.write(genIndent(3) + COLUMN_TAG_START + physicalColumnName + "\"/>" + ENTER);

            }

            writter.write(genIndent(2) + "</createIndex>" + ENTER);

            noOfIndex++;
        }
        writter.write(CHANGE_SET_END);
    }

    private void writeColumnContent(Column column, OutputStreamWriter writter) throws IOException {
        String physicalColumnName = getColumnPyhsicalName(column);
        if (Boolean.TRUE.equals(column.getPrimaryKey())) {

            writter.write(genIndent(3)
                    + COLUMN_TAG_START + physicalColumnName + COLUMN_TAG_TYPE + getColumnType(column) + "\">" + ENTER);

            writter.write(genIndent(4) + "<constraints nullable=\"false\"/>" + ENTER);

            writter.write(genIndent(3)
                    + "</column>" + ENTER);

        } else {
            writter.write(genIndent(3)
                    + COLUMN_TAG_START + physicalColumnName + COLUMN_TAG_TYPE + getColumnType(column) + "\"/>" + ENTER);
        }

    }




    /**
     * Generate insert statement of Liquibase for tables.
     * <pre>
     *     <code>
     * &#60;?xml version="1.0" encoding="UTF-8"?&#62;
     * &#60;databaseChangeLog
     *         xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
     *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *         xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
     *          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd"&#62;
     *     &#60;changeSet author="liquibase-docs" id="insertTable-org.ckr.msdemo.adminservice.entity.MENU" context="!UT"&#62;
     *
     *         &#60;loadUpdateData file="org/ckr/msdemo/adminservice/entity/MENU.csv"
     *                         primaryKey="CODE"
     *                         tableName="MENU"&#62;
     *         &#60;/loadUpdateData&#62;
     *     &#60;/changeSet&#62;
     * &#60;/databaseChangeLog&#62;
     *     </code>
     * </pre>
     */
    public void generateInsertXmlConfigDoc() {
        for (Table table : tableList) {

            File docFile = createDocFile(table, "db.changelog.insert_" + getTablePyhsicalName(table) + ".xml");

            final LiquibaseWriter t = this;

            new FileWriterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeInsertDoc(table, writer);
                }
            }.execute();

        }

    }

    private void writeInsertDoc(Table table, OutputStreamWriter writter) throws IOException {
        //write header
        writter.write(DOC_HEADER);

        writeInsertTableContent(table, writter);

        //the end
        writter.write(DOC_END);
    }

    private void writeInsertTableContent(Table table, OutputStreamWriter writter) throws IOException {

        StringBuilder pkStr = new StringBuilder();

        for (Column column : table.getColumnList()) {
            if (Boolean.TRUE.equals(column.getPrimaryKey())) {

                if (pkStr.length() > 0) {
                    pkStr.append(",");
                }

                pkStr.append(getColumnPyhsicalName(column));
            }
        }

        String tablePyhsicalName = getTablePyhsicalName(table);

        writeChangeSet(writter,
                "insertTable-"
                        + table.getPackageName()
                        + "." + tablePyhsicalName,
                "!UT");


        writter.write(ENTER);

        writter.write(genIndent(2) + "<loadUpdateData file=\""
                + table.getPackageName().replace('.', '/') + "/"
                + tablePyhsicalName + ".csv" + "\"" + ENTER);

        if (pkStr.length() > 0) {
            writter.write(genIndent(2) + "                primaryKey=\""
                    + pkStr.toString() + "\"" + ENTER);
        }

        writter.write(genIndent(2) + "                tableName=\""
                + tablePyhsicalName + "\">" + ENTER);


        for (Column column : table.getColumnList()) {
            this.writeInsertColumnContent(column, writter);
        }

        writter.write(genIndent(2) + "</loadUpdateData>" + ENTER);
        writter.write(CHANGE_SET_END);
    }

    private void writeInsertColumnContent(Column column, OutputStreamWriter writter) throws IOException {

        Class javaFieldType = null;

        try {
            javaFieldType = this.getClass().getClassLoader().loadClass(column.getJavaFieldType());
        } catch (ClassNotFoundException ex) {
            javaFieldType = String.class;
        }

        String columnType = null;

        if (Date.class.isAssignableFrom(javaFieldType)) {
            columnType = "DATE";
        } else if (Number.class.isAssignableFrom(javaFieldType)) {
            columnType = "NUMERIC";
        } else if (Boolean.class.isAssignableFrom(javaFieldType)) {
            columnType = "BOOLEAN";
        }

        if (columnType == null) {
            return;
        }

        writter.write(genIndent(3)
                + COLUMN_TAG_START + getColumnPyhsicalName(column) + COLUMN_TAG_TYPE + columnType + "\"/>" + ENTER);

    }


    /**
     * Generate insert statement of Liquibase for tables
     * <pre>&#60;include file="org/ckr/msdemo/adminservice/entity/db.changelog.create_USER.xml"/&#62;</pre>
     */
    public void generateInsertCsvTemplate() {
        for (Table table : tableList) {

            File docFile = createDocFile(table, getTablePyhsicalName(table) + ".csv");

            final LiquibaseWriter t = this;

            new FileWriterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeCsvTemplateHeader(table, writer);
                }
            }.execute();

        }

    }

    private void writeCsvTemplateHeader(Table table, OutputStreamWriter writer) throws IOException {

        for (int i = 0; i < table.getColumnList().size(); i++) {
            Column column = table.getColumnList().get(i);

            if (i > 0) {
                writer.write(",");
            }
            writer.write(getColumnPyhsicalName(column));

        }


    }

    /**
     * Generate include statement of Liquibase for tables
     * <pre>&#60;include file="org/ckr/msdemo/adminservice/entity/db.changelog.create_USER.xml"/&#62;</pre>
     */
    public void generateIncludeXmlConfig() {
        for (Table table : tableList) {

            File docFile = createDocFile(table, "db.changelog." + getTablePyhsicalName(table) + ".xml");

            final LiquibaseWriter t = this;

            new FileWriterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeIncludeContent(table, writer);
                }
            }.execute();

        }

    }

    private void writeIncludeContent(Table table, OutputStreamWriter writter) throws IOException {

        writter.write(DOC_HEADER);

        writter.write(genIndent(1) + "<include file=\""
                + table.getPackageName().replace(".", "/") + "/"
                + "db.changelog.create_" + getTablePyhsicalName(table) + ".xml\"/>" + ENTER);

        writter.write(genIndent(1) + "<include file=\""
                + table.getPackageName().replace(".", "/") + "/"
                + "db.changelog.insert_" + getTablePyhsicalName(table) + ".xml\"/>" + ENTER);

        writter.write(DOC_END);


    }

    private void writeChangeSet(OutputStreamWriter writter, String changeId) throws IOException {
        writter.write(genIndent(1) + "<changeSet author=\"liquibase-docs\" id=\""
                + changeId + "\">" + ENTER);
    }

    private void writeChangeSet(OutputStreamWriter writter, String changeId, String context) throws IOException {
        writter.write(genIndent(1) + "<changeSet author=\"liquibase-docs\" id=\""
                + changeId + "\" context=\"" + context + "\">" + ENTER);
    }

}
