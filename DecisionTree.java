import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;
import java.util.TreeMap;


public class DecisionTree {

    private class Node {
        boolean is_leaf;
        int value;

        int split_index;
        double split_value;
        Node left;
        Node right;

        Node(int value) {
            is_leaf = true;
            this.value = value;
        }
    }

    private Node root;

    Double entropy(ArrayList<Integer> y) {
        TreeMap<Integer, Integer> count = new TreeMap<>();
        for (Integer y_curr: y) {
            if (count.containsKey(y_curr)) {
                count.compute(y_curr, (k, v) -> v + 1);
            } else {
                count.put(y_curr, 1);
            }
        }
        double entropy = 0;
        for (Map.Entry<Integer, Integer> entry: count.entrySet()) {
            double frequency = (double)entry.getValue() / y.size();
            entropy -= frequency * Math.log(frequency);
        }
        return entropy;
    }

    public void learn(ArrayList<ArrayList<Double>> x, ArrayList<Integer> y) {
        root = new Node(0);
        root.is_leaf = false;
        root.split_index = 0;
        root.split_value = 3.5;
        Node left = new Node(0);
        Node right = new Node(1);
        root.left = left;
        root.right = right;
    }

    public int predict(ArrayList<Double> x) {
        Node node = root;
        while (!node.is_leaf) {
            if (x.get(node.split_index) < node.split_value) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.value;
    }

}
