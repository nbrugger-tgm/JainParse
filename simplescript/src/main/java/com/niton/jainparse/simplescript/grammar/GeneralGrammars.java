package com.niton.jainparse.simplescript.grammar;

import com.niton.parser.grammar.GrammarReferenceMap;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.GrammarReference;
import com.niton.parser.grammar.types.TokenGrammar;
import com.niton.parser.token.DefaultToken;

import static com.niton.jainparse.simplescript.grammar.ExpressionGrammars.*;
import static com.niton.parser.grammar.api.Grammar.keyword;
import static com.niton.parser.grammar.api.Grammar.tokenReference;
import static com.niton.parser.token.DefaultToken.*;

public class GeneralGrammars {
    public static final Grammar<?, ?> identifier = Grammar.build("identifier")
            .token(DefaultToken.LETTERS).add()
            .tokens(DefaultToken.LETTERS, DefaultToken.NUMBER, DefaultToken.UNDERSCORE).repeat().add()
            .get();
    public static final Grammar<?, ?> escapedBackSlash = keyword("\\\\");
    public static final Grammar<?, ?> escapedQuote = keyword("\\\"");

    public static final Grammar<?, ?> variableDefinition = keyword("var").or(keyword("let")).or(keyword("const"))
            .then(tokenReference(WHITESPACE).ignore())
            .then(identifier, "variable_name")
            .then(tokenReference(WHITESPACE).ignore())
            .then(tokenReference(EQUAL))
            .then(tokenReference(WHITESPACE).ignore())
            .then(expression, "variable_value")
            .then(tokenReference(WHITESPACE).ignore())
            .then(tokenReference(SEMICOLON)).named("var_definition");


    public static final Grammar<?, ?> variableAssignment = Grammar.build("assignment")
            .grammar(identifier).add("variable_name")
            .token(WHITESPACE).ignore().add()
            .token(EQUAL).add()
            .token(WHITESPACE).ignore().add()
            .grammar(expression).add("variable_value")
            .token(WHITESPACE).ignore().add()
            .token(SEMICOLON).add()
            .get();


    public static final Grammar<?, ?> printCall = keyword("print")
            .then(tokenReference(BRACKET_OPEN))
            .then(tokenReference(WHITESPACE).ignore())
            .then(expression, "parameter")
            .then(tokenReference(WHITESPACE).ignore())
            .then(tokenReference(BRACKET_CLOSED))
            .then(tokenReference(SEMICOLON)).named("print_call");

    public static final Grammar<?, ?> statement = Grammar.anyOf(
            printCall,
            variableAssignment,
            variableDefinition,
            Grammar.reference("if")
    ).named("statement");
    public static final Grammar<?, ?> codeBlock = tokenReference(ROUND_BRACKET_OPEN)
            .then(tokenReference(WHITESPACE).ignore())
            .then(statement.then(tokenReference(WHITESPACE).or(tokenReference(NEW_LINE)).ignore()).repeat(),"statements")
            .then(tokenReference(WHITESPACE).ignore())
            .then(tokenReference(ROUND_BRACKET_CLOSED))
            .named("code_block");

    public static final Grammar<?, ?> ifCondition = keyword("if")
            .then(tokenReference(WHITESPACE).ignore())
            .then(tokenReference(BRACKET_OPEN))
            .then(tokenReference(WHITESPACE).ignore())
            .then(booleanExpression,"condition")
            .then(tokenReference(WHITESPACE).ignore())
            .then(tokenReference(BRACKET_CLOSED))
            .then(tokenReference(WHITESPACE).or(tokenReference(NEW_LINE)).ignore())
            .then(codeBlock,"code")
            .named("if");

    public static final GrammarReference generalReference = new GrammarReferenceMap()
            .map(ifCondition)
            .map(statement)
            .merge(expressionGrammars);
}
