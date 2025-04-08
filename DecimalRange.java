import java.util.*;

public class DecimalRange {
    private int min;
    private int max;
    private boolean leading;
    public DecimalRange(int start, int end, boolean leadingZeros) {
        this.min = start;
        this.max = end;
        this.leading = leadingZeros;
    }
    //inspiration from https://github.com/micromatch/to-regex-range
    public String getRegex(){
        if(!leading){
            if (Math.abs(min - max) == 1) {
                return "(" + min + "|" + max + ")";
            }

            List<RangePattern> positives = new ArrayList<>();
            List<RangePattern> negatives = new ArrayList<>();
            if(min < 0){
                int newMin = max < 0 ? Math.abs(max) : 1;
                negatives = splitToPattern(newMin, Math.abs(min));
                min = 0;
            }
            if (max >= 0){
                positives = splitToPattern(min, max);
            }

            return collatePatterns(negatives, positives);
        }
        else {
            return "";
        }

    }

    private String collatePatterns(
            List<RangePattern> neg,
            List<RangePattern> pos
    ) {
        // Filter for only negative patterns
        List<String> onlyNegative = filterPatterns(neg, pos, "-", false);

        // Filter for only positive patterns
        List<String> onlyPositive = filterPatterns(pos, neg, "", false);

        // Filter for intersected patterns
        List<String> intersected = filterPatterns(neg, pos, "-?", true);

        // Combine all filtered patterns
        List<String> subpatterns = new ArrayList<>();
        subpatterns.addAll(onlyNegative);
        subpatterns.addAll(intersected);
        subpatterns.addAll(onlyPositive);
        subpatterns.sort((s1, s2) -> Integer.compare(s2.length(), s1.length()));

        // Join with '|'
        return "("+String.join("|", subpatterns)+ ")";
    }

    private List<String> filterPatterns(List<RangePattern> arr, List<RangePattern> comparison, String prefix, boolean intersection){
        List<String> result = new ArrayList<>();

        for(RangePattern rp : arr){
            String pattern = rp.getOutput();
            if (!intersection && !contains(comparison, pattern)) {
                result.add(prefix + pattern);
            }

            if (intersection && contains(comparison, pattern)) {
                result.add(prefix + pattern);
            }
        }

        return result;
    }
    private boolean contains(List<RangePattern> list, String target) {
        for (RangePattern pattern : list) {
            if (pattern.getOutput().equals(target)) {
                return true;
            }
        }
        return false;
    }

    private List<RangePattern> splitToPattern(int min, int max) {
        int maxLength = String.valueOf(max).length();
        List<RangePattern> patterns = new ArrayList<>();
        List<Integer> ranges = splitToRanges(min, max);
        int start = min;
        RangePattern prev = null;

        for (int range : ranges) {
            //System.out.println(range);
            RangePattern current = rangeToPattern(String.valueOf(start), String.valueOf(range), false);

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
                String patternString = current.getPattern() + toQuantifier(current.getCount());
                current.setOutput(patternString);
                patterns.add(current);
                prev = current; // Update prev to current
            }
            start = range + 1;
        }

        return patterns;
    }

    private RangePattern rangeToPattern(String start, String stop, boolean shorthand) {
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

    private List<Integer> splitToRanges(int min, int max) {
        Set<Integer> ranges = new HashSet<>();
        ranges.add(max);
        int i = 1;
        int j = 1;
        int nines = fillWithNines(min,i);
        //System.out.println(nines);
        //System.out.println(zeros);

        while (min <= nines && nines <= max) {
            //System.out.println(nines);
            ranges.add(nines);
            i = i + 1;
            nines = fillWithNines(min, i);
        }


        int stop = fillWithZeros(max + 1, j) - 1;
        while (min < stop && stop <= max) {
            ranges.add(stop);
            j++;
            stop = fillWithZeros(max+1, j) - 1;

            // Print for debugging
            //System.out.println("Backward scan - max: " + max + ", start: " + start);
        }
        List<Integer> result = new ArrayList<>(ranges);
        Collections.sort(result);
        return result;
    }

    private int fillWithNines(int num, int exclude) {
        String s = String.valueOf(num);
        String part = s.substring(0, Math.max(s.length() - exclude,0));

        StringBuilder result = new StringBuilder();
        result.append(part);
        for (int i = 0; i < exclude; i++) {
            result.append('9');
        }

        return Integer.parseInt(result.toString());
    }

    private int fillWithZeros(int num, int exclude) {

        return (int)(num - (num % Math.pow(10, exclude)));
    }

//    private String wrapPattern(String left, String right, boolean capture, boolean wrap) {
//        String result = left + "|" + right;
//
//        if (capture) {
//            return "(" + result + ")";
//        }
//
//        if (!wrap) {
//            return result;
//        }
//
//        return "(?:" + result + ")";
//    }

    private String toCharacterClass(char start, char stop, boolean shorthand) {
        if (start == stop) {
            return String.valueOf(start);
        }
        if (shorthand && start == '0' && stop == '9') {
            return "\\d";
        }
        return "[" + start + "-" + stop + "]";
    }


    private List<char[]> zip(String a, String b) {
        int len = Math.max(a.length(), b.length());
        a = String.format("%" + len + "s", a).replace(' ', '0');
        b = String.format("%" + len + "s", b).replace(' ', '0');

        List<char[]> pairs = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            pairs.add(new char[]{ a.charAt(i), b.charAt(i) });
        }
        return pairs;
    }

    private String toQuantifier(int[] digits) {
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
    private static String padZeros(int value, int maxLength, boolean shorthand) {
        int diff = Math.abs(maxLength - String.valueOf(value).length());
        return switch (diff) {
            case 0 -> "";
            case 1 -> "0"; // One optional or required zero
            case 2 -> shorthand ? "0{2}" : "00"; // Two optional or required zeros
            default -> "0{" + diff + "}"; // More than two zeros
        };
    }
}
