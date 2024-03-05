package com.niton.jainparse.grammar.api;

import com.niton.jainparse.grammar.types.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of a GrammarReference using a {@link HashMap}
 *
 * @author Nils Brugger
 * @version 2019-06-07
 */
public class GrammarReferenceMap extends HashMap<String, Grammar<?>>
        implements Iterable<Map.Entry<String, Grammar<?>>>, GrammarReference {

    @Override
    public Grammar<?> get(String key) {
        return super.get(key);
    }

    public GrammarReferenceMap map(Grammar<?> g) {
        put(g.getName(), g);
        return this;
    }

    public GrammarReferenceMap map(Grammar.Builder g) {
        return map(g.get());
    }

    /**
     * Adds a Grammar to the reference map
     *
     * @param g    the grammar to the map
     * @param name the name of the grammar
     */
    public GrammarReferenceMap map(Grammar<?> g, String name) {
        put(name, g);
        return this;
    }

    public GrammarReferenceMap merge(GrammarReferenceMap ref) {
        putAll(ref);
        return this;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Entry<String, Grammar<?>>> iterator() {
        return this.entrySet().iterator();
    }

    /**
     * @see GrammarReference#grammarNames()
     */
    @Override
    public Set<String> grammarNames() {
        return keySet();
    }

    public GrammarReferenceMap deepMap(@Nullable Grammar<?> gram) {
        if (gram == null) return this;
        if (gram.getName() != null && !(gram instanceof GrammarReferenceGrammar)) {
            map(gram);
        }
        if (gram instanceof GrammarReference) {
            var ref = (GrammarReference) gram;
            for (var grammarName : ref.grammarNames()) {
                if (grammarName.equals(gram.getName())) continue;
                Grammar<?> grammar = ref.get(grammarName);
                //this if prevents from infinite recursion by linking the reference to the name rather than the actual grammar
                if (isNameMappable(grammar)) map(grammar);
            }
        }

        return this;
    }

    private static boolean isNameMappable(Grammar<?> grammar) {
        if(!(grammar instanceof GrammarReferenceGrammar)) return true;
        var referenceGrammar = (GrammarReferenceGrammar) grammar;
        return !grammar.getName().equals(referenceGrammar.getGrammar());
    }
}

