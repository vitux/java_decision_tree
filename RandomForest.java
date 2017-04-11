import java.util.*;


class RandomForest {

    private int nTrees;
    private ArrayList<DecisionTree> trees;
    
    /*
     Constructors: the number of trees in the forest will be equal to 10 by default,
                   or can be re-specified in the constructor.
     */
    
    private void init () {
        trees = new ArrayList<DecisionTree>();
    }
    
    RandomForest () {
        init();
        nTrees = 10;
    }
    
    RandomForest (int _nTrees) {
        init();
        nTrees = _nTrees;
    }
    
    /*
     Helpers.
     */
    
    private ArrayList<Integer> getSample (int samplesNumber) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Random gen = new Random();
        for(int i = 0; i < samplesNumber; ++i) {
            if (gen.nextBoolean()) {
                result.add(i);
            }
        }
        if (result.size() == 0) {
            result.add(gen.nextInt(samplesNumber));
        }
        return result;
    }
    
    private Integer mostFrequentElement (ArrayList<Integer> input) {
        int seqEqual = 1, maxSeqEqual = 0, answer = -1;
        for(int i = 1; i < input.size(); ++i) {
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
    
    public void fit (ArrayList<ArrayList<Double> > x, ArrayList<Integer> y) {
        int samplesNumber = x.size();
        
        trees = new ArrayList<DecisionTree>();
        for (int treeId = 0; treeId < nTrees; ++treeId) {
            DecisionTree currentTree = new DecisionTree();
            
            ArrayList<ArrayList<Double> > sampleX = new ArrayList<ArrayList<Double> >();
            ArrayList<Integer> sampleY = new ArrayList<Integer>();
            
            ArrayList<Integer> subsampleIndices = getSample(samplesNumber);
            for (int index : subsampleIndices) {
                sampleX.add(x.get(index));
                sampleY.add(y.get(index));
            }
            
            currentTree.fit(sampleX, sampleY);
            
            trees.add(currentTree);
            
        }
    }
    
    /*
     Prediction.
     */
    
    public Integer predict (ArrayList<Double> x) {
        ArrayList<Integer> answers = new ArrayList<Integer>();
        for(DecisionTree tree : trees) {
            answers.add(tree.predict(x));
        }
        Collections.sort(answers);
        return mostFrequentElement(answers);
    }
    
    /*
     Merge.
     */
    
    private void mergeWith (RandomForest other) {
        for(DecisionTree tree : other.trees) {
            trees.add(tree);
        }
    }
    
};