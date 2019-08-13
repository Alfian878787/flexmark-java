package com.vladsch.flexmark.formatter;

import com.vladsch.flexmark.formatter.internal.CoreNodeFormatter;
import com.vladsch.flexmark.formatter.internal.FormatterOptions;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.format.NodeContext;
import com.vladsch.flexmark.util.format.options.ElementPlacementSort;

import java.util.Collection;

/**
 * The context for node rendering, including configuration and functionality for the node renderer to use.
 */
public interface NodeFormatterContext extends NodeContext<Node, NodeFormatterContext>, TranslationContext {
    /**
     * @return the HTML writer to use
     */
    MarkdownWriter getMarkdown();

    /**
     * Render the specified node and its children using the configured renderers. This should be used to render child
     * nodes; be careful not to pass the node that is being rendered, that would result in an endless loop.
     *
     * @param node the node to render
     */
    void render(Node node);

    /**
     * Render the children of the node, used by custom renderers
     *
     * @param parent node the children of which are to be rendered
     */
    void renderChildren(Node parent);

    /**
     * @return current rendering phase
     */
    FormattingPhase getFormattingPhase();

    /**
     * Get the current rendering context {@link DataHolder}. These are the options passed or set on the {@link Formatter#builder()} or passed to {@link Formatter#builder(DataHolder)}.
     * To get the document options you should use {@link #getDocument()} as the data holder.
     *
     * @return the current renderer options {@link DataHolder}
     */
    DataHolder getOptions();

    /**
     * @return the {@link FormatterOptions} for the context.
     */
    FormatterOptions getFormatterOptions();

    /**
     * @return the {@link Document} node of the current context
     */
    Document getDocument();

    /**
     * Get iterable of nodes of given types, in order of their appearance in the document tree, depth first traversal.
     * Only node classes returned by {@link NodeFormatter#getNodeClasses()} of all loaded extensions
     * will be available to formatters.
     * <p>
     * {@link CoreNodeFormatter} registers {@link com.vladsch.flexmark.ast.RefNode}
     * if {@link Formatter#REFERENCE_SORT} is set to
     * {@link ElementPlacementSort#SORT_UNUSED_LAST} so that
     *
     * @param classes node classes to return
     * @return iterable
     */
    Iterable<? extends Node> nodesOfType(Class<?>[] classes);
    Iterable<? extends Node> nodesOfType(Collection<Class<?>> classes);
    /**
     * Get iterable of nodes of given types, in reverse order of their appearance in the document tree, depth first traversal.
     * Only node classes returned by {@link NodeFormatter#getNodeClasses()} of all loaded extensions
     * will be available to formatters.
     * <p>
     * {@link CoreNodeFormatter} registers {@link com.vladsch.flexmark.ast.RefNode}
     * if {@link Formatter#REFERENCE_SORT} is set to
     * {@link ElementPlacementSort#SORT_UNUSED_LAST} so that
     *
     * @param classes node classes to return
     * @return iterable
     */
    Iterable<? extends Node> reversedNodesOfType(Class<?>[] classes);
    Iterable<? extends Node> reversedNodesOfType(Collection<Class<?>> classes);
}
