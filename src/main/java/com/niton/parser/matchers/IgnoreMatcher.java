package com.niton.parser.matchers;

import com.niton.parser.Grammar;
import com.niton.parser.GrammarMatcher;
import com.niton.parser.GrammarReference;
import com.niton.parser.GrammarResult;
import com.niton.parser.exceptions.ParsingException;
import com.niton.parser.result.IgnoredGrammarResult;
import com.niton.parser.token.TokenStream;

/**
 * This Grammar ignores the given grammar
 *
 * @author Nils
 * @version 2019-05-29
 */
public class IgnoreMatcher extends GrammarMatcher<IgnoredGrammarResult> {
	private Grammar grammar;

	public IgnoreMatcher(Grammar name2) {
		this.grammar = name2;
	}

	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	@Override
	public ParsingException getLastException() {
		return new ParsingException("Noting to ignore");
	}

	/**
	 * @see GrammarMatcher#process(TokenStream, GrammarReference)
	 */
	@Override
	public IgnoredGrammarResult process(TokenStream tokens, GrammarReference ref)
	throws ParsingException {
		IgnoredGrammarResult thisRes = new IgnoredGrammarResult();
		try {
			GrammarResult res = grammar.parse(tokens, ref);
			thisRes.getIgnored().addAll(res.join());
			tokens.commit();
		} catch (ParsingException e) {
			tokens.rollback();
		}
		return thisRes;
	}
}
