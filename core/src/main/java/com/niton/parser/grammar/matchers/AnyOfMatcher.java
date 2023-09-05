package com.niton.parser.grammar.matchers;

import com.niton.parser.ast.AstNode;
import com.niton.parser.ast.ParsingResult;
import com.niton.parser.ast.SwitchNode;
import com.niton.parser.exceptions.ParsingException;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.GrammarMatcher;
import com.niton.parser.grammar.api.GrammarReference;
import com.niton.parser.grammar.types.MultiGrammar;
import com.niton.parser.internal.Lazy;
import com.niton.parser.token.Location;
import com.niton.parser.token.TokenStream;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * Checks against all given Grammars syncron and returns the first matching
 *
 * @author Nils
 * @version 2019-05-29
 */
public class AnyOfMatcher extends GrammarMatcher<SwitchNode> {
    private final MultiGrammar grammars;

    public AnyOfMatcher(MultiGrammar grammers) {
        this.grammars = grammers;
    }

    /**
     * @param tokens
     * @param ref
     * @see GrammarMatcher#process(TokenStream, GrammarReference)
     */
    @Override
    public @NotNull ParsingResult<SwitchNode> process(@NotNull TokenStream tokens, @NotNull GrammarReference ref) {
        Map<String, ParsingResult<? extends AstNode>> results = new HashMap<>();
        for (var grammar : this.grammars.getGrammars()) {
            var result = grammar.parse(tokens, ref);
            results.put(grammar.getIdentifier(), result);
            if (!result.wasParsed()) {
                continue;
            }
            var node = result.unwrap();
            SwitchNode wrapper = new SwitchNode(node);
            var mappedExceptions = formatFails(results);
            if (mappedExceptions.length > 0) {
                wrapper.setParsingException(new ParsingException(getIdentifier(), String.format(
                        "Expected one of : [%s] and some were not parsable",
                        Arrays.stream(this.grammars.getGrammars()).map(Grammar::getIdentifier).collect(joining(", "))
                ), mappedExceptions));
            }
            return ParsingResult.ok(wrapper);
        }
        var identifiers = Arrays.stream(this.grammars.getGrammars()).map(Grammar::getIdentifier).collect(joining(", "));
        var allOptionsNamed = Arrays.stream(grammars.getGrammars()).allMatch(e -> e.getDisplayName() != null);
        if (allOptionsNamed) {
            var first = results.values().stream()
                    .filter(e -> !e.wasParsed())
                    .map(ParsingResult::exception)
                    .map(ParsingException::getLocation)
                    .map(Lazy::get)
                    .min(Location.byStart)
                    .orElseThrow();
            return ParsingResult.error(new ParsingException(
                    getIdentifier(),
                    "Expected one of: [" + identifiers + "]",
                    first
            ));
        }
        return ParsingResult.error(new ParsingException(
                getIdentifier(),
                "Expected one of : [" + identifiers + "] but none of them was parsable",
                !allOptionsNamed ? formatFails(results) : new ParsingException[0]
        ));
    }

    private ParsingException[] formatFails(Map<String, ParsingResult<? extends AstNode>> fails) {
        return fails.entrySet().stream().map(e -> {
            var parsed = e.getValue().wasParsed();
            ParsingException ex;
            if (parsed && (ex = e.getValue().unwrap().getParsingException()) != null) {
                return new ParsingException(getIdentifier(), e.getKey() + ": was parsed with allowed exception", ex);
            } else if (!parsed)
                return new ParsingException(getIdentifier(), e.getKey() + ": was not parsed", e.getValue().exception());
            else
                return null;
        }).filter(Objects::nonNull).toArray(ParsingException[]::new);
    }


    /**
     * @return the tokens
     */
    public Grammar<?>[] getGrammars() {
        return grammars.getGrammars();
    }
}
