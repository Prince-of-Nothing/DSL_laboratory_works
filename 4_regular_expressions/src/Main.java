import java.util.List;
import java.util.regex.Pattern;

public class Main {
    private static final List<String> PROGRAMS = List.of(
        "(S|T)(U|V)W^*Y^[+]24",
        "L(M|N)O^[3]P*Q(2|3)",
        "R*S(T|U|N)W(X|Y|Z)^[2]"
    );

    public static void main(String[] args) {
        System.out.println("REGULAR EXPRESSIONS - VARIANT 4");
        System.out.println("=".repeat(60));

        for (String program : PROGRAMS) {
            System.out.println("\nPattern: " + program);
            Pattern validator = Pattern.compile(toJavaRegex(program));
            for (String sample : RegexGenerator.generateMany(program, 5)) {
                boolean ok = validator.matcher(sample).matches();
                System.out.printf("  %-20s %s%n", sample, ok ? "OK" : "FAIL");
            }
        }

        System.out.println("\nProcessing trace for the first pattern:");
        System.out.println("-".repeat(60));
        System.out.println(RegexGenerator.trace(PROGRAMS.getFirst()));
    }

    private static String toJavaRegex(String customRegex) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < customRegex.length(); index++) {
            char current = customRegex.charAt(index);
            if (current != '^') {
                builder.append(current);
                continue;
            }

            if (index + 1 >= customRegex.length()) {
                throw new IllegalArgumentException("Dangling '^' in regex: " + customRegex);
            }

            char next = customRegex.charAt(++index);
            if (next == '[') {
                if (index + 1 >= customRegex.length()) {
                    throw new IllegalArgumentException("Unclosed '^[...]' in regex: " + customRegex);
                }

                StringBuilder body = new StringBuilder();
                while (index + 1 < customRegex.length() && customRegex.charAt(index + 1) != ']') {
                    body.append(customRegex.charAt(++index));
                }

                if (index + 1 >= customRegex.length() || customRegex.charAt(index + 1) != ']') {
                    throw new IllegalArgumentException("Unclosed '^[...]' in regex: " + customRegex);
                }
                index++;

                String bodyText = body.toString();
                if (bodyText.equals("*") || bodyText.equals("+") || bodyText.equals("?")) {
                    builder.append(bodyText);
                    continue;
                }
                if (bodyText.chars().allMatch(Character::isDigit)) {
                    builder.append('{').append(bodyText).append('}');
                    continue;
                }
                throw new IllegalArgumentException("Unsupported '^[...]' quantifier in regex: " + customRegex);
            }

            if (next == '*' || next == '+' || next == '?') {
                builder.append(next);
                continue;
            }

            if (Character.isDigit(next)) {
                StringBuilder count = new StringBuilder().append(next);
                while (index + 1 < customRegex.length() && Character.isDigit(customRegex.charAt(index + 1))) {
                    count.append(customRegex.charAt(++index));
                }
                builder.append('{').append(count).append('}');
                continue;
            }

            throw new IllegalArgumentException("Unsupported '^' quantifier in regex: " + customRegex);
        }
        return builder.toString();
    }
}
