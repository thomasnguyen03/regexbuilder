public enum numberBase {
    BINARY(2),
    DECIMAL(10),
    HEX(16);

    private final int base;

    // Constructor
    numberBase(int base) {
        this.base = base;
    }

    // Method to get the base value
    public int getBase() {
        return base;
    }
}