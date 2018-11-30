package com.vladsch.flexmark.test;

import com.vladsch.flexmark.IParse;
import com.vladsch.flexmark.IRender;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.spec.SpecExample;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public abstract class RenderingTestCase {

    public static final String IGNORE_OPTION_NAME = "IGNORE";
    public static final String FAIL_OPTION_NAME = "FAIL";
    public static final String NO_FILE_EOL_OPTION_NAME = "NO_FILE_EOL";
    public static final String FILE_EOL_OPTION_NAME = "FILE_EOL";
    public static final String TIMED_OPTION_NAME = "TIMED";
    public static final String EMBED_TIMED_OPTION_NAME = "EMBED_TIMED";
    public static final String TIMED_FORMAT_STRING = "Timing: parse %.3f ms, render %.3f ms, total %.3f\n";
    public static final DataKey<Boolean> FAIL = new DataKey<Boolean>(FAIL_OPTION_NAME, false);
    public static final DataKey<Boolean> IGNORE = new DataKey<Boolean>(IGNORE_OPTION_NAME, false);
    public static final DataKey<Boolean> NO_FILE_EOL = new DataKey<Boolean>(NO_FILE_EOL_OPTION_NAME, true);
    public static final DataKey<Boolean> TIMED = new DataKey<Boolean>(TIMED_OPTION_NAME, false);
    public static final DataKey<Boolean> EMBED_TIMED = new DataKey<Boolean>(TIMED_OPTION_NAME, false);
    public static final DataKey<String> INCLUDED_DOCUMENT = new DataKey<>("INCLUDED_DOCUMENT", "");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public abstract IParse parser();
    public abstract IRender renderer();
    public abstract SpecExample example();

    /**
     * Customize options for an example
     *
     * @param optionSet name of the options set to use
     * @return options or null to use default
     */
    public DataHolder options(String optionSet) {
        assert optionSet == null;
        return null;
    }

    /**
     * process comma separated list of option sets and combine them for final set to use
     *
     * @param example    spec example instance for which options are being processed
     * @param optionSets comma separate list of option set names
     * @return combined set from applying these options together
     */
    public DataHolder getOptions(SpecExample example, String optionSets) {
        if (optionSets == null) return null;
        String[] optionNames = optionSets.replace('\u00A0', ' ').split(",");
        DataHolder options = null;
        boolean isFirst = true;
        for (String optionName : optionNames) {
            String option = optionName.trim();
            if (option.isEmpty() || option.startsWith("-")) continue;

            if (option.equals(IGNORE_OPTION_NAME)) {//noinspection ConstantConditions
                throwIgnoredOption(example, optionSets, option);
            } else if (option.equals(FAIL_OPTION_NAME)) {
                options = addOption(options, FAIL, true);
            } else if (option.equals(NO_FILE_EOL_OPTION_NAME)) {
                options = addOption(options, NO_FILE_EOL, true);
            } else if (option.equals(FILE_EOL_OPTION_NAME)) {
                options = addOption(options, NO_FILE_EOL, false);
            } else if (option.equals(TIMED_OPTION_NAME)) {
                options = addOption(options, TIMED, true);
            } else if (option.equals(EMBED_TIMED_OPTION_NAME)) {
                options = addOption(options, EMBED_TIMED, true);
            } else {
                if (options == null) {
                    options = options(option);

                    if (options == null) {
                        throw new IllegalStateException("Option " + option + " is not implemented in the RenderingTestCase subclass");
                    }
                } else {
                    DataHolder dataSet = options(option);

                    if (dataSet != null) {
                        if (isFirst) {
                            options = new MutableDataSet(options);
                            isFirst = false;
                        }
                        ((MutableDataSet) options).setAll(dataSet);
                    } else {
                        throw new IllegalStateException("Option " + option + " is not implemented in the RenderingTestCase subclass");
                    }
                }

                if (IGNORE.getFrom(options)) {
                    //noinspection ConstantConditions
                    throwIgnoredOption(example, optionSets, option);
                }
            }
        }
        return options;
    }

    private <T> MutableDataSet addOption(DataHolder options, DataKey<T> key, T value) {
        if (options == null) {
            return new MutableDataSet().set(key, value);
        } else {
            return new MutableDataSet(options).set(key, value);
        }
    }

    private void throwIgnoredOption(SpecExample example, String optionSets, String option) {
        if (example == null)
            throw new AssumptionViolatedException("Ignored: SpecExample test case options(" + optionSets + ") is using " + option + " option");
        else
            throw new AssumptionViolatedException("Ignored: example(" + example.getSection() + ": " + example.getExampleNumber() + ") options(" + optionSets + ") is using " + option + " option");
    }

    public String ast(Node node) {
        return new AstCollectingVisitor().collectAndGetAstText(node);
    }

    protected void actualSource(String html, String optionSet) {

    }

    protected void testCase(Node node, DataHolder options) {

    }

    protected void actualHtml(String html, String optionSet) {

    }

    protected void actualAst(String ast, String optionSet) {

    }

    protected void specExample(String expected, String actual, String optionSet) {

    }

    protected IParse adjustParserForInclusion(IParse parserWithOptions, Document includedDocument) {
        return parserWithOptions;
    }

    protected void assertRendering(String source, String expectedHtml) {
        assertRendering(source, expectedHtml, null);
    }

    /**
     * @return return true if actual html should be used in comparison, else only actual AST will be used in compared
     */
    protected boolean useActualHtml() {
        return true;
    }

    protected void assertRendering(String source, String expectedHtml, String optionsSet) {
        DataHolder options = optionsSet == null ? null : getOptions(example(), optionsSet);
        String parseSource = source;

        if (options != null && options.get(NO_FILE_EOL)) {
            parseSource = DumpSpecReader.trimTrailingEOL(parseSource);
        }

        IParse parserWithOptions = parser().withOptions(options);
        IRender rendererWithOptions = renderer().withOptions(options);

        Node includedDocument = null;

        String includedText = INCLUDED_DOCUMENT.getFrom(parserWithOptions.getOptions());
        if (includedText != null && !includedText.isEmpty()) {
            // need to parse and transfer references
            includedDocument = parserWithOptions.parse(includedText);

            if (includedDocument instanceof Document) {
                parserWithOptions = adjustParserForInclusion(parserWithOptions, (Document) includedDocument);
            }
        }

        long start = System.nanoTime();
        Node node = parserWithOptions.parse(parseSource);
        long parse = System.nanoTime();

        if (node instanceof Document && includedDocument instanceof Document) {
            parserWithOptions.transferReferences((Document) node, (Document) includedDocument);
        }

        String html = rendererWithOptions.render(node);
        long render = System.nanoTime();

        boolean timed = TIMED.getFrom(node.getDocument());
        boolean embedTimed = EMBED_TIMED.getFrom(node.getDocument());

        if (timed || embedTimed) {
            System.out.print(String.format(TIMED_FORMAT_STRING, (parse - start) / 1000000.0, (render - parse) / 1000000.0, (render - start) / 1000000.0));
        }

        testCase(node, options);
        actualHtml(html, optionsSet);
        boolean useActualHtml = useActualHtml();

        // include source for better assertion errors
        String expected;
        String actual;
        if (example() != null && example().getSection() != null) {
            StringBuilder outExpected = new StringBuilder();
            if (embedTimed) {
                outExpected.append(String.format(TIMED_FORMAT_STRING, (parse - start) / 1000000.0, (render - parse) / 1000000.0, (render - start) / 1000000.0));
            }

            DumpSpecReader.addSpecExample(outExpected, source, expectedHtml, "", optionsSet, true, example().getSection(), example().getExampleNumber());
            expected = outExpected.toString();

            StringBuilder outActual = new StringBuilder();
            DumpSpecReader.addSpecExample(outActual, source, useActualHtml ? html : expectedHtml, "", optionsSet, true, example().getSection(), example().getExampleNumber());
            actual = outActual.toString();
        } else {
            if (embedTimed) {
                StringBuilder outExpected = new StringBuilder();
                outExpected.append(String.format(TIMED_FORMAT_STRING, (parse - start) / 1000000.0, (render - parse) / 1000000.0, (render - start) / 1000000.0));
                outExpected.append(DumpSpecReader.addSpecExample(source, expectedHtml, "", optionsSet));
                expected = outExpected.toString();
            } else {
                expected = DumpSpecReader.addSpecExample(source, expectedHtml, "", optionsSet);
            }
            actual = DumpSpecReader.addSpecExample(source, useActualHtml ? html : expectedHtml, "", optionsSet);
        }

        specExample(expected, actual, optionsSet);
        if (options != null && options.get(FAIL)) {
            thrown.expect(ComparisonFailure.class);
        }
        assertEquals(expected, actual);
    }

    //protected void assertRenderingAst(String source, String expectedHtml, String expectedAst) {
    //    assertRenderingAst(source, expectedHtml, expectedAst, null);
    //}

    protected void assertRenderingAst(String source, String expectedHtml, String expectedAst, String optionsSet) {
        //assert options != null || optionsSet == null || optionsSet.isEmpty() : "Non empty optionsSet without any option customizations";
        DataHolder options = optionsSet == null ? null : getOptions(example(), optionsSet);
        String parseSource = source;

        if (options != null && options.get(NO_FILE_EOL)) {
            parseSource = DumpSpecReader.trimTrailingEOL(parseSource);
        }

        IParse parserWithOptions = parser().withOptions(options);
        IRender rendererWithOptions = renderer().withOptions(options);

        Node includedDocument = null;

        String includedText = INCLUDED_DOCUMENT.getFrom(parserWithOptions.getOptions());
        if (includedText != null && !includedText.isEmpty()) {
            // need to parse and transfer references
            includedDocument = parserWithOptions.parse(includedText);

            if (includedDocument instanceof Document) {
                parserWithOptions = adjustParserForInclusion(parserWithOptions, (Document) includedDocument);
            }
        }

        long start = System.nanoTime();
        Node node = parserWithOptions.parse(parseSource);
        long parse = System.nanoTime();

        if (node instanceof Document && includedDocument instanceof Document) {
            parserWithOptions.transferReferences((Document) node, (Document) includedDocument);
        }

        String html = rendererWithOptions.render(node);
        long render = System.nanoTime();

        boolean timed = TIMED.getFrom(node.getDocument());
        boolean embedTimed = EMBED_TIMED.getFrom(node.getDocument());

        if (timed || embedTimed) {
            System.out.print(String.format(TIMED_FORMAT_STRING, (parse - start) / 1000000.0, (render - parse) / 1000000.0, (render - start) / 1000000.0));
        }

        testCase(node, options);
        actualHtml(html, optionsSet);
        String ast = ast(node);
        actualAst(ast, optionsSet);
        boolean useActualHtml = useActualHtml();

        // include source for better assertion errors
        String expected;
        String actual;

        if (example() != null && example().getSection() != null) {
            StringBuilder outExpected = new StringBuilder();
            //if (TIMED.getFrom(node.getDocument())) {
            //    outExpected.append(String.format("Timing: parse %.3f ms, render %.3f ms, total %.3f\n", (parse - start)/1000000.0, (render - parse)/1000000.0, (render - start)/1000000.0));
            //}
            DumpSpecReader.addSpecExample(outExpected, source, expectedHtml, expectedAst, optionsSet, true, example().getSection(), example().getExampleNumber());
            expected = outExpected.toString();

            StringBuilder outActual = new StringBuilder();
            DumpSpecReader.addSpecExample(outActual, source, useActualHtml ? html : expectedHtml, ast, optionsSet, true, example().getSection(), example().getExampleNumber());
            actual = outActual.toString();
        } else {
            if (embedTimed) {
                StringBuilder outExpected = new StringBuilder();
                outExpected.append(String.format(TIMED_FORMAT_STRING, (parse - start) / 1000000.0, (render - parse) / 1000000.0, (render - start) / 1000000.0));
                outExpected.append(DumpSpecReader.addSpecExample(source, expectedHtml, "", optionsSet));
                expected = outExpected.toString();
            } else {
                expected = DumpSpecReader.addSpecExample(source, expectedHtml, "", optionsSet);
            }
            actual = DumpSpecReader.addSpecExample(source, useActualHtml ? html : expectedHtml, ast, optionsSet);
        }

        specExample(expected, actual, optionsSet);

        if (options != null && options.get(FAIL)) {
            thrown.expect(ComparisonFailure.class);
        }
        assertEquals(expected, actual);
    }

    //protected void assertAst(String source, String expectedAst) {
    //    assertAst(source, expectedAst, null);
    //}

    protected void assertAst(String source, String expectedAst, String optionsSet) {
        DataHolder options = optionsSet == null ? null : getOptions(example(), optionsSet);
        String parseSource = source;

        if (options != null && options.get(NO_FILE_EOL)) {
            parseSource = DumpSpecReader.trimTrailingEOL(parseSource);
        }

        Node node = parser().withOptions(options).parse(parseSource);
        String ast = ast(node);
        actualAst(ast, optionsSet);

        String expected;
        String actual;
        if (example() != null && example().getSection() != null) {
            StringBuilder outExpected = new StringBuilder();
            DumpSpecReader.addSpecExample(outExpected, source, "", expectedAst, optionsSet, true, example().getSection(), example().getExampleNumber());
            expected = outExpected.toString();

            StringBuilder outActual = new StringBuilder();
            DumpSpecReader.addSpecExample(outActual, source, "", ast, optionsSet, true, example().getSection(), example().getExampleNumber());
            actual = outActual.toString();
        } else {
            expected = DumpSpecReader.addSpecExample(source, "", expectedAst, optionsSet);
            actual = DumpSpecReader.addSpecExample(source, "", ast, optionsSet);
        }
        specExample(expected, actual, optionsSet);
        if (options != null && options.get(FAIL)) thrown.expect(ComparisonFailure.class);
        assertEquals(expected, actual);
    }
}
