import java.util.List;

public class Main {
    private static final List<String> PROGRAMS = List.of(
        "(S|T)(U|V)W^*Y^[+]24",
        "L(M|N)O^[3]P*Q(2|3)",
        "R*S(T|U|V)W(X|Y|Z)^[2]"
    );

    public static void main(String[] args) {
        System.out.println("REGULAR EXPRESSIONS - VARIANT 4");
        System.out.println("=".repeat(60));

        for (String program : PROGRAMS) {
            System.out.println("\nPattern: " + program);
            List<String> samples = RegexGenerator.generateMany(program, 5);
            System.out.println("{" + String.join(", ", samples) + ", ...}");
        }
    }
}
