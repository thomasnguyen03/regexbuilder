public class OrComponent  implements PatternComponent {
    private final String left, right;

    public OrComponent(String left, String right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String getRegex() {
        return "(" + left + "|" + right + ")";
    }


}
