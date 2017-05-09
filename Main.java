import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Random rr = new Random();
        Double ss = null;
        DecisionTree d = new DecisionTree();
        ArrayList<ArrayList<Feature>> x = new ArrayList<ArrayList<Feature>>();
        /*
        x.add(new ArrayList<>(Arrays.asList(new CategoricalFeature(1))));
        x.add(new ArrayList<>(Arrays.asList(new CategoricalFeature(2))));
        x.add(new ArrayList<>(Arrays.asList(new CategoricalFeature(3))));
        x.add(new ArrayList<>(Arrays.asList(new CategoricalFeature(4))));*/
        ArrayList<Integer> y = new ArrayList<Integer>(); //Arrays.asList(1, 1, 1, 0));
        ArrayList<Double> w = new ArrayList<>(); // Arrays.asList(1., 1., 1., 2.9));
        for (int i = 0; i < 1000; ++i) {

            if ((i & 1) == 1) {
                //ArrayList<Feature> c = new ArrayList<>()
                x.add(new ArrayList<Feature>(Arrays.asList(
                        new NumericalFeature(rr.nextDouble() * 2 + 1), new NumericalFeature(rr.nextDouble() * 2 + 1))));
                w.add(1.);
                y.add(1);
            } else {
                x.add(new ArrayList<Feature>(Arrays.asList(
                        new NumericalFeature(rr.nextDouble() * 2), new NumericalFeature(rr.nextDouble() * 2))));
                w.add(1.);
                y.add(0);
            }
        }

        d.fit(x, w, y);
        String serialized = d.toString();
        System.out.println(d.toString());

        DecisionTree d2 = new DecisionTree();
        d2.fromString(serialized);

        System.out.println(d2.toString());
        /*
        System.out.println(d.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(1)))));
        System.out.println(d.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(2)))));
        System.out.println(d.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(3)))));
        System.out.println(d.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(4)))));
        System.out.println(d2.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(1)))));
        System.out.println(d2.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(2)))));
        System.out.println(d2.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(3)))));
        System.out.println(d2.predict(new ArrayList<Feature>(Arrays.asList(new CategoricalFeature(4)))));
        /*if (1 == 1) return;
*/
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                double q1 = 0.3 * i;
                double q2 = 0.3 * j;
                ArrayList<Feature> example0 = new ArrayList<Feature>(Arrays.asList(new NumericalFeature(q1), new NumericalFeature(q2)));
                System.out.print(d.predict(example0) + ", " + d2.predict(example0) + "\t");
            }
            System.out.println("");
        }
    }

}
