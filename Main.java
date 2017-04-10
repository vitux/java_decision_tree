import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        DecisionTree d = new DecisionTree();
        ArrayList<ArrayList<Double>> x = new ArrayList<ArrayList<Double>>();
        x.add(new ArrayList<Double>(Arrays.asList(1., 2., 3.)));
        x.add(new ArrayList<Double>(Arrays.asList(10., 20., 30.)));
        ArrayList<Integer> y = new ArrayList<Integer>(Arrays.asList(1, 0));

        d.learn(x, y);

        ArrayList<Double> example0 = new ArrayList<Double>(Arrays.asList(1., 2., 3.));
        ArrayList<Double> example1 = new ArrayList<Double>(Arrays.asList(10., -2., 43.));
        System.out.println(d.predict(example0));
        System.out.println(d.predict(example1));
    }
}
