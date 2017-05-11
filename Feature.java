
abstract class Feature implements Comparable{

}

class NumericalFeature extends Feature {
    private Double value;

    NumericalFeature(Double value) {
        this.value = value;
    }

    Double getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        if (value == null)
            return -1;
        if (((NumericalFeature) o).getValue() == null)
            return 1;
        return Double.compare(value, ((NumericalFeature) o).getValue());
    }
}

class CategoricalFeature extends Feature {
    private Integer value;

    CategoricalFeature(Integer value) {
        this.value = value;
    }

    Integer getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        if (value == null)
            return -1;
        if (((CategoricalFeature) o).getValue() == null)
            return 1;
        return Integer.compare(value, ((CategoricalFeature) o).getValue());
    }
}
