package com.vladsch.flexmark.ext.tables;

import com.vladsch.flexmark.util.ast.CustomNode;
import com.vladsch.flexmark.util.sequence.BasedSequence;

/**
 * Head part of a {@link TableBlock} containing {@link TableRow TableRows}.
 */
public class TableHead extends CustomNode {
    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }

    public TableHead() {
    }

    public TableHead(BasedSequence chars) {
        super(chars);
    }
}
