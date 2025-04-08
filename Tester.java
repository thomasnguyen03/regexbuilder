import java.util.Arrays;
import java.util.List;

public class Tester {
    public static void main(String[] args) {
//        RangePattern rp = DecimalRange.rangeToPattern("1400", "1999", false);
//        System.out.println(rp.pattern);
//        System.out.println(Arrays.toString(rp.getCount()));
//
//        for (int range : DecimalRange.splitToRanges(40, 240)) {
//            System.out.println(range);
//        }
//        List<RangePattern> patterns = DecimalRange.splitToPattern(25, 450, false);
//        for (RangePattern pattern : patterns) {
//            System.out.println(pattern.getOutput());
//        }
        DecimalRange dr = new DecimalRange(-4, 15, false);
        System.out.println(dr.getRegex());

//        PatternBuilder pb = new PatternBuilder()
//                .add(new Clause(0, 255, numberBase.DECIMAL, false))  // Matches 0-255 without leading zeros
//                .add(new SpecialCharacter("."))
//                .add(new Clause(0, 255, numberBase.DECIMAL, false))
//                .add(new SpecialCharacter("."))
//                .add(new Clause(0, 255, numberBase.DECIMAL, false))
//                .add(new SpecialCharacter("."))
//                .add(new Clause(0, 255, numberBase.DECIMAL, false));
//
//        String output = pb.toString();
//
//        System.out.println(output);

    }
}
