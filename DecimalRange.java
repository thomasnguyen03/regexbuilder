import java.util.ArrayList;
import java.util.List;

public class DecimalRange {
    private int min;
    private int max;
    private boolean leading;
    public DecimalRange(int start, int end, boolean leadingZeros) {
        this.min = start;
        this.max = end;
        this.leading = leadingZeros;
    }
    public String getRegex(){

        return "";
    }

    public static RangePattern rangeToPattern(String start, String stop, boolean shorthand) {
        if (start.equals(stop)) {
            return new RangePattern(start, new int[0], 0);
        }

        List<char[]> zipped = zip(start, stop);
        int digits = zipped.size();
        StringBuilder pattern = new StringBuilder();
        int count = 0;

        for (int i = 0; i < digits; i++) {
            char startDigit = zipped.get(i)[0];
            char stopDigit = zipped.get(i)[1];

            if (startDigit == stopDigit) {
                pattern.append(startDigit);
            } else if (startDigit != '0' || stopDigit != '9') {
                pattern.append(toCharacterClass(startDigit, stopDigit, shorthand));
            } else {
                count++;
            }
        }

        while (count > 0) {
            pattern.append(shorthand ? "\\d" : "[0-9]");
            count--;
        }

        return new RangePattern(pattern.toString(), new int[]{count}, digits);
    }

    public static List<int[]> splitToRanges(int min, int max) {
        List<int[]> ranges = new ArrayList<>();

        int nines = fillWithNines(min);
        int zeros = fillWithZeros(max);

        // Forward scan (min to max)
        while (min <= zeros) {
            int stop = Math.min(max, nines);
            ranges.add(new int[]{min, stop});
            min = stop + 1;  // Only increment to the next valid range

            // Calculate the next 'nines' range boundary
            nines = fillWithNines(min);

            // Print for debugging
            //System.out.println("Forward scan - min: " + min + ", stop: " + stop + ", nines: " + nines);
        }

        // Backward scan (max to min)
        List<int[]> temp = new ArrayList<>();
        while (min <= max) {
            int start = fillWithZeros(max);
            temp.add(0, new int[]{Math.max(start, min), max});
            max = start - 1;

            // Print for debugging
            System.out.println("Backward scan - max: " + max + ", start: " + start);
        }

        ranges.addAll(temp);
        return ranges;
    }

    private static int fillWithNines(int num) {
        String s = String.valueOf(num);
        StringBuilder result = new StringBuilder();
        boolean foundNonNine = false;
        result.append(s.charAt(0));

        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (foundNonNine) {
                result.append('9');
            } else if (c != '9') {
                result.append('9');
                foundNonNine = true;
            } else {
                result.append(c);
            }
        }

        return Integer.parseInt(result.toString());
    }

    private static int fillWithZeros(int num) {
        String s = String.valueOf(num);
        StringBuilder result = new StringBuilder();
        boolean foundNonZero = false;
        result.append(s.charAt(0));

        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (foundNonZero) {
                result.append('0');
            } else if (c != '0') {
                result.append(c);
                foundNonZero = true;
            } else {
                result.append('0');
            }
        }

        return Integer.parseInt(result.toString());
    }

    public static String wrapPattern(String left, String right, boolean capture, boolean wrap) {
        String result = left + "|" + right;

        if (capture) {
            return "(" + result + ")";
        }

        if (!wrap) {
            return result;
        }

        return "(?:" + result + ")";
    }

    public static String toCharacterClass(char start, char stop, boolean shorthand) {
        if (start == stop) {
            return String.valueOf(start);
        }
        if (shorthand && start == '0' && stop == '9') {
            return "\\d";
        }
        return "[" + start + "-" + stop + "]";
    }


    public static List<char[]> zip(String a, String b) {
        int len = Math.max(a.length(), b.length());
        a = String.format("%" + len + "s", a).replace(' ', '0');
        b = String.format("%" + len + "s", b).replace(' ', '0');

        List<char[]> pairs = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            pairs.add(new char[]{ a.charAt(i), b.charAt(i) });
        }
        return pairs;
    }

}
