package com.vladsch.flexmark.ext.anchorlink.internal;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.ext.anchorlink.AnchorLink;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.NodeTracker;
import com.vladsch.flexmark.util.options.DataHolder;

import static com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension.ANCHORLINKS_NO_BLOCK_QUOTE;

public class AnchorLinkNodePostProcessor extends NodePostProcessor {
    private final AnchorLinkOptions options;

    public AnchorLinkNodePostProcessor(DataHolder options) {
        this.options = new AnchorLinkOptions(options);
    }

    @Override
    public void process(NodeTracker state, Node node) {
        if (node instanceof Heading) {
            if (node.isOrDescendantOfType(BlockQuote.class)) {
                int tmp = 0;
            }
            Heading heading = (Heading) node;
            if (heading.getText().isNotNull()) {
                Node anchor = new AnchorLink();

                if (!options.wrapText) {
                    if (heading.getFirstChild() == null) {
                        anchor.setChars(heading.getText().subSequence(0, 0));
                        heading.appendChild(anchor);
                    } else {
                        anchor.setChars(heading.getFirstChild().getChars().subSequence(0, 0));
                        heading.getFirstChild().insertBefore(anchor);
                    }
                } else {
                    anchor.takeChildren(heading);
                    heading.appendChild(anchor);
                }

                anchor.setCharsFromContent();
                state.nodeAdded(anchor);
            }
        } else {
            int tmp = 0;
        }
    }

    public static class Factory extends NodePostProcessorFactory {
        public Factory(DataHolder options) {
            super(false);

            if (options.get(ANCHORLINKS_NO_BLOCK_QUOTE)) {
                addNodeWithExclusions(Heading.class, BlockQuote.class);
            } else {
                addNodes(Heading.class);
            }
        }

        @Override
        public NodePostProcessor create(Document document) {
            return new AnchorLinkNodePostProcessor(document);
        }
    }
}
