package com.vladsch.flexmark.ext.xwiki.macros;

import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

public class MacroVisitorExt {
    public static <V extends MacroVisitor> VisitHandler<?>[] VISIT_HANDLERS(final V visitor) {
        return new VisitHandler<?>[] {
// @formatter:off
                new VisitHandler<Macro>(Macro.class, new Visitor<Macro>() { @Override public void visit(Macro node) { visitor.visit(node); } }),
                new VisitHandler<MacroClose>(MacroClose.class, new Visitor<MacroClose>() { @Override public void visit(MacroClose node) { visitor.visit(node); } }),
                new VisitHandler<MacroBlock>(MacroBlock.class, new Visitor<MacroBlock>() { @Override public void visit(MacroBlock node) { visitor.visit(node); } }),
 // @formatter:on
        };
    }
}
