import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<RangePattern> splitToPattern(int min, int max, boolean leadingZeros) {
        int maxLength = String.valueOf(max).length();
        List<RangePattern> patterns = new ArrayList<>();
        List<int[]> ranges = splitToRanges(min, max);
        int start = min;
        RangePattern prev = null;

        for (int[] range : ranges) {
            int rangeMax = range[1];
            RangePattern current = rangeToPattern(String.valueOf(start), String.valueOf(rangeMax), false);

            if (prev != null && prev.getPattern().equals(current.getPattern())) {
                int[] prevCounts = prev.getCount();
                if (prevCounts.length > 1) {
                    prevCounts = Arrays.copyOf(prevCounts, prevCounts.length - 1); // Remove the last count
                }
                prevCounts = Arrays.copyOf(prevCounts, prevCounts.length + 1);
                prevCounts[prevCounts.length - 1] = current.getCount()[0];
                prev.setOutput(prev.pattern + toQuantifier(prevCounts));
                patterns.set(patterns.size() - 1, prev);
            } else {
                // Create a new RangePattern
                String zeros = leadingZeros ? padZeros(rangeMax, maxLength, leadingZeros) : "";
                String patternString = zeros + current.getPattern() + toQuantifier(current.getCount());
                current.setOutput(patternString);
                patterns.add(current);
                prev = current; // Update prev to current
            }
            start = rangeMax + 1;
        }

        return patterns;
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

        if (count > 0) {
            pattern.append(shorthand ? "\\d" : "[0-9]");
        }

        return new RangePattern(pattern.toString(), new int[]{count}, digits);
    }

    public static List<int[]> splitToRanges(int min, int max) {
        List<int[]> ranges = new ArrayList<>();

        int i = 1;
        int j = 1;
        int nines = fillWithNines(min,i);
        int zeros = fillWithZeros(max,j);
        //System.out.println(nines);
        //System.out.println(zeros);

        // Forward scan (min to max)
        while (min <= zeros) {
            //System.out.println(nines);
            int stop = Math.min(max, nines);
            ranges.add(new int[]{min, stop});
            min = stop + 1;  // Only increment to the next valid range
            if(String.valueOf(min).length()> String.valueOf(stop).length()){
                nines = fillWithNines(min,i);
                i++;
            }
            // Calculate the next 'nines' range boundary
            else {
                i++;
                nines = fillWithNines(min,i);
            }

            // Print for debugging
            //System.out.println("Forward scan - min: " + min + ", stop: " + stop + ", nines: " + nines);
        }

        // Backward scan (max to min)
        List<int[]> temp = new ArrayList<>();
        while (min <= max) {
            int start = fillWithZeros(max,j);
            temp.add(0, new int[]{Math.max(start, min), max});
            max = start - 1;

            // Print for debugging
            //System.out.println("Backward scan - max: " + max + ", start: " + start);
        }

        ranges.addAll(temp);
        return ranges;
    }

    private static int fillWithNines(int num, int exclude) {
        String s = String.valueOf(num);
        exclude = Math.min(exclude, s.length());
        String part = s.substring(0, s.length() - exclude);

        StringBuilder result = new StringBuilder();
        result.append(part);
        for (int i = 0; i < exclude; i++) {
            result.append('9');
        }

        return Integer.parseInt(result.toString());
    }

    private static int fillWithZeros(int num, int exclude) {

        return (int)(num - (num % Math.pow(10, exclude)));
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

    public static String toQuantifier(int[] digits) {
        // Extract start and stop values from the input array with default values
        int start = digits.length > 0 ? digits[0] : 0; // Default to 0 if array is empty
        Integer stop = digits.length > 1 ? digits[1] : null; // Use null if no second value

        // Check if quantifier is needed
        if (stop != null || start > 1) {
            // Generate the quantifier string
            return "{" + start + (stop != null ? "," + stop : "") + "}";
        }

        // Return an empty string if no quantifier is needed
        return "";
    }
    public static String padZeros(int value, int maxLength, boolean shorthand) {
        int diff = Math.abs(maxLength - String.valueOf(value).length());
        return switch (diff) {
            case 0 -> "";
            case 1 -> shorthand ? "0?" : "0"; // One optional or required zero
            case 2 -> shorthand ? "0{0,2}" : "00"; // Two optional or required zeros
            default -> shorthand ? "0{0," + diff + "}" : "0{" + diff + "}"; // More than two zeros
        };
    }
}
