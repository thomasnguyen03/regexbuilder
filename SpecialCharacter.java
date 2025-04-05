public class SpecialCharacter implements PatternComponent {
    private final String specialCharacter;

    public SpecialCharacter(String specialCharacter) {
        this.specialCharacter = "\\" +specialCharacter;
    }

    @Override
    public String getRegex() {
        return specialCharacter;
    }
}
