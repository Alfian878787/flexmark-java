package com.vladsch.flexmark.ext.tables.internal;

import com.vladsch.flexmark.ast.Block;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.NodeIterator;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.util.Parsing;
import com.vladsch.flexmark.ext.tables.*;
import com.vladsch.flexmark.internal.ReferencePreProcessorFactory;
import com.vladsch.flexmark.parser.InlineParser;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.CharacterNodeFactory;
import com.vladsch.flexmark.parser.block.ParagraphPreProcessor;
import com.vladsch.flexmark.parser.block.ParagraphPreProcessorFactory;
import com.vladsch.flexmark.parser.block.ParserState;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.*;
import java.util.regex.Pattern;

public class TableParagraphPreProcessor implements ParagraphPreProcessor {
    private static BitSet pipeCharacters = new BitSet(1);
    private static BitSet separatorCharacters = new BitSet(3);
    static {
        pipeCharacters.set('|');
        separatorCharacters.set('|');
        separatorCharacters.set(':');
        separatorCharacters.set('-');
    }

    private static HashMap<Character, CharacterNodeFactory> pipeNodeMap = new HashMap<Character, CharacterNodeFactory>();
    static {
        pipeNodeMap.put('|', new CharacterNodeFactory() {
            @Override
            public boolean skipNext(char c) {
                return c == ' ' || c == '\t';
                //return false;
            }

            @Override
            public boolean skipPrev(char c) {
                return c == ' ' || c == '\t';
                //return false;
            }

            @Override
            public boolean wantSkippedWhitespace() {
                return true;
            }

            @Override
            public Node create() {
                return new TableColumnSeparator();
            }
        });
    }
    public static ParagraphPreProcessorFactory Factory() {
        return new ParagraphPreProcessorFactory() {
            @Override
            public boolean affectsGlobalScope() {
                return false;
            }

            @Override
            public Set<Class<? extends ParagraphPreProcessorFactory>> getAfterDependents() {
                HashSet<Class<? extends ParagraphPreProcessorFactory>> set = new HashSet<Class<? extends ParagraphPreProcessorFactory>>();
                set.add(ReferencePreProcessorFactory.class);
                return set;
            }

            @Override
            public Set<Class<? extends ParagraphPreProcessorFactory>> getBeforeDependents() {
                return null;
            }

            @Override
            public ParagraphPreProcessor create(ParserState state) {
                return new TableParagraphPreProcessor(state.getProperties());
            }
        };
    }

    private final TableParserOptions options;
    private final boolean isIntellijDummyIdentifier;
    private final String intellijDummyIdentifier;
    Pattern TABLE_HEADER_SEPARATOR;

    public static Pattern getTableHeaderSeparator(int minColumnDashes, String intellijDummyIdentifier) {
        int minCol = minColumnDashes >= 1 ? minColumnDashes : 1;
        int minColDash = minColumnDashes >= 2 ? minColumnDashes - 1 : 1;
        int minColDashes = minColumnDashes >= 3 ? minColumnDashes - 2 : 1;
        String COL = String.format("(?:" + "\\s*-{%d,}\\s*|\\s*:-{%d,}\\s*|\\s*-{%d,}:\\s*|\\s*:-{%d,}:\\s*" + ")", minCol, minColDash, minColDash, minColDashes);

        if (!intellijDummyIdentifier.isEmpty()) {
            String add = Parsing.INTELLIJ_DUMMY_IDENTIFIER;
            String sp = "(?:\\s" + add + "?)";
            String ds = "(?:-" + add + "?)";
            COL = COL.replace("\\s", sp).replace("-", ds);
        }

        return Pattern.compile(
                "\\|" + COL + "\\|?\\s*" + "|" +
                        COL + "\\|\\s*" + "|" +
                        "\\|?" + "(?:" + COL + "\\|)+" + COL + "\\|?\\s*");
    }

    private TableParagraphPreProcessor(DataHolder options) {
        this.options = new TableParserOptions(options);
        isIntellijDummyIdentifier = Parser.INTELLIJ_DUMMY_IDENTIFIER.getFrom(options);
        intellijDummyIdentifier = isIntellijDummyIdentifier ? Parsing.INTELLIJ_DUMMY_IDENTIFIER : "";
        this.TABLE_HEADER_SEPARATOR = getTableHeaderSeparator(this.options.minSeparatorDashes, intellijDummyIdentifier);
    }

    @Override
    public int preProcessBlock(Paragraph block, ParserState state) {
        InlineParser inlineParser = state.getInlineParser();

        ArrayList<BasedSequence> tableLines = new ArrayList<BasedSequence>();
        int separatorLineNumber = -1;
        BasedSequence separatorLine = null;
        int blockIndent = block.getLineIndent(0);
        BasedSequence captionLine = null;

        int i = 0;
        for (BasedSequence rowLine : block.getContentLines()) {
            int rowNumber = tableLines.size();
            if (separatorLineNumber == -1 && rowNumber > options.maxHeaderRows) return 0;    // too many header rows

            if (rowLine.indexOf('|') < 0) {
                if (separatorLineNumber == -1) return 0;

                if (options.withCaption) {
                    BasedSequence trimmed = rowLine.trim();
                    if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                        captionLine = trimmed;
                    }
                }
                break;
            }

            BasedSequence fullRowLine = block.getLineIndent(rowNumber) <= blockIndent ? rowLine.trimEOL() : rowLine.baseSubSequence(rowLine.getStartOffset() - (block.getLineIndent(rowNumber) - blockIndent), rowLine.getEndOffset() - rowLine.eolLength());
            if (separatorLineNumber == -1) {
                if (rowNumber >= options.minHeaderRows
                        && TABLE_HEADER_SEPARATOR.matcher(rowLine).matches()) {
                    // must start with | or cell, whitespace means its not a separator line
                    if (fullRowLine.charAt(0) != ' ' && fullRowLine.charAt(0) != '\t' || rowLine.charAt(0) != '|') {
                        separatorLineNumber = rowNumber;
                        separatorLine = rowLine;
                    } else if (fullRowLine.charAt(0) == ' ' || fullRowLine.charAt(0) == '\t') {
                        block.setHasTableSeparator(true);
                    }
                }
            } else if (options.multiLineRows) {
                // TODO: need to do inline parsing here to determine whether we have open element constructs which need to include the next line
            }

            tableLines.add(rowLine);
            i++;
        }

        if (separatorLineNumber == -1) return 0;

        ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
        for (BasedSequence rowLine : tableLines) {
            int rowNumber = tableRows.size();

            BasedSequence fullRowLine = block.getLineIndent(rowNumber) <= blockIndent ? rowLine.trimEOL() : rowLine.baseSubSequence(rowLine.getStartOffset() - (block.getLineIndent(rowNumber) - blockIndent), rowLine.getEndOffset() - rowLine.eolLength());
            TableRow tableRow = new TableRow(fullRowLine);
            int tableRowNumber;

            List<Node> sepList;
            if (rowNumber == separatorLineNumber) {
                sepList = inlineParser.parseCustom(fullRowLine, tableRow, separatorCharacters, pipeNodeMap);
                tableRowNumber = 0;
            } else {
                sepList = inlineParser.parseCustom(fullRowLine, tableRow, pipeCharacters, pipeNodeMap);
                if (rowNumber < separatorLineNumber) tableRowNumber = rowNumber + 1;
                else tableRowNumber = rowNumber - separatorLineNumber;
            }

            if (sepList == null) {
                if (rowNumber <= separatorLineNumber) return 0;
                break;
            }

            tableRow.setRowNumber(tableRowNumber);
            tableRows.add(tableRow);
        }

        // table is done, could be earlier than the lines tested earlier, may need to truncate lines
        Block tableBlock = new TableBlock(tableLines.subList(0, tableRows.size()));
        Node section = new TableHead(tableLines.get(0).subSequence(0, 0));
        tableBlock.appendChild(section);

        List<TableCell.Alignment> alignments = parseAlignment(separatorLine);

        int rowNumber = 0;
        int separatorColumns = alignments.size();
        for (TableRow tableRow : tableRows) {
            if (rowNumber == separatorLineNumber) {
                section.setCharsFromContent();
                section = new TableSeparator();
                tableBlock.appendChild(section);
            } else if (rowNumber == separatorLineNumber + 1) {
                section.setCharsFromContent();
                section = new TableBody();
                tableBlock.appendChild(section);
            }

            boolean firstCell = true;
            int cellCount = 0;
            NodeIterator nodes = new NodeIterator(tableRow.getFirstChild());
            TableRow newTableRow = new TableRow(tableRow.getChars());
            newTableRow.setRowNumber(tableRow.getRowNumber());
            int accumulatedSpanOffset = 0;

            while (nodes.hasNext()) {
                if (cellCount >= separatorColumns && options.discardExtraColumns) {
                    if (options.headerSeparatorColumnMatch && rowNumber < separatorLineNumber) {
                        // header/separator mismatch
                        return 0;
                    }

                    break;
                }

                TableCell tableCell = new TableCell();

                if (firstCell && nodes.peek() instanceof TableColumnSeparator) {
                    Node columnSep = nodes.next();
                    tableCell.setOpeningMarker(columnSep.getChars());
                    columnSep.unlink();
                    firstCell = false;
                }

                TableCell.Alignment alignment = cellCount + accumulatedSpanOffset < separatorColumns ? alignments.get(cellCount + accumulatedSpanOffset) : null;
                tableCell.setHeader(rowNumber < separatorLineNumber);
                tableCell.setAlignment(alignment);

                // take all until separator or end of iterator
                while (nodes.hasNext()) {
                    if (nodes.peek() instanceof TableColumnSeparator) break;
                    tableCell.appendChild(nodes.next());
                }

                // accumulate closers, and optional spans
                BasedSequence closingMarker = null;
                int span = 1;
                while (nodes.hasNext()) {
                    if (!(nodes.peek() instanceof TableColumnSeparator)) break;
                    if (closingMarker == null) {
                        closingMarker = nodes.next().getChars();
                        if (!options.columnSpans) break;
                    } else {
                        BasedSequence nextSep = nodes.peek().getChars();

                        if (!closingMarker.isContinuedBy(nextSep)) break;
                        closingMarker = closingMarker.spliceAtEnd(nextSep);
                        nodes.next().unlink();
                        span++;
                    }
                }

                accumulatedSpanOffset += span - 1;

                if (closingMarker != null) tableCell.setClosingMarker(closingMarker);
                tableCell.setChars(tableCell.getChildChars());
                // option to keep cell whitespace, if yes, then convert it to text and merge adjacent text nodes
                if (options.trimCellWhitespace) tableCell.trimWhiteSpace();
                else tableCell.mergeWhiteSpace();

                tableCell.setText(tableCell.getChildChars());
                tableCell.setCharsFromContent();
                tableCell.setSpan(span);
                newTableRow.appendChild(tableCell);
                cellCount++;
            }

            if (options.headerSeparatorColumnMatch && rowNumber < separatorLineNumber && cellCount < separatorColumns) {
                // no match
                return 0;
            }

            while (options.appendMissingColumns && cellCount < separatorColumns) {
                TableCell tableCell = new TableCell();
                tableCell.setHeader(rowNumber < separatorLineNumber);
                tableCell.setAlignment(alignments.get(cellCount));
                newTableRow.appendChild(tableCell);
                cellCount++;
            }

            newTableRow.setCharsFromContent();
            section.appendChild(newTableRow);

            rowNumber++;
        }

        section.setCharsFromContent();

        if (section instanceof TableSeparator) {
            TableBody tableBody = new TableBody(section.getChars().subSequence(section.getChars().length()));
            tableBlock.appendChild(tableBody);
        }

        // Add caption if the option is enabled
        if (captionLine != null) {
            TableCaption caption = new TableCaption(captionLine.subSequence(0, 1), captionLine.subSequence(1, captionLine.length() - 1), captionLine.subSequence(captionLine.length() - 1));
            inlineParser.parse(caption.getText(), caption);
            caption.setCharsFromContent();
            tableBlock.appendChild(caption);
        }

        tableBlock.setCharsFromContent();

        block.insertBefore(tableBlock);
        state.blockAdded(tableBlock);
        return tableBlock.getChars().length();
    }

    private List<TableCell.Alignment> parseAlignment(BasedSequence separatorLine) {
        List<BasedSequence> parts = split(separatorLine, false, false);
        List<TableCell.Alignment> alignments = new ArrayList<TableCell.Alignment>();
        for (BasedSequence part : parts) {
            BasedSequence cleaned = isIntellijDummyIdentifier ? part.replace(intellijDummyIdentifier, "") : part;
            BasedSequence trimmed = cleaned.trim();
            boolean left = trimmed.startsWith(":");
            boolean right = trimmed.endsWith(":");
            TableCell.Alignment alignment = getAlignment(left, right);
            alignments.add(alignment);
        }
        return alignments;
    }

    private static List<BasedSequence> split(BasedSequence input, boolean columnSpans, boolean wantPipes) {
        BasedSequence line = input.trim();
        int lineLength = line.length();
        List<BasedSequence> segments = new ArrayList<BasedSequence>();

        if (line.startsWith("|")) {
            if (wantPipes) segments.add(line.subSequence(0, 1));
            line = line.subSequence(1, lineLength);
            lineLength--;
        }

        boolean escape = false;
        int lastPos = 0;
        int cellChars = 0;
        for (int i = 0; i < lineLength; i++) {
            char c = line.charAt(i);
            if (escape) {
                escape = false;
                cellChars++;
            } else {
                switch (c) {
                    case '\\':
                        escape = true;
                        // Removing the escaping '\' is handled by the inline parser later, so add it to cell
                        cellChars++;
                        break;
                    case '|':
                        if (!columnSpans || lastPos < i) segments.add(line.subSequence(lastPos, i));
                        if (wantPipes) segments.add(line.subSequence(i, i + 1));
                        lastPos = i + 1;
                        cellChars = 0;
                        break;
                    default:
                        cellChars++;
                }
            }
        }

        if (cellChars > 0) {
            segments.add(line.subSequence(lastPos, lineLength));
        }
        return segments;
    }

    private static TableCell.Alignment getAlignment(boolean left, boolean right) {
        if (left && right) {
            return TableCell.Alignment.CENTER;
        } else if (left) {
            return TableCell.Alignment.LEFT;
        } else if (right) {
            return TableCell.Alignment.RIGHT;
        } else {
            return null;
        }
    }
}
