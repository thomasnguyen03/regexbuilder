import java.util.Arrays;
import java.util.List;

public class Tester {
    public static void main(String[] args) {
//        RangePattern rp = DecimalRange.rangeToPattern("1400", "1999", false);
//        System.out.println(rp.pattern);
//        System.out.println(Arrays.toString(rp.getCount()));
//
//        for (int[] range : DecimalRange.splitToRanges(40, 1222)) {
//            System.out.println(Arrays.toString(range));
//        }
        List<RangePattern> patterns = DecimalRange.splitToPattern(40, 1999, true);
        for (RangePattern pattern : patterns) {
            System.out.println(pattern.getOutput());
        }
    }
}
