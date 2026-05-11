public class Main {
    public static void main(String[] args) {
        Grammar grammar = buildVariant20Grammar();
        CNFConverter converter = new CNFConverter();

        // Compute final CNF first and show it as a preview
        Grammar cnfPreview = converter.toCNF(
            converter.eliminateNonProductive(
            converter.eliminateInaccessible(
            converter.eliminateUnit(
            converter.eliminateEpsilon(grammar.copy())))));
        printStep("Final CNF grammar (preview)", cnfPreview);

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

    private static Grammar buildVariant20Grammar() {
        Grammar grammar = new Grammar(
            java.util.List.of("S", "A", "B", "C", "D"),
            java.util.List.of("a", "b"),
            "S"
        );

        grammar.addProduction("S", "a", "B");
        grammar.addProduction("S", "b", "A");
        grammar.addProduction("S", "A");

        grammar.addProduction("A", "B");
        grammar.addProduction("A", "S", "a");
        grammar.addProduction("A", "b", "B", "A");
        grammar.addProduction("A", "b");

        grammar.addProduction("B", "b");
        grammar.addProduction("B", "b", "S");
        grammar.addProduction("B", "a", "D");
        grammar.addProduction("B");

        grammar.addProduction("D", "A", "A");
        grammar.addProduction("C", "B", "a");
        return grammar;
    }

    private static void printStep(String title, Grammar grammar) {
        System.out.println(title);
        System.out.println("-".repeat(title.length()));
        System.out.println(grammar.format());
    }
}
