package com.vladsch.flexmark.ext.zzzzzz.internal;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.TextBase;
import com.vladsch.flexmark.parser.block.DocumentPostProcessor;
import com.vladsch.flexmark.parser.block.DocumentPostProcessorFactory;
import com.vladsch.flexmark.util.ast.*;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class ZzzzzzDocumentPostProcessor extends DocumentPostProcessor {
    private final NodeVisitor myVisitor;

    public ZzzzzzDocumentPostProcessor(Document document) {
        myVisitor = new NodeVisitor(
                new VisitHandler<Text>(Text.class, this::visit)
        );
    }

    @Override
    public Document processDocument(Document document) {
        myVisitor.visit(document);
        return document;
    }

    private void visit(Text node) {
        if (!node.isOrDescendantOfType(DoNotDecorate.class)) {
            // do some processing

            BasedSequence original = node.getChars();
            boolean wrapInTextBase = !(node.getParent() instanceof TextBase);
            TextBase textBase = null;

            while (wrapInTextBase) {
                if (wrapInTextBase) {
                    wrapInTextBase = false;
                    textBase = new TextBase(original);
                    node.insertBefore(textBase);
                    textBase.appendChild(node);
                }
            }
        }
    }

    public static class Factory extends DocumentPostProcessorFactory {
        @Override
        public DocumentPostProcessor apply(Document document) {
            return new ZzzzzzDocumentPostProcessor(document);
        }
    }
}
