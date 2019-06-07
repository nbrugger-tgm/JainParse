package com.niton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.lang.model.element.Modifier;

import com.niton.parser.GrammarObject;
import com.niton.parser.SubGrammerObject;
import com.niton.parser.TokenGrammarObject;
import com.niton.parser.check.ChainGrammer;
import com.niton.parser.check.Grammar;
import com.niton.parser.check.GrammarMatchGrammer;
import com.niton.parser.check.IgnoreGrammer;
import com.niton.parser.check.IgnoreTokenGrammer;
import com.niton.parser.check.MultiGrammer;
import com.niton.parser.check.OptinalGrammer;
import com.niton.parser.check.RepeatGrammer;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

/**
 * This is the JPGenerator Class
 * 
 * @author Nils Brugger
 * @version 2019-06-04
 */
public class JPGenerator {
	private String pack;
	private String path;
	private boolean returnTokens = false;
	/**
	 * @param returnTokens the returnTokens to set
	 */
	public void setReturnTokens(boolean returnTokens) {
		this.returnTokens = returnTokens;
	}
	/**
	 * @return the returnTokens
	 */
	public boolean isReturningTokens() {
		return returnTokens;
	}
	public JPGenerator(String pack, String path) {
		super();
		this.pack = pack;
		this.path = path;
	}

	public JPGenerator() {
		this(null, null);
	}

	/**
	 * @return the pack
	 */
	public String getPack() {
		return pack;
	}

	/**
	 * @param pack the pack to set
	 */
	public void setPack(String pack) {
		this.pack = pack;
	}

	public void generate(ChainGrammer g) throws IOException {
		FieldSpec bjectField = FieldSpec.builder(SubGrammerObject.class, "obj").addModifiers(Modifier.PRIVATE).build();
		MethodSpec constructor = MethodSpec.constructorBuilder().addStatement("this.obj = obj")
				.addParameter(SubGrammerObject.class, "obj").addModifiers(Modifier.PUBLIC).build();
		LinkedList<MethodSpec> getter = new LinkedList<>();
		for (Grammar gr : g.getChain()) {
			if (gr instanceof IgnoreGrammer || gr instanceof IgnoreTokenGrammer)
				continue;
			if (gr.getName() == null)
				continue;
			if (gr.getGrammarObjectType().equals(GrammarObject.class) || gr instanceof RepeatGrammer) {
				// Single object return + type unkown
				if (gr instanceof MultiGrammer) {
					getter.add(MethodSpec.methodBuilder("get" + camelCase(gr.getName()))
							.returns(gr.getGrammarObjectType()).addModifiers(Modifier.PUBLIC)
							.addStatement("return ($T) obj.getObject($S)", gr.getGrammarObjectType(), gr.getName())
							.build());
				}
				// singe object return + type known
				else if (gr instanceof OptinalGrammer || gr instanceof GrammarMatchGrammer) {
					ChainGrammer cgr = null;
					if (gr instanceof OptinalGrammer)
						cgr = (ChainGrammer) ((OptinalGrammer) gr).getCheck();
					if (gr instanceof GrammarMatchGrammer)
						cgr = (ChainGrammer) ((GrammarMatchGrammer) gr).getGrammar();

					generate(cgr);
					getter.add(MethodSpec.methodBuilder("get" + camelCase(gr.getName()))
							.returns(ClassName.get(pack, camelCase(cgr.getName()))).addModifiers(Modifier.PUBLIC)
							.addStatement("return new " + camelCase(cgr.getName()) + "(($T)obj.getObject($S))",
									SubGrammerObject.class, gr.getName())
							.build());
					// object array return + type known
				} else {
					ChainGrammer cgr = null;
					if (gr instanceof RepeatGrammer)
						cgr = (ChainGrammer) ((RepeatGrammer) gr).getCheck();
					generate(cgr);
					
					ParameterizedTypeName listType = ParameterizedTypeName.get(ClassName.get(ArrayList.class),ClassName.get(pack, camelCase(cgr.getName())));
					
					CodeBlock methodContent = CodeBlock.builder().addStatement("$T collection =  ($T)obj.getObject($S)", SubGrammerObject.class,
							SubGrammerObject.class, gr.getName())
					.addStatement("$T out = new ArrayList<>()",listType)
					.beginControlFlow("for ($T iter : collection.objects)", GrammarObject.class)
					.addStatement("out.add(new $T(($T) iter))", ClassName.get(pack, camelCase(cgr.getName())),SubGrammerObject.class)
					.endControlFlow()
					.addStatement("return out").build();
					
					getter.add(MethodSpec.methodBuilder("get" + camelCase(gr.getName()))
							.returns(listType)
							.addModifiers(Modifier.PUBLIC)
							.addCode(methodContent)
							.build());
				}
			} 
			else if((gr.getGrammarObjectType().equals(TokenGrammarObject.class))&& !returnTokens){
				getter.add(MethodSpec.methodBuilder("get" + camelCase(gr.getName()))
						.returns(String.class)
						.addModifiers(Modifier.PUBLIC)
						.addStatement("return (($T)obj.getObject($S)).joinTokens()", gr.getGrammarObjectType(), gr.getName())
						.build());
			} else {
				getter.add(MethodSpec.methodBuilder("get" + camelCase(gr.getName()))
						.returns(gr.getGrammarObjectType())
						.addModifiers(Modifier.PUBLIC)
						.addStatement("return ($T) obj.getObject($S)", gr.getGrammarObjectType(), gr.getName())
						.build());
			}
		}

		Builder build = TypeSpec.classBuilder(camelCase(g.getName())).addModifiers(Modifier.PUBLIC).addField(bjectField)
				.addMethod(constructor);

		for (MethodSpec methodSpec : getter) {
			build.addMethod(methodSpec);
		}
		TypeSpec type = build.build();
		JavaFile javaFile = JavaFile.builder(pack, type).indent("\t").build();
		javaFile.writeTo(new File(path));
	}

	public String camelCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
