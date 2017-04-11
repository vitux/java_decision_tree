import java.io.Serializable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleBinaryOperator;


class DecisionTree {
    private int max_depth = 3;

    private Map<Integer, Integer> get_count(ArrayList<Integer> input) {
        TreeMap<Integer, Integer> count = new TreeMap<>();
        for (Integer y_curr: input) {
            if (count.containsKey(y_curr)) {
                count.compute(y_curr, (k, v) -> v + 1);
            } else {
                count.put(y_curr, 1);
            }
        }
        return count;
    }
    
    private int get_max_y(ArrayList<Integer> input) {
        Map<Integer, Integer> count = get_count(input);
        int max_val = -1;
        int max_y = 1;
        for (Map.Entry<Integer, Integer> entry: count.entrySet()) {
            if (entry.getValue() > max_val) {
                max_val = entry.getValue();
                max_y = entry.getKey();
            }
        }
        
        return max_y;
    }

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

        Node(String serialized_node) {
            String[] parts = serialized_node.split("\t");
            boolean is_left = Boolean.valueOf(parts[0]);
            boolean is_right = Boolean.valueOf(parts[1]);
            if (is_left) {
                left = new Node();
            }
            if (is_right) {
                right = new Node();
            }
            is_leaf = !(is_left || is_right);
            value = Integer.valueOf(parts[2]);
            split_index = Integer.valueOf(parts[3]);
            split_value = Double.valueOf(parts[4]);
        }

        Node() {

        }

        @Override
        public String toString() {
            return "" + (left != null) + "\t" + (right != null) + "\t" +
                    value + "\t" + split_index + "\t" + split_value;
        }
    }

    private Node root;

    double entropy(ArrayList<Integer> y) {
        Map<Integer, Integer> count = get_count(y);

        double entropy = 0;
        for (Map.Entry<Integer, Integer> entry: count.entrySet()) {
            double frequency = (double)entry.getValue() / y.size();
            entropy -= frequency * Math.log(frequency);
        }
        return entropy;
    }

    private void split_node(Node node, int max_depth, ArrayList<ArrayList<Double>> x, ArrayList<Integer> y) {
        if (max_depth == 0) {
            return;
        }
        double curr_entropy = entropy(y);

        double best_entropy = curr_entropy - 1e-6;
        boolean is_split = false;
        int x_split = 0;
        double x_value = 0;
        ArrayList<Integer> left_indexes = new ArrayList<>();
        ArrayList<Integer> right_indexes = new ArrayList<>();
        int left_value = 1;
        int right_value = 1;

        for (int i = 0; i < x.get(0).size(); ++i) {
            ArrayList<Double> all_values = new ArrayList<>();
            for (ArrayList<Double> aX : x) {
                all_values.add(aX.get(i));
            }
            Collections.sort(all_values);
            // Could be optimised here
            for (int j = 0; j < all_values.size() - 1; ++j) {
                if (all_values.get(j + 1) - all_values.get(j) < 1e-6) {
                    continue;
                }
                // [0 .. j] , [j + 1, ...]
                ArrayList<Integer> left = new ArrayList<>();
                ArrayList<Integer> right = new ArrayList<>();
                ArrayList<Integer> l_indexes = new ArrayList<>();
                ArrayList<Integer> r_indexes = new ArrayList<>();

                for (int k = 0; k < y.size(); ++k) {
                    if (x.get(k).get(i) <= all_values.get(j)) {
                        left.add(y.get(k));
                        l_indexes.add(k);
                    } else {
                        right.add(y.get(k));
                        r_indexes.add(k);
                    }
                }
                double new_entropy = (left.size() * entropy(left) + right.size() * entropy(right)) / y.size();
                if (new_entropy < best_entropy) {
                    best_entropy = new_entropy;
                    is_split = true;
                    x_split = i;
                    x_value = (all_values.get(j) + all_values.get(j + 1)) / 2;
                    left_indexes = l_indexes;
                    right_indexes = r_indexes;

                    left_value = get_max_y(left);
                    right_value = get_max_y(right);

                }
            }
        }

        if (is_split) {
            node.is_leaf = false;
            node.split_index = x_split;
            node.split_value = x_value;
            node.left = new Node(left_value);
            node.right = new Node(right_value);
            ArrayList<ArrayList<Double>> x_left = new ArrayList<>();
            ArrayList<ArrayList<Double>> x_right = new ArrayList<>();
            ArrayList<Integer> y_left = new ArrayList<>();
            ArrayList<Integer> y_right = new ArrayList<>();
            for (Integer left_index : left_indexes) {
                x_left.add(x.get(left_index));
                y_left.add(y.get(left_index));
            }
            for (Integer right_index : right_indexes) {
                x_right.add(x.get(right_index));
                y_right.add(y.get(right_index));
            }
            split_node(node.left, max_depth - 1, x_left, y_left);
            split_node(node.right, max_depth - 1, x_right, y_right);
        }

    }

    void fit(ArrayList<ArrayList<Double>> x, ArrayList<Integer> y) {

        int root_value = get_max_y(y);

        root = new Node(root_value);
        root.is_leaf = true;

        split_node(root, max_depth, x, y);
    }

    int predict(ArrayList<Double> x) {
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

    private void addNode(Node node, ArrayList<Node> nodeInfos) {
        nodeInfos.add(node);
        if (node.left != null) {
            addNode(node.left, nodeInfos);
        }
        if (node.right != null) {
            addNode(node.right, nodeInfos);
        }
    }

    @Override
    public String toString() {
        if (root == null) {
            return "";
        }
        ArrayList<Node> nodeInfos = new ArrayList<>();
        addNode(root, nodeInfos);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeInfos.size(); ++i) {
            sb.append(nodeInfos.get(i).toString());
            if (i != nodeInfos.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private int unserialize(Node node, String[] nodes, int next_index) {
        if (node.left != null) {
            node.left = new Node(nodes[next_index]);
            next_index = unserialize(node.left, nodes, next_index + 1);
        }
        if (node.right != null) {
            node.right = new Node(nodes[next_index]);
            next_index = unserialize(node.right, nodes, next_index + 1);
        }
        return next_index;
    }

    public void fromString(String s) {
        String[] nodes = s.split(";");
        if (nodes.length == 0) {
            root = null;
            return;
        }
        root = new Node(nodes[0]);
        unserialize(root, nodes, 1);
    }
}
