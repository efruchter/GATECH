package project.phase2.ll1parsergenerator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a Abstract Syntax Tree.
 * 
 */
public class AST<V> {
	public ASTNode<V> root;

	/**
	 * Constructs an empty AST
	 */
	public AST() {
		root = null;
	}

	/**
	 * Constructs a AST with one node
	 * 
	 * @param key
	 *            root key
	 * @param value
	 *            root value
	 */
	public AST(V value, boolean terminal) {
		root = new ASTNode<V>(value, terminal);
	}

	/**
	 * Print a abstract syntax tree. The format will be parent - children. This
	 * method will print out the abstract syntax tree in level order.
	 */
	public void printAST() {
		Queue<ASTNode<V>> q = new LinkedList<ASTNode<V>>();
		System.out.println("Root: " + this.root.toString());
		ASTNode<V> node;
		node = this.root;
		q.add(node);
		while (q.peek() != null) {
			if (!q.peek().equals(this.root) && !q.peek().isTerminal) {
				System.out.println("Parent: " + q.peek().toString());
			} else if (q.peek().isTerminal) {
				System.out.println("Terminal: " + q.peek().toString());
			}
			for (int i = 0; i < q.peek().children.size(); i++) {
				q.add(q.peek().children.get(i));
			}
			if (!q.peek().isTerminal)
				System.out.println(q.peek().toStringChildren());
			q.poll();
		}
	}

	/**
	 * Print a abstract syntax tree. The format will be parent - children. This
	 * method will print out the abstract syntax tree in level order.
	 */
	public String toString() {
		String result = "";
		Queue<ASTNode<V>> q = new LinkedList<ASTNode<V>>();
		result += "Root: " + this.root.toString() + "\n";
		ASTNode<V> node;
		node = this.root;
		q.add(node);
		while (q.peek() != null) {
			if (!q.peek().equals(this.root) && !q.peek().isTerminal) {
				result += "Parent: " + q.peek().toString() + "\n";
			} else if (q.peek().isTerminal) {
				result += "Terminal: " + q.peek().toString() + "\n";
			}
			for (int i = 0; i < q.peek().children.size(); i++) {
				q.add(q.peek().children.get(i));
			}
			if (!q.peek().isTerminal)
				result += q.peek().toStringChildren() + "\n";
			q.poll();
		}

		return result;
	}
}