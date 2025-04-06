import java.util.Arrays;

public class Tester {
    public static void main(String[] args) {
        RangePattern rp = DecimalRange.rangeToPattern("127", "129", false);
        System.out.println(rp.pattern);

        for (int[] range : DecimalRange.splitToRanges(127, 224)) {
            System.out.println(Arrays.toString(range));
        }
    }
}
