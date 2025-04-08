public class RangePattern {
    public final String pattern;
    public final int[] count;
    public final int digits;
    private String output;

    public RangePattern(String pattern, int[] count, int digits) {
        this.pattern = pattern;
        this.count = count;
        this.digits = digits;
        this.output = "";
    }
    public String getPattern(){
        return pattern;
    }
    public int[] getCount(){
        return count;
    }
    public int getDigits(){
        return digits;
    }

    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output =output;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof RangePattern)) return false;
//        return !output.isEmpty() && output.equals(((RangePattern)obj).output);
//    }
}
