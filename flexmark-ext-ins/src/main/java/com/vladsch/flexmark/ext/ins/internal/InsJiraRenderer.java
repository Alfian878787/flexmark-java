package com.vladsch.flexmark.ext.ins.internal;

import com.vladsch.flexmark.ext.ins.Ins;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;

import java.util.HashSet;
import java.util.Set;

public class InsJiraRenderer implements NodeRenderer {
    public InsJiraRenderer(DataHolder options) {

    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<Ins>(Ins.class, new CustomNodeRenderer<Ins>() {
            @Override
            public void render(Ins node, NodeRendererContext context, HtmlWriter html) { InsJiraRenderer.this.render(node, context, html); }
        }));
        return set;
    }

    private void render(Ins node, NodeRendererContext context, HtmlWriter html) {
        html.raw("+");
        context.renderChildren(node);
        html.raw("+");
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new InsJiraRenderer(options);
        }
    }
}
