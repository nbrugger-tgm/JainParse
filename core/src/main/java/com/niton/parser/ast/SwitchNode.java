package com.niton.parser.ast;

import com.niton.parser.token.Tokenizer;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This is the node that is produced if a grammar can match different grammars. This node expresses which of the grammars matched.
 * For example, the grammar: {@code {TEXT or NUMBER}} would produce a switch node showing {@code TEXT} or {@code NUMBER} was matched and also the matched value itself.
 */
public class SwitchNode extends AstNode {
    private final AstNode result;

    public SwitchNode(AstNode res) {
        this.result = res;
    }

    /**
     * @return the type which shows which grammar of the options was matched. Can be used to determine how to interpret the {@link #getResult()}.
     */
    public String getType() {
        return result.getOriginGrammarName();
    }

    @Override
    public Stream<Tokenizer.AssignedToken> join() {
        return result.join();
    }

    @Override
    public Optional<ReducedNode> reduce(@NonNull String name) {
        if (getType() != null) {
            var innerNode = result.reduce("value");
            return innerNode.map(node -> ReducedNode.node(name, List.of(
                    ReducedNode.leaf("type", getType()),
                    node
            )));
        } else {
            return result.reduce(name);
        }
    }

    /**
     * @return the result of the match
     */
    public AstNode getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
