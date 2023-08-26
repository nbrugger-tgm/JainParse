package com.niton.parser.grammar.types;

import com.niton.parser.ast.OptionalNode;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.WrapperGrammar;
import com.niton.parser.grammar.matchers.OptionalMatcher;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Cheks if the grammar is right if yes it adds the element to the output if not
 * it is ignored
 *
 * @author Nils
 * @version 2019-05-29
 */
@Getter
@Setter
public class OptionalGrammar extends WrapperGrammar<OptionalNode> {
    private Grammar<?> check;

	public OptionalGrammar(Grammar<?> grammarReferenceGrammar) {
		check = grammarReferenceGrammar;
	}


    @Override
    protected Grammar<?> copy() {
        return new OptionalGrammar(check);
    }

    /**
     * @see Grammar#createExecutor()
     */
    @Override
    public OptionalMatcher createExecutor() {
        return new OptionalMatcher(check);
    }


    @Override
    protected Stream<Grammar<?>> getWrapped() {
        return Stream.of(check);
    }
}
