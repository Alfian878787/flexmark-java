
package com.vladsch.flexmark.ext.definition;

import com.vladsch.flexmark.ast.CustomBlock;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.ParagraphItemContainer;
import com.vladsch.flexmark.parser.ListOptions;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;

/**
 * A Definition block node
 */
public class DefinitionTerm extends CustomBlock implements ParagraphItemContainer {

    @Override
    public void getAstExtra(StringBuilder out) {
    }

    @Override
    public BasedSequence[] getSegments() {
        return Node.EMPTY_SEGMENTS;
    }

    public DefinitionTerm() {
    }

    public DefinitionTerm(BasedSequence chars) {
        super(chars);
    }

    public DefinitionTerm(Node node) {
        super();
        appendChild(node);
        this.setCharsFromContent();
    }

    @Override
    public boolean isParagraphWrappingDisabled(Paragraph node, ListOptions listOptions, DataHolder options) {
        return true;
    }
}
