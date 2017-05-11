import java.util.*;

public class AdaBoostClassifier extends AbstractClassifier {

    private int nClassifiers;
    
    private ArrayList<MultipleDecisionTrees> clf;
    private ArrayList<Double> coef;
    
    public AdaBoostClassifier () {
        nClassifiers = 10;
    }
    
    public AdaBoostClassifier (int _nClassifiers) {
        nClassifiers = _nClassifiers;
    }
    
    @Override
    public void fit (ArrayList<ArrayList<Feature>> x, ArrayList<Integer> y) {
        int nObj = x.size();
        ArrayList<Double> w = new ArrayList<Double>();
        clf = new ArrayList<MultipleDecisionTrees>();
        coef = new ArrayList<Double>();
        
        for(int i = 0; i < nObj; ++i) {
            w.add(1.0 / nObj);
        }
        
        for(int i = 0; i < nClassifiers; ++i) {
            ArrayList<Integer> predictedNow = new ArrayList<Integer>();
            MultipleDecisionTrees currentClf = new MultipleDecisionTrees(5);
            currentClf.fit(x, w, y);
            
            clf.add(currentClf);
            
            double pM = 0;

            double k0 = 0;
            double k1 = 0;

            for(int j = 0; j < nObj; ++j) {
                int cl = currentClf.predict(x.get(j));
                predictedNow.add(cl);
                if (y.get(j) != cl) {
                    pM += 1; //
                    k1 += w.get(j);
                } else {
                    k0 += w.get(j);
                }
            }
            pM /= 1.0 * nObj;

            System.out.println(pM + " " + 0.5 * Math.log((1.0 - pM) / pM) + " " + k0 + " " + k1);
            
            coef.add(0.5 * Math.log((1.0 - pM) / pM));
            
            double sumW = 0.0;
            
            for(int j = 0; j < nObj; ++j) {
                double toExp;
                if (y.get(j) == predictedNow.get(j)) {
                    toExp = -coef.get(i);
                } else {
                    toExp = coef.get(i);
                }
                double newW = w.get(j) * Math.exp(toExp);
                w.set(j, newW);
                sumW += newW;
            }
            
            for(int j = 0; j < nObj; ++j) {
                w.set(j, w.get(j) / sumW);
            }
        }
    }
    
    @Override
    public int predict (ArrayList<Feature> x) {
        int nCl = clf.size();
        double sum0 = 0.0, sum1 = 0.0, sum = 0.0;
        for(int i = 0; i < nCl; ++i) {
            int pr = clf.get(i).predict(x);
            if (pr == 0) pr = -1;
            sum += pr * coef.get(i);
        }
        if (sum < 0) return 0; else return 1;
    }

    public double predict_proba (ArrayList<Feature> x, Integer y) {
        int nCl = clf.size();
        double sum0 = 0.0, sum1 = 0.0;
        for(int i = 0; i < nCl; ++i) {
            int pr = clf.get(i).predict(x);
            double cf = coef.get(i);
            if (cf < 0) {
                pr = 1 - pr;
                cf = -cf;
            }
            if (pr == 0) {
                sum0 += cf;
            } else {
                sum1 += cf;
            }
        }
//        System.out.println(sum0 + " " + sum1);
        return sum1 / (sum0 + sum1);
    }
    
    
}