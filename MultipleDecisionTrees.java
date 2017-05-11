//package ru.spark_rf.classifiers;
// by  @zxqfd555 https://github.com/vitux/java_decision_tree/blob/master/RandomForest.java

import java.util.*;


public class MultipleDecisionTrees extends AbstractClassifier {
    public static final String TREE_DELIMITER = "!";

    private int nTrees;
    private ArrayList<DecisionTree> trees;

    /*
     Constructors: the number of trees to split the data.
     */

    private void init() {
        trees = new ArrayList<DecisionTree>();
    }

    public MultipleDecisionTrees() {
        init();
        nTrees = 10;
    }

    public MultipleDecisionTrees(int _nTrees) {
        init();
        nTrees = _nTrees;
    }

    private Integer mostFrequentElement(ArrayList<Integer> input) {
        int seqEqual = 1, maxSeqEqual = 0, answer = -1;
        for (int i = 1; i < input.size(); ++i) {
            if (input.get(i) == input.get(i - 1)) {
                ++seqEqual;
            } else {
                if (seqEqual > maxSeqEqual) {
                    maxSeqEqual = seqEqual;
                    answer = input.get(i - 1);
                }
                seqEqual = 1;
            }
        }
        if (seqEqual > maxSeqEqual) {
            maxSeqEqual = seqEqual;
            answer = input.get(input.size() - 1);
        }
        return answer;
    }

    /*
     Fitting the classifier.
     */
    @Override
    public void fit(ArrayList<ArrayList<Feature>> x, ArrayList<Integer> y) {
        ArrayList<Double> w = new ArrayList<Double>();
        for(int i = 0; i < x.size(); ++i) {
            w.add(1.0);
        }
        fit(x, w, y);
    }
    
    public void fit(ArrayList<ArrayList<Feature>> x, ArrayList<Double> w, ArrayList<Integer> y) {
        int samplesNumber = x.size();
        
        ArrayList<ArrayList<ArrayList<Feature>>> jobInputX = new ArrayList<ArrayList<ArrayList<Feature>>>();
        ArrayList<ArrayList<Integer>> jobInputY = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> jobInputW = new ArrayList<ArrayList<Double>>();
        
        for(int i = 0; i < samplesNumber; ++i) {
            jobInputX.add(new ArrayList<ArrayList<Feature>>());
            jobInputY.add(new ArrayList<Integer>());
            jobInputW.add(new ArrayList<Double>());
        }
        
        Random gen = new Random();
        for(int i = 0; i < samplesNumber; ++i) {
            int jobId = gen.nextInt(nTrees);
            jobInputX.get(jobId).add(x.get(i));
            jobInputY.get(jobId).add(y.get(i));
            jobInputW.get(jobId).add(w.get(i));
        }

        trees = new ArrayList<DecisionTree>();
        for (int treeId = 0; treeId < nTrees; ++treeId) if (jobInputW.get(treeId).size() > 0) {
            DecisionTree currentTree = new DecisionTree(3);
            currentTree.fit(jobInputX.get(treeId), jobInputW.get(treeId), jobInputY.get(treeId));
            trees.add(currentTree);
        }
    }

    @Override
    public int predict(ArrayList<Feature> x) {
        ArrayList<Integer> answers = new ArrayList<Integer>();
        for (DecisionTree tree : trees) {
            answers.add(tree.predict(x));
        }
        Collections.sort(answers);
        return mostFrequentElement(answers);
    }
    
    public double predict_proba(ArrayList<Feature> x, int soughtClass) {
        int hits = 0;
        for (DecisionTree tree : trees) {
            if (tree.predict(x) == soughtClass) {
                ++hits;
            }
        }
        return 1.0 * hits / nTrees;
    }

/*    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (DecisionTree dt : this.trees) {
            if (sb.length() > 0) {
                sb.append(TREE_DELIMITER);
            }
            sb.append(dt.serialize());
        }
        return sb.toString();
    }

    @Override
    public AbstractClassifier deserialize(String data) {
        RandomForest clf = new RandomForest();
        String[] parts = data.split(TREE_DELIMITER);
        clf.nTrees = parts.length;
        ArrayList<DecisionTree> dtLst = new ArrayList<>();
        for (String part : parts) {
            dtLst.add(new DecisionTree().deserialize(part));
        }
        clf.trees = dtLst;
        return clf;
    }
*/
};
