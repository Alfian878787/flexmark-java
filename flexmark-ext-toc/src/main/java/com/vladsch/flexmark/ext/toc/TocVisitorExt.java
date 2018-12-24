package com.vladsch.flexmark.ext.toc;

import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

public class TocVisitorExt {
    public static <V extends TocVisitor> VisitHandler<?>[] VISIT_HANDLERS(final V visitor) {
        return new VisitHandler<?>[] {
                new VisitHandler<TocBlock>(TocBlock.class, new Visitor<TocBlock>() {
                    @Override
                    public void visit(TocBlock node) {
                        visitor.visit(node);
                    }
                }),
        };
    }
}
