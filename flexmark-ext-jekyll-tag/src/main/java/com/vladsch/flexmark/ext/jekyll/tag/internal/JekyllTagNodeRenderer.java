package com.vladsch.flexmark.ext.jekyll.tag.internal;

import com.vladsch.flexmark.ext.jekyll.tag.JekyllTag;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagBlock;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JekyllTagNodeRenderer implements NodeRenderer {
    private final boolean enabledRendering;
    private final Map<String, String> includeContent;

    public JekyllTagNodeRenderer(DataHolder options) {
        enabledRendering = JekyllTagExtension.ENABLE_RENDERING.getFrom(options);
        includeContent = JekyllTagExtension.INCLUDED_HTML.getFrom(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        // @formatter:off
        set.add(new NodeRenderingHandler<JekyllTag>(JekyllTag.class, new CustomNodeRenderer<JekyllTag>() { @Override public void render(JekyllTag node, NodeRendererContext context, HtmlWriter html) { JekyllTagNodeRenderer.this.render(node, context, html); } }));
        set.add(new NodeRenderingHandler<JekyllTagBlock>(JekyllTagBlock.class, new CustomNodeRenderer<JekyllTagBlock>() { @Override public void render(JekyllTagBlock node, NodeRendererContext context, HtmlWriter html) { JekyllTagNodeRenderer.this.render(node, context, html); } }));
        // @formatter:on
        return set;
    }

    private void render(JekyllTag node, NodeRendererContext context, HtmlWriter html) {
        if (enabledRendering) html.text(node.getChars());
        else if (node.getTag().equals("include") && includeContent != null && !node.getParameters().isEmpty()) {
            String content = includeContent.get(node.getParameters());
            if (content != null && !content.isEmpty()) {
                html.rawPre(content);
            }
        }
    }

    private void render(JekyllTagBlock node, NodeRendererContext context, HtmlWriter html) {
        context.renderChildren(node);
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new JekyllTagNodeRenderer(options);
        }
    }
}
