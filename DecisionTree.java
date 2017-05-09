import java.io.Serializable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleBinaryOperator;


class DecisionTree {

    private Map<Integer, Double> get_count(ArrayList<Integer> input, ArrayList<Double> w) {
        TreeMap<Integer, Double> count = new TreeMap<>();
        for (int i = 0; i < input.size(); ++i) {
            Integer y_curr = input.get(i);
            Double w_curr = w.get(i);
            if (count.containsKey(y_curr)) {
                count.compute(y_curr, (k, v) -> v + w_curr);
            } else {
                count.put(y_curr, w_curr);
            }
        }
        return count;
    }

    private int get_max_y(ArrayList<Integer> input, ArrayList<Double> w) {
        Map<Integer, Double> count = get_count(input, w);
        double max_val = -1;
        int max_y = 1;
        for (Map.Entry<Integer, Double> entry: count.entrySet()) {
            if (entry.getValue() > max_val - 1e-6) {
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

        boolean is_categorical_split;
        double split_value;

        int split_left_class;

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

            is_categorical_split = Boolean.valueOf(parts[5]);
            split_left_class = Integer.valueOf(parts[6]);
        }

        Node() {

        }

        @Override
        public String toString() {
            return "" + (left != null) + "\t" + (right != null) + "\t" +
                    value + "\t" + split_index + "\t" + split_value + "\t" + is_categorical_split +
                    "\t" + split_left_class;
        }
    }

    private Node root;

    private double entropy(ArrayList<Integer> y, ArrayList<Double> w) {
        Map<Integer, Double> count = get_count(y, w);

        double entropy = 0;
        for (Map.Entry<Integer, Double> entry: count.entrySet()) {
            double frequency = entry.getValue() / y.size();
            entropy -= frequency * Math.log(frequency);
        }
        return entropy;
    }

    private void TryMakeSplit(SplitConfig splitConfig, ArrayList<ArrayList<Feature>> x,
                                   ArrayList<Double> w, ArrayList<Integer> y, int i) {
        ArrayList<Feature> all_values = new ArrayList<>();
        for (ArrayList<Feature> aX : x) {
            all_values.add(aX.get(i));
        }

        Collections.sort(all_values);

        int previous_category = -1;
        for (int j = 0; j < all_values.size() - 1; ++j) {
            if (all_values.get(0) instanceof NumericalFeature) {
                if (((NumericalFeature) all_values.get(j + 1)).getValue() -
                        ((NumericalFeature)all_values.get(j)).getValue() < 1e-6) {
                    continue;
                }
            } else {
                int curr_category = ((CategoricalFeature)all_values.get(j)).getValue();
                if (curr_category == previous_category)
                    continue;
                previous_category = curr_category;
            }
            // [0 .. j] , [j + 1, ...]
            ArrayList<Integer> left = new ArrayList<>();
            ArrayList<Integer> right = new ArrayList<>();
            ArrayList<Double> left_w = new ArrayList<>();
            ArrayList<Double> right_w = new ArrayList<>();
            ArrayList<Integer> l_indexes = new ArrayList<>();
            ArrayList<Integer> r_indexes = new ArrayList<>();
            double sum_left = 0;
            double sum_right = 0;

            for (int k = 0; k < y.size(); ++k) {
                boolean is_left;
                if (all_values.get(j) instanceof NumericalFeature) {
                    is_left = ((NumericalFeature) x.get(k).get(i)).getValue() <=
                            ((NumericalFeature)all_values.get(j)).getValue();
                } else {
                    is_left = ((CategoricalFeature) x.get(k).get(i)).getValue() ==
                            ((CategoricalFeature)all_values.get(j)).getValue();
                }
                if (is_left) {
                    left.add(y.get(k));
                    left_w.add(w.get(k));
                    sum_left += w.get(k);
                    l_indexes.add(k);
                } else {
                    right.add(y.get(k));
                    right_w.add(w.get(k));
                    sum_right += w.get(k);
                    r_indexes.add(k);
                }
            }

            double new_entropy = (sum_left * entropy(left, left_w) +
                    sum_right * entropy(right, right_w)) / (sum_left + sum_right);
            if (new_entropy < splitConfig.best_entropy) {
                splitConfig.best_entropy = new_entropy;
                splitConfig.is_split = true;
                splitConfig.x_split = i;
                splitConfig.is_categorical = all_values.get(0) instanceof CategoricalFeature;
                if (splitConfig.is_categorical) {
                    splitConfig.left_category = ((CategoricalFeature)all_values.get(j)).getValue();
                } else {
                    splitConfig.x_value = (((NumericalFeature)all_values.get(j)).getValue() +
                            ((NumericalFeature)all_values.get(j + 1)).getValue()) / 2;
                }
                splitConfig.left_indexes = l_indexes;
                splitConfig.right_indexes = r_indexes;
                splitConfig.left_value = get_max_y(left, left_w);
                splitConfig.right_value = get_max_y(right, right_w);
            }
        }
    }

    private class SplitConfig {
        double curr_entropy;
        double best_entropy;
        boolean is_split = false;
        int x_split = 0;
        double x_value = 0;
        ArrayList<Integer> left_indexes = new ArrayList<>();
        ArrayList<Integer> right_indexes = new ArrayList<>();
        int left_value = 1;
        int right_value = 1;
        boolean is_categorical = false;
        int left_category = -1;
        SplitConfig(double curr_entropy) {
            this.curr_entropy = curr_entropy;
            this.best_entropy = curr_entropy - 1e-6;
        }
    }

    private void split_node(Node node, int max_depth, ArrayList<ArrayList<Feature>> x,
                            ArrayList<Double> w, ArrayList<Integer> y) {
        if (max_depth == 0) {
            return;
        }

        SplitConfig splitConfig = new SplitConfig(entropy(y, w));

        for (int i = 0; i < x.get(0).size(); ++i) {
            TryMakeSplit(splitConfig, x, w, y, i);
        }

        if (splitConfig.is_split) {
            make_split(node, splitConfig, max_depth, x, w, y);
        }

    }

    private void make_split(Node node, SplitConfig splitConfig, int max_depth,
                            ArrayList<ArrayList<Feature>> x, ArrayList<Double> w, ArrayList<Integer> y) {

        node.is_leaf = false;
        node.split_index = splitConfig.x_split;
        node.split_value = splitConfig.x_value;
        node.left = new Node(splitConfig.left_value);
        node.right = new Node(splitConfig.right_value);

        node.is_categorical_split = splitConfig.is_categorical;
        node.split_left_class = splitConfig.left_category;

        ArrayList<ArrayList<Feature>> x_left = new ArrayList<>();
        ArrayList<ArrayList<Feature>> x_right = new ArrayList<>();

        ArrayList<Double> w_left = new ArrayList<>();
        ArrayList<Double> w_right = new ArrayList<>();

        ArrayList<Integer> y_left = new ArrayList<>();
        ArrayList<Integer> y_right = new ArrayList<>();
        for (Integer left_index : splitConfig.left_indexes) {
            x_left.add(x.get(left_index));
            w_left.add(w.get(left_index));
            y_left.add(y.get(left_index));
        }
        for (Integer right_index : splitConfig.right_indexes) {
            x_right.add(x.get(right_index));
            w_right.add(w.get(right_index));
            y_right.add(y.get(right_index));
        }
        split_node(node.left, max_depth - 1, x_left, w_left, y_left);
        split_node(node.right, max_depth - 1, x_right, w_right, y_right);
    }

    void fit(ArrayList<ArrayList<Feature>> x, ArrayList<Double> w, ArrayList<Integer> y) {

        int root_value = get_max_y(y, w);

        root = new Node(root_value);
        root.is_leaf = true;

        int max_depth = 3;
        split_node(root, max_depth, x, w, y);
    }

    int predict(ArrayList<Feature> x) {
        Node node = root;
        while (!node.is_leaf) {
            if (node.is_categorical_split) {
                if (((CategoricalFeature)x.get(node.split_index)).getValue() == node.split_left_class) {
                    node = node.left;
                } else {
                    node = node.right;
                }
            } else {
                if (((NumericalFeature)x.get(node.split_index)).getValue() < node.split_value) {
                    node = node.left;
                } else {
                    node = node.right;
                }
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

    void fromString(String s) {
        String[] nodes = s.split(";");
        if (nodes.length == 0) {
            root = null;
            return;
        }
        root = new Node(nodes[0]);
        unserialize(root, nodes, 1);
    }
}
