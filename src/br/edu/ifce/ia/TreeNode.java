package br.edu.ifce.ia;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	public TreeNode parent;
	public String action;
	public List<TreeNode> children;
	public String value;
	public double gain;

	public TreeNode() {

		this.children = new ArrayList<TreeNode>();
	}

	public TreeNode(TreeNode parent, String action, String value) {
		this();
		this.parent = parent;
		this.action = action;
		this.value = value;
	}

	public void print() {
		print("", true);
	}

	private void print(String prefix, boolean isRoot) {
		System.out.print(prefix);

		if ("".equals(action)) {
			System.out.print("└────────────────── ");
		} else {
			System.out.print("├──[" + String.format("%12s", action) + "]── ");
		}

		System.out.println(value);

		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isRoot ? "                    " : "│                   "), false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(prefix + (isRoot ? "                    " : "│                   "), true);
		}
	}

	@Override
	public String toString() {
		return "TreeNode [parent=" + parent.value + ", action=" + action + ", value=" + value + ", gain=" + gain + "]";
	}

}
