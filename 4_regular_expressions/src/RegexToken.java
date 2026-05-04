public record RegexToken(RegexTokenKind kind, String value) {
    @Override
    public String toString() {
        String payload = value == null ? "" : value;
        return kind + "(" + payload + ")";
    }
}
