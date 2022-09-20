package com.niton.jainparse.simplescript.grammar;

import com.niton.parser.grammar.GrammarReferenceMap;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.GrammarName;
import com.niton.parser.grammar.api.GrammarReference;
import com.niton.parser.token.DefaultToken;

import static com.niton.jainparse.simplescript.grammar.GeneralGrammars.*;
import static com.niton.jainparse.simplescript.grammar.NumericExpressionsGrammars.*;
import static com.niton.parser.token.DefaultToken.*;


public class ExpressionGrammars {
    public enum Expression implements GrammarName {
        NUMERIC,
        STRING,
        BOOLEAN,
        CALL,
        REFERENCE,
        GENERIC;

        @Override
        public String getName() {
            return name() + "_EXPRESSION";
        }
    }


    public static final Grammar<?, ?> stringContent = Grammar
            .tokenReference(BACK_SLASH).anyExcept()
            .then(escapedBackSlash.or(escapedQuote)).repeat()
            .then(Grammar.tokenReference(DOUBLEQUOTE).anyExcept());

    public static Grammar<?, ?> stringLiteral = Grammar.build(Expression.STRING)
            .token(DOUBLEQUOTE).add()
            .grammar(stringContent).add("content")
            .token(DOUBLEQUOTE).add()
            .get();

    public static final Grammar<?, ?> numericExpression = Grammar.anyOf(enclosedExpression, arithmeticExpression, number)
            .named(NumericExpression.GENERIC);

    public static final Grammar<?, ?> booleanKeyword = Grammar.anyOf(
            Grammar.keyword("true"),
            Grammar.keyword("false")
    );
    private static final Grammar<?, ?> comparisonExpression = Grammar.build("comparison")
            .grammars(stringLiteral, numericExpression, booleanKeyword, identifier).name("lhs")
            .token(WHITESPACE).ignore().add()
            .grammars(
                    Grammar.keyword("=="),
                    Grammar.keyword(">="),
                    Grammar.keyword("<="),
                    Grammar.keyword("!="),
                    Grammar.keyword(">"),
                    Grammar.keyword("<"),
                    Grammar.keyword("||"),
                    Grammar.keyword("&&")
            ).name("comparator")
            .token(WHITESPACE).ignore().add()
            .grammar(Expression.GENERIC).name("rhs")
            .get();

    public static final Grammar<?, ?> booleanExpression = Grammar.anyOf(
            comparisonExpression,
            booleanKeyword
    ).named(Expression.BOOLEAN);

    public static final Grammar<?, ?> expression = Grammar.anyOf(
            booleanExpression,
            stringLiteral,
            numericExpression,
            identifier
    ).named(Expression.GENERIC);

    public static final GrammarReferenceMap expressionGrammars = new GrammarReferenceMap()
            .map(expression).map(numericExpression);
}
