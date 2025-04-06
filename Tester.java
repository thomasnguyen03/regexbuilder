import java.util.Arrays;

public class Tester {
    public static void main(String[] args) {
        RangePattern rp = DecimalRange.rangeToPattern("1400", "1999", false);
        System.out.println(rp.pattern);

        for (int[] range : DecimalRange.splitToRanges(40, 1222)) {
            System.out.println(Arrays.toString(range));
        }
    }
}
