import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Double ss = null;
        RandomForest d = new RandomForest();
        ArrayList<ArrayList<Double>> x = new ArrayList<ArrayList<Double>>();
        x.add(new ArrayList<Double>(Arrays.asList(0., 0.)));
        x.add(new ArrayList<Double>(Arrays.asList(0., 1.)));
        x.add(new ArrayList<Double>(Arrays.asList(1., 0.)));
        x.add(new ArrayList<Double>(Arrays.asList(1., 1.)));
        ArrayList<Integer> y = new ArrayList<Integer>(Arrays.asList(1, 0, 1, 1));

        d.fit(x, y);

        ArrayList<Double> example0 = new ArrayList<Double>(Arrays.asList(0., 0.));
        ArrayList<Double> example1 = new ArrayList<Double>(Arrays.asList(0., 1.));
        ArrayList<Double> example2 = new ArrayList<Double>(Arrays.asList(1., 0.));
        ArrayList<Double> example3 = new ArrayList<Double>(Arrays.asList(1., 1.));
        System.out.println(d.predict(example0));
        System.out.println(d.predict(example1));
        System.out.println(d.predict(example2));
        System.out.println(d.predict(example3));
    }
}
