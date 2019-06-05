package com.niton.parser.check;

import java.util.ArrayList;

import com.niton.parser.GrammarObject;
import com.niton.parser.IgnoredGrammerObject;
import com.niton.parser.ParsingException;
import com.niton.parser.Tokenizer.AssignedToken;

/**
 * This Grammar ignores the given grammar
 * 
 * @author Nils
 * @version 2019-05-29
 */
public class IgnoreGrammer extends Grammar {
	private Grammar grammar;

	public IgnoreGrammer(Grammar name2) {
		this.grammar = name2;
	}

	/**
	 * @see com.niton.parser.check.Grammar#process(java.util.ArrayList)
	 */
	@Override
	public GrammarObject process(ArrayList<AssignedToken> tokens) throws ParsingException {
		try {
			grammar.check(tokens, index());
			index(grammar.index());
		} catch (ParsingException e) {
		}
		return new IgnoredGrammerObject();
	}

	/**
	 * @see com.niton.parser.check.Grammar#getGrammarObjectType()
	 */
	@Override
	public Class<? extends GrammarObject> getGrammarObjectType() {
		return IgnoredGrammerObject.class;
	}
}
