package com.vladsch.flexmark.docx.converter;

import com.vladsch.flexmark.docx.converter.util.XmlDocxSorter;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.spec.SpecExample;
import com.vladsch.flexmark.spec.SpecReader;
import com.vladsch.flexmark.superscript.SuperscriptExtension;
import com.vladsch.flexmark.test.ComboSpecTestCase;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.*;

public class ComboDocxUserSpecDisabled extends ComboSpecTestCase {
    private static final boolean DUMP_TEST_CASE_FILES = !ComboDocxConverterSpecTest.SKIP_IGNORED_TESTS;
    private static final String PROJECT_ROOT_DIRECTORY = ComboDocxConverterSpecTest.PROJECT_ROOT_DIRECTORY;
    private static final String FILE_TEST_CASE_DUMP_LOCATION = "/flexmark-docx-converter/src/test/resources/docx_user_ast_spec/";

    private static final String SPEC_RESOURCE = "/docx_user_ast_spec.md";
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(HtmlRenderer.INDENT_SIZE, 2)
            .set(Parser.EXTENSIONS, Arrays.asList(
                    DefinitionExtension.create(),
                    EmojiExtension.create(),
                    FootnoteExtension.create(),
                    StrikethroughSubscriptExtension.create(),
                    InsExtension.create(),
                    SuperscriptExtension.create(),
                    TablesExtension.create(),
                    TocExtension.create(),
                    WikiLinkExtension.create()
            ))
            .set(DocxRenderer.RENDER_BODY_ONLY, true)
            .set(DocxRenderer.DOC_RELATIVE_URL, String.format("file://%s/flexmark-docx-converter/src/test/resources/05-SW-Software-Development", PROJECT_ROOT_DIRECTORY))
            .set(DocxRenderer.DOC_ROOT_URL, String.format("file://%s/flexmark-docx-converter/src/test/resources/05-SW-Software-Development", PROJECT_ROOT_DIRECTORY))
            .set(DocxRenderer.SUPPRESS_HTML, true)
            //.set(HtmlRendere  r.PERCENT_ENCODE_URLS, true)
            ;

    private static final Map<String, DataHolder> optionsMap = new HashMap<String, DataHolder>();
    //private static WordprocessingMLPackage ourPackage = new WordprocessingMLPackage();;

    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    // The spec says URL-escaping is optional, but the examples assume that it's enabled.
    private static final DocxRenderer RENDERER = DocxRenderer.builder(OPTIONS).build();
    static {
        //optionsMap.put("src-pos", new MutableDataSet().set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, "md-pos"));
        //optionsMap.put("option1", new MutableDataSet().set(DocxConverterExtension.DOCX_CONVERTER_OPTION1, true));
        optionsMap.put("IGNORED", new MutableDataSet().set(IGNORE, false));
        optionsMap.put("url", new MutableDataSet().set(DocxRenderer.DOC_RELATIVE_URL, String.format("file://%s", PROJECT_ROOT_DIRECTORY)));

        // Set up a simple configuration that logs on the console.
        //BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.addAppender(new NullAppender());
    }
    private static DataHolder optionsSet(String optionSet) {
        if (optionSet == null) return null;
        return optionsMap.get(optionSet);
    }

    public ComboDocxUserSpecDisabled(SpecExample example) {
        super(example);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        List<SpecExample> examples = SpecReader.readExamples(SPEC_RESOURCE);
        List<Object[]> data = new ArrayList<Object[]>();

        // NULL example runs full spec test
        data.add(new Object[] { SpecExample.NULL });

        for (SpecExample example : examples) {
            data.add(new Object[] { example });
        }
        return data;
    }

    @Override
    public DataHolder options(String optionSet) {
        return optionsSet(optionSet);
    }

    @Override
    public String getSpecResourceName() {
        return SPEC_RESOURCE;
    }

    @Override
    public Parser parser() {
        return PARSER;
    }

    @Override
    public IRender renderer() {
        return RENDERER;
    }

    @Override
    protected void testCase(final Node node, final DataHolder options) {
        if (!DUMP_TEST_CASE_FILES) return;

        final SpecExample specExample = example();
        if (!specExample.isFullSpecExample() && !specExample.getSection().isEmpty()) {
            // write it out to file, hard-coded for now                    IGNORE
            File file = new File(String.format("%s/%s%s_%d.docx", PROJECT_ROOT_DIRECTORY, FILE_TEST_CASE_DUMP_LOCATION, specExample.getSection(), specExample.getExampleNumber()));
            File file2 = new File(String.format("%s/%s%s_%d.xml", PROJECT_ROOT_DIRECTORY, FILE_TEST_CASE_DUMP_LOCATION, specExample.getSection(), specExample.getExampleNumber()));
            WordprocessingMLPackage mlPackage = DocxRenderer.getDefaultTemplate();
            if (mlPackage != null) {
                RENDERER.withOptions(options).render(node, mlPackage);

                try {
                    mlPackage.save(file, Docx4J.FLAG_SAVE_ZIP_FILE);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        mlPackage.save(outputStream, Docx4J.FLAG_SAVE_FLAT_XML);
                        final String xml = outputStream.toString("UTF-8");
                        final String s = XmlDocxSorter.sortDocumentParts(xml);
                        FileWriter fileWriter = new FileWriter(file2);
                        fileWriter.append(s);
                        fileWriter.append('\n');
                        fileWriter.close();
                    } catch (Docx4JException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Docx4JException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
