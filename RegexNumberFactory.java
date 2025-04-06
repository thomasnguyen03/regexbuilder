public class RegexNumberFactory {
    public static String getBaseRegex(int min, int max, int base, boolean leadingZeros) {
        if (base == 10) {
            DecimalRange decimalRange = new DecimalRange(min, max, leadingZeros);
            return decimalRange.getRegex();
        }
        return "";
    }

}
