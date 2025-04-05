public class Clause implements PatternComponent{
    private Integer min, max;
    private numberBase base;
    private boolean leadingZeros;

    // Constructor for numeric clauses
    public Clause(int min, int max, numberBase base, boolean leadingZeros) {
        this.min = min;
        this.max = max;
        this.base = base;
        this.leadingZeros = leadingZeros;
    }

    @Override
    public String getRegex() {
        return RegexNumberFactory.getBaseRegex(min, max, base.getBase(), leadingZeros);
    }

}
