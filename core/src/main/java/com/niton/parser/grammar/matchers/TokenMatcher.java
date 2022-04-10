package com.niton.parser.grammar.matchers;

import com.niton.parser.exceptions.ParsingException;
import com.niton.parser.grammar.api.GrammarMatcher;
import com.niton.parser.grammar.api.GrammarReference;
import com.niton.parser.grammar.types.TokenGrammar;
import com.niton.parser.ast.TokenNode;
import com.niton.parser.token.TokenStream;
import com.niton.parser.token.Tokenizer.AssignedToken;
import org.jetbrains.annotations.NotNull;

/**
 * Simply matches a token
 *
 * @author Nils
 * @version 2019-05-28
 */
public class TokenMatcher extends GrammarMatcher<TokenNode> {
	private TokenGrammar     tokenName;

	/**
	 * Creates an Instance of TokenGrammar.java
	 *
	 * @param tokenName
	 *
	 * @author Nils
	 * @version 2019-05-28
	 */
	public TokenMatcher(TokenGrammar tokenName) {
		this.tokenName = tokenName;
	}

	@Override
	public @NotNull TokenNode process(@NotNull TokenStream tokens, @NotNull GrammarReference ref)
			throws ParsingException {
		AssignedToken token = tokens.next();
		if (token.getName().equals(tokenName.getTokenName())) {
			TokenNode obj = new TokenNode();
			obj.tokens.add(token);
			return obj;
		}
		throw new ParsingException(String.format(
				"%s Expected Token \"%s\" but actual value was  \"%s\" (index : %d, char: %d) -> [%s]",
				this.tokenName,
				tokenName.getTokenName(),
				token.getName(),
				tokens.index(),
				token.getStart(),
				token.getValue()
		));
	}

	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName.getTokenName();
	}

	/**
	 * @param tokenName the tokenName to set
	 */
	public void setTokenName(TokenGrammar tokenName) {
		this.tokenName = tokenName;
	}
}
