package com.niton.parser.tests;

import com.niton.parser.grammar.api.Grammar;
import com.niton.parser.grammar.types.ChainGrammar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class GrammarTest {
	@Test
	void disallowDirectRecursion() {
		var grm = Grammar.build("test-grm");
		grm.setDirectRecursion(false);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammar("test-grm").add("content");
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammar("test-grm").anyExcept().add("content");
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammar("test-grm").ignore().add("content");
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammar("test-grm").optional().add("content");
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammar("test-grm").repeat().add("content");
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammars("test-grm").anyExcept().add("content");
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			grm.grammars("test-grm").add("content");
		});
	}
}