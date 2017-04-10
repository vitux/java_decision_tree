import java.util.*;


class DecisionTree {
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
        int x_split;
        double x_value;
        ArrayList<Integer> left_indexes = new ArrayList<>();
        ArrayList<Integer> right_indexes = new ArrayList<>();
        int left_value = 1;

        for (int i = 0; i < x.get(0).size(); ++i) {
            ArrayList<Double> all_values = new ArrayList<>();
            for (ArrayList<Double> aX : x) {
                all_values.add(aX.get(i));
            }
            Collections.sort(all_values);
            // Could be optimised here
            for (int j = 0; j < all_values.size() - 1; ++j) {
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

                    Map<Integer, Integer> count = get_count(left);

                    if (count.containsKey(0)) {
                        if (!count.containsKey(1) || count.get(0) > count.get(1)) {
                            left_value = 0;
                        }
                    }

                }
            }
        }

        if (is_split) {
            node.is_leaf = false;
            node.left = new Node(left_value);
            node.right = new Node(1 - left_value);
            ArrayList<ArrayList<Double>> x_left = new ArrayList<>();
            ArrayList<ArrayList<Double>> x_right = new ArrayList<>();
            ArrayList<Integer> y_left = new ArrayList<>();
            ArrayList<Integer> y_right = new ArrayList<>();
            for (Integer left_index : left_indexes) {
                x_left.add(x.get(left_index));
                y_left.add(y.get(left_index));
            }
            for (Integer right_index : left_indexes) {
                x_right.add(x.get(right_index));
                y_right.add(y.get(right_index));
            }
            split_node(node.left, max_depth - 1, x_left, y_left);
            split_node(node.right, max_depth - 1, x_right, y_right);
        }

    }

    void learn(ArrayList<ArrayList<Double>> x, ArrayList<Integer> y) {
        Map<Integer, Integer> count = get_count(y);
        int root_value = 1;
        if (count.containsKey(0)) {
            if (!count.containsKey(1) || count.get(0) > count.get(1)) {
                root_value = 0;
            }
        }

        root = new Node(root_value);
        root.is_leaf = true;

//        root.split_index = 0;
//        root.split_value = 3.5;
//        Node left = new Node(0);
//        Node right = new Node(1);
//        root.left = left;
//        root.right = right;
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
