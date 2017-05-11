import java.io.*;
import java.util.*;


public class Test {

    public static void main(String[] args) {
        testSpambaseData();
        testRAOP();
    }
    
    private static void testRAOP () {
        ArrayList<ArrayList<Feature> > xTrain = new ArrayList<ArrayList<Feature> >();
        ArrayList<Integer> yTrain = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Feature> > xTest = new ArrayList<ArrayList<Feature> >();
        ArrayList<Integer> yTest = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Feature> > currentSampleX = xTrain;
        ArrayList<Integer> currentSampleY = yTrain;
        
        Random gen = new Random(23);
        try {
            BufferedReader reader = new BufferedReader(new FileReader("datasets/raop.txt"));
            String line = reader.readLine();
            
            while (line != null) {
                String[] tokens = line.split(",");
                
                if (gen.nextBoolean()) {
                    currentSampleX = xTrain;
                    currentSampleY = yTrain;
                } else {
                    currentSampleX = xTest;
                    currentSampleY = yTest;
                }
                
                currentSampleY.add(Integer.parseInt(tokens[12]));
                ArrayList<Feature> row = new ArrayList<Feature>();
                for(int i = 0; i < 12; ++i) {
                    row.add(new NumericalFeature(Double.parseDouble(tokens[i])) );
                }
                
                currentSampleX.add(row);
                
                line = reader.readLine();
            }
            
            reader.close();
        } catch (IOException e) {
            
        }
        
        ArrayList<Double> w = new ArrayList<Double>();
        for(int i = 0; i < xTrain.size(); ++i) w.add(1.0);
        
        AdaBoostClassifier rf = new AdaBoostClassifier(10);
        rf.fit(xTrain, yTrain);
        
        ArrayList<Double> predicted = new ArrayList<Double>();
        int ok = 0;
        
        for (int i = 0; i < xTest.size(); ++i) {
            int q = rf.predict(xTest.get(i));
            if (q == yTest.get(i)) {
                ++ok;
            }
            predicted.add(rf.predict_proba(xTest.get(i), 1));
        }
        System.out.println(ok + " " + yTest.size() + " " + QualityMetrics.aucROC(predicted, yTest));
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader("datasets/raop_test.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("datasets/raop_answers.txt"));
            
            writer.write("request_id,requester_received_pizza\n");
            
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(",");
                
                ArrayList<Feature> row = new ArrayList<Feature>();
                
                for(int i = 0; i < 12; i++) {
                    row.add(new NumericalFeature(Double.parseDouble(tokens[i])));
                }
                
                writer.write(tokens[12] + "," + rf.predict(row) + "\n");
                line = reader.readLine();
            }
            writer.close();
        } catch (IOException e) {
            
        }
        
    }
    
    private static void testSpambaseData () {
        ArrayList<ArrayList<Feature> > xTrain = new ArrayList<ArrayList<Feature> >();
        ArrayList<Integer> yTrain = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Feature> > xTest = new ArrayList<ArrayList<Feature> >();
        ArrayList<Integer> yTest = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Feature> > currentSampleX = xTrain;
        ArrayList<Integer> currentSampleY = yTrain;
        
        Random gen = new Random(23);
        try {
            BufferedReader reader = new BufferedReader(new FileReader("datasets/spambase_data.txt"));
            String line = reader.readLine();
            
            while (line != null) {
                String[] tokens = line.split(",");
                
                if (gen.nextBoolean()) {
                    currentSampleX = xTrain;
                    currentSampleY = yTrain;
                } else {
                    currentSampleX = xTest;
                    currentSampleY = yTest;
                }
                
                currentSampleY.add(Integer.parseInt(tokens[57]));
                ArrayList<Feature> row = new ArrayList<Feature>();
                for(int i = 0; i < 57; ++i) {
                    row.add(new NumericalFeature(Double.parseDouble(tokens[i])) );
                }
                
                currentSampleX.add(row);
                
                line = reader.readLine();
            }
            
            reader.close();
        } catch (IOException e) {
            
        }
        
        AdaBoostClassifier rf = new AdaBoostClassifier();
        ArrayList<Double> w = new ArrayList<Double>();
        
        for(int i = 0; i < xTrain.size(); ++i) w.add(1.0);
        
        rf.fit(xTrain, yTrain);
        
        ArrayList<Double> predicted = new ArrayList<Double>();
        int ok = 0;
        
        for (int i = 0; i < xTest.size(); ++i) {
            int q = rf.predict(xTest.get(i));
            if (q == yTest.get(i)) {
                ++ok;
            }
            predicted.add(rf.predict_proba(xTest.get(i), 1));
        }
        System.out.println(ok + " " + yTest.size() + " " + QualityMetrics.aucROC(predicted, yTest));
        
/*        try {
            BufferedReader reader = new BufferedReader(new FileReader("datasets/raop_test.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("datasets/raop_answers.txt"));
            
            writer.write("request_id,requester_received_pizza\n");
            
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(",");
                
                ArrayList<Feature> row = new ArrayList<Feature>();
                
                for(int i = 0; i < 12; i++) {
                    row.add(new NumericalFeature(Double.parseDouble(tokens[i])));
                }
                
                writer.write(tokens[12] + "," + rf.predict_proba(row, 1) + "\n");
                line = reader.readLine();
            }
            writer.close();
        } catch (IOException e) {
            
        }*/
        
    }
    
/*
    private static void testSpambaseData () {
        ArrayList<ArrayList<Double> > xTrain = new ArrayList<ArrayList<Double> >();
        ArrayList<Integer> yTrain = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Double> > xTest = new ArrayList<ArrayList<Double> >();
        ArrayList<Integer> yTest = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Double> > currentSampleX = xTrain;
        ArrayList<Integer> currentSampleY = yTrain;
        
        Random gen = new Random();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("datasets/spambase_data.txt"));
            String line = reader.readLine();
            
            while (line != null) {
                String [] tokens = line.split(",");
                
                if (gen.nextBoolean()) {
                    currentSampleX = xTrain;
                    currentSampleY = yTrain;
                } else {
                    currentSampleX = xTest;
                    currentSampleY = yTest;
                }
                
                currentSampleY.add(Integer.parseInt(tokens[57]));
                ArrayList<Double> row = new ArrayList<Double>();
                for(int i = 0; i < 57; ++i) {
                    row.add(Double.parseDouble(tokens[i]));
                }
                
                currentSampleX.add(row);
                
                if (currentSampleX.size() == 2300) {
                    currentSampleX = xTest;
                    currentSampleY = yTest;
                }
                
                line = reader.readLine();
            }
            
            reader.close();
        } catch (IOException e) {
            
        }
     
        RandomForest rf = new RandomForest(1);
        rf.fit(xTrain, yTrain);
        ArrayList<Double> probs = new ArrayList<Double>();
        
        int numOK = 0;
        for(int i = 0; i < xTest.size(); ++i) {
            int pClass = rf.predict(xTest.get(i));
            double predicted = rf.predict_proba(xTest.get(i), 1);
            probs.add(predicted);
            if (pClass == yTest.get(i)) {
                ++numOK;
            }
        }
        System.out.println(QualityMetrics.aucROC(probs, yTest) + " " + numOK);
        
    }
    
    private static void testAustralianScale () {
        ArrayList<ArrayList<Double> > xTrain = new ArrayList<ArrayList<Double> >();
        ArrayList<Integer> yTrain = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Double> > xTest = new ArrayList<ArrayList<Double> >();
        ArrayList<Integer> yTest = new ArrayList<Integer>();
        
        ArrayList<ArrayList<Double> > currentSampleX = xTrain;
        ArrayList<Integer> currentSampleY = yTrain;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader("datasets/australian_scale.txt"));
            String line = reader.readLine();
            
            while (line != null) {
                String [] tokens = line.split(" ");
                
                if (tokens[0].equals("+1")) {
                    currentSampleY.add(1);
                } else {
                    currentSampleY.add(0);
                }
                
                ArrayList<Double> row = new ArrayList<Double>();
                for(int i = 0; i < 14; ++i) row.add(0.0);
                
                for(int i = 1; i < tokens.length; ++i) {
                    String[] tmp = tokens[i].split(":");
                    int index = Integer.parseInt(tmp[0]);
                    double value = Double.parseDouble(tmp[1]);
                    row.set(index - 1, value);
                }
                
                currentSampleX.add(row);
                
                if (currentSampleX.size() == 345) {
                    currentSampleX = xTest;
                    currentSampleY = yTest;
                }
                
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            
        }
        
        RandomForest rf = new RandomForest(100);
        rf.fit(xTrain, yTrain);
        ArrayList<Double> probs = new ArrayList<Double>();
        
        int numOK = 0;
        for(int i = 0; i < xTest.size(); ++i) {
            int pClass = rf.predict(xTest.get(i));
            double predicted = rf.predict_proba(xTest.get(i), 1);
            probs.add(predicted);
            if (pClass == yTest.get(i)) {
                ++numOK;
            }
        }
        System.out.println(QualityMetrics.aucROC(probs, yTest) + " " + numOK);
        
    }
*/
}
