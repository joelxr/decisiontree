package br.edu.ifce.ia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecisionTree {

	private List<Map<String, String>> table;

	private TreeNode rootTree;

	public DecisionTree() {
		this.table = new ArrayList<Map<String, String>>();
	}

	public void addRow(String[] fields, String[] data) {
		Map<String, String> rowData = new HashMap<String, String>();

		for (int i = fields.length - 1; i >= 0; i--) {
			rowData.put(fields[i], data[i]);
		}

		this.table.add(rowData);
	}

	public void printTable() {

		System.out.println();
		
		int size = 12;

		if (!this.table.isEmpty()) {
			Set<String> fields = this.table.get(0).keySet();

			for (String field : fields) {
				System.out.print(" | " + field);

				for (int i = 0; i < size - field.length(); i++)
					System.out.print(" ");
			}

			System.out.println();

			for (Map<String, String> row : this.table) {
				Collection<String> data = row.values();

				for (String string : data) {
					System.out.print(" | " + string);

					for (int i = 0; i < size - string.length(); i++)
						System.out.print(" ");
				}

				System.out.println();
			}
		}
	}

	private double calculateGain(TreeNode node) {
		double result = 0.0;
		int allPositiveCount = 0, allNegativeCount = 0;

		List<String> fieldValues = new ArrayList<String>();
		Map<String, Integer> fieldFrequencyPos = new HashMap<String, Integer>();
		Map<String, Integer> fieldFrequencyNeg = new HashMap<String, Integer>();

		for (Map<String, String> map : table) {
			String s = map.get(node.value);

			if (!fieldValues.contains(s)) {
				fieldValues.add(s);
				fieldFrequencyPos.put(s, 0);
				fieldFrequencyNeg.put(s, 0);
			}
		}

		for (Map<String, String> map : table) {

			String s = map.get(node.value);
			String classe = map.get("classe");

			Integer freqPos = fieldFrequencyPos.get(s);
			Integer freqNeg = fieldFrequencyNeg.get(s);

			if (node.parent != null) {

				String parentValue = map.get(node.parent.value);
				String action = node.action;

				if (parentValue.equals(action)) {

					if (classe.equalsIgnoreCase("sim")) {
						fieldFrequencyPos.put(s, ++freqPos);
					} else if (classe.equalsIgnoreCase("não")) {
						fieldFrequencyNeg.put(s, ++freqNeg);
					}
				}
			} else {
				if (classe.equalsIgnoreCase("sim")) {
					fieldFrequencyPos.put(s, ++freqPos);
				} else if (classe.equalsIgnoreCase("não")) {
					fieldFrequencyNeg.put(s, ++freqNeg);
				}
			}
		}

		double resto = 0.0;

		for (String string : fieldValues) {
			int fieldPos, fieldNeg;
			double fieldTotal, total = 0.0;

			fieldPos = fieldFrequencyPos.get(string);
			fieldNeg = fieldFrequencyNeg.get(string);
			fieldTotal = fieldPos + fieldNeg;

			if (node.parent != null) {
				for (Map<String, String> map : table) {
					String parentValue = map.get(node.parent.value);
					String action = node.action;

					if (parentValue.equals(action)) {
						total++;
					}
				}
			} else {
				total = table.size();
			}

			resto += (fieldTotal / total) * calculateEntropy(fieldPos, fieldNeg);
		}

		for (Map<String, String> map : table) {

			String classe = map.get("classe");

			if (node.parent != null) {

				String parentValue = map.get(node.parent.value);
				String action = node.action;

				if (parentValue.equals(action)) {
					if (classe.equalsIgnoreCase("sim")) {
						allPositiveCount++;
					} else if (classe.equalsIgnoreCase("não")) {
						allNegativeCount++;
					}
				}
			} else {
				if (classe.equalsIgnoreCase("sim")) {
					allPositiveCount++;
				} else if (classe.equalsIgnoreCase("não")) {
					allNegativeCount++;
				}
			}
		}

		result = calculateEntropy(allPositiveCount, allNegativeCount) - resto;

		return result;
	}

	private double calculateEntropy(int pos, int neg) {
		double posD, negD, total, fat1, fat2, log1, log2, result;
		posD = pos;
		negD = neg;
		total = posD + negD;

		if (neg == 0) {
			fat1 = pos / total;
			log1 = Math.log(fat1) / Math.log(2);
			result = -fat1 * log1;
		} else if (pos == 0) {
			fat2 = neg / total;
			log2 = Math.log(fat2) / Math.log(2);
			result = -(fat2 * log2);
		} else {
			fat1 = posD / total;
			fat2 = negD / total;
			log1 = Math.log(fat1) / Math.log(2);
			log2 = Math.log(fat2) / Math.log(2);
			result = -fat1 * log1 - fat2 * log2;
		}

		return result;
	}

	private void printTree() {
		System.out.println();
		if (this.rootTree != null)
			this.rootTree.print();

	}

	private void generateTree() {
		this.rootTree = getNode();
		this.generateTree(this.rootTree);
	}

	private TreeNode getNode() {
		Set<String> fields = table.get(0).keySet();
		double maxGain = 0.0;
		TreeNode result = null;

		for (String string : fields) {

			if ("classe".equalsIgnoreCase(string)) {
				continue;
			}

			TreeNode n = new TreeNode();
			n.value = string;
			n.action = "";
			n.children = new ArrayList<TreeNode>();
			n.parent = null;
			n.gain = this.calculateGain(n);

			if (n.gain > maxGain) {
				maxGain = n.gain;
				result = n;
			}
		}

		return result;
	}

	private void generateTree(TreeNode node) {

		if (node == null) {
			return;
		}

		Set<String> nodeActions = new HashSet<String>();

		for (Map<String, String> map : table) {
			nodeActions.add(map.get(node.value));
		}

		for (String action : nodeActions) {
			double maxGain = 0.0;
			TreeNode maxGainNode = null;
			Set<String> fields = table.get(0).keySet();
			Set<String> classeValues = new HashSet<String>();
			String value = "";

			for (Map<String, String> map : table) {

				if (node.parent == null) {

					if (map.get(node.value).equals(action)) {
						classeValues.add(map.get("classe"));
						value = map.get("classe");
					}

					if (classeValues.size() > 1)
						break;
				} else {
					
					if (map.get(node.value).equals(action) && map.get(node.parent.value).equals(node.action)) {
						classeValues.add(map.get("classe"));
						value = map.get("classe");
					}

					if (classeValues.size() > 1)
						break;
				}
			}

			if (classeValues.size() == 1) {
				node.children.add(new TreeNode(node, action, value));
			} else {
				for (String string : fields) {

					if ("classe".equalsIgnoreCase(string)) {
						continue;
					}

					boolean visited = false;

					for (TreeNode aux = node; aux != null; aux = aux.parent) {
						visited = aux.value.equals(string);
					}

					if (visited) {
						continue;
					}

					TreeNode n = new TreeNode(node, action, string);
					n.gain = this.calculateGain(n);

					if (n.gain > maxGain) {
						maxGain = n.gain;
						maxGainNode = n;
					}
				}

				if (maxGainNode != null) {
					node.children.add(maxGainNode);
				}

				generateTree(maxGainNode);
			}
		}

	}

	private void generateTable() {
		String[] fields = new String[] { "tempo", "temperatura", "humidade", "ventando", "classe" };
		addRow(fields, new String[] { "ensolarado", "quente", "alta", "não", "não" });
		addRow(fields, new String[] { "ensolarado", "quente", "alta", "sim", "não" });
		addRow(fields, new String[] { "nublado", "quente", "alta", "não", "sim" });
		addRow(fields, new String[] { "chuva", "média", "alta", "não", "sim" });
		addRow(fields, new String[] { "chuva", "fria", "normal", "não", "sim" });
		addRow(fields, new String[] { "chuva", "fria", "normal", "sim", "não" });
		addRow(fields, new String[] { "nublado", "fria", "normal", "sim", "sim" });
		addRow(fields, new String[] { "ensolarado", "média", "alta", "não", "não" });
		addRow(fields, new String[] { "ensolarado", "fria", "normal", "não", "sim" });
		addRow(fields, new String[] { "chuva", "média", "normal", "não", "sim" });
		addRow(fields, new String[] { "ensolarado", "média", "normal", "sim", "sim" });
		addRow(fields, new String[] { "nublado", "média", "alta", "sim", "sim" });
		addRow(fields, new String[] { "nublado", "quente", "normal", "não", "sim" });
		addRow(fields, new String[] { "chuva", "média", "alta", "sim", "não" });
	}

	public static void main(String[] args) {
		DecisionTree decisionTree = new DecisionTree();
		decisionTree.generateTable();
		decisionTree.generateTree();
		decisionTree.printTable();
		decisionTree.printTree();
	}

}
