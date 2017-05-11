// package ru.spark_rf.classifiers;
// by @zxqfd555

import java.util.*;


public class QualityMetrics {
    
    private static class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2> > implements Comparable<Pair<T1, T2> > {
        private T1 first;
        private T2 second;
        
        Pair(T1 _first, T2 _second) {
            first = _first;
            second = _second;
        }
        
        public T1 getFirst() {
            return first;
        }
        
        public T2 getSecond () {
            return second;
        }
        
        @Override
        public int compareTo (Pair<T1, T2> other) {
            if (first.compareTo(other.first) > 0 || (first.compareTo(other.first) == 0 && second.compareTo(other.second) > 0)) {
                return 1;
            } else if (first.compareTo(other.first) == 0 && second.compareTo(other.second) == 0) {
                return 0;
            } else {
                return -1;
            }
        }
        
    }

    public static double meanSquaredError(ArrayList<Double> predicted, ArrayList<Double> groundTruth) {
        if (predicted.size() != groundTruth.size()) {
            throw new IllegalArgumentException("The lengths of predicted and groundTruth should coincide.");
        }
        int totalPoints = predicted.size();
        double sum = 0;
        for(int i = 0; i < totalPoints; ++i) {
            sum += (predicted.get(i) - groundTruth.get(i)) * (predicted.get(i) - groundTruth.get(i));
        }
        return sum / totalPoints;
    }
    
    public static double aucROC(ArrayList<Double> predicted, ArrayList<Integer> groundTruth) {
        if (predicted.size() != groundTruth.size()) {
            throw new IllegalArgumentException("The lengths of predicted and groundTruth should coincide.");
        }
        int totalPoints = predicted.size();
        int totalPositives = 0;
        int totalNegatives = 0;
        for(int elem : groundTruth) {
            totalPositives += elem;
            totalNegatives += 1 - elem;
        }
        
        ArrayList<Pair<Double, Integer> > vals = new ArrayList<Pair<Double, Integer> >();
        for(int i = 0; i < totalPoints; ++i) {
            vals.add(new Pair<Double, Integer>(predicted.get(i), groundTruth.get(i)));
        }
        Collections.sort(vals);
        Collections.reverse(vals);
        
        double lastFPR = 0.0;
        double lastTPR = 0.0;
        
        double lX = 0, lY = 0;
        
        double area = 0.0;
        for(int i = 0; i < totalPoints; ++i) {
            double newFPR, newTPR;
            if (vals.get(i).getSecond() == 0) {
                newFPR = lastFPR + 1.0 / totalNegatives;
                newTPR = lastTPR;
            } else {
                newFPR = lastFPR;
                newTPR = lastTPR + 1.0 / totalPositives;
            }
            
            if (i == totalPoints - 1 || Math.abs(vals.get(i).first - vals.get(i + 1).first) > 1E-9) {
                area += (newFPR - lX) * (newTPR + lY) / 2.0;
                lX = newFPR;
                lY = newTPR;
            }
            
            lastFPR = newFPR;
            lastTPR = newTPR;
        }
        
        area += (1.0 - lX) * (1.0 + lY) / 2.0;
        return area;
    }
    
}
