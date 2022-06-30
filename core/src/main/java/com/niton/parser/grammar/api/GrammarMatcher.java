package com.niton.parser.grammar.api;

import com.niton.parser.ast.AstNode;
import com.niton.parser.exceptions.ParsingException;
import com.niton.parser.token.TokenStream;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the logic how to handle a specific grammar. It creates a {@link AstNode} out of
 * tokens and marks them as used for the next Executor "consuming" them
 *
 * @author Nils Brugger
 * @version 2019-06-07
 */
public abstract class GrammarMatcher<T extends AstNode> {
	private       String  originGrammarName;

	/**
	 * same as {@link GrammarMatcher#parse(TokenStream, GrammarReference)} but only checks after the
	 * *pos*(parameter) token
	 *
	 * @return the parsed {@link AstNode}
	 *
	 * @throws ParsingException when the tokens are not parsable into this Grammar
	 * @author Nils
	 * @version 2019-05-29
	 */
	@NotNull
	public T parse(@NonNull TokenStream tokens,@NonNull GrammarReference reference) throws ParsingException {
		boolean root = tokens.level() == 0;
		try {
			tokens.elevate();
		}catch (IllegalStateException e) {
			throw new ParsingException(String.format("Parsing %s failed", originGrammarName), e);
		}
		T res;
		try {
			res = process(tokens, reference);
		} catch (ParsingException e) {
			tokens.rollback();
			throw e;
		}
		tokens.commit();
		res.setOriginGrammarName(getOriginGrammarName());
		if (root && tokens.index() + 1 < tokens.size()) {
			throw new ParsingException(
					"Not all tokens consumed at the end of parsing",
					res.getParsingExceptions()
			);
		}
		return res;
	}

	public String getOriginGrammarName() {
		return originGrammarName;
	}

	/**
	 * The parsing process itself use {@link TokenStream#next()} to iterate over the tokens.
	 * Behaviour contract:
	 * <ul>
	 *     <li>When parsing is successfull return the result</li>
	 *     <li>When parsing is not successfull throw a {@link ParsingException}</li>
	 * </ul>
	 * <b>Do not use {@link TokenStream#commit()} or {@link TokenStream#rollback()} unless you opened a new frame yourself</b>
	 *
	 * @param tokens    the tokens representing the tokenized string to parse
	 * @param reference the collection to get Grammars from
	 *
	 * @return the grammar object @NotNull
	 *
	 * @throws ParsingException if anything goes wrong in the parsing process
	 */
	@NotNull
	protected abstract T process(@NotNull TokenStream tokens,@NotNull GrammarReference reference)
			throws ParsingException;


	public GrammarMatcher<T> setOriginGrammarName(String originGrammarName) {
		this.originGrammarName = originGrammarName;
		return this;
	}

}

