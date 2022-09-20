package com.niton.jainparse.simplescript;

import com.niton.parser.grammar.api.Grammar;

import java.util.Set;

import static com.niton.jainparse.simplescript.grammar.ExpressionGrammars.stringContent;

public class StringGrammarTests extends ComplexGrammarTest{
    @Override
    Grammar<?, ?> getGrammar() {
        return stringContent;
    }

    @Override
    Set<String> success() {
        return Set.of(
                "Hallo", "   "," \\\\ ","\\\\","\\\"","\\\\\\\"","Hallo \\\\ Welt", "+'kj \\\" <>>µ",""
        );
    }

    @Override
    Set<String> fail() {
        return Set.of(
                "\"", "  \\\\\" "
        );
    }
}
