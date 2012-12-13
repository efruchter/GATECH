package project.phase2.ll1parsergenerator;

import java.util.ArrayList;

/**
 * AST Node
 * 
 */
public class ASTNode<V> {
	private V value;
	private ArrayList<ASTNode<V>> children;
	private boolean isTerminal;

	/**
	 * Constructor of AST with value
	 * 
	 * @param value
	 */
	public ASTNode(V value) {
		this.setChildren(new ArrayList<ASTNode<V>>());
		this.setValue(value);
	}

	/**
	 * Constructor of AST with value and terminal
	 * 
	 * @param value
	 * @param terminal
	 */
	public ASTNode(V value, boolean terminal) {
		this(value);
		this.setTerminal(terminal);
	}
	
	public ASTNode<V> get(int i) {
		return this.getChildren().get(i);
	}

	/**
	 * Insert a node in children arraylist.
	 * 
	 * @param child
	 */
	public void insert(ASTNode<V> child) {
		if (child != null) {
			this.getChildren().add(child);
		}
	}

	/**
	 * Print the value of the node
	 */
	public String toString() {
		String valStr = "";
		if (getValue() != null) {
			valStr = getValue().toString();
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
		if (getChildren() != null) {
			cStr = getChildren().toString();
		}
		return "Children: " + cStr;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public ArrayList<ASTNode<V>> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ASTNode<V>> children) {
		this.children = children;
	}

	public boolean isTerminal() {
		return isTerminal;
	}

	public void setTerminal(boolean isTerminal) {
		this.isTerminal = isTerminal;
	}

}