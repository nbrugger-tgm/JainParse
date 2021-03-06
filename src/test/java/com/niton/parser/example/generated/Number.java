package com.niton.parser.example.generated;

import com.niton.parser.ResultResolver;
import com.niton.parser.result.SuperGrammarResult;

public class Number {
	private SuperGrammarResult result;

	public Number(SuperGrammarResult res) {
		this.result = res;
	}

	public String getValue() {
		if (result.getObject("value") == null) {
			return null;
		}
		return ((String) ResultResolver.getReturnValue(result.getObject("value")));
	}
}
