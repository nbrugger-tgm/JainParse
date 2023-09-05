package com.niton.parser.grammar.matchers;

import com.niton.parser.ast.ParsingResult;
import com.niton.parser.exceptions.ParsingException;
import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.api.GrammarMatcher;
import com.niton.parser.grammar.api.GrammarReference;
import com.niton.parser.ast.OptionalNode;
import com.niton.parser.token.TokenStream;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Cheks if the grammar is right if yes it adds the element to the output if not
 * it is ignored
 *
 * @author Nils
 * @version 2019-05-29
 */
@Getter
@Setter
public class OptionalMatcher extends GrammarMatcher<OptionalNode> {

	private Grammar<?> check;

	public OptionalMatcher(Grammar<?> value) {
		this.check = value;
	}


	/**
	 * @see GrammarMatcher#process(TokenStream, GrammarReference)
	 * @param tokens
	 * @param ref
	 */
	@Override
	public @NotNull ParsingResult<OptionalNode> process(@NotNull TokenStream tokens, @NotNull GrammarReference ref) {
		var node = new OptionalNode();
		check.parse(tokens, ref).ifParsedOrElse(
				node::setValue,
				node::setParsingException
		);
		return ParsingResult.ok(node);
	}

}
