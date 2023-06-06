package com.niton.parser.grammar.matchers;

import com.niton.parser.ast.AstNode;
import com.niton.parser.ast.SequenceNode;
import com.niton.parser.exceptions.ParsingException;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.GrammarMatcher;
import com.niton.parser.grammar.api.GrammarReference;
import com.niton.parser.token.TokenStream;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Checks the grammar as often as is ocures
 *
 * @author Nils
 * @version 2019-05-29
 */
@Getter
@Setter
public class RepeatMatcher extends GrammarMatcher<SequenceNode> {

    private Grammar<?> check;

    public RepeatMatcher(Grammar<?> expression) {
        this.check = expression;
    }


    /**
     * @param tokens
     * @param ref
     * @see GrammarMatcher#process(TokenStream, GrammarReference)
     */
    @Override
    public @NotNull SequenceNode process(@NotNull TokenStream tokens, @NotNull GrammarReference ref) throws ParsingException {
        SequenceNode obj = new SequenceNode();
        int resultIndex = 0;
        while (true) {
            try {
                var oldPos = tokens.index();
                AstNode gr = check.parse(tokens, ref);
                obj.name(String.valueOf(resultIndex++),gr);
                if (oldPos == tokens.index()) {
                    //Since the position did not change, the grammar did an empty match and the stream did not continue!
                    //This would lead to an infinite loop since it is a stalemate
                    break;
                }
            } catch (ParsingException e) {
                obj.setParsingException(e);
                break;
            }
        }
        return obj;
    }
}
