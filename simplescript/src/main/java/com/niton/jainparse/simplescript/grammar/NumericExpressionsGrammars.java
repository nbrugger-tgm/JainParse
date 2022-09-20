package com.niton.jainparse.simplescript.grammar;

import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.GrammarName;

import static com.niton.jainparse.simplescript.grammar.NumericExpressionsGrammars.NumericExpression.FORMULA;
import static com.niton.parser.grammar.api.Grammar.tokenReference;
import static com.niton.parser.token.DefaultToken.*;
import static com.niton.parser.token.DefaultToken.MINUS;

public class NumericExpressionsGrammars {
    public enum NumericExpression implements GrammarName {
        ENCLOSED,
        GENERIC,
        FORMULA,
        VALUE;

        @Override
        public String getName() {
            return "NUMERIC_" + name();
        }
    }
    public static final Grammar<?, ?> number = Grammar.build(NumericExpression.VALUE)
            .tokens(PLUS, MINUS).optional().name("sign")
            .token(WHITESPACE).ignore().add()
            .token(NUMBER).name("")
            .grammar(tokenReference(POINT).then(tokenReference(NUMBER),"fractional")).optional().name("decimal")
            .get();

    public static final Grammar<?, ?> enclosedExpression = Grammar.build(NumericExpression.ENCLOSED)
            .token(BRACKET_OPEN).add()
            .token(WHITESPACE).ignore().add()
            .grammar(NumericExpression.GENERIC).name("innerExpression")
            .token(WHITESPACE).ignore().add()
            .token(BRACKET_CLOSED).add()
            .get();
    public static final Grammar<?, ?> multiplicativeOperation = Grammar.build("multiplicative_operation")
            .tokens(STAR, SLASH).name("operator")
            .token(WHITESPACE).ignore().add()
            .grammars(enclosedExpression, number).name("expression")
            .get();
    public static final Grammar<?, ?> additiveOperation = Grammar.build("additive_operation")
            .tokens(PLUS, MINUS).name("operator")
            .token(WHITESPACE).ignore().add()
            .grammar(NumericExpression.GENERIC).name("expression")
            .get();
    public static final Grammar<?, ?> arithmeticOperation = multiplicativeOperation.or(additiveOperation);
    public static final Grammar<?, ?> arithmeticExpression = Grammar.build(NumericExpression.FORMULA)
            .grammars(enclosedExpression, number).add("value")
            .token(WHITESPACE).ignore().add()
            .grammar(arithmeticOperation).repeat().add("operators")
            .get();
}
