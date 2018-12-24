package com.vladsch.flexmark.ext.wikilink;

import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

public class WikiLinkVisitorExt {
    public static <V extends WikiLinkVisitor> VisitHandler<?>[] VISIT_HANDLERS(final V visitor) {
        return new VisitHandler<?>[] {
                new VisitHandler<WikiLink>(WikiLink.class, new Visitor<WikiLink>() {
                    @Override
                    public void visit(WikiLink node) {
                        visitor.visit(node);
                    }
                }),
        };
    }
}
