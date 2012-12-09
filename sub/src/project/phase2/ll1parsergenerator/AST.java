package project.phase2.ll1parsergenerator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents a Abstract Syntax Tree.
 * 
 */
public class AST<V> {
	ASTNode<V> root;

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
	 * Set root. For testing purposes only
	 * 
	 * @param root
	 *            new root
	 */
	public void setRoot(ASTNode<V> root) {
		this.root = root;
	}

	/**
	 * Returns the root. For testing purposes only
	 * 
	 * @return the root
	 */
	public ASTNode<V> getRoot() {
		return root;
	}

	/**
	 * Print a abstract syntax tree. The format will be parent - children. This
	 * method will print out the abstract syntax tree in level order.
	 */
	private void printAST() {
		Queue<ASTNode<V>> q = new LinkedList<ASTNode<V>>();
		System.out.println("Root: " + this.getRoot().toString());
		ASTNode<V> node;
		node = this.getRoot();
		q.add(node);
		while (q.peek() != null) {
			if (!q.peek().equals(this.root) && !q.peek().isTerminal()) {
				System.out.println("Parent: " + q.peek().toString());
			} else if (q.peek().isTerminal()) {
				System.out.println("Terminal: " + q.peek().toString());
			}
			for (int i = 0; i < q.peek().getChildren().size(); i++) {
				q.add(q.peek().getChildren().get(i));
			}
			if (!q.peek().isTerminal())
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
		result += "Root: " + this.getRoot().toString() + "\n";
		ASTNode<V> node;
		node = this.getRoot();
		q.add(node);
		while (q.peek() != null) {
			if (!q.peek().equals(this.root) && !q.peek().isTerminal()) {
				result += "Parent: " + q.peek().toString() + "\n";
			} else if (q.peek().isTerminal()) {
				result += "Terminal: " + q.peek().toString() + "\n";
			}
			for (int i = 0; i < q.peek().getChildren().size(); i++) {
				q.add(q.peek().getChildren().get(i));
			}
			if (!q.peek().isTerminal())
				result += q.peek().toStringChildren() + "\n";
			q.poll();
		}

		return result;
	}

	/**
	 * test AST
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		AST<String> ast = new AST<String>();
		ASTNode<String> root = new ASTNode<String>("Rule", false);
		ast.setRoot(root);
		ASTNode<String> a = new ASTNode<String>("a", false);
		ASTNode<String> plus = new ASTNode<String>("+", true);
		ASTNode<String> b = new ASTNode<String>("b", false);
		ASTNode<String> c = new ASTNode<String>("c", true);
		ASTNode<String> minus = new ASTNode<String>("-", true);
		ASTNode<String> d = new ASTNode<String>("d", true);
		ASTNode<String> e = new ASTNode<String>("e", false);
		ASTNode<String> mul = new ASTNode<String>("*", true);
		ASTNode<String> f = new ASTNode<String>("f", true);
		ASTNode<String> g = new ASTNode<String>("g", true);
		ASTNode<String> div = new ASTNode<String>("/", true);
		ASTNode<String> h = new ASTNode<String>("h", true);
		root.insert(a);
		root.insert(plus);
		root.insert(b);
		a.insert(c);
		a.insert(minus);
		a.insert(d);
		b.insert(e);
		b.insert(mul);
		b.insert(f);
		e.insert(g);
		e.insert(div);
		e.insert(h);
		ast.printAST();
	}
}