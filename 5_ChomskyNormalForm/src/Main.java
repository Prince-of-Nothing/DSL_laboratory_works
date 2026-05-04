public class Main {
    public static void main(String[] args) {
        Grammar grammar = buildSampleGrammar();
        CNFConverter converter = new CNFConverter();

        printStep("Original grammar", grammar);

        grammar = converter.eliminateEpsilon(grammar);
        printStep("After epsilon elimination", grammar);

        grammar = converter.eliminateUnit(grammar);
        printStep("After unit elimination", grammar);

        grammar = converter.eliminateInaccessible(grammar);
        printStep("After inaccessible symbol elimination", grammar);

        grammar = converter.eliminateNonProductive(grammar);
        printStep("After nonproductive symbol elimination", grammar);

        grammar = converter.toCNF(grammar);
        printStep("Final CNF grammar", grammar);

        System.out.println();
        System.out.println("CNF validation: " + converter.isCNF(grammar));
    }

    private static Grammar buildSampleGrammar() {
        Grammar grammar = new Grammar(
            java.util.List.of("S", "A", "B", "C", "D", "F", "X"),
            java.util.List.of("a", "b"),
            "S"
        );

        grammar.addProduction("S", "A", "B");
        grammar.addProduction("S", "b", "A");
        grammar.addProduction("S", "D");

        grammar.addProduction("A", "a");
        grammar.addProduction("A", "a", "S");
        grammar.addProduction("A");

        grammar.addProduction("B", "b");
        grammar.addProduction("B", "C");
        grammar.addProduction("B", "b", "B");

        grammar.addProduction("C", "A", "A");
        grammar.addProduction("C", "a", "B");

        grammar.addProduction("D", "A", "B");
        grammar.addProduction("D", "F");

        grammar.addProduction("F", "F", "F");
        grammar.addProduction("X", "a", "X");
        return grammar;
    }

    private static void printStep(String title, Grammar grammar) {
        System.out.println(title);
        System.out.println("-".repeat(title.length()));
        System.out.println(grammar.format());
        System.out.println();
    }
}
