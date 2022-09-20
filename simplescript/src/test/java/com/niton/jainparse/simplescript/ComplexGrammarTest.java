package com.niton.jainparse.simplescript;

import com.niton.parser.Parser;
import com.niton.parser.grammar.GrammarReferenceMap;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.token.TokenStream;
import com.niton.parser.token.Tokenizer;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Set;
import java.util.stream.Stream;

public abstract class ComplexGrammarTest {
    abstract Grammar<?, ?> getGrammar();

    abstract Set<String> success();

    abstract Set<String> fail();

    @TestFactory
    Stream<DynamicTest> tests() {
        var tokenizer = createTokenizer();
        return Stream.concat(success().stream().map(str -> DynamicTest.dynamicTest(
                "Parse correct : " + str,
                () -> getGrammar().parse(new TokenStream(tokenizer.tokenize(str)), new GrammarReferenceMap())
        )), fail().stream().map(str -> DynamicTest.dynamicTest(
                "Parse wrong   : " + str,
                () -> Assertions.assertThatThrownBy(() -> getGrammar().parse(new TokenStream(tokenizer.tokenize(str)), new GrammarReferenceMap()))
        )));
    }

    protected Tokenizer createTokenizer() {
        return new Tokenizer();
    }
}
