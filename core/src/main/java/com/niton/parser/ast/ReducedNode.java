package com.niton.parser.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * A Tree node, each node has a name and a parent. A node can only be a leaf of the tree or a node
 * with subnodes a mix is not possible. use {@link #leaf(String, String)} and
 * {@link #node(String, List)} for instantiation. Parents are automatically assigned
 */
@Data
public class ReducedNode {
	private final          List<ReducedNode> children;
	private final          String            value;
	private final          boolean           isLeaf;
	private final @NotNull String            name;
	private                ReducedNode       parent;

	private ReducedNode(
			List<ReducedNode> children,
			String value,
			boolean isLeaf,
			String name
	) {
		if (children != null) {
			children.forEach(n -> n.setParent(this));
		}
		this.children = children;
		this.value    = value;
		this.isLeaf   = isLeaf;
		this.name     = name;
	}

	private void setParent(ReducedNode parent) {
		this.parent = parent;
	}

	/**
	 * Creates a leaf node, a leave has no child nodes but a value instead
	 *
	 * @param name  the name of the resulting node
	 * @param value the value of the node
	 *
	 * @return the leaf node
	 */
	public static ReducedNode leaf(String name, String value) {
		return new ReducedNode(null, value, true, name);
	}

	/**
	 * Creates a node that consists of sub-nodes
	 *
	 * @param name     the name of the resulting node
	 * @param children the sub-nodes of this node 0..n
	 *
	 * @return a node with children
	 */
	public static ReducedNode node(String name, List<ReducedNode> children) {
		return new ReducedNode(children, null, false, name);
	}

	/**
	 * Get a sub node attached to this node. When the node is a leaf an exception is thrown
	 *
	 * @param name the name of the node to get
	 *
	 * @return the node when a node with the regarding name exists, empty otherwise
	 *
	 * @throws UnsupportedOperationException when using on a leaf
	 */
	public Optional<ReducedNode> getSubNode(String name) {
		verifyNode();
		return children.stream().filter(n -> n.name.equals(name)).findFirst();
	}

	private void verifyNode() {
		if (isLeaf()) {
			throw new UnsupportedOperationException("Only nodes can have children");
		}
	}

	/**
	 * Returns the value of this leaf, if this is not a leaf an exception is thrown
	 *
	 * @return the value stored in this node
	 *
	 * @throws UnsupportedOperationException when not a leaf
	 */
	public @Nullable String getValue() {
		verifyLeaf();
		return value;
	}

	private void verifyLeaf() {
		if (!isLeaf()) {
			throw new UnsupportedOperationException("Only leafs can have values");
		}
	}

	/**
	 * Get all sub nodes attached to this node. When the node is a leaf an exception is thrown
	 *
	 * @return the list of sub-nodes
	 *
	 * @throws UnsupportedOperationException when using on a leaf
	 */
	public List<ReducedNode> getChildren() {
		verifyNode();
		return children;
	}

	/**
	 * Get all sub node names. When the node is a leaf an exception is thrown
	 *
	 * @return the names of this nodes sub-nodes
	 *
	 * @throws UnsupportedOperationException when using on a leaf
	 */
	public List<String> getChildNames() {
		verifyNode();
		return children.stream().map(n -> n.name).collect(toList());
	}

	/**
	 * Formats the tree with ascii symbols (console compatible)
	 * @return a multiline representation of the whole node-tree
	 */
	public String format() {
		if (isLeaf()) {
			return String.format("%s: %s", name, value);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("\n");
		formatChildren(sb);
		return sb.toString();
	}

	private void formatChildren(StringBuilder sb) {
		for (int i = 0; i < children.size(); i++) {
			var child    = children.get(i);
			var childStr = child.format();
			formatChild(sb, i, childStr);
		}
	}

	private void formatChild(StringBuilder sb, int childIndex, String childBlock) {
		String[] lines = childBlock.split("\n");
		for (int j = 0; j < lines.length; j++) {
			if (j == 0) {
				if (childIndex + 1 < children.size()) {
					sb.append("|-");
				} else {
					sb.append("`-");
				}
			} else if (childIndex + 1 < children.size()) {
				sb.append("| ");
			} else {
				sb.append("  ");
			}

			sb.append(lines[j]).append("\n");
		}
	}

	/**
	 * Formats the tree as simple HTML string. Doesn't contain Css just a list of nested {@code ul}s
	 */
	public String formatHtml() {
		StringBuilder sb = new StringBuilder();
		if (isLeaf()) {
			sb.append("<li>")
			  .append(name)
			  .append(": ")
			  .append(value)
			  .append("</li>");
		} else {
			sb.append("<li>\n")
			  .append("\t<span class=\"node\">")
			  .append(name)
			  .append("</span>\n")
			  .append("\t<ul class=\"sub-nodes\">\n");
			for (var child : children) {
				sb.append("\t\t")
				  .append(child.formatHtml().replace("\n", "\n\t\t"))
				  .append("\n");
			}
			sb.append("\t</ul>\n")
			  .append("</li>");
		}
		return sb.toString();
	}

	public String toString(){
		return String.format("ReducedNode{%s: %s}", name, isLeaf ? value : getChildNames());
	}
	@Override
	public boolean equals(Object node){
		if(!(node instanceof ReducedNode)){
			return false;
		}
		return name.equals(((ReducedNode) node).name) &&
				isLeaf == ((ReducedNode) node).isLeaf &&
				isLeaf ? Objects.equals(value,((ReducedNode) node).value) :
				children.equals(((ReducedNode) node).children);
	}

	@Override
	public int hashCode() {
		return 17*(isLeaf?5:13)*name.hashCode()* Objects.hashCode(children)*Objects.hashCode(value);
	}
}
