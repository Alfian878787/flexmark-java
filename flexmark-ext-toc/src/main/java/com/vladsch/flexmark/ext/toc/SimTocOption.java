package com.vladsch.flexmark.ext.toc;

import com.vladsch.flexmark.util.ast.CustomNode;
import com.vladsch.flexmark.util.ast.DoNotDecorate;
import com.vladsch.flexmark.util.sequence.BasedSequence;

/**
 * A sim toc contents node containing all text that came after the sim toc node
 */
public class SimTocOption extends CustomNode implements DoNotDecorate {
    @Override
    public BasedSequence[] getSegments() {
        //return EMPTY_SEGMENTS;
        return EMPTY_SEGMENTS;
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        astExtraChars(out);
    }

    public SimTocOption() {
    }

    public SimTocOption(BasedSequence chars) {
        super(chars);
    }
}
