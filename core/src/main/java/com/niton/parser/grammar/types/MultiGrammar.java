package com.niton.parser.grammar.types;

import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.matchers.AnyOfMatcher;
import com.niton.parser.ast.AnyNode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Checks agains all given Grammars syncron and returns the first matching
 *
 * @author Nils
 * @version 2019-05-29
 */
@Getter
@Setter
public class MultiGrammar extends Grammar<AnyOfMatcher, AnyNode> {
	private Grammar<?,?>[] grammars;

	public MultiGrammar(Grammar<?,?>[] grammars) {
		this.grammars = grammars;
	}

	/**
	 * @see Grammar#createExecutor()
	 */
	@Override
	public AnyOfMatcher createExecutor() {
		return new AnyOfMatcher(this);
	}

	@Override
	public void reconfigMatcher(@NotNull AnyOfMatcher multiMatcher) {
		multiMatcher.setGrammars(this);
	}
}