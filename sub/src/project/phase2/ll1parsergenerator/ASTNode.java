package project.phase2.ll1parsergenerator;

import java.util.ArrayList;

/**
 * AST Node
 * 
 */
public class ASTNode<V> {
	public V value;
	public ArrayList<ASTNode<V>> children;
	public boolean isTerminal;

	/**
	 * Constructor of AST with value
	 * 
	 * @param value
	 */
	public ASTNode(V value) {
		this.children = new ArrayList<ASTNode<V>>();
		this.value = value;
	}

	/**
	 * Constructor of AST with value and terminal
	 * 
	 * @param value
	 * @param terminal
	 */
	public ASTNode(V value, boolean terminal) {
		this(value);
		this.isTerminal = terminal;
	}
	
	public ASTNode<V> get(int i) {
		return this.children.get(i);
	}

	/**
	 * Insert a node in children arraylist.
	 * 
	 * @param child
	 */
	public void insert(ASTNode<V> child) {
		if (child != null) {
			this.children.add(child);
		}
	}

	/**
	 * Print the value of the node
	 */
	public String toString() {
		String valStr = "";
		if (value != null) {
			valStr = value.toString();
		}
		return  valStr;
	}

	/**
	 * Print all children attached to this node
	 * 
	 * @return children
	 */
	public String toStringChildren() {
		String cStr = "";
		if (children != null) {
			cStr = children.toString();
		}
		return "Children: " + cStr;
	}

}