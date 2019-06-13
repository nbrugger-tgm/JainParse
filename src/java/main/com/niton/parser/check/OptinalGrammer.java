package com.niton.parser.check;

import java.util.ArrayList;

import com.niton.parser.GrammarObject;
import com.niton.parser.IgnoredGrammerObject;
import com.niton.parser.ParsingException;
import com.niton.parser.Tokenizer.AssignedToken;

/**
 * Cheks if the grammer is right if yes it adds the element to the output if not
 * it is ignored
 * 
 * @author Nils
 * @version 2019-05-29
 */
public class OptinalGrammer extends Grammar {
	private Grammar check;

	public OptinalGrammer(Grammar value, String name) {
		this.check = value;
		setName(name);
	}

	/**
	 * @see com.niton.parser.check.Grammar#process(java.util.ArrayList)
	 */
	@Override
	public GrammarObject process(ArrayList<AssignedToken> tokens) throws ParsingException {
		try {
			GrammarObject obj = check.check(tokens, index());
			if (obj == null)
				throw new ParsingException("");
			obj.setName(getName());
			index(check.index());
			return obj;
		} catch (Exception e) {
			return new IgnoredGrammerObject();
		}
	}
	

	/**
	 * @return the check
	 */
	public Grammar getCheck() {
		return check;
	}

	/**
	 * @param check the check to set
	 */
	public void setCheck(Grammar check) {
		this.check = check;
	}

	/**
	 * @see com.niton.parser.check.Grammar#getGrammarObjectType()
	 */
	@Override
	public Class<? extends GrammarObject> getGrammarObjectType() {
		return GrammarObject.class;
	}
}