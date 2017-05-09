
abstract class Feature implements Comparable{

}

class NumericalFeature extends Feature {
    private double value;

    NumericalFeature(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(value, ((NumericalFeature) o).getValue());
    }
}

class CategoricalFeature extends Feature {
    private int value;

    CategoricalFeature(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}