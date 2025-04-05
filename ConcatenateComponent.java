public class ConcatenateComponent implements  PatternComponent{
    private final String left, right;

    public ConcatenateComponent(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public String getRegex() {
        return "(" + left + ")" + "("+ right + ")";
    }
}
