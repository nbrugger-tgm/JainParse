package com.niton.jainparse.simplescript;

import com.niton.jainparse.simplescript.grammar.ExpressionGrammars;
import com.niton.parser.DefaultParser;
import com.niton.parser.exceptions.ParsingException;

import javax.swing.*;
import java.util.Scanner;

import static com.niton.jainparse.simplescript.grammar.ExpressionGrammars.expressionGrammars;
import static com.niton.jainparse.simplescript.grammar.GeneralGrammars.*;

public class SimpleScriptRunner {
    public static void main(String[] args) throws ParsingException {
        var parser = new DefaultParser(generalReference, statement);
        var script = JOptionPane.showInputDialog(null);
        System.out.println("Parse : " + script);
        System.out.printf("%s%n", parser.parse(script).reduce("expression").format());
    }
}
