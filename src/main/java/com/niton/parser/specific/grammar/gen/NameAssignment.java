package com.niton.parser.specific.grammar.gen;

import com.niton.parser.ResultResolver;
import com.niton.parser.result.SuperGrammarResult;

public class NameAssignment {
	private final SuperGrammarResult result;

	public NameAssignment(SuperGrammarResult res) {
		this.result = res;
	}

	public String getName() {
		if (result.getObject("name") == null) {
			return null;
		}
		return ((String) ResultResolver.getReturnValue(result.getObject("name")));
	}
}
