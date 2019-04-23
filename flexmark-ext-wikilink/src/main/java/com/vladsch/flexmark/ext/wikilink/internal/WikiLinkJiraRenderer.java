package com.vladsch.flexmark.ext.wikilink.internal;

import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.*;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.HashSet;
import java.util.Set;

public class WikiLinkJiraRenderer implements NodeRenderer {
    private final WikiLinkOptions options;

    public WikiLinkJiraRenderer(DataHolder options) {
        this.options = new WikiLinkOptions(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<WikiLink>(WikiLink.class, new CustomNodeRenderer<WikiLink>() {
            @Override
            public void render(WikiLink node, NodeRendererContext context, HtmlWriter html) {
                WikiLinkJiraRenderer.this.render(node, context, html);
            }
        }));
        return set;
    }

    private void render(WikiLink node, NodeRendererContext context, HtmlWriter html) {
        if (options.disableRendering) {
            html.text(node.getChars().unescape());
        } else {
            ResolvedLink resolvedLink = context.resolveLink(WikiLinkExtension.WIKI_LINK, node.getLink().toString(), null);
            html.raw("[");
            html.raw(node.getText().isNotNull() ? node.getText().toString() : node.getPageRef().toString());
            html.raw("|");
            html.raw(resolvedLink.getUrl());
            html.raw("]");
        }
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer apply(final DataHolder options) {
            return new WikiLinkJiraRenderer(options);
        }
    }
}
